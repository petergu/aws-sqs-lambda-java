# SQS Consumer Lambda
module "sqs_consumer_lambda" {
  source = "../terraform-modules/lambda"

  function_name = "sqs_consumer_lambda"
  deployment_package = "${path.module}/../consumer/target/consumer-1.0-SNAPSHOT.jar"
  handler = "net.devfront.aws.sqslambda.consumer.Main::handleRequest"
  policy_file = "${file("${path.module}/policies/consumerLambdaPolicy.json")}"
  runtime = "java8"
  timeout = 300
  memory_size = 512
  environment_variables = {
    QUEUE_NAME = "${module.sqs_test_queue.name}"
    PROCESS_MESSAGES_MAX = "${var.process_messages_max}"
    WORKER_LAMBDA_NAME = "${var.worker_lambda_name}"
  }
  version_lambda = "${var.version_lambda}"
}

# Cloudwatch permission
resource "aws_lambda_permission" "allow_cloudwatch" {
  statement_id   = "AllowExecutionFromCloudWatch"
  action         = "lambda:InvokeFunction"
  function_name  = "${module.sqs_consumer_lambda.function_name}"
  principal      = "events.amazonaws.com"
}

# Schedule
module "sqs_consumer_cloudwatch_rule" {
  source = "../terraform-modules/cloudwatch/event_rule"
  name = "sqs-consumer-event-rule"
  schedule_expression = "rate(5 minutes)"
}

module "sqs_consumer_cloudwatch_target"{
  source = "../terraform-modules/cloudwatch/event_target"
  target_arn = "${module.sqs_consumer_lambda.arn}"
  rule_name = "${module.sqs_consumer_cloudwatch_rule.name}"
}

# SQS queue
module "sqs_test_queue" {
  source = "../terraform-modules/sqs"
  name = "sqs_test_queue"
}


# API gateway -> SQS integration
module "sqs_api_root" {
  source = "../terraform-modules/api_gateway/api"
  api_name = "sqs_api"
}

# SQS API Resource(Endpoint)
module "sqs_api_resource" {
  source = "../terraform-modules/api_gateway/integration/sqs/resource"
  api_id = "${module.sqs_api_root.id}"
  parent_resource_id = "${module.sqs_api_root.root_resource_id}"
  api_key_required = false
  resource = "test"
  http_method = "GET"
  region= "${var.aws_region}"
  stage = "${var.stage}"
  enable_cors = false
  credentials = "${aws_iam_role.sqs_role.arn}"
  uri =  "arn:aws:apigateway:${var.aws_region}:sqs:path/${data.aws_caller_identity.current.account_id}/${module.sqs_test_queue.name}"

  method_request_parameters = {
    method.request.querystring.eventId = true
  }
  integration_request_parameters = {
    integration.request.querystring.MessageBody = "method.request.querystring.eventId",
    integration.request.querystring.Action = "'SendMessage'"
  }
}

data "aws_caller_identity" "current" {}

resource "aws_iam_role" "sqs_role" {
  name = "apigateway_sqs_role"

  assume_role_policy = <<-EOF
    {
        "Version": "2012-10-17",
        "Statement": [
        {
            "Action": "sts:AssumeRole",
            "Principal": {
                "Service": "apigateway.amazonaws.com"
            },
            "Effect": "Allow",
            "Sid": ""
        }
        ]
    }
    EOF
}

resource "aws_iam_role_policy" "sqs_policy" {
  name = "sqs_policy"
  role = "${aws_iam_role.sqs_role.id}"

  policy = <<-EOF
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Action": ["sqs:SendMessage"],
                "Effect": "Allow",
                "Resource": "arn:aws:sqs:*:*:*"
            }
        ]
    }
    EOF
}
