package neu.edu.crease.Model;

public class Comment {
    private String publisherID;
    private String comment;
    private String commentID;

    public Comment(){

    }

    public Comment(String publisherID, String comment, String commentID) {
        this.publisherID = publisherID;
        this.comment = comment;
        this.commentID = commentID;
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

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
}

