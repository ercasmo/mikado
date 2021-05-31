package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cr.tecnoware.sice.applets.bean.BolsilloClasificacion;

public class BolsilloDAO extends DAO {

	public BolsilloDAO() {
		super();
	}

	public List<BolsilloClasificacion> getAllByQuery(String query) {
		try {
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			List<BolsilloClasificacion> retorno = new ArrayList<BolsilloClasificacion>();
			while (rs.next()) {
				BolsilloClasificacion aux = new BolsilloClasificacion(rs.getInt(1), rs.getInt(2), rs
						.getInt(4),rs.getInt(3),rs.getInt(5),rs.getDouble(6),rs.getDouble(7));
				retorno.add(aux);
			}
			return retorno;
		} catch (Exception e) {
			super.getLogger().error("Error en conexion a Base de Datos");
			System.out.println("error "+e);
			return null;
		} finally {
			super.closeConnection();
		}
	}

}