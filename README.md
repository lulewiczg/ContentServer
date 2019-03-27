# Content Server

Simple, lightweight servlet webapp, that can be run on embeded servlet container on Android (e.g. i-jetty). 
Server allows to download files, add shared folders and users. App is written in java 1.7, so that older devices can run it.

## Getting Started

### Prerequisites

Requirements:
* Device with Android
* [i-jetty](https://github.com/jetty-project/i-jetty) or other servlet container
* [Android SDK](https://developer.android.com/studio/releases/platform-tools)
* Java 1.7 or higher

### Installing
Steps are descirbed for i-jetty:
1. Download and compile code (IDE, Maven or javac)
2. Create new folder, lets call it "App".
3. Copy 
```
src/main/webapp
```
to
```
App/
```
4. Create folders:
```
App/WEB-INF/classes
```
and
```
App/WEB-INF/lib
```
5. Copy all compiled classes to 
```
App/WEB-INF/classes
```
6. Go to
```
App/WEB-INF
```
7. Run following command:
```
java -jar SDK_PATH/lib/dx.jar --dex --output="lib/classes.dex" *
```
8. Compress created file in
```
App/WEB-INF/lib/classes.dex
```
to 
```
App/WEB-INF/lib/classes.zip
```
9. Copy whole folder to device:
```
/storage/sdcard0/jetty/webapps
```
10. Run i-jetty.
11. Server should be running properly.

## Running the tests
Tests can be run directly from IDE or using Maven:
```
mvn test
```
JUnit and Selenium tests are created, Selenium tests can be disabled by configuration in class TestUtil.
