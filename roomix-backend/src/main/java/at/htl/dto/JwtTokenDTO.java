package at.htl.dto;

import at.htl.entity.User;

public class JwtTokenDTO {

    String token;
    long expires_at;
    User user;

    public JwtTokenDTO() {
    }

    public JwtTokenDTO(String token, long expires_at, User user) {
        this.token = token;
        this.expires_at = expires_at;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(long expires_at) {
        this.expires_at = expires_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
