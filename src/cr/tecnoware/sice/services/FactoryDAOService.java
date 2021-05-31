package cr.tecnoware.sice.services;

import cr.tecnoware.sice.DAO.TransaccionEntranteDAO;
import cr.tecnoware.sice.DAO.TransaccionExternaDAO;
import cr.tecnoware.sice.DAO.TransaccionInternaDAO;
import cr.tecnoware.sice.DAO.TransaccionSalienteDAO;
import cr.tecnoware.sice.applets.constantes.AplicacionConstantes;
import cr.tecnoware.sice.interfaz.ProcesarCamaraServices;

public class FactoryDAOService {
	
	public static ProcesarCamaraServices obtenerRegistrarTransacciones(int camara)
	{
		ProcesarCamaraServices interfaz=null;
		switch(camara){
		case AplicacionConstantes.CAMARA_SALIENTE:
			interfaz=new TransaccionSalienteDAO();
			break;
		case AplicacionConstantes.CAMARA_ENTRANTE:
			interfaz=new TransaccionEntranteDAO();
			break;
		case AplicacionConstantes.CAMARA_EXTERNA:
			interfaz=new TransaccionExternaDAO();
			break;
		case AplicacionConstantes.CAMARA_INTERNA:
			interfaz=new TransaccionInternaDAO();
			break;
		}
		return interfaz;
	}

}
