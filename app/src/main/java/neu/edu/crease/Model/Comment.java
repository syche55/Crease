package neu.edu.crease.Model;

public class Comment {
    private String publisherID;
    private String comment;

    public Comment(){

    }

    public Comment(String publisherID, String comment) {
        this.publisherID = publisherID;
        this.comment = comment;
    }

    public String getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(String publisherID) {
        this.publisherID = publisherID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

