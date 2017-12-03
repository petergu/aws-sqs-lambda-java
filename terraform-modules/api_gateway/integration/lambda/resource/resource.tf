variable "api_id" {
  description = "api id"
}

variable "parent_resource_id" {
  description = "parent api resource id"
}

variable "resource" {
  description = "paths for the resources to create on the api"
}

variable "methods" {
  type = "list"
  description = "list of methods for the resources to create on the api"
}


variable "region" {
  default = "us_east_1"
}

variable "lambda_arn" {
  description = "lambda resources will be integrated to through proxy"
}

variable "create_permission" {
  description = "AllowExecutionFromAPIGateway is created when set to true, otherwise skip.  "
}

variable "stage" {
  description = "stage to deploy the resouce"
}

variable "enable_cors" {
  description = "true to enable cors for the resource"
  default = false
}

variable "depends_resource_id" {
  description = "manually add dependency for module until terraform supports it"
  default = ""
}

variable "depends_deployment_id" {
  description = "manually add dependency for module until terraform supports it"
  default = ""
}

variable "api_key_required" {
  description = "true if API requires key, else false "
  default = true
}


# Resources
resource "aws_api_gateway_resource" "resource" {
  rest_api_id = "${var.api_id}"
  parent_id = "${var.parent_resource_id}"

  path_part = "${var.resource}"
}

resource "aws_api_gateway_method" "method" {
  rest_api_id = "${var.api_id}"
  resource_id = "${aws_api_gateway_resource.resource.id}"

  count = "${length(var.methods)}"
  http_method = "${element(var.methods, count.index)}"
  authorization = "NONE"
  api_key_required = "${var.api_key_required}"
}

module "resource_cors" {
  count = "${var.enable_cors}"
  source = "../../../api/cors"
  api_id = "${var.api_id}"
  resource_id = "${aws_api_gateway_resource.resource.id}"
}

//using proxy lambda integration
resource "aws_api_gateway_integration" "integration" {
  rest_api_id = "${var.api_id}"
  resource_id = "${aws_api_gateway_resource.resource.id}"
  count = "${length(var.methods)}"
  http_method =  "${element("${aws_api_gateway_method.method.*.http_method}", count.index)}"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${var.lambda_arn}/invocations"
  depends_on = ["aws_api_gateway_method.method"]
}

# Lambda
resource "aws_lambda_permission" "permission" {
  count = "${var.create_permission}"
  statement_id  = "${aws_api_gateway_resource.resource.id}_AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = "${var.lambda_arn}"
  principal     = "apigateway.amazonaws.com"
}

resource "aws_api_gateway_method_response" "200" {
  rest_api_id = "${var.api_id}"
  resource_id = "${aws_api_gateway_resource.resource.id}"
  count = "${length(var.methods)}"
  http_method =  "${element("${aws_api_gateway_method.method.*.http_method}", count.index)}"
  status_code = "200"
  response_models = {"application/json" = "Empty"}
  //required for enabling CORS
  response_parameters = { "method.response.header.Access-Control-Allow-Origin" = true  }
  depends_on = ["aws_api_gateway_method.method", "aws_api_gateway_integration.integration"]
}

resource "aws_api_gateway_integration_response" "200_integration_response" {
  count = "${length(var.methods)}"
  rest_api_id = "${var.api_id}"
  resource_id = "${aws_api_gateway_resource.resource.id}"
  http_method =  "${element("${aws_api_gateway_method.method.*.http_method}", count.index)}"
  status_code = "200"

  response_templates = {
    "application/json" = ""
  }
  //required for enabling CORS
  response_parameters = { "method.response.header.Access-Control-Allow-Origin" = "'*'"}
  //  Required not to get ConflictException's
  depends_on = ["aws_api_gateway_integration.integration", "aws_api_gateway_method_response.200"]
}

resource "aws_api_gateway_deployment" deployment {
  rest_api_id = "${var.api_id}"
  stage_name = "${var.stage}"
  //force explicit dependency
  description = "this deployment is depended on resource (${var.depends_resource_id}) and deployment id (${var.depends_deployment_id}) "

  // following the docs that recommend the use of depends
  // https://www.terraform.io/docs/providers/aws/r/api_gateway_deployment.html
  depends_on = ["aws_api_gateway_method.method", "aws_api_gateway_integration.integration", "aws_api_gateway_integration_response.200_integration_response"]
}

output "resource_id" {
  value = "${aws_api_gateway_resource.resource.id}"
}

output "deployment_id" {
  value = "${aws_api_gateway_deployment.deployment.id}"
}

