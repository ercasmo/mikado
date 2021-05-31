package cr.tecnoware.sice.DAO;


import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cr.tecnoware.sice.applets.bean.Entidad;
import cr.tecnoware.sice.applets.bean.Oficina;

public class OficinaDAO extends DAO {
	
	public OficinaDAO() {
		super();
	}

	public List<Oficina> getAll(){
		try{
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery("SELECT  id, centronegocio, cod_oficina, cod_banco, codigocontable, escaner_id, nom_oficina,nom_oficina_view,punto_servicio, receptora FROM oficinas ORDER BY cod_oficina ");
			List<Oficina> retorno = new ArrayList<Oficina>();
			while(rs.next())
			{
				Oficina aux =  new Oficina(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getString(7),rs.getString(8),rs.getInt(9),rs.getInt(10));
				retorno.add(aux);
			}
			
			return retorno;
		}
		catch(Exception e)
		{
			super.getLogger().error("Error en conexion a Base de Datos");
			return null;
		}finally{
			super.closeConnection();
		}
	}
	
	public List<Oficina> getAllByEntidad(int entidad){
		try{
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery("SELECT  id, centronegocio, cod_oficina, cod_banco, codigocontable, escaner_id, nom_oficina,nom_oficina_view,punto_servicio, receptora FROM oficinas where cod_banco="+entidad+" ORDER BY cod_oficina ");
			List<Oficina> retorno = new ArrayList<Oficina>();
			while(rs.next())
			{
				Oficina aux =  new Oficina(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(6),rs.getString(7),rs.getString(8),rs.getInt(9),rs.getInt(10));
				retorno.add(aux);
			}
			
			return retorno;
		}
		catch(Exception e)
		{
			super.getLogger().error("Error en conexion a Base de Datos");
			return null;
		}finally{
			super.closeConnection();
		}
	}
	
}
