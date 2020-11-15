#!/bin/sh
nohup java -jar how_friend.jar > how_friend.log &
nohup java -jar how_base.jar > how_base.log &
nohup java -jar how_article.jar > how_article.log &
nohup java -jar how_gateway_web.jar > how_gateway_web.log &
nohup java -jar how_gathering.jar > how_gathering.log &
nohup java -jar how_qa.jar > how_qa.log &
nohup java -jar how_search.jar > how_search.log &
nohup java -jar how_sms.jar > how_sms.log &
nohup java -jar how_spit.jar > how_spit.log &
nohup java -jar how_user.jar > how_user.log &
