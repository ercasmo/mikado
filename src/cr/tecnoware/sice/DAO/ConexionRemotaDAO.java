package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;

import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.utils.encryption.CryptoUtils;

public class ConexionRemotaDAO extends DAO {

	public ConexionRemotaDAO() {
		super();
	}

	public ConexionRemotaBean getConexioRemota(int tipoConexion, int codigoEntidadProcesadora) {
		ConexionRemotaBean conexionRemota = null;
		try {
			String query = "SELECT c.usuario,c.clave,c.ip,c.puerto,c.tipo,c.directorio,c.entidad_procesadora,c.conexion_segura from conf_conexiones_remotas as c  WHERE c.entidad_procesadora=" + codigoEntidadProcesadora + " and c.tipo=" + tipoConexion;
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);

			if (rs.next()) {
				conexionRemota = new ConexionRemotaBean();
				conexionRemota.setUsuario(rs.getString(1));
				conexionRemota.setClave(rs.getString(2));
				conexionRemota.setDireccionIp(rs.getString(3));
				conexionRemota.setPuerto(rs.getInt(4));
				conexionRemota.setTipoConexion(rs.getInt(5));
				conexionRemota.setDirectorioOrigen(rs.getString(6));
				conexionRemota.setEntidadProcesadora(rs.getInt(7));
				conexionRemota.setConexionSegura(rs.getInt(8));
			}
		} catch (Exception e) {
			super.getLogger().error(e);
			conexionRemota = null;
		} finally {
			super.closeConnection();
		}
		return ConexionRemotaDAO.descifrarDatosDeConexion(conexionRemota);
	}

	private static ConexionRemotaBean descifrarDatosDeConexion(ConexionRemotaBean conexion) {
		if (conexion.getUsuario() != null && conexion.getClave() != null && conexion.getDireccionIp() != null) {
			if (!conexion.getUsuario().equalsIgnoreCase("") && !conexion.getClave().equalsIgnoreCase("") && !conexion.getDireccionIp().equalsIgnoreCase("")) {
				conexion.setUsuario(CryptoUtils.decodificarCadenaBaseDatos(conexion.getUsuario()));
				conexion.setClave(CryptoUtils.decodificarCadenaBaseDatos(conexion.getClave()));
				conexion.setDireccionIp(CryptoUtils.decodificarCadenaBaseDatos(conexion.getDireccionIp()));
			}
		}
		return conexion;
	}

	public ConexionRemotaBean getConexionRemotaObject(int tipoConexion, int entidadProcesadora) {
		String query = "SELECT id,clave,conexion_segura,directorio,entidad_procesadora,ip,puerto,tipo,usuario FROM conf_conexiones_remotas";
		String queryFinal = query + " WHERE tipo = " + tipoConexion + " AND entidad_procesadora = " + entidadProcesadora;
		try {
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(queryFinal);

			while (rs.next()) {
				ConexionRemotaBean conexion = new ConexionRemotaBean();
				conexion.setClave(rs.getString("clave"));
				conexion.setConexionSegura(rs.getInt("conexion_segura"));
				conexion.setDirectorioOrigen(rs.getString("directorio"));
				conexion.setEntidadProcesadora(rs.getInt("entidad_procesadora"));
				conexion.setDireccionIp(rs.getString("ip"));
				conexion.setPuerto(rs.getInt("puerto"));
				conexion.setTipoConexion(rs.getInt("tipo"));
				conexion.setUsuario(rs.getString("usuario"));
				return conexion;
			}
			return null;
		} catch (Exception e) {
			super.getLogger().error(e);
			return null;
		} finally {
			super.closeConnection();
		}

	}
}
