package cr.tecnoware.sice.utils;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;

public class BaseDatosUtils {
	public static final int CONEXION_MYSQL = 0;
	public static final int CONEXION_SQL_SERVER = 1;
	public static final int CONEXION_POSTGRESQL = 2;
	public static final int CONEXION_DB2_AS400 = 3;
	private static final Logger log = Logger.getLogger("application");

	public static Connection obtenerConexionJDBC(int tipoBaseDatos, ConexionRemotaBean conexionRemota, String nombreBaseDatos) {
		Connection conexionJDBC = null;
		try {
			String url = "";
			String driver = "";
			switch (tipoBaseDatos) {
			case CONEXION_MYSQL:
				url = "jdbc:mysql://" + conexionRemota.getDireccionIp() + ":" + conexionRemota.getPuerto() + "/" + nombreBaseDatos;
				driver = "com.mysql.jdbc.Driver";
				break;
			case CONEXION_SQL_SERVER:
				url = "jdbc:jtds:sqlserver://" + conexionRemota.getDireccionIp() + ":" + conexionRemota.getPuerto() + ";DatabaseName=" + nombreBaseDatos;
				driver = "net.sourceforge.jtds.jdbc.Driver";
				break;
			case CONEXION_POSTGRESQL:
				url = "jdbc:postgresql://" + conexionRemota.getDireccionIp() + ":" + conexionRemota.getPuerto() + "/" + nombreBaseDatos;
				driver = "org.postgresql.Driver";
				break;
			case CONEXION_DB2_AS400:
				url = "jdbc:as400://" + conexionRemota.getDireccionIp();
				driver = "com.ibm.db2.jcc.DB2Driver";
				break;
			}

			Class.forName(driver).newInstance();
			conexionJDBC = DriverManager.getConnection(url, conexionRemota.getUsuario(), conexionRemota.getClave());
		} catch (Exception e) {
			log.error("Error obteniendo conexion a Base Datos. Entidad: " + conexionRemota.getEntidadProcesadora() + " Tipo Conexion remota:" + conexionRemota.getTipoConexion(), e);
		}
		return conexionJDBC;
	}

	public static void cerrarConexionJDBC(Connection conexion) {
		try {
			if (conexion != null && !conexion.isClosed()) {
				conexion.close();
			}
		} catch (Exception e) {
			// mute
		}
	}

	public static String obtenerURL(int tipoBaseDatos, ConexionRemotaBean conexionRemota,String nombreBaseDatos) {
		String url = "";		
		switch (tipoBaseDatos) {
		case CONEXION_MYSQL:
			url = "jdbc:mysql://" + conexionRemota.getDireccionIp() + ":" + conexionRemota.getPuerto() + "/" + nombreBaseDatos;			
			break;
		case CONEXION_SQL_SERVER:
			url = "jdbc:jtds:sqlserver://" + conexionRemota.getDireccionIp() + ":" + conexionRemota.getPuerto() + ";DatabaseName=" + nombreBaseDatos;			
			break;
		case CONEXION_POSTGRESQL:
			url = "jdbc:postgresql://" + conexionRemota.getDireccionIp() + ":" + conexionRemota.getPuerto() + "/" + nombreBaseDatos;			
			break;
		case CONEXION_DB2_AS400:
			url = "jdbc:as400://" + conexionRemota.getDireccionIp();			
			break;
		}
		return url;
	}

	public static String obtenerDriver(int tipoBaseDatos) {
		String driver = "";
		switch (tipoBaseDatos) {
		case CONEXION_MYSQL:
			driver = "com.mysql.jdbc.Driver";
			break;
		case CONEXION_SQL_SERVER:
			driver = "net.sourceforge.jtds.jdbc.Driver";
			break;
		case CONEXION_POSTGRESQL:
			driver = "org.postgresql.Driver";
			break;
		case CONEXION_DB2_AS400:
			driver = "com.ibm.db2.jcc.DB2Driver";
			break;
		}
		return driver;
	}
}
