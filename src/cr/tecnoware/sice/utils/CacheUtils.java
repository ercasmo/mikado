package cr.tecnoware.sice.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileTransferClient;

import cr.tecnoware.sice.DAO.ConexionRemotaDAO;
import cr.tecnoware.sice.DAO.EntidadDAO;
import cr.tecnoware.sice.DAO.OficinaDAO;
import cr.tecnoware.sice.DAO.TipoDocumentoDAO;
import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.applets.bean.Entidad;
import cr.tecnoware.sice.applets.bean.Oficina;
import cr.tecnoware.sice.applets.bean.TipoDocumento;
import cr.tecnoware.sice.applets.constantes.AplicacionConstantes;
import cr.tecnoware.sice.utils.encryption.CryptoUtils;


public class CacheUtils {

	private static Map<String, Oficina> oficina;
	private static Map<String, Entidad> banco;
	private static Map<String, FileTransferClient> conexion;
	private static Map<String, ConexionRemotaBean> mapaConexiones;
	private static Map<Integer, TipoDocumento> tcs;
	private static List<TipoDocumento>tcList;

	static {
		oficina = new LinkedHashMap<String, Oficina>();
		banco = new LinkedHashMap<String, Entidad>();
		mapaConexiones = new LinkedHashMap<String, ConexionRemotaBean>();
        tcs=new HashMap<Integer, TipoDocumento>();
		OficinaDAO daoOficina = new OficinaDAO();

		List<Oficina> listaOficinas = daoOficina.getAll();
		if(listaOficinas != null)
			for (int i = 0; i < listaOficinas.size(); i++)
				oficina.put(Utils.fillZerosLeft(listaOficinas.get(i).getCodBanco() + "", 4) + "" + Utils.fillZerosLeft(listaOficinas.get(i).getCodOficina() + "", 4), listaOficinas.get(i));
			

		EntidadDAO daoEntidad = new EntidadDAO();
		List<Entidad> listaEntidades = daoEntidad.getAll();
		for (int i = 0; i < listaEntidades.size(); i++)
			banco.put(Utils.fillZerosLeft(listaEntidades.get(i).getCodigo() + "", 4), listaEntidades.get(i));
		
		TipoDocumentoDAO td=new TipoDocumentoDAO();
		tcList=td.getALL();
		
		if(tcList!=null)
		{
			for(TipoDocumento t:tcList)
				tcs.put(t.getTipoDocumento(), t);
		}else 
		{
			System.out.println(" no hay TC's  definidos ");
		}
	}

	public static Oficina getOficina(String banco, String codigo) {
		return oficina.get(Utils.fillZerosLeft(banco, 4) + Utils.fillZerosLeft(codigo, 4));
	}

	public static Entidad getBanco(String codigo) {
		return banco.get(codigo);
	}

	public static FileTransferClient getConexionFTP(String banco, int tipoConexion) {
		String clave = Utils.fillZerosLeft(banco, 4) + "" + Utils.fillZerosLeft(tipoConexion + "", 2);

		if (conexion == null) {
			conexion = new LinkedHashMap<String, FileTransferClient>();
		}

		if (conexion.get(clave) == null) {
			FileTransferClient clienteFTP = null;
			ConexionRemotaBean conexionRemota = null;
			try {
				// Se extrae la conexion asociada.
				ConexionRemotaDAO conexionRemotaDAO = new ConexionRemotaDAO();
				EntidadDAO entidadDao = new EntidadDAO();
				Entidad entidad = entidadDao.getByCodigo(Integer.parseInt(banco));
				int codigoEntidadProcesadora = (entidad.getEntidadMatriz() != null && entidad.getEntidadMatriz() != 0) ? entidad.getEntidadMatriz() : entidad.getCodigo();
				conexionRemota = conexionRemotaDAO.getConexioRemota(tipoConexion, codigoEntidadProcesadora);
				if (null != conexionRemota) {
					clienteFTP = new FileTransferClient();
					clienteFTP.setUserName(conexionRemota.getUsuario());
					clienteFTP.setPassword(conexionRemota.getClave());
					clienteFTP.setRemoteHost(conexionRemota.getDireccionIp());
					clienteFTP.setRemotePort(conexionRemota.getPuerto());
					clienteFTP.setContentType(FTPTransferType.BINARY);
				} else
					return null;

			} catch (Exception e) {
				System.out.println("Error obteniendo conexion FTP para la entidad  " + banco + " Tipo: " + tipoConexion);
				e.printStackTrace();
				return null;
			}
			conexion.put(clave, clienteFTP);
		}

		return conexion.get(clave);
	}

	// --------------------Cambios E.A.G.P------------------------
	// <nuevo>
	public static ConexionRemotaBean getConexionRemota(String banco, int tipoConexion)
	{
		String clave = Utils.fillZerosLeft(banco, 4) + "" + Utils.fillZerosLeft(tipoConexion + "", 2);						
		ConexionRemotaBean conex =null;
	
		if(!mapaConexiones.containsKey(clave)){//Se busca en Base de datos.		
			Entidad entidad = (new EntidadDAO()).getByCodigo(Integer.parseInt(banco)); 
			int codigoEntidadProcesadora = entidad.getCodigo();			
			
			ConexionRemotaDAO conexionRemotaDAO = new ConexionRemotaDAO();
			conex = conexionRemotaDAO.getConexioRemota(tipoConexion, codigoEntidadProcesadora);
			mapaConexiones.put(clave, conex);
		}
		
		return mapaConexiones.get(clave);
	}
	
    public static   ConexionRemotaBean descifrarDatosDeConexion(ConexionRemotaBean conexionInstance)
    {
        if (conexionInstance.getUsuario() != null && conexionInstance.getClave() != null && conexionInstance.getDireccionIp() != null)
        {
            if (!conexionInstance.getUsuario().equalsIgnoreCase("") && !conexionInstance.getClave().equalsIgnoreCase("") && !conexionInstance.getDireccionIp().equalsIgnoreCase(""))
            {
                conexionInstance.setUsuario(CryptoUtils.decodificarCadenaBaseDatos(conexionInstance.getUsuario()));
                conexionInstance.setClave(CryptoUtils.decodificarCadenaBaseDatos(conexionInstance.getClave()));
                conexionInstance.setDireccionIp(CryptoUtils.decodificarCadenaBaseDatos(conexionInstance.getDireccionIp()));
            }
        }        return conexionInstance;
    }
    
    public static void setTcList(List<TipoDocumento> tcList) {
		CacheUtils.tcList = tcList;
	}

	public static List<TipoDocumento> getTcList() {
		return tcList;
	}

	public static void setTcs(Map<Integer, TipoDocumento> tcs) {
		CacheUtils.tcs = tcs;
	}

	public static Map<Integer, TipoDocumento> getTcs() {
		return tcs;
	}
	
	public static int obtenerConexionByCamara(int tipoCamara){
		int tipoConexion=-1;
		switch(tipoCamara){
		case AplicacionConstantes.CAMARA_ENTRANTE:
			tipoConexion=AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_ENTRANTE;
			break;
		case AplicacionConstantes.CAMARA_EXTERNA:
			tipoConexion=AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_EXTERNA;
			break;
		case AplicacionConstantes.CAMARA_SALIENTE:
			tipoConexion=AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_SALIENTE;
			break;
		case AplicacionConstantes.CAMARA_INTERNA:
			tipoConexion=AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_INTERNA;
			break;
		}
		return tipoConexion;
	}
}
