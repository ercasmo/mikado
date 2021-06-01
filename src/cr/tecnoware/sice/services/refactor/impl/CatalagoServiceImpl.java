package cr.tecnoware.sice.services.refactor.impl;

import java.util.List;

import cr.tecnoware.sice.DAO.EntidadDAO;
import cr.tecnoware.sice.DAO.OficinaDAO;
import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.applets.bean.Entidad;
import cr.tecnoware.sice.applets.bean.MotivoDevolucion;
import cr.tecnoware.sice.applets.bean.Oficina;
import cr.tecnoware.sice.services.refactor.CatalogoService;

public class CatalagoServiceImpl implements CatalogoService {
	
	public List<Entidad> getEntidades() {
		EntidadDAO dao = new EntidadDAO();
		List<Entidad> lista = dao.getAll();
		return lista;
	}
	
	public List<Oficina> getOficinas() {
		OficinaDAO dao = new OficinaDAO();
		List<Oficina> lista = dao.getAll();
		return lista;
	}

	@Override
	public List<Entidad> getEntidadesProcesadoras(String user) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MotivoDevolucion> getMotivosDevolucion() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConexionRemotaBean getConexionRemota(int tipoConexion, int codigoEntidadProcesadora) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
