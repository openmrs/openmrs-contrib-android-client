<!--- Add a pull request title above in this format -->
<!--- real example: 'AC-204 Applying MVP Model' -->
<!--- 'AC-JiraIssueNumber JiraIssueTitle' -->

## Description of what I changed
<!--- Describe your changes in detail -->
<!--- It can simply be your commit message, which you must have -->

## Issue I worked on
<!--- This project only accepts pull requests related to open issues -->
<!--- Want a new feature or change? Discuss it in an issue first -->
<!--- Found a bug? Point us to the issue/or create one so we can reproduce it -->
<!--- Just add the issue number at the end: -->
JIRA Issue: https://issues.openmrs.org/browse/AC-

## Checklist: I completed these to help reviewers :)
<!--- Put an `x` in the box if you did the task -->
<!--- If you forgot a task please follow the instructions below -->
- [ ] My pull request only contains **ONE single commit**
(the number above, next to the 'Commits' tab is 1).
<!--- No? -> [read here](https://wiki.openmrs.org/display/docs/Pull+Request+Tips) on how to squash multiple commits into one -->

- [ ] I have **added tests** to cover my changes. (If you refactored
existing code that was well tested you do not have to add tests)
<!--- No? -> write tests and add them to this commit `git add . && git commit --amend`-->

- [ ] All new and existing **tests passed**.
<!--- No? -> figure out why and add the fix to your commit. It is your responsibility to make sure your code works. -->

- [ ] My pull request is **based on the latest changes** of the master branch.
<!--- No? Unsure? -> execute command `git pull --rebase upstream master` -->