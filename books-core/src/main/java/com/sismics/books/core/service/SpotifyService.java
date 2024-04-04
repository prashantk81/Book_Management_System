package com.sismics.books.core.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractIdleService;


public class SpotifyService extends AbstractIdleService implements OnlineServiceProviderStrategy{

	/**
	 * Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(SpotifyService.class);
	final String CLIENT_ID="385a2ee78cc14c8793cf29c08aa63f91";
	final String CLIENT_SECRET="f3e4f9a58d2d4fd6a4a45dad6cfd6be0";
	final String AUTH_URL = "https://accounts.spotify.com/api/token";
    
	@Override
	protected void startUp() throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Spotify service started");
		}
	}
	
    @Override
    protected void shutDown() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Spotify service stopped");
        }
    }

    private String getAccessToken() throws IOException {
        URL url = new URL(AUTH_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);

        connection.setRequestMethod("POST");
        byte[] creds = (CLIENT_ID + ":" + CLIENT_SECRET).getBytes("UTF-8");
        String encodedCreds = DatatypeConverter.printBase64Binary(creds);
        connection.setRequestProperty("Authorization", "Basic " + encodedCreds);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        String body = "grant_type=client_credentials";
        byte[] out = body.getBytes("UTF-8");
        int length = out.length; 
        connection.setFixedLengthStreamingMode(length);
        
        connection.connect();
        
        try(OutputStream os = connection.getOutputStream()) {
            os.write(out);
        }
        
        InputStream inputStream = connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();   
        JsonNode rootNode = mapper.readValue(inputStream, JsonNode.class);
        return rootNode.get("access_token").getValueAsText();
	}

	@Override
	public ArrayNode search(String query, String type) throws IOException {
		String searchTerm = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String BASE_URI="https://api.spotify.com/v1/search?q=%s&type=%s&market=US&limit=20";
        URL url = new URL(String.format(Locale.ENGLISH, BASE_URI, searchTerm, type));

        String accessToken = getAccessToken();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization","Bearer "+ accessToken);
		connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty("Accept", "application/json");
		connection.setRequestMethod("GET");

        InputStream inputStream = connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();   
        JsonNode rootNode = mapper.readValue(inputStream, JsonNode.class);
        ArrayNode items = (ArrayNode) rootNode.get(type+"s").get("items");
        
        return type.equals("episode") ? createPodcastObject(items) : createAudiobookObject(items);
        
	}
	
	private ArrayNode createPodcastObject(ArrayNode items) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode podcasts = mapper.createArrayNode();
		Iterator<JsonNode> iterator = items.getElements();
        while (iterator.hasNext()) {
            JsonNode episode = iterator.next();
            ObjectNode podcast = mapper.createObjectNode();
            podcast.put("audio_url", episode.get("audio_preview_url").getValueAsText());
            podcast.put("name", episode.get("name").getValueAsText());
            podcast.put("imageUrl", episode.get("images").get(2).get("url").getValueAsText());
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
            audiobook.put("author", book.get("authors").get(0).get("name").getValueAsText());
            audiobook.put("narrator", book.get("narrators").get(0).get("name").getValueAsText());
            audiobook.put("name", book.get("name").getValueAsText());
            audiobook.put("imageUrl", book.get("images").get(2).get("url").getValueAsText());
            audiobook.put("description", book.get("html_description").getValueAsText());
            audiobooks.add(audiobook);
        }
        return audiobooks;
	}

	@Override
	public void saveToFavourites() {
		// TODO Auto-generated method stub
		
	}
}