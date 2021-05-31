package cr.tecnoware.sice.transfer;

import org.apache.log4j.Logger;

import com.enterprisedt.net.ftp.FileTransferClient;
import com.enterprisedt.net.ftp.FileTransferOutputStream;

import cr.tecnoware.sice.utils.CacheUtils;
import cr.tecnoware.sice.utils.encryption.CryptoUtils;
import cr.tecnoware.sice.utils.encryption.CryptoUtilsConstantes;

public class FTPUtils {

	private static final Logger log = Logger.getLogger("application");

	public FTPUtils() {
	}

	public static FileTransferClient getConexion(String _codigoEntidadProcesadora, int tipoConexion) {
		
		return CacheUtils.getConexionFTP(_codigoEntidadProcesadora, tipoConexion);
		
	}

	public static void conectar(FileTransferClient clienteFTP) {
		try {
			if (null != clienteFTP && !clienteFTP.isConnected()) {
				clienteFTP.connect();
			}
		} catch (Exception e) {
			log.error("Error estableciendo conexion FTP", e);
		}
	}

	public static void desconectar(FileTransferClient clienteFTP) {
		try {
			if (null != clienteFTP && clienteFTP.isConnected()) {
				clienteFTP.disconnect();
			}
		} catch (Exception e) {
			log.error("Error desconectando FTP", e);
		}
	}

	/**
	 * 
	 * Método que realiza las operaciones de traer, convertir a arreglos de
	 * Byte, desencriptar y retornar al applet la imagen
	 * 
	 * @param source
	 *            Ruta donde se aloja la imagen encriptada en el servidor FTP
	 * @return arreglo de bytes de la imagen desencriptada para ser enviada al
	 *         applet
	 */
	public static byte[] traerImagen(String source, FileTransferClient clienteFTP) {
		try {
			conectar(clienteFTP);
			byte[] arregloOriginal = clienteFTP.downloadByteArray(source);
			byte[] arregloDecodificado = CryptoUtils.decodificar(arregloOriginal, CryptoUtilsConstantes.METODO_AES);
			return arregloDecodificado;
		} catch (Exception e) {
			log.error("Error obteniendo imagen via FTP", e);
			return null;
		}
	}

	public static int subirImagen(String directorio, String fileName, byte[] image, FileTransferClient clienteFTP) {
		try {
			clienteFTP.createDirectory(directorio);
		} catch (Exception e) {
			log.info("Directorio ya existe");
		}

		try {
			System.out.println(image.length);
			byte[] temp = CryptoUtils.codificar(image, CryptoUtilsConstantes.METODO_AES);
			System.out.println(temp.length);
			FileTransferOutputStream output = clienteFTP.uploadStream(directorio + fileName);
			System.out.println("Salida "+output);
			output.write(temp);
			output.close();
			return 1;
		} catch (Exception e) {
			log.error("No se logro subir Imagen ",e);
			return 0;
		}
	}

}
