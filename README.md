# AWS Lambda and SQS Showcase Example
A showcase example that integrates AWS SQS and Lambda, written in Java, using Dagger 2 as dependency injection, Terraform as deployment tool.

## Highlights

* [Dagger 2](https://google.github.io/dagger/) as Dependency Injection(DI).
* Highly testable codes, including:
    * Unit tests, using Spock framework
    * Integration tests, using [ElasticMQ](https://github.com/adamw/elasticmq), [mockito](https://github.com/mockito/mockito)
* Deployment using Terraform

## Quick Start

```bash
cd terraform
terraform init
terraform apply
curl https://{REPLACE-WITH-YOUR-API-GATEWAY-ID}.execute-api.us-east-1.amazonaws.com/Live/test?eventId=1
```

Check your lambda CloudWatch logs.