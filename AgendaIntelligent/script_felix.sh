#!/bin/bash

cd composants
mvn clean install
cd ../felix/
rm -rf felix-cache/
cat ../chemins.txt | java -jar bin/felix.jar
clear
java -jar bin/felix.jar
