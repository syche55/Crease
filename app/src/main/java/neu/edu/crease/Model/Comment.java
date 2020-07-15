package neu.edu.crease.Model;

public class Comment {
    private User commentUser;
    private String commentContent;

    public Comment(){

    }

    public Comment(User commentUser, String commentContent){
        this.commentContent = commentContent;
        this.commentUser = commentUser;
    }


    public User getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(User commentUser) {
        this.commentUser = commentUser;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
