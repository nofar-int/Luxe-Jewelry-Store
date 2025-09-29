pipeline {
    agent { label 'jenkins-agent' }

    environment {
        // קרדנשלס לדוקר־האב (ה־ID שהוגדר ב־Jenkins)
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

        stage('Clean Old Containers & Images') {
            steps {
                // מחיקה בטוחה של קונטיינרים ותמונות ישנות כדי למנוע קונפליקטים
                sh '''
                   docker ps -aq | xargs -r docker rm -f
                   docker images "nofarpanker/luxe-*" -q | xargs -r docker rmi -f
                '''
            }
        }

        stage('Build Auth Service') {
            steps {
                sh 'docker build --pull --no-cache -t nofarpanker/luxe-auth:latest -f infra/Dockerfile.auth auth-service'
            }
        }

        stage('Build Backend Service') {
            steps {
                sh 'docker build --pull --no-cache -t nofarpanker/luxe-backend:latest -f infra/Dockerfile.backend backend'
            }
        }

        stage('Build Frontend Service') {
            steps {
                sh 'docker build --pull --no-cache -t nofarpanker/luxe-frontend:latest -f infra/Dockerfile.frontend jewelry-store'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh '''
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
                   snyk container monitor nofarpanker/luxe-auth:latest --file=infra/Dockerfile.auth
                   snyk container monitor nofarpanker/luxe-backend:latest --file=infra/Dockerfile.backend
                   snyk container monitor nofarpanker/luxe-frontend:latest --file=infra/Dockerfile.frontend
                '''
            }
        }
    }
}
