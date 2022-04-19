pipeline {
  agent any
  stages {
    stage('Build Gradle') {
      steps {
        sh '''sudo chmod 777 gradlew
./gradlew clean build --exclude-task test
'''
      }
    }

    stage('Build Docker') {
      steps {
        script {
          backend_user = docker.build("goalgoru/backend_user")
        }

      }
    }

    stage('Docker push') {
      steps {
        script {
          docker.withRegistry('https://registry.hub.docker.com/', registryCredential) {
            backend_user.push("latest")
            backend_user.push("${BUILD_NUMBER}")
          }
        }

      }
    }

  }
  environment {
    registryCredential = 'dockerhub_cred'
  }
}