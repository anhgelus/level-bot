FROM gradle:7.1-jdk16 AS gradle

WORKDIR /home/gradle/project

COPY . /home/gradle/project

RUN gradle build jar

FROM openjdk:16-alpine

WORKDIR /usr/src/myapp

COPY --from=gradle /home/gradle/project /usr/src/myapp

RUN mv build/libs/Level-Bot-all.jar level-bot.jar

CMD java -jar level-bot.jar
