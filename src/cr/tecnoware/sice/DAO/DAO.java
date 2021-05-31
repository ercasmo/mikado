package cr.tecnoware.sice.DAO;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Logger;

import cr.tecnoware.sice.jdbc.pool.PoolConexiones;

public class DAO {
	private static Logger log = Logger.getLogger("application");
	private Connection conn;

	public Connection getConn() {
		return conn;
	}

	public DAO() {
	}

	protected Logger getLogger() {
		return log;
	}

	public Connection getConnection() {
		try {
			conn = PoolConexiones.getInstance().getDataSource().getConnection();			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return conn;
	}

	public void closeConnection() {
		PoolConexiones.getInstance().liberaConexion(conn);
	}
	
	public void closeStatement(Statement st){
		try {
			if(null!=st){
				st.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		

}
