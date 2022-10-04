#!/bin/bash
rm -rf build/repo
./gradlew publishMavenPublicationToMavenRepository -PexcludeSample=true