package cr.tecnoware.sice.core.xml.generador;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cr.tecnoware.sice.core.bean.XMLBean;


public class GeneradorXMLSTR {

	// Codificacion
	private static final String IDENTACION="";
        private static final String SALTOLINEA="";
	private void generaEncabezadoXML(StringBuffer contenido) {
			contenido.append("<Request>");
			contenido.append(SALTOLINEA);
	}

	private void generarDocumentoXML(XMLBean bean, XMLBean body, StringBuffer contenido) {
		Class obj;
		Field[] stra;
		Object value = "";
		Method met;

		obj = bean.getClass();
		stra = obj.getDeclaredFields();
		try {
			for (int i = 0; i < stra.length; i++) {
				// utilizamos algo de reflection para obtener los valores de los
				// atributos
				met = obj.getMethod(getterFind(stra[i].getName()), new Class[] {});
				String tempSt = obj.getSimpleName().toLowerCase();
				value = met.invoke(bean, new Object[] {});
				// a. Crear item
				if ((null != value)) {
					contenido.append(IDENTACION);
					contenido.append("<");
					contenido.append(stra[i].getName());
					contenido.append(">");
					// b. Asignar un dato al item
					contenido.append(value.toString());
					contenido.append("</");
					contenido.append(stra[i].getName());
					contenido.append(">");
					contenido.append(SALTOLINEA);
				}
			}
			if(null!=body){
				obj = body.getClass();
				stra = obj.getDeclaredFields();
				contenido.append(IDENTACION);
				contenido.append("<Body>");
				contenido.append(SALTOLINEA);
				for (int i = 0; i < stra.length; i++) {
					met = obj.getMethod(getterFind(stra[i].getName()), new Class[] {});
					String tempSt = obj.getSimpleName().toLowerCase();
					value = met.invoke(body, new Object[] {});
					contenido.append(IDENTACION);
					contenido.append(IDENTACION);
					contenido.append("<");
					contenido.append(stra[i].getName());
					contenido.append(">");
					contenido.append(value.toString());
					contenido.append("</");
					contenido.append(stra[i].getName());
					contenido.append(">");
					contenido.append(SALTOLINEA);
				}
				contenido.append(IDENTACION);
				contenido.append("</Body>");
				contenido.append(SALTOLINEA);
				contenido.append("</Request>");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}




	private String getterFind(String atName) {
		String attributeName = "";
		atName = atName.replaceFirst(atName.substring(0, 1), atName.substring(0, 1).toUpperCase());
		attributeName = "get" + atName;
		return attributeName;
	}

	public String generarXML(XMLBean solicitud,XMLBean response) {
		StringBuffer contenidoArchivo = new StringBuffer("");
		generaEncabezadoXML(contenidoArchivo);
		generarDocumentoXML(solicitud,response,contenidoArchivo);
		return contenidoArchivo.toString();
	}
}