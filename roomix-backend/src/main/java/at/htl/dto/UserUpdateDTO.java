package at.htl.dto;

public class UserUpdateDTO {

    String username;
    String displayname;
    String password;
    String picUrl;
    boolean resetProfilePic;

    public UserUpdateDTO() {
    }

    public UserUpdateDTO(String username, String displayname, String password, String picUrl, boolean resetProfilePic) {
        this.username = username;
        this.displayname = displayname;
        this.password = password;
        this.picUrl = picUrl;
        this.resetProfilePic = resetProfilePic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public boolean isResetProfilePic() {
        return resetProfilePic;
    }

    public void setResetProfilePic(boolean resetProfilePic) {
        this.resetProfilePic = resetProfilePic;
    }
}
