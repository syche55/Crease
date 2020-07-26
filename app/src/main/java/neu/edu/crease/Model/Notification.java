package neu.edu.crease.Model;

public class Notification {
    private String userID;
    private String comment_text;
    private String postID;
    private boolean isPost;

    public Notification(String userID, String comment_text, String postID, boolean isPost) {
        this.userID = userID;
        this.comment_text = comment_text;
        this.postID = postID;
        this.isPost = isPost;
    }

    public Notification() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public boolean getIsPost() {
        return isPost;
    }

    public void setIsPost(boolean post) {
        this.isPost = post;
    }
}
