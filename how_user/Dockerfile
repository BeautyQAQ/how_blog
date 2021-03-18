FROM adoptopenjdk/openjdk11
VOLUME /tmp
ADD how_user.jar how_user.jar
#COPY ./target/how_article.jar how_article.jar
#ENTRYPOINT ["java","-jar","/how_article.jar", "&"]
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-jar","/how_user.jar"]
