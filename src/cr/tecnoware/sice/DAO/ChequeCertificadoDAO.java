package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;


public class ChequeCertificadoDAO extends DAO {
	
	public boolean esChequeCertificado(String serial, String cuenta){
		boolean certificado=false;
		try {
			String query = " Select cuenta,documento FROM custom_lecto02 WHERE cuenta='"+cuenta+"' AND documento='"+serial+"'";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			if(rs.next())
				certificado=true;			
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");			
		} finally {
			super.closeConnection();
		}
	return certificado;
	}
}
