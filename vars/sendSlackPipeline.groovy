#!/usr/bin/env groovy
import org.SlackPipeline.SlackPipeline

def call() {



  def jenkinsfile   =  readFile file: "Jenkinsfile"
  def stageNames    =  getStageNames(jenkinsfile)

  def commit        =  sh(returnStdout: true, script: 'git rev-parse HEAD')
  def author        =  sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
  def message       =  sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim() 
  //def slackUser     =  getUser()
  def jobName       = env.JOB_NAME.split('/')
  //def author_name   = "";
  //def author_icon   = "";
  jobName           = jobName[jobName.length-2]

  //author_name = "${slackUser.user.name}";
  //author_icon = "${slackUser.user.profile.image_192}";
  //if (slackUser.ok == "true"){
  //author_name = "${body.user.user.name}";
  //author_icon = "${body.user.user.profile.image_192}";
  //}
  //else {
  //  author_name = "Unknown";
  //  author_icon = "";
  //}
  body = [
    author:            "Jenkins",
    branch:            "${env.GIT_BRANCH}",
    buildURL:          "${env.BUILD_URL}",
    buildNumber:       "${env.BUILD_NUMBER}",
    channel:           "${env.SLACK_ROOM}",
    jobName:           "${jobName}",
    message:           "${message}",
    stageNames:         stageNames,
    slackURL:          "${env.SLACK_WEBHOOK_URL}",
    title_link:        "${scm.getUserRemoteConfigs()[0].getUrl()}",
    footer_url:        "${env.BUILD_URL}console",
    slack_token:       "${env.SLACK_TOKEN}",
    slack_webhook_url: "${env.SLACK_WEBHOOK_URL}",
    author_name:       "Jenkins job - ${jobName} building at ${env.BUILD_URL}",
    //author_icon:       "${author_icon}"
  ]
  
  SlackPipeline sp = new SlackPipeline(body)
  def response = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${sp.message}\' ${env.SLACK_WEBHOOK_URL}/api/chat.postMessage").trim()
  def responseJSON = readJSON text: response
  sp.response = responseJSON
  return sp
}

def getStageNames(jenkinsfile){

  def names = []
  def lines = jenkinsfile.readLines()

  for (int i = 0; i < lines.size(); i++){
    def line = lines[i]
    if (line.trim().size() == 0){}
    else {
      if (line.contains("stage(\'")){
        String [] tokens = line.split("\'");
        String stage = tokens[1]; 
        names.add(stage)
      }
      else if (line.contains("stage(\"")){
        String [] tokens = line.split("\"");
        String stage = tokens[1]; 
        names.add(stage)
      }
    }
  }
  return names
}
