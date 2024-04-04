package com.sismics.books.core.dao.jpa;

import java.util.List;
import javax.persistence.Query;



import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.model.jpa.CommonLibrary;
import com.sismics.util.context.ThreadLocalContext;

public class CommonLibraryDao {
	
	public String create(CommonLibrary commonLibrary) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        em.persist(commonLibrary);
		return commonLibrary.getId();
	}
	
	@SuppressWarnings("unchecked")
	public List<CommonLibrary> getAll(){
		EntityManager em = ThreadLocalContext.get().getEntityManager();
		String jpql = "select e from CommonLibrary e";
		Query query = em.createQuery(jpql);
       return query.getResultList();
	}
	
	public CommonLibrary getById(String id) {
		EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            return em.find(CommonLibrary.class, id);
        } catch (NoResultException e) {
            return null;
        }
	}
	
	@SuppressWarnings("unchecked")
	public List<CommonLibrary> getBookByAuthorName(String authorName) {
		EntityManager em = ThreadLocalContext.get().getEntityManager();
		String jpql = "select e from CommonLibrary e where e.author LIKE :authorName";
		Query query = em.createQuery(jpql);
		query.setParameter("authorName","%"+authorName+"%");
	       return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<CommonLibrary> getBookByGenreName(String genreName) {
		EntityManager em = ThreadLocalContext.get().getEntityManager();
		String jpql = "select e from CommonLibrary e where e.genre LIKE :genreName";
		Query query = em.createQuery(jpql);
		query.setParameter("genreName","%"+genreName+"%");
	       return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<CommonLibrary> getBookByRating(double rating) {
		EntityManager em = ThreadLocalContext.get().getEntityManager();
		String jpql = "select e from CommonLibrary e where e.rating > :rating";
		Query query = em.createQuery(jpql);
		query.setParameter("rating",rating);
	       return query.getResultList();
	}

}
