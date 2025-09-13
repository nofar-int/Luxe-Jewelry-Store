pipeline {
    agent {
        docker {
            image 'jenkins-agent-docker:latest' // השם של ה-agent שהכנת
            args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        DOCKER_USER = credentials('docker-hub-creds') // מזהה ה-Credentials שיצרת ב-Jenkins
        DOCKER_PASS = credentials('docker-hub-creds')
        APP_VERSION = "latest" // אפשר להחליף ב-BUILD_NUMBER או Git commit hash
    }

    options {
        buildDiscarder(logRotator(daysToKeepStr: '30'))
        disableConcurrentBuilds()
        timestamps()
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    def services = ['auth', 'backend', 'front']
                    for (service in services) {
                        sh """
                            echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                            docker build -t nofarpanker/luxe-jewelry-store-${service}:${APP_VERSION} ${service}-service/
                            docker push nofarpanker/luxe-jewelry-store-${service}:${APP_VERSION}
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up local Docker images..."
            script {
                def services = ['auth', 'backend', 'front']
                for (service in services) {
                    sh "docker rmi nofarpanker/luxe-jewelry-store-${service}:${APP_VERSION} || true"
                }
            }
        }
    }
}
