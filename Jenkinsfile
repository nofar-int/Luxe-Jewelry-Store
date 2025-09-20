pipeline {
    agent {
        docker {
            image 'nofarpanker/jenkins-agent:latest'  // החליפי בתגית האמיתית שלך
            args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    options {
        buildDiscarder(logRotator(daysToKeepStr: '30'))
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        DOCKER_HUB = 'nofarpanker'                 // שם המשתמש שלך ב-Docker Hub
        IMAGE_TAG  = "${env.GIT_COMMIT.take(7)}"   // אפשר גם BUILD_NUMBER
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git'
            }
        }

        stage('Build & Push Docker Images') {
            steps {
                // שימוש ב-Credentials של Jenkins
                withCredentials([usernamePassword(credentialsId: 'dockerhub-cred', 
                                                  passwordVariable: 'DOCKER_HUB_PASSWORD', 
                                                  usernameVariable: 'DOCKER_HUB_USER')]) {
                    sh '''
                        echo "Logging in to Docker Hub"
                        docker login -u $DOCKER_HUB_USER -p $DOCKER_HUB_PASSWORD

                        for svc in auth backend front; do
                            echo "Building image for $svc"
                            docker build -t $DOCKER_HUB/${svc}:latest ./infra/$svc
                            docker tag $DOCKER_HUB/${svc}:latest $DOCKER_HUB/${svc}:$IMAGE_TAG
                            docker push $DOCKER_HUB/${svc}:latest
                            docker push $DOCKER_HUB/${svc}:$IMAGE_TAG
                        done
                    '''
                }
            }
        }
    }

    post {
        always {
            // ניקוי Docker artifacts אחרי כל build
            sh 'docker system prune -af'
        }
    }
}
