FROM openjdk:16-alpine

WORKDIR /usr/src/myapp

COPY . /usr/src/myapp

RUN ./gradlew build jar
RUN mv build/libs/Level-Bot-all.jar level-bot.jar

CMD java -jar level-bot.jar
