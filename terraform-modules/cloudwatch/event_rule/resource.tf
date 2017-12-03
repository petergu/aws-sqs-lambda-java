variable "name" {
  description = "Name of the cloudwatch event rule"
}

variable "description" {
  description = "Description of the cloudwatch event rule"
  default = ""
}

variable "schedule_expression" {
  description = "Schedule to execture this event rule"
  default = ""
}

variable "is_enabled" {
  description = "Enable this cloudwatch rule"
  default = true
}

resource "aws_cloudwatch_event_rule" "rule" {
  name = "${var.name}"
  description = "${var.description}"
  is_enabled = "${var.is_enabled}"

  schedule_expression = "${var.schedule_expression}"
}

output "arn" {
  value = "${aws_cloudwatch_event_rule.rule.arn}"
}

output "name" {
  value = "${aws_cloudwatch_event_rule.rule.name}"
}