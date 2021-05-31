package cr.tecnoware.sice.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import cr.tecnoware.sice.applets.bean.TipoDocumento;
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
import cr.tecnoware.sice.applets.bean.RegistroMagnetizacion;
import cr.tecnoware.sice.applets.bean.TransaccionEntrante;
import cr.tecnoware.sice.applets.bean.TransaccionInterna;
import cr.tecnoware.sice.applets.bean.TransaccionSaliente;

public interface RemoteServices extends Remote, Serializable {

	public ImageSignature getImage(String bancoReceptor, String anho, String mes, String dia, String bancoEmisor, String refImagen) throws RemoteException;

	public List<ImageSignature> getGroupImages(String[] bancoReceptor, String[] anho, String[] mes, String[] dia, String[] bancoEmisor, String[] refImagen) throws RemoteException;

	public ImageSignature getImageSaliente(String bancoReceptor, String anho, String mes, String dia, String bancoEmisor, String refImagen) throws RemoteException;

	public List<ImageSignature> getGroupImagesSalientes(String[] bancoReceptor, String[] anho, String[] mes, String[] dia, String[] bancoEmisor, String[] refImagen) throws RemoteException;

	public ImageSignature getImageRevision(String bancoReceptor, String anho, String mes, String dia, String bancoEmisor, String refImagen, String cuentaCliente, int firmas) throws RemoteException;

	public List<Firma> getFirmas(String cuenta) throws RemoteException;

	public List<ImageSignature> getGroupImagesRevision(String[] bancoReceptor, String[] anho, String[] mes, String[] dia, String[] bancoEmisor, String[] refImagen, String[] cuentaCliente) throws RemoteException;

	public List<Entidad> getEntidades() throws RemoteException;

	public List<Entidad> getEntidadesProcesadoras(String user) throws RemoteException;

	public List<Oficina> getOficinas() throws RemoteException;

	public List<MotivoDevolucion> getMotivosDevolucion() throws RemoteException;

	public Map<Integer, Integer> setDigitalizacion(List<Digitalizado> listadoDigitalizacion, String entidadProcesadora, String usuario, String ip, int tipoCamara) throws RemoteException;

	public int setDocumento(TransaccionEntrante transaccion, byte[] frente, byte[] reverso, byte[] uv, String usuario, String direccion) throws RemoteException;

	public int setDocumento(TransaccionEntrante transaccion, byte[] frente, byte[] reverso, byte[] uv, String usuario, String direccion, String referenciaLote) throws RemoteException;

	public int setDocumentoSaliente(TransaccionSaliente transaccion, int estado, String usuario, String direccion) throws RemoteException;

	/**
	 * @param usuario
	 *            Usuario de sice para revision de permisos
	 * @return
	 */

	public List<TransaccionEntrante> getTransaccionesRevisionFirmas(String usuario) throws RemoteException;

	public boolean actualizarListadoTransacciones(List<TransaccionEntrante> lista, String usuario, String ip) throws RemoteException;

	/**
	 * @param cuentaCliente
	 *            Cuenta de 20 digitos del cheque.
	 * @param documento
	 *            Documento asociado al cheque.
	 * @param tc
	 *            Tipo de cheque.
	 * @return Registro de Base de datos con la imagen y el monto para
	 *         magnetizar.
	 * @throws RemoteException
	 */
	public RegistroMagnetizacion getRegistroMagnetizar(String cuentaCliente, String documento, String tc) throws RemoteException;

	public List<TransaccionSaliente> getTransaccionesMagnetizar(int entidadProcesadora) throws RemoteException;

	public List<TransaccionEntrante> getTransaccionesDevueltaEntrante(int entidadProcesadora, Date fecha) throws RemoteException;

	public List<TransaccionSaliente> getTransaccionesDevueltaSaliente(int entidadProcesadora, Date fecha) throws RemoteException;

	public byte[] generarReportePDF(int tipoReporte, List<Object> listado) throws RemoteException;

	public byte[] generarImagenesPDF(Object reporteImagen) throws RemoteException;

	public List<TransaccionEntrante> getTransaccionesDigitalizarEntrante(int entidadProcesadora) throws RemoteException;

