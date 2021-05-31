package cr.tecnoware.sice.utils;

import java.lang.reflect.Field;

import cr.tecnoware.sice.orm.bean.DomainBase;

public class GeneradorSQLUtils {

	
	public String generarSentenciaInsert(String prefijo,DomainBase objeto){
		String nombreClase=objeto.getClass().getCanonicalName();
		nombreClase = nombreClase.replace("cr.tecnoware.sice.orm.bean.","");
		Class clase = objeto.getClass();
		Field []campos = clase.getDeclaredFields();
		String sentenciaInsert="";
		sentenciaInsert+="INSERT into "+prefijo+nombreClase+ " ";
		for (int i = 0; i < campos.length; i++) {
			if(i!=campos.length-1){
			   sentenciaInsert+=campos[i].getName()+",";
			}else{
				sentenciaInsert+=campos[i].getName()+" values (";
			}
		}
		
		for (int i = 0; i < campos.length; i++) {
		}
		sentenciaInsert+=")";
		
		
		
		
		return sentenciaInsert;
		
	}
	
	
}
