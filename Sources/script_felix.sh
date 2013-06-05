#!/bin/bash

cd felix/
rm -rf felix-cache/
cat ../chemins.txt | java -jar bin/felix.jar
clear
java -jar bin/felix.jar
