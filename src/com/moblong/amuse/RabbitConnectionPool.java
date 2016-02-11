package com.moblong.amuse;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public final class RabbitConnectionPool {

	private ObjectPool<Connection> pool;
	
	public RabbitConnectionPool() {
		GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
		conf.setMaxTotal(20);
		conf.setMaxIdle(10);
		final ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("push.tlthsc.com");
		pool = new GenericObjectPool<Connection>(new BasePooledObjectFactory<Connection>(){

			@Override
			public Connection create() throws Exception {
				return factory.newConnection();
			}

			@Override
			public PooledObject<Connection> wrap(Connection connection) {
				return new DefaultPooledObject(connection);
			}

			@Override
			public void destroyObject(PooledObject<Connection> p) throws Exception {
				Connection connection = p.getObject();
				connection.close();
				connection = null;
			}
			
		}, conf);
	}
	
	public Connection getConnection() {
		Connection connection = null;
		try {
			connection = pool.borrowObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
}
