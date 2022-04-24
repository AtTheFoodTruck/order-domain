pipeline {
  agent any
  stages {
    stage('Build Gradle') {
      steps {
        sh '''sudo chmod 777 gradlew
./gradlew clean build --exclude-task test
'''
        sh 'ls'
      }
    }

    stage('Build Docker') {
      steps {
        sh 'ls'
        script {
          backend_order = docker.build("goalgoru/backend_order")
        }

      }
    }

    stage('Docker push') {
      steps {
        script {
          docker.withRegistry('https://registry.hub.docker.com/', registryCredential) {
            backend_order.push("latest")
            backend_order.push("${BUILD_NUMBER}")
          }
        }

      }
    }

    stage('docker-compose') {
      steps {
        sh 'cd /project && docker-compose up -d'
      }
    }

  }
  environment {
    registryCredential = 'dockerhub_cred'
  }
}
