// Memory.java
package Model;

public class Memory {
    private String title;
    private String caption;
    private String imageUri;

    // Empty constructor required for Firestore
    public Memory() {}

    public Memory(String title, String caption, String imageUri) {
        this.title = title;
        this.caption = caption;
        this.imageUri = imageUri;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
