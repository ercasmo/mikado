package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import cr.tecnoware.sice.applets.bean.ParametrosGenerales;
import cr.tecnoware.sice.applets.bean.UsuarioRevisor;

public class UsuarioRevisorDAO extends DAO {

	public UsuarioRevisorDAO() {
		super();
	}

	public ArrayList<UsuarioRevisor> obtenerUsuariosRevisores() {
		ArrayList<UsuarioRevisor> listaRevisores = null;
		try {
			LinkedHashMap<Integer, UsuarioRevisor> mapaRevisores = new LinkedHashMap<Integer, UsuarioRevisor>();
			listaRevisores = new ArrayList<UsuarioRevisor>();
			Statement st = super.getConnection().createStatement();
			String query = "SELECT USUARIO.id AS idUsuario, USUARIO.login AS login,FIRMAS.monto_inicial AS montoInicial,FIRMAS.monto_final AS montoFinal, FIRMASBANCO.banco_id,BANCO.codBanco,TCA.id AS TCID,TR.cod_transaccion AS TC FROM administracion_firmas AS FIRMAS JOIN seguridad_usuario AS usuario ON firmas.usuario_id=usuario.id JOIN administracion_firmas_bancos AS firmasBanco ON firmas.id = firmasBanco.administracion_firmas_bancos_id JOIN bancos AS banco ON firmasBanco.banco_id= banco.id JOIN tipo_cheque_asignado AS TCA ON firmas.id=TCA.usuario_id JOIN transacciones AS TR ON TCA.tc_id = TR.id WHERE firmas.estado=1 ORDER BY usuario.id";
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				UsuarioRevisor revisor = new UsuarioRevisor();
				revisor.setLogin(rs.getString("login"));
				revisor.setMontoInicial(rs.getDouble("montoInicial"));
				revisor.setMontoFinal(rs.getDouble("montoFinal"));
				revisor.setIdUsuario(rs.getInt("idUsuario"));
				if (!mapaRevisores.containsKey(revisor.getIdUsuario())) {
					mapaRevisores.put(revisor.getIdUsuario(), revisor);
					mapaRevisores.get(revisor.getIdUsuario()).agregarBanco(rs.getInt("CODBANCO"));
					mapaRevisores.get(revisor.getIdUsuario()).agregarTC(rs.getInt("TC"));
				} else {
					mapaRevisores.get(revisor.getIdUsuario()).agregarBanco(rs.getInt("CODBANCO"));
					mapaRevisores.get(revisor.getIdUsuario()).agregarTC(rs.getInt("TC"));
				}
			}
			listaRevisores.addAll(mapaRevisores.values());
			for (int i = 0; i < listaRevisores.size(); i++) {
				String queryConteo = "SELECT count(*) AS total from cheque_entrante where usuario_revisor ='"+listaRevisores.get(i).getLogin()+"'";
				rs = st.executeQuery(queryConteo);	
				if(rs.next()){
					listaRevisores.get(i).setFirmasAsignadas(rs.getInt("total"));
				}
			}						
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error Onteniendo ParametrosGenerales", e);
		} finally {
			super.closeConnection();
		}
		return listaRevisores;
	}

}
