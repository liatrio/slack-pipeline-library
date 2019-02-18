#!/usr/bin/env groovy
import org.SlackPipeline.SlackPipeline

def call(def Message, String s = null) {

  def payload = slack.sendStageSkipped("${env.SLACK_ROOM}", "${env.STAGE_NAME}", Message.ts, s)
  def m = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${payload}\' ${env.SLACK_WEBHOOK_URL}/api/chat.update").trim() 
  def json = readJSON text: m
  Message = json
  return Message
}


