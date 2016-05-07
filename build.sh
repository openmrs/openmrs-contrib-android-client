#!/usr/bin/env bash
set -e

chmod +x gradlew

if [ -n "$TRAVIS_TAG" ]; then

echo "Building the app for the Play Store..."
echo $encryptpass | gpg --passphrase-fd 0 release.keystore.gpg
./gradlew build assembleRelease

else

./gradlew assembleDebug

fi
