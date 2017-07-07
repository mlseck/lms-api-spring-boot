package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.entity.Genre;

@RestController
public class GenreController {
	@Autowired
	BookDAO bdao;
	
	@Autowired
	GenreDAO gdao;
	
	@Transactional
	@RequestMapping(value = "/genres/", method = RequestMethod.PUT, consumes="application/json")
	public void saveGenre(Genre genre) throws SQLException{
		if (genre.getGenreId() != null){
				gdao.updateGenre(genre);
		} else{
				gdao.addGenre(genre);
		}
	}
	
	@RequestMapping(value = "/genres/", method = RequestMethod.DELETE, consumes="application/json")
	public void deleteGenre(Genre genre) throws SQLException{
		gdao.deleteGenre(genre);
	}
	
	@RequestMapping(value = "/genres/count", method = RequestMethod.GET, produces="application/json")
	public Integer getGenresCount() throws SQLException {
		return gdao.getGenresCount();
	}
	
	@RequestMapping(value = "/genres/{genreId}", method = RequestMethod.GET, produces="application/json")
	public Genre getGenreByPK(@PathVariable Integer genreId) throws SQLException {
		Genre genre = gdao.getGenreByPK(genreId);
		genre.setBooks(bdao.getBooksWithGenre(genreId));
		return genre;
	}
	
	
	
	@RequestMapping(value = "/genres/{pageNo}/{searchString}", method = RequestMethod.GET, produces="application/json")
	public List<Genre> getAllGenres(@PathVariable Integer pageNo, @PathVariable String searchString) throws SQLException{
		List<Genre> genres = gdao.readAllGenres(pageNo, searchString);
		for (Genre g:genres){
			g.setBooks(bdao.getBooksWithGenre(g.getGenreId()));
		}
		return genres;
	}
}
