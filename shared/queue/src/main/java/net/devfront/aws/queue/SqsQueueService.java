package net.devfront.aws.queue;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AWS SQS queue service implementation.
 */
public class SqsQueueService implements QueueService<Message> {
    private static Logger logger = LoggerFactory.getLogger(SqsQueueService.class);

    private static final int MAX_MESSAGES = 10;
    private static final String APPROXIMATE_NUMBER_OF_MESSAGES = "ApproximateNumberOfMessages";


    private AmazonSQS sqsClient;
    private String name;
    private String url;

    public SqsQueueService(AmazonSQS sqsClient, String name) {
        this.sqsClient = sqsClient;
        this.name = name;
    }

    /**
     * Returns the name of the queue.
     *
     * @return the queue name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves one or more messages (up to maxNumberOfMessages) from the specified queue.
     *
     * @param maxNumberOfMessages The maximum number of messages to return.
     * @return a list of messages.
     */
    public List<Message> receiveMessage(int maxNumberOfMessages) {
        List<Message> messages = new ArrayList<>();

        String queueUrl = getUrl();
        if (queueUrl == null || queueUrl.isEmpty()) {
            return messages;
        }

        int maxForBatch = maxNumberOfMessages > MAX_MESSAGES ? MAX_MESSAGES : maxNumberOfMessages;

        try {
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
                .withMaxNumberOfMessages(maxForBatch);

            messages = sqsClient.receiveMessage(receiveMessageRequest).getMessages();
        } catch (AmazonServiceException ase) {
            logException(ase);
        } catch (AmazonClientException ace) {
            logger.error("Could not reach SQS. {}", ace.toString());
        }

        return messages;
    }

    /**
     * Delete a message from a queue.
     *
     * @param message the message to be deleted.
     */
    public void deleteMessage(Message message) {
        sqsClient.deleteMessage(getUrl(), message.getReceiptHandle());
    }


    /**
     * Returns the number of messages of the queue.
     *
     * @return the number of messages.
     */
    public int getNumberOfMessages() {
        GetQueueAttributesResult approximateNumberOfMessagesResult = sqsClient.getQueueAttributes(getUrl(), Collections.singletonList(APPROXIMATE_NUMBER_OF_MESSAGES));
        String approximateNumberOfMessages = approximateNumberOfMessagesResult.getAttributes().get(APPROXIMATE_NUMBER_OF_MESSAGES);
        return Integer.parseInt(approximateNumberOfMessages);
    }


    /**
     * Returns the queue URL on SQS.
     *
     * @return returns the URL of this queue.
     */
    private String getUrl() {
        if ((url == null || url.isEmpty()) && (name != null && !name.isEmpty())) {
            url = this.sqsClient.getQueueUrl(name).getQueueUrl();
        }
        return url;
    }


    /**
     * Log the exception.
     *
     * @param ase AmazonServiceException
     */
    private static void logException(AmazonServiceException ase) {
        logger.error("AmazonServiceException: ERROR={}, AWS_STATUS_CODE={}, AWS_ERROR_CODE={}, AWS_ERROR_TYPE={}, reqid={}",
            ase.toString(), ase.getStatusCode(), ase.getErrorCode(), ase.getErrorType(), ase.getRequestId());
    }
}
