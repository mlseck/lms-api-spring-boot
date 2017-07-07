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

import com.gcit.lms.entity.Author;
import com.mysql.jdbc.Statement;

@Component
public class AuthorDAO extends BaseDAO implements
		ResultSetExtractor<List<Author>> {

	public void addAuthor(Author author) throws SQLException {
		template.update("INSERT INTO tbl_author(authorName) VALUES (?)",
				new Object[] { author.getAuthorName() });
	}

	public Integer addAuthorWithId(final Author author) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String query = "INSERT INTO tbl_author(authorName) VALUES (?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, author.getAuthorName());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public void updateAuthor(Author author) throws SQLException {
		template.update(
				"UPDATE tbl_author SET authorName = ? WHERE authorId = ?",
				new Object[] { author.getAuthorName(), author.getAuthorId() });
	}

	public void deleteAuthor(Author author) throws SQLException {
		template.update("DELETE FROM tbl_author WHERE authorId = ?",
				new Object[] { author.getAuthorId() });
	}
	
	public Integer getAuthorsCount() throws SQLException {
		return template.queryForObject("select count(*) as COUNT from tbl_author", Integer.class);
	}

	public List<Author> readAllAuthors(Integer pageNo, String searchString)
			throws SQLException {
		setPageNo(pageNo);
		Object[] params = null;
		String query = "SELECT * FROM tbl_author";
		if (searchString != null) {
			searchString = "%" + searchString + "%";
			query += " WHERE authorName LIKE ?";
			params = new Object[] { searchString };
		}
		if (pageNo != null && pageNo > 0){
			int index = (getPageNo() - 1)* 10;
			query+=" LIMIT "+index+" , "+getPageSize();
		}
		return template.query(query, params, this);
	}

	public Author getAuthorByPK(Integer authorId) throws SQLException {
		List<Author> authors = (List<Author>) template.query(
				"SELECT * FROM tbl_author WHERE authorId = ?",
				new Object[] { authorId }, this);
		if (authors != null && !authors.isEmpty()) {
			return authors.get(0);
		}
		return null;
	}

	public List<Author> getAuthorsWithBook(int bookId) throws SQLException {
		return template
				.query("SELECT * FROM tbl_author WHERE authorId IN (SELECT authorId FROM tbl_book_authors WHERE bookId=?)",
						new Object[] { bookId }, this);
	}

	@Override
	public List<Author> extractData(ResultSet rs) throws SQLException {
		List<Author> authors = new ArrayList<>();
		while (rs.next()) {
			Author a = new Author();
			a.setAuthorId(rs.getInt("authorId"));
			a.setAuthorName(rs.getString("authorName"));
			authors.add(a);
		}
		return authors;
	}

}
