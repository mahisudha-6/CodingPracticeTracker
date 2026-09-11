# Coding Practice Tracker

An intermediate-level Java Swing desktop application to track and analyze personal progress across various competitive programming and practice platforms (e.g., LeetCode, HackerRank, CodeStudio). 

This project uses a clean **Model-DAO-Service-UI** layered architecture, implements proper relational database design, utilizes JDBC with `PreparedStatement` to prevent SQL Injection, and enforces robust input validation and exception handling.

---

## 🌟 Key Features

* **Secure Authentication**: User registration and login, password hashing (using SHA-256), and change password capabilities.
* **Practice Directory**: Complete CRUD operations to add, view, update, or delete practice problems.
* **Smart Filter & Search**: Instant filtering by problem difficulty (Easy, Medium, Hard), custom topics (e.g., Dynamic Programming, Arrays), or searching by title keyword.
* **Dashboard Summary Cards**: Quick-glance indicators of Total, Solved, Pending, and Revision problems, alongside a table showing recent submittals.
* **Performance Reports**: 
  * Problems solved in the current calendar month.
  * Quantitative breakdown by topic.
  * Practice allocation breakdown by difficulty.
  * Dedicated Revision watchlist (marked as *Revising*).
* **Dynamic Topic Management**: Add new practice categories directly from the problem input form.

---

## 📂 Project Structure

The project has been structured according to strict separation of concerns:

```
CodingPracticeTracker/
│
├── src/
│   ├── main/          # Application launcher
│   ├── model/         # Simple POJO models representing entities
│   ├── dao/           # Data Access Objects executing SQL queries
│   ├── service/       # Business logic validations
│   ├── util/          # Configuration loader, validations, hashing
│   ├── ui/            # Swing Panels and Window layouts
│   └── exception/     # Customized application exceptions
│
├── database/
│     database.sql     # Database initialization script
│
├── config.properties  # Database credential settings
└── README.md          # Project instructions
```

---

## 🛠️ Setup & Execution

### 1. Database Setup
1. Open your MySQL Command Line Client or GUI (e.g., MySQL Workbench).
2. Execute the initialization script located in `database/database.sql`. This will:
   * Create the `coding_tracker_db` schema.
   * Create the `users`, `topics`, and `coding_problems` tables.
   * Populate initial topics and sample records.
3. Verify your database connection credentials in `config.properties` in the project root:
   ```properties
   db.url=jdbc:mysql://localhost:3306/coding_tracker_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.username=YOUR_MYSQL_USERNAME
   db.password=YOUR_MYSQL_PASSWORD
   ```

### 2. Add the MySQL JDBC Connector
Since this is a core Java project without external dependency managers:
1. Download the **MySQL Connector/J JAR** (e.g., version `mysql-connector-j-9.x.x.jar`) from the [Official MySQL Download Page](https://dev.mysql.com/downloads/connector/j/) or Maven Central.
2. Create a folder named `lib/` in the project root and place the JAR file inside it:
   `CodingPracticeTracker/lib/mysql-connector-j-9.x.x.jar`

### 3. Compile & Run via CLI (Windows CMD/PowerShell)

**To Compile:**
Navigate to the root directory `CodingPracticeTracker/` and run the following command (replace `9.0.0` with your exact connector version if different):
```cmd
javac -cp "lib/mysql-connector-j-9.0.0.jar;src" -d bin src/model/*.java src/exception/*.java src/util/*.java src/dao/*.java src/service/*.java src/ui/*.java src/main/*.java
```

**To Run:**
Run the application by referencing the compiled classpath and launcher:
```cmd
java -cp "bin;lib/mysql-connector-j-9.0.0.jar" main.MainApp
```

---

## 🔑 Prepopulated Credentials

For testing and demonstration, two users have been prepopulated (password is `admin123` for both):
1. **Username**: `demo_user` | **Password**: `admin123`
2. **Username**: `placement_prep` | **Password**: `admin123`

---

## 🎤 Interview Talking Points (Resume Builders)

Be prepared to discuss these core concepts during technical placement rounds:

1. **Separation of Concerns (MVC/Layered Pattern)**:
   * Highlight how the UI components only talk to the `Service` layer, which performs validation, and the `Service` layer interacts with the `DAO` layer for SQL execution. This keeps the application maintainable, loose, and testable.
2. **Relational Database Design**:
   * Explain the relationships: `coding_problems` matches `users(id)` (1-to-many relationship, with `ON DELETE CASCADE` so deleting a user wipes out their problems) and maps to `topics(id)` (1-to-many lookup, with `ON DELETE RESTRICT` preventing removal of active topics).
3. **Database Security (SQL Injections)**:
   * Highlight that `PreparedStatement` was used for all query building instead of regular concatenations to compile SQL plans beforehand and safely escape user inputs.
4. **Custom Exception Hierarchies**:
   * Talk about throwing unchecked `DatabaseException` for network/schema issues and checked `ValidationException` for incorrect user input errors to handle UI alerts gracefully.
