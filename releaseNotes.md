
# Release Notes

### Version 3.0.0

1. [AC-574] - Android Client cannot update provider password
2. [AC-575] - Password Reset
3. [AC-584] - Renew GITHUB API key to deploy to Play store
4. [AC-657] - Adding an about section
5. [AC-717] - updating the PR template so that someone dosent forgets the JIRA workflow
6. [AC-722] - after starting a Visit replace the start button with an end button
7. [AC-821] - Fix deprecation warnings
8. [AC-869] - Full name of languages should be used.
9. [AC-906] - Re-design Home screen with a better UI.
10. [AC-926] - Using View Binding Architecture component in various packages.
11. [AC-945] - android.viewBinding.enabled is obsolete and should be replaced
12. [AC-951] - Migrate matchingpatients package from Java to Kotlin & ViewBinding
13. [AC-958] - Correcting app screenshots' aspect ratio in README.md file
14. [AC-962] - Stay on the SyncedPatientsActivity after deleting synced patient(s)
15. [AC-973] - Feature of setting system default theme
16. [AC-984] - Delete Operation Offline support for Allergy Repository
17. [AC-985] - Offline Strategy for Patient Repository
18. [AC-543] - Delay while inflating layout of Find Patients and Form Entry
19. [AC-545] - Loss of Data after Logout and re-login with same username,password & url
20. [AC-581] - Failed to resolve variable '${project.groupId}'
21. [AC-613] - Job 'generateReleasePlayResources' failed
22. [AC-616] - Encrypt play store files earlier in CI build
23. [AC-669] - Implementing a method to show detailed server error
24. [AC-774] - Travis CI fails to detect failed tests
25. [AC-775] - Theme bug causing glitch when opening register patient
26. [AC-792] - The Intent is not implemented properly
27. [AC-800] - Redesign register patient form as a multi step form
28. [AC-862] - On tapping email us in contact page, the recipient of mail is not correct
29. [AC-864] - The GetProviders method seems incorrect in the providerRepository
30. [AC-865] - We should avoid the use of appContext inside repository layer
31. [AC-866] - The title of action bars is not translated to Hindi.
32. [AC-883] - QuickFix for patientIdentifier Service omission
33. [AC-887] - Memory leak in Fragments
34. [AC-889] - An improvement is needed in the login page as in dark mode the username,password and especially the server url text colours make it not visible correctly.
35. [AC-890] - An improvement is needed in the Settings page UI. The language button English option text is not displayed correctly in some of the devices.
36. [AC-946] - Bug in the language switching feature.
37. [AC-961] - Buttons of alert dialog are not clearly visible in dark mode.
38. [AC-965] - Form Entry TextView Overlap with Image
39. [AC-967] - App Crashes after registering patient
40. [AC-968] - Provider name is not correctly saved in server when switching from offline to online mode
41. [AC-969] - Add Provider Worker is called even after syncing provider when switching from offline to online mode
42. [AC-970] - Client fails to edit or delete a newly created Provider
43. [AC-983] - After Deleting Providers the parent screen still holds old data untill a refresh.
44. [AC-986] - The Provider fetch function duplicates data in the feed
45. [AC-982] - NPE in Patient Photo Activity
46. [AC-981] - Application Rehaul for the Library code encapsulation
47. [AC-798] - Add and view supervised patients for the current provider
48. [AC-834] - Migrate room Entity classes to Kotlin
49. [AC-842] - Add Repository Abstraction for AddEditPatient Package
50. [AC-879] - Migrate Form List Package to Kotlin
51. [AC-888] - Migrate Login Screen to Kotlin
52. [AC-892] - Migrate Add Provider Screen to Kotlin
53. [AC-898] - Convert Java classes of Model Package to Kotlin
54. [AC-929] - Provider Manager in the Dashboard Screen is redirecting to wrong activity
55. [AC-948] - Migrate Logs Screen from MVP Architecture to MVVM architecture
56. [AC-950] - Migrate Provider Dashboard Package from Java to Kotlin.
    
### Version 2.9.0

