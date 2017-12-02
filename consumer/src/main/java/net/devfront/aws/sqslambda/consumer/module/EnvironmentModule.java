package net.devfront.aws.sqslambda.consumer.module;

import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import dagger.Module;
import dagger.Provides;
import net.devfront.aws.sqslambda.consumer.service.ConsumerConfig;
import net.devfront.aws.sqslambda.consumer.service.impl.DefaultConsumerConfig;

import javax.inject.Singleton;

/**
 * Provides environment specific instances.
 */
@Module
public class EnvironmentModule {

    @Provides
    @Singleton
    public ConsumerConfig provideConsumerConfig() {
        return new DefaultConsumerConfig();
    }

    @Provides
    @Singleton
    public AmazonSQS provideAmazonSQS() {
        return AmazonSQSClientBuilder.defaultClient();
    }

    @Provides
    @Singleton
    AWSLambdaAsync provideAWSLambdaAsync() {
        return AWSLambdaAsyncClientBuilder.defaultClient();
    }
}
