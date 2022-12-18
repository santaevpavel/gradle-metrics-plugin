package com.santaev.gradle_metrics_plugin.utils

import java.io.File

val File.sizeOnMegabytes: Long
    get() = length() / (1024 * 1024)

val File.sizeOnKilobytes: Long
    get() = length() / (1024)
