#!/usr/bin/env bash

boxOut(){
    local s="$*"
    tput setaf 3
    echo -e " =${s//?/=}=\n| $(tput setaf 4)$s$(tput setaf 3) |\n =${s//?/=}=\n"
    tput sgr 0
}

isPullRequest() {
    [[ "$TRAVIS_PULL_REQUEST" == "false" ]] && return 1 || return 0
}

exitOnFailedBuild() {
    if [ $? -ne 0 ]; then
        boxOut "BUILD FAILED: $1"
        exit 1
    fi
}

acceptLicenses() {
    mkdir -p ${ANDROID_HOME}licenses
    echo -e "\nd56f5187479451eabf01fb78af6dfcb131a6481e" > ${ANDROID_HOME}licenses/android-sdk-license
}

runUnitTests() {
    ./gradlew --settings-file settings-suas-lib.gradle :suas-lib:test :suas-lib:jacocoTestReport
    exitOnFailedBuild "suas-lib unit tests"

    ./gradlew --settings-file settings-suas-middleware.gradle :suas-middleware-thunk:test :suas-middleware-monitor:test :suas-middleware-logger:test
    exitOnFailedBuild "suas-middleware unit tests"
}

buildSampleAppTodo() {
    ./gradlew :suas-todo-app:assembleDebug :suas-todo-app:assembleDebug
    exitOnFailedBuild "suas-todo-app"
}

buildSampleAppWeather() {
    ./gradlew :suas-weather-app:assembleDebug :suas-weather-app:assembleDebug
    exitOnFailedBuild "suas-weather-app"
}

buildSampleAppCounter() {
    ./gradlew -PmainClass=com.exaple.suas.counter.SuasCounter execute
    exitOnFailedBuild "suas-counter-java"

    ./gradlew -PmainClass=com.example.suas.counter.SuasCounterKotlinKt execute
    exitOnFailedBuild "suas-counter-kotlin"
}

deployArtifacts() {
    boxOut "Deploying suas-lib artefact"
    ./gradlew :suas-lib:assemble :suas-lib:uploadArchives

    boxOut "Deploying suas-middleware-* artefacts"
    ./gradlew --settings-file settings-suas-middleware.gradle :suas-lib:assemble :suas-middleware-logger:uploadArchives :suas-middleware-monitor:uploadArchives :suas-middleware-thunk:uploadArchives
}

prBuild() {
    export LOCAL_BUILD="true"

    if [ "$COMPONENT" == "unit" ]; then
        boxOut "Running unit tests"
        runUnitTests
        bash <(curl -s https://codecov.io/bash) || true

    elif [ "$COMPONENT" == "sample_todo" ]; then
        boxOut "Building Sample App: Todo"
        buildSampleAppTodo

    elif [ "$COMPONENT" == "sample_weather" ]; then
        boxOut "Building Sample App: Weather"
        buildSampleAppWeather

    elif [ "$COMPONENT" == "sample_counter" ]; then
        boxOut "Building Sample App: Counter"
        buildSampleAppCounter

    else
        boxOut "Module doesn't exist: $COMPONENT"
    fi

    unset LOCAL_BUILD
    exit 0
}

branchBuild() {
    export LOCAL_BUILD="false"

    if [ "$COMPONENT" == "build" ]; then
        boxOut "Upload Snapshot"
        deployArtifacts
        buildSampleAppTodo
        buildSampleAppWeather
        buildSampleAppCounter
    else
        boxOut "Module doesn't exist: $COMPONENT"
    fi

    unset LOCAL_BUILD
    exit 0
}

acceptLicenses
if isPullRequest ; then
    boxOut "This is a PR. Hook: script"
    prBuild
else
    boxOut "This is a branch build. Hook: script"
    branchBuild
fi