package cr.tecnoware.sice.services;

import java.sql.Timestamp;

import cr.tecnoware.sice.DAO.AuditoriaTransaccionDAO;
import cr.tecnoware.sice.applets.bean.AuditoriaChequesDigitalizados;
import cr.tecnoware.sice.applets.bean.AuditoriaTransaccionEntrante;
import cr.tecnoware.sice.applets.bean.AuditoriaTransaccionSaliente;
import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.applets.bean.TransaccionEntrante;
import cr.tecnoware.sice.applets.bean.TransaccionSaliente;
import cr.tecnoware.sice.utils.Utils;

public class AuditoriaTransaccionService {

	public static void registrarAuditoria(TransaccionEntrante te,
			String address, int evento, Timestamp fecha, String usuario) {
		try {
			AuditoriaTransaccionEntrante transaccion = new AuditoriaTransaccionEntrante();
			transaccion.setAddress(address);
			transaccion.setCuenta(te.getCuenta());
			transaccion.setEvento(evento);
			transaccion.setFecha(fecha);
			transaccion.setSerial(te.getSerial());
			transaccion.setUsuario(usuario);
			transaccion.setId(te.getId());

			AuditoriaTransaccionDAO dao = new AuditoriaTransaccionDAO();
			dao.save(transaccion);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void registrarAuditoria(TransaccionSaliente te,
			String address, int evento, Timestamp fecha, String usuario) {
		try {
			

			AuditoriaTransaccionSaliente transaccion = new AuditoriaTransaccionSaliente();
			transaccion.setAddress(address);
			transaccion.setCuenta(te.getCuenta());
			transaccion.setEvento(evento);
			transaccion.setFecha(fecha);
			transaccion.setSerial(te.getSerial());
			transaccion.setUsuario(usuario);
			transaccion.setId(te.getId());

			AuditoriaTransaccionDAO dao = new AuditoriaTransaccionDAO();

			dao.save(transaccion);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void registrarAuditoria(ChequesDigitalizados te,
			String address, int evento, Timestamp fecha, String usuario) {
		try {

			AuditoriaChequesDigitalizados transaccion = new AuditoriaChequesDigitalizados();
			transaccion.setAccion(evento);
			transaccion.setFecha(fecha);
			transaccion.setUsuario(usuario);
			transaccion.setId(te.getId());
			transaccion.setIdCheque(te.getId());
			transaccion.setEstado(te.getEstado());
			transaccion.setAddress(address);
			AuditoriaTransaccionDAO dao = new AuditoriaTransaccionDAO();

			dao.save(transaccion);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
