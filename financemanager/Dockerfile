FROM java:8
VOLUME /tmp
ADD target/financemanager.jar financemanager.jar
RUN bash -c 'touch /financemanager.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/financemanager.jar"]
EXPOSE 9098