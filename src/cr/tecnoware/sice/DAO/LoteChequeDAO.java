package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import cr.tecnoware.sice.applets.bean.LoteCheque;
import cr.tecnoware.sice.applets.constantes.AplicacionConstantes;

public class LoteChequeDAO extends DAO {
	public LoteChequeDAO() {
		super();
	}

	public List<LoteCheque> findAllWhere(String whereClause) {
		try {
			Statement st = super.getConnection().createStatement();
			String query = "Select id, cantidad_transacciones, documentos_digitalizados, entidad_origen,  entidad_procesadora, estado,fecha,monto_cero,monto_digitalizado, monto_lote, referencia_lote,tipo_camara,usuario, id_usuario FROM lote_cheque";
			if ((null != whereClause) && (!whereClause.equals(""))) {
				query += whereClause;
			}
			ResultSet rs = st.executeQuery(query);
			List<LoteCheque> retorno = new ArrayList<LoteCheque>();
			while (rs.next()) {
				LoteCheque aux = new LoteCheque();
				aux.setIdLote(rs.getInt("id"));
				aux.setDocumentos(rs.getInt("cantidad_transacciones"));
				aux.setDocumentosDigitalizados(rs.getInt("documentos_digitalizados"));
				aux.setCodigoEntidadOrigen(rs.getInt("entidad_origen"));
				aux.setCodigoEntidadProcesadora(rs.getInt("entidad_procesadora"));
				aux.setEstado(rs.getInt("estado"));
				aux.setFecha(rs.getTimestamp("fecha"));
				aux.setMontoCero(rs.getBoolean("monto_cero"));
				aux.setMontoDigitalizado(rs.getDouble("monto_digitalizado"));
				aux.setMontoLote(rs.getDouble("monto_lote"));
				aux.setReferenciaLote(rs.getString("referencia_lote"));
				aux.setTipoCamara(rs.getInt("tipo_camara"));
				aux.setUsuario(rs.getString("usuario"));
				if (aux.getDocumentosDigitalizados() == null)
					aux.setDocumentosDigitalizados(0);
				aux.setIdUsuario(rs.getInt("id_usuario"));
				retorno.add(aux);
			}

			return retorno;
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
			return null;
		} finally {
			super.closeConnection();
		}
	}

	public Integer updateLote(Integer banco) {
		try {
			Statement st = super.getConnection().createStatement();
			String query = "update lote_cheque set ";
			query += "documentos_digitalizados = (select count (*) from cheque_entrante where codigo_entidad_emisora = " + banco + " and referencia_imagen not like 'Sin Imagen'), ";
			query += "monto_digitalizado =(select  sum(monto_cheque) from cheque_entrante where codigo_entidad_emisora = " + banco + " and referencia_imagen not like 'Sin Imagen') where ";
			query += "entidad_origen = " + banco;
			// query +=" date_trunc('day',fecha)= date_trunc('day',now())";
			System.out.println(query);
			Integer rs = st.executeUpdate(query);
			System.out.println("ACT----------------" + rs);
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
			return 0;
		} finally {
			super.closeConnection();
		}

	}

	public Integer updateLote(LoteCheque lote) {
		try {
			NumberFormat nf = new DecimalFormat("#0.00");
			Statement st = super.getConnection().createStatement();
			String query = " update lote_cheque set ";
			query += " documentos_digitalizados = " + lote.getDocumentosDigitalizados();
			query += " , estado = " + AplicacionConstantes.LOTE_ENTRANTE_PENDIENTE_CUADRAR;
			query += " where ";
			query += " id = " + lote.getIdLote();
			System.out.println(query);
			Integer rs = st.executeUpdate(query);
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
			return 0;
		} finally {
			super.closeConnection();
		}

	}

	public Integer incLote(String referencialote, double monto) {
		try {

			Statement st = super.getConnection().createStatement();
			String query = " update lote_cheque set ";
			query += " documentos_digitalizados = documentos_digitalizados + 1";
			query += " , monto_digitalizado = monto_digitalizado +" + monto + " where ";
			query += " id = " + referencialote;

			System.out.println(query);
			Integer rs = st.executeUpdate(query);
			System.out.println("ACT----------------" + rs);

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
			return 0;
		} finally {
			super.closeConnection();
		}

	}

