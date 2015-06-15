openmrs-contrib-android-client
==============================

### Android client for OpenMRS

#Description
The goal of this project is to provide OpenMRS 2.x client for Android devices. The app is designed to cover most of the functionality of the web application including registering patients, taking visit notes, capturing vitals, etc.
The app will communicate with OpenMRS using REST. It will support working off-line (without network connection) with a chosen subset of patients. The database on the device will be encrypted and password protected to secure patient data.
For more information, including screenshots of the client, visit https://wiki.openmrs.org/display/projects/OpenMRS+2.x+Android+Client

# Development
Before creating pull request run code review tools, *PMD & Checkstyle*, and tests.

### Setup local OpenMRS server

1. Download, unzip and run https://sourceforge.net/projects/openmrs/files/releases/OpenMRS_2.2/openmrs-standalone-2.2.zip/download
2. Download https://wiki.openmrs.org/download/attachments/74252444/webservices.rest-omod-2.12-20150615.175221-10.omod?version=1&modificationDate=1434391646461&api=v2
3. Login with username "admin" and password "Admin123". Go to Administration -> Advanced Administration -> Manage Modules -> Add or Upgrade Module -> Type: xforms and hit install. Repeat to upgrade webservices.rest, but instead of typing upload the omod file downloaded in the previous step.
4. Restart the server.
5. You should be able to login using your Android Client given server is accessible over the network.

### Build project with code review tools
 
     ant clean debug

### Build test and run them on device 

     cd tests/
     ant clean debug install test

# Configuration
1. Add modules from directory
    * modules/odk
    * modules/support/appcompat
    * modules/support/gridlayout
3. Add libs for openmrs-client module
    * openmrs-client module **libs folder**
    * android-support-v4.jar and android-support-v7-appcompat.jar from **/support/appcompat/libs/**
4. Add libs for tests module
    * tests module **libs folder**

### In case of problems (1. modules) use *IDEA IntelliJ 13* instead of *Android Studio* for configuration also follow http://stackoverflow.com/a/18916738/584369

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
- CI http://openmrs-ac-ci.soldevelo.com/ci/ 
- Google Play https://play.google.com/store/apps/details?id=org.openmrs.mobile
