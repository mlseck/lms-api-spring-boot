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

import com.gcit.lms.entity.Publisher;
import com.mysql.jdbc.Statement;

@Component
public class PublisherDAO extends BaseDAO implements
		ResultSetExtractor<List<Publisher>> {

	public void addPublisher(Publisher publisher) throws SQLException {
		template.update(
				"INSERT INTO tbl_publisher(publisherName, publisherAddress, publisherPhone) VALUES(?,?,?)",
				new Object[] { publisher.getPublisherName(),
						publisher.getPublisherAddress(),
						publisher.getPublisherPhone() });
	}

	
	public Integer addPublisherWithId(final Publisher publisher) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String query = "INSERT INTO tbl_publisher(publisherName, publisherAddress, publisherPhone) VALUES(?,?,?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, publisher.getPublisherName());
				ps.setString(2, publisher.getPublisherAddress());
				ps.setString(3, publisher.getPublisherPhone());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}
	
	public void updatePublisher(Publisher publisher) throws SQLException {
		template.update(
				"UPDATE tbl_publisher SET publisherName = ?, publisherAddress = ?, publisherPhone = ? WHERE publisherId = ?",
				new Object[] { publisher.getPublisherName(),
						publisher.getPublisherAddress(),
						publisher.getPublisherPhone(),
						publisher.getPublisherId() });
	}

	public void deletePublisher(Publisher publisher) throws SQLException {
		template.update("DELETE FROM tbl_publisher WHERE publisherId = ?",
				new Object[] { publisher.getPublisherId() });
	}

	
	public Integer getPublishersCount() throws SQLException {
		return template.queryForObject("select count(*) as COUNT from tbl_publisher", Integer.class);
	}

	public List<Publisher> readAllPublishers(Integer pageNo, String searchString)
			throws SQLException {
		setPageNo(pageNo);
		Object[] params = null;
		String query = "SELECT * FROM tbl_publisher";
		if (searchString != null) {
			searchString = "%" + searchString + "%";
			query += " WHERE publisherName LIKE ?";
			params = new Object[] { searchString };
		}
		if (pageNo != null && pageNo > 0){
			int index = (getPageNo() - 1)* 10;
			query+=" LIMIT "+index+" , "+getPageSize();
		}
		return (List<Publisher>) template.query(query, params, this);
	}
	
	public Publisher getBookPublisher(int bookId) throws SQLException {
		List<Publisher> publishers = (List<Publisher>) template.query(
				"SELECT * FROM tbl_publisher WHERE publisherId IN (SELECT pubId FROM tbl_book WHERE bookId = ?)",
				new Object[] { bookId }, this);
		if (publishers != null && !publishers.isEmpty()) {
			return publishers.get(0);
		}
		return null;
	}

	public Publisher getPublisherByPK(Integer pubId) throws SQLException {
		List<Publisher> publishers = (List<Publisher>) template.query(
				"SELECT * FROM tbl_publisher WHERE publisherId = ?",
				new Object[] { pubId }, this);
		if (publishers != null && !publishers.isEmpty()) {
			return publishers.get(0);
		}
		return null;
	}

	@Override
	public List<Publisher> extractData(ResultSet rs) throws SQLException {
		List<Publisher> publishers = new ArrayList<>();
		while (rs.next()) {
			Publisher p = new Publisher();
			p.setPublisherId(rs.getInt("publisherId"));
			p.setPublisherName(rs.getString("publisherName"));
			p.setPublisherAddress(rs.getString("publisherAddress"));
			p.setPublisherPhone(rs.getString("publisherPhone"));
			publishers.add(p);
		}
		return publishers;
	}

}
