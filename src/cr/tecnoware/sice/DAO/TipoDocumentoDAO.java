package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cr.tecnoware.sice.applets.bean.TipoDocumento;

public class TipoDocumentoDAO extends DAO{
	public TipoDocumentoDAO()
	{
		super();
	}
	
	public List<TipoDocumento> getALL()
	{
		List<TipoDocumento> td=new ArrayList<TipoDocumento>();
		String  query="SELECT id, cod_transaccion as tipo_documento, corregirmonto as monto, nom_transaccion, boleta, corregir_banco as banco, corregir_cuenta as cuenta,corregir_documento as documento, corregir_oficina as oficina, tipo_operacion FROM transacciones";

		try
		{
			Statement st = super.getConnection().createStatement(); 
			ResultSet rs = st.executeQuery(query);

			if(rs!=null)
			{
				while(rs.next())
				{
					TipoDocumento tipoDocumento=new TipoDocumento();
					tipoDocumento.setId(rs.getInt("id"));
					tipoDocumento.setBanco(rs.getInt("banco"));
					tipoDocumento.setCuenta(rs.getInt("cuenta"));
					tipoDocumento.setDescripcion(rs.getString("nom_transaccion"));
					tipoDocumento.setDocumento(rs.getInt("documento"));
					tipoDocumento.setMonto(rs.getInt("monto"));
					tipoDocumento.setOficina(rs.getInt("oficina"));
					tipoDocumento.setTipoDocumento(rs.getInt("tipo_documento"));
					tipoDocumento.setTipoOperacion(rs.getInt("tipo_operacion"));
					tipoDocumento.setBoleta(rs.getBoolean("boleta"));
					td.add(tipoDocumento);
					
					
					
				}
			}
		}catch(Exception e)
		{
			System.out.println("obteniendo tipos de cheques ");
			super.getLogger().error(e);
			e.printStackTrace();
		}finally{
			super.closeConnection();
		}
		return td;
	}

}
