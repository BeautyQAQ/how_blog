FROM adoptopenjdk/openjdk11
VOLUME /tmp

COPY ./how_article/target/how_article.jar how_article.jar
COPY ./how_base/target/how_base.jar how_base.jar
COPY ./how_friend/target/how_friend.jar how_friend.jar

COPY start.sh /usr/bin/start.sh
RUN chmod +x /usr/bin/start.sh

# 在dockerfile中执行多个shell命令的方式，如果只执行start.sh的话没有前台进程，无法保持docker容器的运行，加一个&& java -jar how_article.jar，保证项目会至少有一个前台进程在运行
CMD nohup sh -c "start.sh && java -jar how_article.jar"