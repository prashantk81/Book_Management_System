package com.sismics.books.usercreation;

import java.util.Date;


import org.codehaus.jettison.json.JSONException;

import com.sismics.books.core.constant.Constants;
import com.sismics.books.core.dao.jpa.UserDao;
import com.sismics.books.core.model.jpa.User;
import com.sismics.rest.exception.ServerException;

public class UserEntityCreationHandler implements UserCreationHandler {

	private UserCreationHandler nextHandler;
	
	public void setNextHandler(UserCreationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

	@Override
	public void processApplication(UserCreationContext userContext) throws Exception {

		String username = userContext.getUsername();
		String password = userContext.getPassword();
		String email = userContext.getEmail();

		User user = userContext.getUser();

		user.setRoleId(Constants.DEFAULT_USER_ROLE);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setCreateDate(new Date());
        user.setLocaleId(Constants.DEFAULT_LOCALE_ID);
        
        if(nextHandler != null) {
			try {
				nextHandler.processApplication(userContext);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}