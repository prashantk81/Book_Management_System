package com.sismics.books.core.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.sismics.books.core.model.jpa.CommonLibrary;
import com.sismics.books.core.model.jpa.CommonlibraryUserBook;
import com.sismics.util.context.ThreadLocalContext;

public class CommonlibraryUserBookDao {
	
	
	public String create(CommonlibraryUserBook commonlibraryUserBook) {
		 EntityManager em = ThreadLocalContext.get().getEntityManager();

	        em.persist(commonlibraryUserBook);
			return commonlibraryUserBook.getId();
	}

	public CommonlibraryUserBook getRatingById(String userBookKey) {
		EntityManager em = ThreadLocalContext.get().getEntityManager();
		try {
            return em.find(CommonlibraryUserBook.class, userBookKey);
        } catch (NoResultException e) {
            return null;
        }
	}
	


	public void updateRating(CommonlibraryUserBook commonlibraryUserBook) {
		String id = commonlibraryUserBook.getId();
		double rating= commonlibraryUserBook.getRating();
		String jpql = "UPDATE CommonlibraryUserBook SET rating = :rating WHERE id = :id";
		EntityManager em = ThreadLocalContext.get().getEntityManager();
		Query query = em.createQuery(jpql);
		query.setParameter("rating", rating);
	    query.setParameter("id", id);
	}

}
