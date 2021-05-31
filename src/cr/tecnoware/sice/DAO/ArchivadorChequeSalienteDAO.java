package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import cr.tecnoware.sice.applets.bean.ArchivadorChequeSaliente;
import cr.tecnoware.sice.utils.Utils;

public class ArchivadorChequeSalienteDAO extends DAO {

	public ArchivadorChequeSalienteDAO() {
		super();
	}

	public int countArchivadorByFechaProcesado(Date fecha) {
		int codigoResultado = -1;
		try {
			String query = "SELECT COUNT(DISTINCT REFERENCIA_CAJA) FROM ARCHIVADOR_CHEQUE_SALIENTE WHERE FECHA BETWEEN '"
					+ Utils.convertToDateYYYYMMDD(fecha)
					+ " 00:00:00' and '"
					+ Utils.convertToDateYYYYMMDD(fecha) + " 23:59:59'";
			Statement st = super.getConnection().createStatement();

			ResultSet rs = st.executeQuery(query);

			if (rs.next()) {
				return rs.getInt(1);
			}

			codigoResultado = -1;

		} catch (Exception e) {
			codigoResultado = -1;
		} finally {
			super.closeConnection();
		}
		return codigoResultado;
	}

	public int save(ArchivadorChequeSaliente archivo) {
		try {

			String secuencia = "SELECT nextval('bac_seq_archivador_cheque_saliente')";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(secuencia);

			int identificador = 0;

			System.out.println("entra save");

			if (rs.next()) {
				identificador = rs.getInt(1);
			} else {
				System.out.println("revienta por la consulta nula");
				return 0;
			}

			String query = "INSERT INTO ARCHIVADOR_CHEQUE_SALIENTE (ID,FECHA, MONTO_TOTAL, NUMERO_TRANSACCIONES, REFERENCIA_CAJA, REFERENCIA_LOTE) VALUES("
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

	public int update(ArchivadorChequeSaliente archivo) {
		try {
			Statement st = super.getConnection().createStatement();
			String query = "UPDATE ARCHIVADOR_CHEQUE_SALIENTE SET MONTO_TOTAL ="
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
