package cr.tecnoware.sice.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.enterprisedt.net.ftp.FileTransferClient;

import cr.tecnoware.sice.DAO.ArchivadorChequeEntranteDAO;
import cr.tecnoware.sice.DAO.BolsilloDAO;
import cr.tecnoware.sice.DAO.ChequesDigitalizadosDAO;
import cr.tecnoware.sice.DAO.ConexionRemotaDAO;
import cr.tecnoware.sice.DAO.EntidadDAO;
import cr.tecnoware.sice.DAO.Lecto02DAO;
import cr.tecnoware.sice.DAO.Lecto09DAO;
import cr.tecnoware.sice.DAO.LoteChequeDAO;
import cr.tecnoware.sice.DAO.MotivoDevolucionDAO;
import cr.tecnoware.sice.DAO.OficinaDAO;
import cr.tecnoware.sice.DAO.ParametrosGeneralesDAO;
import cr.tecnoware.sice.DAO.TipoDocumentoDAO;
import cr.tecnoware.sice.DAO.TransaccionEntranteDAO;
import cr.tecnoware.sice.DAO.TransaccionInternaDAO;
import cr.tecnoware.sice.DAO.TransaccionSalienteDAO;
import cr.tecnoware.sice.DAO.VerilectDAO;
import cr.tecnoware.sice.applets.bean.ArchivadorChequeEntrante;
import cr.tecnoware.sice.applets.bean.BolsilloClasificacion;
import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.applets.bean.Digitalizado;
import cr.tecnoware.sice.applets.bean.DigitalizadoSaliente;
import cr.tecnoware.sice.applets.bean.Entidad;
import cr.tecnoware.sice.applets.bean.Firma;
import cr.tecnoware.sice.applets.bean.ImageSignature;
import cr.tecnoware.sice.applets.bean.LoteCheque;
import cr.tecnoware.sice.applets.bean.MotivoDevolucion;
import cr.tecnoware.sice.applets.bean.Oficina;
import cr.tecnoware.sice.applets.bean.ParametrosGenerales;
import cr.tecnoware.sice.applets.bean.RegistroMagnetizacion;
import cr.tecnoware.sice.applets.bean.TipoDocumento;
import cr.tecnoware.sice.applets.bean.TransaccionEntrante;
import cr.tecnoware.sice.applets.bean.TransaccionInterna;
import cr.tecnoware.sice.applets.bean.TransaccionSaliente;
import cr.tecnoware.sice.applets.constantes.AplicacionConstantes;
import cr.tecnoware.sice.applets.constantes.ChequeConstantes;
import cr.tecnoware.sice.applets.constantes.ChequeDigitalizadoConstantes;
import cr.tecnoware.sice.applets.constantes.ReporteConstantes;
import cr.tecnoware.sice.cache.CacheServicioVerificarCuenta;
import cr.tecnoware.sice.context.ApplicationParameters;
import cr.tecnoware.sice.gestor_archivos.archivos.GestorArchivosFactory;
import cr.tecnoware.sice.gestor_archivos.interfaces.GestorArchivos;
import cr.tecnoware.sice.interfaz.ProcesarCamaraServices;
import cr.tecnoware.sice.services.AuditoriaTransaccionService;
import cr.tecnoware.sice.services.FactoryDAOService;
import cr.tecnoware.sice.services.ManejadorReportesService;
import cr.tecnoware.sice.services.SignatureService;
import cr.tecnoware.sice.transfer.FTPUtils;
import cr.tecnoware.sice.utils.CacheUtils;
import cr.tecnoware.sice.utils.Utils;
import cr.tecnoware.sice.utils.encryption.CryptoUtils;
import cr.tecnoware.sice.utils.encryption.CryptoUtilsConstantes;

public class RemoteServicesImpl extends UnicastRemoteObject implements RemoteServices {

	public RemoteServicesImpl() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger("application");

	// No cambió - Ojo

	public List<ImageSignature> getGroupImages(String[] bancoReceptor, String[] anho, String[] mes, String[] dia, String[] bancoEmisor, String[] refImagen) {
		List<ImageSignature> lista = new ArrayList<ImageSignature>();
		FileTransferClient clienteFTPRepositorioEntrante = null;
		FileTransferClient clienteFTPRepositorioHistorico = null;
		try {
			FTPUtils.conectar(clienteFTPRepositorioEntrante);
			FTPUtils.conectar(clienteFTPRepositorioHistorico);
			for (int i = 1; i < bancoEmisor.length; i++) {
				try {
					ImageSignature imgFirma = new ImageSignature();
					String source = (new StringBuilder(String.valueOf(Utils.fillZerosLeft(bancoReceptor[i], 4)))).append("/").append(Utils.fillZerosLeft(anho[i], 4)).append("/").append(Utils.fillZerosLeft(mes[i], 2)).append("/").append(Utils.fillZerosLeft(dia[i], 2)).append("/").append(Utils.fillZerosLeft(bancoEmisor[i], 4)).append("/").append(refImagen).toString();
					FTPUtils.conectar(clienteFTPRepositorioEntrante);
					byte imagenAnverso[] = FTPUtils.traerImagen(source, clienteFTPRepositorioEntrante);
					if (imagenAnverso != null) {
						imgFirma.setImagenAnverso(imagenAnverso);
						imgFirma.setImagenReverso(FTPUtils.traerImagen((new StringBuilder(String.valueOf(source.substring(0, source.length() - 1)))).append("R").toString(), clienteFTPRepositorioEntrante));
						imgFirma.setImagenUltraVioleta(FTPUtils.traerImagen((new StringBuilder(String.valueOf(source.substring(0, source.length() - 1)))).append("V").toString(), clienteFTPRepositorioEntrante));
					} else {
						imgFirma.setImagenAnverso(FTPUtils.traerImagen(source, clienteFTPRepositorioHistorico));
						imgFirma.setImagenReverso(FTPUtils.traerImagen((new StringBuilder(String.valueOf(source.substring(0, source.length() - 1)))).append("R").toString(), clienteFTPRepositorioHistorico));
						imgFirma.setImagenUltraVioleta(FTPUtils.traerImagen((new StringBuilder(String.valueOf(source.substring(0, source.length() - 1)))).append("V").toString(), clienteFTPRepositorioHistorico));
					}
					lista.add(imgFirma);
				} catch (Exception e) {
					lista.add(null);
				}
			}
		} catch (Exception e) {
			log.error("Error obteniendo bloque de imagenes", e);
			return null;
		} finally {
			FTPUtils.desconectar(clienteFTPRepositorioEntrante);
			FTPUtils.desconectar(clienteFTPRepositorioHistorico);
		}
		return lista;
	}

	// Si cambió

