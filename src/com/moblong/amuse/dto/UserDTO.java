package com.moblong.amuse.dto;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.moblong.flipped.model.Indicator;
import com.moblong.flipped.model.User;

public final class UserDTO {

	public void save(final ApplicationContext context, final User user) {
		boolean			  exist = false;
		ResultSet			 rs = null;
		Connection			con = null;
		PreparedStatement pstat = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstat = con.prepareStatement("SELECT COUNT(1) FROM t_user_indicator WHERE uid = ?");
			pstat.setString(1, user.getUid());
			pstat.execute();
			rs = pstat.getResultSet();
			if(rs.next()) {
				exist = rs.getInt(1) > 0;
			}
			rs.close();
			rs = null;
			pstat.close();
			pstat = null;
			
			if(exist) {
				pstat = con.prepareStatement("DELETE FROM t_user_indicator WHERE uid = ?");
				pstat.setString(1, user.getUid());
				pstat.execute();
				pstat.close();
				pstat = null;
			}
			
			pstat = con.prepareStatement("INSERT INTO t_user_indicator(uid, title, content, credibility, visible) VALUES(?, ?, ?, ?, ?)");
			Indicator[] indicators = user.getIndicators();
			for(Indicator indicator : indicators) {
				pstat.setString(1, user.getUid());
				pstat.setString(2, indicator.getTitle());
				Object content = indicator.getStatus();
				if(content instanceof String)
					pstat.setString(3, (String)content);
				else if(content instanceof Boolean)
					pstat.setString(3, Boolean.toString((Boolean)content));
				else
					pstat.setString(3, Integer.toString((Integer)content));
				pstat.setBoolean(4, indicator.isCredited());
				pstat.setBoolean(5, indicator.isVisible());
				pstat.addBatch();
			}
			pstat.executeBatch();
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
	
	public User reload(final ApplicationContext context, final String uid) {
		User user = null;
		Connection con = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			user = new User();
			con = ds.getConnection();
			pstat = con.prepareStatement("SELECT title, status, credibility, visible FROM t_user_indicator WHERE uid = ?");
			pstat.setString(1, uid);
			pstat.execute();
			rs = pstat.getResultSet();
			Indicator[] indicators = user.getIndicators();
			int count = 0;
			while(rs.next()) {
				indicators[count].setTitle(rs.getString("title"));
				indicators[count].setVisible(rs.getBoolean("visible"));
				if(indicators[count].getTitle().equals("城市")||indicators[count].getTitle().equals("全名")) {
					indicators[count].setStatus(indicators[count].isVisible() ? rs.getString("status") : "保密");
				} else if(indicators[count].getTitle().equals("性别")) {
					indicators[count].setStatus(Boolean.parseBoolean(rs.getString("status")));
				} else {
					indicators[count].setStatus(indicators[count].isVisible() ? Integer.parseInt(rs.getString("status")) : -2);
				}
				indicators[count].setCredited(rs.getBoolean("credibility"));
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
		return user;
	}
	
	public void delete(final ApplicationContext context, final User user) {
		Connection con = null;
		PreparedStatement pstat = null;
		try {
			DataSource ds = context.getBean("ds", DataSource.class);
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstat = con.prepareStatement("DELETE FROM t_user_indicator WHERE uid = ?");
			pstat.setString(1, user.getUid());
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
	
	public String lookfor(final ApplicationContext context, final String aid) {
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
}
