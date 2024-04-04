package com.sismics.books.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.AbstractIdleService;
import com.sismics.books.core.dao.jpa.BookDao;
import com.sismics.books.core.dao.jpa.TagDao;
import com.sismics.books.core.dao.jpa.UserBookDao;
import com.sismics.books.core.dao.jpa.dto.TagDto;
import com.sismics.books.core.event.BookImportedEvent;
import com.sismics.books.core.model.context.AppContext;
import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.model.jpa.Tag;
import com.sismics.books.core.model.jpa.UserBook;
import com.sismics.books.core.util.TransactionUtil;
import com.sismics.books.core.util.math.MathUtil;

import au.com.bytecode.opencsv.CSVReader;

public class BookImportService extends AbstractIdleService{

	/**
	 * Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(BookImportService.class);
    
	private BookDao bookDao = new BookDao();
    private UserBookDao userBookDao = new UserBookDao();
    private TagDao tagDao = new TagDao();

    @Override
    protected void startUp() throws Exception {
    	if (log.isInfoEnabled()) {
    		log.info("Book import service started");
    	}
    }

    public void importBooks(BookImportedEvent bookImportedEvent) throws Exception {
    	CSVReader reader = null;
    	File importFile = bookImportedEvent.getImportFile();
        try {
        	reader = new CSVReader(new FileReader(importFile));
        } catch (FileNotFoundException e) {
            log.error("Unable to read CSV file", e);
            return;
        }
        
        try{   
			String[] line;
			while ((line = reader.readNext()) != null) {
			    if (isHeaderLine(line)) {
			        continue;
			    }
			    
			    String isbn = getISBNFromLine(line);
			    if (isbn == null) {
			        log.warn("No ISBN number found in line: " + Arrays.toString(line));
			        continue;
			    }
			    
			    processBookLine(bookImportedEvent, isbn, line);
			    
                TransactionUtil.commit();
			}
        } catch (Exception e) {
            log.error("Error parsing CSV line", e);
        }
    }

    private boolean isHeaderLine(String[] line) {
        return line[0].equals("Book Id"); // Use constant for header check
    }

    private String getISBNFromLine(String[] line) {
        return Strings.isNullOrEmpty(line[6]) ? line[5] : line[6];
    }

    private void processBookLine(BookImportedEvent event, String isbn, String[] line) throws Exception {
        Book book = getOrCreateBook(isbn);
        UserBook userBook = getOrCreateUserBook(event, book, line);
        Set<String> tagIdSet = createOrUpdateTags(event, line);
        
        addTagsToUserBook(userBook, tagIdSet);
    }
    
    private Book getOrCreateBook(String isbn) throws Exception {
        Book book = bookDao.getByIsbn(isbn);
        if (book == null) {
        	try {
                book = AppContext.getInstance().getBookDataService().searchBook(isbn);
            } catch (Exception e) {
                return null;
            }
        	bookDao.create(book);
        }
        return book;
    }

    private UserBook getOrCreateUserBook(BookImportedEvent event, Book book, String[] line) {
        UserBook userBook = userBookDao.getByBook(book.getId(), event.getUser().getId());
        if (userBook == null) {
        	userBook = new UserBook();
            userBook.setUserId(event.getUser().getId());
            userBook.setBookId(book.getId());
            userBook.setCreateDate(new Date());
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
            if (!Strings.isNullOrEmpty(line[14])) {
                userBook.setReadDate(formatter.parseDateTime(line[14]).toDate());
            }
            if (!Strings.isNullOrEmpty(line[15])) {
                userBook.setCreateDate(formatter.parseDateTime(line[15]).toDate());
            }
            userBookDao.create(userBook);
        }
        return userBook;
    }

    private Set<String> createOrUpdateTags(BookImportedEvent event, String[] line) {
        // Extract logic for tag creation/update to separate method
    	String[] bookshelfArray = line[16].split(",");
        Set<String> tagIdSet = new HashSet<>();
        for (String bookshelf : bookshelfArray) {
            bookshelf = bookshelf.trim();
            if (!Strings.isNullOrEmpty(bookshelf)) {
                Tag tag = getOrCreateTag(event, bookshelf, tagDao);
                tagIdSet.add(tag.getId());
            }
        }
        return tagIdSet;
    }
    
    private Tag getOrCreateTag(BookImportedEvent event, String bookshelf, TagDao tagDao) {
        Tag tag = tagDao.getByName(event.getUser().getId(), bookshelf);
        if (tag == null) {
            tag = new Tag();
            tag.setName(bookshelf);
            tag.setColor(MathUtil.randomHexColor());
            tag.setUserId(event.getUser().getId());
            tagDao.create(tag);
        }
        return tag;
    }
    
    private void addTagsToUserBook(UserBook userBook, Set<String> tagIdSet) {
        if (!tagIdSet.isEmpty()) {
            List<TagDto> tagDtoList = this.tagDao.getByUserBookId(userBook.getId());
            for (TagDto tagDto : tagDtoList) {
                tagIdSet.add(tagDto.getId());
            }
            tagDao.updateTagList(userBook.getId(), tagIdSet);
        }
    }
    
    
    @Override
    protected void shutDown() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Book import service stopped");
        }
    }
}