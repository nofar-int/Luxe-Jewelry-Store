pipeline {
    agent {
        docker {
            image 'nofarpanker/jenkins-agent:latest'
            args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    stages {
        stage('Build app') {
            steps {
                sh '''
                   docker login --username nofarpanker --password-stdin <<EOF
                   <PASTE_YOUR_TOKEN_HERE>
                   EOF
                   docker build -t nofarpanker/my-app:latest .
                   docker push nofarpanker/my-app:latest
                '''
            }
        }
    }
    options {
        buildDiscarder(daysToKeepStr: '30')
        disableConcurrentBuilds()
        timestamps()
    }
    post {
        always {
            sh 'docker image prune -f'
        }
    }
}
