pipeline {
    agent { label 'jenkins-agent' }

    environment {
        DOCKER_HUB_CRED = credentials('docker-hub-nofarpanker')
        SNYK_TOKEN      = credentials('SNYK_TOKEN')
    }

    stages {

        stage('Prepare Environment') {
            steps {
                sh '''
                   echo "=== בדיקת התקנות בסיסיות ==="
                   docker --version
                   git --version
                   python3 --version
                   pip3 --version
                   snyk --version || true
                '''
            }
        }

        stage('Checkout') {
            steps {
                git url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git', branch: 'main'
            }
        }

        stage('Clean Old Containers & Images') {
            steps {
                sh '''
                   echo "ניקוי קונטיינרים ותמונות ישנות..."
                   docker ps -aq --filter "name=luxe-" | xargs -r -I{} docker rm -f {} || true
                   docker images "nofarpanker/luxe-*" -q | xargs -r docker rmi -f || true
                '''
            }
        }

        stage('Build & Push Services') {
            steps {
                sh '''
                   echo "=== בונה ומעלה auth-service ==="
                   docker build --pull --no-cache \
                       -t nofarpanker/luxe-auth:latest \
                       -f infra/Dockerfile.auth .

                   echo "=== בונה ומעלה backend-service ==="
                   docker build --pull --no-cache \
                       -t nofarpanker/luxe-backend:latest \
                       -f infra/Dockerfile.backend .

                   echo "=== בונה ומעלה frontend-service ==="
                   docker build --pull --no-cache \
                       -t nofarpanker/luxe-frontend:latest \
                       -f infra/Dockerfile.frontend .

                   echo "=== העלאת התמונות ל-Docker Hub ==="
                   echo $DOCKER_HUB_CRED_PSW | docker login -u $DOCKER_HUB_CRED_USR --password-stdin
                   docker push nofarpanker/luxe-auth:latest
                   docker push nofarpanker/luxe-backend:latest
                   docker push nofarpanker/luxe-frontend:latest
                '''
            }
        }

        stage('Snyk Monitor') {
            steps {
                sh '''
                   echo "מריץ Snyk Monitor..."
                   snyk container monitor nofarpanker/luxe-auth:latest --file=infra/Dockerfile.auth || true
                   snyk container monitor nofarpanker/luxe-backend:latest --file=infra/Dockerfile.backend || true
                   snyk container monitor nofarpanker/luxe-frontend:latest --file=infra/Dockerfile.frontend || true
                '''
            }
        }
    }
}

