openmrs-contrib-android-client
==============================

### Android client for OpenMRS

#Description
The goal of this project is to provide OpenMRS 2.x client for Android devices. The app is designed to cover most of the functionality of the web application including registering patients, taking visit notes, capturing vitals, etc.
The app will communicate with OpenMRS using REST. It will support working off-line (without network connection) with a chosen subset of patients. The database on the device will be encrypted and password protected to secure patient data.
For more information, including screenshots of the client, visit https://wiki.openmrs.org/display/projects/OpenMRS+2.x+Android+Client

# Development
Before creating pull request run code review tools, *PMD & Checkstyle*, and tests.

### Build project with code review tools
 
     ant clean debug

### Build test and run them on device 

     cd tests/
     ant clean debug install test

# Configuration 
1. Add libs for openmrs-client module
    * openmrs-client module **libs folder**
    * android-support-v4.jar and android-support-v7-appcompat.jar from **/support/appcompat/libs/**
2. Add modules from directory
    * modules/odk
    * modules/support/appcompat
    * modules/support/gridlayout
3. Add libs for tests module
    * tests module **libs folder**
