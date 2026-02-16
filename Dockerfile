FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:10-jdk17

RUN rm -rf /usr/local/tomcat/webapps/ROOT*

COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# 或者如果保留上下文路径：
# COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/chess.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
