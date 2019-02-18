#!/usr/bin/env groovy
import org.SlackPipeline.SlackPipeline

def call(sp) {

  def payload = sp.sendStageRunning("${env.STAGE_NAME}")
  def m = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${payload}\' ${env.SLACK_WEBHOOK_URL}/api/chat.update").trim() 
  def json = readJSON text: m

  sp.response = json
}

