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

import com.gcit.lms.entity.Borrower;
import com.mysql.jdbc.Statement;

@Component
public class BorrowerDAO extends BaseDAO implements
		ResultSetExtractor<List<Borrower>> {

	public void addBorrower(Borrower borrower) throws SQLException {
		template.update(
				"INSERT INTO tbl_borrower(name, address, phone) VALUES(?,?,?)",
				new Object[] { borrower.getName(), borrower.getAddress(),
						borrower.getPhone() });
	}

	public Integer addBorrowerWithId(final Borrower borrower) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String query = "INSERT INTO tbl_borrower(name, address, phone) VALUES(?,?,?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, borrower.getName());
				ps.setString(2, borrower.getAddress());
				ps.setString(3, borrower.getPhone());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}
	
	public void updateBorrower(Borrower borrower) throws SQLException {
		template.update(
				"UPDATE tbl_borrower SET name = ?, address = ?, phone = ? WHERE cardNo = ?",
				new Object[] { borrower.getName(), borrower.getAddress(),
						borrower.getPhone(), borrower.getCardNo() });
	}

	public void deleteBorrower(Borrower borrower) throws SQLException {
		template.update("DELETE FROM tbl_borrower WHERE cardNo = ?",
				new Object[] { borrower.getCardNo() });
	}
	
	public Integer getBorrowersCount() throws SQLException {
		return template.queryForObject("select count(*) as COUNT from tbl_borrower", Integer.class);
	}

	public List<Borrower> readAllBorrowers(Integer pageNo, String searchString)
			throws SQLException {
		setPageNo(pageNo);
		Object[] params = null;
		String query = "SELECT * FROM tbl_borrower";
		if (searchString != null) {
			searchString = "%" + searchString + "%";
			query += " WHERE name LIKE ?";
			params = new Object[] { searchString };
		}
		if (pageNo != null && pageNo > 0){
			int index = (getPageNo() - 1)* 10;
			query+=" LIMIT "+index+" , "+getPageSize();
		}
		return (List<Borrower>) template.query(query, params, this);
	}

	public Borrower getBorrowerByPK(Integer cardNo) throws SQLException {
		;
		List<Borrower> borrowers = (List<Borrower>) template.query(
				"SELECT * FROM tbl_borrower WHERE cardNo = ?",
				new Object[] { cardNo }, this);
		if (borrowers != null && !borrowers.isEmpty()) {
			return borrowers.get(0);
		}
		return null;
	}

	@Override
	public List<Borrower> extractData(ResultSet rs) throws SQLException {
		List<Borrower> borrowers = new ArrayList<>();
		while (rs.next()) {
			Borrower b = new Borrower();
			b.setCardNo(rs.getInt("cardNo"));
			b.setName(rs.getString("name"));
			b.setAddress(rs.getString("address"));
			b.setPhone(rs.getString("phone"));
			borrowers.add(b);
		}
		return borrowers;

	}

}
