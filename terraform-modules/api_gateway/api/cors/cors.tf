variable "api_id" {
  description = "api id"
}

variable "resource_id" {
  description = "paths for the resources to create on the api"
}

variable "count" {
  description = "count for module, since terraform doesn't support it yet"
  default = 1
}

resource "aws_api_gateway_method" "ResourceOptions" {
  count = "${var.count}"
  rest_api_id = "${var.api_id}"
  resource_id = "${var.resource_id}"
  http_method = "OPTIONS"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "ResourceOptionsIntegration" {
  count = "${var.count}"
  rest_api_id = "${var.api_id}"
  resource_id = "${var.resource_id}"
  http_method = "${aws_api_gateway_method.ResourceOptions.http_method}"
  type = "AWS"
  request_templates = {
    "application/json" = <<PARAMS
      { "statusCode": 200 }
      PARAMS
  }
}

resource "aws_api_gateway_integration_response" "ResourceOptionsIntegrationResponse" {
  count = "${var.count}"
  rest_api_id = "${var.api_id}"
  resource_id = "${var.resource_id}"
  http_method = "${aws_api_gateway_method.ResourceOptions.http_method}"
  status_code = "200"
  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'",
    "method.response.header.Access-Control-Allow-Methods" = "'POST,OPTIONS,GET,PUT,PATCH,DELETE'",
    "method.response.header.Access-Control-Allow-Origin" = "'*'"
  }
  depends_on = ["aws_api_gateway_integration.ResourceOptionsIntegration", "aws_api_gateway_method_response.ResourceOptions200"]
}

resource "aws_api_gateway_method_response" "ResourceOptions200" {
  count = "${var.count}"
  rest_api_id = "${var.api_id}"
  resource_id = "${var.resource_id}"
  http_method = "OPTIONS"
  status_code = "200"
  response_models = { "application/json" = "Empty" }
  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = true,
    "method.response.header.Access-Control-Allow-Methods" = true,
    "method.response.header.Access-Control-Allow-Origin" = true
  }
  depends_on = ["aws_api_gateway_integration.ResourceOptionsIntegration"]
}