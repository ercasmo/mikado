package cr.tecnoware.sice.core.xml.decodificador;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cr.tecnoware.sice.applets.bean.Firma;
import cr.tecnoware.sice.core.bean.ConsultaCuentaDepositariaResponse;
import cr.tecnoware.sice.core.bean.ConsultaCuentaResponse;
import cr.tecnoware.sice.core.bean.ConsultaFirmaResponse;
import cr.tecnoware.sice.core.bean.RegistroNotificacionResponse;

/**
 * 
 * @author PERSONAL
 */
public class XMLDecodificador {

	public static ConsultaFirmaResponse decodificarSalidaFirmas(String salida) throws Exception {
		File archivoTemporal = null;
		ConsultaFirmaResponse consultaFirmaResponse = null;
		try {
			consultaFirmaResponse = new ConsultaFirmaResponse();
			archivoTemporal = File.createTempFile("response", ".xml");
			escribirContenidoArchivoTemporal(archivoTemporal,salida);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(archivoTemporal);
			NodeList listaNodosBody = doc.getElementsByTagName("Body");
			for (int i = 0; i < listaNodosBody.getLength(); i++) {
				Node body = listaNodosBody.item(i);
				if (body.getNodeType() == Node.ELEMENT_NODE) {															
					Element firma = (Element) body;
					boolean terminarLectura = false;
					int contadorIteraciones = 1;
					String condicionFirma = getTagValue("instruccionesFirma", firma);	
					consultaFirmaResponse.setCondicion(condicionFirma);
					while (!terminarLectura) {
						Firma firmaLeida = new Firma();
						String valorFirma = getTagValue("firma" + contadorIteraciones, firma);						
						String tipoFirma = getTagValue("tipoFirma" + contadorIteraciones, firma);						
						if (null != valorFirma) {
							byte[] xmldecode64 = Base64.decodeBase64(valorFirma.getBytes());
							tipoFirma=(null==tipoFirma)?"":tipoFirma;
							firmaLeida.setTipo(tipoFirma);
							firmaLeida.setImagen(xmldecode64);
							firmaLeida.setCondicion(condicionFirma);
							consultaFirmaResponse.getListaFirmas().add(firmaLeida);
						} else {
							terminarLectura = true;
						}
						contadorIteraciones++;
					}
				}
			}
		} finally {
			if (archivoTemporal != null) {
				archivoTemporal.delete();
			}
		}
		return consultaFirmaResponse;
	}

	public static ConsultaCuentaDepositariaResponse decodificarSalidaCuentaDepositaria(String salida) throws Exception {
		File archivoTemporal = null;
		ConsultaCuentaDepositariaResponse consultaResponse = null;
		try {
			consultaResponse = new ConsultaCuentaDepositariaResponse();
			archivoTemporal = File.createTempFile("response", ".xml");
			escribirContenidoArchivoTemporal(archivoTemporal,salida);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(archivoTemporal);
			NodeList listaNodosBody = doc.getElementsByTagName("Body");
			for (int i = 0; i < listaNodosBody.getLength(); i++) {
				Node body = listaNodosBody.item(i);
				if (body.getNodeType() == Node.ELEMENT_NODE) {
					Element elementoBody = (Element) body;
					String cuentaDondeSeDepositoElCheque = getTagValue("cuentaDondeSeDepositoElCheque", elementoBody);
					String tipoCuenta = getTagValue("tipoCuenta", elementoBody);
					consultaResponse.setCuentaDepositaria(cuentaDondeSeDepositoElCheque);
					consultaResponse.setTipoCuenta(tipoCuenta);
				}
			}
		} finally {
			if (archivoTemporal != null) {
				archivoTemporal.delete();
			}
		}

		return consultaResponse;

	}

	public static ConsultaCuentaResponse decodificarSalidaCuenta(String salida) throws Exception {
		File archivoTemporal = null;
		ConsultaCuentaResponse response = null;
		try {
			response = new ConsultaCuentaResponse();
			archivoTemporal = File.createTempFile("response", ".xml");
			escribirContenidoArchivoTemporal(archivoTemporal,salida);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(archivoTemporal);
			NodeList listaNodosBody = doc.getElementsByTagName("Body");
			for (int i = 0; i < listaNodosBody.getLength(); i++) {
				Node body = listaNodosBody.item(i);
				if (body.getNodeType() == Node.ELEMENT_NODE) {
					Element elementoBody = (Element) body;
					String estado = getTagValue("codigoRating", elementoBody);
					response.setEstado(estado);
					String codigo = getTagValue("tipoAgente", elementoBody);
					response.setTipoCuenta(codigo);				
				}
			}
		} finally {
			if (archivoTemporal != null) {
				archivoTemporal.delete();
			}
		}
		return response;
	}
	
	public static RegistroNotificacionResponse decodificarSalidaRegistroNotificacion(String salida) throws Exception {
		File archivoTemporal = null;
		RegistroNotificacionResponse response = null;
		try {
			response = new RegistroNotificacionResponse();
			archivoTemporal = File.createTempFile("response", ".xml");
			escribirContenidoArchivoTemporal(archivoTemporal,salida);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(archivoTemporal);
			NodeList listaNodosBody = doc.getElementsByTagName("Response");
			String descripcionRP="";
			for (int i = 0; i < listaNodosBody.getLength(); i++) {
				Node body = listaNodosBody.item(i);
				if (body.getNodeType() == Node.ELEMENT_NODE) {
					Element elementoBody = (Element) body;
					String idRespuesta = getTagValue("idRespuesta", elementoBody);
					response.setCodigoRespuesta(idRespuesta);
					descripcionRP= getTagValue("descripcion", elementoBody);
								
				}
			}
			NodeList listaNodosBody2 = doc.getElementsByTagName("Body");
			for (int i = 0; i < listaNodosBody2.getLength(); i++) {
				Node body = listaNodosBody2.item(i);
				if (body.getNodeType() == Node.ELEMENT_NODE) {
					Element elementoBody = (Element) body;
					String idRespuesta = getTagValue("idRespuesta", elementoBody);
					response.setCodigoRespuesta(idRespuesta);
					String descripcionRP1 = " "+getTagValue("descripcion", elementoBody);
					if(null != descripcionRP1 && (!"null".equals(descripcionRP1))){
						descripcionRP=descripcionRP+" "+descripcionRP1;
					}
					//response.setDescripcionRespuesta(descripcion);				
				}
			}
			response.setDescripcionRespuesta(descripcionRP);	
		} finally {
			if (archivoTemporal != null) {
				archivoTemporal.delete();
			}
		}
		return response;
	}

	private static String getTagValue(String tag, Element elemento) {
		try {
			NodeList lista = elemento.getElementsByTagName(tag).item(0).getChildNodes();
			Node valor = (Node) lista.item(0);
			return valor.getNodeValue();
		} catch (Exception e) {
		}
		return null;
	}
	
	private static void escribirContenidoArchivoTemporal(File archivoTemporal,String contenido)throws Exception
	{
		FileOutputStream escritor = new FileOutputStream(archivoTemporal);
		escritor.write(contenido.getBytes());
		escritor.close();
		
	}
}
