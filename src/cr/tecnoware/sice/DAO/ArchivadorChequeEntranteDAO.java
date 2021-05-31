package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import cr.tecnoware.sice.applets.bean.ArchivadorChequeEntrante;
import cr.tecnoware.sice.utils.Utils;

public class ArchivadorChequeEntranteDAO extends DAO {

	public ArchivadorChequeEntranteDAO() {
		super();
	}

	public int countArchivadorByFechaProcesado(Date fecha) {

		try {
			String query = "SELECT COUNT(DISTINCT REFERENCIA_CAJA) FROM ARCHIVADOR_CHEQUE_ENTRANTE WHERE FECHA BETWEEN '"
					+ Utils.convertToDateYYYYMMDD(fecha)
					+ " 00:00:00' and '"
					+ Utils.convertToDateYYYYMMDD(fecha) + " 23:59:59'";
			Statement st = super.getConnection().createStatement();

			ResultSet rs = st.executeQuery(query);

			if (rs.next()) {
				return rs.getInt(1);
			}

			return -1;

		} catch (Exception e) {
			return -1;
		} finally {
			super.closeConnection();
		}
	}

	public int save(ArchivadorChequeEntrante archivo) {
		try {

			String secuencia = "SELECT nextval('bac_seq_archivador_cheque_entrante')";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(secuencia);

			int identificador = 0;

			if (rs.next()) {
				identificador = rs.getInt(1);
			} else
				return 0;

			String query = "INSERT INTO ARCHIVADOR_CHEQUE_ENTRANTE (ID,FECHA, MONTO_TOTAL, NUMERO_TRANSACCIONES, REFERENCIA_CAJA, REFERENCIA_LOTE) VALUES("
					+ identificador
					+ ","
					+ "'"
					+ Utils.convertToDateYYYYMMDD(archivo.getFecha())
					+ "',"
					+ archivo.getMonto()
					+ ","
					+ archivo.getNumeroTransacciones()
					+ ","
					+ "'"
					+ archivo.getReferenciaCaja()
					+ "',"
					+ archivo.getReferenciaLote() + ")";
			st.executeUpdate(query);

			return identificador;

		} catch (Exception e) {
			return 0;
		} finally {
			super.closeConnection();
		}
	}

	public int update(ArchivadorChequeEntrante archivo) {
		try {

			Statement st = super.getConnection().createStatement();
			String query = "UPDATE ARCHIVADOR_CHEQUE_ENTRANTE SET MONTO_TOTAL ="
					+ archivo.getMonto()
					+ ", NUMERO_TRANSACCIONES = "
					+ archivo.getNumeroTransacciones()
					+ " WHERE ID= "
					+ archivo.getId();
			st.executeUpdate(query);

			return 1;

		} catch (Exception e) {
			return 0;
		} finally {
			super.closeConnection();
		}

	}

}
