package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;

import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.applets.constantes.AplicacionConstantes;
import cr.tecnoware.sice.applets.constantes.ChequeDigitalizadoConstantes;
import cr.tecnoware.sice.applets.utils.Utils;
import cr.tecnoware.sice.context.ApplicationParameters;

public class Lecto02DAO extends DAO {

	public LinkedHashMap<String, Integer> getChequesCertificadosParaBolsillo(int bolsillo) {
		LinkedHashMap<String, Integer> mapa = new LinkedHashMap<String, Integer>();

		String query = "select l.cuenta,l.documento from custom_lecto02 l where l.cuenta not like '000000000000000'";
		Statement st;
		try {
			st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				String cuenta = Utils.fillZerosLeft(rs.getString(1), 15);
				String serial = Utils.fillZerosLeft(rs.getString(2), 8);
				mapa.put(cuenta + serial, bolsillo);
				// System.out.println("HAY CHEQUE CTA " + cuenta + " SERIAL " +
				// serial + " CLAVE " + cuenta + serial);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			super.closeConnection();
		}

		return mapa;

	}

	public void marcarRegistrosRecibidos(List<ChequesDigitalizados> transacciones) {
		try {
			int tipoChequeCertificado = ApplicationParameters.getInstance().getInt("cheque.certificado.tc");
			int tipoChequeGerencia = ApplicationParameters.getInstance().getInt("cheque.caja.tc");
			for (ChequesDigitalizados cheque : transacciones) {
				int tc = Integer.parseInt(cheque.getCheque().getTipoCheque());
				boolean actualizarCheque = ((tc == tipoChequeCertificado) || (tc == tipoChequeGerencia)) ? true : false;
				if (actualizarCheque) {
					try {
						String cuenta = (tc == tipoChequeGerencia) ? "000000000000000" : cheque.getCheque().getCuenta();
						String query = " Update custom_lecto02 set estado_cheque=" + AplicacionConstantes.VERILECT_VERIFICADO + " WHERE cuenta='" + cuenta + "' AND documento='" + cheque.getCheque().getSerial() + "'";
						Statement st = super.getConnection().createStatement();
						st.executeQuery(query);
					} catch (Exception e) {
						super.getLogger().error("Error actualiando LECTO02", e);
					} finally {
						super.closeConnection();
					}
				}
			}
		} catch (Exception e) {
			super.getLogger().error("Error actualizando LECTO02", e);
		}
	}

	public void marcarRegistrosLote() {
		try {
			/*
			 * int tipoChequeCertificado =
			 * ApplicationParameters.getInstance().getInt
			 * ("cheque.certificado.tc"); int tipoChequeGerencia =
			 * ApplicationParameters.getInstance().getInt("cheque.caja.tc");
			 * String sentenciaUno =
			 * "UPDATE custom_lecto02  set estado_cheque=3 ,id_cheque=(select id from cheque_entrante ch where custom_lecto02.documento = ch.serial_cheque AND ch.tipo_cheque="
			 * + tipoChequeGerencia + ")"; String sentenciaDos =
			 * "UPDATE cheque_entrante  set id_lecto02=(select id from custom_lecto02 lecto where cheque_entrante.serial_cheque=lecto.documento  AND cheque_entrante.tipo_cheque="
			 * + tipoChequeGerencia + ")"; String sentenciaTres =
			 * "UPDATE custom_lecto02  set estado_cheque=3 , id_cheque=(select id from cheque_entrante ch where custom_lecto02.documento = ch.serial_cheque AND custom_lecto02.cuenta=ch.cuenta_cheque AND ch.tipo_cheque="
			 * + tipoChequeCertificado + ")"; String sentenciaCuatro =
			 * "UPDATE cheque_entrante  set id_lecto02=(select id from custom_lecto02 lecto where cheque_entrante.serial_cheque = lecto.documento AND cheque_entrante.cuenta_cheque=lecto.cuenta AND cheque_entrante.tipo_cheque="
			 * + tipoChequeCertificado + ")"; try { Statement st =
			 * super.getConnection().createStatement();
			 * st.execute(sentenciaUno); st.execute(sentenciaDos);
			 * st.execute(sentenciaTres); st.execute(sentenciaCuatro); } catch
			 * (Exception e) {
			 * super.getLogger().error("Error actualiando LECTO02", e); }
			 * finally { super.closeConnection(); }
			 */
		} catch (Exception e) {
			super.getLogger().error("Error actualizando LECTO02", e);
		}
	}

