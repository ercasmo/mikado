package cr.tecnoware.sice.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cr.tecnoware.sice.core.ServicioComunicacionCore;
import cr.tecnoware.sice.core.bean.ConsultaCuentaResponse;
import cr.tecnoware.sice.utils.Utils;

public class CacheServicioVerificarCuenta {

	private static Map<String, String> mapaEstadosCuenta = new LinkedHashMap<String, String>();
	private static final int MAX_REGISTROS = 15000;
	private static final int LONGITUD_CUENTA = 10;
	private static final Logger log = Logger.getLogger("application");
	
	public static String getEstadoCuenta(String cuenta) {
		String ctaFormateada = Utils.fillZerosLeftCuenta(Utils.removeLeadingZeros(cuenta), LONGITUD_CUENTA);
		String estado = null;
		if(mapaEstadosCuenta.containsKey(ctaFormateada))
			estado = mapaEstadosCuenta.get(ctaFormateada);
		if (null == estado) {
			ConsultaCuentaResponse rp =ServicioComunicacionCore.consultarCuenta(ctaFormateada); 
			estado = (null!=rp)?rp.getEstado():null; 
			if (null != estado && !estado.equals(" ")) {
				agregarCuenta(ctaFormateada, estado);
			}else{
				estado="A";
			}
		}

		return estado;
	}

	private static void agregarCuenta(String cuenta, String estado) {
		// Verifica si la cache supero el max registros para limpiarla
		if (mapaEstadosCuenta.size() > MAX_REGISTROS) {
			try {
				mapaEstadosCuenta.clear();
			} catch (Exception e) {
				log.error("CacheServicioVerificarCuenta. Error limpiando Cache.", e);
			}
		}
		mapaEstadosCuenta.put(cuenta, estado);
	}
}
