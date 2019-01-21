import net.sf.json.JSONObject
import net.sf.json.JSONArray

def random

NOTIFICATION_USER = "Jenkins"
HIPCHAT_SERVER = "sme-apps.slack.com"

JENKINS_SLACK_CREDENTIALS = "slack-jenkins"

//
CHATOPS_STATUS_COLOR_MAP = ["STARTED":"GREEN", "UNSTABLE":"YELLOW", "FAILURE":"RED", "SUCCESS":"GREEN", "ABORTED":"GRAY"]

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


def notifyBuild(status) {
    def statusStr = status.toString()
//    def color = CHATOPS_STATUS_COLOR_MAP.containsKey(statusStr) ? CHATOPS_STATUS_COLOR_MAP.get(statusStr) : "PURPLE"
    def color = "#36A64F" 
    if(!params.containsKey("MUTE_HIPCHAT") || !params["MUTE_HIPCHAT"]) {
        if(!params.containsKey("SLACK_CHANNEL") || !params["SLACK_CHANNEL"]) {
            logStep "SLACK_CHANNEL parameter is not defined or empty. Please pass a parameter as SLACK_CHANNEL with appropriate channel value to the build job."
        } else {
            slackSend (
            color: "${color}", notify: true, credentialId: JENKINS_SLACK_CREDENTIALS,
            message: "${statusStr}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'", 
            channel: params.SLACK_CHANNEL,
//            sendAs: NOTIFICATION_USER, server: SLACK_SERVER, textFormat: true, v2enabled: true
            )
        }
    }
}

return this
