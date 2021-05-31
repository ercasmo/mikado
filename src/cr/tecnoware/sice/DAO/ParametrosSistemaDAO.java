package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;

import cr.tecnoware.sice.applets.bean.ParametrosSistema;

public class ParametrosSistemaDAO extends DAO {

	public ParametrosSistemaDAO() {
		super();
	}

	public ParametrosSistema findByKey(String key) {
		try {
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st
					.executeQuery("SELECT llave, valor FROM PARAMETROS_SISTEMA where llave='"
							+ key + "'");
			rs.next();

			return new ParametrosSistema(rs.getString(1), rs.getString(2));

		} catch (Exception e) {
			super.getLogger().error(
					"Error en consulta de Parametros de Sistema");
			return null;
		} finally {
			super.closeConnection();
		}
	}
}
