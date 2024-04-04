package com.sismics.books.usercreation;


import com.sismics.books.core.model.jpa.User;

public class UserCreationContext {
    private boolean isRejected;
    private String username;
    private String password; 
    private String localeId;
    private String email;
    private User user;
    private String userId;
    private String status;

    public UserCreationContext(String username, String password, String localeId, String email) {
        isRejected = false;
        this.username = username;
        this.password = password;
        this.localeId = localeId;
        this.email = email;
        this.user = new User();
        this.setStatus("ok");
    }
    
    public User getUser() {
        return user;
    }
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocaleId() {
        return localeId;
    }

    public void setLocaleId(String localeId) {
        this.localeId = localeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public void setRejected(boolean isRejected) {
        this.isRejected = isRejected;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}