package com.sismics.books.core.service;

import java.io.IOException;

import org.codehaus.jackson.node.ArrayNode;

public class AudiobookPodcastService {
	private OnlineServiceProviderStrategy onlineServiceProviderStrategy;
	
	public AudiobookPodcastService(OnlineServiceProviderStrategy onlineServiceProviderStrategy) {
		this.onlineServiceProviderStrategy = onlineServiceProviderStrategy;
	}
	public ArrayNode search(String query, String type) throws IOException {
		return this.onlineServiceProviderStrategy.search(query, type);
	}
}