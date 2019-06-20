package com.dub.spring.actors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.dub.spring.site.actors.CreateActorPhoto;
import com.dub.spring.entities.Actor;
import com.dub.spring.entities.ActorPhoto;

public interface ActorServices {
	
	List<Actor> getAllActors();
	
	Actor getActor(long id);
	
	Actor getActor(String firstName, String lastName);
	
	void deleteActor(long id);
	
	void addActor(Actor actor);
	
	void updateActor(Actor actor);
	
	long numberOfActors();

	void createActorPhoto(CreateActorPhoto photo) 
			throws FileNotFoundException, IOException;
	
	// here id is the key of actorImages table
	byte[] getPhotoData(long id);
	
	ActorPhoto getPhoto(long id);
		
	Long getPhotoId(Actor actor);
	
	List<Long> getAllPhotoIds(Actor actor);
	
	void deletePhoto(long id);
	
}
