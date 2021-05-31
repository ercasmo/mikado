package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cr.tecnoware.sice.applets.bean.MotivoDevolucion;


public class MotivoDevolucionDAO extends DAO {
	private static final int CODIGO_CLASIFICACION_FINTEC = 2;
	private static final int CODIGO_PRODUCTO_CHEQUES = 30;
	
	
	public MotivoDevolucionDAO(){
		super();
	}
	
	public List<MotivoDevolucion> obtenerCodigosRevisionFirmas(){
		try{
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery("SELECT CODIGO,CODIGO_CLASIFICACION, MOTIVO_ASOCIADO, NOMBRE, NOMBRE_CORTO, TIPO, CODIGO_PRODUCTO FROM DEFINICION_MOTIVODEVOLUCION WHERE CODIGO_PRODUCTO = "+CODIGO_PRODUCTO_CHEQUES+" AND HABILITADO_FIRMA = 1 ORDER BY CODIGO");
			List<MotivoDevolucion> retorno = new ArrayList<MotivoDevolucion>();
			while(rs.next())
			{
				MotivoDevolucion aux = new MotivoDevolucion(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4), rs.getString(5), rs.getInt(6));
				retorno.add(aux);
			}
						
			return retorno;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
			return null;
		}finally{
			super.closeConnection();
		}
	}
	
	public List<MotivoDevolucion> obtenerCodigosDevolucion(){
		try{
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery("SELECT CODIGO,CODIGO_CLASIFICACION, MOTIVO_ASOCIADO, NOMBRE, NOMBRE_CORTO, TIPO, CODIGO_PRODUCTO,CATEGORIA_DEVOLUCION FROM DEFINICION_MOTIVODEVOLUCION WHERE CODIGO_PRODUCTO = "+CODIGO_PRODUCTO_CHEQUES+" ORDER BY CODIGO");
			List<MotivoDevolucion> retorno = new ArrayList<MotivoDevolucion>();
			while(rs.next())
			{
				MotivoDevolucion aux = new MotivoDevolucion(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4), rs.getString(5), rs.getInt(6),rs.getInt(7));
				retorno.add(aux);
			}
						
			return retorno;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
			return null;
		}finally{
			super.closeConnection();
		}
	}
	
}
