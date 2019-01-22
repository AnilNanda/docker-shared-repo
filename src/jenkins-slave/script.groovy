def runScript(){
	
	node("jenkins-ecs-slave-base"){
	try {
                commons.notifyBuild('STARTED')
		stage('Setup'){
		  commons.cleanWs()
		  commons.checkoutGitFromScm(scm)
		  sleep(60)
	    	}
		stage('PackageImage'){
		  docker.build("${params.ECR_REPO_NAME}:${env.BUILD_ID}")
		  sh "echo Docker build completed successfully"
		}
		stage('Publish Image'){
		  docker.withRegistry("${params.ECR_HOST}","ecr:${params.ECR_REGION}:${ECR_CREDS}"){
		  	docker.image("${params.ECR_REPO_NAME}:${env.BUILD_ID}").push("${params.ECR_REPO_NAME}-${env.BUILD_ID}")
			docker.image("${params.ECR_REPO_NAME}:${env.BUILD_ID}").push("latest")
		}}
//                stage('Notify'){
//                  commons.notifyBuild('SUCCESS')
		            if(currentBuild.result == null) {
                currentBuild.setResult("SUCCESS")
                }

}
catch(error){
	     if(currentBuild.result == null) {
                currentBuild.setResult("FAILURE")
            }
	throw error
}
finally{
	deleteDir()
        commons.notifyBuild(currentBuild.result)
}}}

return this
