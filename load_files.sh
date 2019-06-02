#!/usr/bin/env bash

# Decrypt GitHub release and Play Store files on a new release

set -e

if [[ -n "$TRAVIS_TAG" ]]; then

echo "Decrypting keystore..."
echo $KEYSTORE_GPG_PASSPHRASE | gpg --passphrase-fd 0 release.keystore.gpg
echo "Decrypting service account credentials..."
echo $KEYSTORE_GPG_PASSPHRASE | gpg --passphrase-fd 0 google_play.json.gpg

fi