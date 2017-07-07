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

import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.Branch;

@RestController
public class BranchController {

	@Autowired
	BranchDAO brdao;
	@Autowired
	BookCopiesDAO bcdao;
	@Autowired
	BookDAO bdao;
	
	@Transactional
	@RequestMapping(value = "/branches/", method = RequestMethod.PUT, consumes="application/json")
	public void saveBranch(@RequestBody Branch branch) throws SQLException{
		if (branch.getBranchId() != null){
				brdao.updateBranch(branch);
		} else{
				brdao.addBranch(branch);
		}
	}
	
	@RequestMapping(value = "/branches/", method = RequestMethod.DELETE, consumes="application/json")
	public void deleteBranch(@RequestBody Branch branch) throws SQLException{
		brdao.deleteBranch(branch);
	}
	
	@RequestMapping(value = "/branches/count", method = RequestMethod.GET, produces="application/json")
	public Integer getBranchesCount() throws SQLException {
		return brdao.getBranchesCount();
	}
	
	@RequestMapping(value = "/branches/{pageNo}/{searchString}", method = RequestMethod.GET, produces="application/json")
	public List<Branch> getAllBranches(@PathVariable Integer pageNo, @PathVariable String searchString) throws SQLException{
		return brdao.readAllBranches(pageNo, searchString);
	}
	
	@RequestMapping(value = "/branches/{branchId}", method = RequestMethod.GET, produces="application/json")
	public Branch getBranchById(@PathVariable Integer branchId) throws SQLException{
		return brdao.getBranchByPK(branchId);
	}
	
	@RequestMapping(value = "/branches/books/", method = RequestMethod.PUT, consumes="application/json")
	public void addBookCopiesToBranch(@RequestBody List<Book> books, @RequestBody Branch branch)
			throws SQLException {
		for (Book book : books) {
			BookCopies newCopy = new BookCopies();
			newCopy.setBook(book);
			newCopy.setBranch(branch);
			newCopy.setCopies(0);
			bcdao.addBookCopies(newCopy);
		}
	}
	
	@RequestMapping(value = "/branches/bookCopies/", method = RequestMethod.PUT, consumes="application/json")
	public void updateBookCopies(@RequestBody BookCopies bc) throws SQLException {
		bcdao.updateBookCopies(bc);
	}
	
	@RequestMapping(value = "/branches/{branchId}/books/", method = RequestMethod.GET, produces="application/json")
	public List<Book> getAllBooksInBranch(@PathVariable Integer branchId) throws SQLException {
		return bdao.getBooksInBranch(branchId);
	}

	@RequestMapping(value = "/branches/{branchId}/bookCopies/", method = RequestMethod.GET, produces="application/json")
	public List<BookCopies> getBookCopiesInBranch(@PathVariable Integer branchId)
			throws SQLException {
		return bcdao.getBookCopiesByBranchId(branchId);
	}
	
	@RequestMapping(value = "/branches/{branchId}/bookCopies/{bookId}", method = RequestMethod.GET, produces="application/json")
	public BookCopies getBookCopiesByPK(@PathVariable Integer branchId, @PathVariable Integer bookId)
			throws SQLException {
		return bcdao.getBookCopiesByPK(bookId, branchId);
	}
}
