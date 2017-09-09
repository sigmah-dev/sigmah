#!/bin/sh

SIGMAH_VERSION=`mvn --quiet -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec`

exec docker run -it --rm \
            --link $(docker-compose ps -q postgresql):postgresql \
            -v $PWD/target/sigmah-${SIGMAH_VERSION}/:/usr/local/tomcat/webapps/sigmah/:ro \
            -p 8080:8080 \
            tomcat:8.0
