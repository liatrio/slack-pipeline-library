#!/usr/bin/env groovy

package org.Slack.SlackPipeline
import groovy.json.JsonOutput



class SlackPipeline {

  def message
  def attachments = [:]

  public SlackPipeline(body) {
    def abort = "${body.buildURL}stop"
    def actions = [
      [text: "Abort", name: abort, value: abort, type: "button"]
    ]
    def fields = [
      [
        title: "Branch",
        value: "${body.branch}",
        short: true
      ],
      [
        title: "Last Commit",
        value: "${body.message}",
        short: true
      ]
    ]
    def header = [
      title:        "1 new commit to ${body.jobName}, build #${body.buildNumber}",
      title_link:   "${body.title_link}",
      color:        "primary",
      author_name:  "${body.user.user.name}",
      author_icon:  "${body.user.user.profile.image_192}",
      callback_id:  "stage_callback",
      actions:       actions,
      fields:        fields
    ]
    this.attachments.header = header
    for (int i = 0; i < body.stageNames.size(); i++){
      def stage = [
        color:  "primary",
        text:   ":not_started: ${body.stageNames[i]}: Not started"
      ]
      this.attachments["${body.stageNames[i]}"] = stage
    }
    def message = JsonOutput.toJson([
        channel: "${body.channel}",
        username: "Jenkins",
        as_user: true,
        attachments: this.attachments
    ])
    this.message = message
  }

  def getMessage() {
    return this.message
  }


  def sendStageSkipped(channel, name, ts, String s = null) {
    def stage

    if (s == null){
      stage = [
        color: "#5BA9EF",
        text: ":skipped: ${name}: skipped"
      ]
    }
    else {
      stage = [
        color: "#5BA9EF",
        text: ":skipped: ${name}: ${s}"
      ]
    }
    this.attachments["${name}"] = stage
    def payload = JsonOutput.toJson([
        ts: "${ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: this.attachments
    ])
    return payload
  }

  def sendStageAbort(Message, channel, ts, buildURL) {
    def attachments = []
    attachments.add(Message.message.attachments[0])
    def stage = [
      color: "#cccc00",
      "text": "<${buildURL}|Build has been aborted.>"
    ]
    attachments.add(stage)

    def payload = JsonOutput.toJson([
        ts: "${ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: attachments
    ])

    return payload
  }

  def sendStageRunning(channel, name, ts) {
    def stage = [
      color: "#cccc00",
      text: ":in_progress: ${name}: running"
    ]
    this.attachments["${name}"] = stage

    def payload = JsonOutput.toJson([
        ts: "${this.message.ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: this.attachments
    ])

    return payload
  }

  def sendStageInput(channel, name, ts, String s = null, id, buildURL) {
    def proceed = "${buildURL}input/${id}/proceedEmpty"
    def abort = "${buildURL}input/${id}/abort"
    def actions = [
      [text: "Proceed", name: proceed, value: proceed, type: "button"],
      [text: "Abort", name: abort, value: abort, type: "button"]
    ]

    def stage
    if (s != null){
      stage = [
        color: "#cccc00",
        callback_id: "stage_callback",
        text: ":in_progress: ${name}: ${s}",
        actions: actions
      ]
      this.attachments["${name}"] = stage
    }
    def payload = JsonOutput.toJson([
        ts: "${ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: this.attachments
    ])
    return payload
  }

  def sendStageSuccess(channel, name, ts, String s = null) {
    def attachments = []
    def stage

    if (s == null){
      stage = [
        color: "#45B254",
        text: ":passed: ${name}: passed!"
      ]
    }
    else {
      stage = [
        color: "#45B254",
        text: ":passed: ${name}: ${s}"
      ]
    }
    this.attachments["${name}"] = stage
    def payload = JsonOutput.toJson([
        ts: "${ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: this.attachments
    ])
    return payload
  }

  def sendPipelineFailure(channel, name, ts, log) {
    def stage = [
      color: "danger",
      text: ":failed: ${name}: failed```${log}```",
      mrkdwn_in: ["text"]
    ]
    this.attachments["${name}"] = stage
    def payload = JsonOutput.toJson([
        ts: "${ts}",
        channel: "${channel}",
        username: "Jenkins",
        as_user: true,
        attachments: this.attachments  
    ])

    return payload
  }
}

