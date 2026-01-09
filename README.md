# Semester-Project
My 3rd Semester project:
# Bug Tracker System Using Java OOP, Data Structures & SQL Database

## Project Overview
This project is a Java-based Bug Tracker System developed as a Data Structures and Algorithms (DSA) primary project.  
The system demonstrates practical usage of Object-Oriented Programming (OOP), advanced data structures, and SQL database integration using JDBC.

Unlike traditional CRUD applications, this system uses data structures as the main processing layer, while the database is used only for permanent data storage.

---

## Project Objectives
- Apply Data Structures and Algorithms to a real-world problem
- Demonstrate strong Java OOP concepts
- Use Trees and Graphs for efficient bug management
- Implement role-based access control
- Integrate SQL database using JDBC
- Develop a professional Java Swing GUI

---

## Object-Oriented Programming Concepts Used

Inheritance:
- User is the parent class
- Admin, Manager, TechnicalPerson, and Customer extend User

Encapsulation:
- All class variables are private
- Access provided through getters and setters

Polymorphism:
- Different user roles perform role-specific actions

Abstraction:
- Business logic separated using service and repository layers

---

## Data Structures Used (Primary Focus)

HashMap:
- Used for user authentication and role-based access
- Provides constant time lookup

ArrayList:
- Used to store product data
- Allows sequential access

Binary Search Tree (BST):
- Used to store and search bugs by Bug ID
- Provides faster searching than linear lists

Directed Graph:
- Used to control bug lifecycle transitions
- Ensures only valid status changes occur

Queue (FIFO):
- Used for bug assignment
- Ensures first reported bug is assigned first

Priority Queue (Heap):
- Used for severity-based bug handling
- Critical bugs are handled before low severity bugs

Stack (LIFO):
- Used to maintain bug status history
- Supports undo and rollback operations

LinkedList:
- Used to store bug comments
- Allows dynamic insertion

TreeMap:
- Used to generate date-wise sorted bug reports

---

## Tree Implementation
Binary Search Tree is used to efficiently search and manage bugs based on Bug ID.  
Average time complexity for searching is O(log n).

---

## Graph Implementation
Bug lifecycle is represented as a directed graph:

REPORTED → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED

Graph traversal ensures invalid status transitions are prevented.

---

## Algorithms Applied
- Binary Search Tree insertion and search
- Graph traversal (DFS / BFS)
- FIFO scheduling using Queue
- Heap ordering using Priority Queue
- LIFO undo operation using Stack

---

## User Roles and Features

Admin:
- Add products
- Add managers and technical persons
- View system reports

Manager:
- View products
- View reported bugs
- Assign bugs
- Track bug progress

Technical Person:
- View assigned bugs
- Update bug status
- Submit solutions

Customer:
- Register and login
- Report new bugs
- View bug status and solutions

---

## Technologies Used
- Java
- Java Swing
- Data Structures and Algorithms
- MySQL / SQL Server
- JDBC
- MVC Architecture

---

## Database Design
The system uses an SQL database with the following tables:
- users
- products
- bugs
- comments
- bug_history

Database features:
- Auto-increment primary keys
- Foreign key relationships
- Prepared statements for security

---

## Project Architecture

GUI (Java Swing)
↓
Service Layer (Business Logic)
↓
Data Structures Layer (Primary)
↓
Repository Layer (JDBC)
↓
SQL Database

---

## How to Run the Project
1. Clone the repository
2. Open the project in IntelliJ IDEA or NetBeans
3. Configure JDK 8 or above
4. Import the SQL schema into MySQL
5. Update database credentials
6. Run Main.java

---

## Academic Value
- Designed as a Data Structures course project
- Includes Trees, Graphs, Queues, Stacks, and Heaps
- Suitable for viva and final submission
- Demonstrates real-world application of data structures

---

## Author
Junaid Khan  
BS Software Engineering  

---

## License
This project is developed for educational purposes only.
