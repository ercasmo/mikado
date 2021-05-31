package cr.tecnoware.sice.gestor_archivos.protocolos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.enterprisedt.net.ftp.FileTransferInputStream;
import com.enterprisedt.net.ftp.FileTransferOutputStream;

import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.gestor_archivos.interfaces.GestorArchivos;

public class GestorFTP implements GestorArchivos {
	private FileTransferClient cliente;
	private static Logger log = Logger.getLogger("application");


	public GestorFTP() {
		this.cliente = new FileTransferClient();
	}

	public boolean conectar(ConexionRemotaBean conexion) {
		if (this.cliente != null) {
			this.desconectar(true);
			try {
				this.cliente.setRemoteHost(conexion.getDireccionIp());
				this.cliente.setRemotePort(conexion.getPuerto());
				this.cliente.setPassword(conexion.getClave());
				this.cliente.setUserName(conexion.getUsuario());
				try {
					this.cliente.connect();
					return this.cliente.isConnected();
				} catch (IOException ex) {
					log.error(" IO Conectando al FTP "+ex);
					return false;
				}
			} catch (FTPException ex) {
				log.error(" FTP Conectando al FTP "+ex);
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean desconectar(boolean inmediato) {
		if (this.cliente != null) {
			if (this.cliente.isConnected()) {
				try {
					this.cliente.disconnect(true);
					return !this.cliente.isConnected();
				} catch (FTPException ex) {
					log.error(" FTP  Desconectando del FTP "+ex);
					return false;
				} catch (IOException ex) {
					log.error(" IO  Desconectando del FTP "+ex);
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public byte[] traerImagen(String rutaCompleta) {
		byte[] contenidoArchivo = null;
		if (this.hayConexionActiva()) {
			try {
				rutaCompleta = this.verificarRutaArchivo(rutaCompleta);
				this.cliente.setContentType(FTPTransferType.BINARY);
				contenidoArchivo = this.cliente.downloadByteArray(rutaCompleta);
			} catch (IOException ex) {
				log.error("IO traerImagen FTP");
			} catch (FTPException ex) {
				log.error("FTP traerImagen FTP");
			}
		}
		return contenidoArchivo;
	}

	public int subirImagen(String ruta, String nombreArchivo, byte[] contenidoArchivo) {
		FileTransferOutputStream outputStream;
		if (this.hayConexionActiva()) {
			ruta = this.verificarRutaArchivo(ruta);
			try {
				this.cliente.setContentType(FTPTransferType.BINARY);
				try {
					ruta = this.verificarRutaArchivo(ruta);
					this.cliente.createDirectory(ruta);
				} catch (Exception e) {
					//.error("setContentType FTP "+e);
				}

				outputStream = this.cliente.uploadStream(ruta + nombreArchivo);
				outputStream.write(contenidoArchivo);
				outputStream.close();
				return 1;
			} catch (IOException ex) {
				log.error("subir Imagen FTP IO "+ex);
				return 0;
			} catch (FTPException ex) {
				log.error("subir Imagen FTP  "+ex);
				return 0;
			}
		}
		return 0;
	}

	private String verificarRutaArchivo(String rutaActual) {
		if (rutaActual.indexOf("/") == 0) {
			return rutaActual;
		} else {
			return "/" + rutaActual;
		}
	}

	public synchronized boolean existeArchivo(String rutaNombreArchivo) {
		if (this.hayConexionActiva()) {
			try {
				rutaNombreArchivo = this.verificarRutaArchivo(rutaNombreArchivo, "Izquierdo");
				return this.cliente.exists(rutaNombreArchivo);
			} catch (FTPException ex) {
				log.error("existeArchivo FTP "+ex);
				return false;
			} catch (IOException ex) {
				log.error("existeArchivo IO "+ex);
				return false;
			}
		} else {
			return false;
		}
	}

	public synchronized String[] listarArchivosDirectorio(String ruta) {
		String[] listaArchivos = null;
		long inicio, fin;
		inicio = System.currentTimeMillis();
		if (this.hayConexionActiva()) {
			try {
				ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
				log.info("Ruta Archivos: " + ruta);
				listaArchivos = this.cliente.directoryNameList(ruta, true);
				fin = System.currentTimeMillis();
				log.info("Lista Archivos: " + listaArchivos.length + " Tiempo (seg): " + ((double) (fin - inicio) / 1000));
			} catch (Exception e2) {
				log.error("Error listando archivos " + e2);
			}
		} else {
			// System.out.println("El directorio no pudo ser listado.");
		}
		return listaArchivos;
	}

	public synchronized byte[] obtenerBytesArchivo(String ruta) {
		long inicio, fin;
		inicio = System.currentTimeMillis();
		byte[] contenidoArchivo = null;
		if (this.hayConexionActiva()) {
			try {
				ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
				contenidoArchivo = this.cliente.downloadByteArray(ruta);
				fin = System.currentTimeMillis();
			} catch (FTPException ex) {
				log.error("obtenerBytesArchivo FTP " + ex);
			} catch (IOException ex) {
				log.error("obtenerBytesArchivo IO " + ex);
			}

		} else {
			// System.out.println("No se pudo obtener el contenido del archivo.");
		}
		return contenidoArchivo;
	}

	public synchronized byte[] obtenerBytesArchivoBinario(String ruta) {
		long inicio, fin;
		inicio = System.currentTimeMillis();
		byte[] contenidoArchivo = null;
		if (this.hayConexionActiva()) {
			try {
				ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
				this.cliente.setContentType(FTPTransferType.BINARY);
				contenidoArchivo = this.cliente.downloadByteArray(ruta);
				fin = System.currentTimeMillis();
				// System.out.println("Tiempo obteniendo bytes(seg): " +
				// ((double)(fin - inicio) / 1000));
				// System.out.println("Tamaño archivo (bytes): " +
				// contenidoArchivo.length);
			} catch (IOException ex) {
				log.error("obtenerBytesArchivoBinario  " + ex);
			} catch (FTPException ex) {
				log.error("obtenerBytesArchivoBinario  " + ex);
			}
		} else {
			// System.out.println("No se pudo obtener el contenido del archivo.");
		}
		return contenidoArchivo;
	}

	public synchronized byte[] obtenerBytesArchivo(String ruta, int numBytes) {
		long inicio, fin;
		byte[] contenidoArchivo = null;
		inicio = System.currentTimeMillis();
		if (this.hayConexionActiva()) {
			try {
				ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
				FileTransferInputStream stream = this.cliente.downloadStream(ruta);
				contenidoArchivo = new byte[numBytes];
				stream.read(contenidoArchivo);
				fin = System.currentTimeMillis();
				// System.out.println("Tiempo obteniendo bytes(seg): " +
				// ((double)(fin - inicio) / 1000));
				// System.out.println("Tamaño archivo (bytes): " +
				// contenidoArchivo.length);
			} catch (FTPException ex) {
				log.error("obtenerBytesArchivo  " + ex);
			} catch (IOException ex) {
				log.error("obtenerBytesArchivo  " + ex);
			}
		} else {
			// System.out.println("No se pudo obtener el contenido del archivo.");
		}
		return contenidoArchivo;
	}

	public synchronized boolean subirArchivo(String ruta, String nombreArchivo, byte[] contenidoArchivo, int tipoContenido) {
		boolean exito = false;
		FileTransferOutputStream archivoOutputStream;
		if (this.hayConexionActiva()) {
			if (tipoContenido == GestorArchivos.CONTENIDO_BINARIO) {
				try {
					this.cliente.setContentType(FTPTransferType.BINARY);
					exito = true;
				} catch (IOException ex) {
					log.error("subirArchivo  " + ex);		
					return false;
				} catch (FTPException ex) {
					log.error("subirArchivo  " + ex);		
					return false;
				}
			}
			if (exito) {
				try {
					ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
					ruta = this.verificarRutaArchivo(ruta, "Derecho");
					archivoOutputStream = this.cliente.uploadStream(ruta + nombreArchivo);
					archivoOutputStream.write(contenidoArchivo);
					archivoOutputStream.close();
				} catch (FTPException ex) {
					log.error("subirArchivo  " + ex);		
					return false;
				} catch (IOException ex) {
					log.error("subirArchivo  " + ex);		
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	public synchronized boolean subirArchivo(String ruta, String nombreArchivo, byte[] archivo) {
		if (this.hayConexionActiva()) {
			try {
				ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
				ruta = this.verificarRutaArchivo(ruta, "Derecho");
				FileTransferOutputStream archivoLotesOutputStream = this.cliente.uploadStream(ruta + nombreArchivo);
				archivoLotesOutputStream.write(archivo);
				archivoLotesOutputStream.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean renombrarArchivo(String ruta, String nombreOriginal, String nuevoNombre) {
		if (this.hayConexionActiva()) {
			try {
				ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
				ruta = this.verificarRutaArchivo(ruta, "Derecho");
				this.cliente.rename(ruta + nombreOriginal, ruta + nuevoNombre);
				return true;
			} catch (FTPException ex) {
				log.error("renombrar Archivo  " + ex);		
				return false;
			} catch (IOException ex) {
				log.error("renombrarArchivo  " + ex);		
				return false;
			}
		} else {
			return false;
		}
	}

	public void crearDirectorio(String ruta) {
		if (this.hayConexionActiva()) {
			ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
			try {
				ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
				this.cliente.createDirectory(ruta);
			} catch (Exception e) {
				log.error("crearDirectorio  " + e);		
			}
		}
	}

	public boolean hayConexionActiva() {
		return this.cliente != null && this.cliente.isConnected();
	}

	public synchronized List[] bajarArchivosPorLote(List listadoRutas) {
		int i;
		List[] contenidoArchivo;
		contenidoArchivo = new List[2];
		contenidoArchivo[0] = new ArrayList();
		contenidoArchivo[1] = new ArrayList();
		if (this.hayConexionActiva()) {
			for (i = 0; i < listadoRutas.size(); i++) {
				if (this.existeArchivo(listadoRutas.get(i).toString())) {
					try {
						this.cliente.setContentType(FTPTransferType.BINARY);
						contenidoArchivo[0].add(listadoRutas.get(i).toString());
						contenidoArchivo[1].add(this.cliente.downloadByteArray(this.verificarRutaArchivo(listadoRutas.get(i).toString(), "Izquierdo")));
					} catch (IOException ex) {
						log.error("bajarARchivo por Lote  " + ex);		
					} catch (FTPException ex) {
						log.error("bajarArchivo por Lote  " + ex);		
					}
				}
			}
		}
		return contenidoArchivo;
	}

	private String verificarRutaArchivo(String rutaActual, String extremo) {
		if (extremo.equals("Derecho")) {
			if (rutaActual.lastIndexOf("/") == rutaActual.length() - 1) {
				return rutaActual;
			} else {
				return rutaActual + "/";
			}
		} else if (extremo.equals("Izquierdo")) {
			if (rutaActual.indexOf("/") == 0) {
				return rutaActual;
			} else {
				return "/" + rutaActual;
			}
		}
		return rutaActual;
	}

	public boolean borrarArchivos(String ruta, String[] listaArchivos)// lista
																		// de
																		// archivos
																		// a
																		// eliminar
																		// debe
																		// ser
																		// un []
																		// de
																		// string
	{
		int i;
		boolean exito = false;
		// String[] listaArchivos = null;
		if (this.hayConexionActiva()) {
			ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
			ruta = this.verificarRutaArchivo(ruta, "Derecho");
			// listaArchivos = this.listarArchivosDirectorio(ruta);
			if (listaArchivos != null) {
				for (i = 0; i < listaArchivos.length; i++) {
					try {
						this.cliente.deleteFile(ruta + listaArchivos[i]);
						exito = true;
					} catch (FTPException ex) {
						log.error("borrarArchivos  " + ex);		
						exito = false;
					} catch (IOException ex) {
						log.error("borrarArchivos  " + ex);		
						exito = false;
					}
				}
			}
		}
		return exito;
	}

	public boolean borrarArchivo(String ruta, String[] archivo) {
		int i;
		boolean exito = false;
		if (this.hayConexionActiva()) {
			ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
			ruta = this.verificarRutaArchivo(ruta, "Derecho");
			if (archivo != null) {
				try {
					this.cliente.deleteFile(ruta + archivo);
					exito = true;
				} catch (FTPException ex) {
					log.error("borrarArchivo  " + ex);		
					exito = false;
				} catch (IOException ex) {
					log.error("borrarArchivos  " + ex);		
					exito = false;
				}
			}
		}
		return exito;
	}
}
