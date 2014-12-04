#include <array>
#include <iostream>
#include <random>
#include <sstream>

constexpr unsigned long long CLUSTER_COUNT = 20;
constexpr unsigned long long DIMENSIONS = 1024;
constexpr unsigned long long KILOBYTE = 1024;
constexpr unsigned long long MEGABYTE = KILOBYTE * 1024;
constexpr unsigned long long GIGABYTE = MEGABYTE * 1024;

constexpr double RANGE = 1000000.0;
constexpr double MAX_WIDTH = 10000.0;

constexpr unsigned long long TARGET_BYTE_COUNT = 10 * MEGABYTE;

typedef std::array<double, DIMENSIONS> Point;
std::array<Point, CLUSTER_COUNT> cluster_centers;
std::array<Point, CLUSTER_COUNT> cluster_widths;

int main()
{
    std::mt19937_64 generator(0);
    std::uniform_real_distribution<double> uniform_range(-RANGE, RANGE);
    std::uniform_real_distribution<double> uniform_width(-MAX_WIDTH, MAX_WIDTH);

    // choose the cluster centers
    for (Point& center : cluster_centers)
    {
        for (double& coord : center)
        {
            coord = uniform_range(generator);
        }
    }
    // choose the cluster sizes
    for (Point& center : cluster_widths)
    {
        for (double& coord : center)
        {
            coord = uniform_range(generator);
        }
    }

    // Apparently this interval is closed on both ends
    std::uniform_int_distribution<unsigned> cluster_chooser(0, CLUSTER_COUNT - 1);
    std::normal_distribution<double> normal;
    unsigned long long bytes_written = 0;
    unsigned long long row_counter = 0;
    while (bytes_written < TARGET_BYTE_COUNT)
    {
        std::ostringstream strm;
        strm << "key: " << row_counter << ";";
        strm << "value: random; len: " << DIMENSIONS << ";";
        unsigned cluster = cluster_chooser(generator);
        for (unsigned dim = 0; dim < DIMENSIONS; ++dim)
        {
            double zscore = normal(generator);
            double offset = zscore * cluster_widths.at(cluster).at(dim);
            double position = offset + cluster_centers.at(cluster).at(dim);
            strm << " " << dim << ": " << position << ";";
        }
        strm << "\n";
        std::cout << strm.str();
        bytes_written += strm.str().size();
        row_counter += 1;
    }
}
