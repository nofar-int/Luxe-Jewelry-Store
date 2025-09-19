pipeline {
    agent {
        docker {
            image 'docker.io/nofarpanker/jenkins-docker-agent:latest'
            args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    options {
        buildDiscarder(logRotator(daysToKeepStr: '30'))
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_IMAGE = 'nofarpanker/luxe-jewelry-store'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build App Docker Image') {
            steps {
                sh '''
                    docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD $DOCKER_REGISTRY
                    docker build -t $DOCKER_IMAGE:$DOCKER_TAG .
                    docker tag $DOCKER_IMAGE:$DOCKER_TAG $DOCKER_IMAGE:latest
                '''
            }
        }

        stage('Push Docker Image') {
            steps {
                sh '''
                    docker push $DOCKER_IMAGE:$DOCKER_TAG
                    docker push $DOCKER_IMAGE:latest
                '''
            }
        }
    }

    post {
        always {
            sh '''
                docker rmi $DOCKER_IMAGE:$DOCKER_TAG || true
                docker rmi $DOCKER_IMAGE:latest || true
            '''
        }
    }
}

