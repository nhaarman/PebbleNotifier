# Releasing

 1. Update the `CHANGELOG.md` file with relevant info and date.
 2. Commit: `git commit -am "Prepare version X.Y.Z."`
 3. Merge into `master`: `git checkout master`, `git merge dev`
 4. Tag: `git tag -a X.Y.Z -m "Version X.Y.Z"`
 5. Release: `gradlew clean assembleRelease -PisRelease=true`
 6. Publish release in the Google Play Developer Console
 7. Checkout `dev`: `git checkout dev`
 8. Push: `git push && git push --tags`
 9. Update release information on https://github.com/nhaarman/PebbleNotifier/releases
 10. Grab a coffee.