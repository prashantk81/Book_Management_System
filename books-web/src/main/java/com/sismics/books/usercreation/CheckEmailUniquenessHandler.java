package com.sismics.books.usercreation;

import com.sismics.books.core.dao.jpa.UserDao;
import com.sismics.books.core.model.jpa.User;
import com.sismics.rest.exception.ServerException;

public class CheckEmailUniquenessHandler implements UserCreationHandler{
	private UserCreationHandler nextHandler;


	public void setNextHandler(UserCreationHandler nextHandler) {
		this.nextHandler = nextHandler;
	}
	
	@Override
	public void processApplication(UserCreationContext userContext) throws Exception {
		User user = userContext.getUser();
		
		// Create the user
	    UserDao userDao = new UserDao();

	    // check for email uniqueness
	    User u = userDao.checkIfEmailExists(user.getEmail());
	    if (u != null) {
	    	userContext.setStatus("A User account already exists with the specified email");
	    	throw new ServerException("A User account already exists with the specified email", null);
	    }
	    
	    try {
	    	userDao.create(user);
	    } catch (Exception e) {
	        if ("AlreadyExistingUsername".equals(e.getMessage())) {
	        	userContext.setStatus("A User account already exists with the specfied username");
	            throw new ServerException("AlreadyExistingUsername", "Login already used", e);
	        } else {
	            throw new ServerException("UnknownError", "Unknown Server Error", e);
	        }
	    }
		
		if(nextHandler != null) {
			nextHandler.processApplication(userContext);
		}
	}
    
}