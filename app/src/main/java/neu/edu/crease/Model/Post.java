package neu.edu.crease.Model;

import com.google.firebase.database.Exclude;

import org.w3c.dom.Comment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Post {
    private String postID = "";
    private String postPublisher = "";
    private String postTitle = "";
    private String postContent = "";
    private String postImage;
    private ArrayList<Comment> commentsUnderPost;

    public Post(String postID, String postPublisher, String postImage, String postTitle, String postContent){
        this.postID = postID;
        this.postPublisher = postPublisher;
        this.postImage = postImage;
        this.postContent = postContent;
        this.postTitle = postTitle;
    }
    public Post(){

    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostPublisher() {
        return postPublisher;
    }

    public void setPostPublisher(String postPublisher) {
        this.postPublisher = postPublisher;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postID", postID);
        result.put("postPublisher", postPublisher);
        result.put("postTitle", postTitle);
        result.put("postContent", postContent);
        result.put("postImage", postImage);

        return result;
    }
}
