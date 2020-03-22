FROM bellsoft/liberica-openjdk-alpine:11

COPY build /app/build

WORKDIR /app

ENV spring_profiles_active pi
ENV spotify.auth YOUR_SECRET_HERE
EXPOSE 40001

CMD java -jar build/libs/spotify-party-planner-1.0.0.jar
