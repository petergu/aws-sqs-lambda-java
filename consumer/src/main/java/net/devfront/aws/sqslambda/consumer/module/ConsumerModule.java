package net.devfront.aws.sqslambda.consumer.module;

import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import dagger.Module;
import dagger.Provides;
import net.devfront.aws.queue.QueueService;
import net.devfront.aws.queue.SqsQueueService;
import net.devfront.aws.sqslambda.consumer.service.ConsumerConfig;
import net.devfront.aws.sqslambda.consumer.service.DequeueService;
import net.devfront.aws.sqslambda.consumer.service.impl.DequeueServiceImpl;

import javax.inject.Singleton;

@Module
public class ConsumerModule {

    @Provides
    @Singleton
    public QueueService<Message> provideQueueService(
        AmazonSQS sqsClient,
        ConsumerConfig consumerConfig
    ) {
        return new SqsQueueService(sqsClient, consumerConfig.getQueueName());
    }

    @Provides
    @Singleton
    DequeueService provideDequeueService(
        ConsumerConfig consumerConfig,
        QueueService<Message> queueService,
        AWSLambdaAsync awsLambdaAsync
    ) {
        return new DequeueServiceImpl(consumerConfig, queueService, awsLambdaAsync);
    }
}
