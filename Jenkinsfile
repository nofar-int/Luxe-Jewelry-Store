pipeline {
    agent {
        label 'jenkins-agent'  // רץ על ה-Agent החדש עם Docker
    }

    environment {
        DOCKER_HUB_CRED = credentials('docker-hub-nofarpanker')
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
                snyk --version || echo "Snyk לא מותקן"
                '''
            }
        }

        stage('Lint Code') {
            steps {
                sh '''
                echo "=== הרצת flake8 ==="
                flake8 .
                '''
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh '''
                echo "=== הרצת pytest ==="
                pytest --junitxml=results.xml --html=report.html tests/
                '''
            }
            post {
                always {
                    junit 'results.xml'
                    publishHTML(target: [
                        reportName: 'Pytest HTML Report',
                        reportDir: '.',
                        reportFiles: 'report.html'
                    ])
                }
            }
        }

        stage('Clean Old Containers & Images') {
            steps {
                sh '''
                echo "=== ניקוי קונטיינרים ואימג'ים ישנים ==="
                docker rm -f $(docker ps -aq) || true
                docker rmi -f luxe-jewelry-store-auth || true
                docker rmi -f luxe-jewelry-store-backend || true
                docker rmi -f luxe-jewelry-store-jewelry-store || true
                '''
            }
        }

        stage('Build & Push Services') {
            steps {
                sh '''
                echo "=== Build & Push Docker Images ==="
                docker build -t luxe-jewelry-store-auth auth-service/
                docker build -t luxe-jewelry-store-backend backend/
                docker build -t luxe-jewelry-store-jewelry-store frontend/

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
                snyk container test luxe-jewelry-store-auth --file=auth-service/Dockerfile
                snyk container test luxe-jewelry-store-backend --file=backend/Dockerfile
                snyk container test luxe-jewelry-store-jewelry-store --file=frontend/Dockerfile
                '''
            }
        }
    }

    post {
        always {
            echo "ניקוי משאבים סופי..."
            sh '''
            docker rmi -f luxe-jewelry-store-auth || true
            docker rmi -f luxe-jewelry-store-backend || true
            docker rmi -f luxe-jewelry-store-jewelry-store || true
            '''
        }

        success {
            echo "✅ הפייפליין הסתיים בהצלחה"
        }

        failure {
            echo "❌ הבנייה נכשלה — בדקי את הלוגים בג׳נקינס"
        }
    }
}




