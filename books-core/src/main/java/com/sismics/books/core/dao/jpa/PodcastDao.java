package com.sismics.books.core.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.sismics.books.core.model.jpa.UserPodcast;
import com.sismics.util.context.ThreadLocalContext;

/**
 * Tag DAO.
 * 
 * @author bgamard
 */
public class PodcastDao {
	/**
     * Creates a new podcast.
     * 
     * @param podcast UserPodcast
     * @return New ID
     * @throws Exception
     */
    public String create(UserPodcast podcast) {
        // Create the book
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(podcast);
        return podcast.getId();
    }
    /**
     * Gets a podcast by its ID.
     * 
     * @param id UserPodcast ID
     * @return UserPodcast
     */
    public UserPodcast getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(UserPodcast.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Returns the list of all podcasts.
     * 
     * @return List of podcasts
     */
    @SuppressWarnings("unchecked")
    public List<UserPodcast> getAllPodcasts(String userId, String provider) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select t from UserPodcast t where t.userId = :userId and t.provider = :provider");
        q.setParameter("userId", userId);
        q.setParameter("provider", provider);
        return q.getResultList();
    }
    
}
