package cr.tecnoware.sice.gestor_archivos.interfaces;

import java.util.List;

import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;

public interface GestorArchivos {
	public static final int CONTENIDO_ASCII = 0;
	public static final int CONTENIDO_BINARIO = 1;
	public static final int GESTOR_FTP = 0;
	public static final int GESTOR_SFTP = 1;

	public abstract boolean conectar(ConexionRemotaBean conexion);

	public abstract boolean desconectar(boolean inmediato);

	public abstract byte[] traerImagen(String rutaCompleta);

	public abstract int subirImagen(String ruta, String nombreArchivo, byte[] contenidoArchivo);

	public abstract boolean existeArchivo(String rutaNombreArchivo);

	public String[] listarArchivosDirectorio(String ruta);

	public byte[] obtenerBytesArchivo(String ruta);

	public byte[] obtenerBytesArchivoBinario(String ruta);

	public byte[] obtenerBytesArchivo(String ruta, int numBytes);

	public boolean subirArchivo(String ruta, String nombreArchivo, byte[] contenidoArchivo, int tipoContenido);

	public boolean subirArchivo(String ruta, String nombreArchivo, byte[] archivo);

	public boolean renombrarArchivo(String ruta, String nombreOriginal, String nuevoNombre);

	public void crearDirectorio(String ruta);

	public boolean hayConexionActiva();

	public List[] bajarArchivosPorLote(List listadoRutas);

	public boolean borrarArchivos(String ruta, String[] listaArchivos); // lista
																		// de
																		// archivos
																		// a
																		// eliminar
																		// debe
																		// ser
																		// un []
																		// de
																		// string

	public boolean borrarArchivo(String ruta, String[] archivo);
}
