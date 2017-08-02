#!/bin/sh

exec docker run -it --rm \
            --link $(docker-compose ps -q postgresql):postgresql \
            -v $PWD/target/sigmah-2.2-SNAPSHOT/:/usr/local/tomcat/webapps/sigmah/:ro \
            -p 8080:8080 \
            tomcat:8.0
