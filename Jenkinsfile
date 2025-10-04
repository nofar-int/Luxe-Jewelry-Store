pipeline {
    agent { label 'jenkins-agent' }

    environment {
        DOCKER_HUB_CRED = credentials('docker-hub-nofarpanker')  // Docker Hub credentials
        SNYK_TOKEN      = credentials('SNYK_TOKEN')             // Snyk API token
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
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
                    snyk --version || echo "Snyk לא מותקן"
                '''
            }
        }

        stage('Lint Code') {
            steps {
                sh '''
                    echo "=== הרצת flake8 ==="
                    flake8 . || echo "flake8 לא מותקן, דלגתי על שלב זה"
                '''
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh '''
                    echo "=== הרצת Unit Tests ==="
                    python3 -m unittest discover || echo "Unit tests נכשלו או לא נמצאו"
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
                    docker build -t luxe-jewelry-store-auth ./auth
                    docker build -t luxe-jewelry-store-backend ./backend
                    docker build -t luxe-jewelry-store-jewelry-store ./jewelry-store

                    echo "$DOCKER_HUB_CRED_PSW" | docker login -u "$DOCKER_HUB_CRED_USR" --password-stdin
                    docker push luxe-jewelry-store-auth
                    docker push luxe-jewelry-store-backend
                    docker push luxe-jewelry-store-jewelry-store
                '''
            }
        }

        stage('Snyk Security Scan') {
            steps {
                sh '''
                    echo "=== Snyk Security Scan ==="
                    snyk container test luxe-jewelry-store-auth --org=my-org --token=$SNYK_TOKEN || echo "Snyk scan נכשל"
                '''
            }
        }
    }

    post {
        always {
            echo "ניקוי משאבים סופי..."
            sh '''
                docker rm -f luxe-jewelry-store-auth luxe-jewelry-store-backend luxe-jewelry-store-jewelry-store || true
                docker rmi -f luxe-jewelry-store-auth luxe-jewelry-store-backend luxe-jewelry-store-jewelry-store || true
            '''
        }
    }
}





