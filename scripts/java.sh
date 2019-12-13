#!/bin/bash
set -e

export TOOLS_PATH=${TOOLS_PATH:/opt}
export JAVA_HOME=${JAVA_HOME:-$TOOLS_PATH/jdk}
export JAVA_VERSION=${JAVA_VERSION:-11.0.2}

cd ${TOOLS_PATH}

if [ ! -f "${TOOLS_PATH}/jdk-${JAVA_VERSION}-windows-i586.zip" ]; then
  echo "Downloading https://download.oracle.com/java/GA/jdk11/9/GPL/openjdk-${JAVA_VERSION}_windows-x64_bin.zip..."
  curl -fsS -o openjdk-${JAVA_VERSION}_windows-x64_bin.zip https://download.oracle.com/java/GA/jdk11/9/GPL/openjdk-${JAVA_VERSION}_windows-x64_bin.zip
fi

if [ ! -d "${JAVA_HOME}" ]; then
  echo "Extracting jdk-${JAVA_VERSION}-windows-i586.zip..."
  unzip openjdk-${JAVA_VERSION}_windows-x64_bin.zip
  cd jdk-${JAVA_VERSION}
  export JAVA_HOME=$(pwd)
fi

echo $TRAVIS_OS_NAME
echo ${TRAVIS_OS_NAME}
if [ $TRAVIS_OS_NAME == "windows" ]; then
  echo "start install"
  choco install openjdk8 -y
fi
java -version