import net.sf.json.JSONObject
import net.sf.json.JSONArray

def random


def getRandom(){
if (random){
	return random
} else{
	random= new Random()
	return random
}
}

def createDir(dirName){
	sh "mkdir -p ${dirName}"
}


def setAWSRegion(region) {

sh "aws configure set region ${region}"
}

def cleanWs(){
	echo "Clean workspace at ${pwd()}"
	sh "rm -rf ${pwd()}/*"
}


def cleanUpDockerImages() {
	echo "Clean un-used docker images"
	sh "docker system prune -af"
}

def checkoutGitFromScm(scm){
	checkout changelog: true, poll: true,
	scm: [
		$class: 'GitSCM',
		branches: scm.branches,            
		doGenerateSubmoduleConfigurations: scm.doGenerateSubmoduleConfigurations,            
		extensions: scm.extensions,
            userRemoteConfigs: scm.userRemoteConfigs
        ]
}


return this
