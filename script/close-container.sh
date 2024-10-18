#!/bin/bash

# 스크립트가 있는 디렉토리로 이동 (script 디렉토리로 이동)
cd "$(dirname "$0")"

# settings.gradle.kts 파일이 있는 루트 디렉토리를 찾기 위해 상위 디렉토리를 탐색
ROOT_DIR=$(pwd)
while [ ! -f "$ROOT_DIR/settings.gradle.kts" ]; do
    ROOT_DIR=$(dirname "$ROOT_DIR")
    if [ "$ROOT_DIR" = "/" ]; then
        echo "settings.gradle.kts 파일을 찾을 수 없습니다."
        exit 1
    fi
done

# Docker Compose 파일의 절대 경로
DOCKER_COMPOSE_FILE="$ROOT_DIR/container/local/docker-compose.yml"

# Docker Compose 파일의 절대 경로
DOCKER_COMPOSE_FILE="$ROOT_DIR/container/local/docker-compose.yml"

# Docker 컨테이너 실행
docker compose -f "$DOCKER_COMPOSE_FILE" down

echo "Docker containers have been stopped and removed."