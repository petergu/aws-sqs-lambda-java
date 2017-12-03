module "worker_lambda" {
  source = "../terraform-modules/lambda"

  function_name = "worker_bee_lambda"
  deployment_package = "${path.module}/../worker/target/worker-1.0-SNAPSHOT.jar"
  handler = "net.devfront.aws.sqslambda.worker.Main::handleRequest"
  policy_file = "${file("${path.module}/policies/workerLambdaPolicy.json")}"
  runtime = "java8"
  timeout = 300
  memory_size = 512
  environment_variables = {
    ENV1="Test1"
  }
  version_lambda = "${var.version_lambda}"
}