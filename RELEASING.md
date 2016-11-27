Releasing
========

 1. Change the version in `gradle.properties` to a non-SNAPSHOT version.
 2. Update the `CHANGELOG.md` for the impending release.
 3. Update the `README.md` with the new version.
 4. Update documentation on the website.
 5. Update download info on the website (including the button in the page header).
 6. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the new version)
 7. `./gradlew clean install bintrayUpload`.
 8. Visit [Bintray](https://bintray.com/) and sync with MavenCentral.
 9. `git tag -a X.Y.X -m "Version X.Y.Z"` (where X.Y.Z is the new version)
 10. Update the `gradle.properties` to the next SNAPSHOT version.
 11. `git commit -am "Prepare next development version."`
 12. `git push && git push --tags`
