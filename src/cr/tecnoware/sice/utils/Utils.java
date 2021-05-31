package cr.tecnoware.sice.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import cr.tecnoware.sice.applets.constantes.AplicacionConstantes;
import cr.tecnoware.sice.utils.Utils;

import cr.tecnoware.sice.applets.bean.ChequeCorregido;
import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.applets.bean.ReporteDataGeneral;
import cr.tecnoware.sice.applets.bean.ReporteGeneralEntidad;
import cr.tecnoware.sice.applets.bean.ReporteGeneralOficina;
import cr.tecnoware.sice.applets.bean.TransaccionEntrante;
import cr.tecnoware.sice.applets.bean.TransaccionSaliente;
import cr.tecnoware.sice.applets.constantes.AplicacionConstantes;
import cr.tecnoware.sice.applets.constantes.ChequeConstantes;
import cr.tecnoware.sice.applets.constantes.ReporteConstantes;
import cr.tecnoware.sice.context.ApplicationParameters;
import cr.tecnoware.sice.utils.comparators.EntidadClienteCoparator;
import cr.tecnoware.sice.utils.comparators.EntidadEmisoraComparator;
import cr.tecnoware.sice.utils.comparators.OficinaClienteComparator;

public class Utils {

	/**
	 * Este método coloca ceros a la izquierda de un String
	 * 
	 * @param string
	 *            el String al que se le van a colocar los ceros
	 * @param fillLength
	 *            el numero de ceros que se van a colocar
	 * @return el String recibido con ceros a la izquierda solicitados o null
	 */
	public static String fillZerosLeft(String string, int length) {
		int stringLength = string.length();
		int numberZeros = length - stringLength;
		if (numberZeros >= 0) {
			for (int j = 0; j < numberZeros; j++) {
				string = "0" + string;
			}
		} else {
			return null;
		}
		return string;
	}

	public static String fillZerosLeftCuenta(String string, int length) {
		int stringLength = string.length();
		int numberZeros = length - stringLength;
		if (numberZeros >= 0) {
			for (int j = 0; j < numberZeros; j++) {
				string = "0" + string;
			}
		} else {
			return string;
		}
		return string;
	}

