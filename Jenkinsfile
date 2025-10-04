pipeline {
    agent {
        docker { 
            image 'nofarpanker/jenkins-agent:latest'
            args '-v /var/run/docker.sock:/var/run/docker.sock -v $PWD:/home/jenkins/workspace -w /home/jenkins/workspace'
        }
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
                python3 --version || echo "Python לא מותקן ב-Agent (ישתמש ב-Docker בעת הריצה)"
                pip3 --version || echo "pip לא מותקן ב-Agent (ישתמש ב-Docker בעת הריצה)"
                snyk --version || echo "Snyk לא מותקן"
                '''
            }
        }

        stage('Lint Code') {
            steps {
                sh '''
                docker run --rm -v $PWD:/app -w /app python:3.11 bash -c "pip install flake8 && flake8 ."
                '''
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh '''
                docker run --rm -v $PWD:/app -w /app python:3.11 bash -c "pip install -r environments-ci.txt pytest pytest-html && pytest --junitxml=results.xml --html=report.html tests/"
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





