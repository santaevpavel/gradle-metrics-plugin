# gradle-metrics-plugin
Experimental gradle plugin to fetch and save metrics

# What is it?

This plugin allows to collect build metrics (like build time, tasks, etc) and dispatch to
different sources.

![Terminal](https://raw.githubusercontent.com/santaevpavel/gradle-metrics-plugin/master/raw/terminal_output.png)

## Built-in collectors

1. Build time
2. Task name
3. Tasks count
4. Project name
5. Root project name
6. File size
7. Unit test results

## Dispatchers

1. Console
2. Amplitude
3. File

# How to test?

There is `sample` project that includes this plugin and core extensions.
Test project requires plugin and extensions dependency in `build/repo` directory. 
To publish dependencies to local repo (`build/repo`) you need to run 
`plugin:publishMavenPublicationToMavenRepository` and `plugin-core-extensions::publishMavenPublicationToMavenRepository` tasks.

Then run command `sample:jar` to check plugin out.