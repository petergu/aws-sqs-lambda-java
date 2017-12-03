variable "api_id" {
  description = "api id"
}

variable "count" {
  description = "count for module, since terraform doesn't support it yet"
  default = 1
}

variable "parent_resource_id" {
  description = "parent api resource id"
}

variable "resource" {
  description = "paths for the resources to create on the api"
}

variable "region" {
  default = "us_east_1"
}

variable "credentials" {
  description = "Role ARN for the api gateway integration"
}

variable "stage" {
  description = "stage to deploy the resource"
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

variable "integration_request_parameters" {
  description = "request parameter map for the Integration"
  type = "map"
  default = {}
}

variable "method_request_parameters" {
  description = "request parameter map for the Method"
  type = "map"
  default = {}
}

variable "api_key_required" {
  description = "true if API requires key, else false "
  default = true
}

variable "uri" {
  description = "URI to sqs queue - https://docs.aws.amazon.com/apigateway/api-reference/resource/integration/#uri"
}

variable "http_method" {
  description = "The http method for this resource"
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

  count = "${var.count}"
  http_method = "${var.http_method}"
  authorization = "NONE"
  api_key_required = "${var.api_key_required}"
  request_parameters = "${var.method_request_parameters}"
}

module "resource_cors" {
  count = "${var.enable_cors}"
  source = "../../../api/cors"
  api_id = "${var.api_id}"
  resource_id = "${aws_api_gateway_resource.resource.id}"
}

resource "aws_api_gateway_integration" "integration" {
  rest_api_id = "${var.api_id}"
  resource_id = "${aws_api_gateway_resource.resource.id}"
  count = "${var.count}"
  http_method =  "${var.http_method}"
  integration_http_method = "${var.http_method}"
  type = "AWS"
  uri = "${var.uri}"
  depends_on = ["aws_api_gateway_method.method"]
  request_parameters = "${var.integration_request_parameters}"
  credentials = "${var.credentials}"
}

resource "aws_api_gateway_method_response" "200" {
  rest_api_id = "${var.api_id}"
  resource_id = "${aws_api_gateway_resource.resource.id}"
  count = "${var.count}"
  http_method =  "${var.http_method}"
  status_code = "200"
  response_models = {"application/json" = "Empty"}
  //required for enabling CORS
  response_parameters = { "method.response.header.Access-Control-Allow-Origin" = true  }
  depends_on = ["aws_api_gateway_method.method", "aws_api_gateway_integration.integration"]
}

resource "aws_api_gateway_integration_response" "200_integration_response" {
  count = "${var.count}"
  rest_api_id = "${var.api_id}"
  resource_id = "${aws_api_gateway_resource.resource.id}"
  http_method =  "${var.http_method}"
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
  depends_on = ["aws_api_gateway_integration.integration", "aws_api_gateway_integration_response.200_integration_response"]
}

output "resource_id" {
  value = "${aws_api_gateway_resource.resource.id}"
}

output "deployment_id" {
  value = "${aws_api_gateway_deployment.deployment.id}"
}

