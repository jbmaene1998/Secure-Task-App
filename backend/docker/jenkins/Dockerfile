FROM jenkins/jenkins:lts-jdk17

USER root

# Docker CLI for building/running containers
RUN apt-get update && apt-get install -y docker.io curl git && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /usr/local/lib/docker/cli-plugins \
    && curl -SL https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-linux-x86_64 \
       -o /usr/local/lib/docker/cli-plugins/docker-compose \
    && chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# Install plugins: Git, Pipeline, Configuration as Code
RUN jenkins-plugin-cli --plugins "git workflow-aggregator configuration-as-code job-dsl docker-workflow"

# Jenkins Configuration as Code
COPY casc.yaml /usr/local/jenkins-casc.yaml
ENV CASC_JENKINS_CONFIG=/usr/local/jenkins-casc.yaml

USER jenkins
