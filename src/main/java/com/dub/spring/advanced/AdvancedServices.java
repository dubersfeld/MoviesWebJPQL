package com.dub.spring.advanced;

import java.util.Date;
import java.util.List;

import com.dub.spring.entities.Actor;
import com.dub.spring.entities.Director;
import com.dub.spring.entities.DisplayMovie;


public interface AdvancedServices {
	
	public List<DisplayMovie> getMoviesWithActor(String firstName, String lastName);

	public List<Actor> getActorsInMovie(String title, Date releaseDate);

	public List<DisplayMovie> getMoviesByDirector(String firstName, String lastName);

	public List<Actor> getActorsByDirector(String firstName, String lastName);

	public List<Director> getDirectorsByActor(String firstName, String lastName);

	public void createActorFilm(long actorId, long movieId);
	
	public void createActorFilmSpecial(
			Actor actorEntity, 
			String title, Date releaseDate);		 
}
