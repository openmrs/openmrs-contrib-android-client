openmrs-contrib-android-client
==============================

### Android client for OpenMRS

[![Build Status](https://travis-ci.org/openmrs/openmrs-contrib-android-client.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-contrib-android-client) [![GitHub version](https://badge.fury.io/gh/openmrs%2Fopenmrs-contrib-android-client.svg)](https://github.com/openmrs/openmrs-contrib-android-client/releases/latest)

# GSoC 2016

The project has been selected as a candidate for Google Summer of Code 2016. Please visit the project's page for more details: https://wiki.openmrs.org/display/projects/OpenMRS+Android+Client+-+GSoC+2016

For a detailed description of all work done during GSoC 2016, please checkout the [GSoC_2016.md](GSoC_2016.md) file.

#Description
The goal of this project is to provide OpenMRS 2.x client for Android devices. The app is designed to cover most of the functionality of the web application including registering patients, taking visit notes, capturing vitals, etc.
The app will communicate with OpenMRS using REST. It will support working off-line (without network connection) with a chosen subset of patients. The database on the device will be encrypted and password protected to secure patient data.
For more information, including screenshots of the client, visit https://wiki.openmrs.org/display/projects/OpenMRS+2.x+Android+Client


# Development
Before creating pull request run code review tools, *PMD & Checkstyle*, and tests.

# QuickStart

As of February 2016, this project has been migrated to gradle to work successfully with Android studio. Steps to set up:

1. Clone the project
2. From the Android Studio menu select File > New > Import Project. Alternatively, from the Welcome screen, select Import project.
3. Navigate to the folder when you have cloned this repo and select the build.gradle file inside it. Select import.
4. Done! Wait for dependencies to load and download from Maven, and you are ready to go! 
5. Make sure that you have the latest SDK and build tools downloaded, as we will always build against the latest release.

[In-depth tutorial] (https://github.com/codepath/android_guides/wiki/Getting-Started-with-Gradle)

If you have been building on Eclipse before this change was made, you have two options:

A. Re-clone, import in Studio and manually change the files if any done after February 2016. This is easier and recommended.

B. Open Android Studio, import project and select your openmrs project. Next, import module and in this step, import the odkcollect module. Set it as your project dependency. As a final step, add the lines useLibrary 'org.apache.http.legacy' in odkcollect/build.gradle under android{ }.

Note: If you are working behind a proxy, check [this](https://wiki.appcelerator.org/display/guides2/Using+Studio+From+Behind+a+Proxy) to get things working.

# Releasing

In order to release the application, go to [releases](https://github.com/openmrs/openmrs-contrib-android-client/releases) and click the [Draft a new release](https://github.com/openmrs/openmrs-contrib-android-client/releases/new) button. It will create a new version tag in the repository and build the app. The tag name will be used as the version number for this release.

If you want to release a new major or minor version, please be sure to update the applicationVersion variable in [build.gradle](https://github.com/openmrs/openmrs-contrib-android-client/blob/master/openmrs-client/build.gradle#L26) prior to the release.

# Release Notes
### Version 2.0
#### The first release is named to match OpenMRS 2.0.
### Summary:
1. Login screen
2. Read-only access to patient details (full name, birthdate, address), vitals and visits.
3. Downloading and storing patient data in the encrypted database on the device
[See more in JIRA](https://issues.openmrs.org/browse/AC/fixforversion/16506/?selectedTab=com.atlassian.jira.jira-projects-plugin:version-summary-panel)

# Objectives
### Version 2.0.1
- Starting/stopping visits
- To Be Determined
[See more in JIRA](https://issues.openmrs.org/browse/AC/?selectedTab=com.atlassian.jira.jira-projects-plugin:summary-panel)

# User Manual
- [Version 2.0](https://wiki.openmrs.org/download/attachments/74252444/User%20Manual%202.0.pdf?version=1&modificationDate=1414759790000&api=v2)

# Resources
- JIRA https://issues.openmrs.org/browse/AC/?selectedTab=com.atlassian.jira.jira-projects-plugin:summary-panel 
- Sprint board https://issues.openmrs.org/secure/RapidBoard.jspa?rapidView=60
- Dashboard https://issues.openmrs.org/secure/Dashboard.jspa?selectPageId=12851
- Repository https://github.com/openmrs/openmrs-contrib-android-client
- CI https://travis-ci.org/openmrs/openmrs-contrib-android-client 
- Google Play https://play.google.com/store/apps/details?id=org.openmrs.mobile
