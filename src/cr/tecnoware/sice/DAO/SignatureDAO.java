package cr.tecnoware.sice.DAO;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.applets.bean.Firma;
import cr.tecnoware.sice.context.ApplicationParameters;
import cr.tecnoware.sice.utils.BaseDatosUtils;


public class SignatureDAO {

	private static Logger log = Logger.getLogger("application");
	private Connection conn;

	public SignatureDAO(int entidadProcesadora, ConexionRemotaBean conexionBaseFirmas) {
		try {
			int tipoConexion = ApplicationParameters.getInstance().getInt("firmas.tipoConexion." + entidadProcesadora);
			String nombreBaseDatos = ApplicationParameters.getInstance().getString("firmas.baseDatos." + entidadProcesadora);
			conn = BaseDatosUtils.obtenerConexionJDBC(tipoConexion, conexionBaseFirmas, nombreBaseDatos);

		} catch (Exception e) {
			log.error(ApplicationParameters.getInstance().getString("message.firmas.errorConexionSQL"), e);
			return;
		}
	}

	protected Connection getConnection() {
		return conn;
	}

	public void closeConnection() {
		try {
			if (!conn.isClosed())
				conn.close();
		} catch (Exception e) {
			log.error("Error al cerrar la Conexion de Firmas", e);
		}
	}

	public List<Firma> getSignatures(String cuentaCliente) {
		List<Firma> retorno = null;
		try {
			if (conn != null && !conn.isClosed()) {
				Statement st = conn.createStatement();
				int codigoEntidad = Integer.parseInt(cuentaCliente.substring(0, 4));
				String query = ApplicationParameters.getInstance().getString("firmas.consulta." + codigoEntidad);
				query += " '" + cuentaCliente + "'";
				ResultSet rs = st.executeQuery(query);
				retorno = new ArrayList<Firma>();
				conn.setAutoCommit(false);
				System.out.println(query);
				while (rs.next()) {
					Firma tmp_firma = new Firma();
					Blob blob = rs.getBlob("imagen");
					byte temp[] = blob.getBytes(1, (int) blob.length());
					tmp_firma.setImagen(temp);
					tmp_firma.setCondicion(rs.getString("condicion"));
					tmp_firma.setTipo(rs.getString("tipo"));
					retorno.add(tmp_firma);
				}
			}
		} catch (Exception e) {
			log.error("Error al traer Firmas", e);
		}

		return retorno;

	}

}
