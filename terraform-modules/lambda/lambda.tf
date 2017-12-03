variable "deployment_package" {
  description = "The filename of the lambda zip"
}

variable "function_name" {
  description = <<DESC
    The name of the lambda to create that shows up in the AWS console Lambda > Functions.
  DESC
}

variable "suffix" {
  description = "Suffix to function name differentiate between development envs or developers"
  default = ""
}

variable "runtime" {
  description = "The runtime of the lambda to create"
  default = "nodejs"
}

variable "timeout" {
  description = "Timeout in seconds"
  default = "3"
}

variable "memory_size" {
  description = "Memory in MB, also affect CPU speed"
  default = "128"
}



variable "handler" {
  description = <<DESC
    The handler of the lambda (a function defined in your lambda function file)
    For example helloworld.handler for a handler file helloworld.js
    as explained in http://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-handler.html
    Will default to function_name.handler
  DESC
  default = ""
}

variable "policy_file" {
  description = "IAM role attached to the Lambda Function (ARN)"
}

variable "environment_variables" {
  type = "map"
  description = "The environment variables to pass to lambda function"
  default = {
    foo = "bar"
  }
}

variable "version_lambda" {
  description = "True - True means publish new lambda function with the Test alias, otheriwse promote Live to the most recent Test version"
  default = true
}

variable "first_time_deploy" {
  description = "True - first deployment (Before we have any lambda functions). Used to create Live alias along side Test alias"
  default = false
}

module "lambda_role" {
  source = "../iam/lambda"
  service = "lambda"
  role_name = "${var.function_name}${var.suffix}_role"
  policy = "${var.policy_file}"
  role_policy_name = "${var.function_name}_policy"
}

module "lambda_dlq" {
  source = "../sqs"
  name = "${var.function_name}${var.suffix}_dlq"
}

resource "aws_lambda_function" "lambda" {
  filename = "${var.deployment_package}"
  function_name = "${var.function_name}${var.suffix}"
  role = "${module.lambda_role.arn}"
  handler = "${coalesce(var.handler, "${var.function_name}.handler")}"
  runtime = "${var.runtime}"
  timeout = "${var.timeout}"
  memory_size = "${var.memory_size}"
  source_code_hash = "${base64sha256(file("${var.deployment_package}"))}"
  dead_letter_config = {
    target_arn = "${module.lambda_dlq.arn}"
  }
  environment {
    variables = "${var.environment_variables}"
  }
  publish = true
}

output "arn" {
  value = "${aws_lambda_function.lambda.arn}"
}

resource "aws_lambda_alias" "lambda_alias" {
  function_name = "${aws_lambda_function.lambda.function_name}"
  function_version = "$LATEST"
  name = "Test"
  description = "${aws_lambda_function.lambda.function_name} Test Lambda Function"
}

resource "aws_lambda_alias" "lambda_alias_Live" {
  function_name = "${aws_lambda_function.lambda.function_name}"
  function_version = "$LATEST"
  name = "Live"
  description = "${aws_lambda_function.lambda.function_name} Live Lambda Function"
}

output "lambda_alias_version" {
  value = "${aws_lambda_alias.lambda_alias.function_version}"
}

output "lambda_alias_live_version" {
  value = "${aws_lambda_alias.lambda_alias_Live.function_version}"
}

output "function_name" {
  value = "${aws_lambda_function.lambda.function_name}"
}