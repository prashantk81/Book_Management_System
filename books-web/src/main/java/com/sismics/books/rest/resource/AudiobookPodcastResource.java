package com.sismics.books.rest.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sismics.books.core.dao.jpa.AudiobookDao;
import com.sismics.books.core.dao.jpa.PodcastDao;
import com.sismics.books.core.model.context.AppContext;
import com.sismics.books.core.model.jpa.UserAudiobook;
import com.sismics.books.core.model.jpa.UserPodcast;
import com.sismics.books.core.service.AudiobookPodcastService;
import com.sismics.books.core.service.OnlineServiceProviderStrategy;
import com.sismics.rest.exception.ForbiddenClientException;

/**
 * Tag REST resources.
 * 
 * @author bgamard
 */
@Path("/thirdparty")
public class AudiobookPodcastResource extends BaseResource{
	@GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
	public Response search(
			@QueryParam("query") String query,
			@QueryParam("type") String type,
			@QueryParam("provider") String provider) throws JSONException, IOException {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        
        OnlineServiceProviderStrategy onlineServiceProviderStrategy = provider.equals("spotify") ? AppContext.getInstance().getSpotifyService(): AppContext.getInstance().getItunesService(); 
        
        ArrayNode items = new AudiobookPodcastService(onlineServiceProviderStrategy).search(query, type);
        List<JSONObject> itemsToReturn = createJsonObject(items);
        
        JSONObject response = new JSONObject();
        response.put(type+"s", itemsToReturn);
        return Response.ok().entity(response).build();
    }
	
	private List<JSONObject> createJsonObject(ArrayNode items) throws JSONException {
		List<JSONObject> itemsToReturn = new ArrayList<JSONObject>();
        
        Iterator<JsonNode> iterator = items.getElements();
        while (iterator.hasNext()) {
            JsonNode item = iterator.next();
            JSONObject itemToReturn = new JSONObject();
            Iterator<String> fieldNames = item.getFieldNames();
            while (fieldNames.hasNext()) {
            	String fieldName = fieldNames.next(); 
            	itemToReturn.put(fieldName, item.get(fieldName).getValueAsText());
			}
            itemsToReturn.add(itemToReturn);
        }
        
        return itemsToReturn;
	}
	
	@GET
	@Path("/favourites/podcasts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFavouritePodcasts(
			@QueryParam("provider") String provider) throws JSONException {
		if (!authenticate()) {
            throw new ForbiddenClientException();
        }
		String userId = principal.getId();
		
		PodcastDao userPodcastDao = new PodcastDao();
		List<UserPodcast> podcasts = userPodcastDao.getAllPodcasts(userId, provider);
		
		List<JSONObject> itemsToReturn = new ArrayList<JSONObject>();
        
        for (UserPodcast podcast : podcasts) {
            JSONObject itemToReturn = new JSONObject();
    		itemToReturn.put("audio_url", podcast.getAudioUrl());
            itemToReturn.put("name", podcast.getName());
            itemToReturn.put("imageUrl", podcast.getImageUrl());
            itemsToReturn.add(itemToReturn);
        }
        
        JSONObject response = new JSONObject();
        response.put("podcasts", itemsToReturn);
        return Response.ok().entity(response).build();
	}
	
	@GET
	@Path("/favourites/audiobooks")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFavouriteAudiobooks(
			@QueryParam("provider") String provider) throws JSONException {
		if (!authenticate()) {
            throw new ForbiddenClientException();
        }
		String userId = principal.getId();
		
		AudiobookDao userAudiobookDao = new AudiobookDao();
		List<UserAudiobook> audiobooks = userAudiobookDao.getAllAudiobooks(userId, provider);
		
		List<JSONObject> itemsToReturn = new ArrayList<JSONObject>();
        
        for (UserAudiobook audiobook : audiobooks) {
            JSONObject itemToReturn = new JSONObject();
            itemToReturn.put("name", audiobook.getName());
            itemToReturn.put("author", audiobook.getAuthor());
            itemToReturn.put("imageUrl", audiobook.getImageUrl());
            itemToReturn.put("previewUrl", audiobook.getPreviewUrl());
            itemToReturn.put("description", audiobook.getDescription());
            itemToReturn.put("narrator", audiobook.getNarrator());
            itemsToReturn.add(itemToReturn);
        }
        
        JSONObject response = new JSONObject();
        response.put("audiobooks", itemsToReturn);
        return Response.ok().entity(response).build();
	}
	
	@POST
	@Path("/favourites/podcast")
	@Produces(MediaType.APPLICATION_JSON)
	public Response savePodcast(
			@FormParam("audio_url") String audioUrl,
			@FormParam("name") String name,
			@FormParam("imageUrl") String imageUrl,
			@FormParam("provider") String provider) throws JSONException, IOException {
		if (!authenticate()) {
            throw new ForbiddenClientException();
        }
		String userId = principal.getId();
		
		PodcastDao userPodcastDao = new PodcastDao();
		
		UserPodcast podcast = new UserPodcast();
		podcast.setUserId(userId);
		podcast.setAudioUrl(audioUrl);
		podcast.setImageUrl(imageUrl);
		podcast.setName(name);
		podcast.setProvider(provider);
		podcast.setId(UUID.randomUUID().toString());
	
		userPodcastDao.create(podcast);
		
		JSONObject response = new JSONObject();
		response.put("id", podcast.getId());
        return Response.ok().entity(response).build();
    }
	
	@POST
	@Path("/favourites/audiobook")
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveAudiobook(
			@FormParam("previewUrl") String previewUrl,
			@FormParam("name") String name,
			@FormParam("imageUrl") String imageUrl,
			@FormParam("provider") String provider,
			@FormParam("author") String author,
			@FormParam("narrator") String narrator,
			@FormParam("description") String description) throws JSONException, IOException {
		if (!authenticate()) {
            throw new ForbiddenClientException();
        }
		String userId = principal.getId();
		
		AudiobookDao userAudiobookDao = new AudiobookDao();
		
		UserAudiobook audiobook = new UserAudiobook();
		audiobook.setUserId(userId);
		audiobook.setAuthor(author);
		audiobook.setImageUrl(imageUrl);
		audiobook.setName(name);
		audiobook.setProvider(provider);
		audiobook.setDescription(description);
		audiobook.setNarrator(narrator);
		audiobook.setPreviewUrl(previewUrl);
		audiobook.setId(UUID.randomUUID().toString());
	
		userAudiobookDao.create(audiobook);
		
		JSONObject response = new JSONObject();
		response.put("id", audiobook.getId());
        return Response.ok().entity(response).build();
    }
}
