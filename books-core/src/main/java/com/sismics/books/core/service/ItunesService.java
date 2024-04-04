package com.sismics.books.core.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractIdleService;


public class ItunesService extends AbstractIdleService implements OnlineServiceProviderStrategy{

	/**
	 * Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(ItunesService.class);
    
	@Override
	protected void startUp() throws Exception {
		if (log.isInfoEnabled()) {
			log.info("iTunes service started");
		}
	}
	
    @Override
    protected void shutDown() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("iTunes service stopped");
        }
    }

	@Override
	public ArrayNode search(String query, String type) throws IOException {
		String searchTerm = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String BASE_URI="https://itunes.apple.com/search?term=%s&media=%s%s&limit=20";
        String entity = type.equals("audiobook") ? "" : "&entity=podcastEpisode"; 
        URL url = new URL(String.format(Locale.ENGLISH, BASE_URI, searchTerm, type, entity));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty("Accept", "application/json");
		connection.setRequestMethod("GET");

        InputStream inputStream = connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();   
        JsonNode rootNode = mapper.readValue(inputStream, JsonNode.class);
        ArrayNode items = (ArrayNode) rootNode.get("results");
        
        return type.equals("podcast") ? createPodcastObject(items) : createAudiobookObject(items);
        
	}
	
	private ArrayNode createPodcastObject(ArrayNode items) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode podcasts = mapper.createArrayNode();
		Iterator<JsonNode> iterator = items.getElements();
        while (iterator.hasNext()) {
            JsonNode episode = iterator.next();
            ObjectNode podcast = mapper.createObjectNode();
            podcast.put("audio_url", episode.get("episodeUrl").getValueAsText());
            podcast.put("name", episode.get("trackName").getValueAsText());
            podcast.put("imageUrl", episode.get("artworkUrl160").getValueAsText());
            podcasts.add(podcast);
        }
        return podcasts;
	}
	
	private ArrayNode createAudiobookObject(ArrayNode items) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode audiobooks = mapper.createArrayNode();
		Iterator<JsonNode> iterator = items.getElements();
        while (iterator.hasNext()) {
            JsonNode book = iterator.next();
            ObjectNode audiobook = mapper.createObjectNode();
            audiobook.put("name", book.get("collectionName").getValueAsText());
            audiobook.put("author", book.get("artistName").getValueAsText());
            audiobook.put("imageUrl", book.get("artworkUrl100").getValueAsText());
            audiobook.put("previewUrl", book.get("previewUrl").getValueAsText());
            audiobook.put("description", book.get("description").getValueAsText());
            audiobooks.add(audiobook);
        }
        return audiobooks;
	}

	@Override
	public void saveToFavourites() {
		// TODO Auto-generated method stub
		
	}
}