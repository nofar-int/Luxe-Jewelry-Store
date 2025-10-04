pipeline {
    agent any

    environment {
        DOCKER_HUB_CRED = credentials('docker-hub-credentials')
        SNYK_TOKEN = credentials(' SNYK_TOKEN')
        DOCKER_USER = 'nofarpanker'
    }

    stages {

        stage('Prepare Environment') {
            steps {
                echo "=== בדיקת התקנות בסיסיות ==="
                sh '''
                    docker --version
                    git --version
                    python3 --version
                    pip3 --version
                    snyk --version
                '''
            }
        }

        stage('Checkout') {
            steps {
                git branch: 'main', credentialsId: 'github-credentials-id', url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git'
            }
        }

        stage('Install Dependencies') {
            steps {
                echo "=== מתקין תלויות CI/Testing ==="
                sh '''
                    set -e
                    python3 -m venv venv
                    . venv/bin/activate
                    pip install --upgrade pip
                    pip install -r requirements-ci.txt
                '''
            }
        }

        stage('Lint Code') {
            steps {
                echo "=== מריץ בדיקות Lint ==="
                sh '''
                    . venv/bin/activate
                    pylint backend auth-service || true
                '''
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo "=== מריץ בדיקות יחידה ==="
                sh '''
                    . venv/bin/activate
                    pytest --junitxml=test-results.xml --maxfail=1 --disable-warnings -q
                '''
            }
            post {
                always {
                    junit 'test-results.xml'
                }
            }
        }

        stage('Clean Old Containers & Images') {
            steps {
                echo "=== מנקה תמונות ישנות ==="
                sh '''
                    docker ps -a -q | xargs -r docker rm -f || true
                    docker images -q "nofarpanker/luxe-*" | xargs -r docker rmi -f || true
                '''
            }
        }

        stage('Build & Push Services') {
            steps {
                echo "=== בונה ומעלה Docker Images ==="
                sh '''
                    docker build -t ${DOCKER_USER}/luxe-frontend:latest -f frontend/Dockerfile .
                    docker build -t ${DOCKER_USER}/luxe-backend:latest -f backend/Dockerfile .
                    docker build -t ${DOCKER_USER}/luxe-auth:latest -f auth-service/Dockerfile .

                    echo "${DOCKER_HUB_CRED_PSW}" | docker login -u "${DOCKER_USER}" --password-stdin
                    docker push ${DOCKER_USER}/luxe-frontend:latest
                    docker push ${DOCKER_USER}/luxe-backend:latest
                    docker push ${DOCKER_USER}/luxe-auth:latest
                '''
            }
        }

        stage('Snyk Security Scan') {
            steps {
                echo "=== מבצע סריקת אבטחה עם Snyk ==="
                sh '''
                    snyk auth ${SNYK_TOKEN}
                    snyk container test ${DOCKER_USER}/luxe-backend:latest || true
                '''
            }
        }
    }

    post {
        always {
            echo "ניקוי משאבים..."
            sh '''
                docker rmi ${DOCKER_USER}/luxe-auth:latest || true
                docker rmi ${DOCKER_USER}/luxe-backend:latest || true
                docker rmi ${DOCKER_USER}/luxe-frontend:latest || true
            '''
        }
        failure {
            echo "❌ הבנייה נכשלה — בדקי את הלוגים בג׳נקינס"
        }
        success {
            echo "✅ כל השלבים הושלמו בהצלחה!"
        }
    }
}


