package com.dub.directors;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dub.exceptions.DuplicateDirectorException;
import com.dub.entities.Director;
import com.dub.exceptions.DirectorNotFoundException;


@Repository
public class JpqlDirectorDAO implements DirectorDAO {
	
	@PersistenceContext
	private EntityManager entityManager;

	
	@Override
	@Transactional
	public void update(Director director) {
		entityManager.merge(director);
		entityManager.flush();		
	}
	

	@Override
	@Transactional
	public void delete(long id) {
		Director director = entityManager.find(Director.class, id);
		if (director != null) {
			entityManager.remove(director);
			entityManager.flush();
		} else {
			throw new DirectorNotFoundException();
		}
	}


	@Override
	public List<Director> listAllDirectors() {	
		return this.entityManager.createQuery(
				"SELECT d FROM Director d", Director.class
		).getResultList();
	}


	@Override
	public long getNumberOfDirectors() {
		return (long) this.entityManager.createQuery(
				"SELECT COUNT(a) FROM Director a").getSingleResult();
	}


	@Override
	public Director getDirector(long id) {	
		Director director = entityManager.find(Director.class, id);	
		if (director != null) {
			return director;		
		} else {
			throw new DirectorNotFoundException();
		}
	}


	@Override
	public Director getDirector(String firstName, String lastName) {	
		try {
			Director director = entityManager.createQuery(
				"SELECT d FROM Director d " + 
		        "WHERE d.firstName = :firstName AND d.lastName = :lastName", 
		        	Director.class)
				.setParameter("firstName", firstName)
				.setParameter("lastName", lastName)
				.getSingleResult();
			return director;	
		} catch (NoResultException e) {			
			throw new DirectorNotFoundException();			
		}
	}


	@Override
	@Transactional
	public void add(Director director) {
		try {	
			entityManager.persist(director);
			entityManager.flush();
		} catch (Exception e) {
			String ex = ExceptionUtils.getRootCauseMessage(e);
			if (ex.contains("director_unique")) {
				throw new DuplicateDirectorException();
			} else {
				throw e;
			}
		}
	}
}
