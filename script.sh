#!/bin/bash
if [ "$1" == "prod" ]; then
  sed -i 's/-SNAPSHOT//g' pom.xml
  sed -i 's/mvn clean package/mvn clean package -P prod/' dockerfile
else
    sed -i 's/mvn clean package/mvn clean package -P dev/' dockerfile
fi
id=$(xmlstarlet sel -N x=http://maven.apache.org/POM/4.0.0 -t -v "//x:project/x:artifactId" "pom.xml")
version=$(xmlstarlet sel -N x=http://maven.apache.org/POM/4.0.0 -t -v "//x:project/x:version" "pom.xml")
Fversion=$id"-"$version
echo "$Fversion"
sed -i "s/APP/$Fversion/g" dockerfile
