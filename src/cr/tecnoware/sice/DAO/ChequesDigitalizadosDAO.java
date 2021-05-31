package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cr.tecnoware.sice.applets.bean.ChequeCorregido;
import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.utils.Utils;

public class ChequesDigitalizadosDAO extends DAO {
	private final String findByQuerySql = "SELECT id, micr, micrcorregido, imagennombre, idlote, entidadprocesadora, tipocamara, estado, fecha, posLote, oficina FROM cheques_digitalizados ";
	private final String findByQuerySqlHistorico = "";

	public ChequesDigitalizadosDAO() {
		super();
	}

	public boolean insertCheque(ChequesDigitalizados cheque) {
		try {
			String query = "INSERT INTO Cheques_Digitalizados (MICR,imagenNombre,idLote,entidadProcesadora,tipoCamara,fecha,estado, posLote,micrcorregido,oficina)" + " values( " + "'00" + cheque.getMICR() + "'," + "'" + cheque.getImagenNombre() + "'," + "" + cheque.getIdLote() + "," + "" + cheque.getEntidadProcesadora() + "," + "" + cheque.getTipoCamara() + ",'" + Utils.FormatDateToStringToFormat(cheque.getFecha(), "yyyyMMdd hh:mm:s") + "'," + cheque.getEstado() + "," + cheque.getPosicionLote() + ",'','0'" + ")";
			System.out.println(query);
			Statement st = super.getConnection().createStatement();
			st.executeUpdate(query);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			super.closeConnection();
		}
	}

	public List<ChequesDigitalizados> findAllWhere(String query) {
		try {
			String sentencia = findByQuerySql;
			if (query != null && query.trim().length() > 0)
				sentencia += query;

			Statement st = super.getConnection().createStatement();
			ResultSet resul = st.executeQuery(sentencia);
			if (resul != null) {
				List<ChequesDigitalizados> lista = new ArrayList<ChequesDigitalizados>();
				while (resul.next()) {
					ChequesDigitalizados cheque = new ChequesDigitalizados();
					ChequeCorregido chequeC=new ChequeCorregido();
					chequeC.setCodigoOficina(resul.getString("oficina"));
					cheque.setId(resul.getInt("id"));
					cheque.setMICR(resul.getString("micr"));
					cheque.setImagenNombre(resul.getString("imagennombre"));
					cheque.setIdLote(resul.getInt("idLote"));
					cheque.setEntidadProcesadora(resul.getInt("entidadprocesadora"));
					cheque.setTipoCamara(resul.getInt("tipoCamara"));
					cheque.setFecha(resul.getTimestamp("fecha"));
					cheque.setEstado(resul.getInt("estado"));
					cheque.setPosicionLote(resul.getInt("posLote"));
					cheque.setMICRCorregido(resul.getString("micrcorregido"));
					cheque.setCheque(chequeC);
					lista.add(cheque);
				}
				return lista;

			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			super.closeConnection();
		}

	}

	public boolean actualizarChequeDigitalizado(ChequesDigitalizados cheque) {
		boolean valido = true;
		if (cheque != null) {
			cheque.setMICRCorregido(Utils.buildMICR(cheque.getCheque()));
			String query = "UPDATE cheques_digitalizados  SET  micrcorregido='" + cheque.getMICRCorregido() + "', estado=" + cheque.getEstado()+ ", oficina="+cheque.getCheque().getCodigoOficina().trim();
			query += " where id=" + cheque.getId();
			try {
				Statement st = super.getConnection().createStatement();
				st.executeUpdate(query);
			} catch (Exception e) {
				e.printStackTrace();
				valido = false;
			}finally{
				super.closeConnection();
			}
		}
		return valido;
	}

	public ChequesDigitalizados get(ChequesDigitalizados cheque0) {
		try {
			String query = findByQuerySql + " where " + " entidadprocesadora = " + cheque0.getEntidadProcesadora() + " and tipocamara = " + cheque0.getTipoCamara() + " and estado = " + cheque0.getEstado() + " and fecha = '" + cheque0.getFecha() + "'"
			// +" and micr = '" + cheque0.getMICR()+"'"
					+ " and idLote = " + cheque0.getIdLote() + " and imagennombre = '" + cheque0.getImagenNombre() + "'";
            System.out.println("Query a realizar");
			Statement st = super.getConnection().createStatement();
			ResultSet resul = st.executeQuery(query);
			System.out.println("devolvio " + resul);

			ChequesDigitalizados cheque = new ChequesDigitalizados();

			if (resul != null) {
				List<ChequesDigitalizados> lista = new ArrayList<ChequesDigitalizados>();
				while (resul.next()) {

					cheque.setId(resul.getInt("id"));
					cheque.setMICR(resul.getString("micr"));
					cheque.setImagenNombre(resul.getString("imagennombre"));
					cheque.setIdLote(resul.getInt("idLote"));
					cheque.setEntidadProcesadora(resul.getInt("entidadprocesadora"));
					cheque.setTipoCamara(resul.getInt("tipoCamara"));
					cheque.setFecha(resul.getTimestamp("fecha"));
					cheque.setEstado(resul.getInt("estado"));

				}
				System.out.println("va a devolver " + lista.size());
				return cheque;

			} else {
				System.out.println("RESULTADO NULO DEL QUERY");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			super.closeConnection();
		}
	}

	public boolean liberarChequeDigitalizado(List<ChequesDigitalizados> cheques) {
		boolean valido = true;
		if (cheques != null) {
			try {
				Statement st = super.getConnection().createStatement();
				for (ChequesDigitalizados cheque : cheques) {
					cheque.setMICRCorregido(Utils.buildMICR(cheque.getCheque()));
					String query = "UPDATE cheques_digitalizados  SET  micr='" + cheque.getMICRCorregido() + "'";
					query+=" , micrcorregido='" + cheque.getMICRCorregido() + "'";
					query+=" , oficina="+cheque.getCheque().getCodigoOficina();
					query += " where id=" + cheque.getId();
					st.executeUpdate(query);
				}
			} catch (Exception e) {
				e.printStackTrace();
				valido = false;
			} finally {
				super.closeConnection();
			}

		}
		return valido;
	}

	public boolean actualizarEstado(ChequesDigitalizados cheque) {
		boolean valido = true;
		if (cheque != null) {
			try {
				Statement st = super.getConnection().createStatement();
				String query = "UPDATE cheques_digitalizados  SET  estado='" + cheque.getEstado() + "'";
				query += " where id=" + cheque.getId();
				st.executeUpdate(query);

			} catch (Exception e) {
				e.printStackTrace();
				valido = false;
			} finally {
				super.closeConnection();
			}

		}
		return valido;
	}
}
