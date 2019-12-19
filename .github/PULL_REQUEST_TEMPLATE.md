<!--- Add a pull request title above in this format -->
<!--- real example: 'AC-204 Applying MVP Model' -->
<!--- 'AC-JiraIssueNumber JiraIssueTitle' -->

## Description of what I changed
The class that was causing problem was Cache.java which was a child of ActiveAndroid and was using android.support.v4.LruCache instead of android.util.LruCache which is the current supported version.
But this couldn't be changed directly in the class as it was only read only, so I created another class new_cache.java and transferred everything to it to fix the Unit Test Issue.
I also created new_ModelInfo.java class because the original one which was a child of ActiveAndroid couldn't be imported and used this new one in new_cache.java

## Issue I worked on

JIRA Issue: https://issues.openmrs.org/browse/AC-635

## Checklist: I completed these to help reviewers :)
<!--- Put an `x` in the box if you did the task -->
<!--- If you forgot a task please follow the instructions below -->
- [x] My pull request only contains **ONE single commit**
(the number above, next to the 'Commits' tab is 1).
<!--- No? -> [read here](https://wiki.openmrs.org/display/docs/Pull+Request+Tips) on how to squash multiple commits into one -->

- [ ] I have **added tests** to cover my changes. (If you refactored
existing code that was well tested you do not have to add tests)
<!--- No? -> write tests and add them to this commit `git add . && git commit --amend`-->

- [x] All new and existing **tests passed**.
<!--- No? -> figure out why and add the fix to your commit. It is your responsibility to make sure your code works. -->

- [x] My pull request is **based on the latest changes** of the master branch.
<!--- No? Unsure? -> execute command `git pull --rebase upstream master` -->