package org.openmrs.mobile.models.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Session {

    @SerializedName("sessionId")
    @Expose
    private String sessionId;
    @SerializedName("authenticated")
    @Expose
    private boolean authenticated;
    @SerializedName("user")
    @Expose
    private User user;

    public Session(String sessionId, boolean authenticated, User user) {
        this.sessionId = sessionId;
        this.authenticated = authenticated;
        this.user = user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
