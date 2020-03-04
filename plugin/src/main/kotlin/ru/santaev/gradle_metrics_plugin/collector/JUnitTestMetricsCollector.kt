package ru.santaev.gradle_metrics_plugin.collector

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionAdapter
import org.gradle.api.tasks.TaskState
import ru.santaev.gradle_metrics_plugin.IMetricsStore
import ru.santaev.gradle_metrics_plugin.LongMetric
import ru.santaev.gradle_metrics_plugin.MetricUnit
import ru.santaev.gradle_metrics_plugin.utils.logger
import java.io.File

class JUnitTestMetricsCollector : BaseMetricCollector() {

    private val logger = logger(this)
    private var wasTestTaskExecuted = false
    private val projectsWithTests = mutableListOf<Project>()

    override fun init(metricsStore: IMetricsStore, project: Project) {
        super.init(metricsStore, project)
        project.gradle.addListener(TestsBuildTaskListener())
    }

    override fun onBuildFinish() {
        if (!wasTestTaskExecuted) {
            logger.info("Test task had not been executed")
            return
        }
        collectMetrics()
    }

    private fun collectMetrics() {
        val reportFiles = findTestReportFiles()
        collectTestMetrics(reportFiles)
    }

    private fun findTestReportFiles(): List<File> {
        return projectsWithTests.flatMap { project ->
            val reportsDir = File(project.buildDir.absolutePath + JUNIT_TEST_RESULTS_DIR_PATH)
            if (reportsDir.exists() && reportsDir.isDirectory) {
                reportsDir.listFiles { _, name -> name.endsWith(XML_FILE_EXTENSION) }.toList()
            } else {
                emptyList<File>()
            }
        }
    }

    private fun collectTestMetrics(reportFiles: List<File>) {
        var testsCount = 0
        var failedTestsCount = 0
        var skippedTestsCount = 0
        reportFiles.forEach { reportFile ->
            reportFile
                .readLines()
                .firstOrNull { it.startsWith(JUNIT_TEST_REPORTS_TAG_TEST_SUITE) }
                ?.let { line ->
                    testsCount += getIntAttributeValue(line, JUNIT_TEST_REPORTS_ATTR_TESTS) ?: 0
                    failedTestsCount += getIntAttributeValue(line, JUNIT_TEST_REPORTS_ATTR_FAILURES) ?: 0
                    skippedTestsCount += getIntAttributeValue(line, JUNIT_TEST_REPORTS_ATTR_SKIPPED) ?: 0
                }
        }
        saveMetrics(
            testsCount = testsCount,
            failedTestsCount = failedTestsCount,
            skippedTestsCount = skippedTestsCount,
            successTestsCount = testsCount - failedTestsCount - skippedTestsCount
        )
    }

    private fun saveMetrics(testsCount: Int, failedTestsCount: Int, skippedTestsCount: Int, successTestsCount: Int) {
        with(metricsStore ?: return) {
            add(LongMetric(METRIC_ID_TESTS_COUNT, testsCount.toLong(), MetricUnit.Pieces))
            add(LongMetric(METRIC_ID_FAILED_TESTS_COUNT, failedTestsCount.toLong(), MetricUnit.Pieces))
            add(LongMetric(METRIC_ID_SKIPPED_TESTS_COUNT, skippedTestsCount.toLong(), MetricUnit.Pieces))
            add(LongMetric(METRIC_ID_SUCCESS_TESTS_COUNT, successTestsCount.toLong(), MetricUnit.Pieces))
        }
    }

    private fun getIntAttributeValue(line: String, attribute: String): Int? {
        return line.substringAfter("$attribute=\"")
            .substringBefore("\"")
            .toIntOrNull()
    }

    private inner class TestsBuildTaskListener : TaskExecutionAdapter() {

        override fun afterExecute(task: Task, state: TaskState) {
            if (task.name == TEST_TASK_NAME) {
                wasTestTaskExecuted = true
                projectsWithTests.add(task.project)
            }
        }
    }

    companion object {
        private const val TEST_TASK_NAME = "test"
        private const val JUNIT_TEST_RESULTS_DIR_PATH = "/test-results/test/"
        private const val METRIC_ID_TESTS_COUNT = "TestsCount"
        private const val METRIC_ID_FAILED_TESTS_COUNT = "FailedTestCount"
        private const val METRIC_ID_SKIPPED_TESTS_COUNT = "SkippedTestCount"
        private const val METRIC_ID_SUCCESS_TESTS_COUNT = "SuccessCount"
        private const val XML_FILE_EXTENSION = ".xml"
        private const val JUNIT_TEST_REPORTS_TAG_TEST_SUITE = "<testsuite"
        private const val JUNIT_TEST_REPORTS_ATTR_TESTS = "tests"
        private const val JUNIT_TEST_REPORTS_ATTR_FAILURES = "failures"
        private const val JUNIT_TEST_REPORTS_ATTR_SKIPPED = "skipped"
    }
}