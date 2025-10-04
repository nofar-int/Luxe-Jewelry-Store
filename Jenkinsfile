pipeline {
    agent { label 'jenkins-agent' }
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
                snyk --version
                '''
            }
        }

        stage('Lint Code') {
            steps {
                sh '''
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
                sh 'python3 -m unittest discover'
            }
        }

        stage('Clean Old Containers & Images') {
            steps {
                sh '''
                docker rm -f auth-service backend jewelry-store || true
                docker rmi -f auth-service backend jewelry-store || true
                '''
            }
        }

        stage('Build & Push Services') {
            steps {
                script {
                    def services = [
                        [name: 'auth-service', dockerfile: 'Luxe-Jewelry-Store/infra/Dockerfile.auth'],
                        [name: 'backend', dockerfile: 'Luxe-Jewelry-Store/infra/Dockerfile.backend'],
                        [name: 'jewelry-store', dockerfile: 'Luxe-Jewelry-Store/infra/Dockerfile.frontend']
                    ]

                    for (s in services) {
                        sh """
                        echo "=== Build & Push ${s.name} ==="
                        docker build -f ${s.dockerfile} -t ${s.name} ./Luxe-Jewelry-Store
                        docker tag ${s.name} ${DOCKER_HUB_CRED}/${s.name}:latest
                        docker login -u ${DOCKER_HUB_CRED_USR} -p ${DOCKER_HUB_CRED_PSW}
                        docker push ${DOCKER_HUB_CRED}/${s.name}:latest
                        """
                    }
                }
            }
        }

        stage('Snyk Security Scan') {
            steps {
                sh '''
                echo "=== Running Snyk Scan ==="
                snyk container test ${DOCKER_HUB_CRED}/auth-service:latest --org=my-org || true
                snyk container test ${DOCKER_HUB_CRED}/backend:latest --org=my-org || true
                snyk container test ${DOCKER_HUB_CRED}/jewelry-store:latest --org=my-org || true
                '''
            }
        }
    }

    post {
        always {
            sh '''
            echo "ניקוי משאבים סופי..."
            docker rm -f auth-service backend jewelry-store || true
            docker rmi -f auth-service backend jewelry-store || true
            '''
        }
        success {
            echo "✅ הבנייה הצליחה!"
        }
        failure {
            echo "❌ הבנייה נכשלה — בדקי את הלוגים בג׳נקינס"
        }
    }
}




