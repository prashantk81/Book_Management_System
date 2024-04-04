package com.sismics.books.core.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Objects;

/**
 * Book entity.
 * 
 * @author bgamard
 */
@Entity
@Table(name = "T_AUDIOBOOK")
public class UserAudiobook {
    /**
     * UserAudiobook ID.
     */
    @Id
    @Column(name = "AB_ID_C", length = 36)
    private String id;
    
    /**
     * User ID.
     */
    @Column(name = "AB_USER_ID_C", nullable =false, length = 36)
    private String userId;
    
	/**
     * UserAudiobook Author.
     */
    @Column(name = "AB_AUTHOR_C", nullable = false, length = 255)
    private String author;
    
    /**
     * UserAudiobook Preview Url.
     */
    @Column(name = "AB_PREVIEW_URL_C", nullable = false, length = 255)
    private String previewUrl;
    
    /**
     * UserAudiobook Image Url.
     */
    @Column(name = "AB_IMAGE_URL_C", nullable = false, length = 255)
    private String imageUrl;
    
    /**
     * UserAudiobook Name
     */
    @Column(name = "AB_NAME_C", nullable = false, length = 255)
    private String name;
    
    /**
     * UserAudiobook Description.
     */
    @Column(name = "AB_DESCRIPTION_C", nullable = false, length = 4000)
    private String description;
    
    /**
     * UserAudiobook Narrator.
     */
    @Column(name = "AB_NARRATOR_C", nullable = false, length = 255)
    private String narrator;
    
    /**
     * UserPodcast Provider
     */
    @Column(name = "AB_PROVIDER_C", nullable = false, length = 255)
    private String provider;
    
    public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getAuthor() {
		return author;
	}



	public void setAuthor(String author) {
		this.author = author;
	}



	public String getPreviewUrl() {
		return previewUrl;
	}



	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}



	public String getImageUrl() {
		return imageUrl;
	}



	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}

    public String getNarrator() {
		return narrator;
	}



	public void setNarrator(String narrator) {
		this.narrator = narrator;
	}



	public String getUserId() {
		return userId;
	}



	public void setUserId(String userId) {
		this.userId = userId;
	}



	public String getProvider() {
		return provider;
	}



	public void setProvider(String provider) {
		this.provider = provider;
	}



	@Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .toString();
    }
}
