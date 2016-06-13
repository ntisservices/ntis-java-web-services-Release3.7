NTIS Subscriber Service example - README
========================================
	
This is an example implementation of an NTIS Subscriber Service that uses the Spring web-services framework. 
You can import the project into Eclipse and tailor it to suit your needs.

The NTIS Subscriber Service enables registered Subscribers to receive DATEXII traffic data publications from the Highways Agency NTIS system.
Refer to the general information file NTISSubscriberService-Information.txt (located in this repository) for further information.

Prerequisites	
------------------

1. Software to install :

 JDK v1.6

 Gradle v1.11

 Eclipse J2EE IDE v3.6.2
 
 SoapUI v5.0.0 (for testing)
 
 *Note: Versions listed above are those used to test the install/build process, earlier/later compatible versions should also work.*

2. Set the system environment variables:

 JAVA_HOME=\<JDK installation directory\>
	
 GRADLE_HOME=\<Gradle installation directory\>
	
 PATH=${PATH}:${JAVA_HOME}:${GRADLE_HOME}

*Note: all other required Java libraries are downloaded and installed during the gradle-based build process.*

Importing Project in to Eclipse
---------------------------------

1. Download the SubscriberService example application from https://github.com/ntisservices/ntis-java-web-services-Release2.5/archive/master.zip

2. Extract the zip file into the target location on your local file system.

3. Open Eclipse and Import the project into the Eclipse workspace.

 a. File->Import

 b. Select the option General->Existing Project into Workspace

 c. Select the option 'Select root directory' and locate the downloaded project root directory: <target dir>/SubscriberService

 d. Click Finish to import the project.

Building the Project
--------------------

From the command line, execute the following (from the project root directory {workspace_loc}/SubscriberService): 'gradle eC clean build'

*Note: the build procedure is managed by Gradle - refer to the build.gradle file, supplied with the project.*

Running on the Jetty Application Server
---------------------------------------

To start the application, execute the following from the command line (from {workspace_loc}/SubscriberService): 'gradle jettyRunWar'

*Note: this command will automatically (re)build the application before deploying and running on the Jetty application server.*

The WSDL can be accessed from http://localhost:8880/SubscriberService/services/push.wsdl (this URL may vary, depending on your server/project configuration)

The application logs information and errors to the local console and also to the log file SubscriberService.log.

To terminate the application, enter Ctrl-C into the console in which the start command, above, was executed.  If the application was run in the background, or the orignal console window is otherwise unavailable, use the command 'gradle jettyStop' to terminate the application.

Deploy on Other Servers
-----------------------

Simply copy the SubscriberService.war file from the build/libs folder to a target application server deployment folder.

Once deployed, the WSDL can be invoked from http://localhost:8880/SubscriberService/services/subscriber.wsdl (this URL may vary, depending on your server/project configuration)

Testing the Application with SoapUI
-----------------------------------

1. First deploy application and run on the Jetty application server (see above).

2. Open the SoapUI Application and click on menu File -> New SOAP Project. This should bring up a New SOAP Project dialog box.

3. Enter a Project Name, for example 'NTIS Subscriber Service'.

4. In the 'Initial WSDL/WADL' field copy and paste the path 'http://localhost:8880/SubscriberService/services/push.wsdl' (this URL may vary, depending on your server/project configuration)

5. Click the 'Ok' button; a SoapUI project will be created and you should be able to see the putDatex2Data operation.
  
6. Under putDatex2Data, double click on the Request1 element. This will open up a request dialog window in SoapUI.

7. Open one of the SOAP example request files provided with the project in Eclipse - or any another text editor.  The example request files are located in {workspace_loc}/SubscriberService/src/test/resources/exampleRequests.  These files contain example publications of all the different DATEXII Feed Types published by the NTIS system.

8. Copy/paste the full content of the example request file into the SoapUI request window (wholly replacing any XML already in the request window).

9. Click on the green arrow icon button in the SoapUI request window to send this request to the specified endpoint.  The application will output success or failure logs to the console and to the SubscriberService.log file.  Additionally, SoapUI also contains various monitoring windows (such as a HTTP log) that can be utilised to test the connection and service request.
