package neu.edu.crease.Model;

import android.net.Uri;

import java.util.ArrayList;

public class User {
    private String userID;
    private String userName = "";
    private Uri userProfileImage;
    private int userBeingLiked;
    private String userSelfDescription = "";

    public User()
    {
    }

    public User(String userName, Uri userProfileImage){
        this.userName = userName;
        this.userProfileImage = userProfileImage;
    }

    public User(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
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

    public void setUserSelfDescription(String userSelfDescription) {
        this.userSelfDescription = userSelfDescription;
    }

    public String getUserSelfDescription() {
        return userSelfDescription;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
