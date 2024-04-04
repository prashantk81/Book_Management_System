package com.sismics.books.usercreation;

import org.codehaus.jettison.json.JSONException;

public interface UserCreationHandler {
	void processApplication(UserCreationContext userContext) throws Exception;
}