	public ImageSignature getImage(String bancoReceptor, String anho, String mes, String dia, String bancoEmisor, String refImagen) {
		ImageSignature imgFirma = null;
		GestorArchivos gestor = null;
		try {
			String pathImagenAnverso = (new StringBuilder(String.valueOf(Utils.obtenerRutaImagen(refImagen)))).append(refImagen).toString();
			String pathImagenReverso = pathImagenAnverso.replace(".TCA", ".TCR");
			byte imagenAnverso[] = (byte[]) null;
			byte imagenReverso[] = (byte[]) null;
			ConexionRemotaBean miConexion = new ConexionRemotaBean();
			int tipoCamara = Integer.parseInt(refImagen.substring(12, 16));
			int tipoConexion = CacheUtils.obtenerConexionByCamara(tipoCamara);
			miConexion = CacheUtils.getConexionRemota(bancoReceptor, tipoConexion);
			gestor = GestorArchivosFactory.obtenerGestor(miConexion);
			if (gestor != null) {
				gestor.conectar(miConexion);
				try {
					imagenAnverso = gestor.traerImagen(pathImagenAnverso);
					imagenReverso = gestor.traerImagen(pathImagenReverso);
				} catch (Exception e) {
					log.error((new StringBuilder("Error trayendo imagen del diario entrante")).append(e).toString());
				}
				gestor.desconectar(true);
			}
			if (imagenAnverso == null) {
				miConexion = CacheUtils.getConexionRemota(bancoReceptor, 4);
				if (miConexion != null)
					miConexion = CacheUtils.descifrarDatosDeConexion(miConexion);
				gestor = GestorArchivosFactory.obtenerGestor(miConexion);
				if (gestor != null) {
					gestor.conectar(miConexion);
					try {
						imagenAnverso = gestor.traerImagen(pathImagenAnverso);
						imagenReverso = gestor.traerImagen(pathImagenReverso);
					} catch (Exception e) {
						log.error((new StringBuilder("Error trayendo imagen del historico ")).append(e).toString());
					}
					gestor.desconectar(true);
				}
			}
			imgFirma = new ImageSignature();
			imagenAnverso = imagenAnverso == null ? imagenAnverso : CryptoUtils.decodificar(imagenAnverso, 1);
			imagenReverso = imagenReverso == null ? imagenReverso : CryptoUtils.decodificar(imagenReverso, 1);
			imgFirma.setImagenAnverso(imagenAnverso);
			imgFirma.setImagenReverso(imagenReverso);
		} catch (Exception e) {
			imgFirma = null;
			log.error("Error trayendo imagen", e);
		}
		return imgFirma;
	}

	// No cambió

	public List<ImageSignature> getGroupImagesRevision(String[] bancoReceptor, String[] anho, String[] mes, String[] dia, String[] bancoEmisor, String[] refImagen, String[] cuentaCliente) throws RemoteException {
		List<ImageSignature> lista = new ArrayList<ImageSignature>();
		SignatureService servicioFirmas = null;
		try {
			for (int i = 1; i < bancoEmisor.length; i++) {
				try {
					servicioFirmas = new SignatureService();
					ImageSignature temp = getImage(bancoReceptor[i], anho[i], mes[i], dia[i], bancoEmisor[i], refImagen[i]);
					ImageSignature imgFirma = new ImageSignature();
					imgFirma.setImagenAnverso(temp.getImagenAnverso());
					imgFirma.setImagenReverso(temp.getImagenReverso());
					imgFirma.setImagenUltraVioleta(temp.getImagenUltraVioleta());
					imgFirma.setSignatures(servicioFirmas.getSignatures(cuentaCliente[i]));
					lista.add(imgFirma);
				} catch (Exception e) {
					lista.add(null);
				}
			}
		} catch (Exception e) {
			log.error("Error en getGroupImagesRevision", e);
			return null;
		}
		return lista;
	}

	// No cambió

	public ImageSignature getImageRevision(String bancoReceptor, String anho, String mes, String dia, String bancoEmisor, String refImagen, String cuentaCliente, int firmas) throws RemoteException {
		try {
			ImageSignature temp = getImage(bancoReceptor, anho, mes, dia, bancoEmisor, refImagen);
			ImageSignature imgFirma = new ImageSignature();
			if (firmas != 0) {
				imgFirma.setSignatures(getFirmas(cuentaCliente));
			}
			imgFirma.setImagenAnverso(temp.getImagenAnverso());
			imgFirma.setImagenReverso(temp.getImagenReverso());
			return imgFirma;
		} catch (Exception e) {
			log.error((new StringBuilder(" error getImageRevision")).toString(), e);
		}
		return null;
	}

	public List<Firma> getFirmas(String cuenta) throws RemoteException {
		SignatureService service = new SignatureService();
		return service.getSignatures(cuenta);

	}

	// No cambió

	public List<Entidad> getEntidades() {
		EntidadDAO dao = new EntidadDAO();
		List<Entidad> lista = dao.getAll();
		return lista;
	}

	// No cambió

	public List<Oficina> getOficinas() {
		OficinaDAO dao = new OficinaDAO();
		List<Oficina> lista = dao.getAll();
		return lista;
	}

	// Si cambió

