package cr.tecnoware.sice.services;

import java.util.ArrayList;

import java.util.Collections;

import org.apache.log4j.Logger;

import cr.tecnoware.sice.DAO.DAO;
import cr.tecnoware.sice.DAO.ParametrosGeneralesDAO;
import cr.tecnoware.sice.DAO.UsuarioRevisorDAO;
import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.applets.bean.TipoDocumento;
import cr.tecnoware.sice.applets.bean.UsuarioRevisor;
import cr.tecnoware.sice.context.ApplicationParameters;
import cr.tecnoware.sice.utils.CacheUtils;

public class AsignarFirmasService {
	private static Logger log = Logger.getLogger("application");

	public void asignarFirmas(ArrayList<ChequesDigitalizados> listaDigitalizados) {

		ArrayList<UsuarioRevisor> listaUsuariosRevisores = (new UsuarioRevisorDAO()).obtenerUsuariosRevisores();
		double montoRiesgo = ((new ParametrosGeneralesDAO()).obtenerParametrosSistema()).getMontoRiesgo();

		int certificadoTC = ApplicationParameters.getInstance().getInt("cheque.certificado.tc");
		int cajaTC = ApplicationParameters.getInstance().getInt("cheque.caja.tc");

		for (ChequesDigitalizados cheque : listaDigitalizados) {
			TipoDocumento tc = CacheUtils.getTcs().get(Integer.parseInt(cheque.getCheque().getTipoCheque().trim()));
			if (tc != null && !tc.isBoleta() && tc.getTipoDocumento() != certificadoTC && tc.getTipoDocumento() != cajaTC) {
				if (cheque.getCheque().getMonto() > montoRiesgo) {
					ArrayList<UsuarioRevisor> listaPosiblesRevisores = obtenerRevisores(listaUsuariosRevisores, cheque);
					if (listaPosiblesRevisores.size() > 0) {
						Collections.sort(listaPosiblesRevisores);
						cheque.setUsuarioRevisor(listaPosiblesRevisores.get(0).getLogin());
						listaPosiblesRevisores.get(0).aumentarFirmasAsignadas();
					}else
					{
						log.warn("Transaccion Serial "+cheque.getCheque().getSerial()+" Monto "+cheque.getCheque().getMonto()+" TC "+cheque.getCheque().getTipoCheque()+" No fue asignada por falta de Usuario Revisor");
					}

				}
			}
		}
	}

	private ArrayList<UsuarioRevisor> obtenerRevisores(ArrayList<UsuarioRevisor> listaRevisores, ChequesDigitalizados cheque) {
		ArrayList<UsuarioRevisor> listaDepurada = new ArrayList<UsuarioRevisor>();
		for (UsuarioRevisor usuarioRevisor : listaRevisores) {
			if (usuarioRevisor.analizarRevisarCheque(cheque)) {
				listaDepurada.add(usuarioRevisor);
			}
		}
		return listaDepurada;
	}
}
