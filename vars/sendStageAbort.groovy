#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call(def Message) {
  Slack slack = new Slack()

  def payload = slack.sendStageAbort(Message, "${env.SLACK_ROOM}", Message.ts, "${env.BUILD_URL}")
  def m = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${payload}\' ${env.SLACK_WEBHOOK_URL}/api/chat.update").trim() 
  def json = readJSON text: m
  Message = json

  return Message
}

