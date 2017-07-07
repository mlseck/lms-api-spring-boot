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
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;
import com.mysql.jdbc.Statement;

@Component
public class BookDAO extends BaseDAO implements ResultSetExtractor<List<Book>> {

	public void addBook(Book book) throws SQLException {
		template.update("INSERT INTO tbl_book(title, pubId) VALUES (?, ?)",
				new Object[] { book.getTitle(),
						book.getPublisher().getPublisherId() });
	}

	public Integer addBookWithId(final Book book) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String query = "INSERT INTO tbl_book(title, pubId) VALUES (?, ?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, book.getTitle());
				ps.setInt(2, book.getPublisher().getPublisherId());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();

	}

	public void updateBook(Book book) throws SQLException {
		Object[] params = new Object[] { book.getTitle(), null, book.getBookId()};
		if (book.getPublisher() != null){
			params = new Object[] { book.getTitle(),
					book.getPublisher().getPublisherId(), book.getBookId() };
		}
		template.update(
				"UPDATE tbl_book SET title = ?, pubId = ? WHERE bookId = ?",
				params);
	}

	public void deleteBook(Book book) throws SQLException {
		template.update("DELETE FROM tbl_book WHERE bookId = ?",
				new Object[] { book.getBookId() });
	}
	
	public Integer getBooksCount() throws SQLException {
		return template.queryForObject("select count(*) as COUNT from tbl_book", Integer.class);
	}

	public List<Book> readAllBooks(Integer pageNo, String searchString)
			throws SQLException {
		setPageNo(pageNo);
		Object[] params = null;
		String query = "SELECT * FROM tbl_book";
		if (searchString != null) {
			searchString = "%" + searchString + "%";
			query += " WHERE title LIKE ?";
			params = new Object[] { searchString };
		}
		if (pageNo != null && pageNo > 0){
			int index = (getPageNo() - 1)* 10;
			query+=" LIMIT "+index+" , "+getPageSize();
		}
		return template.query(query, params, this);
	}

	public Book getBookByPK(Integer bookId) throws SQLException {
		List<Book> books = (List<Book>) template.query(
				"SELECT * FROM tbl_book WHERE bookId = ?",
				new Object[] { bookId }, this);
		if (books != null && !books.isEmpty()) {
			return books.get(0);
		}
		return null;
	}


	public List<Book> getBooksInBranch(int branchId) throws SQLException {
		return template
				.query("SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_copies WHERE branchId=?)",
						new Object[] { branchId }, this);
	}

	public List<Book> getBooksWithAuthor(int authorId) throws SQLException {
		return template
				.query("SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_authors WHERE authorId=?)",
						new Object[] { authorId }, this);
	}

	public List<Book> getBooksWithGenre(int genreId) throws SQLException {
		return template
				.query("SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_genres WHERE genre_Id=?)",
						new Object[] { genreId }, this);
	}

	public List<Book> getBooksWithPublisher(int pubId) throws SQLException {
		return template.query("SELECT * FROM tbl_book WHERE pubId =?",
				new Object[] { pubId }, this);
	}

	public void addAuthorToBook(Book b, Author a) throws SQLException {
		template.update("INSERT INTO tbl_book_authors VALUES (?, ?)",
				new Object[] { b.getBookId(), a.getAuthorId() });
	}

	public void addGenreToBook(Book b, Genre g) throws SQLException {
		template.update("INSERT INTO tbl_book_genres VALUES (?, ?)",
				new Object[] { g.getGenreId(), b.getBookId() });
	}

	public void deleteAuthorFromBook(Book b, Author a) throws SQLException {
		template.update(
				"DELETE FROM tbl_book_authors WHERE bookId = ? AND authorId = ?",
				new Object[] { b.getBookId(), a.getAuthorId() });
	}

	public void deleteGenreFromBook(Book b, Genre g) throws SQLException {
		template.update(
				"DELETE FROM tbl_book_genres WHERE genre_id = ? AND bookId = ?",
				new Object[] { g.getGenreId(), b.getBookId() });
	}

	@Override
	public List<Book> extractData(ResultSet rs) throws SQLException {
		List<Book> books = new ArrayList<>();
		while (rs.next()) {
			Book b = new Book();
			b.setBookId(rs.getInt("bookId"));
			b.setTitle(rs.getString("title"));
			books.add(b);
		}
		return books;
	}

}
