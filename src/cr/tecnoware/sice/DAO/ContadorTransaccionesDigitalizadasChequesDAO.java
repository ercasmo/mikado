package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;

import cr.tecnoware.sice.applets.bean.ContadorTransaccionesDigitalizadasCheques;

public class ContadorTransaccionesDigitalizadasChequesDAO extends DAO {
	
	public ContadorTransaccionesDigitalizadasChequesDAO()
	{
		super();
	}

	public ContadorTransaccionesDigitalizadasCheques getContadores() {
		try {
			String query = "Select contador_entrante,contador_saliente,contador_externa,contador_interna from Contador_Transacciones_Digitalizadas_Cheques";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			ContadorTransaccionesDigitalizadasCheques contador = new ContadorTransaccionesDigitalizadasCheques(); 

			while (rs.next()) {
				contador.setContadorEntrante(rs.getInt(1));
				contador.setContadorSaliente(rs.getInt(2));
				contador.setContadorExterna(rs.getInt(3));
				contador.setContadorInterna(rs.getInt(4));
			}

			return contador;

		} catch (Exception e) {
			super.getLogger().error(e);
			return null;
		} finally {
			super.closeConnection();
		}
	}

	public int updateContadores(ContadorTransaccionesDigitalizadasCheques contador) {
		try {
			String query = "Update Contador_Transacciones_Digitalizadas_Cheques set contador_entrante = "+contador.getContadorEntrante()+",contador_saliente = "+contador.getContadorSaliente()+",contador_externa = "+contador.getContadorExterna()+",contador_interna = "+contador.getContadorInterna();
			Statement st = super.getConnection().createStatement();
			int a = st.executeUpdate(query);
			return a;

		} catch (Exception e) {
			super.getLogger().error(e);
			return 0;
		} finally {
			super.closeConnection();
		}
	}
	
}
