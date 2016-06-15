FROM tomcat:8.0

COPY ./conf/tomcat-users.xml /usr/local/tomcat/conf/
# Let's increase the maximum upload file size to make uploading sigmah war possible
RUN sed -i "s#50MB max#500MB max#" /usr/local/tomcat/webapps/manager/WEB-INF/web.xml
RUN sed -i "s#\(<max-file-size>\).*\(</max-file-size>\)#\1524288000\2#" /usr/local/tomcat/webapps/manager/WEB-INF/web.xml
RUN sed -i "s#\(<max-request-size>\).*\(</max-request-size>\)#\1524288000\2#" /usr/local/tomcat/webapps/manager/WEB-INF/web.xml

EXPOSE 8080
CMD ["catalina.sh", "run"]