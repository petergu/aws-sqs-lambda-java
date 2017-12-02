package net.devfront.aws.sqslambda.consumer.service

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.CreateQueueRequest
import com.amazonaws.services.sqs.model.SendMessageRequest
import net.devfront.aws.sqslambda.consumer.component.DaggerTestConsumerComponent
import net.devfront.aws.sqslambda.consumer.component.TestConsumerComponent
import net.devfront.aws.sqslambda.consumer.util.SqsFactory
import org.elasticmq.rest.sqs.SQSRestServerBuilder
import spock.lang.Specification

class DequeueServiceTest extends Specification {
    def QUEUE_NAME = "localQueue"
    def sqsServer
    AmazonSQS sqsClient
    TestConsumerComponent testConsumerComponent

    def setup() {
        sqsServer = SQSRestServerBuilder.start()
        sqsClient = SqsFactory.getClient("local")

        Thread.sleep(1000)

        createQueueAndMessages()

        testConsumerComponent = DaggerTestConsumerComponent.builder().build()
    }

    def cleanup() {
        sqsClient.shutdown()
        sqsServer.stopAndWait()
    }

    def 'dequeue'() {
        given:
        DequeueService dequeueService = testConsumerComponent.getDequeueService()

        when:
        int numberOfProcessedMessages = dequeueService.run()

        then:
        numberOfProcessedMessages == 20
    }

    def createQueueAndMessages() {
        def queueUrl = sqsClient.createQueue(
            new CreateQueueRequest(QUEUE_NAME).withAttributes(["VisibilityTimeout":"1"]))
            .getQueueUrl()

        getIdList(1,20).collect {item ->
            sqsClient.sendMessage(new SendMessageRequest(queueUrl, String.valueOf(item)))
        }
    }

    def getIdList(int start, int end) {
        def list = []
        for (int i = start; i <= end; i++) {
            list.add(i)
        }
        return list
    }
}
