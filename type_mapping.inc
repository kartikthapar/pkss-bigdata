#ifndef FITS_STRING_TYPE
#define FITS_STRING_TYPE FITS_TYPE
#endif
FITS_STRING_TYPE(TSTRING, std::string)

FITS_TYPE(TSHORT, signed short)
FITS_TYPE(TUSHORT, unsigned short)
FITS_TYPE(TINT, signed int)
FITS_TYPE(TUINT, unsigned int)

#ifndef FITS_LONG_TYPE
#define FITS_LONG_TYPE FITS_TYPE
#endif
FITS_LONG_TYPE(TLONG, signed int)

#ifndef FITS_ULONG_TYPE
#define FITS_ULONG_TYPE FITS_TYPE
#endif
FITS_ULONG_TYPE(TULONG, unsigned int)

FITS_TYPE(TLONGLONG, long long)

FITS_TYPE(TFLOAT, float)
FITS_TYPE(TDOUBLE, double)
FITS_TYPE(TCOMPLEX, std::complex<float>)
FITS_TYPE(TDBLCOMPLEX, std::complex<double>)
FITS_TYPE(TBYTE, unsigned char)
