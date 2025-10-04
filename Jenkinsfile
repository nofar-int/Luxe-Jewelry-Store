pipeline {
    agent { label 'jenkins-agent' } // אג'נט שלנו

    environment {
        DOCKER_HUB_CRED = credentials('docker-hub-username')
        DOCKER_HUB_CRED_PSW = credentials('docker-hub-password')
        SNYK_TOKEN = credentials('snyk-api-token')
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
                    echo "=== הרצת flake8 ==="
                    flake8 .
                '''
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh '''
                    echo "=== הרצת Unit Tests ==="
                    pytest
                '''
            }
        }

        stage('Build & Push Services') {
            steps {
                script {
                    def services = ['auth', 'backend', 'jewelry-store']
                    for (service in services) {
                        sh """
                            docker build -t luxe-jewelry-store-${service} ./${service}
                            docker tag luxe-jewelry-store-${service} nofarpanker/luxe-jewelry-store-${service}:latest
                            docker push nofarpanker/luxe-jewelry-store-${service}:latest
                        """
                    }
                }
            }
        }

        stage('Snyk Security Scan') {
            steps {
                sh '''
                    echo "=== בדיקת אבטחה עם Snyk ==="
                    snyk test --docker nofarpanker/luxe-jewelry-store-auth:latest
                    snyk test --docker nofarpanker/luxe-jewelry-store-backend:latest
                    snyk test --docker nofarpanker/luxe-jewelry-store-jewelry-store:latest
                '''
            }
        }
    }

    post {
        always {
            echo 'ניקוי משאבים סופי...'
            sh '''
                docker rmi -f nofarpanker/luxe-jewelry-store-auth || true
                docker rmi -f nofarpanker/luxe-jewelry-store-backend || true
                docker rmi -f nofarpanker/luxe-jewelry-store-jewelry-store || true
            '''
        }

        failure {
            echo '❌ הבנייה נכשלה — בדקי את הלוגים בג׳נקינס'
        }

        success {
            echo '✅ הבנייה הושלמה בהצלחה!'
        }
    }
}





