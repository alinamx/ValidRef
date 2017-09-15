# ValidRef
ValidRef is a validator tool for scientific references. It can check references in the styles APA, Chicago, Vancouver, MLA and Harvard.
It is developed as web service application and can be called via the API.
There is also an MS Word Add-in, that uses the service of ValidRef.

## Installation


### MS Word Web-App


### ValidRef
Run the .jar file by just double clicking on it or start it from the command line tool. Make sure it runs on port 4567 if you want to use the MS Word Web-App.

## API
Depending on the server, call the application via the POST call http://server:port/checkAll.
In the body a JSON file needs to be send, with the following input:
{"references":"references as text separated by \n","style":"citation style as written in the description"}

{"references":"Carmichael, F., Mchale, I., & Thomas, D. (2011). Maintaining market position: Team performance, revenue and wage expenditure in the English Premier League.\nCarruth, A., Dickerson, A., & Henley, A. (2000). What do we know about Investment under Uncertainty?\nChang, C.-W. (2010). Transnational production of Taiwanese integrated circuit industry in China.","style":"Chicago"}

## Developers guide
In the following some important hints are listed for developers of frontend applications using the API of ValidRef.
* 
*

## User guide
If you are using the MS Word Add in, make sure, that ValidRef is running on localhost/4567 to reach it.
