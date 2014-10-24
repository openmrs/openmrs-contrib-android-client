openmrs-contrib-android-client
==============================

### Android client for OpenMRS

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