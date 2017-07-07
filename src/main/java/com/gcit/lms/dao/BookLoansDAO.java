package com.gcit.lms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gcit.lms.entity.BookLoans;

@Component
public class BookLoansDAO extends BaseDAO implements
		ResultSetExtractor<List<BookLoans>> {

	@Autowired
	BookDAO bdao;
	@Autowired
	BranchDAO brdao;
	@Autowired
	BorrowerDAO bodao;
	
	public void addBookLoans(BookLoans bookLoans) throws SQLException {
		template.update(
				"INSERT INTO tbl_book_loans VALUES (?, ?, ?, ?, ?, ?)",
				new Object[] { bookLoans.getBook().getBookId(),
						bookLoans.getBranch().getBranchId(),
						bookLoans.getBorrower().getCardNo(),
						bookLoans.getDateOut(), bookLoans.getDueDate(),
						bookLoans.getDateIn() });
	}


	public void updateBookLoans(BookLoans bookLoans) throws SQLException {
		template.update(
				"UPDATE tbl_book_loans SET dueDate = ?, dateIn = ? WHERE bookId = ? AND branchId = ? AND cardNo = ? AND dateOut = ?",
				new Object[] { bookLoans.getDueDate(), bookLoans.getDateIn(),
						bookLoans.getBook().getBookId(),
						bookLoans.getBranch().getBranchId(),
						bookLoans.getBorrower().getCardNo(),
						bookLoans.getDateOut() });
	}

	public void deleteBookLoans(BookLoans bookLoans) throws SQLException {
		template.update(
				"DELETE FROM tbl_book_loans WHERE bookId = ? AND branchId = ? AND cardNo = ? AND dateOut = ?",
				new Object[] { bookLoans.getBook().getBookId(),
						bookLoans.getBranch().getBranchId(),
						bookLoans.getBorrower().getCardNo(),
						bookLoans.getDateOut() });
	}

	public Integer getBookLoansCount() throws SQLException {
		return template.queryForObject("select count(*) as COUNT from tbl_book_loans", Integer.class);
	}

	public List<BookLoans> readAllBookLoans(Integer pageNo, String searchString)
			throws SQLException {
		setPageNo(pageNo);
		Object[] params = null;
		String query = "SELECT * FROM tbl_book_loans";
		if (searchString != null) {
			searchString = "%" + searchString + "%";
			query += " WHERE cardNo LIKE ?";
			params = new Object[] { searchString };
		}
		query += " ORDER BY dateIn";
		if (pageNo != null && pageNo > 0){
			int index = (getPageNo() - 1)* 10;
			query+=" LIMIT "+index+" , "+getPageSize();
		}	
		return (List<BookLoans>) template.query(query, params, this);
	}

	public BookLoans getBookLoansByPK(Integer bookId, Integer branchId,
			Integer cardNo, String dateOut) throws SQLException {
		List<BookLoans> bookLoans = (List<BookLoans>) template
				.query("SELECT * FROM tbl_book_loans WHERE bookId = ? AND branchId = ? AND cardNo = ? AND dateOut = ?",
						new Object[] { bookId, branchId, cardNo, dateOut },
						this);
		if (bookLoans != null && !bookLoans.isEmpty()) {
			return bookLoans.get(0);
		}
		return null;
	}

	public List<BookLoans> getBookLoansByCardNo(Integer cardNo)
			throws SQLException {
		return (List<BookLoans>) template.query(
				"SELECT * FROM tbl_book_loans WHERE cardNo = ?",
				new Object[] { cardNo }, this);
	}

	public List<BookLoans> getLoansByBranchId(int branchId) throws SQLException {
		return (List<BookLoans>) template.query(
				"SELECT * FROM tbl_book_loans WHERE branchId = ?",
				new Object[] { branchId }, this);
	}

	@Override
	public List<BookLoans> extractData(ResultSet rs) throws SQLException {
		List<BookLoans> bookLoans = new ArrayList<>();
		while (rs.next()) {
			BookLoans l = new BookLoans();
			l.setBook(bdao.getBookByPK(rs.getInt("bookId")));
			l.setBranch(brdao.getBranchByPK(rs.getInt("branchId")));
			l.setBorrower(bodao.getBorrowerByPK(rs.getInt("cardNo")));
			l.setDateOut(rs.getString("dateOut"));
			l.setDueDate(rs.getString("dueDate"));
			if (rs.getString("dateIn") != null) {
				l.setDateIn(rs.getString("dateIn"));
			}
			bookLoans.add(l);
		}
		return bookLoans;
	}
}
