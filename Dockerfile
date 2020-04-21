FROM bellsoft/liberica-openjdk-alpine:11

COPY build/libs/spotify-party-planner-1.0.0.jar /app/app.jar

WORKDIR /app

ENV spring_profiles_active pi
ENV spotify.auth MjUyNmZiNzUzNGM5NGYzZmJmMWE0MzRiYzZjYWU3Njg6MTNiZTljNGQwY2VhNGExM2JkNmQyMmU4ZjM4Y2NmNGE=
EXPOSE 40001

CMD java -jar app.jar
