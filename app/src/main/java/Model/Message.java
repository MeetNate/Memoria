package Model;

public class Message {
    private String messageText;
    private boolean isSender;
    private String senderName;
    private String receiverName; // New field for receiver's name

    // Constructor with receiverName
    public Message(){

    }
    public Message(String messageText, boolean isSender, String senderName, String receiverName) {
        this.messageText = messageText;
        this.isSender = isSender;
        this.senderName = senderName;
        this.receiverName = receiverName;
    }

    // Constructor without receiverName (for backward compatibility or cases where it's not immediately needed)
    public Message(String messageText, boolean isSender, String senderName) {
        this.messageText = messageText;
        this.isSender = isSender;
        this.senderName = senderName;
    }

    // Getters
    public String getMessageText() {
        return messageText;
    }

    public boolean isSender() {
        return isSender;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    // Setters
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setSender(boolean isSender) {
        this.isSender = isSender;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
