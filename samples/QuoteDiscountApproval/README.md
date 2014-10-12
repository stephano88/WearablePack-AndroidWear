# Quote Discount Approval Sample App

This sample Android Wear application pushes notifications for Quote Discount Approvals from Salesforce.com to an Android Wear device.

To learn more about the architecture and code visit: https://github.com/developerforce/WearablePack-AndroidWear

To get this sample application running you will need:
* An Android 4.3 or newer phone
* A Salesforce organization with Mobile Push enabled

    > Mobile Push will be generally available with the Summer '14 release.  If you are an existing Salesforce customer you can contact support to have Mobile Push enabled.

Now lets get started setting up everything needed to run this sample application.


## Part 1) Setup Google Cloud Messaging (GCM)

1. Follow the instructions at: http://developer.android.com/google/gcm/gs.html      
2. Take note of your *Project Number* and *API Key*


## Part 2) Setup the Android Wear Development Environment

1. Plug your Android device into your computer and enable debugging:
    1. Launch *Settings*
    2. Select *About Phone*
    3. Tap *Build Number* multiple times until the developer tools is enabled
    4. Back in *Settings* select *Developer options*
    5. Select the *USB debugging* checkbox
1. [Install Android Studio](https://developer.android.com/sdk/installing/studio.html)
1. From the Android Studio splash screen select *Open Project* and select the `build.gradle` file
1. Follow the *Set Up an Android Wear Emulator or Device* instructions from https://developer.android.com/training/wearables/apps/creating.html#SetupEmulator


## Part 3) Setup the Sample Project

1. Clone this git repo
2. From the `AndroidWearPreview` directory copy the `wearable-preview-support.jar` file to the `QuoteDiscountApproval/libs` directory
3. In the `QuoteDiscountApproval` directory create a new file named `gradle.properties` containing:

        forceUsername=YOUR_SALESFORCE_USERNAME
        forcePassword=YOUR_SALESFORCE_PASSWORD_AND_ACCESS_TOKEN
        androidPushNotificationClientId="YOUR_GCM_PROJECT_NUMBER"
        
    Note: Make sure you put the GCM Project Number in double quotes, but not the others.

4. In the `QuoteDiscountApproval` directory create a new file named `local.properties` containing:

        sdk.dir=YOUR_FULL_PATH_TO_THE_ANDROID_SDK

5. From the command line run the following in the `QuoteDiscountApproval` directory:

        For Mac & Linux:
        ./gradlew forceMetadataDeploy
        
        For Windows:
        gradlew forceMetadataDeploy
    
    This deploys the Salesforce code from `src/main/salesforce` into your organization

6. Setup OAUTH
    1. In Salesforce, select *Setup*
    2. Select *Create*
    3. Select *Apps*
    4. In *Connected Apps* select the *QuoteDiscountApproval* app
    5. Copy the *Consumer Key*
    6. Add a new line to the `gradle.properties` file containing:
    
            remoteAccessConsumerKey=YOUR_OAUTH_CONSUMER_KEY

7. Enable Mobile Push
    1. In the *QuoteDiscountApproval* screen select *Edit*
    2. Select the *Push Messaging Enabled* checkbox
    3. From the *Supported Push Platform* drop-down select *Android GCM*
    4. Enter your GCM API key in the *Server Key* field
    5. Select *Save*

8. Create a manager for your user (if one doesn't exist already)
    1. Select *Setup*
    2. Select *Manage Users*
    3. Select *Users*
    4. Select *New User*
    5. Fill in the required fields
    6. For *User License* select *Salesforce*
    7. For *Profile* select *System Administrator*
    8. Save the user
    9. Select *Setup*
    10. Select *Manage Users*
    11. Select *Users*
    12. Select *Edit* for your user
    13. In the *Approver Settings* section select the previously created user as the *Manager*
    14. Save your user


## Part 4) Setup Salesforce1

1. In the Play Store install the Salesforce1 app on your phone
2. Launch Salesforce 1 and login as the manager you created


## Part 5) Build and launch the Android app on your phone

1. Use the `adb` tool to list the connected devices like:

        ~/adt-bundle-mac-x86_64-20140321/sdk/platform-tools $ ./adb devices
        List of devices attached 
        emulator-5554   device
        070ca3ae00faad68        device

2. Specify the non-emulator device as the one to run this app on:

        On Mac & Linux:
        export ANDROID_SERIAL=070ca3ae00faad68
        
        On Windows:
        set ANDROID_SERIAL=070ca3ae00faad68

3. From the `QuoteDiscountApproval` directory install the APK on the phone:

        On Mac & Linux:
        ./gradlew installDebug
        
        On Windows:
        gradlew installDebug

4. On the phone launch the *Quote Discount Approval* app and login as the manager user

## Part 6) Test the notifications

1. In the *Quote Discount Approval* app on the phone tap the *Send Test Notification* button.  In the watch emulator you should now see a Quote Approval notification.
2. In Salesforce open the Developer Console (Located by selecting your name in the top-right)
3. Select *Debug* and then *Open Execute Anonymous Window*
4. Enter the following code and select *Execute*:

        QuotePushTest.createTestQuote();
        
    This should cause a notification to be pushed from Salesforce to the phone and then to the watch.


## Full Demo Flow

1. Sign into Salesforce.com as a user who's approvals will be sent to the user you signed into the phone app with
2. Create a new `Opportunity` in Salesforce
3. Create a new `Quote` on the `Opportunity`
4. Add a `Line Item` to the `Quote`
5. Select a `Product` and specify a quantity and a discount >= 20%
6. A notification should display on the watch emulator
7. Either Approve, Reject, or Open (on the phone) the `Quote`
