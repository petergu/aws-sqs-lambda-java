variable "target_arn" {
  description = "Target ARN for this event"
}

variable "rule_name" {
  description = "Name of the rule to apply to this target"
}

resource "aws_cloudwatch_event_target" "target" {
  arn = "${var.target_arn}"
  rule = "${var.rule_name}"
}