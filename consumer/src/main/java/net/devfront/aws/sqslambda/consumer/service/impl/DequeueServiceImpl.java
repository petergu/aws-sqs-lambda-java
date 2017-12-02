package net.devfront.aws.sqslambda.consumer.service.impl;

import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.devfront.aws.queue.QueueService;
import net.devfront.aws.sqslambda.consumer.model.Event;
import net.devfront.aws.sqslambda.consumer.service.ConsumerConfig;
import net.devfront.aws.sqslambda.consumer.service.DequeueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Dequeue service implementation.
 */
public class DequeueServiceImpl implements DequeueService {

    private static final Logger logger = LoggerFactory.getLogger(DequeueServiceImpl.class);

    // This is the AWS SQS limitation.
    private static final int RECEIVE_MESSAGES_MAX_DEFAULT = 10;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ConsumerConfig consumerConfig;
    private final QueueService<Message> queueService;
    private final AWSLambdaAsync awsLambdaAsync;


    @Inject
    public DequeueServiceImpl(
            ConsumerConfig consumerConfig,
            QueueService<Message> queueService,
            AWSLambdaAsync awsLambdaAsync
    ) {
        this.consumerConfig = consumerConfig;
        this.queueService = queueService;
        this.awsLambdaAsync = awsLambdaAsync;
    }

    @Override
    public int run() {
        String queueName = queueService.getName();
        int numberOfMessages = queueService.getNumberOfMessages();

        logger.info("Dequeue start, QUEUE_NAME={}, QUEUE_LENGTH={}", queueName, numberOfMessages);

        int numberOfProcessedMessages = 0;

        do {
            List<Message> messages = queueService.receiveMessage(RECEIVE_MESSAGES_MAX_DEFAULT);

            if (messages.isEmpty()) {
                break;
            }

            for (Message message : messages) {
                handleMessage(message);
                numberOfProcessedMessages++;
            }
        } while (numberOfProcessedMessages <= consumerConfig.getProcessMessagesMax());

        logger.info("Dequeue end, NUMBER_OF_MESSAGE_PROCESSED={}", numberOfProcessedMessages);

        return numberOfProcessedMessages;
    }

    /**
     * Handle each individual message.
     *
     * @param message {@link Message}
     */
    private void handleMessage(Message message) {
        Event event = getEventFromMessage(message);

        String payload = getPayloadFromEvent(event);
        if (payload != null && !payload.isEmpty()) {
            invokeWorkerLambda(payload);
        }

        queueService.deleteMessage(message);
    }

    /**
     * Invoke worker lambda to do the work.
     *
     * @param payload the payload
     */
    private void invokeWorkerLambda(String payload) {
        InvokeRequest invokeRequest = new InvokeRequest()
            .withFunctionName(consumerConfig.getWorkerLambdaName())
            .withInvocationType(InvocationType.Event)
            .withPayload(ByteBuffer.wrap(payload.getBytes()));

        Future<InvokeResult> futureResult = awsLambdaAsync.invokeAsync(invokeRequest);

        try {
            InvokeResult invokeResult = futureResult.get();
            logger.info("Invoke worker lambda, STATUS_CODE={}", invokeResult.getStatusCode());
        } catch (InterruptedException|ExecutionException e) {
            logger.error("Error on invoking worker lambda, {}", e.getMessage());
        }
    }

    /**
     * Convert {@link Event} object to JSON String.
     *
     * @param event {@link Event}
     * @return the JSON String
     */
    private String getPayloadFromEvent(Event event) {
        String payload = "";
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            logger.warn("Can't convert to JSON string, EVENT={}", event);
        }
        return payload;
    }

    /**
     * Message to Event mapper.
     *
     * @param message {@link Message}
     * @return the {@link Event}
     */
    private Event getEventFromMessage(Message message) {
        return new Event(Integer.valueOf(message.getBody()), message.getMessageId());
    }
}
