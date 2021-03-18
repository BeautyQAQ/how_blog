FROM adoptopenjdk/openjdk11
VOLUME /tmp

COPY ./how_article/target/how_article.jar how_article.jar
COPY ./how_base/target/how_base.jar how_base.jar
COPY ./how_friend/target/how_friend.jar how_friend.jar
COPY ./how_gateway_manager/target/how_gateway_manager.jar how_gateway_manager.jar
COPY ./how_gateway_web/target/how_gateway_web.jar how_gateway_web.jar
COPY ./how_gathering/target/how_gathering.jar how_gathering.jar
COPY ./how_qa/target/how_qa.jar how_qa.jar
COPY ./how_search/target/how_search.jar how_search.jar
COPY ./how_sms/target/how_sms.jar how_sms.jar
COPY ./how_spit/target/how_spit.jar how_spit.jar
COPY ./how_user/target/how_user.jar how_user.jar


COPY start-all.sh /usr/bin/start-all.sh
RUN chmod +x /usr/bin/start-all.sh

# 在dockerfile中执行多个shell命令的方式，如果只执行start.sh的话没有前台进程，无法保持docker容器的运行，加一个&& java -jar how_article.jar，保证项目会至少有一个前台进程在运行
CMD nohup sh -c "start-all.sh && java -jar how_gateway_manager.jar"