package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cr.tecnoware.sice.applets.bean.Entidad;

public class EntidadDAO extends DAO {

	public EntidadDAO() {
		super();
	}

	public Entidad getByCodigo(int codigo) {
		try {
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery("SELECT CODBANCO,ENTIDAD_MATRIZ,NOMBANCO,NOMBANCOVIEW,  OPERACIONES_CONTABLES_HABILITADAS FROM BANCOS WHERE CODBANCO = "
							+ codigo);
			rs.next();
			Entidad aux = new Entidad(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getInt(5));
			return aux;
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error(e);
			return null;
		} finally {
			super.closeConnection();
		}
	}

	public List<Entidad> getAll() {
		try {
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery("SELECT CODBANCO,ENTIDAD_MATRIZ,NOMBANCO,NOMBANCOVIEW,  OPERACIONES_CONTABLES_HABILITADAS FROM BANCOS");
			List<Entidad> retorno = new ArrayList<Entidad>();
			while (rs.next()) {
				Entidad aux = new Entidad(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getInt(5));
				retorno.add(aux);
			}

			return retorno;
		} catch (Exception e) {
			super.getLogger().error("Error en conexion a Base de Datos");
			return null;
		} finally {
			super.closeConnection();
		}
	}

	public List<Entidad> getAllByQuery(String query) {
		try {
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			List<Entidad> retorno = new ArrayList<Entidad>();
			while (rs.next()) {
				Entidad aux = new Entidad(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4));
				retorno.add(aux);
			}
			return retorno;
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
			return null;
		} finally {
			super.closeConnection();
		}
	}

}
