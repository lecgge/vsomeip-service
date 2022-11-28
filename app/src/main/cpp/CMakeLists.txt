# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)



project("vsomeip")

include_directories(include)





find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

add_library(ServiceJNI SHARED ServiceJNI.cpp)

target_link_libraries( ServiceJNI ${log-lib} vsomeip3 vsomeip3-cfg vsomeip3-e2e vsomeip3-sd)
