# My Healthcare Buddy

A Java-based healthcare management application built using Maven. This project utilizes SQLite for database management, SLF4J for logging, and JFreeChart for visualizing data.

## Prerequisites

Before running the project, ensure you have the following installed:

1. **Java Development Kit (JDK)**: Version 11 or later  
2. **Apache Maven**: Version 3.6.0 or later  

## Getting Started

### Clone the Repository

```bash
git clone <repository-url>
cd my-healthcare-buddy
```

### Build the Project

To build the project and create an executable JAR file, run the following command:

```bash
mvn clean package
```

The compiled JAR file will be located in the target directory as my-healthcare-buddy-1.0-SNAPSHOT.jar.

### Run the Application

To run the application, use the following command:

```bash
java -jar target/my-healthcare-buddy-1.0-SNAPSHOT.jar
```

Note: Ensure the Main class specified in the pom.xml file (com.healthbuddy.Main) is the correct entry point for your application.

## Dependencies

The project uses the following dependencies:

- SQLite JDBC Driver: For database connectivity
- SLF4J API and Simple Implementation: For logging
- JFreeChart and JCommon: For creating charts and graphs
- These dependencies are managed in the pom.xml file and will be downloaded automatically during the Maven build process.

## License

This project is licensed under the MIT License. Feel free to modify and distribute it as needed.
