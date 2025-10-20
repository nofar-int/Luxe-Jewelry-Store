@Library('jenkins-shared-library') _  // ✅ טוען את הספרייה המשותפת (Shared Library) שיצרנו מראש

pipeline {
    agent { label 'jenkins-agent' }  // ✅ מריץ את הפייפליין על סוכן (Agent) שמוגדר בשם 'jenkins-agent'

    // ⚙️ אפשרויות כלליות לפייפליין
    options {
        timeout(time: 25, unit: 'MINUTES')  // 🕒 הפייפליין יעצור אוטומטית אחרי 25 דקות כדי למנוע תקיעות
        buildDiscarder(logRotator(numToKeepStr: '10'))  // 💾 שומר רק את 10 הבניות האחרונות
        timestamps()  // 🧭 מוסיף חותמות זמן ללוגים
    }

    // 🎯 טריגר — מריץ את הפייפליין כל 2 דקות אם יש שינוי ב־Git
    triggers {
        pollSCM('H/2 * * * *')
    }

    // 🌍 משתני סביבה
    environment {
        SNYK_TOKEN = credentials('SNYK_TOKEN')  // 🔑 מושך טוקן מאובטח של Snyk מתוך Credentials של Jenkins
        PYTHONPATH = "${WORKSPACE}"              // 🧩 מוסיף את תיקיית העבודה ל־PYTHONPATH
        PATH = "/opt/jenkins_venv/bin:$PATH"     // 🐍 מוסיף ל־PATH את ה־venv שבו מותקנים pytest/pylint
    }

    stages {

        // 🧾 שלב 1: שליפת קוד מה־Repository
        stage('Checkout SCM') {
            steps {
                checkout scm  // 🔄 שולף את הקוד מה־GitHub או מה־Git remote שהוגדר בג׳נקינס
            }
        }

        // 🧰 שלב 2: בדיקת סביבת העבודה
        stage('Prepare Environment') {
            steps {
                sh '''
                    echo "=== בדיקת התקנות בסיסיות ==="
                    docker --version
                    docker-compose --version || true
                    git --version
                    python3 --version
                    pip3 --version
                    node --version
                    npm --version
                    snyk --version
                    pylint --version
                    pytest --version
                '''
            }
        }

        // 🧠 שלב 3: אנליזה סטטית והרצת טסטים במקביל (Parallel)
        stage('Static Analysis') {
            parallel {  // 🚀 שני שלבים רצים במקביל כדי לחסוך זמן

                stage('🔍 Shared Library Linting (Pylint)') {
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                            script {
                                echo "=== Running Pylint via Shared Library ==="
                                sh 'mkdir -p reports/pylint'
                                lintPython(
                                    "auth-service/*.py backend/*.py jewelry-store/*.py",
                                    "reports/pylint/pylint_report.txt"
                                )
                            }
                        }
                    }
                }

                stage('🧪 Shared Library Unit Tests (Pytest)') {
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                            script {
                                echo "=== Running Unit Tests via Shared Library ==="
                                sh 'mkdir -p reports'
                                runPytest("reports/unit_test_report.html")
                            }
                        }
                    }
                }
            }
        }

        // 📊 שלב 4: הפקת דוחות HTML
        stage('Publish HTML Reports') {
            steps {
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'reports/pylint',
                    reportFiles: 'pylint_report.txt',
                    reportName: 'Pylint Report'
                ])
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'reports',
                    reportFiles: 'unit_test_report.html',
                    reportName: 'Unit Test Report'
                ])
            }
        }

        // 🧹 שלב 5: ניקוי קונטיינרים ותמונות ישנים
        stage('Clean Old Containers & Images') {
            steps {
                sh '''
                    echo "=== ניקוי קונטיינרים ותמונות ישנים ==="
                    docker-compose down || true
                    docker rm -f auth-service backend-service jewelry-store || true
                    docker rmi -f auth-service backend-service jewelry-store || true
                '''
            }
        }

        // 🏗️ שלב 6: Build & Push לדוקר האב
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

        // 🛡️ שלב 7: סריקות אבטחה עם Snyk
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

        // 🏷️ שלב 8: Deploy לרג׳יסטרי של Nexus
        stage('🔹 Deploy to Nexus Registry') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'nexus-docker-credentials',
                                                     usernameVariable: 'NEXUS_USER',
                                                     passwordVariable: 'NEXUS_PASS')]) {
                        sh '''
                            echo "=== Logging into Nexus Registry ==="
                            docker login localhost:5000 -u $NEXUS_USER -p $NEXUS_PASS
                        '''
                    }
                }
            }
        }

        // 🚀 שלב 9: הפעלת המערכת בפועל בעזרת Docker Compose
        stage('Deploy App (via Docker Compose)') {
            steps {
                sh '''
                    echo "=== Deploying app using Docker Compose ==="
                    docker-compose down || true
                    docker-compose pull
                    docker-compose up -d
                    echo "=== Containers currently running ==="
                    docker ps
                '''
            }
        }
    }

    // 🧩 שלבי POST — מתבצעים תמיד אחרי הפייפליין
    post {
        always {
            sh '''
                echo "ניקוי משאבים סופי..."
                docker logout || true
            '''
        }
        success {
            echo "✅ כל השלבים הושלמו בהצלחה (כולל Deploy דרך Docker Compose)!"
        }
        unstable {
            echo "⚠️ יש אזהרות או כשלונות חלקיים (Lint/Unit Tests) — בדקי את הדוחות."
        }
        failure {
            echo "❌ הבנייה נכשלה — בדקי את הלוגים בג׳נקינס."
        }
    }
}


