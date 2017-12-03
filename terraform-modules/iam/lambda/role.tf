variable "role_policy_name" {
  description = "name of the role policy"
}

variable "role_name" {
  description = "name of the role"
}

variable "service" {
  description = "service to be granted the policy"
}

variable "policy" {
  description = "the policy to attach to the role"
}

data "aws_iam_policy_document" "service-assume-role-policy" {
  statement {
    actions = [ "sts:AssumeRole" ]

    principals {
      type = "Service"
      identifiers = ["${var.service}.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "role" {
  name = "${var.role_name}"
  assume_role_policy = "${data.aws_iam_policy_document.service-assume-role-policy.json}"
}

resource "aws_iam_role_policy" "policy" {
  name = "${var.role_policy_name}"
  role = "${aws_iam_role.role.id}"
  policy = "${var.policy}"
}

output "arn" {
  value = "${aws_iam_role.role.arn}"
}