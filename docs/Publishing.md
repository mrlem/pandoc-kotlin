# Publishing

## Pre-requisites

* have the project GPG private key in your keyring
* run the GPG agent:

```bash
gpgconf --launch gpg-agent
```

Note: you can also check your setup using:

```bash
echo "test" | gpg --clearsign
```

* add your maven central credentials, in `~/.gradle/gradle.properties`:
```properties
mavenCentralUsername=username
mavenCentralPassword=password
```
* configure GPG for signing:
  - add the signing key to your GPG keyring
  - run the GPG agent:
```bash
gpgconf --launch gpg-agent
```

## Publishing a new version

Considering you want to publish version X.Y.Z:

* modify new version number:
  - `Readme.md` sample code to use X.Y.Z
  - `library/build.gradle.kts` version variable to X.Y.Z
  - `sample/build.gradle.kts` build file to use X.Y.Z
* commit new version number:
```bash
git commit -m "chore: bump version to X.Y.Z"
git push
```
* pre-publish to [Maven Central](https://central.sonatype.com/) using:
```bash
./gradlew publishToMavenCentral
```
* publish in Maven Central, wait for published status
* add tag:
```bash
git tag vX.Y.Z
git push --tags
```
