pipeline{
    agent any
    tools{
        maven "LOCAL_MAVEN"
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
                deploy adapters: [tomcat9(credentialsId: '8219abb7-1b49-476a-b4b8-f82fcee3adda', path: '', url: 'http://localhost:8080/')], contextPath: null, war: '**/*.war'
            }
        }
    }
}