package neu.edu.crease.Model;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String userID;
    private String userName = "";
    private String userProfileImage;
    private int userBeingLiked;
    private String userSelfDescription = "";

    public User()
    {
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

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userProfileImage", userProfileImage);
        result.put("userSelfDescription", userSelfDescription);
        return result;
    }
}
