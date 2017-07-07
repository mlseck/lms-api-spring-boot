package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.Publisher;

@RestController
public class PublisherController {

	@Autowired
	BookDAO bdao;
	
	@Autowired
	PublisherDAO pdao;
	
	@Transactional
	@RequestMapping(value = "/publishers/", method = RequestMethod.PUT, consumes="application/json")
	public void savePublisher(@RequestBody Publisher publisher) throws SQLException{
		if (publisher.getPublisherId() != null){
				pdao.updatePublisher(publisher);
		}else{
				pdao.addPublisher(publisher);
		}
	}
	
	@RequestMapping(value = "/publishers/{pubId}", method = RequestMethod.GET, produces="application/json")
	public Publisher getPublisherByPK(@PathVariable Integer pubId) throws SQLException {
		Publisher publisher = pdao.getPublisherByPK(pubId);
		publisher.setBooks(bdao.getBooksWithPublisher(pubId));
		return publisher;
	}
	
	@RequestMapping(value = "/publishers/", method = RequestMethod.DELETE, consumes="application/json")
	public void deletePublisher(@RequestBody Publisher publisher) throws SQLException{
		pdao.deletePublisher(publisher);
	}
	
	@RequestMapping(value = "/publishers/count", method = RequestMethod.GET, produces="application/json")
	public Integer getPublishersCount() throws SQLException {
		return pdao.getPublishersCount();
	}
	
	@RequestMapping(value = "/publishers/{pageNo}/{searchString}", method = RequestMethod.GET, produces="application/json")
	public List<Publisher> getAllPublishers(@PathVariable Integer pageNo, @PathVariable String searchString) throws SQLException{
		List<Publisher> publishers = pdao.readAllPublishers(pageNo, searchString);
		for (Publisher p:publishers){
			p.setBooks(bdao.getBooksWithPublisher(p.getPublisherId()));
		}
		return publishers;
	}
}
