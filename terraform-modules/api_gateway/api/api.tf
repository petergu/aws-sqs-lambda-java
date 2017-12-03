variable "api_name" {
  description = "name of the api"
}

variable "suffix" {
  description = "Suffix to function name differentiate between development envs or developers"
  default =""
}

# Rest API
resource "aws_api_gateway_rest_api" "api" {
  name = "${var.api_name}${var.suffix}"
}

output "id" {
  value = "${aws_api_gateway_rest_api.api.id}"
}

output "root_resource_id" {
  value = "${aws_api_gateway_rest_api.api.root_resource_id}"
}
