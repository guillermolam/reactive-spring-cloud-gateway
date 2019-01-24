pipeline {
   
   agent any
   
    stages {
                // run unit tests
		        stage('TEST'){
		            steps{
						// setting up gradle wrapper as an exe
                		sh 'chmod +x gradlew'
                		sh './gradlew clean'
						// skipping test temporarily. Unit and integration tests are run separately.
                		sh './gradlew build -x test -DNEXUS_USERNAME=$DOCKER_NEXUS_CREDS_USR -DNEXUS_PASSWORD=$DOCKER_NEXUS_CREDS_PSW'
		                sh './gradlew test -Dspring.profiles.active=test'
		            }
		        }
                 // run static code analysis without tests. Tests are run parallel above and not needed again
		        stage('STATIC ANALYSIS'){
		            steps{
		                sh './gradlew sonarqube -Dsonar.host.url=http://mdv-docdevl01:9000 -x test '
		            }
		    
              }

        // creating runnable jar
        stage('PUBLISH JAR'){
           environment {
				DOCKER_NEXUS_CREDS = credentials('nexus')
            }
            steps{
                sh './gradlew bootJar -x test'
                sh './gradlew uploadBootArchives -x test -DNEXUS_USERNAME=$DOCKER_NEXUS_CREDS_USR -DNEXUS_PASSWORD=$DOCKER_NEXUS_CREDS_PSW'
            }
        }

		stage("DEV"){
			steps{
				pushToCloudFoundry(
    				target: '${DEV_PCF_HOST}',
    				organization: '${DEV_PCF_ORG}',
    				cloudSpace: '${DEV_PCF_SPACE}',
    				credentialsId: '${DEV_SPACE_CREDENTIALS}',
    				pluginTimeout: '240', // default value is 120
    				manifestChoice: [ // optional... defaults to manifestFile: manifest.yml
        				manifestFile: 'manifest.yml'
    				]
				)
			}
		}
    }
}

