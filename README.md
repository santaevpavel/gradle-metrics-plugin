# gradle-metrics-plugin
Experimental gradle plugin to fetch and save metrics

# How to test?

There is `sample` project that includes this plugin and core extensions.
Test project requires plugin and extensions dependency in `build/repo` directory. 
To publish dependencies to local repo (`build/repo`) you need to run 
`plugin:publishMavenPublicationToMavenRepository` and `plugin-core-extensions::publishMavenPublicationToMavenRepository` tasks.

Then run command `sample:jar` to check plugin out.