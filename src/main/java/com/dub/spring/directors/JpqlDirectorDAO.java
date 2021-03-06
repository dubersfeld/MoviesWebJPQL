package com.dub.spring.directors;

import java.text.ParseException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dub.spring.exceptions.DuplicateDirectorException;
import com.dub.spring.site.DateCorrect;
import com.dub.spring.entities.Director;
import com.dub.spring.exceptions.DirectorNotFoundException;


@Repository
public class JpqlDirectorDAO implements DirectorDAO {
	
	@PersistenceContext
	private EntityManager entityManager;

	
	@Override
	@Transactional
	public void update(Director director) {
		try {
			director.setBirthDate(DateCorrect.correctDate(director.getBirthDate()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			director.setBirthDate(DateCorrect.correctDate(director.getBirthDate()));
			entityManager.persist(director);
			entityManager.flush();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
