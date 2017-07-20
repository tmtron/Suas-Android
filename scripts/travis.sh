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

runUnitTests() {
    ./gradlew --settings-file settings-suas-lib.gradle :suas-lib:test
    exitOnFailedBuild "suas-lib unit tests"

    ./gradlew --settings-file settings-suas-middleware.gradle  :suas-middleware:test
    exitOnFailedBuild "suas-middleware unit tests"
}

buildSampleApp() {
    ./gradlew :suas-sample:assembleDebug :suas-sample:assembleDebug
    exitOnFailedBuild "suas-sample"
}

deployArtifacts() {
    boxOut "Deploying suas-lib artefact"
    ./gradlew :suas-lib:assemble :suas-lib:uploadArchives

    boxOut "Deploying suas-middleware artefact"
    ./gradlew --settings-file settings-suas-middleware.gradle :suas-lib:assemble :suas-middleware:uploadArchives
}

prBuild() {
    export LOCAL_BUILD="true"

    if [ "$COMPONENT" == "unit" ]; then
        boxOut "Running unit tests"
        runUnitTests

    elif [ "$COMPONENT" == "sample" ]; then
        boxOut "Building Sample App"
        buildSampleApp

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
        deployHockeyApp
    else
        boxOut "Module doesn't exist: $COMPONENT"
    fi

    unset LOCAL_BUILD
    exit 0
}


if isPullRequest ; then
    boxOut "This is a PR. Hook: script"
    prBuild
else
    boxOut "This is a branch build. Hook: script"
    branchBuild
fi