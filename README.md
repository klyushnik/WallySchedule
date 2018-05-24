# WallySchedule
An alternative app to view the schedule for Walmart employees.

This is an app to fetch and view the schedule from Walmart One. Not being developed anymore, but can be used as a template for a similar app.

Description of classes:

MainActivity - self explanatory. The app loads the HTML fetched from the schedule page, parses it into separate items, and loads it into the list.

NetUtil - the network operations. Here's how it works: 
1) we download the login page from walmartone.com and get the __EVENTVALIDATION and __VIEWSTATE, and save them to a temp variable.
2) we generate the header and paramMap using the saved credentials from the main app and the __EVENTVALIDATION + __VIEWSTATE from earlier. Then, the login page gets downloaded using this information (or, any restricted page would work). The purpose of this is to invoke the redirection script. If there is a redirection script (we check for the length of the response), then login was successful, if not - then the login page would be the one that was downloaded, meaning there is a credential problem.
3) the client, now having proper credentials, downloads the schedule page and validates it, then broadcasts it to the app to be received by the widget or main activity, if it is open.
4) if any step fails, the error broadcast is sent instead, forcing the app to rely on already saved data. This way older schedule can be viewed. Most common problem is a server timeout.

Util - misc utility methods. Includes methods: OpenSavedHtml, GetTodaysTimeCode, toSentenceCase, SaveDownloadedSchedule, SaveCredentials, GetLogin, GetSavedPassword, GetSavedTimestamp, GetDownloadLock, SetDownloadLock, GetUpdateFrequency
GetDownloadLock and SetDownloadLock are not used in the app, they are part of a legacy code, but I'd like to save them if I ever need them.

FirstLaunchActivity - the welcome screen. The purpose is to load the default values, save username and password, and download the schedule for the first time.

TaxCalc and TaxCalcConfig - the tax calculator similar to FinalPrice. The difference is that you can have unlimited number of different taxes.

ReportAbsenceFragment - allows you to report an absense from the app either by dialing the number or opening the online link to report an absence.

DeptList* - the department list, just a simple dialogfragment with all department numbers.

ScheduleWidget - the widget code. It's very similar to the main activity, but adapted for a widget.

The rest of the classes are pretty much UI - activities, adapters, fragments, and custom listviews.
