#!/usr/bin/env groovy
import org.SlackPipeline.SlackPipeline

def call(sp) {

  def payload = sp.sendPipelineFailure("${env.STAGE_NAME}", "${env.BUILD_URL}")
  def m = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${payload}\' ${env.SLACK_WEBHOOK_URL}/api/chat.update").trim() 
  def json = readJSON text: m

  sp.response = json
}