	public boolean asignarLoteUsuario(LoteCheque lote, String usuario) {
		boolean resultado = true;
		try {

			Statement st = super.getConnection().createStatement();
			String q = "select usuario from lote_cheque where id=" + lote.getIdLote();
			ResultSet res = st.executeQuery(q);
			String user = "";
			while (res.next()) {
				user = res.getString("usuario");
			}

			if (user == null || user.trim().length() == 0) {
				String query = " update lote_cheque set ";
				query += " usuario='" + usuario + "'";
				query += " where id = " + lote.getIdLote() + " ";

				Integer rs = st.executeUpdate(query);
				if (rs < 1)
					resultado = false;
				else
					resultado = true;
			} else if (user.trim().compareToIgnoreCase(usuario) != 0)
				resultado = false;

		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
			resultado = false;
		} finally {
			super.closeConnection();
		}

		return resultado;
	}

	public boolean LiberarLoteUuario(LoteCheque lote) {
		try {
			String user = null;
			Statement st = super.getConnection().createStatement();
			String query = " update lote_cheque set ";
			query += " usuario=" + user;
			query += " where id = " + lote.getIdLote() + "";

			Integer rs = st.executeUpdate(query);
			if (rs < 1)
				return false;
			else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			super.getLogger().error("Error en conexion a Base de Datos");
			return false;
		} finally {
			super.closeConnection();
		}

	}

	public void actualizarEstado(int numLote, int tipoCamara, int estado) {
		try {
			Statement st = super.getConnection().createStatement();
			String query = " update lote_cheque set ";
			query += " estado=" + estado;
			query += " where id_usuario = " + numLote + " AND tipo_camara=" + tipoCamara;
			st.executeUpdate(query);
		} catch (Exception e) {
			super.getLogger().error("Error actualizando Lote", e);
		} finally {
			super.closeConnection();
		}
	}

	public void marcarImagenesRespaldadas(int numLote, int tipoCamara) {
		try {
			Statement st = super.getConnection().createStatement();
			String query = " update lote_cheque set ";
			query += " imagenes_respaldas=" + AplicacionConstantes.LOTE_IMAGENES_RESPALDADAS;
			query += " where id_usuario = " + numLote + " AND tipo_camara=" + tipoCamara;
			st.executeUpdate(query);
		} catch (Exception e) {
			super.getLogger().error("Error actualizando Lote", e);
		} finally {
			super.closeConnection();
		}
	}

	public void marcarDatosMigrados(int numLote, int tipoCamara) {
		try {
			Statement st = super.getConnection().createStatement();
			String query = " update lote_cheque set ";
			query += " respaldado=" + AplicacionConstantes.LOTE_DATA_RESPALDADA;
			query += " where id_usuario = " + numLote + " AND tipo_camara=" + tipoCamara;
			st.executeUpdate(query);
		} catch (Exception e) {
			super.getLogger().error("Error actualizando Lote", e);
		} finally {
			super.closeConnection();
		}
	}

	public int ejecutarProceso(int proceso, int numeroLote, int tipoCamara) {
		int resultado = 101;
		try {
			Statement st = super.getConnection().createStatement();
			if (proceso == 0) {//TOMAR EL LOTE
				Integer loteTomado = Integer.valueOf(0);
				String select = "Select tomado as TOMADO FROM lote_cheque";
				select = select + " where id_usuario = " + numeroLote + " AND tipo_camara=" + tipoCamara;
				ResultSet rs = st.executeQuery(select);
				if (rs.next()) {
					loteTomado = Integer.valueOf(rs.getInt("TOMADO"));
				}
				if ((loteTomado == null) || (loteTomado.intValue() == 0)) {
					String query = " update lote_cheque set ";
					query = query + " tomado=1";
					query = query + " where id_usuario = " + numeroLote + " AND tipo_camara=" + tipoCamara;
					st.executeUpdate(query);
					resultado = 100;
				}
			} else if (proceso == 1) {
					String query = " update lote_cheque set ";
					query = query + " tomado=0";
					query = query + " where id_usuario = " + numeroLote + " AND tipo_camara=" + tipoCamara;
					st.executeUpdate(query);
					resultado=100;
			}
		} catch (Exception e) {
			super.getLogger().error("Error actualizando Lote", e);
		} finally {
			super.closeConnection();
		}
		return resultado;
	}

}
