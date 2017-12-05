# AWS Lambda, API Gateway and SQS Integration Showcase Example
A showcase example that integrates AWS API Gateway, SQS and Lambda, written in Java 8, using Dagger 2 as dependency injection, Terraform as deployment tool.

## Integrate AWS Lambda, API Gateway and SQS
The following figure shows all components:
* Other system hits the API Gateway endpoint.
* The API Gateway sends messages to the SQS queue. 
* The SQS queue receives and stores messages.
* The CloudWatch Event Rule triggers the Lambda Consumer based on a schedule (e.g. every 5 minutes).
* The Lambda Consumer reads as many messages as possible from the SQS and invoke a Lambda Worker for each message asynchronous.
* The Lambda Worker does the actual work.

<p align="center">
  <img src="https://user-images.githubusercontent.com/2072930/33588139-57fc87a4-d93f-11e7-9719-5add43acae12.png" width="800">
</p>

## Considerations for writing AWS Lambda functions.

* AWS Lambda functions might seem like small, independent functions, but we should not put everything into a single file, ignore code reusability and separation of concerns.
* Separate the Lambda handler (entry point) from your application logic. Lambda should be viewed only as one entry point into your application.
* Using Dependency Injection ( DI ) pattern is great way to structure your Lambda codes.
* Spring Framework DI is too heavy for Lambda.
* Even [Guice](https://github.com/google/guice) is not a good fit, due to it's jar size and performance(Reflection).
* [Dagger 2](https://google.github.io/dagger/) is a good fit, it's a code generator, it's runtime jar size is just about 16K, no reflection, just like your hand written codes.
* By using Dagger 2, we create different services for different responsibilities of the application, we test them independently from others.
* As a result, we have highly testable codes, including:
    * Unit tests, using Spock framework
    * Integration tests, using [ElasticMQ](https://github.com/adamw/elasticmq), [mockito](https://github.com/mockito/mockito)
* Automating everything by using Terraform.

## Quick Start

```bash
mvn clean install
cd terraform
terraform init
terraform apply
```

Log into you AWS management console, check your API Gateway URL created by Terraform.

Now you hit the API Gateway endpoint by entering the following commands:
```bash
curl https://{REPLACE-WITH-YOUR-API-GATEWAY-ID}.execute-api.us-east-1.amazonaws.com/Live/test?eventId=1
curl https://{REPLACE-WITH-YOUR-API-GATEWAY-ID}.execute-api.us-east-1.amazonaws.com/Live/test?eventId=2
curl https://{REPLACE-WITH-YOUR-API-GATEWAY-ID}.execute-api.us-east-1.amazonaws.com/Live/test?eventId=3
```

Wait a few minutes, check the consumer lambda and worker lambda CloudWatch logs, you should see they have done their works. In this example, the worker is just a dummy lambda.
