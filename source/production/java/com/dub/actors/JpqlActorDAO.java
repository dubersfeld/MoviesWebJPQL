package com.dub.actors;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


import org.apache.commons.lang3.exception.ExceptionUtils;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dub.entities.Actor;
import com.dub.exceptions.ActorNotFoundException;
import com.dub.exceptions.DuplicateActorException;


@Repository
public class JpqlActorDAO implements ActorDAO {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	@Override
	@Transactional
	public void update(Actor actor) {
		entityManager.merge(actor);
		entityManager.flush();		
	}
	
	
	@Override
	@Transactional
	public void delete(long id) {		
		Actor actor = entityManager.find(Actor.class, id);
		if (actor != null) {
			entityManager.remove(actor);
			entityManager.flush();
		} else {
			throw new ActorNotFoundException();
		}		  
	}

	
	@Override
	public List<Actor> listAllActors() {
		return this.entityManager.createQuery(
				"SELECT a FROM Actor a", Actor.class
		).getResultList();
	}
	
	@Override
	public long getNumberOfActors() {
		return (long) this.entityManager.createQuery(
				"SELECT COUNT(a) FROM Actor a").getSingleResult();
	}
	
	
	@Override
	public Actor getActor(long id) {
		Actor actor = entityManager.find(Actor.class, id);
		if (actor != null) {
			return actor;
		} else {
			throw new ActorNotFoundException();
		}
	}
	
	@Override
	@Transactional
	public void add(Actor actor) {
		try {
			entityManager.persist(actor);
			entityManager.flush();
		} catch (Exception e ) {
			String ex = ExceptionUtils.getRootCauseMessage(e);
			if (ex.contains("actor_unique")) {
				throw new DuplicateActorException();
			} else {
				throw e;
			}
		}
	}
	
	@Override
	public Actor getActor(String firstName, String lastName) {
		try {
			Actor actor = entityManager.createQuery(
				"SELECT a FROM Actor a " + 
		        "WHERE a.firstName = :firstName AND a.lastName = :lastName", 
		        	Actor.class)
				.setParameter("firstName", firstName)
				.setParameter("lastName", lastName)
				.getSingleResult();
			return actor;
		} catch (NoResultException e) {
			throw new ActorNotFoundException();			
		}
	}

}