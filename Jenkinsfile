pipeline {
    agent { label 'jenkins-agent' }

    environment {
        FRONT_IMAGE = 'luxe-jewelry-store-front'
        BACK_IMAGE = 'luxe-jewelry-store-backend'
        AUTH_IMAGE = 'luxe-jewelry-store-auth'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out the code...'
                checkout scm
            }
        }

        stage('Build Frontend Docker Image') {
            steps {
                echo 'Building Frontend Docker image...'
                sh 'docker build -t $FRONT_IMAGE ./jewelry-store'
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                echo 'Building Backend Docker image...'
                sh 'docker build -t $BACK_IMAGE ./backend'
            }
        }

        stage('Build Auth Docker Image') {
            steps {
                echo 'Building Auth Docker image...'
