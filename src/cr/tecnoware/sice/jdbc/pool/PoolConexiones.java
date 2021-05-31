package cr.tecnoware.sice.jdbc.pool;

import java.sql.Connection;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.context.ApplicationParameters;
import cr.tecnoware.sice.encode.DataBaseEncoder;
import cr.tecnoware.sice.utils.BaseDatosUtils;

public class PoolConexiones {
	/** Instancia de la clase. */
	private static PoolConexiones instance = new PoolConexiones();
	private static BasicDataSource dataSource;
	private static Logger log = Logger.getLogger("application");

	private PoolConexiones() {
		dataSource = new BasicDataSource();
		try { 
			
			ConexionRemotaBean conexionRemota = new ConexionRemotaBean();
			String user = ApplicationParameters.getInstance().getString("dataSource.username");
			String password = DataBaseEncoder.decode(ApplicationParameters.getInstance().getString("dataSource.password"));
			String database = ApplicationParameters.getInstance().getString("database.name");
			String host = ApplicationParameters.getInstance().getString("database.host");
			String puerto = ApplicationParameters.getInstance().getString("database.port");
			int tipoBaseDatos = ApplicationParameters.getInstance().getInt("database.tipo");
			String sentenciaTest = ApplicationParameters.getInstance().getString("pool.sentencia.test." + tipoBaseDatos);
			int maximoConexionesPool = ApplicationParameters.getInstance().getInt("pool.maximo.conexiones");
			int maximoConexionesDormidas = ApplicationParameters.getInstance().getInt("pool.maximo.conexiones.idle");
			conexionRemota.setUsuario(user);
			conexionRemota.setClave(password);
			conexionRemota.setPuerto(Integer.parseInt(puerto));
			conexionRemota.setDireccionIp(host);
			String url = BaseDatosUtils.obtenerURL(tipoBaseDatos, conexionRemota, database);
			String driver = BaseDatosUtils.obtenerDriver(tipoBaseDatos);

			dataSource.setDriverClassName(driver);
			dataSource.setUsername(conexionRemota.getUsuario());
			dataSource.setPassword(conexionRemota.getClave());
			dataSource.setUrl(url);			
			dataSource.setValidationQuery(sentenciaTest);
			dataSource.setMaxActive(maximoConexionesPool);
			dataSource.setMaxIdle(maximoConexionesDormidas);			
			System.out.println("Iniciando un pool de conexiones");
			System.out.println("Pool maximo conexiones: "+maximoConexionesPool);
			System.out.println("Pool maximo conexiones dormidas: "+maximoConexionesDormidas);									
		} catch (Exception e) {
			e.printStackTrace();
			//log.error("ERROR INICIALIZANDO EL POOL DE CONEXIONES A BD", e);
		}
	}

	public static PoolConexiones getInstance() {
		System.out.println("getInstance() activas "+dataSource.getNumActive()+" inactivas "+dataSource.getNumIdle());
		return instance;
	}

	public BasicDataSource getDataSource() {
		return dataSource;
	}

	public void liberaConexion(Connection conexion) {
		try {
			if (null != conexion) {
				conexion.close();
			}
			//System.out.println("liberaConexion() activas "+dataSource.getNumActive()+" inactivas "+dataSource.getNumIdle());

		} catch (Exception e) {
			log.error("Error Cerrando la conexion ",e);
		}
	}

}
