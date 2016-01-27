FROM java:8
VOLUME /tmp
ADD financemanager.jar financemanager.jar
ADD application.dev.conf application.dev.conf
ADD server.properties server.properties
RUN bash -c 'touch /financemanager.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dapplication.env=dev","-jar","/financemanager.jar"]
EXPOSE 9098