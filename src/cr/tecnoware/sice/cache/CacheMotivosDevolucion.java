package cr.tecnoware.sice.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import cr.tecnoware.sice.DAO.MotivoDevolucionDAO;
import cr.tecnoware.sice.applets.bean.MotivoDevolucion;

public class CacheMotivosDevolucion {
	
	public static LinkedHashMap<Integer,MotivoDevolucion> listaCodigos = new LinkedHashMap<Integer,MotivoDevolucion>();
    public static Date ultimaRevision=null;
    
    
    /*public static ArrayList<MotivoDevolucion> obtenerCodigosMotivoDevolucion(){
    	if(listaCodigos.size()==0){
    		MotivoDevolucionDAO dao = new MotivoDevolucionDAO();
    		List<MotivoDevolucion> motivos = dao.obtenerCodigosDevolucion();
    		if(motivos.size()>0){
    		//	listaCodigos.put(motivos);
    		}   		
    	}
    	return listaCodigos.values();   	
    }*/
    
}
