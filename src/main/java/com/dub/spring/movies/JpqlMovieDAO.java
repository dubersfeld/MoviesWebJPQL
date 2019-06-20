package com.dub.spring.movies;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dub.spring.entities.Movie;
import com.dub.spring.exceptions.DirectorNotFoundException;
import com.dub.spring.exceptions.DuplicateMovieException;
import com.dub.spring.exceptions.MovieNotFoundException;
import com.dub.spring.site.DateCorrect;


@Repository
public class JpqlMovieDAO implements MovieDAO {
	
	
	@PersistenceContext
	private EntityManager entityManager;
		
	
	
	@Override
	@Transactional
	public void create(Movie movie) {
	
		try {
			movie.setReleaseDate(DateCorrect.correctDate(movie.getReleaseDate()));	
			entityManager.persist(movie);
			entityManager.flush();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			String ex = ExceptionUtils.getRootCauseMessage(e);
			if (ex.contains("movie_unique")) {
				throw new DuplicateMovieException();				
			} else if (ex.contains("FOREIGN KEY")) {
				throw new DirectorNotFoundException();
			} else {
				throw e;
			}
		}
	}
	
	

	@Override
	@Transactional
	public void update(Movie movie) {
	
		try {	
			movie.setReleaseDate(DateCorrect.correctDate(movie.getReleaseDate()));	
			entityManager.merge(movie);	
			entityManager.flush();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			String ex = ExceptionUtils.getRootCauseMessage(e);						
			if (ex.contains("FOREIGN KEY")) {
				throw new DirectorNotFoundException();
			} else {
				throw e;
			}
		}
		
	}

	@Override
	@Transactional
	public void delete(long id) {
		Movie movie = entityManager.find(Movie.class, id);
		if (movie != null) {
		entityManager.remove(movie);	
		entityManager.flush();
		} else {
			throw new MovieNotFoundException();
		}
		
	}

	@Override
	public List<Movie> listAllMovies() {
		return this.entityManager.createQuery(
				"SELECT m FROM Movie m", Movie.class
		).getResultList();
	}
	

	@Override
	public Movie getMovie(long id) {
		Movie movie = entityManager.find(Movie.class, id);
		if (movie != null) {
			return movie;
		} else {
			throw new MovieNotFoundException();
		}
	}

	@Override
	public Movie getMovie(String title, Date releaseDate) {
		try {
			return this.entityManager.createQuery(
				"SELECT m FROM Movie m WHERE m.title = :title " + 
				"AND releaseDate = :releaseDate", Movie.class)
					.setParameter("title", title)
					.setParameter("releaseDate", DateCorrect.correctDate(releaseDate))
					.getSingleResult();
		} catch (NoResultException | ParseException e) {
			throw new MovieNotFoundException();
		}
	}


	@Override
	public List<Movie> getMovie(String title) {
			return this.entityManager.createQuery(
				"SELECT m FROM Movie m WHERE m.title = :title", 
				Movie.class)
					.setParameter("title", title)
					.getResultList();
	}

	
	@Override
	public long getNumberOfMovies() {
		return (long) this.entityManager.createQuery(
				"SELECT COUNT(m) FROM Movie m").getSingleResult();
	}
	
}
