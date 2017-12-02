package net.devfront.aws.sqslambda.consumer.module;

import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.sqs.AmazonSQS;
import dagger.Module;
import dagger.Provides;
import net.devfront.aws.sqslambda.consumer.service.ConsumerConfig;
import net.devfront.aws.sqslambda.consumer.service.impl.DefaultConsumerConfig;
import net.devfront.aws.sqslambda.consumer.util.SqsFactory;
import javax.inject.Singleton;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@Module
public class TestEnvironmentModuleModule {

    @Provides
    @Singleton
    public ConsumerConfig provideConsumerConfig() {
        return new DefaultConsumerConfig(
            "localQueue",
            50,
            "worker"
        );
    }

    @Provides
    @Singleton
    public AmazonSQS provideAmazonSQS() {
        return SqsFactory.getClient("local");
    }

    @Provides
    @Singleton
    AWSLambdaAsync provideAWSLambdaAsync() {
        AWSLambdaAsync awsLambdaAsync = mock(AWSLambdaAsync.class);

        when(awsLambdaAsync.invokeAsync(any(InvokeRequest.class))).thenAnswer(invocation -> {
            System.out.println("Invoking worker lambda");

            InvokeResult invokeResult = mock(InvokeResult.class);
            when(invokeResult.getStatusCode()).thenReturn(202);
            return CompletableFuture.completedFuture(invokeResult);
        });

        return awsLambdaAsync;
    }


}
