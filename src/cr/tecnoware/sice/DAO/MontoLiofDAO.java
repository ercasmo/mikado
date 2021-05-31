package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MontoLiofDAO extends DAO {

	public List<Double> obtenerLimites() {
		List<Double> retorno = new ArrayList<Double>();
		String query = "Select monto_inicial,monto_final from custom_montosCobroImpuesto";
		Statement st;
		try {
			st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				double montoInferior=rs.getDouble(1);
				double montoSuperior=rs.getDouble(2);
				retorno.add(montoInferior);
				retorno.add(montoSuperior);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			super.closeConnection();
		}

		return retorno;
	}

}
