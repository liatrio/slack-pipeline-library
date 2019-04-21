#!/usr/bin/env groovy
import org.SlackPipeline.SlackPipeline

def call(sp) {

  def blue_ocean_url = "${env.JENKINS_URL}blue/rest/organizations/jenkins/pipelines"
  //def stages = sh(returnStdout: true, script: "curl --silent  -H 'Authorization: Bearer ${env.SLACK_TOKEN}'  ${blue_ocean_url}").trim() 

  //def payload = sp.sendPipelineFailure("${env.STAGE_NAME}")
  //def m = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${payload}\' ${env.SLACK_WEBHOOK_URL}/api/chat.update").trim() 

}
