package com.moblong.amuse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import org.postgis.java2d.PGShapeGeometry;

import com.moblong.flipped.model.Account;

public final class SmartAssisterService {
	
	public void init() {
		List<IFilter<Account>> filters = new ArrayList<IFilter<Account>>();
		filters.add(new IFilter<Account>() {

			@Override
			public void filter(Account account) {
				
			}
			
		});
	}
	
	private void update(final ApplicationContext context, final String aid, final double latitude, final double longitude) {
		DataSource  ds = context.getBean("ds", DataSource.class);
		Connection con = null;
		PreparedStatement pstate = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstate = con.prepareStatement("UPDATE t_location_realtime SET location = ST_SetSRID(ST_MakePoint(?, ?) WHERE aid = ?");
			pstate.setDouble(1, latitude);
			pstate.setDouble(2, longitude);
			pstate.setString(3, aid);
			pstate.execute();
			pstate.close();
			pstate = null;
			
			pstate = con.prepareStatement("INSERT INTO t_location_history(aid, location) VALUES(?, ST_SetSRID(ST_MakePoint(?, ?), 4326))");
			pstate.setString(1, aid);
			pstate.setDouble(2, latitude);
			pstate.setDouble(3, longitude);
			pstate.execute();
			pstate.close();
			pstate = null;
			
			con.commit();
			con.close();
			con = null;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(pstate != null) {
				try {
					pstate.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstate = null;
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
	
	private void nearby(final ApplicationContext context, final String aid, final double latitude, final double longitude) {
		String sql = "SELECT base.aid, base.alias, base.signature, nearby.distance FROM t_account_base AS base, (SELECT aid, ST_Distance('POINT(? ?)', location) as distance FROM t_location_realtime WHERE aid <> ? ORDER BY distance LIMIT 1000) nearby WHERE base.aid = nearby.aid";
		DataSource ds = context.getBean("ds", DataSource.class);
		Connection con = null;
		PreparedStatement pstate = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstate = con.prepareStatement(sql);
			pstate.setDouble(1, latitude);
			pstate.setDouble(2, longitude);
			pstate.setString(3, aid);
			pstate.execute();
		} catch(SQLException ex) {
			
		}
	}
	
	public void onUpdate(final ApplicationContext context, final String aid, final double latitude, final double longitude) {
		update(context, aid, latitude, longitude);
	}
	
}
