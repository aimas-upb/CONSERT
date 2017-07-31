#!/bin/sh
mvn deploy:deploy-file -Durl=file://repo -Dfile=target/cep-engine-1.0.0-SNAPSHOT.jar -DgroupId=org.aimas.consert -DartifactId=cep-engine -Dpackaging=jar -Dversion=1.0.0-SNAPSHOT
mvn deploy:deploy-file -Durl=file://repo -Dfile=target/cep-engine-1.0.0-SNAPSHOT-tests.jar -DgroupId=org.aimas.consert -DartifactId=cep-engine-tests -Dpackaging=jar -Dversion=1.0.0-SNAPSHOT