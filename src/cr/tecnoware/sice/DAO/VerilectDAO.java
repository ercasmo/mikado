package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.applets.constantes.AplicacionConstantes;
import cr.tecnoware.sice.applets.utils.Utils;
import cr.tecnoware.sice.cache.CacheLoteVerificado;

public class VerilectDAO extends DAO {

	public boolean esChequeInterno(String serial, String cuenta) {
		boolean certificado = false;
		try {
			serial = Utils.fillZerosLeft(serial, AplicacionConstantes.LONGITUD_DOCUMENTO);
			cuenta = Utils.fillZerosLeft(cuenta, AplicacionConstantes.LONGITUD_CUENTA);
			String query = " Select cuenta,documento FROM custom_verilect WHERE cuenta='" + cuenta + "' AND documento='" + serial + "'";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next())
				certificado = true;
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
		} finally {
			super.closeConnection();
		}
		return certificado;
	}

	public boolean procesadoVerilect(String serial, String cuenta) {
		boolean certificado = false;
		try {
			serial = Utils.fillZerosLeft(serial, AplicacionConstantes.LONGITUD_DOCUMENTO);
			cuenta = Utils.fillZerosLeft(cuenta, AplicacionConstantes.LONGITUD_CUENTA);
			String query = " Select cuenta,documento FROM custom_verilect WHERE cuenta='" + cuenta + "' AND documento='" + serial + "'";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				certificado = true;
				String update = "Update custom_verilect set estado_cheque=" + AplicacionConstantes.VERILECT_VERIFICADO + " WHERE cuenta='" + cuenta + "' AND documento='" + serial + "'";
				st.executeUpdate(update);
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
		} finally {
			super.closeConnection();
		}
		return certificado;
	}

	public boolean procesadoVerilect(ChequesDigitalizados cheque) {
		boolean certificado = false;
		try {
			String serial = Utils.fillZerosLeft(cheque.getCheque().getSerial(), 8);
			String cuenta = Utils.fillZerosLeft(cheque.getCheque().getCuenta(), 15);
			Long fechaRegistros = obtenerFechaRegistros(cheque);

			String query = " Select cuenta,documento FROM custom_verilect WHERE cuenta='" + cuenta + "' AND documento='" + serial + "' AND (fecha_proceso BETWEEN'" + obtenerLimiteInferiorConsulta(fechaRegistros.longValue()) + "' AND '" + obtenerLimiteSuperiorConsulta(fechaRegistros.longValue()) + "')";
			System.out.println(query);
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				certificado = true;
				String update = "Update custom_verilect set estado_cheque=3 WHERE cuenta='" + cuenta + "' AND documento='" + serial + "' AND (fecha_proceso BETWEEN'" + obtenerLimiteInferiorConsulta(fechaRegistros.longValue()) + "' AND '" + obtenerLimiteSuperiorConsulta(fechaRegistros.longValue()) + "')";
				st.executeUpdate(update);
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
		} finally {
			super.closeConnection();
		}
		return certificado;
	}

	public void marcarRegistrosRecibidos(List<ChequesDigitalizados> transacciones) {
		for (ChequesDigitalizados cheque : transacciones) {
			try {
				String query = " Update custom_verilect set estado_cheque=" + AplicacionConstantes.VERILECT_VERIFICADO + " WHERE cuenta='" + cheque.getCheque().getCuenta() + "' AND documento='" + cheque.getCheque().getSerial() + "'";
				Statement st = super.getConnection().createStatement();
				st.executeQuery(query);
			} catch (Exception e) {
				super.getLogger().error("Error actualiando VERILECT", e);
			} finally {
				super.closeConnection();
			}
		}
	}

	private Long obtenerFechaRegistros(ChequesDigitalizados cheque) {
		Long fechaRegistro = null;
		String camara = Utils.fillZerosLeft("3", 2);
		String fechaLote = Utils.FormatDateToStringToFormat(cheque.getFecha().getTime(), "yyyyMMdd");
		String lote = Utils.fillZerosLeft(new StringBuilder(cheque.getIdLote()).toString(), 4);
		String llaveBusqueda = camara + "-" + lote + "-" + fechaLote;
		fechaRegistro = CacheLoteVerificado.obtenerFechaRegistros(llaveBusqueda);
		if (fechaRegistro == null) {
			fechaRegistro = consultarFechaLoteRegistro(cheque.getIdLote(), 3);
		}
		if (fechaRegistro != null) {
			CacheLoteVerificado.agregarFechaRegistros(llaveBusqueda, fechaRegistro);
		}
		return fechaRegistro;
	}

	public Long consultarFechaLoteRegistro(int numLote, int tipoCamara) {
		Long fechaRegistro = null;
		try {
			java.sql.Date c = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
			Statement st = super.getConnection().createStatement();
			String query = " SELECT fecha_registros FROM lote_cheque WHERE id_usuario = " + numLote + " AND tipo_camara=" + tipoCamara;
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				Timestamp fechaRegistros = rs.getTimestamp("fecha_registros");
				if (fechaRegistros != null) {
					c.setTime(fechaRegistros.getTime());
				}
			}
			fechaRegistro = Long.valueOf(c.getTime());
		} catch (Exception e) {
			super.getLogger().error("Error consultado Lote ", e);
		} finally {
			super.closeConnection();
		}
		return fechaRegistro;
	}

	public String obtenerLimiteSuperiorConsulta(long representacionFecha) {
		String fechaFormateada = "";
		GregorianCalendar fechaTemporal2 = new GregorianCalendar();
		fechaTemporal2.setTimeInMillis(representacionFecha);
		fechaTemporal2.set(11, 23);
		fechaTemporal2.set(12, 59);
		fechaTemporal2.set(13, 59);
		fechaTemporal2.set(14, 0);
		fechaFormateada = getStringFromDate(fechaTemporal2.getTime(), "yyyyMMdd HH:mm:ss");
		return fechaFormateada;
	}

	public String obtenerLimiteInferiorConsulta(long representacionFecha) {
		String fechaFormateada = "";
		GregorianCalendar fechaTemporal2 = new GregorianCalendar();
		fechaTemporal2.setTimeInMillis(representacionFecha);
		fechaTemporal2.set(11, 0);
		fechaTemporal2.set(12, 0);
		fechaTemporal2.set(13, 0);
		fechaTemporal2.set(14, 0);
		fechaFormateada = getStringFromDate(fechaTemporal2.getTime(), "yyyyMMdd HH:mm:ss");
		return fechaFormateada;
	}

	public String getStringFromDate(java.util.Date fecha, String patron) {
		SimpleDateFormat sdf = new SimpleDateFormat(patron);
		return sdf.format(fecha);
	}
}
