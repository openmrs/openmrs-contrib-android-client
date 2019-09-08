[![Logo](http://i.imgur.com/fpVkTZk.png)](http://www.openmrs.org)

OpenMRS Android Client
==============================

[![Build Status Travis](https://travis-ci.org/openmrs/openmrs-contrib-android-client.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-contrib-android-client) [![Build Status AppVeyor](https://ci.appveyor.com/api/projects/status/github/openmrs/openmrs-contrib-android-client?branch=master&svg=true)](https://ci.appveyor.com/project/AvijitGhosh82/openmrs-contrib-android-client) [![Demo Server](https://img.shields.io/badge/demo-online-green.svg)](http://devtest04.openmrs.org:8080/openmrs) [![GitHub version](https://d25lcipzij17d.cloudfront.net/badge.svg?id=gh&type=6&v=2.6.1&x2=0)](https://github.com/openmrs/openmrs-contrib-android-client/releases/latest) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/37fa8e86a3cb4256a3b7ffcc644f13c6)](https://www.codacy.com/app/marzeion-tomasz/openmrs-contrib-android-client?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=openmrs/openmrs-contrib-android-client&amp;utm_campaign=Badge_Grade) [![codecov](https://codecov.io/gh/openmrs/openmrs-contrib-android-client/branch/master/graph/badge.svg)](https://codecov.io/gh/f4ww4z/openmrs-contrib-android-client) [![IRC](https://img.shields.io/badge/IRC-%23openmrs-1e72ff.svg?style=flat)](http://irc.openmrs.org)

## Table of Contents
- [OpenMRS Android Client](#OpenMRS-Android-Client)
	- [Table of Contents](#Table-of-Contents)
- [Description](#Description)
			- [Key Features](#Key-Features)
- [Screenshots](#Screenshots)
- [GSoC 2019](#GSoC-2019)
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
- [Release Notes](#Release-Notes)
        - [Version 2.8.0](#Version-280)
		- [Version 2.7.4](#Version-274)
		- [Summary:](#Summary)
		- [Version 2.7.3](#Version-273)
		- [Summary:](#Summary-1)
		- [Version 2.7.2](#Version-272)
		- [Summary:](#Summary-2)
		- [Version 2.7.1](#Version-271)
		- [Summary:](#Summary-3)
		- [Version 2.7.0](#Version-270)
		- [Summary:](#Summary-4)
		- [Version 2.6.2](#Version-262)
		- [Summary:](#Summary-5)
		- [Version 2.6.1](#Version-261)
		- [Summary:](#Summary-6)
		- [Version 2.6.0](#Version-260)
		- [Summary:](#Summary-7)
		- [Version 2.5](#Version-25)
		- [Summary:](#Summary-8)
		- [Version 2.4](#Version-24)
		- [Summary:](#Summary-9)
- [Objectives](#Objectives)
		- [Goals:](#Goals)
- [User Manual](#User-Manual)
- [License](#License)
- [Resources](#Resources)

# Description
The purpose of this project is to provide an OpenMRS 2.x client for Android devices. The app is designed to cover most of the functionality currently on the web application.
The app communicates with OpenMRS instances using REST. It supports working offline (without network connection). The database on the device is encrypted and password protected to secure patient data.
For more information on the client, visit https://wiki.openmrs.org/display/projects/OpenMRS+2.x+Android+Client

#### Key Features
- Connect to OpenMRS server and sync all data
- Register and Edit patients
- Record Visits and Encounters
- View patient data (Details, Diagnoses, Visits, and Vitals)
- Offline access

# Screenshots
![Login](http://i.imgur.com/zinrnCK.png) ![Dashboard](http://i.imgur.com/TLIwMoy.png) ![Register Patient](http://i.imgur.com/n7LaeKS.png)

# GSoC 2019

This project was also selected for Google Summer of Code 2019. More details can be found on the [Project Wiki Page](https://wiki.openmrs.org/pages/viewpage.action?pageId=216367730).

It was been selected as a candidate for Google Summer of Code 2017. Please visit the [Project Wiki Page](https://wiki.openmrs.org/display/projects/OpenMRS+Android+Client+Feature+Parity+and+Improvements+-+GSoC+2017) for more details.

The project was also a part of GSoC 2016, the details of which can be seen in the [GSoC_2016.md](GSoC_2016.md) file.

# Development
We use JIRA to track issues and monitor project development. Refer to this link to view all issues and project summary: [Android Client JIRA](https://issues.openmrs.org/browse/AC). 
To get started contributing, try working on [introductory issues](https://issues.openmrs.org/issues/?filter=17165) in JIRA and check out [OpenMRS Pull Request Tips](https://wiki.openmrs.org/display/docs/Pull+Request+Tips).
Also, before creating a pull request, please run code review tools (Lint) and all tests.

### Code Style
The coding conventions used by OpenMRS are outlined [here](https://wiki.openmrs.org/display/docs/Coding+Conventions).

### Model pattern
We are following [Google's MVP sample](https://github.com/googlesamples/android-architecture/tree/todo-mvp) for this application's development.


### HTTP call debugging
In order to debug HTTP calls, we have integrated [Android Snooper](https://github.com/jainsahab/AndroidSnooper) (a GUI based open source library) to track network calls. To use Android snooper, all you have to do is shake your device and snooper will present list of network calls made by OpenMRS app.

# Quick Start

As of February 2016, this project has been migrated to gradle to work successfully with Android Studio.

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

##### Demo Username: admin
##### Demo Password: Admin123

# Releasing [Collaborators only]

We follow the sprint model for development. Read more about it here: [OpenMRS Sprints](https://wiki.openmrs.org/display/RES/Development+Sprints).

To release the application, make sure to do these steps **in order**:

1. Update the [version variable in build.gradle](https://github.com/openmrs/openmrs-contrib-android-client/blob/master/openmrs-client/build.gradle#L21) prior to the release.
3. Update the [Release notes](#Release-Notes) section.
4. Update the [release notes text file](https://github.com/openmrs/openmrs-contrib-android-client/blob/master/openmrs-client/src/main/play/release-notes/en-US/default.txt) to publish in the Play store. Ideally change the wording so that normal end users understand.
5. Now commit with the title `Release <version number here>` to the master branch.
6. Tag the commit, using the version as the tag name. Make sure CI is green!
7. Go to [the releases page](https://github.com/openmrs/openmrs-contrib-android-client/releases) and click the [Draft a new release](https://github.com/openmrs/openmrs-contrib-android-client/releases/new) button. It will create a new version tag in the repository and build the app. The tag name will be used as the version number for this. Be sure to bump unfinished issues to the next due version.
8. Go to [JIRA's releases page](https://issues.openmrs.org/projects/AC?selectedItem=com.atlassian.jira.jira-projects-plugin:release-page), click on the three-dots on the right, and hit **Release**.
9. [Optional] Post a new Talk thread and describe what is changed or improved in the release.

# Release Notes

### Version 2.8.0
### Summary:
1. Add Dark mode to the app
2. Provider management: admins can now find, add, edit and delete providers
3. Refactor Repository names.
4. Fix details not showing when searching patients

### Version 2.7.4
### Summary:
1. Make UI more consistent and follow more of the material design specs
2. Implement Initial Provider Management
3. Remove redundant type cast
4. Add Floating Action Button in patient's details tab

<a name="version-2.7.3"></a>
### Version 2.7.3
### Summary:
1. Remove Apache HTTP API Client library
2. Patient now extends Person
3. Make Search primary function for Find Patients
4. Revamp Settings Page (new material design!)
5. Integrated android snooper for debugging purpose

<a name="version-2.7.2"></a>
### Version 2.7.2
### Summary:
1. Added Kotlin dependency to app level build.gradle file
2. Added release folder to gitignore
3. Initialized SQLCipher, made app 64-bit compliant
4. Changed Gradle to latest version
5. Fixed Play Publisher not publishing to Play store

<a name="version-2.7.1"></a>
### Version 2.7.1
### Summary:
1. Fixed bug on showing/hiding the password during login
2. Visit Notes can now correctly show details
3. Use Codecov as the code coverage tool
4. Add privacy policy link to Settings page
5. Re-developed OpenMRSLogger - faster loading times
6. Most buttons now follow the material design guideline
7. User is now taken back to the completed form on clicking cancel during registration
8. Replace country selection with a country picker
9. Setup Android Room and create entities
10. Add Contextual Action Bar in Synced Patients to delete multiple patients at once

<a name="version-2.7.0"></a>
### Version 2.7.0
### Summary:
1. Implement RxJava in DAOs and migration to AndroidX
2. Pick patient photo when registering
3. Login form improved and doesn't require login locations when no locations are configured
4. Add data validation when registering patients
5. Encrypted the database with BCrypt, derived from username and password
6. Show toasts when toggling sync button and fixed crash when updating non-synced patient
7. Saving user input when device is rotated, or when app instance is lost
8. Added a Privacy Policy
9. Renewed GitHub API Key
10. Set a Custom Path to look for the Release APK


<a name="version-2.6.2"></a>
### Version 2.6.2
### Summary:
1. Add patient picture
2. Bug Fixes

<a name="version-2.6.1"></a>
### Version 2.6.1
### Summary:
1. Handle camera and storage  permissions manually
2. Bug Fixes

<a name="version-2.6.0"></a>
### Version 2.6.0
### Summary:
1. Fixed patient selection when changing orientation
2. More user-friendly register form
3. Added unit tests
4. Filter patients by given/middle/family names at the same time
5. Get data from DB in background task
6. Lint check to GitHub Pull Requests

<a name="version-2.5"></a>
### Version 2.5
### Summary:
1. Log in offline
2. Coded fields in forms
3. Edit forms
4. Edit patients
5. Lots of bug fixes

<a name="version-2.4"></a>
### Version 2.4
### Summary:
1. Added merging patients registered offline
2. Find Patient storyboard refactoring
3. Fixed bugs

# Objectives

<a name="version-2.8+"></a>
### Version 2.8+ (next releases)
### Goals:
1. Forgot Password
2. Provider Relationship Module
3. UI Improvements
4. Analytics


# User Manual
Check this link for the manual: [Version 2.x](https://wiki.openmrs.org/download/attachments/74252444/User%20Manual%202.0.pdf?version=1&modificationDate=1414759790000&api=v2)

# License
This project is licensed under the OpenMRS Public License, see the [copyright](copyright/copyright) file for details.

# Resources
- JIRA https://issues.openmrs.org/browse/AC/?selectedTab=com.atlassian.jira.jira-projects-plugin:summary-panel 
- Sprint board https://issues.openmrs.org/secure/RapidBoard.jspa?rapidView=60
- Dashboard https://issues.openmrs.org/secure/Dashboard.jspa?selectPageId=12851
- CI https://travis-ci.org/openmrs/openmrs-contrib-android-client
- Google Play https://play.google.com/store/apps/details?id=org.openmrs.mobile
