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

import com.gcit.lms.entity.Branch;
import com.mysql.jdbc.Statement;

@Component
public class BranchDAO extends BaseDAO implements
		ResultSetExtractor<List<Branch>> {

	public void addBranch(Branch branch) throws SQLException {
		template.update(
				"INSERT INTO tbl_library_branch(branchName, branchAddress) VALUES(?,?)",
				new Object[] { branch.getBranchName(),
						branch.getBranchAddress() });
	}

	
	public Integer addBranchWithId(final Branch branch) throws SQLException {
		KeyHolder holder = new GeneratedKeyHolder();
		final String query = "INSERT INTO tbl_library_branch(branchName, branchAddress) VALUES(?,?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, branch.getBranchName());
				ps.setString(2, branch.getBranchAddress());
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}

	public void updateBranch(Branch branch) throws SQLException {
		template.update(
				"UPDATE tbl_library_branch SET branchName = ?, branchAddress = ? WHERE branchId = ?",
				new Object[] { branch.getBranchName(),
						branch.getBranchAddress(), branch.getBranchId() });
	}

	public void deleteBranch(Branch branch) throws SQLException {
		template.update("DELETE FROM tbl_library_branch WHERE branchId = ?",
				new Object[] { branch.getBranchId() });
	}
	
	public Integer getBranchesCount() throws SQLException {
		return template.queryForObject("select count(*) as COUNT from tbl_library_branch", Integer.class);
	}

	public List<Branch> readAllBranches(Integer pageNo, String searchString)
			throws SQLException {
		setPageNo(pageNo);
		Object[] params = null;
		String query = "SELECT * FROM tbl_library_branch";
		if (searchString != null) {
			searchString = "%" + searchString + "%";
			query += " WHERE branchName LIKE ?";
			params = new Object[] { searchString };
		}
		if (pageNo != null && pageNo > 0){
			int index = (getPageNo() - 1)* 10;
			query+=" LIMIT "+index+" , "+getPageSize();
		}
		return (List<Branch>) template.query(query, params, this);
	}

	public Branch getBranchByPK(Integer branchId) throws SQLException {
		List<Branch> branches = (List<Branch>) template.query(
				"SELECT * FROM tbl_library_branch WHERE branchId = ?",
				new Object[] { branchId }, this);
		if (branches != null && !branches.isEmpty()) {
			return branches.get(0);
		}
		return null;
	}

	@Override
	public List<Branch> extractData(ResultSet rs) throws SQLException {
		List<Branch> branches = new ArrayList<>();
		while (rs.next()) {
			Branch br = new Branch();
			br.setBranchId(rs.getInt("branchId"));
			br.setBranchName(rs.getString("branchName"));
			br.setBranchAddress(rs.getString("branchAddress"));
			branches.add(br);
		}
		return branches;
	}

}
