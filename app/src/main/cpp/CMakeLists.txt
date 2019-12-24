cmake_minimum_required(VERSION 3.4.1)

add_library(about-me SHARED
            about-me.c)

target_link_libraries(about-me
                      android
                      log)
