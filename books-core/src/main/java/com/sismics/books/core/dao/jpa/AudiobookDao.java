package com.sismics.books.core.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.sismics.books.core.model.jpa.UserAudiobook;
import com.sismics.util.context.ThreadLocalContext;

/**
 * Tag DAO.
 * 
 * @author bgamard
 */
public class AudiobookDao {
	/**
     * Creates a new audiobook.
     * 
     * @param audiobook UserAudiobook
     * @return New ID
     * @throws Exception
     */
    public String create(UserAudiobook audiobook) {
        // Create the book
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(audiobook);
        return audiobook.getId();
    }
    /**
     * Gets a audiobook by its ID.
     * 
     * @param id UserAudiobook ID
     * @return UserAudiobook
     */
    public UserAudiobook getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(UserAudiobook.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Returns the list of all audiobook.
     * 
     * @return List of audiobooks
     */
    @SuppressWarnings("unchecked")
    public List<UserAudiobook> getAllAudiobooks(String userId, String provider) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery("select t from UserAudiobook t where t.userId = :userId and t.provider = :provider");
        q.setParameter("userId", userId);
        q.setParameter("provider", provider);
        return q.getResultList();
    }
    
}
