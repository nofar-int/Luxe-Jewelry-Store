pipeline {
    agent { label 'jenkins-agent' } 
    environment {
        SNYK_TOKEN = credentials('SNYK_TOKEN')
        PYTHONPATH = "${WORKSPACE}"
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
                node --version
                npm --version
                snyk --version
                flake8 --version || echo "flake8 לא מותקן, דלגתי"
                '''
            }
        }

        stage('Lint Code') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    sh '''
                    flake8 .
                    '''
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh '''
                mkdir -p reports
                pytest --html=reports/unit_test_report.html --self-contained-html
                '''
            }
        }

        stage('Publish HTML Report') {
            steps {
                publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'reports',
                    reportFiles: 'unit_test_report.html',
                    reportName: 'Unit Test Report'
                ])
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
                        [name: 'auth-service', dockerfile: 'infra/Dockerfile.auth', context: '.'],
                        [name: 'backend', dockerfile: 'infra/Dockerfile.backend', context: '.'],
                        [name: 'jewelry-store', dockerfile: 'infra/Dockerfile.frontend', context: '.']
                    ]

                    withCredentials([usernamePassword(credentialsId: 'docker-hub-nofarpanker', 
                                                     usernameVariable: 'DOCKER_USER', 
                                                     passwordVariable: 'DOCKER_PASS')]) {
                        sh 'docker login -u $DOCKER_USER -p $DOCKER_PASS'

                        for (s in services) {
                            sh """
                            echo "=== Build & Push ${s.name} ==="
                            docker build -f ${s.dockerfile} -t ${s.name} ${s.context}
                            docker tag ${s.name} $DOCKER_USER/${s.name}:latest
                            docker push $DOCKER_USER/${s.name}:latest
                            """
                        }
                    }
                }
            }
        }

        stage('Snyk Security Scan & Monitor') {
            steps {
                script {
                    def images = [
                        [name: 'auth-service', dockerfile: 'infra/Dockerfile.auth'],
                        [name: 'backend', dockerfile: 'infra/Dockerfile.backend'],
                        [name: 'jewelry-store', dockerfile: 'infra/Dockerfile.frontend']
                    ]

                    withCredentials([usernamePassword(credentialsId: 'docker-hub-nofarpanker', 
                                                     usernameVariable: 'DOCKER_USER', 
                                                     passwordVariable: 'DOCKER_PASS')]) {
                        for (img in images) {
                            sh """
                            echo "=== Snyk Test ${img.name} ==="
                            snyk container test $DOCKER_USER/${img.name}:latest \
                                --file=${img.dockerfile} \
                                --org=nofar-int \
                                --ignore-file=./snyk-ignore.txt || true

                            echo "=== Snyk Monitor ${img.name} ==="
                            snyk container monitor $DOCKER_USER/${img.name}:latest \
                                --file=${img.dockerfile} \
                                --org=nofar-int \
                                --ignore-file=./snyk-ignore.txt || true
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            sh '''
            echo "ניקוי משאבים סופי..."
            docker rm -f auth-service backend jewelry-store || true
            docker rmi -f auth-service backend jewelry-store || true
            docker logout || true
            '''
        }
        success {
            echo "✅ הבנייה והבדיקות הצליחו!"
        }
        unstable {
            echo "⚠️ הבדיקות נכשלו או יש אזהרות (Lint או Unit Tests) — בדקי את הדוחות"
        }
        failure {
            echo "❌ הבנייה נכשלה — בדקי את הלוגים בג׳נקינס"
        }
    }
}




