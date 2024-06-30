# ProgrammingProject-23-24

**Name:** Noah Thanei

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

**Create Schedule**: Use the create method in ScheduleCreator to generate a schedule,           specifying start and end dates, number of employees per day, and rest day preferences.

**View Schedule**: The generated schedule contains assigned shifts for each employee per day, ensuring proper coverage based on the specified criteria.

This system is designed to help organizations manage employee schedules efficiently, ensuring coverage while respecting individual employee preferences and limitations.

**Programming Techniques:** The topics covered during the lessons which where quite useful for me where : "Objects and Classes" and  "Abstract Data Types".

**Workload**: While the workload may not have been distributed perfectly evenly, I believe each member contributed according to their abilities and available time.

**Challenges**: Due to my limited programming experience, I encountered numerous obstacles. Fortunately, I was able to seek assistance from other project members, who were always willing to help.
