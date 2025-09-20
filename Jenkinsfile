pipeline {
    agent any

    environment {
        // כאן אפשר להגדיר משתנים כלליים אם צריך
        WORKSPACE_PATH = "${env.WORKSPACE}"
    }

    stages {

        stage('Prepare Workspace') {
            steps {
                script {
                    // מוסיפים את תיקיית העבודה ל-safe.directory
                    sh "git config --global --add safe.directory ${WORKSPACE_PATH}"
                    echo "Configured safe.directory for Git"
                }
            }
        }

        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh '''
                    npm install
                    npm run build
                    '''
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend-service') {
                    sh '''
                    pip install -r requirements.txt
                    # לדוגמה: להריץ build או migrations אם צריך
                    '''
                }
            }
        }

        stage('Run Tests') {
            steps {
                dir('backend-service') {
                    sh '''
                    # להריץ את הבדיקות
                    pytest
                    '''
                }
            }
        }

        stage('Post Actions') {
            steps {
                echo "Pipeline finished successfully!"
            }
        }
    }

    post {
        always {
            echo "Cleaning up workspace..."
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}



