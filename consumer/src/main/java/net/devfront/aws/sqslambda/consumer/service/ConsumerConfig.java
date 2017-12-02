package net.devfront.aws.sqslambda.consumer.service;

public interface ConsumerConfig {
    String getQueueName();

    int getProcessMessagesMax();

    String getWorkerLambdaName();
}
