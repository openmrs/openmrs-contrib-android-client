[![Logo](http://i.imgur.com/fpVkTZk.png)](http://www.openmrs.org)

OpenMRS Android Client
==============================

[![Build Status Travis](https://travis-ci.org/openmrs/openmrs-contrib-android-client.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-contrib-android-client) [![Build Status AppVeyor](https://ci.appveyor.com/api/projects/status/github/openmrs/openmrs-contrib-android-client?branch=master&svg=true)](https://ci.appveyor.com/project/AvijitGhosh82/openmrs-contrib-android-client) [![Demo Server](https://img.shields.io/badge/demo-online-green.svg)](http://devtest04.openmrs.org:8080/openmrs) [![GitHub version](https://d25lcipzij17d.cloudfront.net/badge.svg?id=gh&type=6&v=2.8.1&x2=0)](https://github.com/openmrs/openmrs-contrib-android-client/releases/latest) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/37fa8e86a3cb4256a3b7ffcc644f13c6)](https://www.codacy.com/app/marzeion-tomasz/openmrs-contrib-android-client?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=openmrs/openmrs-contrib-android-client&amp;utm_campaign=Badge_Grade) [![codecov](https://codecov.io/gh/openmrs/openmrs-contrib-android-client/branch/master/graph/badge.svg)](https://codecov.io/gh/f4ww4z/openmrs-contrib-android-client) [![IRC](https://img.shields.io/badge/IRC-%23openmrs-1e72ff.svg?style=flat)](http://irc.openmrs.org)

## Table of Contents
- [OpenMRS Android Client](#OpenMRS-Android-Client)
	- [Table of Contents](#Table-of-Contents)
- [Description](#Description)
			- [Key Features](#Key-Features)
- [Screenshots](#Screenshots)
- [Development](#Development)
		- [Code Style](#Code-Style)
		- [Model pattern](#Model-pattern)
		- [HTTP call debugging](#HTTP-call-debugging)
- [Quick Start](#Quick-Start)
		- [Steps to set up:](#Steps-to-set-up)
- [Demo Server](#Demo-Server)
				- [Demo Username: admin](#Demo-Username-admin)
				- [Demo Password: Admin123](#Demo-Password-Admin123)
- [Releasing [Collaborators only]](#Releasing-Collaborators-only)
- [Objectives](#Objectives)
		- [Goals:](#Goals)
- [User Manual](#User-Manual)
- [License](#License)
- [Resources](#Resources)

# Description
The purpose of this project is to provide an OpenMRS 2.x client for Android devices. The app is designed to cover most of the functionality currently on the web application.
The app communicates with OpenMRS instances using REST. It supports working offline (without network connection). The database on the device is encrypted and password protected to secure patient data.
For more information on the client, visit https://wiki.openmrs.org/display/projects/OpenMRS+2.x+Android+Client

# Development
We use JIRA to track issues and monitor project development. Refer to this link to view all issues and project summary: [Android Client JIRA](https://issues.openmrs.org/browse/AC). 
To get started contributing, try working on [introductory issues](https://issues.openmrs.org/issues/?filter=17165) in JIRA and check out [OpenMRS Pull Request Tips](https://wiki.openmrs.org/display/docs/Pull+Request+Tips).
Also, before creating a pull request, please run code review tools (Lint) and all tests.

#### 1. openmrs-android-sdk package
* There was a need to make the app extendable without forking it out, rather just adding it as a dependency in any android application which wants to use the functionality but with a custom UI on top of it.
* we cam add implementation 	
``` 
    dependencies {
                      implementation 'com.github.openmrs:openmrs-contrib-android-client:deploy-android-sdk-SNAPSHOT'
                 } 
```
as a dependency in the app module build.gradle to get the functionality provided by openmrs-android-sdk.
* The openmrs-android-sdk exposes the functionality through some methods divided in various repository classes which just need to be plugged in with the UI and view-Model.
* An simple example of the usage would be [this demo application](https://github.com/LuGO0/Test-Application), a more complex application depicting the usage would be the openmrs-client package itself. There is a confluence article [here](will add) which will take you through the creation and usage of the test Application mentioned above.
* The JavaDocs for the published Library [here](will add it after hosting it on our repository) 

#### 2. openmrs-client package
* This package was earlier used as the sole package containing all the code for the OpenMRS-Android-Client now a part of it has been encapsulated in the form of openmrs-android-sdk and published on Jitpack from where it can simply be added as a dependency to any other app.
* This package uses the methods exposed by the openmrs-android-sdk and build UI on top of it, which can be used as example to implement UI on top of openmrs-android-sdk.
* The app is also published on PlayStore just to get used to the fuctionalities it provides and can be tested on local OpenMRS server or Demo OpenMRS Server

#### Key Features
- Connect to OpenMRS server and sync all data
- Register and Edit patients
- Record Visits and Encounters
- View patient data (Details, Diagnoses, Visits, and Vitals)
- Offline access

# Screenshots
<img src="https://user-images.githubusercontent.com/45125121/82362785-804c2800-9a2a-11ea-9bb1-f1b778c70de5.jpg" width="280" height="520" alt="Login page" >  <img src="http://i.imgur.com/KmaWzNv.png" width="280" height="520"> <img src="http://i.imgur.com/hiCNNIx.png" width="280" height="520">

### Code Style
The coding conventions used by OpenMRS are outlined [here](https://wiki.openmrs.org/display/docs/Developer+How-To+Setup+And+Use+IntelliJ#DeveloperHow-ToSetupAndUseIntelliJ-SetupCodeStyleAndFormatForOpenMRS). These can be applied to Android Studio by following the steps given below.
1. Install [Eclipse Code Formatter](https://github.com/krasa/EclipseCodeFormatter) plugin in Android studio
	I. Go to `Settings` > `Preferences` > `Plugins`
	II. Select Marketplace and searh for the plugin by name. 
	Then install it
2. Copy the required [XML](https://github.com/openmrs/openmrs-core/blob/master/tools/src/main/resources/eclipse/OpenMRSFormatter.xml)
3. Go to `Settings` > `Eclipse Code Formatter` and import the XML created in previous step
4. Now go to `Editor` > `Code Style` in the same window
5. Select `Java` in the menu on left and update values in `Imports` as shown in below picture
![Java:Imports settings](https://wiki.openmrs.org/download/attachments/3346739/Screen%20Shot%202017-03-02%20at%2013.01.04.png?version=1&modificationDate=1488438089000&api=v2)
5. Now select `XML` in the menu on left and update values in `Tabs and Indents` tab as shown in below picture
![XMLOther setings](https://wiki.openmrs.org/download/attachments/3346134/Screen%20Shot%202017-03-10%20at%2010.52.44.png?version=2&modificationDate=1489121590000&api=v2)


Please follow the below instructions while contributing:
1. When project is opened with Android Studio, go to `File` > `Settings` > `Editor` > `Code Style`
2. Select Project from the Scheme dropdown
3. Make sure that **hard wrap** is set to **125** for Java and XML language settings.


### Model pattern
We are following [Google's MVP sample](https://github.com/googlesamples/android-architecture/tree/todo-mvp) for this application's development.


### HTTP call debugging
In order to debug HTTP calls, we have integrated [Chuck Library](https://github.com/jgilfelt/chuck) (a GUI based open source library) to track network calls. Apps using Chuck will display a notification showing a summary of ongoing HTTP activity. Tapping on the notification launches the full Chuck UI.

### Steps to set up:
1. Fork the repository and clone your fork.
2. From the Android Studio menu select `File` > `New` > `Import Project`. Alternatively, from the `Welcome screen`, select `Import project`.
3. Navigate to the folder where you have cloned this repo and select the `build.gradle` file inside. Select `Import`.
4. Done! Wait for dependencies to load and download from Maven, then you're ready to go!
5. Make sure that you have the latest SDK and build tools downloaded, as we always build against the latest release.

[In-depth tutorial](https://github.com/codepath/android_guides/wiki/Getting-Started-with-Gradle)

Note:
- If you are working behind a proxy, check [this](https://wiki.appcelerator.org/display/guides2/Using+Studio+From+Behind+a+Proxy) to get things working.
- To start development on a local server, type http://10.0.2.2:8080/openmrs (for Android Studio AVD) or http://10.0.3.2:8080/openmrs (for Genymotion) as URL.

# Demo Server

The demo test server dedicated to the client is (https://demo.openmrs.org/openmrs/).
In case the demo server fails to respond, you can use oher alternate servers provided [here.](https://wiki.openmrs.org/display/ISM/OpenMRS+environments)
##### Demo Username: admin
##### Password: Admin123
<br/>

# Releasing [Collaborators only]

### 1. OpenMRS-Android-Client to PlayStore
We follow the sprint model for development. Read more about it here: [OpenMRS Sprints](https://wiki.openmrs.org/display/RES/Development+Sprints).
To release the application, make sure to do these steps **in order**:

1. Update the [version variable in versions.gradle](https://github.com/openmrs/openmrs-contrib-android-client/blob/master/openmrs-client/versions.gradle#L6) prior to the release.
3. Update the [Release notes](releaseNotes.md) file.
4. Update the [release notes text file](https://github.com/openmrs/openmrs-contrib-android-client/blob/master/openmrs-client/src/main/play/release-notes/en-US/default.txt) to publish in the Play store. Ideally change the wording so that normal end users understand.
5. Now commit with the title `Release <version number here>` to the master branch.
6. Tag the commit, using the version as the tag name. Make sure CI is green!
7. Go to [the releases page](https://github.com/openmrs/openmrs-contrib-android-client/releases) and click the [Draft a new release](https://github.com/openmrs/openmrs-contrib-android-client/releases/new) button. It will create a new version tag in the repository and build the app. The tag name will be used as the version number for this. Be sure to bump unfinished issues to the next due version.
8. Go to [JIRA's releases page](https://issues.openmrs.org/projects/AC?selectedItem=com.atlassian.jira.jira-projects-plugin:release-page), click on the three-dots on the right, and hit **Release**.
9. Post a new Talk thread and describe what is changed or improved in the release.

### 2. Openmrs-Android-Sdk
1. The Openmrs-Android-sdk gets published to the Jitpack library so that it can be added as a dependency in various projects.
2. Due to some Issues with the release build configuration of the app explained in detail [here](https://stackoverflow.com/questions/68420822/handling-release-keystore-while-uploading-android-library-to-jitpack) we are not able to do jitpack releases from master branch.
3. There is a dedicated branch for this purpose which has got only the debug variant of the android application. So that the jitpack build passes.
4. For now untill the Issue gets resolved or we write a script for it, In order to release the latest code improvements in the openmrs-android-sdk package to the jitpack, we need to copy whole package from master to branch deploy-android-sdk and publish a snapshot of the branch to jitpack.

# User Manual
Check this link for the manual: [Version 2.9+](https://wiki.openmrs.org/download/attachments/235275984/User%20Guide.pdf?api=v2)

# License
This project is licensed under the OpenMRS Public License, see the [copyright](copyright/copyright) file for details.

# Resources
- [Contribution Guidelines](https://github.com/openmrs/openmrs-contrib-android-client/blob/master/CONTRIBUTING.md)
- [JIRA](https://issues.openmrs.org/browse/AC/?selectedTab=com.atlassian.jira.jira-projects-plugin:summary-panel)
- [Sprint board](https://issues.openmrs.org/secure/RapidBoard.jspa?rapidView=60)
- [Dashboard](https://issues.openmrs.org/secure/Dashboard.jspa?selectPageId=12851)
- [CI](https://travis-ci.org/openmrs/openmrs-contrib-android-client)
- [Google Play](https://play.google.com/store/apps/details?id=org.openmrs.mobile)
- [Release Notes](releaseNotes.md)
