#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call() {


  def jenkinsfile   =  readFile file: "Jenkinsfile"
  def stageNames    =  getStageNames(jenkinsfile)

  def commit        =  sh(returnStdout: true, script: 'git rev-parse HEAD')
  def author        =  sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
  def message       =  sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim() 
  def user          =  getSlackUser()

  body = [
    author:            "${author}",
    branch:            "${env.BRANCH_NAME}",
    buildURL:          "${env.BUILD_URL}",
    buildNumber:       "${env.BUILD_NUMBER}",
    channel:           "${env.SLACK_ROOM}",
    jobName:           "${scm.getUserRemoteConfigs()[0].getUrl().tokenize('/')[4].split("\\.")[0]}",
    message:           "${message}",
    stageNames:         stageNames,
    slackURL:          "${env.SLACK_WEBHOOK_URL}",
    title_link:        "${scm.getUserRemoteConfigs()[0].getUrl()}",
    slack_token:       "${env.SLACK_TOKEN}",
    slack_webhook_url: "${env.SLACK_WEBHOOK_URL}",
    user:               user
  ]
  
  SlackPipeline sp = new SlackPipeline(body)
  def response = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${sp.getMessage()}\' ${env.SLACK_WEBHOOK_URL}/api/chat.postMessage").trim()
  def responseJSON = readJSON text: response
  return responseJSON 
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
