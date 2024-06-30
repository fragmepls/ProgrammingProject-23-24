# ProgrammingProject-23-24


**Neumair Isaiah Noah**  
<i>Student ID:</i> 22537<br>
gitHub identifier: IsaiahNeumair<br>
<br>
<b>Main area:</b> export<br>
<br>
<b>Interface to other components:</b><br>
<li><b>The 'Export' Interface:</b><br>
The 'Export' Interface is the most important Interface of the export package as it allows to export either a 'FixedShiftSchedule' as a CSV file or the list of some Employees as a JSON file.<br>
As the FixedShiftSchedule and all its supporter Classes are also in the export package, they could be seen as equally important. The public methods and classes have all JavaDoc descriptions.<br>
<br>
<b>Third-Party libraries used:</b><br>
<li>Jackson by fasterXML<br>
<br>
<b>Programming techniques used:</b><br>
I've used different techniques leanrned in the lectures wherever useful and if they were presented bevore I had finished the part of the project where a certain technique may have been useful.<br>
I have used different aspects of techniques presented in the lectures about I/O and JSON Serialization.<br>
I have tried to implement the best suiting data structured known to me at the time of programming.<br>
Furthermore I tried to cath all exceptions and tried to design the methods so they are easier to debug.<br>
<br>
<b>Human experience out of my perception</b><br>
<br>
<b>Workload:</b><br>
I believe we have been able to split the workload quite equally. We made a list of all the to-do's and the members of the group decided according to their ability and interest on which one thy wanted to work.<br>
<br>
<b>Use of git:</b><br>
We used git as a version control system. Everyone of us had his own branch and whenever a part was finished he merged with the main channel and pushed the changes.<br>
<br>
<b>Challenge(s):</b><br>
<i>Programing challenges:</i><br>
I didn't face an major challenges while programming. It took me a few tries to export a 'nice looking' CSV of the schedule and I struggeled a bit on how to structure my part of the project properly.<br>
I had also some difficulties designing the FixedShiftSchedule data structure due to not knowing how to generically program at the time.<br>
Obviously there were some minor challenges that needed some resarch but nothing overwhelming.<br>
<br>
<i>Human challenges:</i><br>
There were some difficulties structuring the project and the workload poperly and organising/coordinating the project<br>
But nothing unexpected for being the first group project in university. I definitely have taken some learnings I wouldn't have gotten otherwise.
<br>
<br>

**Oberrauch Leonard**  
*Student ID:* 23262  
gitHub identifier: fragmepls

**Main area:** Database Utilities

**Utilities to other components:**

- `DBUtils`: Manages database connections and operations as well as initialization.
- `SQLQueries`: Provides methods for preset SQL queries related to employees.

**Third-party libraries used:**

- only standard Java library

**Programming techniques used:**  
My primary goal with my implementation was to keep the code as clean and readable as possible.  
I also tried to keep the code as modular as possible, so that it can be easily extended or modified in the future.  
I also tried to keep the code as light-weight as possible.

**Human experience out of personal perception:**

**Challenge(s):**  
*Programming challenges:*  
As I have already worked with databases, and Java applications in general in the past, I did not have any problems
with the implementation.
<br>
<br>

**Noah Thanei**

**Student ID:** 23213

**GitHub ID:** NoahThanei

Main area: Methods

With the help of the other project-members I worked on the two central methods of the project which are part of the "EmployeeComparator" and the "ScheduleCreator" classes.

Disclaimer: The keywords mentioned below are not the names of the classes' methods they are only meant to give a better overview of the main tasks.

**EmployeeComparator**

The EmployeeComparator class is responsible for assigning shifts to employees based on their availability and working hours. Its key functionalities include:

-   **Shift Assignment**: Allocates full-day, morning, afternoon, or overtime shifts to employees based on availability and working hours.
-   **Vacation Management**: Automatically removes past vacations from employee records.
-   **Working Hours Management**: Sets standard working hours for full-time and part-time employees.
-   **Overtime Management**: Distributes overtime shifts fairly among employees with the least overtime hours.
-   **Availability Check**: Determines if an employee is available on a given date, considering vacations, rest days, and weekend work preferences.

