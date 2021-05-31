package cr.tecnoware.sice.services;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import cr.tecnoware.sice.DAO.TransaccionEntranteDAO;
import cr.tecnoware.sice.applets.bean.TransaccionEntrante;

public class ProcesoRespaldoService {
	public static final int TAMANO_INSUFICIENTE = 0;
	public static final int TAMANO_JUSTO = 1;
	public static final int TAMANO_EXCEDE = 2;
	private static Logger log = Logger.getLogger("application");

	public void iniciarProcesoRespaldo(int codigoEntidadProcesadora) {
		try {
			// Se respaldan las transacciones Entrantes.
			String sentenciaWhereEntrante = " WHERE id_respaldo_id=null AND codigo_entidad_procesadora="+codigoEntidadProcesadora;			
            ArrayList<TransaccionEntrante> listaTransacciones=null;
            listaTransacciones= (ArrayList<TransaccionEntrante>)(new TransaccionEntranteDAO()).findAllByQueryHistorico(sentenciaWhereEntrante);
            System.out.println(listaTransacciones.size());
            
            
		} catch (Exception e) {

		}
	}

	/*
	 * def iniciarProcesoRespaldo(def entidadProcesadora){ def
	 * listaTransaccionesEntrantes =[] def listaTransaccionesSalientes =[] def
	 * listaTransaccionesInternas =[] def listaTransaccionesExternas = [] def
	 * listaTransaccionesRespaldar = []
	 * 
	 * //Paso 1. Se obtienen las transacciones de Base de datos que no se han
	 * respaldado. def chequeService = new ChequeService(); def
	 * parametrosQuery=[:] def paginacion=[:]
	 * parametrosQuery.put('repaldoCheques',true) listaTransaccionesEntrantes =
	 * chequeService
	 * .consultarCheques(CamaraConstantes.CAMARA_ENTRANTE,CamaraConstantes
	 * .PERIODO_HISTORICO,paginacion,parametrosQuery,false)
	 * listaTransaccionesSalientes =
	 * chequeService.consultarCheques(CamaraConstantes
	 * .CAMARA_SALIENTE,CamaraConstantes
	 * .PERIODO_HISTORICO,paginacion,parametrosQuery,false)
	 * listaTransaccionesInternas =
	 * chequeService.consultarCheques(CamaraConstantes
	 * .CAMARA_INTERNA,CamaraConstantes
	 * .PERIODO_HISTORICO,paginacion,parametrosQuery,false)
	 * listaTransaccionesExternas =
	 * chequeService.consultarCheques(CamaraConstantes
	 * .CAMARA_EXTERNA,CamaraConstantes
	 * .PERIODO_HISTORICO,paginacion,parametrosQuery,false)
	 * listaTransaccionesRespaldar =
	 * obtenerTransaccionesRespaldo(listaTransaccionesEntrantes) //
	 * listaTransaccionesRespaldar =
	 * obtenerTransaccionesRespaldo(listaTransaccionesSalientes) //
	 * listaTransaccionesRespaldar =
	 * obtenerTransaccionesRespaldo(listaTransaccionesInternas) //
	 * listaTransaccionesRespaldar =
	 * obtenerTransaccionesRespaldo(listaTransaccionesExternas)
	 * 
	 * LoggerUtils.info("Transacciones Entrantes a Respaldar: "+
	 * listaTransaccionesRespaldar.size())
	 * if(listaTransaccionesRespaldar.size()>0){ def
	 * nombreDirectorioRespaldo=generarNombreCarpetaRespaldo
	 * (CamaraConstantes.CAMARA_ENTRANTE)
	 * respaldarImagenes(entidadProcesadora,listaTransaccionesRespaldar
	 * ,nombreDirectorioRespaldo,CamaraConstantes.CAMARA_ENTRANTE)
	 * 
	 * }
	 * 
	 * }
	 */

}
