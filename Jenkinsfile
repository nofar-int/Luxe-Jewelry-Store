pipeline {
    agent any
    stages {
        stage('Build app') {
            steps {
                withCredentials([usernamePassword(credentialsId: '<ID שלך>', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        docker login -u $DOCKER_USER -p $DOCKER_PASS
                        docker build -t nofarpanker/luxe-jewelry-store-auth:latest ./auth
                        docker push nofarpanker/luxe-jewelry-store-auth:latest
                        docker build -t nofarpanker/luxe-jewelry-store-backend:latest ./backend
                        docker push nofarpanker/luxe-jewelry-store-backend:latest
                        docker build -t nofarpanker/luxe-jewelry-store-front:latest ./front
                        docker push nofarpanker/luxe-jewelry-store-front:latest
                    '''
                }
            }
        }
    }
}
