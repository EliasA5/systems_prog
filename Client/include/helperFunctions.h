//
// Created by spl211 on 04/01/2022.
//

#ifndef CLIENT_HELPERFUNCTIONS_H
#define CLIENT_HELPERFUNCTIONS_H

#include "boost/date_time/gregorian/gregorian.hpp"
#include "boost/date_time/posix_time/posix_time.hpp"

inline short bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

inline short bytesToShort(const char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

inline void shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

inline std::string getDateAsString() {
    boost::posix_time::ptime time(boost::posix_time::second_clock::local_time());
    boost::gregorian::date::ymd_type ymd = time.date().year_month_day();
    std::stringstream dateStream;
    dateStream << ymd.day << "-" << ymd.month.as_number() << "-" << ymd.year << " " << time.time_of_day().hours() << ":"
               << time.time_of_day().minutes();
    return dateStream.str();
}
#endif //CLIENT_HELPERFUNCTIONS_H
