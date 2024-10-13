package Model;

public class Message {
    private String message;
    private boolean isSender; // true if the message is from the sender, false if from the receiver

    public Message(String message, boolean isSender) {
        this.message = message;
        this.isSender = isSender;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSender() {
        return isSender;
    }
}
