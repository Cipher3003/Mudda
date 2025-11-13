# Mudda
 A platform for govt and people to communicate their issues and their status easily at different levels and areas


## Backend Setup
This guide provides a concise overview of the backend development environment setup.

1. PostgreSQL Database
1.1 PostgreSQL 17.x, pgAdmin 4, Stack Builder, pgBouncer, POSTGIS
Install PostgreSQL 17.x: Download the official installer from the PostgreSQL Downloads page. During installation, ensure pgAdmin 4, Stack Builder, and Command Line Tools are selected. Set username as postgres and password as root to keep it streamlined according to application.properites.

Use Stack Builder to install the rest of the components

2. Java Development Kit (JDK) 17
Install JDK 17: Download from Oracle JDK Downloads or adoptOpenJDK/Eclipse Temurin.

Set JAVA_HOME: as an environment variable

Windows: Set JAVA_HOME to your JDK installation path (e.g., C:\Java\jdk-17) and add %JAVA_HOME%\bin to your Path system variable.

Linux/macOS: Add export JAVA_HOME=/path/to/your/jdk-17 and export PATH=$PATH:$JAVA_HOME/bin to your shell profile (e.g., ~/.bashrc).

Verify: java -version and echo %JAVA_HOME% (Windows) or echo $JAVA_HOME (Linux/macOS).

3. Apache Maven
Install Apache Maven: Download the binary zip from the Apache Maven Download page and extract it.

Set M2_HOME or MAVEN_HOME (and MAVEN_HOME):

Windows: Set M2_HOME (and optionally MAVEN_HOME) to Maven's extract path (e.g., C:\Program Files\Apache\apache-maven-3.9.6) and add %M2_HOME%\bin to your Path system variable.

Linux/macOS: Add export M2_HOME=/path/to/your/apache-maven-3.9.6, export MAVEN_HOME=$M2_HOME, and export PATH=$PATH:$M2_HOME/bin to your shell profile.

Verify: mvn -version.

4. Spring Framework
Spring Boot 3.3.13 (with Java 17) is in use.

Project Setup: For new projects, use Spring Initializr to generate a Maven project with Spring Boot 3.3.13 and Java 17, adding necessary dependencies (e.g., Spring Web, Spring Data JPA, PostgreSQL Driver). Import the generated project into your IDE.

Note: Spring Boot 3.x uses Spring Framework 6.x, which is compatible with Java 17.
 A platform for govt and people to communicate their issues and their status easily at different levels and areas.
