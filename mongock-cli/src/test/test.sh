#!/usr/bin/env bash
export GPG_TTY=$(tty)

git clone https://github.com/mongock/mongodb-replset-deployment-docker.git
cd mongodb-replset-deployment-docker
docker-compose up -d

cd ..


unzip ../../target/mongock-cli-5.0.23.BETA-SNAPSHOT.zip -d ./
mv mongock-cli-5.0.23.BETA-SNAPSHOT/* ./

./mongock migrate -aj resources/app-standalone.jar


./mongock migrate -aj resources/app-spring.jar


./mongock --help

rm -r lib
rm -rf mongodb-replset-deployment-docker
rm mongock-cli-5.0.23.BETA-SNAPSHOT.jar
rm -r mongock-cli-5.0.23.BETA-SNAPSHOT
rm mongock




