# amiok-android-app

### Main Idea
I want to create an app (deskop browser and mobile) where people can, with a few clicks, setup an automated help searching mechanism. That is, that person can create an „entry“, consisting of a name, a timeintervall, a password, optionally an email with a message. That name can then be searched and found in the app for other people, showing them that the entry-creator is„alright“. As soon as the entry-creator doesn't refresh or delete the entry within the given timeintervall by entering it's password again, it's status will be marked „not alright“. Then also other linked communication channels will trigger (f.e. The message will be sent to given email). People can „track“ other entries to keep them at hand. This can be the persons own entry for fast resfreshing it or other persons entries to keep on being informed about their status.

### Audience
The target audience are people who find themselves in risky situations, where in worst-case, they might not be able to communicate to the outside world anymore. Where the knowledge, that someone is „not alright“ or a little piece of information can safe that person.
This might be travellers, sleeping at strangers places and leaving a safety message of that strangers address in the app, to be released if  the password doesn't get retyped next morning. Sick or old people, who are living alone, could use it as a precaution against different kinds of health-threats. Parents could ask their kids to communicate their well-being by maintaining an app entry, when going out or in other risky situations.

### Development decisions so far
I was considering a software i could use for app-cross-platform development (PhoneGap). It would be nice to have said feature, but i found lots of bad expereinces with cross-platform development, which is why i feel like i should concentrate on one platform. So my current plan is to make the mobile app for just android natively with android studio (Java).
I also plan to not make a login, beacuse for me, it seems to be an overkill for a user. The small downside to this is, that ones own entries need also to be in the tracked tab (to have them at hand), together with the foreign/observed entries and are tracked locally. So resetting the phone will reset the users tracks. But it is almost the same effort to login as it is to search and add your few entries again (newly created entries get added automatically).
I plan to use php/mysql as my serverside technology, simply because i have a paid running VPS already that i need for something else, but has enough spare capacities. However, i would like to learn some node.js, so if i find a good/cheap server solution to do it that way, i might rethink my backend plans.

### Tools
Android Studio, Java, AsyncTask class for server communication

Server for desktopbrowser and database with DigitalOcean Virtual Private Server: Apache server, Php and mysql to handle database requests, Cronjobs for checking timeintervalls and triggering communication channels

Gimp for custom designs

### Procedure
- Create clickdummy app.
- 3 Menus: New Entry (Form). Search (Searchbar, Table), Tracked (Table)
- 1 Entry editpage/popup: track/untrack, delete, refresh, password input
- Setup server and database. Create php database api script (insert of a new entry)
- Implement server communication and test inserting of a new entry.
- Add the remaining operations, properly display and test them: deleteEntry, refreshEntry (in case of reentering password),	getAllEntries/getEntryByName (for the table)
- Add local storage logic for tracking entries and display them.
- Write script to check and handle elapsed timeintervals and setup a cronjob for it on server

### Optional Additions
- Make a desktopbrowser version
- Add more communication channels if a tracked entrys status was changed
- Push-notification
- Social media shares
