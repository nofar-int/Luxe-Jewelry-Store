/* 
 * שימוש ב-Shared Library שהוגדרה בג'נקינס תחת "Global Pipeline Libraries"
 * שם הספרייה ב-@Library צריך להיות בדיוק כמו שהגדרת בג'נקינס UI
 */
@Library('jenkins-shared-library') _

pipeline {
    agent { label 'jenkins-agent' }  /* מציין שעל הפייפליין לרוץ על agent בשם jenkins-agent */

    environment {
        /* משתני סביבה גלובליים — כאן נשמר טוקן של Snyk ו-PYTHONPATH */
        SNYK_TOKEN = credentials('SNYK_TOKEN')
        PYTHONPATH = "${WORKSPACE}"
    }

    stages {

        /* === שלב ראשון: משיכת קוד מה-SCM (GitHub) === */
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        /* === שלב שני: בדיקת סביבת עבודה והתקנות בסיסיות === */
        stage('Prepare Environment') {
            steps {
                sh '''
                    echo "=== בדיקת התקנות בסיסיות ==="
                    docker --version
                    docker-compose version || true
                    git --version
                    python3 --version
                    pip3 --version
                    node --version
                    npm --version
                    snyk --version
                '''
            }
        }

        /* === שלב שלישי: בדיקות סטטיות והרצת טסטים === */
        stage('Static Analysis') {
            parallel {

                /* תת-שלב ראשון: בדיקת איכות קוד עם pylint */
                stage('🔍 Static Code Linting (Pylint)') {
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                            script {
                                echo "=== Running Pylint ==="
                                sh 'mkdir -p reports/pylint'

                                /* קריאה לפונקציה מה-shared library */
                                lintPython(
                                    "auth-service/*.py backend/*.py jewelry-store/*.py",
                                    "reports/pylint/pylint_report.txt"
                                )
                            }
                        }
                    }
                }

                /* תת-שלב שני: הרצת Unit Tests עם pytest */
                stage('🧪 Unit Tests (Pytest)') {
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                            script {
                                echo "=== Running Unit Tests ==="
                                sh 'mkdir -p reports'

                                /* קריאה לפונקציה מה-shared library להרצת pytest ויצירת דוח HTML */
                                runPytest("reports/unit_test_report.html")
                            }
                        }
                    }
                }
            }
        }

        /* === שלב רביעי: פרסום דוחות בדפדפן Jenkins === */
        stage('Publish HTML Reports') {
            steps {
                script {
                    /* שימוש בפונקציה מה-shared library, אם קיימת */
                    publishReports('reports/pylint/pylint_report.txt', 'Pylint Report')
                    publishReports('reports/unit_test_report.html', 'Unit Test Report')
                }
            }
        }

        /* === שלב חמישי: ניקוי קונטיינרים ותמונות ישנות לפני בנייה חדשה === */
        stage('Clean Old Containers & Images') {
            steps {
                sh '''
                    docker-compose down || true
                    docker rm -f auth-service backend-service jewelry-store || true
                    docker rmi -f auth-service backend-service jewelry-store || true
                '''
            }
        }

        /* === שלב שישי: בנייה ודחיפת תמונות ל-Docker Hub === */
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

        /* === שלב שביעי: סריקות אבטחה עם Snyk === */
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

        /* === שלב שמיני: Deploy של האפליקציה בעזרת Docker Compose === */
        stage('Deploy App (via Docker Compose)') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'nexus-docker-credentials',
                                                     usernameVariable: 'NEXUS_USER',
                                                     passwordVariable: 'NEXUS_PASS')]) {
                        sh '''
                            echo "=== Logging into Nexus Registry ==="
                            docker login localhost:5000 -u $NEXUS_USER -p $NEXUS_PASS

                            echo "=== Deploying stack using Docker-Compose ==="
                            docker-compose down || true
                            docker-compose build
                            docker-compose up -d

                            echo "=== Containers currently running ==="
                            docker ps
                        '''
                    }
                }
            }
        }
    }

    /* === שלב סופי: פעולות post המופעלות תמיד === */
    post {
        always {
            sh '''
                echo "ניקוי משאבים סופי..."
                docker logout || true
            '''
        }
        success {
            echo "✅ כל השלבים הושלמו בהצלחה (כולל Deploy דרך Docker-Compose)!"
        }
        unstable {
            echo "⚠️ יש אזהרות או כשלונות (Lint/Unit Tests) — בדקי את הדוחות"
        }
        failure {
            echo "❌ הבנייה נכשלה — בדקי את הלוגים בג׳נקינס"
        }
    }
}
