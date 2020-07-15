package neu.edu.crease.Model;

import android.net.Uri;

import java.util.ArrayList;

public class User {
    private String userID;
    private String userName;
    private Uri userProfileImage;
    private ArrayList<Post> postsUserLiked;
    private int userBeingLiked;

    public User()
    {
        this.postsUserLiked = new ArrayList<>();
    }

    public User(String userName, Uri userProfileImage){
        this.userName = userName;
        this.userProfileImage = userProfileImage;
        this.postsUserLiked = new ArrayList<>();
        this.userBeingLiked=0;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Uri getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(Uri userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public int getUserBeingLiked() {
        return userBeingLiked;
    }

    public void setUserBeingLiked(int userBeingLiked) {
        this.userBeingLiked = userBeingLiked;
    }
}
