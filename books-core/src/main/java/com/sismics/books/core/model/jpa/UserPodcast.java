package com.sismics.books.core.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Objects;

@Entity
@Table(name = "T_PODCAST")
public class UserPodcast{
    /**
     * UserPodcast ID.
     */
    @Id
    @Column(name = "POD_ID_C", length = 36)
    private String id;
    
    /**
     * User ID.
     */
    @Column(name = "POD_USER_ID_C", nullable =false, length = 36)
    private String userId;
    
    /**
     * UserPodcast Audio Url.
     */
    @Column(name = "POD_AUDIO_URL_C", nullable = false, length = 255)
    private String audioUrl;
    
    /**
     * UserPodcast Image Url.
     */
    @Column(name = "POD_IMAGE_URL_C", nullable = false, length = 255)
    private String imageUrl;
    
    /**
     * UserPodcast Name
     */
    @Column(name = "POD_NAME_C", nullable = false, length = 255)
    private String name;
    
    /**
     * UserPodcast Provider
     */
    @Column(name = "POD_PROVIDER_C", nullable = false, length = 255)
    private String provider;
    

    public String getId() {
		return id;
	}




	public void setId(String id) {
		this.id = id;
	}




	public String getUserId() {
		return userId;
	}




	public void setUserId(String userId) {
		this.userId = userId;
	}




	public String getAudioUrl() {
		return audioUrl;
	}




	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
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
