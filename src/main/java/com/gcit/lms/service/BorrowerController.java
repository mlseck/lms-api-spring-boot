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

import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Borrower;

@RestController
public class BorrowerController {

	@Autowired
	BorrowerDAO bodao;
	
	@Transactional
	@RequestMapping(value = "/borrowers/", method = RequestMethod.PUT, consumes="application/json")
	public void saveBorrower(@RequestBody Borrower borrower) throws SQLException{
		if (borrower.getCardNo() != null){
				bodao.updateBorrower(borrower);
		} else{
				bodao.addBorrower(borrower);
		}
	}
	
	@RequestMapping(value = "/borrowers/", method = RequestMethod.DELETE, consumes="application/json")
	public void deleteBorrower(Borrower borrower) throws SQLException{
		bodao.deleteBorrower(borrower);
	}
	
	@RequestMapping(value = "/borrowers/count", method = RequestMethod.GET, produces="application/json")
	public Integer getBorrowersCount() throws SQLException {
		return bodao.getBorrowersCount();
	}
	
	@RequestMapping(value = "/borrowers/{cardNo}", method = RequestMethod.GET, produces="application/json")
	public Borrower getBorrowerByPK(@PathVariable Integer cardNo) throws SQLException {
		Borrower borrower = bodao.getBorrowerByPK(cardNo);
		return borrower;
	}
	
	@RequestMapping(value = "/borrowers/{pageNo}/{searchString}", method = RequestMethod.GET, produces="application/json")
	public List<Borrower> getAllBorrowers(@PathVariable Integer pageNo, @PathVariable String searchString) throws SQLException{
		return bodao.readAllBorrowers(pageNo, searchString);
	}
}
