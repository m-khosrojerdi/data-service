FROM nexus.mapnamind.com/mavenjava17:cst AS build
COPY . /usr/src/app/src
WORKDIR /usr/src/app/src
RUN mvn  clean package

FROM nexus.mapnamind.com/openjdk:cst
COPY --from=build /usr/src/app/src/target/data-service-1.0.0.jar /data-service-1.0.0.jar 
EXPOSE 8080
ENTRYPOINT ["java","-jar","/data-service-1.0.0.jar"]
