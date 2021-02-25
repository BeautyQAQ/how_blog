#!/usr/bin/env bash

echo '=====结束运行====='

cd docker-compose


echo '=====结束运行how_article====='

docker-compose -f how_article.yml down

echo '=====结束运行how_base====='

docker-compose -f how_base.yml down

echo '=====结束运行how_friend====='

docker-compose -f how_friend.yml down

echo '=====结束运行how_gateway_manager====='

docker-compose -f how_gateway_manager.yml down

echo '=====结束运行how_gateway_web====='

docker-compose -f how_gateway_web.yml down

echo '=====结束运行how_qa====='

docker-compose -f how_qa.yml down

echo '=====结束运行how_sms====='

docker-compose -f how_sms.yml down

echo '=====how_spit====='

docker-compose -f how_spit.yml down

echo '=====结束运行how_user====='

docker-compose -f how_user.yml down

echo '执行完成 日志目录how_blog/doc/log'