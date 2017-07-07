package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.gcit.lms.entity.Genre;
import com.mysql.jdbc.Statement;

@Component
public class GenreDAO extends BaseDAO implements
		ResultSetExtractor<List<Genre>> {

	public void addGenre(Genre genre) throws SQLException {
		template.update("INSERT INTO tbl_genre(genre_name) VALUES (?)",
				new Object[] { genre.getGenreName() });
	}

	public Integer addGenreWithId(final Genre genre) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String query = "INSERT INTO tbl_genre(genre_name) VALUES (?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, genre.getGenreName());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}
	
	public void updateGenre(Genre genre) throws SQLException {
		template.update(
				"UPDATE tbl_genre SET genre_name = ? WHERE genre_id = ?",
				new Object[] { genre.getGenreName(), genre.getGenreId() });
	}

	public void deleteGenre(Genre genre) throws SQLException {
		template.update("DELETE FROM tbl_genre WHERE genre_id = ?",
				new Object[] { genre.getGenreId() });
	}

	public Integer getGenresCount() throws SQLException {
		return template.queryForObject("select count(*) as COUNT from tbl_genre", Integer.class);
	}

	public List<Genre> readAllGenres(Integer pageNo, String searchString)
			throws SQLException {
		setPageNo(pageNo);
		Object[] params = null;
		String query = "SELECT * FROM tbl_genre";
		if (searchString != null) {
			searchString = "%" + searchString + "%";
			query += " WHERE genre_name LIKE ?";
			params = new Object[] { searchString };
		}
		if (pageNo != null && pageNo > 0){
			int index = (getPageNo() - 1)* 10;
			query+=" LIMIT "+index+" , "+getPageSize();
		}
		return (List<Genre>) template.query(query, params, this);
	}
	
	public List<Genre> getGenresWithBook(int bookId) throws SQLException {
		return 	template
				.query("SELECT * FROM tbl_genre WHERE genre_Id IN (SELECT genre_Id FROM tbl_book_genres WHERE bookId=?)",
						new Object[] { bookId }, this);
	}

	public Genre getGenreByPK(Integer genre_id) throws SQLException {
		List<Genre> genres = (List<Genre>) template.query(
				"SELECT * FROM tbl_genre WHERE genre_id = ?",
				new Object[] { genre_id }, this);
		if (genres != null && !genres.isEmpty()) {
			return genres.get(0);
		}
		return null;
	}

	@Override
	public List<Genre> extractData(ResultSet rs) throws SQLException {
		List<Genre> genres = new ArrayList<>();
		while (rs.next()) {
			Genre g = new Genre();
			g.setGenreId(rs.getInt("genre_id"));
			g.setGenreName(rs.getString("genre_name"));
			genres.add(g);
		}
		return genres;
	}

}
