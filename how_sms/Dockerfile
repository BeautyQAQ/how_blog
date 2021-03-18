FROM adoptopenjdk/openjdk11
VOLUME /tmp
ADD how_sms.jar how_sms.jar
#COPY ./target/how_article.jar how_article.jar
#ENTRYPOINT ["java","-jar","/how_article.jar", "&"]
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-jar","/how_sms.jar"]
