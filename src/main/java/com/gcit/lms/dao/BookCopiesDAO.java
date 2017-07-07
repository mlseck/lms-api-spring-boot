package com.gcit.lms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gcit.lms.entity.BookCopies;

@Component
public class BookCopiesDAO extends BaseDAO implements
		ResultSetExtractor<List<BookCopies>> {
	
	@Autowired
	BookDAO bdao;
	@Autowired
	BranchDAO brdao;

	public void addBookCopies(BookCopies bookCopies) throws SQLException {
		template.update(
				"INSERT INTO tbl_book_copies VALUES (?, ?, ?)",
				new Object[] { bookCopies.getBook().getBookId(),
						bookCopies.getBranch().getBranchId(),
						bookCopies.getCopies() });
	}

	public void updateBookCopies(BookCopies bookCopies) throws SQLException {
		template.update(
				"UPDATE tbl_book_copies SET noOfCopies = ? WHERE bookId = ? AND branchId = ?",
				new Object[] { bookCopies.getCopies(),
						bookCopies.getBook().getBookId(),
						bookCopies.getBranch().getBranchId() });
	}

	public void deleteBookCopies(BookCopies bookCopies) throws SQLException {
		template.update(
				"DELETE FROM tbl_book_copies WHERE bookId = ? AND branchId = ?",
				new Object[] { bookCopies.getBook().getBookId(),
						bookCopies.getBranch().getBranchId() });
	}
	
	public Integer getBookCopiesCount() throws SQLException {
		return template.queryForObject("select count(*) as COUNT from tbl_book_copies", Integer.class);
	}

	public List<BookCopies> readAllBookCopies(Integer pageNo)
			throws SQLException {
		setPageNo(pageNo);
		return (List<BookCopies>) template.query(
				"SELECT * FROM tbl_book_copies", this);
	}

	public BookCopies getBookCopiesByPK(Integer bookId, Integer branchId)
			throws SQLException {
		List<BookCopies> bookCopies = (List<BookCopies>) template
				.query("SELECT * FROM tbl_book_copies WHERE bookId = ? AND branchId = ?",
						new Object[] { bookId, branchId }, this);
		if (bookCopies != null && !bookCopies.isEmpty()) {
			return bookCopies.get(0);
		}
		return null;
	}

	public List<BookCopies> getBookCopiesByBranchId(Integer branchId)
			throws SQLException {
		return (List<BookCopies>) template.query(
				"SELECT * FROM tbl_book_copies WHERE branchId = ?",
				new Object[] { branchId }, this);
	}

	@Override
	public List<BookCopies> extractData(ResultSet rs) throws SQLException {
		List<BookCopies> bookCopies = new ArrayList<>();
		while (rs.next()) {
			BookCopies bc = new BookCopies();
			bc.setBook(bdao.getBookByPK(rs.getInt("bookId")));
			bc.setBranch(brdao.getBranchByPK(rs.getInt("branchId")));
			bc.setCopies(rs.getInt("noOfCopies"));
			bookCopies.add(bc);

		}
		return bookCopies;
	}

}
