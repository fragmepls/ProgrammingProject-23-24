# ProgrammingProject-23-24

A project by: Isaiah Noah Neumair, Leonard Oberrauch, Noah Thanei, and Elias Vieider.

## Overview

More details on personal experience and tasks below.

The build procedure for our project was kept as simple as possible.  
Sadly no standalone runnable jar file can be built, due to how JavaFX dependencies are handled. Since Java 11 JavaFX is
not included in the JDK anymore, and has to be added as a dependency which complicates the use of a standalone jar.

The project can still be build and run through the command line using maven, with the following commands:

Firstly, clone the repository:

```shell
git clone https://github.com/fragmepls/ProgrammingProject-23-24.git
```

Then navigate to the project directory:

```shell
cd ProgrammingProject-23-24
```

And finally build and run the project:

```shell
mvn clean package exec:java
```

Further informations about the ScheduleCreator application, and how it helps users creating nice schedules you can find
in the Welcome Tab of our Graphical User Interface.

## Personal Experiences and Contributions

### Neumair Isaiah Noah

*Student ID:* 22537  
*GitHub identifier:* IsaiahNeumair

**Main area:** Export

**Interface to other components:**

- **The 'Export' Interface:**
    - The 'Export' Interface is the most important Interface of the export package as it allows to export either a '
      FixedShiftSchedule' as a CSV file or the list of some Employees as a JSON file.
    - As the FixedShiftSchedule and all its supporter Classes are also in the export package, they could be seen as
      equally important. The public methods and classes have all JavaDoc descriptions.

**Third-Party libraries used:**

- Jackson by fasterXML

**Programming techniques used:**

- I've used different techniques learned in the lectures wherever useful and if they were presented before I had
  finished the part of the project where a certain technique may have been useful.
- I have used different aspects of techniques presented in the lectures about I/O and JSON Serialization.
- I have tried to implement the best-suited data structures known to me at the time of programming.
- Furthermore, I tried to catch all exceptions and design the methods so they are easier to debug.

**Human experience out of my perception:**

- **Workload:**
    - I believe we have been able to split the workload quite equally. We made a list of all the to-dos and the members
      of the group decided according to their ability and interest on which ones they wanted to work.
- **Use of git:**
    - We used git as a version control system. Everyone of us had his own branch and whenever a part was finished he
      merged with the main channel and pushed the changes.
- **Challenge(s):**
    - *Programming challenges:*
        - I didn't face any major challenges while programming. It took me a few tries to export a 'nice looking' CSV of
          the schedule and I struggled a bit on how to structure my part of the project properly.
        - I had some difficulties designing the FixedShiftSchedule data structure due to not knowing how to generically
          program at the time.
        - Obviously, there were some minor challenges that needed some research but nothing overwhelming.
    - *Human challenges:*
        - There were some difficulties structuring the project and the workload properly and organizing/coordinating the
          project.
        - But nothing unexpected for being the first group project in university. I definitely have taken some learnings
          I wouldn't have gotten otherwise.

### Oberrauch Leonard

*Student ID:* 23262  
*GitHub identifier:* fragmepls

**Main area:** Database Utilities

**Utilities to other components:**

- `DBUtils`: Manages database connections and operations as well as initialization.
- `SQLQueries`: Provides methods for preset SQL queries related to employees.

**Third-party libraries used:**

- Only standard Java library

**Programming techniques used:**

- My primary goal with my implementation was to keep the code as clean and readable as possible.
- I also tried to keep the code as modular as possible, so that it can be easily extended or modified in the future.
- I also tried to keep the code as lightweight as possible.

**Human experience out of personal perception:**

**Challenge(s):**

- *Programming challenges:*
    - As I have already worked with databases, and Java applications in general in the past, I did not have any problems
      with the implementation.

### Noah Thanei

**Student ID:** 23213  
**GitHub ID:** NoahThanei

**Main area:** Methods

With the help of the other project-members I worked on the two central methods of the project which are part of the "
EmployeeComparator" and the "ScheduleCreator" classes.

**EmployeeComparator**

The EmployeeComparator class is responsible for assigning shifts to employees based on their availability and working
hours. Its key functionalities include:

