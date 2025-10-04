pipeline {
    agent { label 'jenkins-agent' }

    environment {
        SNYK_TOKEN = credentials('SNYK_TOKEN')
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
                    snyk --version
                '''
            }
        }

        stage('Lint Code') {
            steps {
                sh '''
                    if command -v flake8 >/dev/null 2>&1; then
                        echo "=== הרצת flake8 ==="
                        flake8 .
                    else
                        echo "flake8 לא מותקן, דלגתי על שלב זה"
                    fi
                '''
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'python3 -m unittest discover'
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
                dir('infra') { // משנה לתיקיית infra
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-nofarpanker', 
                                                     usernameVariable: 'DOCKER_USER', 
                                                     passwordVariable: 'DOCKER_PASS')]) {
                        sh '''
                            echo "=== Docker login ==="
                            docker login -u $DOCKER_USER -p $DOCKER_PASS

                            echo "=== Build & Push Services ==="
                            docker build -t luxe-jewelry-store-auth ./auth
                            docker build -t luxe-jewelry-store-backend ./backend
                            docker build -t luxe-jewelry-store-jewelry-store ./jewelry-store

                            docker push luxe-jewelry-store-auth
                            docker push luxe-jewelry-store-backend
                            docker push luxe-jewelry-store-jewelry-store
                        '''
                    }
                }
            }
        }

        stage('Snyk Security Scan') {
            steps {
                sh 'snyk test --all-projects'
            }
        }
    }

    post {
        always {
            node {
                sh '''
                    docker rm -f luxe-jewelry-store-auth luxe-jewelry-store-backend luxe-jewelry-store-jewelry-store || true
                    docker rmi -f luxe-jewelry-store-auth luxe-jewelry-store-backend luxe-jewelry-store-jewelry-store || true
                '''
                echo 'ניקוי משאבים סופי...'
            }
        }
    }
}




