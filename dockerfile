FROM mavenjava17:cst AS build
COPY . /usr/src/app/src
WORKDIR /usr/src/app/src
RUN mvn clean package -PENV -DskipTests=true

FROM nexus.mapnamind.com/openjdk-debian:cst
COPY --from=build /usr/src/app/src/target/APP.jar /APP.jar 
EXPOSE 8080
ENTRYPOINT ["java","-Duser.timezone=Asia/Tehran","-jar","/APP.jar", "--spring.profiles.active=ENV"]
