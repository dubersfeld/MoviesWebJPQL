package com.dub.spring.directors;

import java.util.List;

import com.dub.spring.entities.Director;

public interface DirectorServices {

	List<Director> getAllDirectors();
	
	Director getDirector(long id);	
	Director getDirector(String firstName, String lastName);
	
	void deleteDirector(long id);
	void addDirector(Director director);
	void updateDirector(Director director);
	long numberOfDirectors();
		
}

