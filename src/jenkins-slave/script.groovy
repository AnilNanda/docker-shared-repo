def commons =null

def runScript(){
	
	node("jenkins-ecs-slave"){
	try {
		stage('Setup'){
		  commons.cleanWs()
		  commons.checkoutGitFromSCM(scm)
	    	}
		stage('PackageImage'){
		  docker.build("${params.ECR_REPO_NAME}:${env.BUILD_ID}")
		  sh "echo Docker build completed successfully"
		}
		stage('Publish Image'){
		  docker.withRegistry("${params.ECR_HOST}","ecr:${params.ECR_REGION}:${ECR_CREDS}"){
		  	docker.image("${params.ECR_REPO_NAME}:${env.BUILD_ID}").push("${params.ECR_REPO_NAME}-${env.BUILD_ID}")
			docker.image("{params.ECR_REPO_NAME}:${env.BUILD_ID}").push("latest")
		}}

}
catch(error){
	throw error
}
finally{
	deleteDir()
}}}

return this