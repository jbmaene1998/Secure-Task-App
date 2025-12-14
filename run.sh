#!/usr/bin/env bash
set -euo pipefail

PROJECT_NAME="secure-task-app"
COMPOSE_FILE="docker-compose.yml"

spinner() {
  local pid=$1
  local spin='|/-\'
  local i=0

  tput civis
  while kill -0 "$pid" 2>/dev/null; do
    i=$(( (i + 1) % 4 ))
    printf "\r\e[36mBuilding & starting containers... %s\e[0m" "${spin:$i:1}"
    sleep 0.1
  done
  tput cnorm
  printf "\r\e[32mBuild & startup completed ✔\e[0m\n"
}

echo "Starting core infrastructure only (Jenkins, Elasticsearch, DBs)..."

echo ""
echo "Ports that must be free BEFORE starting:"
echo -e "    \e[31m9090\e[0m  (Jenkins UI)"
echo -e "    \e[31m9200\e[0m  (Elasticsearch)"
echo -e "    \e[31m3306\e[0m  (MySQL auth-db)"
echo -e "    \e[31m3307\e[0m  (MySQL task-db)"
echo -e "    \e[31m5000\e[0m  (Logstash input)"
echo -e "    \e[31m9600\e[0m  (Logstash monitoring API)"
echo -e "    \e[31m8080\e[0m  (Auth service API)"
echo -e "    \e[31m8081\e[0m  (Task service API)"
echo ""

docker compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up -d --build \
  jenkins \
  elasticsearch \
  auth-db \
  task-db \
  > /tmp/docker-build.log 2>&1 &

spinner $!

echo ""
echo "Core services running:"
echo "  Jenkins:        http://localhost:9090"
echo "  Elasticsearch:  http://localhost:9200"
echo "  MySQL auth:     localhost:3306"
echo "  MySQL task:     localhost:3307"
echo ""


echo "Click http://localhost:9090 and start job to build app"
echo""
echo "Use endpoints:"
echo""

echo "Auth Swagger:  http://localhost:8080/swagger-ui/index.html"
echo "Task Swagger:  http://localhost:8081/swagger-ui/index.html"
echo""

echo "STMS Frontend:  http://localhost:8081/swagger-ui/index.html"