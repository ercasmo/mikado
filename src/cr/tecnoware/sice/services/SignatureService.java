package cr.tecnoware.sice.services;

import java.util.ArrayList;
import java.util.List;


import cr.tecnoware.sice.applets.bean.Firma;
import cr.tecnoware.sice.core.ServicioComunicacionCore;
import cr.tecnoware.sice.core.bean.ConsultaFirmaResponse;

public class SignatureService {

	public List<Firma> getSignatures(String cuentaCliente) {
		List<Firma> listaFirmas = new ArrayList<Firma>();
		ConsultaFirmaResponse consultarFirmasResponse = new ConsultaFirmaResponse();
		consultarFirmasResponse = ServicioComunicacionCore.consultarFirmas(cuentaCliente);
		if (null != consultarFirmasResponse) {
			for (int i = 0; i < consultarFirmasResponse.getListaFirmas().size(); i++) {
                 Firma firma = consultarFirmasResponse.getListaFirmas().get(i);              
			     listaFirmas.add(firma);
			}
		}

		return listaFirmas;
	}

}