1. Updated UI for different activities (#787, #785, #803, #806, #808, #809)
2. Added repository layer for different activity layer (#784, #791, #797, #799)
3. Migrated codebase to kotlin (#794, #802, #811, #814)
4. Removed unused resources (#788, #805)
5. Created offline support for Allergy module (#786)
6. Create allergy based on server configuration (#796)
7. Allergy can be updated as per server settings (#798)
8. Allergy can be deleted in online/offline mode (#793)
9. Overloaded methods of AppDataBase Helper class (#789)
10. Removed Collapsable bar in provider dashboard (#800)
11. Removed unnecessary restAPI param passing to the repository (#792)
12. Migrated Chart ListAdapter to RecyclerView (#807)
13. Fixed app restarts on downloading concepts (#795)
14. Fixed crash at admission form by using live data (#804)
15. Fixed Release version blocker (#790)
16. Added possible NPE checks at PatientComparator (#810)

### Version 2.8.4

1. Create admission form (#696)
2. Migrate Java code to Kotlin (#757, #766, #765, #764, #767)
3. Fix Floating Action Button bugs (#763, #769)
4. Add crop image feature in patient registration (#768)
5. Update UI to follow material design (#770, #778, #777)
6. Upgrade dependencies to the latest versions (#772)
7. Add expandable FAB for provider dashboard (#773)
8. Add empty active visits view (#771)
9. Mark patient deceased using coded response (#774)
10. Removed swipe actions in provider dashboard (#775)
11. Fix the reverse flipping bug in patient dashboard (#779)
12. Add allergy tab to fetch allergies from server (#780)
13. Finish migration from ActiveAndroid to Room (#781, #783)
14. Add repository layer in visit dashboard (#782)

### Version 2.8.3

1. Fixed critical bugs (#718, #721, #722, #730, #734, #741, #742)
2. Add showcase view for manage providers module (#720)
3. Continue creating Room DAOs (#727, #736, #739, #740, #735, #738)
4. Refined custom dialog box in add patient activity (#731)
5. Start visit is disabled in offline mode (#729)
6. Add option for 'Unknown' patients when registering (#733)
7. Change main font to Roboto (#745)
8. Fixed app crash on lower API devices (#749)
9. Avoid keyboard blocking SnackBar messages (#747)
10. Change endpoint debugger to Chucker to avoid crash (#750)
11. Better localization (#723)
12. Fix ill formed provider fields (#751)
13. UI overhaul, better compliant with Material UI guidelines (#744, #746, #743, #753, #759, #761, #760)
14. Migrate Java code to Kotlin (#752, #758, #756)
15. Add offline support for provider module (#748)
16. Double back press to exit the app (#762)

### Version 2.8.2
1. Add contribution guidelines to the repository (#671, #662)
2. Removed unused resources, decreasing APK size (#669)
3. Removed hardcoded strings (#673)
4. Dashboard now responds to change in app theme (#661)
5. Add language translation in hindi (#650)
6. Create Room Data Access Objects (DAOs) (#670, #659, #682, #657, #662)
7. UI fixes in register patient screen (#686)
8. Added reset button (#692)
9. Improved login and fragment dialog button design (#672)
10. Improve PatientDetailFragment UI (#700)
11. Fix app crash on entering values in email or password text fields (#691)
12. Add City and State selector using Google Places API (#694)
13. Fix crash at register patient activity (#717)
14. Fix context-related errors (#713, #716)
15. Add swipe to refresh feature in relevant screens (#701)
16. Add Contact Us screen (#710)
17. Migrate POJO classes to Kotlin (#675, #719)
18. Add validation to HIV status in Visit Notes screen (#698)

### Version 2.8.1
### Summary:
1. Fix window leakage error when viewing patient details
2. Fix various performance issues
3. Integrate leak canary for debug builds
4. Add a splash screen
5. Add intro slides for first time users
6. Set a code style
7. Fix broken unit tests

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

<a name="version-2.9+"></a>
### Version 2.9+ (next releases)
### Goals:
1. Material UI design
2. Integrate more components of the webapp to the client
3. Password reset via email
4. Replace Java code with Kotlin
5. Migrate ActiveAndroid SQL library to room library