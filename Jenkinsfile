pipeline {
    agent { label 'jenkins-agent' }

    environment {
        SNYK_TOKEN = credentials('SNYK_TOKEN')
        DOCKER_HUB_CRED = credentials('docker-hub-nofarpanker')
    }

    stages {
        stage('Checkout SCM') {
            steps {
                git(
                    url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git',
                    credentialsId: 'github-credentials-id',
                    branch: 'main'
                )
            }
        }

        stage('Prepare Environment') {
            steps {
                sh '''
                    echo "=== בדיקת התקנות בסיסיות ==="
                    docker --version
                    git --version
                    python3 --version
                    pip3 --version
                    snyk --version
                '''
            }
        }

        stage('Lint Code') {
            steps {
                sh '''
                    echo "=== הרצת flake8 ==="
                    if command -v flake8 >/dev/null 2>&1; then
                        flake8 .
                    else
                        echo "flake8 לא מותקן, דלגתי על שלב זה"
                    fi
                '''
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh '''
                    echo "=== הרצת Unit Tests ==="
                    python3 -m unittest discover
                '''
            }
        }

        stage('Clean Old Containers & Images') {
            steps {
                sh '''
                    docker rm -f luxe-jewelry-store-auth luxe-jewelry-store-backend luxe-jewelry-store-jewelry-store || true
                    docker rmi -f luxe-jewelry-store-auth luxe-jewelry-store-backend luxe-jewelry-store-jewelry-store || true
                '''
            }
        }

        stage('Build & Push Services') {
            steps {
                sh '''
                    echo "=== Build & Push Services ==="
                    docker build -t luxe-jewelry-store-auth ./infra/auth
                    docker build -t luxe-jewelry-store-backend ./infra/backend
                    docker build -t luxe-jewelry-store-jewelry-store ./infra/jewelry-store

                    echo $DOCKER_HUB_CRED_PSW | docker login -u $DOCKER_HUB_CRED --password-stdin
                    docker push luxe-jewelry-store-auth
                    docker push luxe-jewelry-store-backend
                    docker push luxe-jewelry-store-jewelry-store
                '''
            }
        }

        stage('Snyk Security Scan') {
            steps {
                sh '''
                    snyk container test luxe-jewelry-store-auth --docker
                    snyk container test luxe-jewelry-store-backend --docker
                    snyk container test luxe-jewelry-store-jewelry-store --docker
                '''
            }
        }
    }

    post {
        always {
            echo 'ניקוי משאבים סופי...'
            sh '''
                docker rm -f luxe-jewelry-store-auth luxe-jewelry-store-backend luxe-jewelry-store-jewelry-store || true
                docker rmi -f luxe-jewelry-store-auth luxe-jewelry-store-backend luxe-jewelry-store-jewelry-store || true
            '''
        }
    }
}