	public int setDocumento(TransaccionEntrante transaccion, byte[] frente, byte[] reverso, byte[] uv, String usuario, String direccion) throws RemoteException {
		TransaccionEntranteDAO dao = new TransaccionEntranteDAO();
		ConexionRemotaBean miConexion = new ConexionRemotaBean();
		GestorArchivos gestor;
		miConexion = CacheUtils.getConexionRemota(Utils.fillZerosLeft(transaccion.getEntidadProcesadora() + "", 4), AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_ENTRANTE);
		gestor = GestorArchivosFactory.obtenerGestor(miConexion);
		if (gestor != null) {
			try {
				String directorio = Utils.getRutaRepositorioImagenesEntrante(transaccion);
				int sumaImagenes = gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_ANVERSO, CryptoUtils.codificar(frente, CryptoUtilsConstantes.METODO_AES));
				sumaImagenes += gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_REVERSO, CryptoUtils.codificar(reverso, CryptoUtilsConstantes.METODO_AES));
				if (uv != null) {
					sumaImagenes += gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_UVIOLETA, CryptoUtils.codificar(uv, CryptoUtilsConstantes.METODO_AES));
				} else {
					sumaImagenes++;
				}
				gestor.desconectar(true);
				if (sumaImagenes < AplicacionConstantes.CANTIDAD_IMAGENES) {
					return AplicacionConstantes.DIGITALIZACION_ERROR_IMAGEN;
				}
				transaccion.setImage(transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_ANVERSO);
				if (dao.updateImage(transaccion)) {
					dao.closeConnection();
					AuditoriaTransaccionService.registrarAuditoria(transaccion, direccion, ChequeConstantes.CHEQUE_ENTRANTE_IMAGEN_DIGITALIZADA, new Timestamp(System.currentTimeMillis()), usuario);
					return AplicacionConstantes.DIGITALIZACION_PROCESADO;
				} else {
					return AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO;
				}
			} catch (Exception e) {
				return AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO;
			} finally {
				gestor.desconectar(true);
			}
		} else {
			return AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO;
		}
	}

	public int setDocumento(TransaccionEntrante transaccion, byte[] frente, byte[] reverso, byte[] uv, String usuario, String direccion, String referenciaLote) throws RemoteException {
		TransaccionEntranteDAO dao = new TransaccionEntranteDAO();
		ConexionRemotaBean miConexion = new ConexionRemotaBean();
		GestorArchivos gestor;
		miConexion = CacheUtils.getConexionRemota(Utils.fillZerosLeft(transaccion.getEntidadProcesadora() + "", 4), AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_ENTRANTE);
		gestor = GestorArchivosFactory.obtenerGestor(miConexion);
		if (gestor != null) {
			try {
				String directorio = Utils.getRutaRepositorioImagenesEntrante(transaccion);
				int sumaImagenes = gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_ANVERSO, CryptoUtils.codificar(frente, CryptoUtilsConstantes.METODO_AES));
				sumaImagenes += gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_REVERSO, CryptoUtils.codificar(reverso, CryptoUtilsConstantes.METODO_AES));
				if (uv != null) {
					sumaImagenes += gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_UVIOLETA, CryptoUtils.codificar(uv, CryptoUtilsConstantes.METODO_AES));
				} else {
					sumaImagenes++;
				}
				gestor.desconectar(true);
				if (sumaImagenes < AplicacionConstantes.CANTIDAD_IMAGENES) {
					return AplicacionConstantes.DIGITALIZACION_ERROR_IMAGEN;
				}
				transaccion.setImage(transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_ANVERSO);
				if (dao.updateImage(transaccion)) {
					dao.closeConnection();
					AuditoriaTransaccionService.registrarAuditoria(transaccion, direccion, ChequeConstantes.CHEQUE_ENTRANTE_IMAGEN_DIGITALIZADA, new Timestamp(System.currentTimeMillis()), usuario);
					try {
						if (referenciaLote != null && referenciaLote.trim().length() > 0) {
							LoteChequeDAO loteDAO = new LoteChequeDAO();
							loteDAO.incLote(referenciaLote, transaccion.getMonto());
						} else
							System.out.println("no hay para actualizar lote");

					} catch (Exception e) {
						e.printStackTrace();
					}
					return AplicacionConstantes.DIGITALIZACION_PROCESADO;
				} else {
					return AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO;
				}
			} catch (Exception e) {
				return AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO;
			} finally {
				gestor.desconectar(true);
			}
		} else {
			return AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO;
		}

	}

	public List<TransaccionEntrante> getTransaccionesRevisionFirmas(String usuario) throws RemoteException {
		TransaccionEntranteDAO transaccionDAO = new TransaccionEntranteDAO();
		try {
			String query = (new StringBuilder(" WHERE referencia_imagen != 'Sin imagen' AND usuario_revisor = '")).append(usuario).append("' AND (firma_revisada is null OR firma_revisada = ").append(0).append(")  ORDER BY id ASC").toString();
			List<TransaccionEntrante> transacciones = transaccionDAO.findAllByQuery2(query, AplicacionConstantes.REVISION_FIRMAS_CANTIDAD_RESULTADOS);
			transaccionDAO.closeConnection();
			return transacciones;
		} catch (Exception e) {
			return null;
		}
	}

	public List<MotivoDevolucion> getMotivosDevolucion() throws RemoteException {
		MotivoDevolucionDAO dao = new MotivoDevolucionDAO();
		List<MotivoDevolucion> lista = dao.obtenerCodigosRevisionFirmas();
		dao.closeConnection();
		return lista;
	}

	// No cambió

	public boolean actualizarListadoTransacciones(List<TransaccionEntrante> lista, String usuario, String ip) throws RemoteException {
		try {
			TransaccionEntranteDAO dao = new TransaccionEntranteDAO();
			for (int i = 0; i < lista.size(); i++) {
				if (lista.get(i).getMotivoDevolucion() != -1) {
					dao.updateMotivoDevolucionFirma(lista.get(i));
					AuditoriaTransaccionService.registrarAuditoria(lista.get(i), ip, (lista.get(i).getMotivoDevolucion() > 0) ? ChequeConstantes.CHEQUE_ENTRANTE_FIRMA_RECHAZADA : ChequeConstantes.CHEQUE_ENTRANTE_REVISADO, new Timestamp(System.currentTimeMillis()), usuario);
				}
			}
			dao.closeConnection();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public RegistroMagnetizacion getRegistroMagnetizar(String _cuentaCliente, String documento, String tc) throws RemoteException {
		ConexionRemotaBean miConexion = new ConexionRemotaBean();
		TransaccionSalienteDAO dao = new TransaccionSalienteDAO();
		RegistroMagnetizacion registroMagnetizar = null;
		try {
			int longitudTotalMontoMagnetizar = ApplicationParameters.getInstance().getInt("magnetizar.longitud.monto");
			String clausulaWhereSql = "";
			String cuentaCliente[] = Utils.cuentaClienteSplit(_cuentaCliente);
			clausulaWhereSql = (new StringBuilder(" where serial_cheque = '")).append(documento).append("' and codigo_banco_cheque=").append(cuentaCliente[0]).append(" and codigo_oficina_cheque=").append(cuentaCliente[1]).append(" and digito_control_cheque=").append(cuentaCliente[2]).append(" and cuenta_cheque='").append(cuentaCliente[3]).append("' and tipo_cheque=").append(tc).toString();
			TransaccionSaliente transaccionAsociada = dao.findByQuery(clausulaWhereSql);
			if (transaccionAsociada != null) {
				miConexion = CacheUtils.getConexionRemota((new StringBuilder()).append(transaccionAsociada.getEntidadProcesadora()).toString(), AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_SALIENTE);
				GestorArchivos gestor = GestorArchivosFactory.obtenerGestor(miConexion);
				if (gestor != null) {
					String rutaImagen = (new StringBuilder(String.valueOf(Utils.getRutaRepositorioImagenesSaliente(transaccionAsociada)))).append(transaccionAsociada.getImagen()).toString();
					byte imagenAnverso[] = gestor.traerImagen(rutaImagen);
					if (imagenAnverso != null) {
						registroMagnetizar = new RegistroMagnetizacion();
						registroMagnetizar.setImagenAnverso(imagenAnverso);
						registroMagnetizar.setMontoMagnetizar(Utils.convertirMonto(transaccionAsociada.getMonto(), longitudTotalMontoMagnetizar));
					}
					gestor.desconectar(true);
				}
			}
		} catch (Exception e) {
			log.error("getRegistroMagnetizar", e);
			return null;
		}

		return registroMagnetizar;
	}

	public List<Entidad> getEntidadesProcesadoras(String user) throws RemoteException {
		EntidadDAO dao = new EntidadDAO();
		String query = (new StringBuilder("SELECT B.CODBANCO, B.NOMBANCO, B.ENTIDAD_MATRIZ, B.NOMBANCOVIEW FROM BANCOS B, SEGURIDAD_USUARIO U, SEGURIDAD_USUARIO_BANCOS UB WHERE U.LOGIN = '")).append(user).append("' AND U.ID = UB.USUARIO_ENTIDADES_PROCESADORAS_ID AND UB.BANCO_ID = B.ID").toString();
		List<Entidad> lista = dao.getAllByQuery(query);
		return lista;
	}

	public List<TransaccionSaliente> getTransaccionesMagnetizar(int entidadProcesadora) throws RemoteException {
		EntidadDAO entidadDao = new EntidadDAO();
		Entidad auxEntity = entidadDao.getByCodigo(entidadProcesadora);
		TransaccionSalienteDAO dao = new TransaccionSalienteDAO();
		try {
			String whereClause = (auxEntity.getEntidadMatriz() == null || auxEntity.getEntidadMatriz() == 0) ? "" : " where codigo_entidad_emisora = " + entidadProcesadora;
			List<TransaccionSaliente> transacciones = dao.findAllByQuery(whereClause);
			return transacciones;
		} catch (Exception e) {
			return null;
		}
	}

	public List<TransaccionEntrante> getTransaccionesDevueltaEntrante(int entidadProcesadora, Date fecha) throws RemoteException {
		EntidadDAO entidadDao = new EntidadDAO();
		Entidad auxEntity = entidadDao.getByCodigo(entidadProcesadora);
		TransaccionEntranteDAO dao = new TransaccionEntranteDAO();
		try {
			Calendar fechaInicio = Calendar.getInstance();
			fechaInicio.setTime(fecha);
			fechaInicio.set(Calendar.HOUR, 0);
			fechaInicio.set(Calendar.MINUTE, 0);
			fechaInicio.set(Calendar.SECOND, 0);
			String whereClause = ((auxEntity.getEntidadMatriz() == null || auxEntity.getEntidadMatriz() == 0) ? "where " : " where codigo_banco_cheque = " + entidadProcesadora + " and ") + " codigo_motivo_devolucion != " + AplicacionConstantes.ESTADO_APROBADO + " and fecha_camara between '" + Utils.convertToDateYYYYDDMM(fechaInicio.getTime()) + " 00:00:00' and '" + Utils.convertToDateYYYYDDMM(fechaInicio.getTime()) + " 23:59:59'";
			List<TransaccionEntrante> transacciones = dao.findAllByQuery(whereClause);
			if (transacciones.size() == 0) {
				transacciones = dao.findAllByQueryHistorico(whereClause);
			}
			return transacciones;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<TransaccionSaliente> getTransaccionesDevueltaSaliente(int entidadProcesadora, Date fecha) throws RemoteException {
		EntidadDAO entidadDao = new EntidadDAO();
		Entidad auxEntity = entidadDao.getByCodigo(entidadProcesadora);
		TransaccionSalienteDAO dao = new TransaccionSalienteDAO();
		try {
			Calendar fechaInicio = Calendar.getInstance();
			fechaInicio.setTime(fecha);
			fechaInicio.set(Calendar.HOUR, 0);
			fechaInicio.set(Calendar.MINUTE, 0);
			fechaInicio.set(Calendar.SECOND, 0);
			String whereClause = ((auxEntity.getEntidadMatriz() == null || auxEntity.getEntidadMatriz() == 0) ? "where " : " where codigo_entidad_emisora = " + entidadProcesadora + " and ") + " codigo_motivo_devolucion != " + AplicacionConstantes.ESTADO_APROBADO + " and fecha_camara between '" + Utils.convertToDateYYYYDDMM(fechaInicio.getTime()) + " 00:00:00' and '" + Utils.convertToDateYYYYDDMM(fechaInicio.getTime()) + " 23:59:59'";
			List<TransaccionSaliente> transacciones = dao.findAllByQuery(whereClause);
			if (transacciones.size() == 0) {
				transacciones = dao.findAllByQueryHistorico(whereClause);
			}
			return transacciones;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// No cambió

	public byte[] generarImagenesPDF(Object reporteImagen) throws RemoteException {
		ManejadorReportesService manejador = new ManejadorReportesService();
		Map<String, String> patch = new LinkedHashMap<String, String>();
		List<Object> listaPatch = new ArrayList<Object>();
		listaPatch.add(reporteImagen);
		return manejador.generarReporte(listaPatch, ReporteConstantes.TIPO_CONSULTA_IMAGEN, patch);
	}

	// No cambió

	public byte[] generarReportePDF(int tipoReporte, List<Object> listado) throws RemoteException {
		ManejadorReportesService manejador = new ManejadorReportesService();
		Map<String, String> patch = new LinkedHashMap<String, String>();
		byte[] arreglo = manejador.generarReporte(listado, tipoReporte, patch);
		return arreglo;
	}

	// No cambió - Ojo

	public List<ImageSignature> getGroupImagesSalientes(String[] bancoReceptor, String[] anho, String[] mes, String[] dia, String[] bancoEmisor, String[] refImagen) throws RemoteException {
		List<ImageSignature> lista = new ArrayList<ImageSignature>();
		FileTransferClient clienteFTPRepositorioSaliente = null;
		FileTransferClient clienteFTPRepositorioHistorico = null;
		try {
			FTPUtils.conectar(clienteFTPRepositorioSaliente);
			FTPUtils.conectar(clienteFTPRepositorioHistorico);
			for (int i = 1; i < bancoEmisor.length; i++) {
				try {
					ImageSignature imgFirma = new ImageSignature();
					String source = (new StringBuilder(String.valueOf(Utils.fillZerosLeft(bancoEmisor[i], 4)))).append("/").append(Utils.fillZerosLeft(anho[i], 4)).append("/").append(Utils.fillZerosLeft(mes[i], 2)).append("/").append(Utils.fillZerosLeft(dia[i], 2)).append("/").append(Utils.fillZerosLeft(bancoReceptor[i], 4)).append("/").append(refImagen).toString();
					FTPUtils.conectar(clienteFTPRepositorioSaliente);
					byte[] imagenAnverso = FTPUtils.traerImagen(source, clienteFTPRepositorioSaliente);
					if (null != imagenAnverso) {
						imgFirma.setImagenAnverso(imagenAnverso);
						imgFirma.setImagenReverso(FTPUtils.traerImagen(source.substring(0, source.length() - 1) + "R", clienteFTPRepositorioSaliente));
					} else {
						imgFirma.setImagenAnverso(FTPUtils.traerImagen(source, clienteFTPRepositorioHistorico));
						imgFirma.setImagenReverso(FTPUtils.traerImagen(source.substring(0, source.length() - 1) + "R", clienteFTPRepositorioHistorico));
					}
					lista.add(imgFirma);
				} catch (Exception e) {
					lista.add(null);
				}
			}
		} catch (Exception e) {
			return null;
		} finally {
			FTPUtils.desconectar(clienteFTPRepositorioSaliente);
			FTPUtils.desconectar(clienteFTPRepositorioHistorico);
		}
		return lista;
	}

	// Si cambió

	public ImageSignature getImageSaliente(String bancoReceptor, String anho, String mes, String dia, String bancoEmisor, String refImagen) throws RemoteException {
		ConexionRemotaBean miConexion = new ConexionRemotaBean();
		GestorArchivos gestor;
		miConexion = CacheUtils.getConexionRemota(bancoReceptor, AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_SALIENTE);
		gestor = GestorArchivosFactory.obtenerGestor(miConexion);
		if (gestor != null) {
			try {
				ImageSignature imgFirma = new ImageSignature();
				String source = (new StringBuilder(String.valueOf(Utils.fillZerosLeft(bancoReceptor, 4)))).append("/").append(Utils.fillZerosLeft(anho, 4)).append("/").append(Utils.fillZerosLeft(mes, 2)).append("/").append(Utils.fillZerosLeft(dia, 2)).append("/").append(Utils.fillZerosLeft(bancoEmisor, 4)).append("/").append(refImagen).toString();
				byte[] imagenAnverso = gestor.traerImagen(source);
				if (null != imagenAnverso) {
					imgFirma.setImagenAnverso(imagenAnverso);
					imgFirma.setImagenReverso(gestor.traerImagen(source.substring(0, source.length() - 1) + "R"));
					gestor.desconectar(true);
				} else {
					gestor.desconectar(true);
					miConexion = CacheUtils.getConexionRemota(bancoReceptor, AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_HISTORICO);
					gestor = GestorArchivosFactory.obtenerGestor(miConexion);
					if (gestor != null) {
						imgFirma.setImagenAnverso(gestor.traerImagen(source));
						imgFirma.setImagenReverso(gestor.traerImagen(source.substring(0, source.length() - 1) + "R"));
						gestor.desconectar(true);
					}
				}
				return imgFirma;
			} catch (Exception e) {
				return null;
			} finally {
				gestor.desconectar(true);
			}
		} else {
			return null;
		}
	}

	// No cambió

	public List<TransaccionEntrante> getTransaccionesDigitalizarEntrante(int entidadProcesadora) throws RemoteException {
		EntidadDAO entidadDao = new EntidadDAO();
		Entidad auxEntity = entidadDao.getByCodigo(entidadProcesadora);
		TransaccionEntranteDAO dao = new TransaccionEntranteDAO();
		try {
			String whereClause = auxEntity.getEntidadMatriz() != null && auxEntity.getEntidadMatriz().intValue() != 0 ? (new StringBuilder(" where codigo_banco_cheque = ")).append(entidadProcesadora).toString() : " ";
			List<TransaccionEntrante> transacciones = dao.findAllByQuery(whereClause);
			return transacciones;
		} catch (Exception e) {
			return null;
		}
	}

	public List<TransaccionEntrante> getTransaccionesDigitalizarEntrante2(int entidadProcesadora, ArrayList<Integer> entidades) throws RemoteException {
		TransaccionEntranteDAO dao = new TransaccionEntranteDAO();
		String entidadesString = "";
		try {
			for (int i = 0; i < entidades.size(); i++) {
				entidadesString = (new StringBuilder(String.valueOf(entidadesString))).append(entidades.get(i)).append(",").toString();
			}
			entidadesString = entidadesString.substring(0, entidadesString.length() - 1);
			String whereClause = (new StringBuilder(" where codigo_entidad_emisora in (")).append(entidadesString).append(")").toString();
			List transacciones = dao.findAllByQuery(whereClause);
			return transacciones;
		} catch (Exception e) {
			return null;
		}
	}

	// No cambió

	public int setDocumentoSaliente(TransaccionSaliente transaccion, int estado, String usuario, String direccion) throws RemoteException {
		try {
			TransaccionSalienteDAO saliente = new TransaccionSalienteDAO();
			if (saliente.actualizarEstado(transaccion, estado)) {
				AuditoriaTransaccionService.registrarAuditoria(transaccion, direccion, ChequeConstantes.CHEQUE_SALIENTE_MAGNETIZADO, new Timestamp(System.currentTimeMillis()), usuario);
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 0;
	}

	public Map<Integer, Integer> setDigitalizacion(List<Digitalizado> listadoDigitalizacion, String entidadProcesadora, String usuario, String ip, int tipoCamara) throws RemoteException {
		long inicioProceso = System.currentTimeMillis();
		System.out.println("Inicio proceso Registro de Transacciones. Tipo Camara: " + tipoCamara);
		Map<Integer, Integer> contador = new LinkedHashMap<Integer, Integer>();
		ConexionRemotaBean miConexion = new ConexionRemotaBean();
		GestorArchivos gestor;

		int tipCam = Utils.getTipoConexion(tipoCamara);

		if (tipCam == 0)
			return null;

		miConexion = CacheUtils.getConexionRemota(Utils.fillZerosLeft(entidadProcesadora + "", 4), tipCam);
		gestor = GestorArchivosFactory.obtenerGestor(miConexion);
		if (gestor != null) {
			try {
				ChequesDigitalizadosDAO dao = new ChequesDigitalizadosDAO();

				try {
					for (int i = 0; i < listadoDigitalizacion.size(); i++) {
						long inicioBloque = System.currentTimeMillis();
						ChequesDigitalizados transaccion = listadoDigitalizacion.get(i).getTransaccion();
						transaccion.setImagenNombre(transaccion.getIdLote() + "-" + transaccion.getPosicionLote() + ChequeConstantes.IMAGEN_ANVERSO);

						ParametrosGenerales parametros = (new ParametrosGeneralesDAO()).obtenerParametrosSistema();
						String fechaCamaraStr = parametros.getFechaCamara();
						transaccion.setFecha(cr.tecnoware.sice.applets.utils.Utils.StringToTimestamp(fechaCamaraStr));
						String directorio = Utils.getRutaRepositorioImagenes(transaccion);
						int sumaImagenes = gestor.subirImagen(directorio, transaccion.getIdLote() + "-" + transaccion.getPosicionLote() + ChequeConstantes.IMAGEN_ANVERSO, CryptoUtils.codificar(listadoDigitalizacion.get(i).getImagenAnverso(), CryptoUtilsConstantes.METODO_AES));
						sumaImagenes += gestor.subirImagen(directorio, transaccion.getIdLote() + "-" + transaccion.getPosicionLote() + ChequeConstantes.IMAGEN_REVERSO, CryptoUtils.codificar(listadoDigitalizacion.get(i).getImagenReverso(), CryptoUtilsConstantes.METODO_AES));
						if (listadoDigitalizacion.get(i).getImagenVioleta() != null) {
							sumaImagenes += gestor.subirImagen(directorio, transaccion.getIdLote() + "-" + transaccion.getPosicionLote() + ChequeConstantes.IMAGEN_UVIOLETA, CryptoUtils.codificar(listadoDigitalizacion.get(i).getImagenVioleta(), CryptoUtilsConstantes.METODO_AES));
						} else {
							sumaImagenes++;
						}
						if (sumaImagenes < AplicacionConstantes.CANTIDAD_IMAGENES) {
							contador.put(AplicacionConstantes.DIGITALIZACION_ERROR_IMAGEN, (contador.get(AplicacionConstantes.DIGITALIZACION_ERROR_IMAGEN) != null) ? (contador.get(AplicacionConstantes.DIGITALIZACION_ERROR_IMAGEN) + 1) : 1);
							continue;
						}
						inicioBloque = System.currentTimeMillis();
						if (dao.insertCheque(transaccion)) {
							ChequesDigitalizados transaccion2 = dao.get(transaccion);
							AuditoriaTransaccionService.registrarAuditoria(transaccion2, ip, ChequeConstantes.CHEQUE_ENTRANTE_IMAGEN_DIGITALIZADA, new Timestamp(System.currentTimeMillis()), usuario); // AuditoriaTransaccionService.registrarAuditoria(transaccion,
							contador.put(AplicacionConstantes.DIGITALIZACION_PROCESADO, (contador.get(AplicacionConstantes.DIGITALIZACION_PROCESADO) != null) ? (contador.get(AplicacionConstantes.DIGITALIZACION_PROCESADO) + 1) : 1);
						} else {
							System.out.println("ERROR REGISTRANDO TRANSACCION");
							contador.put(AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO, (contador.get(AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO) != null) ? (contador.get(AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO) + 1) : 1);
						}
						System.out.println("Fin de proceso de la data: " + (System.currentTimeMillis() - inicioBloque) + " MS " + ((System.currentTimeMillis() - inicioBloque) / 1000) + " Seg");
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				} finally {
					gestor.desconectar(true);
					dao.closeConnection();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			gestor.desconectar(true);
			long finalProceso = System.currentTimeMillis();
			System.out.println("Fin de proceso en el servidor: " + (finalProceso - inicioProceso) + " MS " + ((finalProceso - inicioProceso) / 1000) + " Seg");
		} else {
			System.out.println("Gestor es nulo.");
		}
		return contador;
	}

	public int actLote(LoteCheque lote) {
		LoteChequeDAO daoLote = new LoteChequeDAO();
		return daoLote.updateLote(lote);
	}

	// Si cambió
	public Map<Integer, Integer> setDigitalizacionSaliente(List<DigitalizadoSaliente> listadoDigitalizacion, String entidadProcesadora, String usuario, String ip) throws RemoteException {
		Map<Integer, Integer> contador = new LinkedHashMap<Integer, Integer>();
		ConexionRemotaBean miConexion = new ConexionRemotaBean();
		GestorArchivos gestor;
		miConexion = CacheUtils.getConexionRemota(Utils.fillZerosLeft(entidadProcesadora + "", 4), AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_SALIENTE);
		gestor = GestorArchivosFactory.obtenerGestor(miConexion);
		if (gestor != null) {
			try {
				TransaccionSalienteDAO dao = new TransaccionSalienteDAO();
				try {
					for (int i = 0; i < listadoDigitalizacion.size(); i++) {
						TransaccionSaliente transaccion = listadoDigitalizacion.get(i).getTransaccion();
						String directorio = Utils.getRutaRepositorioImagenesSaliente(transaccion);
						int sumaImagenes = gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_ANVERSO, CryptoUtils.codificar(listadoDigitalizacion.get(i).getImagenAnverso(), CryptoUtilsConstantes.METODO_AES));
						sumaImagenes += gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_REVERSO, CryptoUtils.codificar(listadoDigitalizacion.get(i).getImagenReverso(), CryptoUtilsConstantes.METODO_AES));
						if (listadoDigitalizacion.get(i).getImagenVioleta() != null) {
							sumaImagenes += gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_UVIOLETA, CryptoUtils.codificar(listadoDigitalizacion.get(i).getImagenVioleta(), CryptoUtilsConstantes.METODO_AES));
						} else {
							sumaImagenes++;
						}
						if (sumaImagenes < AplicacionConstantes.CANTIDAD_IMAGENES) {
							contador.put(AplicacionConstantes.DIGITALIZACION_ERROR_IMAGEN, (contador.get(AplicacionConstantes.DIGITALIZACION_ERROR_IMAGEN) != null) ? (contador.get(AplicacionConstantes.DIGITALIZACION_ERROR_IMAGEN) + 1) : 1);
							continue;
						}
						transaccion.setImagen(transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_ANVERSO);
						if (dao.updateImage(transaccion)) {
							AuditoriaTransaccionService.registrarAuditoria(transaccion, ip, ChequeConstantes.CHEQUE_SALIENTE_IMAGEN_DIGITALIZADO, new Timestamp(System.currentTimeMillis()), usuario);
							contador.put(AplicacionConstantes.DIGITALIZACION_PROCESADO, (contador.get(AplicacionConstantes.DIGITALIZACION_PROCESADO) != null) ? (contador.get(AplicacionConstantes.DIGITALIZACION_PROCESADO) + 1) : 1);
						} else {
							contador.put(AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO, (contador.get(AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO) != null) ? (contador.get(AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO) + 1) : 1);
						}
					}
				} catch (Exception e) {
					return null;
				} finally {
					gestor.desconectar(true);
					dao.closeConnection();
				}
			} catch (Exception e) {
				return null;
			}
			gestor.desconectar(true);
		}
		return contador;
	}

	// Si cambió

	public int setDocumentoDigitalizarSaliente(TransaccionSaliente transaccion, byte[] frente, byte[] reverso, byte[] uv, String usuario, String direccion) throws RemoteException {
		TransaccionSalienteDAO dao = new TransaccionSalienteDAO();
		ConexionRemotaBean miConexion = new ConexionRemotaBean();
		GestorArchivos gestor;
		miConexion = CacheUtils.getConexionRemota(Utils.fillZerosLeft(transaccion.getEntidadProcesadora() + "", 4), AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_SALIENTE);
		gestor = GestorArchivosFactory.obtenerGestor(miConexion);
		if (gestor != null) {
			try {
				String directorio = Utils.getRutaRepositorioImagenesSaliente(transaccion);
				int sumaImagenes = gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_ANVERSO, CryptoUtils.codificar(frente, CryptoUtilsConstantes.METODO_AES));
				sumaImagenes += gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_REVERSO, CryptoUtils.codificar(reverso, CryptoUtilsConstantes.METODO_AES));
				if (uv != null) {
					sumaImagenes += gestor.subirImagen(directorio, transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_UVIOLETA, CryptoUtils.codificar(uv, CryptoUtilsConstantes.METODO_AES));
				} else {
					sumaImagenes++;
				}
				if (sumaImagenes < AplicacionConstantes.CANTIDAD_IMAGENES) {
					return AplicacionConstantes.DIGITALIZACION_ERROR_IMAGEN;
				}
				gestor.desconectar(true);
				transaccion.setImagen(transaccion.getReferenciaIBRN() + ChequeConstantes.IMAGEN_ANVERSO);
				if (dao.updateImage(transaccion)) {
					dao.closeConnection();
					AuditoriaTransaccionService.registrarAuditoria(transaccion, direccion, ChequeConstantes.CHEQUE_SALIENTE_IMAGEN_DIGITALIZADO, new Timestamp(System.currentTimeMillis()), usuario);
					return AplicacionConstantes.DIGITALIZACION_PROCESADO;
				} else {
					return AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO;
				}
			} catch (Exception e) {
				return AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO;
			} finally {
				gestor.desconectar(true);
			}
		} else {
			return AplicacionConstantes.DIGITALIZACION_ERROR_ACTUALIZANDO;
		}
	}

	// No cambió

	public List<TransaccionEntrante> getTransaccionesEntrantes(int entidadProcesadora, Date fecha) throws RemoteException {
		EntidadDAO entidadDao = new EntidadDAO();
		Entidad auxEntity = entidadDao.getByCodigo(entidadProcesadora);
		TransaccionEntranteDAO dao = new TransaccionEntranteDAO();
		try {
			Calendar fechaInicio = Calendar.getInstance();
			fechaInicio.setTime(fecha);
			fechaInicio.set(Calendar.HOUR, 0);
			fechaInicio.set(Calendar.MINUTE, 0);
			fechaInicio.set(Calendar.SECOND, 0);
			String whereClause = (new StringBuilder(String.valueOf(auxEntity.getEntidadMatriz() != null && auxEntity.getEntidadMatriz().intValue() != 0 ? ((Object) ((new StringBuilder(" where codigo_banco_cheque = ")).append(entidadProcesadora).append(" and ").toString())) : "where "))).append(" fecha_camara between '").append(Utils.convertToDateYYYYMMDD(fechaInicio.getTime())).append(" 00:00:00' and '").append(Utils.convertToDateYYYYMMDD(fechaInicio.getTime())).append(" 23:59:59'").toString();
			List<TransaccionEntrante> transacciones = dao.findAllByQuery(whereClause);
			if (transacciones.size() == 0) {
				transacciones = dao.findAllByQueryHistorico(whereClause);
			}
			return transacciones;
		} catch (Exception e) {
			return null;
		}

	}

	// No cambió

	public String getReferenciaCaja(Date fechaProceso) throws RemoteException {
		ArchivadorChequeEntranteDAO dao = new ArchivadorChequeEntranteDAO();
		int resultado = dao.countArchivadorByFechaProcesado(fechaProceso);
		if (resultado != -1) {
			return Utils.convertToDateYYYYMMDD(fechaProceso).replace("-", "") + "-" + Utils.fillZerosLeft(Integer.toString(resultado + 1), 4);
		}
		return null;
	}

	// No cambió

	public int setDataArchivador(Map<Integer, LinkedList<TransaccionEntrante>> transacciones, Date fecha) throws RemoteException {
		try {
			Set<Integer> pockets = transacciones.keySet();
			TransaccionEntranteDAO dao = new TransaccionEntranteDAO();
			ArchivadorChequeEntranteDAO daoArchivo = new ArchivadorChequeEntranteDAO();
			for (Iterator<Integer> iterator = pockets.iterator(); iterator.hasNext();) {
				int bolsillo = iterator.next();
				LinkedList<TransaccionEntrante> listaEnlazada = transacciones.get(bolsillo);
				if (listaEnlazada == null || listaEnlazada.size() == 0)
					continue;
				double monto = 0.0;
				int numeroTransacciones = 0;
				ArchivadorChequeEntrante archivo = new ArchivadorChequeEntrante();
				archivo.setFecha(fecha);
				archivo.setReferenciaCaja(listaEnlazada.get(0).getReferenciaCaja());
				archivo.setReferenciaLote(Utils.fillZerosLeft(Integer.toString(bolsillo + 1), 3));
				archivo.setNumeroTransacciones(0);
				archivo.setMonto(0.0);
				int referencia = daoArchivo.save(archivo);
				if (referencia == 0)
					continue;
				archivo.setId(referencia);
				for (int i = 0; i < listaEnlazada.size(); i++) {
					if (dao.updateReferenciasArchivo(listaEnlazada.get(i), referencia, listaEnlazada.size() - i)) {
						numeroTransacciones++;
						monto += listaEnlazada.get(i).getMonto();
					}
				}
				archivo.setMonto(monto);
				archivo.setNumeroTransacciones(numeroTransacciones);
				daoArchivo.update(archivo);
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<BolsilloClasificacion> getBolsilloClasificacion(int tipo, int codigoEntidadProcesadora) throws RemoteException {
		BolsilloDAO dao = new BolsilloDAO();
		List<BolsilloClasificacion> lista = null;
System.out.println("TIPO DE CLASIFICACION "+tipo);
System.out.println("BANCO "+AplicacionConstantes.CLASIFICACION_BANCO);
System.out.println("OFICINA "+AplicacionConstantes.CLASIFICACION_OFICINA);
System.out.println("ENTRANTE P "+AplicacionConstantes.CLASIFICACION_ENTRANTE_PERSONALIZADA);


		if (tipo == AplicacionConstantes.CLASIFICACION_BANCO) {
			String query = (new StringBuilder("SELECT B.CODIGO,B.NUMERO_BOLSILLO,B.CODIGO_ENTIDAD_PROCESADORA,B.TIPO, B.RAFAGA,B.monto_inicial,B.monto_final FROM  BOLSILLO_CLASIFICACION B WHERE B.CODIGO_ENTIDAD_PROCESADORA=")).append(codigoEntidadProcesadora).append(" AND B.TIPO =").append(1).append(" ").toString();
			lista = dao.getAllByQuery(query);
		} else if (tipo == AplicacionConstantes.CLASIFICACION_OFICINA) {
			String query = (new StringBuilder("SELECT B.CODIGO,B.NUMERO_BOLSILLO,B.CODIGO_ENTIDAD_PROCESADORA,B.TIPO, B.RAFAGA,B.monto_inicial,B.monto_final FROM  BOLSILLO_CLASIFICACION B WHERE B.CODIGO_ENTIDAD_PROCESADORA=")).append(codigoEntidadProcesadora).append(" AND B.TIPO =").append(2).append(" ").toString();
			lista = dao.getAllByQuery(query);
		}else if (tipo == AplicacionConstantes.CLASIFICACION_ENTRANTE_PERSONALIZADA) {
			String query = (new StringBuilder("SELECT B.CODIGO,B.NUMERO_BOLSILLO,B.CODIGO_ENTIDAD_PROCESADORA,B.TIPO, B.RAFAGA,B.monto_inicial,B.monto_final FROM  BOLSILLO_CLASIFICACION B WHERE B.CODIGO_ENTIDAD_PROCESADORA= '")).append(codigoEntidadProcesadora).append("' AND B.TIPO ='").append(3).append("' ").toString();
			lista = dao.getAllByQuery(query);
		}else {
			String query = (new StringBuilder("SELECT B.CODIGO,B.NUMERO_BOLSILLO,B.CODIGO_ENTIDAD_PROCESADORA,B.TIPO, B.RAFAGA,B.monto_inicial,B.monto_final FROM  BOLSILLO_CLASIFICACION B WHERE B.CODIGO_ENTIDAD_PROCESADORA= '")).append(codigoEntidadProcesadora).append("' AND B.TIPO ='").append(tipo).append("' ").toString();
			lista = dao.getAllByQuery(query);
		}

		return lista;
	}

	public List<LoteCheque> obtenerLotes(int estado, int codigoEntidadProcesadora, int tipoCamara) throws RemoteException {

		LoteChequeDAO dao = new LoteChequeDAO();
		List<LoteCheque> lista = new ArrayList<LoteCheque>();
		String whereClause = (new StringBuilder(" WHERE estado in(0,1) AND entidad_procesadora=")).append(codigoEntidadProcesadora).append(" AND tipo_camara= ").append(tipoCamara).toString();
		lista = dao.findAllWhere(whereClause);
		if (null == lista) {
			lista = new ArrayList<LoteCheque>();
		}
		return lista;

	}

	public List<LoteCheque> obtenerLotesPorEstado(int estado, int codigoEntidadProcesadora, int tipoCamara) throws RemoteException {

		LoteChequeDAO dao = new LoteChequeDAO();
		List<LoteCheque> lista = new ArrayList<LoteCheque>();
		String whereClause = (new StringBuilder(" WHERE estado in(")).append(estado).append(")").append(" AND entidad_procesadora=").append(codigoEntidadProcesadora).append(" AND tipo_camara= ").append(tipoCamara).toString();
		lista = dao.findAllWhere(whereClause);
		if (null == lista) {
			lista = new ArrayList<LoteCheque>();
		}
		return lista;

	}

	public ConexionRemotaBean getConexionRemota(int tipoConexion, int codigoEntidadProcesadora) throws RemoteException {
		ConexionRemotaDAO dao = new ConexionRemotaDAO();
		return dao.getConexioRemota(tipoConexion, codigoEntidadProcesadora);
	}

	public LoteCheque obtenerLote(int id) throws RemoteException {

		LoteChequeDAO dao = new LoteChequeDAO();
		List<LoteCheque> lista = new ArrayList<LoteCheque>();
		String whereClause = (new StringBuilder(" WHERE id =")).append(id).toString();
		lista = dao.findAllWhere(whereClause);
		if (null == lista) {
			lista = new ArrayList<LoteCheque>();
		}
		return lista.get(0);

	}

	// Trae lista de cheques con estado maracod como incosistente con archivo
	// verilect
	public List<TransaccionInterna> getTransaccionesInconsistenciasVerilect(Date fecha) throws RemoteException {
		TransaccionInternaDAO dao = new TransaccionInternaDAO();
		try {
			Calendar fechaInicio = Calendar.getInstance();
			fechaInicio.setTime(fecha);
			fechaInicio.set(Calendar.HOUR, 0);
			fechaInicio.set(Calendar.MINUTE, 0);
			fechaInicio.set(Calendar.SECOND, 0);
			String whereClause = " where estado_cheque = " + ChequeConstantes.CHEQUE_INTERNO_INCOSISTENCIA_VERILECT;
			List<TransaccionInterna> transacciones = dao.findAllByQueryVerilect(whereClause);
			if (transacciones.size() == 0) {
				transacciones = dao.findAllByQueryHistoricoVerilect(whereClause);
			}
			return transacciones;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ChequesDigitalizados getCuentaDepositaria(ChequesDigitalizados cheque) throws RemoteException {
		if (cheque.getTipoCamara() == AplicacionConstantes.CAMARA_SALIENTE)
			cheque.getCheque().setCuentaDepositaria("01370012650007412191");
		cheque.setEstado(ChequeDigitalizadoConstantes.CHEQUE_PENDIENTE_APROBAR);
		return cheque;
	}

	public boolean registrarChequesCorregidos(List<ChequesDigitalizados> cheques, int entidad, int tipoCamara) throws RemoteException {
		ProcesarCamaraServices servicio = FactoryDAOService.obtenerRegistrarTransacciones(tipoCamara);
		boolean valido = false;
		if (servicio != null)
			valido = servicio.registrarTransaccionesCorregidas(cheques, entidad);
		else
			System.out.println("el servicio es nulo");
		return valido;
	}

	public List<ChequesDigitalizados> getTransaccionesByCorreccionByLote(LoteCheque lote, List<Integer> estados) throws RemoteException {

		try {
			String where = " where ";
			if (lote != null) {
				where = (new StringBuilder(String.valueOf(where))).append(" idLote = ").append(lote.getIdUsuario()).toString();
				where = (new StringBuilder(String.valueOf(where))).append(" and tipocamara = ").append(lote.getTipoCamara()).toString();
			}
			if (estados != null && estados.size() > 0) {
				where = (new StringBuilder(String.valueOf(where))).append(" and estado in ( ").toString();
				for (Iterator iterator = estados.iterator(); iterator.hasNext();) {
					Integer i = (Integer) iterator.next();
					where = (new StringBuilder(String.valueOf(where))).append(" ").append(i).append(", ").toString();
				}

				where = where.trim();
				if (where.endsWith(","))
					where = where.substring(0, where.length() - 1);
				where = (new StringBuilder(String.valueOf(where))).append(")").toString();
			}

			ChequesDigitalizadosDAO dao = new ChequesDigitalizadosDAO();
			List<ChequesDigitalizados> cheques = dao.findAllWhere(where + " order by id");

			if (lote.getTipoCamara() == AplicacionConstantes.CAMARA_ENTRANTE) {
				TransaccionEntranteDAO daoE = new TransaccionEntranteDAO();
				where = " where  lote=" + lote.getIdUsuario();
				where += " and estado!=" + ChequeConstantes.CHEQUE_SALIENTE_RECIBIDO_REGIONAL;
				List<TransaccionEntrante> t = daoE.findAllByQuery(where);
				HashMap<Integer, Integer> secuencias = new HashMap<Integer, Integer>();
				if (t != null && t.size() > 0) {
					for (TransaccionEntrante c : t)
						secuencias.put(c.getSecuencia(), c.getSecuencia());
				} else
					return cheques;

				List<ChequesDigitalizados> chequesE = new ArrayList<ChequesDigitalizados>();
				for (ChequesDigitalizados c : cheques) {
					if (secuencias.get(c.getPosicionLote()) == null)
						chequesE.add(c);
				}
				return chequesE;
			} else
				return cheques;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public List<TipoDocumento> getTiposDocumentos() throws RemoteException {
		List<TipoDocumento> tc = CacheUtils.getTcList();
		if (tc == null) {
			TipoDocumentoDAO td = new TipoDocumentoDAO();
			tc = td.getALL();
			if (tc != null) {
				CacheUtils.setTcList(tc);
			}
		}

		return tc;
	}

	public boolean liberarTransacciones(List<ChequesDigitalizados> cheques) throws RemoteException {
		ChequesDigitalizadosDAO digitalizados = new ChequesDigitalizadosDAO();
		return digitalizados.liberarChequeDigitalizado(cheques);

	}

	public boolean asignarLoteUsuario(LoteCheque lote, String usuario) throws RemoteException {
		LoteChequeDAO loteDAO = new LoteChequeDAO();
		return loteDAO.asignarLoteUsuario(lote, usuario);
	}

	public boolean liberarLoteUsuario(LoteCheque lote) throws RemoteException {
		LoteChequeDAO loteDAO = new LoteChequeDAO();
		return loteDAO.LiberarLoteUuario(lote);
	}

	public List<ChequesDigitalizados> refrescarLote(LoteCheque lote, int secuencia, List<Integer> estados) throws RemoteException {
		List<ChequesDigitalizados> cheques = null;

		try {
			String where = " where ";
			if (lote != null) {
				where = (new StringBuilder(String.valueOf(where))).append(" idLote = ").append(lote.getIdUsuario()).toString();
				where = (new StringBuilder(String.valueOf(where))).append(" and tipocamara = ").append(lote.getTipoCamara()).toString();
				where = (new StringBuilder(String.valueOf(where))).append(" and poslote >").append(secuencia).toString();

				if (estados != null && estados.size() > 0) {
					where += " and estado in ( ";
					for (Integer i : estados)
						where += " " + i + ", ";
					where = where.trim();
					if (where.endsWith(","))
						where = where.substring(0, where.length() - 1);

					where += ")";
				}

				ChequesDigitalizadosDAO dao = new ChequesDigitalizadosDAO();
				cheques = dao.findAllWhere(where + " order by id");
			}

			return cheques;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public ChequesDigitalizados verificarStatusCuenta(ChequesDigitalizados cheque) throws RemoteException {
		if (cheque != null) {
			if (cheque.getTipoCamara() == AplicacionConstantes.CAMARA_ENTRANTE) {
				String estado = CacheServicioVerificarCuenta.getEstadoCuenta(cheque.getCheque().getCuenta().trim());
				try {
					cheque.setEstado(ApplicationParameters.getInstance().getInt("cuenta.estatus." + estado.trim().toUpperCase() + ".estado"));
					cheque.setCodigoMotivoDevolucion(ApplicationParameters.getInstance().getInt("cuenta.estatus." + estado.trim().toUpperCase() + ".motivo"));
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Error consultando cuenta");
				}
			} else {
				VerilectDAO dao = new VerilectDAO();
				boolean existe = dao.procesadoVerilect(cheque.getCheque().getSerial(), cheque.getCheque().getCuenta());
				String estado = (existe) ? "A" : "N";
				cheque.setEstado(ApplicationParameters.getInstance().getInt("cuenta.estatus." + estado.trim().toUpperCase() + ".estado"));
				cheque.setCodigoMotivoDevolucion(ApplicationParameters.getInstance().getInt("cuenta.estatus." + estado.trim().toUpperCase() + ".motivo"));
			}
		}
		return cheque;
	}

	public boolean actualizarEstadoChequeDigitalizado(ChequesDigitalizados cheque) throws RemoteException {
		// TODO Auto-generated method stub
		ChequesDigitalizadosDAO dao = new ChequesDigitalizadosDAO();
		return dao.actualizarEstado(cheque);

	}

	public LinkedHashMap<String, Integer> getChequesCertificadosParaBolsillo(int bolsillo) throws RemoteException {
		Lecto02DAO dao = new Lecto02DAO();
		return dao.getChequesCertificadosParaBolsillo(bolsillo);
	}

	@Override
	public boolean actualizarChequesCorregidos(List<ChequesDigitalizados> cheques, int entidad, int tipoCamara) throws RemoteException {
		ProcesarCamaraServices servicio = FactoryDAOService.obtenerRegistrarTransacciones(tipoCamara);
		boolean valido = false;
		if (servicio != null) {
			valido = servicio.actualizarTransaccionesCorregidas(cheques, entidad);
		} else
			System.out.println("el servicio es nulo");
		return valido;
	}

	@Override
	public List<Oficina> getOficinasByEntidad(int entidad) throws RemoteException {
		OficinaDAO dao = new OficinaDAO();
		List<Oficina> lista = dao.getAllByEntidad(entidad);
		return lista;
	}

	public LinkedHashMap<String, Integer> getCuentasRetornarCheques() throws RemoteException {
		Lecto09DAO dao = new Lecto09DAO();
		return dao.getCuentasRetornarCheques();
	}

	@Override
	public ChequesDigitalizados verificarLecto02(ChequesDigitalizados cheque) throws RemoteException {
		Lecto02DAO lecto = new Lecto02DAO();
		lecto.buscarChequeEnLecto02(cheque);
		String[] cuentasCaja = ApplicationParameters.getInstance().getString("correccion.cuentas.caja").split(",");
		if (cheque.getEstado() == ChequeDigitalizadoConstantes.CHEQUE_PENDIENTE_APROBAR) {
			boolean encontrado = false;
			for (int i = 0; i < cuentasCaja.length; i++) {
				if (cheque.getCheque().getCuenta().equalsIgnoreCase(cuentasCaja[i])) {
					encontrado = true;
					break;
				}
			}
			if (!encontrado) {
				cheque.setEstado(ChequeDigitalizadoConstantes.CHEQUE_NO_EXISTE);
			}
		}
		return cheque;
	}

	public int tomarLote(int proceso, int lote, int camara) {
		if (camara == AplicacionConstantes.CAMARA_INTERNA) {
			return (new LoteChequeDAO()).ejecutarProceso(proceso, lote, camara);
		} else {
			return 100;
		}
	}

}
