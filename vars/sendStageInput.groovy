#!/usr/bin/env groovy
import org.SlackPipeline.SlackPipeline

def call(String s = null, String id, String buildURL) {

  def payload = slack.sendStageInput("${env.SLACK_ROOM}", "${env.STAGE_NAME}", Message.ts, s, id, buildURL)
  def m = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${payload}\' ${env.SLACK_WEBHOOK_URL}/api/chat.update").trim() 
  def json = readJSON text: m
  Message = json
  return Message
}

