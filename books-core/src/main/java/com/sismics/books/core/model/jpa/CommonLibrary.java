package com.sismics.books.core.model.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_COMMON_LIBRARY")
public class CommonLibrary {

	@Id
	@Column(name = "CL_BOOK_ID", length = 36)
	private String id;

	@Column(name = "CL_BOOK_TITLE", nullable = false, length = 255)
	private String title;
	
	@Column(name = "CL_SUBTITLE", length = 255)
    private String subtitle;
	
	@Column(name = "CL_BOOK_AUTHOR", nullable = false, length = 255)
	private String author;

	@Column(name = "CL_DESCRIPTION", length = 4000)
    private String description;
	
	@Column(name = "CL_BOOK_GENRE")
	private String genre;
	
	@Column(name = "CL_BOOK_RATING")
	private double rating;
	
	@Column(name = "CL_BOOK_N_RATING")
	private Integer noOfRatings;

	@Column(name = "CL_ISBN10", length = 10)
    private String isbn10;
	
	 @Column(name = "CL_ISBN13", length = 13)
	 private String isbn13;
	 
	 @Column(name = "CL_PAGECOUNT")
	    private Long pageCount;
	 
	 @Column(name = "CL_LANGUAGE", length = 2)
	    private String language;
	 
	 @Column(name = "CL_PUBLISHDATE", nullable = false)
	    private Date publishDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public Integer getNoOfRatings() {
		return noOfRatings;
	}

	public void setNoOfRatings(Integer noOfRatings) {
		this.noOfRatings = noOfRatings;
	}

	public String getIsbn10() {
		return isbn10;
	}

	public void setIsbn10(String isbn10) {
		this.isbn10 = isbn10;
	}

	public String getIsbn13() {
		return isbn13;
	}

	public void setIsbn13(String isbn13) {
		this.isbn13 = isbn13;
	}

	public Long getPageCount() {
		return pageCount;
	}

	public void setPageCount(Long pageCount) {
		this.pageCount = pageCount;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	@Override
	public String toString() {
		return "CommonLibrary [id=" + id + ", title=" + title + ", subtitle=" + subtitle + ", author=" + author
				+ ", description=" + description + ", genre=" + genre + ", rating=" + rating + ", noOfRatings="
				+ noOfRatings + ", isbn10=" + isbn10 + ", isbn13=" + isbn13 + ", pageCount=" + pageCount + ", language="
				+ language + ", publishDate=" + publishDate + "]";
	}
	 
	
	
	

}
