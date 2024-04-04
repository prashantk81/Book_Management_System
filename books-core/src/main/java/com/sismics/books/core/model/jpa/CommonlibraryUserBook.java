package com.sismics.books.core.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_COMMON_LIBRARY_USERBOOK")
public class CommonlibraryUserBook {
	@Id
	@Column(name = "CL_USERBOOK_ID", length = 255)
	private String id;
	
	@Column(name = "CL_USERBOOK_RATING")
	private double rating;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}
	@Override
	public String toString() {
		return "CommonlibraryUserBook [id=" + id + ", rating=" + rating + "]";
	}
}
