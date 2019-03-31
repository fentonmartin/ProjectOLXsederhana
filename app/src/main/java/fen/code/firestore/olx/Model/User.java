package fen.code.firestore.olx.Model;

public class User {

    String email,id,imageUrl,statusinfo,username;


    public User(String email, String id, String imageUrl, String statusinfo, String username) {
        this.email = email;
        this.id = id;
        this.imageUrl = imageUrl;
        this.statusinfo = statusinfo;
        this.username = username;
    }

    public User()
    {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatusinfo() {
        return statusinfo;
    }

    public void setStatusinfo(String statusinfo) {
        this.statusinfo = statusinfo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
