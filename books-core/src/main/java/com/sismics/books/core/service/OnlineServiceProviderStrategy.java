package com.sismics.books.core.service;

import java.io.IOException;

import org.codehaus.jackson.node.ArrayNode;

public interface OnlineServiceProviderStrategy{
	public ArrayNode search(String query, String type) throws IOException;
	public void saveToFavourites();
}