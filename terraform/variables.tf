variable "aws_region" {
  default = "us-east-1"
}

variable "version_lambda" {
  description = "False - False means publish new lambda function with the Test alias, otheriwse promote Live to the most recent Test version"
  default = false
}

variable "stage" {
  default = "Live"
  description = "api deployment stage"
}

variable "process_messages_max" {
  default = "100"
}

variable "worker_lambda_name" {
  default = "worker_bee_lambda"
}