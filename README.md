[![Logo](http://i.imgur.com/fpVkTZk.png)](http://www.openmrs.org)

OpenMRS Android Client
==============================

[![Build Status](https://travis-ci.org/openmrs/openmrs-contrib-android-client.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-contrib-android-client) [![Demo Server](https://img.shields.io/badge/demo-online-green.svg)](http://devtest04.openmrs.org:8080/openmrs) [![GitHub version](https://d25lcipzij17d.cloudfront.net/badge.svg?id=gh&type=6&v=2.5&x2=0)](https://github.com/openmrs/openmrs-contrib-android-client/releases/latest) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/37fa8e86a3cb4256a3b7ffcc644f13c6)](https://www.codacy.com/app/marzeion-tomasz/openmrs-contrib-android-client?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=openmrs/openmrs-contrib-android-client&amp;utm_campaign=Badge_Grade) [![IRC](https://img.shields.io/badge/IRC-%23openmrs-1e72ff.svg?style=flat)](http://irc.openmrs.org)

## Table of Contents
* [Description](#description)
	* [Key Features](#key-features)
* [Screenshots](#screenshots)
* [GSoC 2016](#gsoc-2016)
* [Development](#development)
	* [Code Style](#code-style)
	* [Model Pattern](#model-pattern)
* [Quick Start](#quick-start)
* [Demo Server](#demo-server)
* [Releasing](#releasing)
* [Release Notes](#release-notes)
	* [Version 2.5](#version 2.5)
	* [Version 2.4](#version 2.4)
* [Objectives](#objectives)
	* [Version 2.6](#version 2.6)
* [User Manual](#user-manual)
* [License](#license)
* [Resources](#resources)

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

# GSoC 2016

The project has been selected as a candidate for Google Summer of Code 2016. Please visit the project's page for more details: https://wiki.openmrs.org/display/projects/OpenMRS+Android+Client+-+GSoC+2016

For a detailed description of all work done during GSoC 2016, please checkout the [GSoC_2016.md](GSoC_2016.md) file.

# Development
We use JIRA to track issues and monitor project development. Refer to this link to view all issues and project summary: [Android Client JIRA](https://issues.openmrs.org/browse/AC)
To get started contributing, try working on [introductory issues](https://issues.openmrs.org/issues/?filter=17165) in JIRA and check out [OpenMRS Pull Request Tips](https://wiki.openmrs.org/display/docs/Pull+Request+Tips). 
Also, before creating a pull request, please run code review tools (Lint) and all tests.

### Code Style
The coding conventions used by OpenMRS are outlined [here](https://wiki.openmrs.org/display/docs/Coding+Conventions).

### Model pattern
We are following [Google's MVP sample](https://github.com/googlesamples/android-architecture/tree/todo-mvp) for this application's development.

# Quick Start

As of February 2016, this project has been migrated to gradle to work successfully with Android Studio. 

### Steps to set up:
1. Fork the repository and clone your fork.
2. From the Android Studio menu select File > New > Import Project. Alternatively, from the Welcome screen, select Import project.
3. Navigate to the folder where you have cloned this repo and select the build.gradle file inside. Select "Import".
4. Done! Wait for dependencies to load and download from Maven, then you're ready to go! 
5. Make sure that you have the latest SDK and build tools downloaded, as we always build against the latest release.

[In-depth tutorial](https://github.com/codepath/android_guides/wiki/Getting-Started-with-Gradle)

Note:
- If you are working behind a proxy, check [this](https://wiki.appcelerator.org/display/guides2/Using+Studio+From+Behind+a+Proxy) to get things working.
- To start development on a local server, type http://10.0.2.2:8080/openmrs (for Android Studio AVD) or http://10.0.3.2:8080/openmrs (for Genymotion) as URL.

# Demo Server

The demo test server dedicated for the client is (http://devtest04.openmrs.org:8080/openmrs).

##### Demo Username : admin
##### Demo Password : Admin123

# Releasing

We follow the sprint model for development. Read more about it here: [OpenMRS Sprints](https://wiki.openmrs.org/display/RES/Development+Sprints).

In order to release the application, go to [releases](https://github.com/openmrs/openmrs-contrib-android-client/releases) and click the [Draft a new release](https://github.com/openmrs/openmrs-contrib-android-client/releases/new) button. It will create a new version tag in the repository and build the app. The tag name will be used as the version number for this release.

If you want to release a new major or minor version, please be sure to update the applicationVersion variable in [build.gradle](https://github.com/openmrs/openmrs-contrib-android-client/blob/master/openmrs-client/build.gradle#L26) prior to the release.

# Release Notes

<a name="version 2.5"></a>
### Version 2.5
### Summary:
1. Log in offline
2. Coded fields in forms
3. Edit forms
4. Edit patients
5. Lots of bug fixes

<a name="version 2.4"></a>
### Version 2.4 
### Summary: 
1. Added merging patients registered offline
2. Find Patient storyboard refactoring
3. Fixed bugs

# Objectives

<a name="version 2.6"></a>
### Version 2.6 (next release)
### Goals: 
1. Add/Edit/View Patient Photo
2. Lint Code Review Integration
3. Asynchronous Data Synchronization

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