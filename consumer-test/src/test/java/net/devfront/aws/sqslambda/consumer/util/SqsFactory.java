package net.devfront.aws.sqslambda.consumer.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

/**
 * Factory method for AWS SQS.
 */
public class SqsFactory {
    private static final String LOCAL_ENDPOINT = "http://localhost:9324";

    private SqsFactory() { }

    /**
     * Returns a client instance for AWS SQS.
     * @param env running environment.
     * @return a client that talks to SQS.
     */
    public static AmazonSQS getClient(String env) {
        if ("local".compareToIgnoreCase(env) == 0) {
            return AmazonSQSClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("test", "test")))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(LOCAL_ENDPOINT, ""))
                    .build();
        } else {
            return AmazonSQSClientBuilder.defaultClient();
        }
    }
}
