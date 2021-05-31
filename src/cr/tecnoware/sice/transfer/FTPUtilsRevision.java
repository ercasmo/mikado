package cr.tecnoware.sice.transfer;

import org.apache.log4j.Logger;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileTransferClient;

import cr.tecnoware.sice.context.ApplicationParameters;
import cr.tecnoware.sice.utils.encryption.CryptoUtils;
import cr.tecnoware.sice.utils.encryption.CryptoUtilsConstantes;


public class FTPUtilsRevision {

	private static final ApplicationParameters app = ApplicationParameters.getInstance();
	private static final Logger log = Logger.getLogger("application");
	private static FileTransferClient cliente = new FileTransferClient();

	public FTPUtilsRevision() {
		try {

			if (!cliente.isConnected()) {
				cliente.setRemoteHost(app.getString("ftp.host"));
				cliente.setRemotePort(app.getInt("ftp.port"));
				cliente.setUserName(app.getString("ftp.user"));
				cliente.setPassword(app.getString("ftp.password"));
				cliente.connect();
				cliente.setContentType(FTPTransferType.BINARY);
			}
		} catch (Exception e) {
			cerrarConexion();
			log.error("Error al momento de conexion ", e);
		}
	}

	public static void cerrarConexion() {
		try {
			if (cliente.isConnected()) {
				cliente.cancelAllTransfers();
				cliente.disconnect(true);
			}
		} catch (Exception e) {
			log.error("Error al desconectar el FTP", e);
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
	public byte[] traerImagen(String source) {
		try {
			byte[] arregloOriginal = cliente.downloadByteArray(source);
			byte[] arregloDecodificado = CryptoUtils.decodificar(arregloOriginal, CryptoUtilsConstantes.METODO_AES);
			return arregloDecodificado;
		} catch (FTPException f) {
			if (f.getMessage().equals("File not found")) {
				log.error("Imagen no Encontrada " + source, f);
				cerrarConexion();
				return null;
			} else {
				log.error("Error en conexion FTP " + source, f);
				cerrarConexion();
				return null;
			}
		} catch (Exception e) {
			log.error("Error al traer imagen " + source);
			cerrarConexion();
			return null;
		}
	}
}
