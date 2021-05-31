package cr.tecnoware.sice.core.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;

import cr.tecnoware.sice.DAO.Lecto02DAO;
import cr.tecnoware.sice.DAO.TransaccionEntranteDAO;
import cr.tecnoware.sice.DAO.TransaccionInternaDAO;
import cr.tecnoware.sice.applets.bean.Firma;
import cr.tecnoware.sice.encode.DataBaseEncoder;
import cr.tecnoware.sice.services.SignatureService;
import cr.tecnoware.sice.utils.Utils;
import cr.tecnoware.sice.utils.encryption.CryptoUtils;
import cr.tecnoware.sice.utils.encryption.CryptoUtilsConstantes;

public class Test extends Thread {

	public final static String[] oficinas = { "500", "501", "502", "139", "590" };
	public final static String[] digitos = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	public final static String[] entidades = { "0101", "0501", "0601", "0701", "0801", "0901", "1001" };
	// 10% pueden ser tipo 4 el resto tipo 3
	public final static String[] TC = { "03", "03", "04", "03", "03", "03", "03", "03", "03", "03" };
	public final static String[] IMAGENES = { "1A.jpg", "1R.jpg", "2A.jpg", "2R.jpg", "3A.jpg", "3R.jpg", "4A.jpg", "4R.jpg", "5A.jpg", "5R.jpg" };
	public final static String inicioCuenta = "00000";

	public static int TAMANNO_ULTIMO_ARCHIVO_GENERADO = 0;
	public static int LIMITE_ARCHIVO_SIMULACION = 100;
	public int i;

	public static void obtenerPropiedadesCifradas() {
		System.out.println("database.user=" + DataBaseEncoder.encode("bacosi"));
		System.out.println("database.password=" + DataBaseEncoder.encode("bacosipwd"));
		System.out.println("database.name=" + DataBaseEncoder.encode("BacosiElectronico"));
		System.out.println("database.host=" + DataBaseEncoder.encode("192.168.0.1"));
		System.out.println("database.port=" + DataBaseEncoder.encode("1433"));
	}

	public static String analizarSentencia(String cadena) {
		String sentencia = "";
		char listaCaracteres[] = cadena.toCharArray();

		for (int i = 0; i < listaCaracteres.length; i++) {
			if (((int) listaCaracteres[i] >= 65) && ((int) listaCaracteres[i] <= 90)) {
				sentencia += "_" + listaCaracteres[i];
			} else {
				sentencia += listaCaracteres[i];
			}
		}
		return sentencia.toLowerCase();
	}