	public void establecerTipoCheque(ChequesDigitalizados cheque) {
		try {
			String cuentaChequeCamara = "000000000000000";
			int tipoChequeCaja = ApplicationParameters.getInstance().getInt("cheque.caja.tc");
			String queryCertificado = " Select cuenta,documento,monto FROM custom_lecto02 WHERE cuenta='" + cheque.getCheque().getCuenta() + "' AND documento='" + cheque.getCheque().getSerial() + "'";
			String queryChequeCaja = " Select cuenta,documento,monto FROM custom_lecto02 WHERE cuenta='" + cuentaChequeCamara + "' AND documento='" + cheque.getCheque().getSerial() + "'";
			String sentenciaUpdate = "Update custom_lecto02 set estado_cheque =" + AplicacionConstantes.VERILECT_VERIFICADO + " WHERE ";
			String query = "";
			String tcCertificado = ApplicationParameters.getInstance().getString("cheque.certificado.tc");
			if (Integer.parseInt(cheque.getCheque().getTipoCheque()) != tipoChequeCaja) {
				sentenciaUpdate += "cuenta='" + cheque.getCheque().getCuenta() + "' AND documento='" + cheque.getCheque().getSerial() + "'";
				query = queryCertificado;
			} else {
				sentenciaUpdate += "cuenta='" + cuentaChequeCamara + "' AND documento='" + cheque.getCheque().getSerial() + "'";
				query = queryChequeCaja;
			}

			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				if (rs.getDouble("monto") == cheque.getCheque().getMonto().doubleValue()) {
					cheque.setChequeVerificado(true);

					if (Integer.parseInt(cheque.getCheque().getTipoCheque()) != tipoChequeCaja) {
						cheque.getCheque().setTipoCheque(tcCertificado);
					}
					System.out.println("sentencia lecto02 " + sentenciaUpdate);
					st.executeUpdate(sentenciaUpdate);

					if (Utils.removeLeadingZeros(cheque.getCheque().getTipoCheque()).equalsIgnoreCase(ApplicationParameters.getInstance().getString("cheque.caja.tc"))) {
						cheque.getCheque().setCodigoOficina(ApplicationParameters.getInstance().getString("oficina.cheque.caja"));
					}
				} else {
					if (!(Utils.removeLeadingZeros(cheque.getCheque().getTipoCheque()).equalsIgnoreCase(ApplicationParameters.getInstance().getString("cheque.caja.tc")))) {
						cheque.getCheque().setTipoCheque(tcCertificado);
					}else{
						cheque.getCheque().setCodigoOficina(ApplicationParameters.getInstance().getString("oficina.cheque.caja"));
					}
				}
			}
		} catch (Exception e) {
			super.getLogger().error("Error Verificando Cheque Certificado", e);
		} finally {
			super.closeConnection();
		}
	}

	public ChequesDigitalizados buscarChequeEnLecto02(ChequesDigitalizados cheque) {
		try {
			String query = "SELECT id,agencia,cuenta,documento,entidad_procesadora,estado_cheque,id_archivo_procesado,id_cheque,monto,tipo_cheque,fecha_proceso FROM custom_lecto02 where documento='" + Utils.fillZerosLeft(Utils.removeLeadingZeros(cheque.getCheque().getSerial()), 8) + "' ";
			query += " and cuenta='" + Utils.fillZerosLeft("0", 15) + "' and tipo_cheque='" + ApplicationParameters.getInstance().getInt("cheque.caja.tc") + "'";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			cheque.setEstado(ChequeDigitalizadoConstantes.CHEQUE_NO_EXISTE);
			if (rs.next()) {
				cheque.setEstado(ChequeDigitalizadoConstantes.CHEQUE_PENDIENTE_APROBAR);
			}
		} catch (Exception e) {
			cheque.setEstado(ChequeDigitalizadoConstantes.CHEQUE_PENDIENTE_CORRECCION);
			super.getLogger().error(e);
			e.printStackTrace();
		} finally {
			super.closeConnection();
		}
		return cheque;
	}
}
