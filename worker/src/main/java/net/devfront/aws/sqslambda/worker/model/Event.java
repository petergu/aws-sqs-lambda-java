package net.devfront.aws.sqslambda.worker.model;

/**
 * Event POJO.
 */
public class Event {
    private Integer id;
    private String messageId;

    public Event() {
    }

    public Event(Integer id, String messageId) {
        this.id = id;
        this.messageId = messageId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (!id.equals(event.id)) return false;
        return messageId.equals(event.messageId);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + messageId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