	public List<TransaccionEntrante> getTransaccionesDigitalizarEntrante2(int entidadProcesadora, ArrayList<Integer> entidades) throws RemoteException;

	public Map<Integer, Integer> setDigitalizacionSaliente(List<DigitalizadoSaliente> listadoDigitalizacion, String entidadProcesadora, String usuario, String ip) throws RemoteException;

	public int setDocumentoDigitalizarSaliente(TransaccionSaliente transaccion, byte[] frente, byte[] reverso, byte[] uv, String usuario, String direccion) throws RemoteException;

	// Funciones para el proceso de archivar

	public List<TransaccionEntrante> getTransaccionesEntrantes(int entidadProcesadora, Date fecha) throws RemoteException;

	// public List<TransaccionSaliente> getTransaccionesSalientes(int
	// entidadProcesadora, Date fecha) throws RemoteException;

	public String getReferenciaCaja(Date fechaProceso) throws RemoteException;

	public int setDataArchivador(Map<Integer, LinkedList<TransaccionEntrante>> transaccionesClasificadas, Date fecha) throws RemoteException;

	// public int
	// setDataArchivadorSaliente(Map<Integer,LinkedList<TransaccionSaliente>>
	// transaccionesClasificadas, Date fecha) throws RemoteException;

	public List<BolsilloClasificacion> getBolsilloClasificacion(int tipo, int codigoEntidadProcesadora) throws RemoteException;

	public List<LoteCheque> obtenerLotes(int estado, int codigoEntidadProcesadora, int tipoCamara) throws RemoteException;

	public int actLote(LoteCheque lote) throws RemoteException;

	// se trae la transacciones por corregir de un lote ,@lote lote,@estados
	// estados a incluir,lista vacia o nulla todo los estados
	public List<ChequesDigitalizados> getTransaccionesByCorreccionByLote(LoteCheque lote, List<Integer> estados) throws RemoteException;

	public LoteCheque obtenerLote(int id) throws RemoteException;

	// Trae list ade cheques con estado maracod como incosistente con archivo
	// verilect
	public List<TransaccionInterna> getTransaccionesInconsistenciasVerilect(Date fecha) throws RemoteException;

	public ConexionRemotaBean getConexionRemota(int tipoConexion, int codigoEntidadProcesadora) throws RemoteException;

	public ChequesDigitalizados getCuentaDepositaria(ChequesDigitalizados cheque) throws RemoteException;

	public boolean registrarChequesCorregidos(List<ChequesDigitalizados> cheques, int entidad, int tipoCamara) throws RemoteException;

	public List<TipoDocumento> getTiposDocumentos() throws RemoteException;

	public boolean liberarTransacciones(List<ChequesDigitalizados> cheques) throws RemoteException;

	public boolean asignarLoteUsuario(LoteCheque lote, String usuario) throws RemoteException;

	public boolean liberarLoteUsuario(LoteCheque lote) throws RemoteException;

	public List<ChequesDigitalizados> refrescarLote(LoteCheque lote, int secuencia, List<Integer> estados) throws RemoteException;

	public ChequesDigitalizados verificarStatusCuenta(ChequesDigitalizados cheque) throws RemoteException;

	public boolean actualizarEstadoChequeDigitalizado(ChequesDigitalizados cheque) throws RemoteException;

	public LinkedHashMap<String, Integer> getChequesCertificadosParaBolsillo(int bolsillo) throws RemoteException;

	public boolean actualizarChequesCorregidos(List<ChequesDigitalizados> cheques, int entidad, int tipoCamara) throws RemoteException;

	public List<Oficina> getOficinasByEntidad(int entidad) throws RemoteException;

	public LinkedHashMap<String, Integer> getCuentasRetornarCheques() throws RemoteException;

	public List<LoteCheque> obtenerLotesPorEstado(int estado, int codigoEntidadProcesadora, int tipoCamara) throws RemoteException;

	public ChequesDigitalizados verificarLecto02(ChequesDigitalizados cheque) throws RemoteException;

	public abstract int tomarLote(int i, int j, int k) throws RemoteException;
	
}
