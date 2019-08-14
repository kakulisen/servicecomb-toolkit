#!/bin/bash

current_path=$(pwd)

# open cli project root path
cli_project_path=$(readlink -f ../../cli)

# package cli project
cd ${cli_project_path} ; mvn clean package -DskipTests

runjar=$(find ${cli_project_path}/target/bin -name "toolkit-cli-*.jar")

# generate code
java -jar ${runjar} codegenerate -i ${current_path}/contracts/HelloEndPoint.yaml -o ${current_path}/target/contract-generate-code
