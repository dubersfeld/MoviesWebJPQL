package com.dub.actors;

import java.util.List;

import com.dub.entities.Actor;
import com.dub.entities.ActorPhoto;


public interface PhotoDAO 
{	  
	   /** Using automatically generated id */ 
	   void create(ActorPhoto photo);

	   byte[] getPhotoData(long photoId);
	   
	   ActorPhoto getPhoto(long photoId);
	    	   
	   List<Long> getAllPhotoIds(Actor actor);
	   
	   void delete(long photoId);
	   	   
}