package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;

import cr.tecnoware.sice.applets.bean.ParametrosGenerales;

public class ParametrosGeneralesDAO extends DAO {
	public ParametrosGeneralesDAO() {
		super();
	}

	public ParametrosGenerales obtenerParametrosSistema() {
		ParametrosGenerales parametrosSistema = null;
		try {
			Statement st = super.getConnection().createStatement();
			String query = "SELECT id,monto_truncamiento,monto_conformacion,monto_notificacion,monto_truncamiento_activado,integracion_automatica,rafaga_saliente_activa,maximo_registros_archivo,maximo_tamano_archivo,maximo_tamano_archivo,rafaga_cheque_saliente,intentos_conformacion,monto_riesgo,limite_bajo_valor,fecha_camara FROM definicion_parametrosGenerales";
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				parametrosSistema = new ParametrosGenerales();
				parametrosSistema.setId(rs.getInt("id"));
				parametrosSistema.setMontoTruncamiento(rs.getDouble("monto_truncamiento"));
				parametrosSistema.setMontoConformacion(rs.getDouble("monto_conformacion"));
				parametrosSistema.setMontoNotificacion(rs.getDouble("monto_notificacion"));
				parametrosSistema.setMontoRiesgo(rs.getDouble("monto_riesgo"));
				parametrosSistema.setLimiteBajoValor(rs.getDouble("limite_bajo_valor"));
				parametrosSistema.setMontoTruncamientoActivado(rs.getBoolean("monto_Truncamiento_activado"));
				parametrosSistema.setIntegracionAutomatica(rs.getBoolean("integracion_automatica"));
				parametrosSistema.setRafagaSalienteActiva(rs.getBoolean("rafaga_saliente_activa"));
				parametrosSistema.setMaximoRegistrosArchivo(rs.getInt("maximo_registros_archivo"));
				parametrosSistema.setMaximoTamanoArchivo(rs.getInt("maximo_tamano_archivo"));
				parametrosSistema.setIntentosConformacion(rs.getInt("intentos_conformacion"));
				parametrosSistema.setRafagaChequeSaliente(rs.getString("rafaga_cheque_saliente"));
				parametrosSistema.setFechaCamara(rs.getString("fecha_camara"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error Onteniendo ParametrosGenerales", e);
		} finally {
			super.closeConnection();
		}
		return parametrosSistema;
	}

}
