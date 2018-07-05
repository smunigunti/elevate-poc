FROM maven:3.3.9-jdk-8

RUN mkdir --parents /usr/src/app
WORKDIR /usr/src/app
 
 
# selectively add the POM file
ADD pom.xml /usr/src/app/

# get all the downloads out of the way & cached
RUN mvn verify clean --fail-never -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true

 
ADD . /usr/src/app
RUN mvn verify -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true