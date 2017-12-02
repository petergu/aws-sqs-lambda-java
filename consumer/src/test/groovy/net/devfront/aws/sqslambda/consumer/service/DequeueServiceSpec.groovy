package net.devfront.aws.sqslambda.consumer.service

import com.amazonaws.services.lambda.AWSLambdaAsync
import com.amazonaws.services.lambda.model.InvokeRequest
import com.amazonaws.services.lambda.model.InvokeResult
import com.amazonaws.services.sqs.model.Message
import net.devfront.aws.queue.QueueService
import net.devfront.aws.sqslambda.consumer.service.impl.DefaultConsumerConfig
import net.devfront.aws.sqslambda.consumer.service.impl.DequeueServiceImpl
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class DequeueServiceSpec extends Specification {
    DequeueServiceImpl dequeueService

    // Dependencies
    ConsumerConfig consumerConfig
    QueueService<Message> queueService
    AWSLambdaAsync awsLambdaAsync

    InvokeResult invokeResult


    def setup() {
        consumerConfig = new DefaultConsumerConfig(
            "queueName": "localQueue",
            "processMessagesMax": 50,
            "workerLambdaName": "worker"
        )

        queueService = Stub()
        awsLambdaAsync = Mock()

        dequeueService = new DequeueServiceImpl(consumerConfig, queueService, awsLambdaAsync)
        queueService.getNumberOfMessages() >> 15

        invokeResult = Stub()
        invokeResult.getStatusCode() >> 202
    }

    def "Should pull messages from queue and invoke worker lambda to do the work for each message"() {
        given:
        queueService.receiveMessage(_ as Integer) >>> [getMessages(10), getMessages(5), []]

        when:
        int numberOfProcessedMessages = dequeueService.run()

        then:
        15 * awsLambdaAsync.invokeAsync(_ as InvokeRequest) >> CompletableFuture.completedFuture(invokeResult)
        numberOfProcessedMessages == 15
    }

    def getMessages(int count) {
        def list = []
        for (int i = 0; i < count; i++) {
            Message message = new Message()
            message.setMessageId(String.valueOf(i))
            message.setBody(String.valueOf(i))
            list.add(message)
        }
        return list
    }
}
