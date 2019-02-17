#!/usr/bin/env groovy
import org.Slack.Slack
import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput

def call(err, Message) {
  Slack slack = new Slack()

  Message.message.attachments.eachWithIndex { attachment, index ->
    if (attachment.text != '' && attachment.text != null){
      def name = attachment.text.replaceAll(": running", "")
      if ("${name}" == "${env.STAGE_NAME}"){
        def payload = slack.sendPipelineFailure(Message, "${env.SLACK_ROOM}", name, Message.ts, index, Message.message.attachments.size(), err)
        def m = sh(returnStdout: true, script: "curl --silent -X POST -H 'Authorization: Bearer ${env.SLACK_TOKEN}' -H \"Content-Type: application/json\" --data \'${payload}\' ${env.SLACK_WEBHOOK_URL}/api/chat.update").trim() 
        def json = readJSON text: m
        //def m = httpRequest validResponseCodes: '409,201,200', 
        //          customHeaders: [[name: "Authorization", value: "Bearer ${env.SLACK_TOKEN}"]], 
        //          consoleLogResponseBody: true, 
        //          acceptType: 'APPLICATION_JSON', 
        //          contentType: 'APPLICATION_JSON', 
        //          httpMode: 'POST', 
        //          requestBody: "${slackMessage}", 
        //          url: "${env.SLACK_WEBHOOK_URL}/api/chat.postMessage"
        //def json = readJSON text: m.content
        Message = json
      }
    }
  }

}
