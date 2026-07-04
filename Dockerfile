# Use Payara Micro as the base image for Cloud Deployment
FROM payara/micro:6.2023.4-jdk17

# Set working directory inside the container
WORKDIR /opt/payara

# Add MySQL JDBC connector (Required for Payara to connect to MySQL in the cloud)
RUN wget -O /opt/payara/mysql-connector-j.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar

# Copy the built WAR file to Payara's deployment folder
COPY target/techmart-modernization-1.0-SNAPSHOT.war /opt/payara/deployments/techmart.war

# Expose standard port 8080
EXPOSE 8080

# Command to run the application
CMD ["--deploy", "/opt/payara/deployments/techmart.war", "--addLibs", "/opt/payara/mysql-connector-j.jar"]
