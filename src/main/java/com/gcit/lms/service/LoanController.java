package com.gcit.lms.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.BookLoans;
import com.gcit.lms.entity.Borrower;

@RestController
public class LoanController {
	@Autowired
	BookLoansDAO bldao;
	@Autowired
	BookCopiesDAO bcdao;
	
	@Transactional
	@RequestMapping(value = "/loans/checkOut", method = RequestMethod.POST, consumes="application/json")
	public void checkOutBook(@RequestBody BookCopies bc, @RequestBody Borrower br) throws SQLException{
		BookLoans bl = new BookLoans();
		bl.setBook(bc.getBook());
		bl.setBranch(bc.getBranch());
		bl.setBorrower(br);
		bl.setDateOut(LocalDateTime.now().toString());
		bl.setDueDate(LocalDateTime.now().plusDays(30).toString());
		
		bldao.addBookLoans(bl);
	}
	
	@RequestMapping(value = "/loans/return", method = RequestMethod.POST, consumes="application/json")
	public void returnBook(@RequestBody BookLoans bl) throws SQLException{
		
		BookCopies bc = new BookCopies();
		
		bl = bldao.getBookLoansByPK(bl.getBook().getBookId(), bl.getBranch().getBranchId(), bl.getBorrower().getCardNo(), bl.getDateOut());
		bl.setDateIn(LocalDateTime.now().toString());
		bldao.updateBookLoans(bl);
		bc = bcdao.getBookCopiesByPK(bl.getBook().getBookId(), bl.getBranch().getBranchId());
		bc.setCopies(bc.getCopies()+1);
		bcdao.updateBookCopies(bc);
	}
	
	@RequestMapping(value = "/loans/overwrite", method = RequestMethod.POST, consumes="application/json")
	public void overwriteLoan(@RequestBody BookLoans l) throws SQLException{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		String date = l.getDueDate();
		LocalDateTime dueDate = LocalDateTime.parse(date, formatter);
		l.setDueDate(dueDate.plusDays(30).toString());
		bldao.updateBookLoans(l);
	}
	
	@RequestMapping(value = "/loans/{cardNo}", method = RequestMethod.GET, produces="application/json")
	public List<BookLoans> getLoansFromBorrower(@PathVariable Integer cardNo) throws SQLException{
		return bldao.getBookLoansByCardNo(cardNo);
	}
	
	@RequestMapping(value = "/loans/{cardNo}/{bookId}/{branchId}/{dateOut}", method = RequestMethod.GET, produces="application/json")
	public BookLoans getLoanByPK(@PathVariable Integer cardNo, @PathVariable Integer bookId, @PathVariable Integer branchId, @PathVariable String dateOut) throws SQLException{
		return bldao.getBookLoansByPK(bookId, branchId, cardNo, dateOut);
	}
	
	@RequestMapping(value = "/loans/count", method = RequestMethod.GET, produces="application/json")
	public Integer getLoansCount() throws SQLException {
		return bldao.getBookLoansCount();
	}
	
	@RequestMapping(value = "/loans/{pageNo}/{searchString}", method = RequestMethod.GET, produces="application/json")
	public List<BookLoans> getAllLoans(@PathVariable Integer pageNo, @PathVariable String searchString) throws SQLException{
		return bldao.readAllBookLoans(pageNo, searchString);
	}
	
}
