# 按条件筛选之后删除镜像
docker rmi `docker images | grep aliyuncs | awk '{print $3}'`