	public static List<String> formatCMC7(String cmc7) {
		if (cmc7.length() == 0)
			return null;
		if (cmc7.length() < 37)
			return null;
		if (cmc7.indexOf("!") != -1)
			return null;

		try {

			List<String> retorno = new ArrayList<String>();

			cmc7 = cmc7.replaceAll(" ", "");
			cmc7 = cmc7.trim();
			String serial = cmc7.substring(1, 9);
			String entidad = cmc7.substring(10, 14);
			String oficina = cmc7.substring(15, 19);
			String digito = cmc7.substring(20, 22);
			String cuenta = cmc7.substring(23, 33);
			String tc = cmc7.substring(34, 36);
			String monto = "000000000000";

			if (cmc7.length() > 37) {
				monto = cmc7.substring(38, cmc7.length() - 1);
			}

			retorno.add(serial);
			retorno.add(entidad);
			retorno.add(oficina);
			retorno.add(digito);
			retorno.add(cuenta);
			retorno.add(tc);
			retorno.add(monto);

			return retorno;
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Metodo que separa una cuenta cliente en cada uno de sus componentes.
	 * 
	 * @param cuentaCliente
	 *            Cuenta cliente asociada.
	 * @return
	 */
	public static String[] cuentaClienteSplit(String cuentaCliente) {
		String cmc7Split[] = new String[4];
		cmc7Split[0] = cuentaCliente.substring(0, 4);// Banco
		cmc7Split[1] = cuentaCliente.substring(4, 8);// Oficina
		cmc7Split[2] = cuentaCliente.substring(8, 10);// Digito Control
		cmc7Split[3] = cuentaCliente.substring(10, 20);// Cuenta cliente

		return cmc7Split;
	}

	/**
	 * Separa una fecha en un vector de String tamaño 3. [0] Dia [1] Mes [2] Ano
	 * 
	 * @param fecha
	 *            java.sql.Timestamp para convertir.
	 */
	public static String[] splitFecha(Timestamp fecha) {
		String[] fechaSplit = new String[3];
		String fechaStr = fecha.toString();
		fechaSplit[0] = fechaStr.substring(8, 10);
		fechaSplit[1] = fechaStr.substring(5, 7);
		fechaSplit[2] = fechaStr.substring(0, 4);
		return fechaSplit;
	}

	/**
	 * Construye la ruta para el repositorio de imagenes entrante.
	 * 
	 * @param transaccion
	 *            Transaccion asociada a la imagen.
	 * @return Estructura de directorios.
	 */
	public static String getRutaRepositorioImagenesEntrante(TransaccionEntrante transaccion) {
		String[] fecha = splitFecha(transaccion.getFecha());
		String ruta = "";
		ruta += Utils.fillZerosLeft("" + transaccion.getEntidadProcesadora(), 4) + "/";
		ruta += Utils.fillZerosLeft(fecha[2], 4) + "/";
		ruta += Utils.fillZerosLeft(fecha[1], 2) + "/";
		ruta += Utils.fillZerosLeft(fecha[0], 2) + "/";
		return ruta;
	}

	public static String getRutaRepositorioImagenes(ChequesDigitalizados transaccion) {
		String[] fecha = splitFecha(transaccion.getFecha());
		String ruta = "";
		ruta += Utils.fillZerosLeft("" + transaccion.getEntidadProcesadora(), 4) + "/";
		ruta += Utils.fillZerosLeft(fecha[2], 4) + "/";
		ruta += Utils.fillZerosLeft(fecha[1], 2) + "/";
		ruta += Utils.fillZerosLeft(fecha[0], 2) + "/TEMP/";
		return ruta;
	}

	/**
	 * Construye la ruta para el repositorio de imagenes entrante.
	 * 
	 * @param transaccion
	 *            Transaccion asociada a la imagen.
	 * @return Estructura de directorios.
	 */
	public static String getRutaRepositorioImagenesSaliente(TransaccionSaliente transaccion) {
		String[] fecha = splitFecha(transaccion.getFecha());
		String ruta = "";
		ruta += Utils.fillZerosLeft("" + transaccion.getEntidadProcesadora(), 4) + "/";
		ruta += Utils.fillZerosLeft(fecha[2], 4) + "/";
		ruta += Utils.fillZerosLeft(fecha[1], 2) + "/";
		ruta += Utils.fillZerosLeft(fecha[0], 2) + "/";
		ruta += Utils.fillZerosLeft("" + transaccion.getOficinaProcesadora(), 4) + "/";
		ruta += Utils.fillZerosLeft("" + transaccion.getOficinaProcesadora(), 4) + "/";
		ruta += Utils.fillZerosLeft("" + transaccion.getNumeroLote(), 4) + "/";
		return ruta;
	}

	/*
	 * Recibe monto double y lo convierte a String de n caracteres con 2
	 * decimales hasta completar la longitud recibida por parametro.
	 */
	public static String convertirMonto(double monto, int longitudToral) {
		String fmt = "0.00#";
		DecimalFormat df = new DecimalFormat(fmt);
		String MontoStr = df.format(monto);
		String val = MontoStr;
		String MontoEnviar = "";
		if (val.indexOf(".") > 0) {
			val = val.substring(0, val.indexOf(".") + 3);
			for (int i = 0; i < val.length(); i++) {
				if (val.charAt(i) != '.')
					MontoEnviar += val.charAt(i);
			}
		} else if (val.indexOf(",") > 0) {
			val = val.substring(0, val.indexOf(",") + 3);
			for (int i = 0; i < val.length(); i++) {
				if (val.charAt(i) != ',')
					MontoEnviar += val.charAt(i);
			}
		} else
			MontoEnviar = val + "00";
		int t = longitudToral - MontoEnviar.length();
		for (int i = 0; i < t; i++)
			MontoEnviar = "0" + MontoEnviar;
		return MontoEnviar;
	}

	/**
	 * @param monto
	 *            Monto a redondear.
	 * @param decimales
	 *            Decimales a redondear.
	 * @return Monto redondeado.
	 */
	public static double redondearMonto(double monto, int decimales) {
		return Math.round(monto * Math.pow(10, decimales)) / Math.pow(10, decimales);

	}

	// Metodo encargado de llevar el archivo pdf a arreglo de byte
	public static byte[] pdfToByte(File reporte) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(reporte);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		try {
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum);
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
		return bos.toByteArray();
	}

	public static String convertToDateYYYYMMDDHHMMSS(Date date) {
		return (date.getYear() + 1900) + "-" + fillZerosLeft((date.getMonth() + 1) + "", 2) + "-" + fillZerosLeft((date.getDate()) + "", 2) + " " + fillZerosLeft((date.getHours()) + "", 2) + ":" + fillZerosLeft((date.getMinutes()) + "", 2) + ":" + fillZerosLeft((date.getSeconds()) + "", 2);
	}

	public static String convertToDateYYYYMMDD(Date date) {
		return (date.getYear() + 1900) + "-" + fillZerosLeft((date.getMonth() + 1) + "", 2) + "-" + fillZerosLeft((date.getDate()) + "", 2);
	}

	public static String convertToDateYYYYDDMM(Date date) {
		return (date.getYear() + 1900) + "-" + fillZerosLeft((date.getDate()) + "", 2) + "-" + fillZerosLeft((date.getMonth() + 1) + "", 2);
	}

	public static byte[] imgToByte(String nomImg) throws FileNotFoundException {
		File file = new File("c:\\" + nomImg + ".jpg");
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		try {
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum);
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
		return bos.toByteArray();
	}

	public static ArrayList<Object> calcularDataGeneral(List<Object> lista, int tipoAgrupacion) {

		ArrayList<Object> listaData = new ArrayList<Object>();
		try {
			switch (tipoAgrupacion) {
			case ReporteConstantes.TIPO_AGRUPAR_POR_ENTIDAD_EMISORA:
				listaData = clasificarEntidad(lista, ReporteConstantes.TIPO_AGRUPAR_POR_ENTIDAD_EMISORA);
				break;
			case ReporteConstantes.TIPO_AGRUPAR_POR_ENTIDAD_CLIENTE:
				listaData = clasificarEntidadCliente(lista, ReporteConstantes.TIPO_AGRUPAR_POR_ENTIDAD_CLIENTE);
				break;
			case ReporteConstantes.TIPO_AGRUPAR_POR_OFICINA:
				listaData = clasificarOficinaCliente(lista, ReporteConstantes.TIPO_AGRUPAR_POR_OFICINA);
				break;
			default:
				System.out.println("Tipo no implementado");
				break;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return listaData;
	}

	public static ArrayList<Object> clasificarEntidad(List<Object> listaTransacciones, int tipoReporte) {
		ArrayList<Object> listaGeneral = new ArrayList<Object>();

		if (listaTransacciones.size() > 0) {
			// ordeno la lista por entidad emisora
			switch (tipoReporte) {
			case ReporteConstantes.TIPO_AGRUPAR_POR_ENTIDAD_EMISORA:
				Collections.sort(listaTransacciones, new EntidadEmisoraComparator());
				break;
			case ReporteConstantes.TIPO_AGRUPAR_POR_ENTIDAD_CLIENTE:
				Collections.sort(listaTransacciones, new EntidadClienteCoparator());
				System.out.println("Agrupando por entidad Cliente");
				break;

			default:
				System.out.println("Llego un tipo no mapeado " + tipoReporte);
				break;
			}

			String entidadEmisoraPivot = ((ReporteDataGeneral) listaTransacciones.get(0)).getEntidadEmisora();
			// Itero sobre la lista y voy sacando entidad emisora, total
			int numeroTransacciones = 0;
			double montoTotal = 0;
			for (int i = 0; i < listaTransacciones.size(); i++) {
				ReporteDataGeneral transaccion = (ReporteDataGeneral) listaTransacciones.get(i);

				if (entidadEmisoraPivot.equalsIgnoreCase(transaccion.getEntidadEmisora())) {
					numeroTransacciones = numeroTransacciones + 1;
					montoTotal += transaccion.getMonto();
				} else {

					ReporteGeneralEntidad registro = new ReporteGeneralEntidad();
					registro.setEntidad(entidadEmisoraPivot);
					registro.setMonto(montoTotal);
					registro.setTransacciones(numeroTransacciones);
					listaGeneral.add(registro);
					entidadEmisoraPivot = transaccion.getEntidadEmisora();
					montoTotal = transaccion.getMonto();
					numeroTransacciones = 1;
				}
			}

			ReporteGeneralEntidad registro = new ReporteGeneralEntidad();
			registro.setEntidad(entidadEmisoraPivot);
			registro.setMonto(montoTotal);
			registro.setTransacciones(numeroTransacciones);
			listaGeneral.add(registro);
			// Contador llevo monto total.
		}
		return listaGeneral;
	}

	public static ArrayList<Object> clasificarEntidadCliente(List<Object> listaTransacciones, int tipoReporte) {
		ArrayList<Object> listaGeneral = new ArrayList<Object>();

		if (listaTransacciones.size() > 0) {
			// ordeno la lista por entidad emisora
			Collections.sort(listaTransacciones, new EntidadClienteCoparator());

			String entidadEmisoraPivot = ((ReporteDataGeneral) listaTransacciones.get(0)).getEntidadCliente();
			// Itero sobre la lista y voy sacando entidad emisora, total
			int numeroTransacciones = 0;
			double montoTotal = 0;
			for (int i = 0; i < listaTransacciones.size(); i++) {
				ReporteDataGeneral transaccion = (ReporteDataGeneral) listaTransacciones.get(i);

				if (entidadEmisoraPivot.equalsIgnoreCase(transaccion.getEntidadCliente())) {
					numeroTransacciones = numeroTransacciones + 1;
					montoTotal += transaccion.getMonto();
				} else {

					ReporteGeneralEntidad registro = new ReporteGeneralEntidad();
					registro.setEntidad(entidadEmisoraPivot);
					registro.setMonto(montoTotal);
					registro.setTransacciones(numeroTransacciones);
					listaGeneral.add(registro);
					entidadEmisoraPivot = transaccion.getEntidadCliente();
					montoTotal = transaccion.getMonto();
					numeroTransacciones = 1;
				}
			}
			ReporteGeneralEntidad registro = new ReporteGeneralEntidad();
			registro.setEntidad(entidadEmisoraPivot);
			registro.setMonto(montoTotal);
			registro.setTransacciones(numeroTransacciones);
			listaGeneral.add(registro);
			// Contador llevo monto total.
		}
		return listaGeneral;
	}

	public static void main(String args[]) {
		List<Object> listaExportar = new ArrayList<Object>();
		ReporteDataGeneral temp = new ReporteDataGeneral();

		temp.setEntidadCliente("0161");
		temp.setCuentaDepositaria("00070039870070086454");
		temp.setEntidadEmisora("0007");
		temp.setOficinaCliente("0039");
		temp.setCuentaDocumento("01611234121234567890");
		temp.setTc(90);
		temp.setSerial("12345678");
		temp.setFecha(new Timestamp(System.currentTimeMillis()));
		temp.setMonto(2000.00);

		listaExportar.add(temp);
		temp = new ReporteDataGeneral();
		temp.setEntidadCliente("0161");
		temp.setCuentaDepositaria("00070039870070086454");
		temp.setEntidadEmisora("0007");
		temp.setOficinaCliente("0100");
		temp.setCuentaDocumento("01611234121234567890");
		temp.setTc(90);
		temp.setSerial("12345678");
		temp.setFecha(new Timestamp(System.currentTimeMillis()));
		temp.setMonto(2000.00);

		listaExportar.add(temp);
		temp = new ReporteDataGeneral();
		temp.setEntidadCliente("0161");
		temp.setCuentaDepositaria("00070039870070086454");
		temp.setEntidadEmisora("0001");
		temp.setOficinaCliente("0039");
		temp.setCuentaDocumento("01611234121234567890");
		temp.setTc(90);
		temp.setSerial("12345678");
		temp.setFecha(new Timestamp(System.currentTimeMillis()));
		temp.setMonto(2000.00);

		listaExportar.add(temp);
		temp = new ReporteDataGeneral();
		temp.setEntidadCliente("0161");
		temp.setCuentaDepositaria("00070039870070086454");
		temp.setEntidadEmisora("0007");
		temp.setOficinaCliente("0039");
		temp.setCuentaDocumento("01611234121234567890");
		temp.setTc(90);
		temp.setSerial("12345678");
		temp.setFecha(new Timestamp(System.currentTimeMillis()));
		temp.setMonto(2000.00);

		listaExportar.add(temp);
		temp = new ReporteDataGeneral();
		temp.setEntidadCliente("0161");
		temp.setCuentaDepositaria("00070039870070086454");
		temp.setEntidadEmisora("0007");
		temp.setOficinaCliente("0031");
		temp.setCuentaDocumento("01611234121234567890");
		temp.setTc(90);
		temp.setSerial("12345678");
		temp.setFecha(new Timestamp(System.currentTimeMillis()));
		temp.setMonto(2000.00);

		listaExportar.add(temp);
		temp = new ReporteDataGeneral();
		temp.setEntidadCliente("0161");
		temp.setCuentaDepositaria("00070039870070086454");
		temp.setEntidadEmisora("0007");
		temp.setOficinaCliente("0001");
		temp.setCuentaDocumento("01611234121234567890");
		temp.setTc(90);
		temp.setSerial("12345678");
		temp.setFecha(new Timestamp(System.currentTimeMillis()));
		temp.setMonto(2000.00);

		listaExportar.add(temp);
		temp = new ReporteDataGeneral();
		temp.setEntidadCliente("0161");
		temp.setCuentaDepositaria("00070039870070086454");
		temp.setEntidadEmisora("0007");
		temp.setOficinaCliente("0039");
		temp.setCuentaDocumento("01611234121234567890");
		temp.setTc(90);
		temp.setSerial("12345678");
		temp.setFecha(new Timestamp(System.currentTimeMillis()));
		temp.setMonto(2000.00);

		listaExportar.add(temp);
		temp = new ReporteDataGeneral();
		temp.setEntidadCliente("0161");
		temp.setCuentaDepositaria("00070039870070086454");
		temp.setEntidadEmisora("0007");
		temp.setOficinaCliente("0100");
		temp.setCuentaDocumento("01611234121234567890");
		temp.setTc(90);
		temp.setSerial("12345678");
		temp.setFecha(new Timestamp(System.currentTimeMillis()));
		temp.setMonto(2000.00);

		listaExportar.add(temp);

		clasificarOficinaCliente(listaExportar, 0);
	}

	public static ArrayList<Object> clasificarOficinaCliente(List<Object> listaTransacciones, int tipoReporte) {
		ArrayList<Object> listaGeneral = new ArrayList<Object>();

		if (listaTransacciones.size() > 0) {
			// ordeno la lista por entidad emisora
			Collections.sort(listaTransacciones, new OficinaClienteComparator());

			String oficinaClientePivot = ((ReporteDataGeneral) listaTransacciones.get(0)).getOficinaCliente();
			// Itero sobre la lista y voy sacando entidad emisora, total
			int numeroTransacciones = 0;
			double montoTotal = 0;
			for (int i = 0; i < listaTransacciones.size(); i++) {
				ReporteDataGeneral transaccion = (ReporteDataGeneral) listaTransacciones.get(i);

				if (oficinaClientePivot.equalsIgnoreCase(transaccion.getOficinaCliente())) {
					numeroTransacciones = numeroTransacciones + 1;
					montoTotal += transaccion.getMonto();
				} else {

					ReporteGeneralOficina registro = new ReporteGeneralOficina();
					registro.setOficina(oficinaClientePivot);
					registro.setMonto(montoTotal);
					registro.setTransacciones(numeroTransacciones);
					listaGeneral.add(registro);
					oficinaClientePivot = transaccion.getOficinaCliente();
					montoTotal = transaccion.getMonto();
					numeroTransacciones = 1;
				}
			}
			ReporteGeneralOficina registro = new ReporteGeneralOficina();
			registro.setOficina(oficinaClientePivot);
			registro.setMonto(montoTotal);
			registro.setTransacciones(numeroTransacciones);
			listaGeneral.add(registro);
			// Contador llevo monto total.
		}
		return listaGeneral;
	}

	public static int getTipoConexion(int tipoCamara) {
		int tipCam = 0;
		switch (tipoCamara) {
		case AplicacionConstantes.CAMARA_ENTRANTE:
			tipCam = AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_ENTRANTE;
			break;
		case AplicacionConstantes.CAMARA_EXTERNA:
			tipCam = AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_EXTERNA;
			break;
		case AplicacionConstantes.CAMARA_INTERNA:
			tipCam = AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_INTERNA;
			break;
		case AplicacionConstantes.CAMARA_SALIENTE:
			tipCam = AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_SALIENTE;
			break;

		default:
			tipCam = 0;
			break;
		}
		return tipCam;
	}

	public static String buildMICR(ChequeCorregido cheque) {
		return (ApplicationParameters.getInstance().getInt("tipo.micr.activado") == AplicacionConstantes.MICR_E13B) ? buildE13B(cheque) : buildCMC7(cheque);
	}

	public static String buildCMC7(ChequeCorregido cheque) {
		String cmc7 = null;
		cmc7 = "<" + cheque.getSerial() + ">";
		cmc7 += cheque.getCodigoBanco() + ";";
		cmc7 += cheque.getCodigoOficina() + ";";
		cmc7 += cheque.getCuenta() + ";";
		cmc7 += cheque.getTipoCheque() + ";";
		cmc7 += Utils.fillZerosLeft(cheque.getMonto() + "", 16) + ";";
		return cmc7;
	}

	public static String getReferencia() {
		GregorianCalendar calendario = new GregorianCalendar();
		calendario.setTime(Calendar.getInstance().getTime());
		String referencia = fillZerosLeft("" + calendario.get(Calendar.YEAR), 4);
		referencia += fillZerosLeft("" + (calendario.get(Calendar.MONTH) + 1), 2);
		referencia += fillZerosLeft("" + calendario.get(Calendar.DAY_OF_MONTH), 2);
		referencia += fillZerosLeft("" + calendario.get(Calendar.HOUR_OF_DAY), 2);
		referencia += fillZerosLeft("" + calendario.get(Calendar.MINUTE), 2);
		referencia += fillZerosLeft("" + calendario.get(Calendar.SECOND), 2);
		return referencia;
	}

	public static String obtenerIpServidor() {
		String ip = "127.0.0.1";
		try {
			InetAddress a = InetAddress.getByName(InetAddress.getLocalHost().getHostName());
			ip = a.getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}

	public static String removeLeadingZeros(String str) {
		if (str == null) {
			return null;
		}
		char[] chars = str.toCharArray();
		int index = 0;
		for (; index < str.length(); index++) {
			if (chars[index] != '0') {
				break;
			}
		}
		return (index == 0) ? str : str.substring(index);
	}

	// ajuste para el monto y que el serial en el e13b quede de 7
	public static String buildE13B(ChequeCorregido cheque) {
		String e13b = null;
		e13b = fillZerosLeft(Utils.removeLeadingZeros(cheque.getTipoCheque()), 4) + ">";
		e13b += fillZerosLeft(Utils.removeLeadingZeros(cheque.getCodigoBanco()), 4) + ">";
		e13b += fillZerosLeft(Utils.removeLeadingZeros(cheque.getCuenta()), 15) + ":";
		e13b += Utils.fillZerosLeft(Utils.removeLeadingZeros(cheque.getSerial()), 7) + ":=";
		String monto = Utils.convertirMontoDosDecimales(cheque.getMonto());
		if (monto != null) {
			monto = monto.replaceAll("[.]", " ");
			monto = monto.replaceAll(" ", "");
		}

		e13b += monto + "=";
		return e13b;
	}

	// funcion que hace que el monto sea un string de dos decimales y entendible
	// para el usuario
	public static String convertirMontoDosDecimales(double monto) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);

		String montO = nf.format(monto).replace(".", "");
		montO = montO.replace(",", "");
		montO = montO.substring(0, montO.length() - 2) + "." + montO.substring(montO.length() - 2, montO.length());

		return montO;

	}

	public static String doubleToString(double monto) {
		monto = redondearMonto(monto, 2);
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(',');
		DecimalFormat formateador = new DecimalFormat("0.00", otherSymbols);
		String retorno = formateador.format(monto);
		retorno = retorno.replace(",", "");
		return retorno;
	}

	public static String construirReferenciaUnica(ChequesDigitalizados cheque, int tipoCamara, String fechaCamara) {
		String referenciaUnica = "";
		referenciaUnica += fillZerosLeft("" + cheque.getEntidadProcesadora(), 4);
		referenciaUnica += fechaCamara;
		referenciaUnica += fillZerosLeft("" + tipoCamara, 4);

		switch (tipoCamara) {
		case AplicacionConstantes.CAMARA_ENTRANTE:
		case AplicacionConstantes.CAMARA_INTERNA:
			referenciaUnica += (tipoCamara == AplicacionConstantes.CAMARA_ENTRANTE) ? fillZerosLeft("" + cheque.getCheque().getCodigoOficina(), 4) : fillZerosLeft("" + cheque.getCodigoOficinaProcesadora(), 4);
			referenciaUnica += fillZerosLeft("" + cheque.getIdLote(), 4);
			referenciaUnica += fillZerosLeft("" + cheque.getPosicionLote(), 4);
			referenciaUnica += fillZerosLeft("" + cheque.getCodigoBancoEmisor(), 4);
			break;
		case AplicacionConstantes.CAMARA_SALIENTE:
		case AplicacionConstantes.CAMARA_EXTERNA:
			referenciaUnica += fillZerosLeft("" + cheque.getCodigoOficinaProcesadora(), 4);
			referenciaUnica += fillZerosLeft("" + cheque.getIdLote(), 4);
			referenciaUnica += fillZerosLeft("" + cheque.getPosicionLote(), 4);
			referenciaUnica += fillZerosLeft("" + cheque.getCodigoBancoDestino(), 4);
			break;
		default:
			break;
		}

		return referenciaUnica;
	}

	public static String obtenerRutaImagen(ChequesDigitalizados cheque) {
		String ruta = "";
		String datos = cheque.getReferenciaUnica();
		ruta += datos.substring(0, 4) + "/"; // Entidad procesadora
		ruta += datos.substring(8, 4 + 2 + 2 + 4) + "/";// AAAA
		ruta += datos.substring(6, 4 + 2 + 2) + "/"; // MM
		ruta += datos.substring(4, 4 + 2) + "/"; // DD
		ruta += datos.substring(12, 4 + 2 + 2 + 4 + 4) + "/";// TipoCamara
		ruta += datos.substring(20, 4 + 2 + 2 + 4 + 4 + 4 + 4) + "/";// Lote
		ruta += datos.substring(16, 4 + 2 + 2 + 4 + 4 + 4) + "/";// Oficina
		// ruta+=datos.substring(24,4+2+2+4+4+4)+ "/";//Entidad Origen o destino
		// de acuerdo a la camara
		return ruta;
	}

	public static String obtenerRutaImagen(String referenciaUnica) {
		String ruta = "";
		String datos = referenciaUnica;
		ruta += datos.substring(0, 4) + "/"; // Entidad procesadora
		ruta += datos.substring(8, 4 + 2 + 2 + 4) + "/";// AAAA
		ruta += datos.substring(6, 4 + 2 + 2) + "/"; // MM
		ruta += datos.substring(4, 4 + 2) + "/"; // DD
		ruta += datos.substring(12, 4 + 2 + 2 + 4 + 4) + "/";// TipoCamara
		ruta += datos.substring(20, 4 + 2 + 2 + 4 + 4 + 4 + 4) + "/";// Lote
		ruta += datos.substring(16, 4 + 2 + 2 + 4 + 4 + 4) + "/";// Oficina
		// ruta+=datos.substring(24,4+2+2+4+4+4)+ "/";//Entidad Origen o destino
		// de acuerdo a la camara
		return ruta;
	}

	public static String getCurrentDate() {
		Calendar calendario = Calendar.getInstance();
		return fillZerosLeft("" + calendario.get(Calendar.DAY_OF_MONTH), 2) + fillZerosLeft("" + calendario.get(Calendar.MONTH), 2) + fillZerosLeft("" + calendario.get(Calendar.YEAR), 4);
	}

	public static String formatDateDDMMYYYY(long l) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy H:MM:s");
		String dateString = formatter.format(l);
		return dateString;
	}

	/*
	 * @fecha parametro a formatear @format formato a obtener
	 */
	public static String FormatDateToStringToFormat(Object fecha, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(fecha);
		return dateString;
	}

	public static String getTipoCamara(int tipoCamara) {
		switch (tipoCamara) {
		case AplicacionConstantes.CAMARA_ENTRANTE:
			return "Entrante";
		case AplicacionConstantes.CAMARA_EXTERNA:
			return "Externa";
		case AplicacionConstantes.CAMARA_INTERNA:
			return "Interna";
		case AplicacionConstantes.CAMARA_SALIENTE:
			return "Saliente";
		default:
			return "";
		}

	}

	public static Calendar obtenerFechaProcesoCamaraSaliente() {
		Calendar fecha = Calendar.getInstance();
		int hora = fecha.get(Calendar.HOUR_OF_DAY), horaTope = 7;
		try {
			horaTope = ApplicationParameters.getInstance().getInt("hora.tope.saliente");
		} catch (Exception e) {
			e.printStackTrace();
			horaTope = 7;
		}

		if (hora < horaTope) {
			fecha.set(Calendar.DAY_OF_YEAR, fecha.get(Calendar.DAY_OF_YEAR) - 1);

		}
		return fecha;
	}

	public static String getStringFromDate(String patron) {
		SimpleDateFormat sdf = new SimpleDateFormat(patron);
		return sdf.format(Calendar.getInstance().getTime());
	}
}
