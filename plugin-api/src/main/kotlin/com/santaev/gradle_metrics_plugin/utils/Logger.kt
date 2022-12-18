package com.santaev.gradle_metrics_plugin.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun logger(of: Any): Logger = LoggerFactory.getLogger(of::class.java)