- **Shift Assignment**: Allocates full-day, morning, afternoon, or overtime shifts to employees based on availability
  and working hours.
- **Vacation Management**: Automatically removes past vacations from employee records.
- **Working Hours Management**: Sets standard working hours for full-time and part-time employees.
- **Overtime Management**: Distributes overtime shifts fairly among employees with the least overtime hours.
- **Availability Check**: Determines if an employee is available on a given date, considering vacations, rest days, and
  weekend work preferences.

**ScheduleCreator**

The ScheduleCreator class is used to generate and manage shift schedules for a specified period. Its main
functionalities include:

- **Employee Management**: Allows adding and removing employees from the scheduling pool.
- **Schedule Generation**: Creates shift schedules for a specified date range, considering the number of employees
  needed, weekend work, and designated rest days.
- **Date List Generation**: Generates a list of dates for the scheduling period, excluding rest days and weekends if
  applicable.
- **Overtime Reporting**: Prints the overtime hours for each employee at the end of the scheduling process.

**Usage**

- **Add Employees**: Use the addEmployee method in ScheduleCreator to add employees to the scheduling pool.
- **Create Schedule**: Use the create method in ScheduleCreator to generate a schedule, specifying start and end dates,
  number of employees per day, and rest day preferences.
- **View Schedule**: The generated schedule contains assigned shifts for each employee per day, ensuring proper coverage
  based on the specified criteria.

This system is designed to help organizations manage employee schedules efficiently, ensuring coverage while respecting
individual employee preferences and limitations.

**Programming Techniques:** The topics covered during the lessons which were quite useful for me where: "Objects and
Classes" and "Abstract Data Types".

**Workload**: While the workload may not have been distributed perfectly evenly, I believe each member contributed
according to their abilities and available time.

**Challenges**: Due to my limited programming experience, I encountered numerous obstacles. Fortunately, I was able to
seek assistance from other project members, who were always willing to help.

### Elias Vieider

*Student ID:* 22560  
*GitHub identifier:* vieiderElias

**Main area:** The two util classes (Employee and Vacation), GUI, Methods

**Utilities to other components:**

- `Employee`: Is the creatable employee, one of the fundamental components of the application.
- `Vacation`: Makes possible to create Vacations, which sends Employees on Vacation.
- `Gui2`: Brings all the methods from the different classes together and creates a simple, but functional graphical user
  Interface.

**Third-Party libraries used:**

- JavaFX for GUI
-

**Programming techniques used:**

- I've used different techniques learned in the lectures, where they were useful. But we tried to keep the code -
  especially in the methods defined in the classes of the planner package - as simple as possible and tried to work a
  lot with simpler techniques such as for loops, since for some of our group (including me) it's the first bigger
  programming-project.
- In the methods defined in the planner package, we used different abstract data types. In addition to that, I used Unit
  Tests for debugging and testing purposes. Furthermore, I tried to throw exceptions where it makes sense.

**Human experience out of my perception:**

**Workload:**

- I believe we have split the tasks quite equally and adequately to the strengths and interests of each group member. We
  also helped each other and worked together where it was advantageous, which helped us understand some concepts and
  parts of the project better, and increased the productivity in the creation process of the project.
- For the tasks each member programmed individually, we used git and the branch system to bring everything together.

**Challenges:**

- *Programming challenges:*
    - I had some issues at the beginning to bring all the different classes and methods together such that they work
      correctly. My biggest challenge was however to make the main methods work as good as possible. Even though the
      algorithm assigning the shifts afforded a lot of testing, making little changes and debugging to bring it to the
      final state.
    - As the project proceeded I learned a lot and things became clearer and easier to understand.
- *Human challenges:*
    - It was for me and for some other group members the first major programming project - at the beginning some basic
      concepts such as the usage of git were challenging but I got used to many things fastly and I'm happy with our end
      result.
    - It was also my first time creating a GUI and it was a great experience.
- All together I can say that the effort it took me and my group to create this project was absolute worth it and the
  project taught me a lot of things and made me understand the usage of basic and higher-level programming and java
  concepts a lot better.
- We tried to make the project easy expansible and there is a lot that could and can be added in the future!
