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
                        [name: 'auth-service', dockerfile: 'infra/Dockerfile.auth', context: 'auth-service'],
                        [name: 'backend', dockerfile: 'infra/Dockerfile.backend', context: 'backend'],
                        [name: 'jewelry-store', dockerfile: 'infra/Dockerfile.frontend', context: 'jewelry-store']
                    ]

                    for (s in services) {
                        sh """
                        echo "=== Build & Push ${s.name} ==="
                        docker build -f ${s.dockerfile} -t ${s.name} ${s.context}
                        echo "${DOCKER_HUB_CRED_PSW}" | docker login -u "${DOCKER_HUB_CRED_USR}" --password-stdin
                        docker tag ${s.name} ${DOCKER_HUB_CRED_USR}/${s.name}:latest
                        docker push ${DOCKER_HUB_CRED_USR}/${s.name}:latest
                        docker logout
                        """
                    }
                }
            }
        }

        stage('Snyk Security Scan') {
            steps {
                sh '''
                echo "=== Running Snyk Scan ==="
                snyk container test ${DOCKER_HUB_CRED_USR}/auth-service:latest --org=my-org || true
                snyk container test ${DOCKER_HUB_CRED_USR}/backend:latest --org=my-org || true
                snyk container test ${DOCKER_HUB_CRED_USR}/jewelry-store:latest --org=my-org || true
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




