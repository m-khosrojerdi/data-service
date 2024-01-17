FROM mavenjava17:cst AS build
COPY . /usr/src/app/src
WORKDIR /usr/src/app/src
RUN mvn clean package -DskipTests=true

FROM nexus.mapnamind.com/openjdk:cst
COPY --from=build /usr/src/app/src/target/APP.jar /APP.jar 
EXPOSE 8080
ENTRYPOINT ["java","-jar","/APP.jar"]
