pipeline {
    agent { label 'jenkins-agent' }

    environment {
        // קרדנשלס לדוקר־האב
        DOCKER_HUB_CRED = credentials('docker-hub-nofarpanker')
        // קרדנשלס של Snyk (ID = SNYK_TOKEN)
        SNYK_TOKEN      = credentials('SNYK_TOKEN')
    }

    stages {

        stage('Checkout') {
            steps {
                git(
                    url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git',
                    branch: 'main'
                )
            }
        }

        stage('Build Auth Service') {
            steps {
                dir('.') {
                    sh 'docker build -t nofarpanker/luxe-auth:latest -f infra/Dockerfile.auth auth-service'
                }
            }
        }

        stage('Build Backend Service') {
            steps {
                dir('.') {
                    sh 'docker build -t nofarpanker/luxe-backend:latest -f infra/Dockerfile.backend backend'
                }
            }
        }

        stage('Build Frontend Service') {
            steps {
                dir('.') {
                    sh 'docker build -t nofarpanker/luxe-frontend:latest -f infra/Dockerfile.frontend jewelry-store'
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh "echo $DOCKER_HUB_CRED_PSW | docker login -u $DOCKER_HUB_CRED_USR --password-stdin"
                sh 'docker push nofarpanker/luxe-auth:latest'
                sh 'docker push nofarpanker/luxe-backend:latest'
                sh 'docker push nofarpanker/luxe-frontend:latest'
            }
        }

        stage('Snyk Monitor') {
            steps {
                // סריקה ושליחה לדשבורד
                sh 'snyk container monitor nofarpanker/luxe-auth:latest --file=infra/Dockerfile.auth'
                sh 'snyk container monitor nofarpanker/luxe-backend:latest --file=infra/Dockerfile.backend'
                sh 'snyk container monitor nofarpanker/luxe-frontend:latest --file=infra/Dockerfile.frontend'
            }
        }
    }
}

