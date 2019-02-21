FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/rekommender-0.0.1-SNAPSHOT-standalone.jar /rekommender/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/rekommender/app.jar"]
