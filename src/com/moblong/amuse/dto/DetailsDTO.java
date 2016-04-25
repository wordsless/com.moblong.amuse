package com.moblong.amuse.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.moblong.flipped.model.Indicator;
import com.moblong.flipped.model.VerifiableItem;

public final class DetailsDTO {

	public void init(final ApplicationContext context, final String uid) {
		List<VerifiableItem> self = new ArrayList<VerifiableItem>(16);
		self.add(new VerifiableItem(uid, new Indicator<String>(0,  "所在城市", ""),  false));
		self.add(new VerifiableItem(uid, new Indicator<String>(1,  "全名",    ""),  false));
		self.add(new VerifiableItem(uid, new Indicator<String>(2,  "性别", "女"), false));
		self.add(new VerifiableItem(uid, new Indicator<String>(3,  "身高", Integer.toString(170)), false));
		self.add(new VerifiableItem(uid, new Indicator<String>(4,  "职业", Integer.toString(2)),   false));
		self.add(new VerifiableItem(uid, new Indicator<String>(5,  "收入", Integer.toString(2)),   false));
		self.add(new VerifiableItem(uid, new Indicator<String>(6,  "民族", Integer.toString(2)),   false));
		self.add(new VerifiableItem(uid, new Indicator<String>(7,  "学历", Integer.toString(2)),   false));
		self.add(new VerifiableItem(uid, new Indicator<String>(8,  "购房", Integer.toString(2)),   false));
		self.add(new VerifiableItem(uid, new Indicator<String>(9,  "购车", Integer.toString(2)),   false));
		self.add(new VerifiableItem(uid, new Indicator<String>(10, "婚姻", Integer.toString(2)),   false));
		save(context, uid, self);
	}
	
	public void save(final ApplicationContext context, final String uid, final List<VerifiableItem> details) {
		boolean			  exist = false;
		ResultSet			 rs = null;
		Connection			con = null;
		PreparedStatement pstat = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstat = con.prepareStatement("SELECT COUNT(1) FROM t_contact_indicator WHERE uid = ?");
			pstat.setString(1, uid);
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
				pstat = con.prepareStatement("DELETE FROM t_contact_indicator WHERE uid = ?");
				pstat.setString(1, uid);
				pstat.execute();
				pstat.close();
				pstat = null;
			}
			
			/**
			 * uid character(128),
			   iid integer,
			   title character varying(32),
			   content text,
			   modifyed date DEFAULT now(),
			   visible boolean NOT NULL DEFAULT false,
			   credibility boolean NOT NULL DEFAULT false 
			 */
			pstat = con.prepareStatement("INSERT INTO t_user_indicator(uid, iid, title, content, modifyed, visible, credibility) VALUES(?, ?, ?, ?, ?, ?, ?)");
			for(VerifiableItem item : details) {
				Indicator<String> indicator = item.getIndicator();
				pstat.setString(1, uid);
				pstat.setInt(2, indicator.getId());
				pstat.setString(3, indicator.getTitle());
				pstat.setString(4, indicator.getContent());
				pstat.setDate(5, new java.sql.Date(System.currentTimeMillis()));
				pstat.setBoolean(6, indicator.isVisible());
				pstat.setBoolean(7, item.isVerified());
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
	
	public List<VerifiableItem> reload(final ApplicationContext context, final String uid) {
		List<VerifiableItem> user = null;
		Connection con = null;
		PreparedStatement pstat = null;
		ResultSet rs = null;
		DataSource ds = context.getBean("ds", DataSource.class);
		try {
			user = new ArrayList<VerifiableItem>(10);
			con = ds.getConnection();
			pstat = con.prepareStatement("SELECT uid, iid, title, content, modifyed, visible, credibility FROM t_contact_indicator WHERE uid = ?");
			pstat.setString(1, uid);
			pstat.execute();
			rs = pstat.getResultSet();
			while(rs.next()) {
				int iid = rs.getInt("iid");
				user.add(new VerifiableItem(uid, new Indicator<String>(iid, rs.getString("title"), rs.getString("content")), rs.getBoolean("credibility")));
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
	
	public void delete(final ApplicationContext context, final String uid) {
		Connection con = null;
		PreparedStatement pstat = null;
		try {
			DataSource ds = context.getBean("ds", DataSource.class);
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstat = con.prepareStatement("DELETE FROM t_contact_indicator WHERE uid = ?");
			pstat.setString(1, uid);
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
