#!/usr/bin/env groovy

package org.SlackPipeline
import groovy.json.JsonOutput



class SlackPipeline {

  def message
  def response
  def channel
  def attachments = [:]

  public SlackPipeline(body) {
    /*def abort = "${body.buildURL}stop"
    def actions = [
      [text: "Abort", name: abort, value: abort, type: "button"]
    ]
    */
    def fields = [
      [
        title: "Branch",
        value: "${body.branch}, #${body.buildNumber}",
        short: true
      ],
      [
        title: "Last Commit",
        value: "${body.message}",
        short: true
      ]
    ]
    def header = [
      title:        "1 new commit to ${body.jobName}",
      title_link:   "${body.title_link}",
      color:        "primary",
      author_name:  "${body.author_name}",
      //author_icon:  "${body.author_icon}",
      footer:       "<${body.footer_url}|Jenkins Build log>",
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
    def stages = []
    for (val in attachments)
      stages.add(val.value)
    def message = JsonOutput.toJson([
        channel: "${body.channel}",
        username: "Jenkins",
        as_user: true,
        attachments: stages
    ])

    this.channel = "${body.channel}"
    this.message = message
  }



  def sendStageSkipped(name, String s = null) {
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
    def stages = []
    for (val in this.attachments)
      stages.add(val.value)
    def payload = JsonOutput.toJson([
        ts: "${this.response.ts}",
        channel: "${this.channel}",
        username: "Jenkins",
        as_user: true,
        attachments: stages
    ])
    return payload
  }
  
  def sendStageAbort(channel, buildURL) {
    def stage = [
      color: "#cccc00",
      "text": ":failed: <${buildURL}|Build has been aborted.>"
    ]
    this.attachments["${channel}"] = stage
    def stages = []
    for (val in this.attachments)
      stages.add(val.value)
    def payload = JsonOutput.toJson([
        ts: "${this.response.ts}",
        channel: "${this.channel}",
        username: "Jenkins",
        as_user: true,
        attachments: stages
    ])

    return payload
  }

  def sendStageRunning(name) {
    def stage = [
      color: "#cccc00",
      text: ":in_progress: ${name}: running"
    ]
    this.attachments["${name}"] = stage

    def stages = []
    for (val in this.attachments)
      stages.add(val.value)
    def payload = JsonOutput.toJson([
        ts: "${this.response.ts}",
        channel: "${this.channel}",
        username: "Jenkins",
        as_user: true,
        attachments: stages
    ])

    return payload
  }

  def sendStageInput(name, String s = null, id, buildURL) {
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
    def stages = []
    for (val in this.attachments)
      stages.add(val.value)
    def payload = JsonOutput.toJson([
        ts: "${this.response.ts}",
        channel: "${this.channel}",
        username: "Jenkins",
        as_user: true,
        attachments: stages
    ])
    return payload
  }

  def sendStageSuccess(name, String s = null) {
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
    def stages = []
    for (val in this.attachments)
      stages.add(val.value)
    def payload = JsonOutput.toJson([
        ts: "${this.response.ts}",
        channel: "${this.channel}",
        username: "Jenkins",
        as_user: true,
        attachments: stages
    ])
    return payload
  }

  def sendPipelineFailure(name, log) {
    def stage = [
      color: "danger",
      text: ":failed: Build failed check logs here - ${log}",
      mrkdwn_in: ["text"]
    ]
    this.attachments["${name}"] = stage
    def stages = []
    for (val in this.attachments)
      stages.add(val.value)
    def payload = JsonOutput.toJson([
        ts: "${this.response.ts}",
        channel: "${this.channel}",
        username: "Jenkins",
        as_user: true,
        attachments: stages  
    ])

    return payload
  }
}

