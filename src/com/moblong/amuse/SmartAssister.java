package com.moblong.amuse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.moblong.amuse.dto.UserDTO;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.DetailsItem;

public final class SmartAssister {
	
	public List<ITendentiousFilter<DetailsItem<?>>> load(final ApplicationContext context, final String aid) {
		List<ITendentiousFilter<DetailsItem<?>>> tendencies = null;
		File cache = new File(context.getBean("tendency", File.class), aid);
		BufferedReader reader = null;
		try {
			Gson gson = new Gson();
			reader = new BufferedReader(new FileReader(cache));
			String json = reader.readLine();
			reader.close();
			reader = null;
			tendencies = gson.fromJson(json, new TypeToken<ITendentiousFilter<?>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				reader = null;
			}
		}
		return tendencies;
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
	
	public List<Account> nearby(final ApplicationContext context, final String aid, final double latitude, final double longitude, final double radius) {
		String sql = "SELECT base.aid, base.alias, base.telephone, base.registered, base.lastest, base.signature, base.ppid, base.type, base.uid, nearby.distance FROM t_account_base AS base, (SELECT aid, ST_Distance('POINT(? ?)', location) AS distance FROM t_location_realtime WHERE aid <> ? AND distance <= ? ORDER BY distance LIMIT 1000) AS nearby WHERE base.aid = nearby.aid";
		List<Account> nearby = new ArrayList<Account>();
		DataSource ds = context.getBean("ds", DataSource.class);
		Connection con = null;
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			pstate = con.prepareStatement(sql);
			pstate.setDouble(1, latitude);
			pstate.setDouble(2, longitude);
			pstate.setString(3, aid);
			pstate.setDouble(4, radius);
			pstate.execute();
			rs = pstate.getResultSet();
			while(rs.next()) {
				Account account = new Account();
				account = new Account();
				account.setId(rs.getString(1).trim());
				account.setAlias(rs.getString(2).trim());
				account.setTelephone(rs.getString(3).trim());
				account.setRegistered(new java.util.Date(rs.getDate(4).getTime()));
				account.setLast(new java.util.Date(rs.getDate(5).getTime()));
				account.setSignature(rs.getString(6).trim());
				account.setAvatar(rs.getString(7).trim());
				account.setType(rs.getString(8));
				account.setUid(rs.getString(9));
				account.setDistance(rs.getInt(10));
				nearby.add(account);
			}
			rs.close();
			rs = null;
			pstate.close();
			pstate = null;
			con.close();
			con = null;
		} catch(SQLException ex) {
			ex.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs = null;
			}
			
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
		
		return nearby;
	}
	
	public void filter(final ApplicationContext context, final List<Account> nearby, final List<ITendentiousFilter<?>> filters) {
		final UserDTO userDTO = context.getBean("UserDTO", UserDTO.class);
		for(Account account : nearby) {
			int c = 0;
			List<DetailsItem<?>> user = userDTO.reload(context, account.getUid());
			for(DetailsItem item : user) {
				if(filters.get(item.getIid()).filte(item))
					break;
				++c;
			}
			if(c < filters.size())
				nearby.remove(account);
		}
	}
	
	public void onUpdate(final ApplicationContext context, final String aid, final double latitude, final double longitude) {
		update(context, aid, latitude, longitude);
	}
	
}
