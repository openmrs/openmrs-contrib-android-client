# Contributing to the OpenMRS Android Client.
OpenMRS is a collaborative open-source project to develop software to support the delivery of health care in developing countries. It allows the designing of medical record systems with minimal programming knowledge. Our mission is to improve health care delivery in resource-constrained environments by coordinating a global community to create and support this software.

The purpose of the Android Client is to provide an OpenMRS 2.x client for Android devices. The app is designed to cover most of the functionality currently on the web application. The app communicates with OpenMRS instances using REST. It supports working offline.


## Installing Git
The first step to starting to contribute to the Android Client is installing Git Bash. You can also use Github Desktop, but using Bash is recommended. This is because Bash allows the user to perform more commands as compared to the latter. After downloading, you need to log in to Git Bash using your GitHub credentials.
* [Download Git Bash for Mac](https://git-scm.com/download/mac) 
* [Download Git Bash for Windows](https://git-scm.com/download/win) 
* [Download Git Bash for Linux/UNIX](https://git-scm.com/download/linux) 
* [Download Github Desktop](https://desktop.github.com/)      
 

## Steps to set up:
1. Fork the repository and clone your fork.
2. From the Android Studio menu select `File` > `New` > `Import Project`. Alternatively, from the `Welcome screen`, select `Import project`.
3. Navigate to the folder where you have cloned this repo and select the `build.gradle` file inside. Select `Import`.
4. Done! Wait for dependencies to load and download from Maven, then you're ready to go!
5. Make sure that you have the latest SDK and build tools downloaded, as we always build against the latest release.

### Forking and Cloning the repository
The first actual step in contributing to any open source project is forking and cloning the repository. Just head on to the repository on GitHub and click on the *Fork* button.

The next step is to clone your fork of the repository. You can do that by running    
```git clone https://github.com/[username]/openmrs-contrib-android-client``` in Git Bash or your terminal. 


### Code Style
The coding conventions used by OpenMRS are outlined [here](https://wiki.openmrs.org/display/docs/Developer+How-To+Setup+And+Use+IntelliJ#DeveloperHow-ToSetupAndUseIntelliJ-SetupCodeStyleAndFormatForOpenMRS). These can be applied to Android Studio by following the steps given below.
1. Install [Eclipse Code Formatter](https://github.com/krasa/EclipseCodeFormatter) plugin in Android studio
   I. Go to `Settings` > `Preferences` > `Plugins`
   II. Select Marketplace and search for the plugin by name. 
   Then install it
2. Copy the required [XML](https://github.com/openmrs/openmrs-core/blob/master/tools/src/main/resources/eclipse/OpenMRSFormatter.xml)
3. Go to `Settings` > `Eclipse Code Formatter` and import the XML created in the previous step
4. Now go to `Editor` > `Code Style` in the same window
5. Select `Java` in the menu on the left and update values in `Imports` as shown in the below picture
![Java:Imports settings](https://wiki.openmrs.org/download/attachments/3346739/Screen%20Shot%202017-03-02%20at%2013.01.04.png?version=1&modificationDate=1488438089000&api=v2)
5. Now select `XML` in the menu on left and update values in the `Tabs and Indents` tab as shown in the below picture
![XMLOther setings](https://wiki.openmrs.org/download/attachments/3346134/Screen%20Shot%202017-03-10%20at%2010.52.44.png?version=2&modificationDate=1489121590000&api=v2)


Please follow the below instructions while contributing:
1. When project is opened with Android Studio, go to `File` > `Settings` > `Editor` > `Code Style`
2. Select Project from the Scheme dropdown
3. Make sure that **hard wrap** is set to **125** for Java and XML language settings.


### Model pattern
We are following [Google's MVP sample](https://github.com/googlesamples/android-architecture/tree/todo-mvp) for this application's development.


### HTTP call debugging
In order to debug HTTP calls, we have integrated [Chuck Library](https://github.com/jgilfelt/chuck) (a GUI-based open-source library) to track network calls. Apps using Chuck will display a notification showing a summary of ongoing HTTP activity. Tapping on the notification launches the full Chuck UI.

[In-depth tutorial](https://github.com/codepath/android_guides/wiki/Getting-Started-with-Gradle)

Note:
- If you are working behind a proxy, check [this](https://wiki.appcelerator.org/display/guides2/Using+Studio+From+Behind+a+Proxy) to get things working.
- To start development on a local server, type http://10.0.2.2:8080/openmrs (for Android Studio AVD) or http://10.0.3.2:8080/openmrs (for Genymotion) as URL.


## Finding an Issue
Now that you've created a local repository, the next step is to find an issue to work on. You can either choose an existing issue or create an Issue ticket of your own.  
All the Issues for the Android Client can be found [here](https://issues.openmrs.org/projects/AC/issues).   
Please note that to gain access to the Issue Tracker, you would first have to submit a ticket to the helpdesk. For more details, head over to [this link](https://wiki.openmrs.org/display/docs/How+to+get+access+to+issues.openmrs.org)
If the issue is tagged ```Ready for Work```, you may claim it and start working on it.

## Filing an Issue
To create an issue, you would need access to the Issue Tracker. To gain access, head over to [this link](https://wiki.openmrs.org/display/docs/How+to+get+access+to+issues.openmrs.org) and follow the instructions.  
* Once you have access, Sign in to your account.
* Next, head over to the [Android Client Issues Page.](https://issues.openmrs.org/projects/AC/issues)
* Click on the Create button to start creating a new issue.
  * First, choose the issue type that best describes your issue.
  * Provide a short summary of the issue.
  * Your Issue description should cover the following points.
    * Current Behavior
    * Expected Behavior
    * If it's a bug, then you need to write the Steps to reproduce the bug
    * Also remember to add images to make things more clear.
    * Add appropriate labels and Create the Issue.
  * Once an org representative tags your issue as *Ready for Work*, you can claim the issue.




## Working on your local repository
* First, open Git Bash and ```cd``` into your repository folder.
* It is common practice to create a branch for every pull request. 
* To create one, type ```git checkout -b [AC-<issue-no.>]```  
  * As stated in the [OpenMRS Pull Request Guidelines](https://wiki.openmrs.org/display/docs/Pull+Request+Tips), do not forget to name your branch the same as the issue that you have worked on
* Make the changes to the files that will solve the issue according to you. 

## Performing a Gradle Check (for Android Projects)
It is always advised to run a Gradle check before you create a pull request. You can do this by typing ```./gradlew check``` in your terminal of Android Studio. If the check is failing, please fix all the errors.

## Committing your changes
When you're done with the changes, type these commands in the Git Bash.  
* ```git add .``` 
* ```git commit -m "commit message"```
* ```git push```  

Please note that the format of your commit message is supposed to be as descriptive as possible.
Also, make sure you're in the branch that you created.

## Making a Pull Request
When you're done with all the steps above, you're finally ready to make a pull request. A pull request is you requesting the org maintainers to merge your changes to the actual project.
* You can make a pull request by heading over to your repository link, where you'll see your latest commit, and a button with the text ```Create Pull Request``` on it.
* Title your Pull Request as ```[AC-<IssueNumber>] <Short Description of what you did>```
* Fill in all the details in the Pull Request template and finally post the Pull Request.

## Squashing your commits
It is very common that a PR doesn't get approved on the first try and you are told to do some edits to your code. In a situation like this, your PR is obviously going to have a lot of commits. A large number of commits makes it difficult to manage the commit tree. That's when squashing is going to help you combine all the commits into one single one. You can squash your commits by typing these commands in the Bash:

```git rebase -i HEAD~x``` Please note that x is the number of commits you want to merge.

For example: ```git rebase -i HEAD~2``` opens rebase for the last two commits.

You'll now see the commits that you've done in the branch with ```pick``` as the prefix. 
* Press ```i``` on your keyboard, and change ```pick``` to ```s``` for every commit except the topmost one. 
* Once you've done that, you'll need to save that. You can do this by pressing ```esc``` key and then typing ```:wq``` and pressing ```enter``` key. 
* Then you'll see a screen which shows the commit messages for the new changes that you've just done. Type ```:wq``` again.  

Now that it's saved, you'll need to again push your changes. You can do this by typing ```git push --force``` in the Bash Terminal.

## Editing your commits
Often times you'll be told to edit your commit message to the one suggested by the org maintainers. You can do this by typing ```git commit --amend```.
