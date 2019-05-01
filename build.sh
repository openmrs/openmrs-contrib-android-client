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

./gradlew test

if [ -n "$TRAVIS_TAG" ]; then

echo "Building relase apk for Github and Play Store..."
echo $KEYSTORE_GPG_PASSPHRASE | gpg --passphrase-fd 0 release.keystore.gpg
echo $KEYSTORE_GPG_PASSPHRASE | gpg --passphrase-fd 0 google_play.json.gpg

./gradlew build assembleRelease
./gradlew publishRelease

else

./gradlew assembleDebug lint

fi
