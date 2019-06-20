package com.dub.spring.advanced;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import com.dub.spring.actors.ActorDAO;
import com.dub.spring.movies.MovieDAO;
import com.dub.spring.site.DateCorrect;
import com.dub.spring.entities.Actor;
import com.dub.spring.entities.ActorMovieCompositeId;
import com.dub.spring.entities.ActorMovie;
import com.dub.spring.entities.Director;
import com.dub.spring.entities.Movie;
import com.dub.spring.exceptions.ActorNotFoundException;
import com.dub.spring.exceptions.DuplicateEntryException;
import com.dub.spring.exceptions.MovieNotFoundException;


@Repository
public class JpqlAdvancedDAO implements AdvancedDAO {
	
	@Resource 
	private DataSource dataSource;
	
	@Resource
	private ActorDAO actorDAO;
	
	@Resource
	private MovieDAO movieDAO;
	
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	
	@Override
	public List<Movie> getMoviesByActorName(String firstName, String lastName) 
	{	
		List<Movie> movies = entityManager.createQuery(
				"SELECT m FROM Movie m JOIN m.actor a " + 
				"WHERE a.firstName = :firstName AND a.lastName = :lastName", Movie.class)
				.setParameter("firstName", firstName)
				.setParameter("lastName", lastName)
				.getResultList();
				
		return movies;		
	}

	@Override
	public List<Actor> getActorsByMovie(String title, Date releaseDate) {
		List<Actor> actors;
		try {
			actors = entityManager.createQuery(				
					"SELECT a FROM Actor a JOIN a.movie m " +
					"WHERE m.title = :title AND m.releaseDate = :releaseDate", 
					Actor.class)
					.setParameter("title", title)
					.setParameter("releaseDate", DateCorrect.correctDate(releaseDate)).getResultList();
			return actors;	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}

	@Override
	public List<Movie> getMoviesByDirectorName(String firstName, String lastName) {

		List<Movie> movies = entityManager.createQuery(
				"SELECT m from Movie m JOIN m.director d " + 
				"WHERE d.firstName = :firstName AND d.lastName = :lastName", 
				Movie.class)
				.setParameter("firstName", firstName)
				.setParameter("lastName", lastName)
				.getResultList();
		
		return movies;	
	}

	@Override
	public List<Actor> getActorsByDirectorName(String firstName, String lastName) {	
		List<Actor> actors = entityManager.createQuery(
				"SELECT a FROM Actor a WHERE a.id IN " + 
				"(SELECT am.id.actorId FROM ActorMovie am WHERE am.id.movieId IN " + 
				"(SELECT m.id FROM Movie m WHERE m.directorId IN " + 
				"(SELECT d.id FROM Director d WHERE d.firstName = :firstName AND d.lastName = :lastName)))",  
				Actor.class)
				.setParameter("firstName", firstName)
				.setParameter("lastName", lastName)
				.getResultList();
			
			return actors;	
	}

	@Override
	public List<Director> getDirectorsByActorName(String firstName, String lastName) {
		List<Director> directors = entityManager.createQuery(
				"SELECT d FROM Director d WHERE d.id IN " + 
				"(SELECT m.directorId FROM Movie m WHERE m.id IN " + 
				"(SELECT am.id.movieId FROM ActorMovie am WHERE am.id.actorId IN " + 
				"(SELECT a.id FROM Actor a WHERE a.firstName = :firstName AND a.lastName = :lastName)))",
				Director.class)
				.setParameter("firstName", firstName)
				.setParameter("lastName", lastName)
				.getResultList();
		
		return directors;		
	}

	@Override
	@Transactional
	public void createActorFilm(long actorId, long movieId) {
		ActorMovie am = new ActorMovie();
		ActorMovieCompositeId amci = new ActorMovieCompositeId();
		amci.setActorId(actorId);
		amci.setMovieId(movieId);
		am.setId(amci);
		try {	
			entityManager.persist(am);
			entityManager.flush();
		} catch (PersistenceException e) {
			String ex = ExceptionUtils.getRootCauseMessage(e);
			if (ex.contains("FOREIGN KEY") && ex.contains("actorId")) {
				throw new ActorNotFoundException();
			} else if (ex.contains("FOREIGN KEY") && ex.contains("filmId")) {
				throw new MovieNotFoundException();
			} else if (ex.contains("Duplicate")) {
				throw new DuplicateEntryException();
			}
		}
	}

	@Override
	@Transactional
	public void createActorFilm(Actor actor, String title, Date releaseDate) {
		Actor act = actorDAO.getActor(
				actor.getFirstName(), actor.getLastName());
		Movie movie = movieDAO.getMovie(title, releaseDate);
		if (movie != null) {
			this.createActorFilm(act.getId(), movie.getId());
		} else {
			throw new MovieNotFoundException();
		}
	}
		
}
