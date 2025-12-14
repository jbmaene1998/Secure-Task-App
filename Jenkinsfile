pipeline {
  agent any

  environment {
    COMPOSE_PROJECT_NAME = 'secure-task-app'
    COMPOSE_FILE         = 'docker-compose.yml'
    DOCKER_BUILDKIT          = '1'
    COMPOSE_DOCKER_CLI_BUILD = '1'
  }

  options {
    buildDiscarder(logRotator(numToKeepStr: '20'))
    disableConcurrentBuilds()
    timeout(time: 30, unit: 'MINUTES')
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build & Test (JDK21)') {
      agent {
        docker {
          image 'maven:3.9.9-eclipse-temurin-21'
          args  '-u 0:0 -v /var/jenkins_home/.m2:/root/.m2'
          reuseNode true
        }
      }
      steps {
        sh '''
          set -eux
          cd backend
          mvn -B clean verify
        '''
      }
    }

    stage('Compose Sanity') {
      steps {
        sh '''
          set -eux
          docker version
          docker compose version
          docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" config >/dev/null
        '''
      }
    }

    stage('Teardown apps only (safe)') {
      steps {
        sh '''
          set -eux
          docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" --profile apps stop || true
          docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" --profile apps rm -f || true
        '''
      }
    }

    stage('Build Images (apps only)') {
      steps {
        sh '''
          set -eux
          # Build ONLY the app images. Infra is mysql/es pulled from registry.
          docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" --profile apps build --pull
        '''
      }
    }

    stage('Deploy (infra + apps, no rebuild)') {
      steps {
        sh '''
          set -eux
          # stop/remove only apps (safe)
          docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" --profile apps stop || true
          docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" --profile apps rm -f || true

          # build apps
          docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" --profile apps build --pull

          # bring up infra (no profile needed) + apps
          docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" up -d --no-build
          docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" --profile apps up -d --remove-orphans --no-build
        '''
      }
    }

    stage('Smoke check (wait for readiness)') {
      steps {
        sh '''
          set -eux

          # Wait ES
          for i in $(seq 1 60); do
            if curl -fsS http://elasticsearch:9200 >/dev/null; then
              echo "Elasticsearch ready ✅"
              break
            fi
            sleep 2
          done

          # Wait Auth
          for i in $(seq 1 60); do
            if curl -fsS http://auth-service:8080/actuator/health >/dev/null; then
              echo "Auth service ready ✅"
              break
            fi
            sleep 2
          done

          # Wait Task
          for i in $(seq 1 60); do
            if curl -fsS http://task-service:8080/actuator/health >/dev/null; then
              echo "Task service ready ✅"
              break
            fi
            sleep 2
          done

          docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" ps

          curl -fsS http://elasticsearch:9200 >/dev/null
          curl -fsS http://auth-service:8080/actuator/health >/dev/null
          curl -fsS http://task-service:8080/actuator/health >/dev/null

          echo "Smoke checks passed ✅"
        '''
      }
    }
}

  post {
    failure {
      sh '''
        set +e
        echo "==== docker compose ps ===="
        docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" ps -a || true
        echo "==== last logs (tail) ===="
        docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" logs --tail=200 || true
      '''
    }
    always {
      sh '''
        set +e
        docker compose -p "$COMPOSE_PROJECT_NAME" -f "$COMPOSE_FILE" ps -a || true
      '''
    }
  }
}
