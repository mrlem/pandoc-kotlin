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

## Publishing a new version

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
* publish locally (i.e. to `~/.m2/repository`) using:
```bash
./gradlew publishToMavenLocal
```
* publish to [Maven Central](https://central.sonatype.com/) using:
```bash
./gradlew publishToMavenCentral
```