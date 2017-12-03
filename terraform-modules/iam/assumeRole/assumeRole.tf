variable "source_bucket" {
  description = "source_bucket to upload to"
}

variable "policy_file" {
  description = "the policy to attach to the role"
}

variable "suffix" {
  description = "Suffix to function name differentiate between development envs or developers"
  default = ""
}

variable "upload_asset" {
  description = "The upload asset name - used for policy and role"
}

data "aws_caller_identity" "current" {}

data "aws_iam_policy_document" "service-assume-role-policy" {
  statement {
    actions = [ "sts:AssumeRole" ]
    principals {
      type = "AWS"
      identifiers = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"]
    }
    effect = "Allow"
  }
}

data "template_file" "upload_policy" {
  template = "${file("${var.policy_file}")}"
  vars {
    source_bucket = "${var.source_bucket}"
  }
}

resource "aws_iam_role" "role" {
  name = "${var.upload_asset}${var.suffix}_role"
  assume_role_policy = "${data.aws_iam_policy_document.service-assume-role-policy.json}"
}

resource "aws_iam_role_policy" "policy" {
  name = "${var.upload_asset}${var.suffix}_policy"
  role = "${aws_iam_role.role.id}"
  policy = "${data.template_file.upload_policy.rendered}"
}