### DHIS2 Android Skeleton App
---
The DHIS2 Android Skeleton App exist to provide a smooth first contact
with the DHIS2 Android Sdk to the developers.

# The DHIS2 Android Skeleton App 
## A Starting point

![DHIS2 Skeleton App logo][skeletonLogo] 

The *Skeleton App* serves as an entry point for developers who want to
build their own DHIS2 android app. 

This application includes the **DHIS2 Android Sdk** and the **DHIS2 Rule
Engine** dependencies and allows developers to log in to DHIS2 servers
and download DHIS2 data and metadata. It's composed for a splash, a
login activity and a main activity. It also provide a menu to log out
and delete data.

[DHIS2 Android Sdk repository](https://github.com/dhis2/dhis2-android-sdk)<br>
[DHIS2 Rule Engine repository](https://github.com/dhis2/dhis2-rule-engine)

## How the app looks
![Skeleton app feel and look][skeletonAppScreenshots]

This app allows to: 

* Login/Logout
* Download metadata
* Download data
* Wipe data

# Use cases

In this repository it is also possible to find a branch named
`use-cases`. This branch contains an application with DHIS2 Android Sdk
use cases.

## How the use cases app looks
![Use cases feel and look][useCasesScreenshots]

This app allows to:

* Login/Logout
* Download metadata
* Download data
* Upload data
* Wipe data
* Download file resources
* Upload file resources
* Create tracked entity instances
* Search tracked entity instances
* Create events without registration
* Create data values
* List programs
* List data sets
* List data set instances 
* List foreign key violations
* List D2Errors
* Show granular sync states
* Execute code snippets

[skeletonLogo]: https://github.com/dhis2/dhis2-android-skeleton-app/blob/master/assets/logo-launcher.png?raw=true "Skeleton logo screenshot"
[skeletonAppScreenshots]: https://github.com/dhis2/dhis2-android-skeleton-app/blob/master/assets/skeleton-app-screenshots.jpg?raw=true "Skeleton app screenshots"
[useCasesScreenshots]: https://github.com/dhis2/dhis2-android-skeleton-app/blob/master/assets/use-cases-skeleton-app-screenshots.jpg?raw=true "Use cases skeleton app screenshots"