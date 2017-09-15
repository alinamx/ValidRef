# ValidRef
ValidRef is a validator tool for scientific references. It can check references in the styles APA, Chicago, Vancouver, MLA and Harvard.
It is developed as web service application and can be called via the API.
There is also an MS Word Add-in, that uses the service of ValidRef.

## Installation
The following gives a quick overview, how to install or run the applications.

### MS Word Web-App
To run the Word Add-In follow the instructions on the website of microsoft, the Add-In itself must be published in a webserver (localhost can be used). Then follow these steps, to add it to Microsoft Word.
https://dev.office.com/docs/add-ins/testing/create-a-network-shared-folder-catalog-for-task-pane-and-content-add-ins
Another option is to import the project in Visual Studio and run it there.

### ValidRef
Run the .jar file by just double clicking on it or start it from the command line tool. Make sure it runs on port 4567 if you want to use the MS Word Web-App.

## API
Depending on the server, call the application via the POST call http://server:port/checkAll.
In the body a JSON file needs to be send, with the following input:
{"references":"references as text separated by \n","style":"citation style as written in the description"}

{"references":"Carmichael, F., Mchale, I., & Thomas, D. (2011). Maintaining market position: Team performance, revenue and wage expenditure in the English Premier League.\nCarruth, A., Dickerson, A., & Henley, A. (2000). What do we know about Investment under Uncertainty?\nChang, C.-W. (2010). Transnational production of Taiwanese integrated circuit industry in China.","style":"Chicago"}

## Developers guide
In the following some important hints are listed for developers of frontend applications using the API of ValidRef.
* Make sure, the styles, that are sent on the POST requests are written in the same way as above, just upper and lowercase does not matter.
* The output is also a JSON file.

## User guide
If you are using the MS Word Add in, make sure, that ValidRef is running on localhost/4567 to reach it.
