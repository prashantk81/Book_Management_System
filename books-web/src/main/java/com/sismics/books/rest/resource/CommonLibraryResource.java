package com.sismics.books.rest.resource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import com.sismics.books.core.dao.jpa.CommonLibraryDao;
import com.sismics.books.core.dao.jpa.CommonlibraryUserBookDao;
import com.sismics.books.core.dao.jpa.UserBookDao;
import com.sismics.books.core.commonlibrary.AverageRatingStrategy;
import com.sismics.books.core.commonlibrary.NumberOfRatingStrategy;
import com.sismics.books.core.commonlibrary.SortingStrategy;
import com.sismics.books.core.dao.jpa.BookDao;
import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.model.jpa.CommonLibrary;
import com.sismics.books.core.model.jpa.CommonlibraryUserBook;
import com.sismics.books.core.model.jpa.UserBook;
import com.sismics.books.core.util.DirectoryUtil;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;

@Path("commonlibrary")
public class CommonLibraryResource extends BaseResource {

	private void authenticateOrThrowForbidden() throws JSONException {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}
	}

	@POST
	@Path("{id: [a-z0-9\\-]+}")
	@Produces(MediaType.APPLICATION_JSON)
	// for adding a specific book into the common library
	public Response addBookInLibrary(
			@PathParam("id") String userBookId,
			@FormParam("genre") String genres,
			@FormParam("rating") double rating) throws JSONException {
		authenticateOrThrowForbidden();
		// Get the user book
		UserBookDao userBookDao = new UserBookDao();
		String userId = principal.getId();
		UserBook userBook = userBookDao.getUserBook(userBookId, userId);
		if (userBook == null) {
			throw new ClientException("BookNotFound", "Book not found with id " + userBookId);
		}
		String bookId = userBook.getBookId();
		CommonLibraryDao commonLibraryDao = new CommonLibraryDao();

		JSONObject response = new JSONObject();
		if (commonLibraryDao.getById(bookId) != null) {
			response.put("message", "Book already exists in common library");
			return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
		}

		BookDao bookDao = new BookDao();
		Book book = bookDao.getById(bookId);

		CommonLibrary commonLibrary = new CommonLibrary();
		commonLibrary.setAuthor(book.getAuthor());
		commonLibrary.setGenre(genres);
		commonLibrary.setId(bookId);
		commonLibrary.setTitle(book.getTitle());
		commonLibrary.setRating(Math.round(rating * 10) / 10.0);
		commonLibrary.setNoOfRatings(1);
		commonLibrary.setDescription(book.getDescription());
		commonLibrary.setIsbn10(book.getIsbn10());
		commonLibrary.setIsbn13(book.getIsbn13());
		commonLibrary.setLanguage(book.getLanguage());
		commonLibrary.setPageCount(book.getPageCount());
		commonLibrary.setPublishDate(book.getPublishDate());
		commonLibrary.setSubtitle(book.getSubtitle());

		commonLibraryDao.create(commonLibrary);

		CommonlibraryUserBook commonlibraryUserBook = new CommonlibraryUserBook();
		String userBookKey = userId + commonLibrary.getId();
		commonlibraryUserBook.setId(userBookKey);
		commonlibraryUserBook.setRating(rating);

		CommonlibraryUserBookDao commonlibraryUserBookDao = new CommonlibraryUserBookDao();
		commonlibraryUserBookDao.create(commonlibraryUserBook);
		System.out.println(commonlibraryUserBook);

		response.put("status", "ok");
		return Response.ok().entity(response).build();
	}

	@GET
	@Path("list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() throws JSONException {
		authenticateOrThrowForbidden();
		CommonLibraryDao commonLibraryDao = new CommonLibraryDao();
		List<CommonLibrary> listAllBooks = commonLibraryDao.getAll();
		JSONObject response = new JSONObject();
		List<JSONObject> books = new ArrayList<>();

		for (CommonLibrary commonLibBooks : listAllBooks) {
			JSONObject book = new JSONObject();
			book.put("id", commonLibBooks.getId());
			book.put("title", commonLibBooks.getTitle());
			book.put("author", commonLibBooks.getAuthor());
			book.put("genre", commonLibBooks.getGenre());
			book.put("rating", commonLibBooks.getRating());
			books.add(book);
		}
		Integer totalBook = books.size();
		response.put("total", totalBook);
		response.put("books", books);
		return Response.ok().entity(response).build();
	}

	@GET
	@Path("{id: [a-z0-9\\-]+}/cover")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response cover(
			@PathParam("id") final String bookId) throws JSONException {
		authenticateOrThrowForbidden();

		// Get the cover image
		File file = Paths.get(DirectoryUtil.getBookDirectory().getPath(), bookId).toFile();
		InputStream inputStream = null;
		try {
			if (file.exists()) {
				inputStream = new FileInputStream(file);
			} else {
				inputStream = new FileInputStream(new File(getClass().getResource("/dummy.png").getFile()));
			}
		} catch (FileNotFoundException e) {
			throw new ServerException("FileNotFound", "Cover file not found", e);
		}

		return Response.ok(inputStream)
				.header("Content-Type", "image/jpeg")
				.header("Expires",
						new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z").format(new Date().getTime() + 3600000))
				.build();
	}

	@PUT
	@Path("changeRating")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateRating(
			@FormParam("id") String commonLibraryId,
			@FormParam("rating") String rating) throws JSONException {
		authenticateOrThrowForbidden();

		CommonLibraryDao commonLibraryDao = new CommonLibraryDao();
		CommonLibrary commonLibrary = commonLibraryDao.getById(commonLibraryId);

		String userId = principal.getId();
		String userBookKey = userId + commonLibrary.getId();

		CommonlibraryUserBookDao commonlibraryUserBookDao = new CommonlibraryUserBookDao();
		CommonlibraryUserBook oldUserBook = commonlibraryUserBookDao.getRatingById(userBookKey);
		double oldRating = commonLibrary.getRating();
		Integer totalRatings = commonLibrary.getNoOfRatings();

		if (oldUserBook == null) {
			oldUserBook = new CommonlibraryUserBook();
			oldUserBook.setId(userBookKey);
			oldUserBook.setRating(Double.valueOf(rating));
			commonlibraryUserBookDao.create(oldUserBook);
			double newRating = (oldRating * (double) totalRatings + Double.valueOf(rating))
					/ (double) (totalRatings + 1);
			commonLibrary.setRating(Math.round(newRating  * 10) / 10.0);
			commonLibrary.setNoOfRatings(totalRatings + 1);
		} else {
			double oldBookRatingGivenByUser = oldUserBook.getRating();
			System.out.println(oldBookRatingGivenByUser);
			oldUserBook.setRating(Double.valueOf(rating));
			commonlibraryUserBookDao.updateRating(oldUserBook);
			double newRating = (oldRating * (double) totalRatings - oldBookRatingGivenByUser + Double.valueOf(rating))
					/ (double) (totalRatings);
			commonLibrary.setRating(Math.round(newRating * 10) / 10.0);
		}

		JSONObject response = new JSONObject();
		response.put("status", "ok");
		return Response.ok().entity(response).build();
	}

	@GET
	@Path("{id: [a-z0-9\\-]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(
			@PathParam("id") String bookId) throws JSONException {
		CommonLibraryDao commonLibraryDao = new CommonLibraryDao();
		CommonLibrary book = commonLibraryDao.getById(bookId);

		JSONObject item = new JSONObject();

		item.put("id", book.getId());
		item.put("title", book.getTitle());
		item.put("subtitle", book.getSubtitle());
		item.put("author", book.getAuthor());
		item.put("description", book.getDescription());
		item.put("genre", book.getGenre());
		item.put("rating", book.getRating());
		item.put("noofrating", book.getNoOfRatings());
		item.put("isbn10", book.getIsbn10());
		item.put("isbn13", book.getIsbn13());
		item.put("pagecount", book.getPageCount());
		item.put("language", book.getLanguage());
		item.put("publishdate", book.getPublishDate());
		return Response.ok().entity(item).build();
	}
	@POST
	 @Path("author")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response findBookByAuthor(
	    @FormParam("authorNames") String authorNames
	   ) throws JSONException{
	  authenticateOrThrowForbidden();
	  String[] allAuthors = authorNames.split("\\$\\$");
	  CommonLibraryDao commonLibraryDao = new CommonLibraryDao();
	  List<String> allId = new ArrayList<>();
	  List<JSONObject> books = new ArrayList<>();
	  for (String author : allAuthors) {
	            // Process each author
	   List<CommonLibrary>listBook = commonLibraryDao.getBookByAuthorName(author);
	   for (CommonLibrary commonLibBooks : listBook) {
	    JSONObject book = new JSONObject();
	    book.put("id" , commonLibBooks.getId());
	    book.put("title", commonLibBooks.getTitle());
	    book.put("author", commonLibBooks.getAuthor());
	    book.put("genre",commonLibBooks.getGenre());
	    book.put("rating", commonLibBooks.getRating());
	    if(!allId.contains(commonLibBooks.getId())) {
	     allId.add(commonLibBooks.getId());
	     books.add(book);
	    }
	   }
	  }
	  
	  Integer totalBook = books.size();
	  JSONObject response = new JSONObject();
	  response.put("total",totalBook);
	        response.put("books", books);
	        return Response.ok().entity(response).build();
	 }
	 @GET
	 @Path("allAuthor")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response getAllAuthorName()throws JSONException {
	  authenticateOrThrowForbidden();
	  CommonLibraryDao commonLibraryDao = new CommonLibraryDao();
	  List<CommonLibrary> allBooks = commonLibraryDao.getAll();
	  String authorName = "";
	  for ( CommonLibrary commonLibrary : allBooks) {
	   String multiAuthor = commonLibrary.getAuthor();
	   String[] allAuthors = multiAuthor.split("\\,\\ ");
	   for (String auth : allAuthors) {
	    authorName += auth;
	    authorName += "$$"; 
	   }
	  }
	  authorName=authorName.substring(0, authorName.length() - 2);
	  System.out.println(authorName);
	  JSONObject response = new JSONObject();
	        response.put("allAuthor", authorName);
	        return Response.ok().entity(response).build();
	 }
	 
	 @POST
	 @Path("genre")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response findBookByGenre(
	    @FormParam("genreNames") String genreNames
	   ) throws JSONException{
	  authenticateOrThrowForbidden();
	  String[] allGenres = genreNames.split("\\$\\$");
	  CommonLibraryDao commonLibraryDao = new CommonLibraryDao();
	  List<String> allId = new ArrayList<>();
	  List<JSONObject> books = new ArrayList<>();
	  for (String genre : allGenres) {
	            // Process each author
	   List<CommonLibrary>listBook = commonLibraryDao.getBookByGenreName(genre);
	   for (CommonLibrary commonLibBooks : listBook) {
	    JSONObject book = new JSONObject();
	    book.put("id" , commonLibBooks.getId());
	    book.put("title", commonLibBooks.getTitle());
	    book.put("author", commonLibBooks.getAuthor());
	    book.put("genre",commonLibBooks.getGenre());
	    book.put("rating", commonLibBooks.getRating());
	    if(!allId.contains(commonLibBooks.getId())) {
	     allId.add(commonLibBooks.getId());
	     books.add(book);
	    }
	   }
	  }
	  Integer totalBook = books.size();
	  JSONObject response = new JSONObject();
	  response.put("total",totalBook);
	        response.put("books", books);
	        return Response.ok().entity(response).build();
	 }
	 
	 
	 @GET
	 @Path("allGenre")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response getAllGenreName()throws JSONException {
	  authenticateOrThrowForbidden();
	  CommonLibraryDao commonLibraryDao = new CommonLibraryDao();
	  List<CommonLibrary> allBooks = commonLibraryDao.getAll();
	  String genreName = "";
	  for ( CommonLibrary commonLibrary : allBooks) {
	   String multiGenre = commonLibrary.getGenre();
	   String[] allGenre = multiGenre.split("\\,\\ ");
	   for (String gen : allGenre) {
	    genreName += gen;
	    genreName += "$$"; 
	   }
	   
	  }
	  genreName=genreName.substring(0, genreName.length() - 2);
	  JSONObject response = new JSONObject();
	        response.put("allGenre", genreName);
	        return Response.ok().entity(response).build(); 
	 }
	 
	 
	 @POST
	 @Path("rating")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response findBookByRating(
			    @FormParam("rating") String rating
			   ) throws JSONException{
			  authenticateOrThrowForbidden();
			  CommonLibraryDao commonLibraryDao = new CommonLibraryDao();
			  List<JSONObject> books = new ArrayList<>();
			  double ratingVal = (double)(rating.charAt(1)-'0');
			  
			  List<CommonLibrary>listBook = commonLibraryDao.getBookByRating(ratingVal);
			  System.out.println(listBook.size());
			  for (CommonLibrary commonLibBooks : listBook) {
			   JSONObject book = new JSONObject();
			   book.put("id" , commonLibBooks.getId());
			   book.put("title", commonLibBooks.getTitle());
			   book.put("author", commonLibBooks.getAuthor());
			   book.put("genre",commonLibBooks.getGenre());
			   book.put("rating", commonLibBooks.getRating());
			   books.add(book);
			  }
			  Integer totalBook = books.size();
			  JSONObject response = new JSONObject();
			  response.put("total",totalBook);
			        response.put("books", books);
			        return Response.ok().entity(response).build();
			 }
	 
	 @GET
	    @Path("rankingData")
		@Produces(MediaType.APPLICATION_JSON)
	    public Response getData(@QueryParam("param") String param) throws JSONException {
	        SortingStrategy sortingStrategy;
	        
	        if(param.equals("averageRating")) {
	          sortingStrategy = new AverageRatingStrategy();
	        }
	        else {
	         sortingStrategy = new NumberOfRatingStrategy();
	        }
	        
	        CommonLibraryDao commonLibraryDao = new CommonLibraryDao();
	        List<CommonLibrary> listAllBooks = commonLibraryDao.getAll();
	        List<CommonLibrary> sortedbooks = sortingStrategy.sort(listAllBooks);
	        
	        for (CommonLibrary co : listAllBooks ) {
	        	System.out.println(co);
	        }
	        
	        JSONObject response = new JSONObject();
			List<JSONObject> books = new ArrayList<>();
			int count = 1;
	        for (CommonLibrary commonLibBooks : sortedbooks) {
				JSONObject book = new JSONObject();
				book.put("id" , commonLibBooks.getId());
				book.put("title", commonLibBooks.getTitle());
				book.put("author", commonLibBooks.getAuthor());
				book.put("genre",commonLibBooks.getGenre());
				book.put("rating", commonLibBooks.getRating());
				books.add(book);
				if(count == 10) {
					break;
				}
				count++;
			}
			Integer totalBook = books.size();
			response.put("total",totalBook);
	        response.put("books", books);
	        return Response.ok().entity(response).build();
	    }



	
	
	
}