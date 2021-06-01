package cr.tecnoware.sice.services.refactor;


import java.util.List;

import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.applets.bean.Entidad;
import cr.tecnoware.sice.applets.bean.MotivoDevolucion;
import cr.tecnoware.sice.applets.bean.Oficina;

public interface CatalogoService {
	public List<Entidad> getEntidades() throws Exception;

	public List<Entidad> getEntidadesProcesadoras(String user) throws  Exception;

	public List<Oficina> getOficinas() throws  Exception;
	
	public List<MotivoDevolucion> getMotivosDevolucion() throws Exception;
	
	public ConexionRemotaBean getConexionRemota(int tipoConexion, int codigoEntidadProcesadora) throws Exception;

}
