FROM eclipse-temurin
WORKDIR /app
COPY target/attendanceApp-0.0.1-SNAPSHOT.jar app.jar 
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]