**ScheduleCreator**

The ScheduleCreator class is used to generate and manage shift schedules for a specified period. Its main functionalities include:

-   **Employee Management**: Allows adding and removing employees from the scheduling pool.
-   **Schedule Generation**: Creates shift schedules for a specified date range, considering the number of employees needed, weekend work, and designated rest days.
-   **Date List Generation**: Generates a list of dates for the scheduling period, excluding rest days and weekends if applicable.
-   **Overtime Reporting**: Prints the overtime hours for each employee at the end of the scheduling process.

**Usage**

**Add Employees**: Use the addEmployee method in ScheduleCreator to add employees to the scheduling pool.

**Create Schedule**: Use the create method in ScheduleCreator to generate a schedule,specifying start and end dates, number of employees per day, and rest day preferences.

**View Schedule**: The generated schedule contains assigned shifts for each employee per day, ensuring proper coverage based on the specified criteria.

This system is designed to help organizations manage employee schedules efficiently, ensuring coverage while respecting individual employee preferences and limitations.



**Programming Techniques:** The topics covered during the lessons which where quite useful for me where : "Objects and Classes" and  "Abstract Data Types".

**Workload**: While the workload may not have been distributed perfectly evenly, I believe each member contributed according to their abilities and available time.

**Challenges**: Due to my limited programming experience, I encountered numerous obstacles. Fortunately, I was able to seek assistance from other project members, who were always willing to help.
<br>
<br>

**Elias Vieider** <br>
<i>Student ID:</i> 22560<br>
gitHub identifier: vieiderElias<br>
<br>
<b>Main area:</b> The two util classes (Employee and Vacation), GUI, Methods<br>
<br>
**Utilities to other components:**

- `Employee`: Is the creatable employee, one of the fundamental components of the application. 
- `Vacation`: Makes possible to create Vacations, which sends Employees on Vacation.
- `Gui2`: Brings all the methods from the different classes together and creates a simple, but functional graphical user Interface.

<br>The different Unit-Test, used to debug and test the different Methods - especially the one we created in the planner package.</b>
<br>
<br>
<b>Third-Party libraries used:</b><br>

<li>JavaFX<br>
<br>
<b>Programming techniques used:</b><br>
I've used different techniques learned in the lectures, where they where useful. But we tried to keep the code - especially in the methods defined in the classes of the planner package - as simple as possible and tried to work a lot with simpler technics such as for loops, since for some of our group (including me) it's the first bigger programming-project.<br>
In the methods defined in the planner package we used different abstract data types. In addition to that I used Unit Tests for debugging and testing purpose. Furthermore, I tried to through exceptions where it makes sense.<br>
<br>
<b>Human experience out of my perception</b><br>
<br>
<b>Workload:</b><br>
I believe we have splitted the tasks quite equally and adequate to the strengths and interests of each group member. We also helped each other and worked together where it was from advantage, what helped us understanding some concepts and parts of the project better, and increased the productivity in the creation process of the project.<br>
For the tasks each member programmed individually, we used the git and the branch system to bring everything together.<br>
<br>

<b>Challenge(s):</b><br>
<i>Programing challenges:</i><br>
I had some issues at the begin to bring all the different classes and methods together such that they work correctly. My biggest challange was however to make the main methods work as good as possible. Even though the algorithm assigning the shifts afforded a lot of testing, making little changes and debugging to bring it to the final state. <br>
As the project proceeded I learned a lot and things became clearer and easier to understand.<br>
<br>
<i>Human challenges:</i><br>
It was for me and for some other group members the first major programming project - at the begin some basic concepts such as the usage of git where challenging but I got used to many things fastly and I'm happy with our end result. <br>
It was also my first time creating a GUI and it was a great experience. <br>
All together I can say that the effort it took me and my group to create this project was absolute worth it and the project teached me a lot of things and made me understand the usage of basic and higher levl programming and java concepts a lot better. <br>
We tried to make the project easy expansible and there is a lot that could and can be added in the future!