pipeline{
    agent any
    tools{
        maven "Maven"
    }
    stages{
        stage('Build'){
            steps{
                sh 'mvn clean package'
            }
            post{
                success{
                    echo "Archiving the artifacts"
                archiveArtifacts artifacts: "**/target/*.war"
                }
            }
        }
        stage('Deploy To TomCatServer'){
            steps{
                deploy adapters: [tomcat9(credentialsId: '2b628e71-79b5-4c97-9e98-22b6bc8d839c', path: '', url: 'http://localhost:8080/')], contextPath: null, war: '**/*.war'
            }
        }
    }
}