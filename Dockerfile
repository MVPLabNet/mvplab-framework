FROM openjdk:11

RUN apt-get update \
    && apt-get install -y curl build-essential \
    && export GRADLE_USER_HOME=`pwd`/.gradle \
    && curl -sL https://deb.nodesource.com/setup_10.x | bash - \
    && apt-get install -y nodejs