	public static void generarSelectProcedimientoAlmacenado() throws Exception {
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		int contadorLineas = 0;
		try {
			archivo = new File("C:\\Users\\PERSONAL\\Desktop\\archivo.txt");
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			ArrayList<String> listaValores = new ArrayList<String>();
			// Lectura del fichero
			String linea;
			String salida = "";
			while ((linea = br.readLine()) != null) {
				linea = linea.replace(";", "");
				linea = linea.replace(" ", "");
				linea = linea.replace("Integer", "");
				linea = linea.replace("Date", "");
				linea = linea.replace("String", "");
				linea = linea.replace("Double", "");
				linea = linea.replace("\t", "");
				linea = linea.replace("FirmaDigital", "");
				linea = linea.replace("ProcesoRespaldo", "");
				listaValores.add(analizarSentencia(linea));
			}

			Collections.sort(listaValores);
			for (String cadena : listaValores) {
				System.out.println(cadena);
				salida = salida + cadena + ",";
			}
			System.out.println(salida);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// En el finally cerramos el fichero, para asegurarnos
			// que se cierra tanto si todo va bien como si salta
			// una excepcion.
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	public static StringBuffer generarLoteInterno(boolean simularBoleta) {
		int limite = (int) (1 + (Math.random() * LIMITE_ARCHIVO_SIMULACION));
		TAMANNO_ULTIMO_ARCHIVO_GENERADO = limite;
		StringBuffer resultadoSimulacion = new StringBuffer();
		int indiceBanco = 0;
		String cadena = "";
		if (simularBoleta) {
			cadena += "!!>!!!!>!!!!!!!!!!!!!!!:!!!!!!!:";
			resultadoSimulacion.append(cadena);
			resultadoSimulacion.append("\n");
		}
		for (int i = 0; i < limite; i++) {
			int indiceOficina = (int) (0 + (Math.random() * oficinas.length));
			int indiceTC = (int) (0 + (Math.random() * TC.length));
			// E13B = TC+BANCO+CUENTA+SERIAL+MONTO
			// 0003>0101>000005900563495:0018636:=529625=
			cadena = TC[indiceTC];
			cadena += ">";
			cadena += entidades[indiceBanco];
			cadena += ">";
			cadena += inicioCuenta;
			cadena += oficinas[indiceOficina];
			for (int j = 0; j < 7; j++) {
				cadena += digitos[(int) (0 + (Math.random() * digitos.length))];
			}
			cadena += ":";
			for (int j = 0; j < 7; j++) {
				cadena += digitos[(int) (0 + (Math.random() * digitos.length))];
			}
			cadena += ":=";

			resultadoSimulacion.append(cadena);
			resultadoSimulacion.append("\n");
		}
		return resultadoSimulacion;
	}

	public static StringBuffer generarLoteEntrante(boolean simularBoleta) {
		int limite = (int) (1 + (Math.random() * LIMITE_ARCHIVO_SIMULACION));
		TAMANNO_ULTIMO_ARCHIVO_GENERADO = limite;
		StringBuffer resultadoSimulacion = new StringBuffer();
		int indiceBanco = 0;
		String cadena = "@";
		String inicioMonto = "0000";
		if (simularBoleta) {
			cadena += "!!>!!!!>!!!!!!!!!!!!!!!:!!!!!!!:";
			resultadoSimulacion.append(cadena);
			resultadoSimulacion.append("\n");

		}
		for (int i = 0; i < limite; i++) {
			int indiceOficina = (int) (0 + (Math.random() * oficinas.length));
			int indiceTC = (int) (0 + (Math.random() * TC.length));
			// E13B = TC+BANCO+CUENTA+SERIAL+MONTO
			// 0003>0101>000005900563495:0018636:=529625=
			cadena = "@";
			cadena += TC[indiceTC];
			cadena += ">";
			cadena += entidades[indiceBanco];
			cadena += ">";
			cadena += inicioCuenta;
			cadena += oficinas[indiceOficina];
			for (int j = 0; j < 7; j++) {
				cadena += digitos[(int) (0 + (Math.random() * digitos.length))];
			}
			cadena += ":";
			for (int j = 0; j < 7; j++) {
				cadena += digitos[(int) (0 + (Math.random() * digitos.length))];
			}
			cadena += ":=";
			cadena += inicioMonto;
			for (int j = 0; j < 7; j++) {
				cadena += digitos[(int) (0 + (Math.random() * digitos.length))];
			}
			cadena += "=";

			resultadoSimulacion.append(cadena);
			resultadoSimulacion.append("\n");
		}
		return resultadoSimulacion;
	}

	public static StringBuffer generarLoteSaliente(boolean simularBoleta) {
		int limite = (int) (1 + (Math.random() * LIMITE_ARCHIVO_SIMULACION));
		TAMANNO_ULTIMO_ARCHIVO_GENERADO = limite;
		StringBuffer resultadoSimulacion = new StringBuffer();
		int indiceBanco = 0;
		String cadena = "";
		String inicioMonto = "0000";
		if (simularBoleta) {
			cadena += "!!>!!!!>!!!!!!!!!!!!!!!:!!!!!!!:";
			resultadoSimulacion.append(cadena);
			resultadoSimulacion.append("\n");

		}
		limite = 50;
		for (int i = 0; i < limite; i++) {
			int indiceOficina = (int) (0 + (Math.random() * oficinas.length));
			int indiceTC = (int) (0 + (Math.random() * TC.length));
			indiceBanco = (int) (0 + (Math.random() * entidades.length));
			if (indiceBanco == 0) {
				indiceBanco++;
			}
			// E13B = TC+BANCO+CUENTA+SERIAL+MONTO
			// 0003>0101>000005900563495:0018636:=529625=
			cadena = TC[indiceTC];
			cadena += ">";
			cadena += entidades[indiceBanco];
			cadena += ">";
			cadena += inicioCuenta;
			cadena += oficinas[indiceOficina];
			for (int j = 0; j < 7; j++) {
				cadena += digitos[(int) (0 + (Math.random() * digitos.length))];
			}
			cadena += ":";
			for (int j = 0; j < 7; j++) {
				cadena += digitos[(int) (0 + (Math.random() * digitos.length))];
			}
			cadena += ":=";
			cadena += "1000000";
			resultadoSimulacion.append(cadena);
			resultadoSimulacion.append("\n");
		}
		return resultadoSimulacion;
	}

	public static void construirArchivo(StringBuffer contenido, String rutaArchivo) throws Exception {
		File archivo = new File(rutaArchivo);
		FileOutputStream archivoSalida = new FileOutputStream(archivo);
		archivoSalida.write(contenido.toString().getBytes());
		archivoSalida.close();
	}

	public static void simularImages(String pathOrigen, String pathDestino, int cantidad) throws Exception {
		LinkedHashMap<Integer, byte[]> mapaImagenes = new LinkedHashMap<Integer, byte[]>();
		int maxImg = (cantidad > 0) ? cantidad : TAMANNO_ULTIMO_ARCHIVO_GENERADO;
		for (int i = 0; i < maxImg; i++) {
			int indiceImagenAnverso = (int) (0 + (Math.random() * IMAGENES.length));
			int indiceImagenReverso = (int) (0 + (Math.random() * IMAGENES.length));
			System.out.println("Construyendo IMG: " + i);
			if (mapaImagenes.containsKey(indiceImagenAnverso)) {
				File archivoSalida = new File(pathDestino + "\\FS" + Utils.fillZerosLeft("" + i, 7) + ".jpg");
				FileOutputStream salida = new FileOutputStream(archivoSalida);
				salida.write(mapaImagenes.get(indiceImagenAnverso));
				salida.close();
			} else {
				File archivoEntrada = new File(pathOrigen + "\\" + IMAGENES[indiceImagenAnverso]);
				byte[] contenidoArchivo = getBytesFromFile(archivoEntrada);
				mapaImagenes.put(indiceImagenAnverso, contenidoArchivo);
				File archivoSalida = new File(pathDestino + "\\FS" + Utils.fillZerosLeft("" + i, 7) + ".jpg");
				FileOutputStream salida = new FileOutputStream(archivoSalida);
				salida.write(mapaImagenes.get(indiceImagenAnverso));
				salida.close();
			}

			if (mapaImagenes.containsKey(indiceImagenReverso)) {
				File archivoSalida = new File(pathDestino + "\\RS" + Utils.fillZerosLeft("" + i, 7) + ".jpg");
				FileOutputStream salida = new FileOutputStream(archivoSalida);
				salida.write(mapaImagenes.get(indiceImagenReverso));
				salida.close();
			} else {
				File archivoEntrada = new File(pathOrigen + "\\" + IMAGENES[indiceImagenReverso]);
				byte[] contenidoArchivo = getBytesFromFile(archivoEntrada);
				mapaImagenes.put(indiceImagenReverso, contenidoArchivo);
				File archivoSalida = new File(pathDestino + "\\FS" + Utils.fillZerosLeft("" + i, 7) + ".jpg");
				FileOutputStream salida = new FileOutputStream(archivoSalida);
				salida.write(mapaImagenes.get(indiceImagenReverso));
				salida.close();
			}

		}

	}

	private static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	public static void descifrar() throws Exception {
		File archivoEntrada = new File("C:\\Users\\PERSONAL\\Desktop\\1\\1-47.TCA");
		File archivoSalida = new File("C:\\Users\\PERSONAL\\Desktop\\1\\1-47A.JPG");
		byte archivoEntradaBytes[] = getBytesFromFile(archivoEntrada);
		archivoEntradaBytes = CryptoUtils.decodificar(archivoEntradaBytes, CryptoUtilsConstantes.METODO_AES);
		FileOutputStream salida = new FileOutputStream(archivoSalida);
		salida.write(archivoEntradaBytes);
		salida.close();
	}

	public static Timestamp StringToTimestamp(String fechaStr) {
		Timestamp fecha = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			Date date = sdf.parse(fechaStr);
			fecha = new Timestamp(date.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return fecha;
	}

	/*
	 * @fecha parametro a formatear @format formato a obtener
	 */
	public static String FormatDateToStringToFormat(Object fecha, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(fecha);
		return dateString;
	}

	public static void codificarImagen() throws Exception {
		String rutaInicio = "C:\\Users\\PERSONAL\\Desktop\\";
		String rutaImagenAnverso = rutaInicio + "Anverso.jpg";
		String rutaImagenReverso = rutaInicio + "Reverso.jpg";
		File anverso = new File(rutaImagenAnverso);
		File reverso = new File(rutaImagenReverso);
		File anversoCodificado = new File(rutaInicio + "Anverso.TCA");
		File reversoCodificado = new File(rutaInicio + "Reverso.TCR");
		byte[] anversoByte = getBytesFromFile(anverso);
		byte[] reversoByte = getBytesFromFile(reverso);
		anversoByte = CryptoUtils.codificar(anversoByte, CryptoUtilsConstantes.METODO_AES);
		reversoByte = CryptoUtils.codificar(reversoByte, CryptoUtilsConstantes.METODO_AES);
		FileOutputStream anversoCodificadoStream = new FileOutputStream(anversoCodificado);
		FileOutputStream reversoCodificadoStream = new FileOutputStream(reversoCodificado);
		anversoCodificadoStream.write(anversoByte);
		reversoCodificadoStream.write(reversoByte);
		anversoCodificadoStream.close();
		reversoCodificadoStream.close();
	}

	public static void generarImagenesRepositorio(int camara, String rutaSimulacion) throws Exception {
		File archivoIndice = new File(rutaSimulacion + "\\indice.txt");
		File archivoEntradaAnverso = new File(rutaSimulacion + "\\Anverso.TCA");
		File archivoEntradaReverso = new File(rutaSimulacion + "\\Reverso.TCR");
		byte archivoEntradaAnversoBytes[] = getBytesFromFile(archivoEntradaAnverso);
		byte archivoEntradaReversoBytes[] = getBytesFromFile(archivoEntradaReverso);
		BufferedReader br = new BufferedReader(new FileReader(archivoIndice));
		String linea = "";
		int contadorSimulacion = 0;
		System.out.println("Iniciando Proceso...");
		while ((linea = br.readLine()) != null) {
			System.out.println("Simulacion [" + (++contadorSimulacion) + "] " + linea);
			String rutaImagen = Utils.obtenerRutaImagen(linea);
			rutaImagen.replace("/", "\\");
			File rutaDirectorios = new File(rutaSimulacion + "\\" + rutaImagen);
			rutaDirectorios.mkdirs();
			File resultadoAnverso = new File(rutaSimulacion + "\\" + rutaImagen + "\\" + linea);
			File resultadoReverso = new File(rutaSimulacion + "\\" + rutaImagen + "\\" + linea.replace(".TCA", ".TCR"));

			FileOutputStream salidaAnverso = new FileOutputStream(resultadoAnverso);
			FileOutputStream salidaReverso = new FileOutputStream(resultadoReverso);
			salidaAnverso.write(archivoEntradaAnversoBytes);
			salidaReverso.write(archivoEntradaReversoBytes);
			salidaAnverso.close();
			salidaReverso.close();
		}
		System.out.println("Proceso Finalizado-....");
		br.close();

	}

	public static void registrarArchivoBD(int camara, String rutaSimulacion) throws Exception {
		File archivoIndice = new File(rutaSimulacion + "\\Registros.txt");
		BufferedReader br = new BufferedReader(new FileReader(archivoIndice));
		String linea = "";
		int contadorSimulacion = 1;
		TransaccionInternaDAO tr = new TransaccionInternaDAO();
		while ((linea = br.readLine()) != null) {
			// tr.ejecutarSQL(linea);
			contadorSimulacion++;
		}
		br.close();

	}

	public static void crearRegistroIndice() throws Exception {
		File archivo1 = new File("C:\\Users\\PERSONAL\\Desktop\\__Simulador\\img1.txt");
		File archivoSalida = new File("C:\\Users\\PERSONAL\\Desktop\\__Simulador\\indice.txt");
		BufferedReader br = new BufferedReader(new FileReader(archivo1));
		String linea = "";
		FileOutputStream salida = new FileOutputStream(archivoSalida);
		System.out.println("Iniciando proceso...");
		while ((linea = br.readLine()) != null) {
			linea = linea.replace("insert into cheque_interno_historico (referencia_imagen) values ('", "");
			linea = linea.replace("');", "");
			salida.write(linea.getBytes());
			salida.write("\n".getBytes());
		}
		salida.close();
		br.close();
		System.out.println("Proceso finalizado...");
	}

	public static void probarFirmas() {
		SignatureService service = new SignatureService();
		ArrayList<Firma> consultaFirmas = (ArrayList<Firma>) service.getSignatures("123");
		System.out.println("Cantidad de FIRMAS: " + consultaFirmas.size());
		if (consultaFirmas.size() > 0) {
			System.out.println(consultaFirmas.get(0).getCondicion());
		}

	}

	public static void main(String[] args) {
		new TransaccionEntranteDAO().marcarChequesExcentos(2);
		/*
		Calendar limiteInferior = Calendar.getInstance();
		Calendar limiteSuperior = Calendar.getInstance();
		limiteInferior.setTime(new Date());
		limiteSuperior.setTime(new Date());
		int diferenciaDias = 0;
		if (limiteInferior.get(Calendar.YEAR) == limiteSuperior.get(Calendar.YEAR)) {
			diferenciaDias = limiteSuperior.get(Calendar.DAY_OF_YEAR) - limiteInferior.get(Calendar.DAY_OF_YEAR);
		} else {
			int diasAnoAnterior = 365 - limiteInferior.get(Calendar.DAY_OF_YEAR);
			diferenciaDias = diasAnoAnterior + limiteSuperior.get(Calendar.DAY_OF_YEAR);
		}

		System.out.println(diferenciaDias);*/
	}

	public void run() {
		Lecto02DAO lecto = new Lecto02DAO();
		System.out.println("peticion " + this.i);
		LinkedHashMap<String, Integer> l = lecto.getChequesCertificadosParaBolsillo(0);
		System.out.print(" resultado petivion " + this.i + " size " + l.size());
		System.out.println("");
	}

}
