# CS 360 Reflection

> *Briefly summarize the requirements and goals of the app you developed. What user needs was this app designed to address?*

&ensp; The requirements and goals for the app involved many features to meet the users needs. This app required the implementation and usage of three databases - one for storing usernames and passwords, one for storing logged weights, and one for storing options. To begin, the user should be able to create a username and password. Then they can login with their created username and password. The Weight Tracker app required a grid for displaying the user's logged weights. This grid was to be interactive and allow the user to change the logged weight by clicking on it in the grid. Once the last logged weight meets the condition for meeting their weight goal, it should display a message to the user.

> *What screens and features were necessary to support user needs and produce a user-centered UI for the app? How did your UI designs keep users in mind? Why were your designs successful?*

&ensp; This app required the implementation of four screens - one for logging in, one for displaying the grid, one for logging weights, and one for changing options. The elements on each screen flowed logically. This resulted in an intuitive layout for each screen. Button elements were logical and hinted to the user that they were interactive. Text elements were also concise and descriptive, and they accurately labeled each section of the screen. This led to easy, intuitive usage of the app for the user. The options screen was particularly useful for enabling the app to cater towards multiple users - ones that want to lose weight and ones that want to gain weight.

> *How did you approach the process of coding your app? What techniques or strategies did you use? How could those be applied in the future?*

&ensp; I approached the development of this application incrementally. First, an initial implementation of each screen was created. Then, each feature was implemented, one-by-one, in order of importance. As the features were implemented, I changed the UI to fit the implementations of the features. I tested each feature as I implemented it, and then I did acceptance testing after the initial build was finished. If any bugs were discovered in acceptance testing, I fixed them.

> *How did you test to ensure your code was functional? Why is this process important and what did it reveal?*

&ensp; I mainly tested this application through using the app in the Android Emulator after implementing a feature. I also utilized the inspection tools that Android Studio provides to make sure the databases were updating correctly. I made sure that each function produced the correct result before moving onto the next.

> *Considering the full app design and development process, from initial planning to finalization, where did you have to innovate to overcome a challenge?*

&ensp; I would say that most of the innovation came during the design process. This included the creating of wireframe views for each screen. This gave me an idea of how I should approach the development of each feature.

> *In what specific component from your mobile app were you particularly successful in demonstrating your knowledge, skills, and experience?*

&ensp; I would say that the most successful component in my Weight Tracker application is the implementation of the Monthly Grid view in WeightTrackerActivity. This was a custom implementation for a calendar view with clickable buttons for each day that could be logged. It had an intuitive layout that correctly displayed each logged weight for the current month. The user could even use arrows to change the month, and the grid would update and display the logged weights for each month.
