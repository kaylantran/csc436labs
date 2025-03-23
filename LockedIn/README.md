1. This project attempts to aid in the implementation pomodoro study/work 
technique, which entails working in intervals with short breaks in between. 
For example, you'll study for 25 minutes and then take a 5 minute break. 
There is a settings, which allows you to adjust your work and break times. 
Additionally, I had planned to implement a history/analytics pages, but did
not get to it.
2. https://www.figma.com/design/GzraqRmRb6Wl5ioIT1e6yo/Locked-In-Wireframes?node-id=0-1&t=BN6Vx1vGPzN4UC6L-1
3. *Foreground service, LiveData, ViewModel, SharedPreferences, Notification Channels,
ImageButton. No 3rd party libraries used. Had to figure out view model. 
4. Minimum SDK: API 24, Target SDK: API 34, Permissions Required: POST_NOTIFICATIONS,
FOREGROUND_SERVICE -> Users must be on Android 7 or higher to run the app. They
must allow foreground service to run the app properly, but they do not need to
allow post notifications. 

*While the project was meant to utilize Jetpack Compose, due to time constraints
and existing View-based implementation, I decided to focus on delivering 
a functional and stable app. 

I understand that state in Compose should be managed using remember, mutableStateOf,
and ViewModel state hoisting. My existing ViewModel uses LiveData for timer values,
which would be replaced by mutableStateOf in Compose. Navigation between screens would
be handled using NavHost() and NavController instead of FragmentContainerView, and
I would use composables like Scaffold, Bottom Navigation, and IconButton for UI 
structure. Finally, I used SharedPreferences for persistence, but modern Compose-based
apps use Jetpack DataStore. 