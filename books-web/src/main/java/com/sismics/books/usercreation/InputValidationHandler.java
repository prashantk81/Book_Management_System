package com.sismics.books.usercreation;

import com.sismics.rest.util.ValidationUtil;

public class InputValidationHandler implements UserCreationHandler{

	private UserCreationHandler nextHandler;


	public void setNextHandler(UserCreationHandler nextHandler) {
		this.nextHandler = nextHandler;
	}
	@Override
	public void processApplication(UserCreationContext userContext) throws Exception {

		String username = userContext.getUsername();
		String password = userContext.getPassword();
		String localeId = userContext.getLocaleId();
		String email = userContext.getEmail();

		username = ValidationUtil.validateLength(username, "username", 3, 50);
        ValidationUtil.validateAlphanumeric(username, "username");
        password = ValidationUtil.validateLength(password, "password", 8, 50);
        email = ValidationUtil.validateLength(email, "email", 3, 50);
        ValidationUtil.validateEmail(email, "email");

        userContext.setUsername(username);
        userContext.setPassword(password);
        userContext.setLocaleId(localeId);
        userContext.setEmail(email);

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