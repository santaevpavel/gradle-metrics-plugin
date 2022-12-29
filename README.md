# gradle-metrics-plugin
Experimental gradle plugin to fetch and save metrics

# What is it?

This plugin allows to collect build metrics (like build time, tasks, etc) and dispatch to
different sources.

Also the plugin provides API to implement your own collectors and dispatchers.  

![Terminal](https://raw.githubusercontent.com/santaevpavel/gradle-metrics-plugin/master/raw/terminal_output.png)

# Usage

### Add Github repository and classpath

```groovy
buildscript {
    repositories {
        // ...
        maven {
            url = uri("https://maven.pkg.github.com/santaevpavel/gradle-metrics-plugin")
            credentials {
                username = "YOUR USERNAME"
                password = "GITHUB TOKEN"
            }
        }
    }
    // ...
    dependencies {
        classpath "com.santaev.gradle-metrics-plugin:gradle-metrics-plugin:1.0.0"
    }
}
```

### Apply plugin

```groovy
apply plugin: 'com.santaev.gradle-metrics-plugin'
```

### Configure plugin

```groovy
metrics {
    collectors {
        Build
        TaskNames
        TasksCount
        ProjectProperties
    }
    dispatchers {
        ConsoleDispatcher
    }
}

```
### Collectors and dispatchers

## Built-in collectors

1. Build time
2. Build result
3. Task name
4. Tasks count
5. Project name
6. Root project name
7. File size
8. Unit test results

## Built-in dispatchers

1. Console
2. File

## Extension

### Amplitude dispatcher 

This extension allows to send build metrics to [Amplitude](https://amplitude.com/). 

![AmplitudeChart](https://raw.githubusercontent.com/santaevpavel/gradle-metrics-plugin/master/raw/amplitude_chart.png)

#### How to setup

1. Add Github maven repository.
```groovy
buildscript {
    repositories {
        // ...
        maven {
            url = uri("https://maven.pkg.github.com/santaevpavel/gradle-metrics-plugin")
            credentials {
                username = "YOUR USERNAME"
                password = "GITHUB TOKEN"
            }
        }
    }
    // ...
}
```
Add Github properties to local.properties

2. Add dependency
```groovy
dependencies {
    // ...
    gradleMetricsPluginExtension "com.santaev.gradle-metrics-plugin:amplitude-extension:1.0.0"
}
```
3. Configure amplitude extension
```groovy
metrics {
    collectors {
        // ...
    }
    dispatchers {
        // ...
        AmplitudeDispatcher {
            apiKey = "AMPLITUDE API KEY"
        }
    }
}
```

# How to test?

There is `sample` project that includes this plugin and core extensions.
Test project requires plugin and extensions dependency in `build/repo` directory. 
To publish dependencies to local repo (`build/repo`) you need to run 
`./publishLocal.sh`.

Then run command `sample:jar` to check plugin out.