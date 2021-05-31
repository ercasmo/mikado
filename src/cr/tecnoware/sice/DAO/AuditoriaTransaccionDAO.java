package cr.tecnoware.sice.DAO;

import java.sql.Statement;
import java.util.ArrayList;

import cr.tecnoware.sice.applets.bean.AuditoriaChequeEntrante;
import cr.tecnoware.sice.applets.bean.AuditoriaChequeExterno;
import cr.tecnoware.sice.applets.bean.AuditoriaChequeInterno;
import cr.tecnoware.sice.applets.bean.AuditoriaChequeSaliente;
import cr.tecnoware.sice.applets.bean.AuditoriaChequesDigitalizados;
import cr.tecnoware.sice.applets.bean.AuditoriaTransaccionEntrante;
import cr.tecnoware.sice.applets.bean.AuditoriaTransaccionSaliente;
import cr.tecnoware.sice.utils.Utils;

public class AuditoriaTransaccionDAO extends DAO {

	public AuditoriaTransaccionDAO() {
		super();
	}

	public int save(AuditoriaTransaccionEntrante audit) {
		int codigoResultado = -1;
		try {
			String query = "INSERT INTO seguridad_auditoria_cheque_entrante (accion,fecha,id_transaccion,ip,usuario,descripcion,cuenta,serial)" + " values ( "+audit.getEvento() + "," + "'" + Utils.FormatDateToStringToFormat(audit.getFecha(),"yyyyMMdd HH:MM:s") + "'," + "" + audit.getId() + "," + "'" + audit.getAddress() + "'," + "'" + audit.getUsuario() + "'," + "'','" + audit.getCuenta() + "','" + audit.getSerial() + "')";

			Statement st = super.getConnection().createStatement();
			st.executeUpdate(query);

			codigoResultado = 1;
		} catch (Exception e) {
			super.getLogger().error(e);
			codigoResultado = 0;
		} finally {
			super.closeConnection();
		}
		return codigoResultado;
	}

	public int save(AuditoriaTransaccionSaliente audit) {
		int codigoResultado = -1;
		try {
			String query = "INSERT INTO seguridad_auditoria_cheque_saliente (accion,fecha,id_transaccion,ip,usuario,descripcion)" + " values ("+ audit.getEvento() + "," + "'" + audit.getFecha() + "'," + "" + audit.getId() + "," + "'" + audit.getAddress() + "'," + "'" + audit.getUsuario() + "'," + "'')";

			Statement st = super.getConnection().createStatement();

			st.executeUpdate(query);

			codigoResultado = 1;
		} catch (Exception e) {
			super.getLogger().error(e);
			codigoResultado = 0;
		} finally {
			super.closeConnection();
		}
		return codigoResultado;
	}

	public int save(AuditoriaChequesDigitalizados audit) {
		int codigoResultado = -1;
		try {

			String query = "INSERT INTO seguridad_auditoria_cheque_digitalizado (accion,fecha,id_Cheque,addres,usuario,estado)" + " values ("  + audit.getAccion() + "," + "'" + audit.getFecha() + "'," + "" + audit.getIdCheque() + "," + "'" + audit.getAddress() + "'," + "'" + audit.getUsuario() + "'," + audit.getEstado() + ")";

			Statement st = super.getConnection().createStatement();
			st.executeUpdate(query);

			codigoResultado = 1;
		} catch (Exception e) {
			super.getLogger().error(e);
			//e.printStackTrace();
			codigoResultado = 0;
		} finally {
			super.closeConnection();
		}
		return codigoResultado;
	}

	public void registrarListaAuditoriasEntrantes(ArrayList<AuditoriaChequeEntrante> listaAuditorias) {

		try {
			Statement st = super.getConnection().createStatement();
			for (AuditoriaChequeEntrante auditoria : listaAuditorias) {
				String sql = auditoria.obtenerSentenciaInsert();
				st.executeUpdate(sql);
			}
		} catch (Exception e) {
			super.getLogger().error("Error: "+e);
		} finally {
			super.closeConnection();
		}
	}
	
	public void registrarListaAuditoriasInternas(ArrayList<AuditoriaChequeInterno> listaAuditorias) {

		try {
			Statement st = super.getConnection().createStatement();
			for (AuditoriaChequeInterno auditoria : listaAuditorias) {
				String sql = auditoria.obtenerSentenciaInsert();
				st.executeUpdate(sql);
			}
		} catch (Exception e) {
			super.getLogger().error(e);
		} finally {
			super.closeConnection();
		}
	}
	
	public void registrarListaAuditoriasSalientes(ArrayList<AuditoriaChequeSaliente> listaAuditorias) {

		try {
			Statement st = super.getConnection().createStatement();
			for (AuditoriaChequeSaliente auditoria : listaAuditorias) {
				String sql = auditoria.obtenerSentenciaInsert();
				st.executeUpdate(sql);
			}
		} catch (Exception e) {
			super.getLogger().error(e);
		} finally {
			super.closeConnection();
		}
	}
	
	public void registrarListaAuditoriasExternass(ArrayList<AuditoriaChequeExterno> listaAuditorias) {
		try {
			Statement st = super.getConnection().createStatement();
			
			for (AuditoriaChequeExterno auditoria : listaAuditorias) 
			{
				String sql = auditoria.obtenerSentenciaInsert();
				st.executeUpdate(sql);
			}
		} catch (Exception e) {
			super.getLogger().error(e);
		} finally {
			super.closeConnection();
		}
	}
	

	


}
