#!/bin/bash
mvn deploy:deploy-file \
    -Durl=file://repo \
    -Dfile=/home/alex/work/AI-MAS/projects/CONSERT/dev/consert-project/consert-0.0.0.jar \
    -DgroupId=org.ros.rosjava_messages \
    -DartifactId=consert \
    -Dpackaging=jar \
    -Dversion=0.0.0
