package com.moblong.amuse.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.moblong.flipped.model.Account;

public final class AccountDTO {

	public void save(final ApplicationContext context, final String uid, final String pwd, final Account account) {
		Connection con = null;
		PreparedStatement pstat = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("INSERT INTO t_account_base(uid, aid, alias, pwd, signature, telephone, latitude, longitude, registered, lastest) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstat.setString(1, uid);
			pstat.setString(2, account.getId());
			pstat.setString(3, account.getAlias());
			pstat.setString(4, pwd);
			pstat.setString(5, account.getSignature());
			pstat.setString(6, account.getTelephone());
			pstat.setDouble(7, account.getLatitude());
			pstat.setDouble(8, account.getLongitude());
			pstat.setDate(9,   new java.sql.Date(account.getRegistered().getTime()));
			pstat.setDate(10,  new java.sql.Date(account.getLast().getTime()));
			pstat.execute();
			con.commit();
			
			pstat.close();
			pstat = null;
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			if(pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstat = null;
			}
			
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				con = null;
			}
		}
	}
	
	public Account reload(final ApplicationContext context, final String aid) {
		Account		    account = null;
		Connection			con = null;
		PreparedStatement pstat = null;
		ResultSet			 rs = null;
		DataSource 			 ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("SELECT * FROM t_account_base WHERE aid == ?");
			pstat.setString(1, aid);
			pstat.execute();
			rs = pstat.getResultSet();
			if(rs.next()) {
				account = new Account();
				account.setId(rs.getString("aid"));
				account.setAlias(rs.getString("alias"));
				account.setTelephone(rs.getString("telphone"));
				account.setRegistered(new java.util.Date(rs.getDate("registered").getTime()));
				account.setLast(new java.util.Date(rs.getDate("lastest").getTime()));
				account.setSignature(rs.getString("signature"));
				account.setPpid(rs.getString("ppid"));
			}
			rs.close();
			rs = null;
			pstat.close();
			pstat = null;
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs = null;
			}
			
			if(pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstat = null;
			}
			
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				con = null;
			}
		}
		return account;
	}
	
	public String lookforUserId(final ApplicationContext context, final String aid) {
		String				uid = null;
		Connection			con = null;
		PreparedStatement pstat = null;
		ResultSet			 rs = null;
		DataSource 			 ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("SELECT uid FROM t_account_base WHERE aid = ?");
			pstat.setString(1, aid);
			pstat.execute();
			rs = pstat.getResultSet();
			if(rs.next()) {
				uid = rs.getString(1);
			}
			rs.close();
			rs = null;
			pstat.close();
			pstat = null;
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs = null;
			}
			
			if(pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstat = null;
			}
			
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				con = null;
			}
		}
		return uid;
	}
	
	public List<Account> candidate(final ApplicationContext context, final String aid, final int page) {
		List<Account> candidates = new ArrayList<Account>();
		Connection con = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			pstat = con.prepareStatement("SELECT * FROM t_account_base WHERE aid <> ? AND LENGTH(alias) > 0");//匿名注册时也会有account id，但是alias为NULL
			pstat.setString(1, aid);
			pstat.execute();
			rs = pstat.getResultSet();
			while(rs.next()) {
				Account candidate = new Account();
				candidate.setId(rs.getString("aid"));
				candidate.setAlias(rs.getString("alias"));
				candidate.setRegistered(new java.util.Date(rs.getDate("registered").getTime()));
				candidate.setLast(new java.util.Date(rs.getDate("lastest").getTime()));
				candidate.setSignature(rs.getString("signature"));
				candidate.setPpid(rs.getString("ppid"));
				candidates.add(candidate);
			}
			rs.close();
			rs = null;
			pstat.close();
			pstat = null;
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs = null;
			}
			
			if(pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstat = null;
			}
			
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				con = null;
			}
		}
		return candidates;
	}

	public void update(ApplicationContext context, Account account) {
		Connection			con = null;
		PreparedStatement pstat = null;
		DataSource			 ds = context.getBean("ds", DataSource.class);
		try {
			con   = ds.getConnection();
			if(account.getPpid() == null) {
				pstat = con.prepareStatement("UPDATE t_account_base SET alias = ?, signature = ? WHERE aid = ?");
				pstat.setString(1, account.getAlias());
				pstat.setString(2, account.getSignature());
				pstat.setString(3, account.getId());
			} else {
				pstat = con.prepareStatement("UPDATE t_account_base SET alias = ?, signature = ?, ppid = ? WHERE aid = ?");
				pstat.setString(1, account.getAlias());
				pstat.setString(2, account.getSignature());
				pstat.setString(3, account.getPpid());
				pstat.setString(4, account.getId());
			}
			pstat.execute();
			con.commit();
			pstat.close();
			pstat = null;
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			if(pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstat = null;
			}
			
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				con = null;
			}
		}
	}
}
