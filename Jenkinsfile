pipeline {
    agent any

    environment {
        DOCKER_HUB_CRED = credentials('docker-hub-nofarpanker')
        SNYK_TOKEN = credentials('SNYK_TOKEN')
        VENV_DIR = 'venv'
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

        stage('Install Dependencies') {
            steps {
                sh '''
                echo "=== יצירת Virtual Environment והתקנת תלותים ==="
                python3 -m venv $VENV_DIR
                source $VENV_DIR/bin/activate
                pip install --upgrade pip
                pip install -r environments-ci.txt
                '''
            }
        }

        stage('Lint Code') {
            steps {
                sh '''
                source $VENV_DIR/bin/activate
                flake8 .
                '''
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh '''
                source $VENV_DIR/bin/activate
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
                docker rm -f $(docker ps -aq) || true
                docker rmi -f nofarpanker/luxe-auth:latest || true
                docker rmi -f nofarpanker/luxe-backend:latest || true
                docker rmi -f nofarpanker/luxe-frontend:latest || true
                '''
            }
        }

        stage('Build & Push Services') {
            steps {
                sh '''
                echo "=== Build & Push Docker Images ==="
                docker build -t nofarpanker/luxe-auth:latest auth-service/
                docker build -t nofarpanker/luxe-backend:latest backend/
                docker build -t nofarpanker/luxe-frontend:latest frontend/

                echo $DOCKER_HUB_CRED_PSW | docker login -u $DOCKER_HUB_CRED_USR --password-stdin
                docker push nofarpanker/luxe-auth:latest
                docker push nofarpanker/luxe-backend:latest
                docker push nofarpanker/luxe-frontend:latest
                '''
            }
        }

        stage('Snyk Security Scan') {
            steps {
                sh '''
                source $VENV_DIR/bin/activate
                snyk container test nofarpanker/luxe-auth:latest --file=auth-service/Dockerfile
                snyk container test nofarpanker/luxe-backend:latest --file=backend/Dockerfile
                snyk container test nofarpanker/luxe-frontend:latest --file=frontend/Dockerfile
                '''
            }
        }
    }

    post {
        always {
            echo "ניקוי משאבים..."
            sh '''
            docker rmi -f nofarpanker/luxe-auth:latest || true
            docker rmi -f nofarpanker/luxe-backend:latest || true
            docker rmi -f nofarpanker/luxe-frontend:latest || true
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



