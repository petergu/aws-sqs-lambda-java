variable "name" {
  description = "The name of the queue"
}

resource "aws_sqs_queue" "sqs_queue" {
  name = "${var.name}"
  message_retention_seconds = 1209600
}

output "arn" {
  value = "${aws_sqs_queue.sqs_queue.arn}"
}

output "name" {
  value = "${var.name}"
}