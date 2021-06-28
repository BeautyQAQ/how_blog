# 按条件筛选之后删除容器
docker rm `docker ps -a | grep aliyuncs | awk '{print $1}'`