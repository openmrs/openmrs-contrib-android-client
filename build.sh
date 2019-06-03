#!/usr/bin/env bash

# For the script to work, please set the following environment variables in Travis:
# KEYSTORE_PASSWORD - the password for release.keystore
# KEYSTORE_ALIAS_NAME - the alias name in release.keystore
# KEYSTORE_ALIAS_PASS - the password name for the alias
# KEYSTORE_GPG_PASSPHRASE - the password for decrypting release.keystore.gpg
# GITHUB_API_KEY - Your github api key with public repo access
# PUBLISHER_ACCOUNT_ID - Email ID for Developer Console


set -e

chmod +x gradlew

echo "Running tests..."
./gradlew test

if [[ -n "$TRAVIS_TAG" ]]; then

echo "Tagged build: building release..."
./gradlew build assembleRelease
echo "Publishing release to the Play store..."
./gradlew publishRelease

else

echo "Debug build: building..."
./gradlew assembleDebug lint

fi