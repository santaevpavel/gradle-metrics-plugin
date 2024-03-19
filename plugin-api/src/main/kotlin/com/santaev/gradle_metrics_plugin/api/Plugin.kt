package com.santaev.gradle_metrics_plugin.api

import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.build.event.BuildEventsListenerRegistry
import javax.inject.Inject

@Suppress("UnstableApiUsage")
interface Plugin {

    @Inject
    fun getBuildEventsListenerRegistry(): BuildEventsListenerRegistry

    @Inject
    fun getFlowScope(): FlowScope

    @Inject
    fun getFlowProviders(): FlowProviders
}