package net.devfront.aws.sqslambda.consumer.service.impl;

import net.devfront.aws.sqslambda.consumer.service.ConsumerConfig;

public class DefaultConsumerConfig implements ConsumerConfig {
    private static final String QUEUE_NAME_KEY = "QUEUE_NAME";
    private static final String PROCESS_MESSAGES_MAX_KEY = "PROCESS_MESSAGES_MAX";
    private static final String WORKER_LAMBDA_NAME_KEY = "WORKER_LAMBDA_NAME";

    private static final int PROCESS_MESSAGES_MAX_DEFAULT = 100;

    private String queueName;
    private int processMessagesMax;
    private String workerLambdaName;

    public DefaultConsumerConfig() {
        this.queueName = System.getenv(QUEUE_NAME_KEY);
        this.processMessagesMax = getMessagesMax(System.getenv(PROCESS_MESSAGES_MAX_KEY));
        this.workerLambdaName = System.getenv(WORKER_LAMBDA_NAME_KEY);
    }

    public DefaultConsumerConfig(
        String queueName,
        int processMessagesMax,
        String workerLambdaName
    ) {
        this.queueName = queueName;
        this.processMessagesMax = processMessagesMax;
        this.workerLambdaName = workerLambdaName;
    }

    @Override
    public String getQueueName() {
        return queueName;
    }

    @Override
    public int getProcessMessagesMax() {
        return processMessagesMax;
    }

    @Override
    public String getWorkerLambdaName() {
        return this.workerLambdaName;
    }


    private int getMessagesMax(String messagesMax) {
        if (messagesMax != null && !messagesMax.isEmpty()) {
            return Integer.parseInt(messagesMax);
        }

        return PROCESS_MESSAGES_MAX_DEFAULT;
    }
}
