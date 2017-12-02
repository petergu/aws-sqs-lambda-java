package net.devfront.aws.queue;

import com.amazonaws.services.sqs.model.Message;

import java.util.List;

/**
 * QueueService interface.
 */
public interface QueueService<T> {

    /**
     * Returns the name of the queue.
     *
     * @return the queue name.
     */
    String getName();


    /**
     * Retrieves one or more messages (up to maxNumberOfMessages) from the specified queue.
     *
     * @param maxNumberOfMessages The maximum number of messages to return.
     * @return a list of messages.
     */
    List<T> receiveMessage(int maxNumberOfMessages);

    /**
     * Delete a message from a queue.
     *
     * @param message the message to be deleted.
     */
    void deleteMessage(Message message);


    /**
     * Returns the number of messages of the queue.
     *
     * @return the number of messages.
     */
    int getNumberOfMessages();

}