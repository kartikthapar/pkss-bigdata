#include <complex>
#include <iostream>
#include <stdexcept>
#include <string>
#include <vector>

#include "fitsio.h"

struct AnyColumn
{
    std::string name;
    int col_num;
    int bytes_per_entry;
    int repeats;
    fitsfile * fitsptr;

    AnyColumn(std::string n, int cn, int r, int bpe, fitsfile* fptr)
        : name(n), col_num(cn), bytes_per_entry(bpe), repeats(r), fitsptr(fptr)
    {
        if( repeats == 0 ) repeats = 1;
    }
    virtual ~AnyColumn() { }

    virtual void writef(size_t column, std::ostream& output, size_t idx = 1) = 0;

    template<typename... T>
    static AnyColumn * newForType(int typecode, T... args);
};

template<typename T>
struct Column : public AnyColumn
{
    static const int TYPECODE;
    static const std::string type_name;
    Column(std::string n, int cn, int r, int bpe, fitsfile* fptr)
        : AnyColumn(n, cn, r, bpe, fptr)
    {
        if( sizeof(T) != bytes_per_entry )
        {
            std::cout << "My column name is " << name << "\n";
            std::cout << "My type_name is " << type_name << "\n";
            std::cout << "My bytes per entry is " << bytes_per_entry << "\n";
            std::cout << "The size of my c type is " << sizeof(T) << "\n";
        }
    }

    virtual void writef(size_t row, std::ostream& output, size_t idx = 1) override
    {
        T buffer;

        int status = 0;
        // TODO need to check for null values
        int is_null = false;
        int result = fits_read_col(fitsptr, TYPECODE, col_num, row, idx, 1, NULL, &buffer, &is_null, &status);
        if (result || status) throw std::runtime_error("Error reading data from file.");
        output << buffer;
    }
};

template<>
struct Column<std::string> : public AnyColumn
{
    static const int TYPECODE = TSTRING;
    Column(std::string n, int cn, int r, int bpe, fitsfile* fptr)
        : AnyColumn(n, cn, r, bpe, fptr)
    {
        std::cout << "Column " << name << " is a string with " << bytes_per_entry << " bytes per entry and " << repeats << " repeats\n";
    }

    virtual void writef(size_t row, std::ostream& output, size_t idx) override
    {
        char * buffer = reinterpret_cast<char*>(std::calloc(bytes_per_entry + 1, 1));

        int status = 0;
        int is_null = false;
        int result = fits_read_col(fitsptr, TYPECODE, col_num, row, idx, 1, NULL, &buffer, &is_null, &status);
        if( result || status ) throw std::runtime_error("Error reading data from file.");
        output << buffer;
        free(buffer);
    }
};

#define FITS_TYPE(fits, cpp) \
template<> const int Column<cpp>::TYPECODE = fits; template<> const std::string Column<cpp>::type_name(#cpp);
#define FITS_STRING_TYPE(a, b)
#define FITS_LONG_TYPE(a, b)
#define FITS_ULONG_TYPE(a, b)
#include "type_mapping.inc"
#undef FITS_LONG_TYPE
#undef FITS_ULONG_TYPE
#undef FITS_STRING_TYPE
#undef FITS_TYPE

template<typename... T>
AnyColumn * AnyColumn::newForType(int typecode, T... args)
{
    switch(typecode) {
#define FITS_TYPE(fits, cpp) \
        case fits:\
            return new Column<cpp>(args...); \
            break;
#include "type_mapping.inc"
#undef FITS_TYPE
        default:
            std::cerr << "type = " << typecode << "\n";
            throw std::runtime_error("Not implemented!");
    }
    return nullptr;
}

struct TableHeader
{
    size_t bytes_per_row;
    long row_count;
    int column_count;
    std::vector<AnyColumn*> columns;

    TableHeader()
        : bytes_per_row(-1), row_count(-1), column_count(-1), columns()
    { }
    TableHeader(const TableHeader&) = delete;
    TableHeader(TableHeader&&) = delete;

    TableHeader& operator=(const TableHeader&) = delete;
    TableHeader& operator=(const TableHeader&&) = delete;
};

template<typename T>
void readValue(fitsfile* fitsptr, const char * key, T* target);
template<>
void readValue(fitsfile* fitsptr, const char * key, unsigned long * target)
{
    int status;
    int result = fits_read_key(fitsptr, TULONG, key, target, NULL, &status);
    if (VALUE_UNDEFINED == result) throw std::runtime_error("Undefined value");
    if (result || status) throw std::runtime_error("Unknown.  FIXME");
}
template<>
void readValue(fitsfile* fitsptr, const char * key, char * target)
{
    int status = 0;
    int result = fits_read_key(fitsptr, TSTRING, key, target, NULL, &status);
    if (result || status) throw std::runtime_error("Unknown.  FIXME");
}

int main(int argc, char* argv[])
{
    fitsfile * fitsptr;
    int status = 0;
    int nkeys = 0;

    if( argc != 2 )
    {
        fprintf(stderr, "Usage: fits2csv input_file\n");
        return EXIT_FAILURE;
    }
#ifndef DEBUG
    try {
#endif
        fits_open_file(&fitsptr, argv[1], READONLY, &status);
        if( status ) throw std::runtime_error("Error opening file!");

        int hdu_count = 0;
        int result = fits_get_num_hdus(fitsptr, &hdu_count, &status);
        if( status ) throw std::runtime_error("Unknown.  FIXME.");
        if( hdu_count < 2 ) throw std::runtime_error("Expected 2 HDUs in the file (first is just empty, second has data).");

        int hdu_type = 0;
        result = fits_movabs_hdu(fitsptr, 2, &hdu_type, &status);
        if( status ) throw std::runtime_error("Unknown.  FIXME");

        fits_get_hdrspace(fitsptr, &nkeys, NULL, &status);
        if( status ) throw std::runtime_error("Unknown.  FIXME");
        TableHeader header;

        fits_get_num_rows(fitsptr, &header.row_count, &status);
        if( status ) throw std::runtime_error("Unknown.  FIXME");
        fits_get_num_cols(fitsptr, &header.column_count, &status);
        if( status ) throw std::runtime_error("Unknown.  FIXME");

        header.columns.resize(header.column_count);

        char col_key[FLEN_CARD];
        char col_title[FLEN_CARD];
        for (int i = 0; i < header.column_count; ++i)
        {
            int colnum = i + 1;
            snprintf(col_key, FLEN_CARD, "%d", colnum);
            result = fits_get_colname(fitsptr, !CASESEN, col_key, col_title, &colnum, &status);
            if (result || status) throw std::runtime_error("Help!");

            int typecode;
            long repeat;
            long width;

            result = fits_get_coltype(fitsptr, colnum, &typecode, &repeat, &width, &status);
            if (result || status) throw std::runtime_error("Help!");
            bool variable_length = (typecode < 0);
            if( variable_length )
            {
                typecode *= -1;
                throw std::logic_error("I didn't implement variable width columns yet.");
            }
            AnyColumn * col = nullptr;
            col = AnyColumn::newForType(typecode, col_title, colnum, repeat, width, fitsptr);
            header.columns.at(i) = col;
        }

        bool first = true;
        for (AnyColumn * col : header.columns)
        {
            if (!first)
            {
                std::cout << ", ";
            }
            col->writef(1, std::cout);
            first = false;
        }

        fits_close_file(fitsptr, &status);
        return 0;

#ifndef DEBUG
    }
    catch(std::exception& exp)
    {
        std::cerr << exp.what() << "\n";
        fits_close_file(fitsptr, &status);
        return EXIT_FAILURE;
    }
#endif
}
