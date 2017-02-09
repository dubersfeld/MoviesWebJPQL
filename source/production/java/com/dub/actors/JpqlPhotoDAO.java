package com.dub.actors;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dub.entities.Actor;
import com.dub.entities.ActorPhoto;
import com.dub.exceptions.PhotoNotFoundException;


@Repository
public class JpqlPhotoDAO implements PhotoDAO {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	@Override
	@Transactional
	public void create(ActorPhoto photo)
	{
		entityManager.persist(photo);
		entityManager.flush();
	}
	
	@Override
	public byte[] getPhotoData(long photoId) 
	{
		ActorPhoto photo = entityManager.find(ActorPhoto.class, photoId);
		if (photo != null) {
			return photo.getImageData();
		} else {
	    	throw new PhotoNotFoundException();
	    }			
	}
	
	@Override
	public ActorPhoto getPhoto(long photoId) 
	{
		ActorPhoto photo = entityManager.find(ActorPhoto.class, photoId);
		if (photo != null) {
			return photo;
		} else {
	    	throw new PhotoNotFoundException();
	    }			
	}
	
	
	@Override
	public List<Long> getAllPhotoIds(Actor actor) {
		return entityManager.createQuery(
				"SELECT photo.id FROM ActorPhoto photo " + 
				"WHERE photo.actorId = :actorId", Long.class)
				.setParameter("actorId", actor.getId()).getResultList();		
	}
	
	
	@Override
	@Transactional
	public void delete(long id) {
		ActorPhoto photo = entityManager.find(ActorPhoto.class, id);
		if (photo != null) {
		entityManager.remove(photo);
		entityManager.flush();
		} else {
			throw new PhotoNotFoundException();
		}
	}
}