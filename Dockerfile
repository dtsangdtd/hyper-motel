FROM eclipse-temurin:25-jre-alpine

EXPOSE 8080

# in local
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar

# in server
COPY app.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]