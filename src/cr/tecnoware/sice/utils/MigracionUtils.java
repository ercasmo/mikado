package cr.tecnoware.sice.utils;

public class MigracionUtils {
	public static String obtenerRutaImagen(String referenciaUnica){
		String ruta = "";		
		String datos=referenciaUnica;		
		ruta+=datos.substring(0,4)+ "/"; //Entidad procesadora
		ruta+=datos.substring(8,4+2+2+4)+ "/";//AAAA
		ruta+=datos.substring(6,4+2+2)+ "/"; //MM
		ruta+=datos.substring(4,4+2)+ "/"; //DD				
		ruta+=datos.substring(12,4+2+2+4+4)+ "/";//TipoCamara
		ruta+=datos.substring(20,4+2+2+4+4+4+4)+ "/";//Lote
		ruta+=datos.substring(16,4+2+2+4+4+4)+ "/";//Oficina		
		//ruta+=datos.substring(24,4+2+2+4+4+4)+ "/";//Entidad Origen o destino de acuerdo a la camara	 
		return ruta;
	}
}
