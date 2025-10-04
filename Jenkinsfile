pipeline {
    agent { label 'jenkins-agent' }

    environment {
        DOCKER_HUB_CRED = credentials('docker-hub-nofarpanker')
        SNYK_TOKEN      = credentials('SNYK_TOKEN')
        SNYK_IGNORE_DIR = "security/snyk"
    }

    stages {

        stage('Prepare Environment') {
            steps {
                sh '''
                    echo "=== בדיקת התקנות בסיסיות ==="
                    docker --version
                    git --version
                    python3 --version
                    pip3 --version
                    snyk --version || true
                '''
            }
        }

        stage('Checkout') {
            steps {
                git url: 'https://github.com/nofar-int/Luxe-Jewelry-Store.git', branch: 'main'
            }
        }

        stage('Install Dependencies') {
            steps {
                sh '''
                    echo "=== מתקין תלויות CI/Testing ==="
                    pip install --upgrade pip
                    pip install -r requirements-ci.txt
                '''
            }
        }

        stage('Lint Code') {
            steps {
                sh '''
                    echo "=== מבצע ניתוח קוד עם pylint ==="
                    pylint $(find backend -name "*.py") $(find auth-service -name "*.py") || true
                '''
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh '''
                    echo "=== מריץ בדיקות יחידה (pytest) ==="
                    mkdir -p reports
                    pytest --junitxml=reports/results.xml --html=reports/report.html tests/ || true
                '''
            }
        }

        stage('Clean Old Containers & Images') {
            steps {
                sh '''
                    echo "ניקוי קונטיינרים ותמונות ישנות..."
                    docker ps -aq --filter "name=luxe-" | xargs -r -I{} docker rm -f {} || true
                    docker images "nofarpanker/luxe-*" -q | xargs -r docker rmi -f || true
                '''
            }
        }

        stage('Build & Push Services') {
            steps {
                sh '''
                    echo "=== בונה ומעלה auth-service ==="
                    docker build --pull --no-cache -t nofarpanker/luxe-auth:latest -f infra/Dockerfile.auth .

                    echo "=== בונה ומעלה backend-service ==="
                    docker build --pull --no-cache -t nofarpanker/luxe-backend:latest -f infra/Dockerfile.backend .

                    echo "=== בונה ומעלה frontend-service ==="
                    docker build --pull --no-cache -t nofarpanker/luxe-frontend:latest -f infra/Dockerfile.frontend .

                    echo "=== העלאת התמונות ל-Docker Hub ==="
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
                    echo "מריץ סריקות אבטחה באמצעות Snyk..."

                    for service in auth backend frontend; do
                        DOCKER_IMAGE="nofarpanker/luxe-${service}:latest"
                        DOCKER_FILE="infra/Dockerfile.${service}"
                        IGNORE_FILE="$SNYK_IGNORE_DIR/snyk-${service}-ignore.txt"

                        echo "== סריקת $service =="

                        if [ -f "$IGNORE_FILE" ]; then
                            echo "קובץ Ignore נמצא: $IGNORE_FILE"
                            snyk container test $DOCKER_IMAGE --file=$DOCKER_FILE --ignore-policy=$IGNORE_FILE || true
                        else
                            snyk container test $DOCKER_IMAGE --file=$DOCKER_FILE || true
                        fi
                    done
                '''
            }
        }
    }

    post {
        always {
            echo "ניקוי משאבים..."
            sh '''
                docker rmi nofarpanker/luxe-auth:latest || true
                docker rmi nofarpanker/luxe-backend:latest || true
                docker rmi nofarpanker/luxe-frontend:latest || true
            '''
            junit 'reports/results.xml'
            publishHTML(target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'reports',
                reportFiles: 'report.html',
                reportName: 'Pytest Report'
            ])
        }
    }
}



