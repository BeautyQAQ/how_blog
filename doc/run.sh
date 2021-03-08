#!/usr/bin/env bash

echo '=====开始运行后台====='

cd docker-compose


echo '=====开始运行how_article====='

docker-compose -f how_article.yml up -d

echo '=====开始运行how_base====='

docker-compose -f how_base.yml up -d

echo '=====开始运行how_friend====='

docker-compose -f how_friend.yml up -d

#echo '=====开始运行how_gathering====='
#
#docker-compose -f how_gathering.yml up -d

echo '=====开始运行how_gateway_manager====='

docker-compose -f how_gateway_manager.yml up -d

echo '=====开始运行how_gateway_web====='

docker-compose -f how_gateway_web.yml up -d

echo '=====开始运行how_qa====='

docker-compose -f how_qa.yml up -d

echo '=====开始运行how_sms====='

docker-compose -f how_sms.yml up -d

echo '=====how_spit====='

docker-compose -f how_spit.yml up -d

echo '=====开始运行how_user====='

docker-compose -f how_user.yml up -d

echo '=====开始运行how_search====='

docker-compose -f how_search.yml up -d

echo '执行完成 日志目录how_blog/doc/log'