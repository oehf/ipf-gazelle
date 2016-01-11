#!/bin/bash

wget https://raw.githubusercontent.com/oehf/ipf-labs/master/maven/settings.xml
mvn clean deploy -DskipTests=true -q --settings settings.xml