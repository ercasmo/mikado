package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

import cr.tecnoware.sice.applets.utils.Utils;

public class Lecto09DAO extends DAO  {
	
	public LinkedHashMap<String, Integer> getCuentasRetornarCheques()
	{
		LinkedHashMap<String, Integer> mapa = new LinkedHashMap<String, Integer>();
		String query = "Select id, cuenta from custom_Lecto09";
		Statement st;
		try {
			st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				String cuenta = Utils.fillZerosLeft(rs.getString(2), 15);
				mapa.put(cuenta, rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			super.closeConnection();
		}
		
		return mapa;
	}

}
