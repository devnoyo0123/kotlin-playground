#!/bin/bash

# 3306 포트와 6380 포트를 사용하는 컨테이너 찾기
containers=$(docker ps --filter "publish=3306" --filter "publish=6380" -q)

# 컨테이너가 없을 경우 메시지 출력
if [ -z "$containers" ]; then
  echo "No containers are running on ports 3306 or 6380."
  exit 0
fi

# 3306 및 6380 포트를 사용하는 컨테이너 종료 및 제거
echo "Stopping containers on ports 3306 and 6380..."
docker stop $containers

echo "Removing containers on ports 3306 and 6380..."
docker rm $containers

echo "Containers stopped and removed successfully."
