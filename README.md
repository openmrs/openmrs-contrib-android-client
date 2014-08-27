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
1. Make sure you have installed the Android Support Library, from the Android SDK Manager. 
2. In *IDEA*: Open your project, then select **File / Import Module** 
    * select the directory under the SDK: **{SDK}\extras\android\support\v7\gridlayout** 
    * select Import module from external model and Eclipse
    * you can then select Create module files near .classpath files, or choose to put them elsewhere if you can't / don't want to write into your SDK path
    * in the next screen you should see projects to import and android-support-v7-gridlayout, *IDEA* will also ask you to reload the project.
    * this should add a module gridlayout *[android-support-v7-gridlayout]* in your project.
3. Now you have to update your project dependencies to this module for the resources, and add the .jar libraries. **Select File / Project Structure**
4. Steps from point 2 but for **{SDK}\extras\android\support\v7\appcompat**
5. Add libs for openmrs-client module
    * openmrs-client module **libs folder**
    * android-support-v4.jar and android-support-v7-appcompat.jar from **{SDK}\extras\android\support\v7\appcompat**
6. Add libs for tests module
    * tests module **libs folder**

### In case of problems use *IDEA IntelliJ 13* instead of *Android Studio* for configuration also follow http://stackoverflow.com/a/18916738/584369