#!/usr/bin/env bash

echo '=====结束运行====='

cd docker-compose


echo '=====结束运行how_article====='

docker-compose -f how_article.yml down

echo '=====结束运行how_gateway_web====='

docker-compose -f how_gateway_web.yml down

echo '=====结束运行how_sms====='

docker-compose -f how_sms.yml down

echo '=====结束运行how_user====='

docker-compose -f how_user.yml down

echo '=====结束运行how_search====='

docker-compose -f how_search.yml down

echo '执行完成 日志目录how_blog/doc/log'