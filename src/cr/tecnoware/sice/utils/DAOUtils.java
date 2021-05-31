package cr.tecnoware.sice.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import cr.tecnoware.sice.applets.bean.ChequeCorregido;
import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.applets.constantes.AplicacionConstantes;
import cr.tecnoware.sice.applets.constantes.ChequeConstantes;
import cr.tecnoware.sice.applets.constantes.ChequeDigitalizadoConstantes;
import cr.tecnoware.sice.applets.parser.CodigoMotivoParser;
import cr.tecnoware.sice.context.ApplicationParameters;

public class DAOUtils {

	public static String createInsertQuerySaliente(ChequesDigitalizados cheque,String fechaCamaraStr) {
		String transaccion = null;
		if (cheque != null) {
			Date c = new Date(Utils.obtenerFechaProcesoCamaraSaliente().getTimeInMillis());
			ChequeCorregido ch = cheque.getCheque();
			String referenciaIBRN = Utils.fillZerosLeft(""+cheque.getCheque().getTipoCheque(),4)+Utils.fillZerosLeft(""+cheque.getCheque().getCodigoBanco(),4)+cheque.getCheque().getSerial() + cheque.getCheque().getCuenta();
			Timestamp fechaCamara=cr.tecnoware.sice.applets.utils.Utils.StringToTimestamp(fechaCamaraStr);
			transaccion = " values ( ";
			transaccion += "'" + ch.getCodigoBanco() + "','" + cheque.getEntidadProcesadora() + "','" + cheque.getEntidadProcesadora() + "',";
			transaccion += "'1',0,'" + ch.getCuenta() + "','" + ChequeConstantes.CHEQUE_SALIENTE_RECIBIDO_REGIONAL + "',";
			transaccion += "'" + Utils.FormatDateToStringToFormat((Object)c,"yyyyMMdd hh:mm:s") + "','" + Utils.FormatDateToStringToFormat((Object)fechaCamara.getTime(),"yyyyMMdd hh:mm:s") + "','" + cheque.getReferenciaUnica() + "','" + cheque.getIdLote() + "','" + ch.getMonto() + "','" + cheque.getIdLote() + "','" + cheque.getReferenciaUnica() + ChequeConstantes.IMAGEN_ANVERSO + "',";
			transaccion += "'" + ch.getSerial() + "','" + ChequeConstantes.CHEQUE_CON_SOPORTE_FISICO + "','" + ChequeConstantes.CHEQUE_CON_SOPORTE_DIGITAL + "','" + ch.getTipoCheque() + "',";
			transaccion += "'" + cheque.getIdLote() + "','" + cheque.getPosicionLote() + "','" + cheque.getCodigoOficinaProcesadora() + "',0";
			transaccion+=",0,'"+referenciaIBRN+"'";
			transaccion += " ) ";			
		}
		return transaccion;
	}

	public static String createInsertQueryEntrante(ChequesDigitalizados cheque,String fechaCamaraStr) {
		String transaccion = null;
		if (cheque != null) {
			Date c = new Date(Calendar.getInstance().getTimeInMillis());
			Timestamp fechaCamara=cr.tecnoware.sice.applets.utils.Utils.StringToTimestamp(fechaCamaraStr);
			ChequeCorregido ch = cheque.getCheque();
			int entidadProcesadora = cheque.getEntidadProcesadora();
			int codigoMotivo = CodigoMotivoParser.obtenerCodigoMotivoEntrante(cheque.getEstado());
			int procesoDevolucion = (codigoMotivo > 0) ? AplicacionConstantes.PROCESO_DEVOLUCION_CUENTA : AplicacionConstantes.PROCESO_DEVOLUCION_CORRECTO;
			String referenciaIBRN = Utils.fillZerosLeft(""+cheque.getCheque().getTipoCheque(),4)+Utils.fillZerosLeft(""+cheque.getCheque().getCodigoBanco(),4)+cheque.getCheque().getSerial() + cheque.getCheque().getCuenta();
			int estado=(codigoMotivo > 0) ? ChequeConstantes.CHEQUE_ENTRANTE_RECHAZADO : ChequeConstantes.CHEQUE_SALIENTE_RECIBIDO_REGIONAL;
			int estadoVerificado=0;
			String usuarioRevisor=cheque.getUsuarioRevisor();			
			int tipoChequeCaja = ApplicationParameters.getInstance().getInt("cheque.caja.tc");
			int tipoChequeCertificado = ApplicationParameters.getInstance().getInt("cheque.certificado.tc");
			int tipoCheque=Integer.parseInt(cheque.getCheque().getTipoCheque());
			if((tipoCheque==tipoChequeCaja||tipoCheque==tipoChequeCertificado)){
				estadoVerificado=(cheque.isChequeVerificado())?AplicacionConstantes.VERILECT_VERIFICADO:AplicacionConstantes.VERILECT_INCONSISTENCIA_ARCHIVO;
			}
			
			transaccion = " values ( ";
			transaccion += "'" + entidadProcesadora + "','" + cheque.getCodigoBancoEmisor() + "','" + cheque.getEntidadProcesadora() + "',";
			transaccion += "'1','" + ch.getCodigoOficina() + "','" + Utils.fillZerosLeft(Utils.removeLeadingZeros(ch.getCuenta()), ChequeConstantes.CHEQUE_LONGITUD_CUENTA) + "','" + estado + "',";
			transaccion += "'" +  Utils.FormatDateToStringToFormat((Object)c,"yyyyMMdd hh:mm:s") + "','" + Utils.FormatDateToStringToFormat((Object)fechaCamara.getTime(),"yyyyMMdd hh:mm:s") + "','" + cheque.getReferenciaUnica() + "','" + cheque.getIdLote() + "','" + ch.getMonto() + "','" + cheque.getPosicionLote() + "','" + cheque.getReferenciaUnica() + ChequeConstantes.IMAGEN_ANVERSO + "',";
			transaccion += "'" + cheque.getPosicionLote() + "','" + Utils.fillZerosLeft(Utils.removeLeadingZeros(ch.getSerial()), ChequeConstantes.CHEQUE_LONGITUD_SERIAL) + "','" + ch.getTipoCheque() + "',";
			transaccion += codigoMotivo + ",0,0,'','" + referenciaIBRN + "'," + procesoDevolucion+",0,"+estadoVerificado;			
			if(null!= usuarioRevisor && !usuarioRevisor.equals("")){
				transaccion+=",'"+usuarioRevisor+"'";
			}else{
				transaccion+=",null";
			}
			transaccion += " ) ";
			
		}
		return transaccion;
	}

	public static String createInsertQueryInterno(ChequesDigitalizados cheque,String fechaCamaraStr) {
		String transaccion = null;
		if (cheque != null) {
			Date c = new Date(Calendar.getInstance().getTimeInMillis());
			ChequeCorregido ch = cheque.getCheque();
			Timestamp fechaCamara=cr.tecnoware.sice.applets.utils.Utils.StringToTimestamp(fechaCamaraStr);
			String referenciaIBRN = Utils.fillZerosLeft(""+cheque.getCheque().getTipoCheque(),4)+Utils.fillZerosLeft(""+cheque.getCheque().getCodigoBanco(),4)+cheque.getCheque().getSerial() + cheque.getCheque().getCuenta()+Utils.fillZerosLeft(Utils.removeLeadingZeros(cheque.getCodigoOficinaProcesadora()+""),4);
			String oficinaCheque = cr.tecnoware.sice.applets.utils.Utils.obtenerOficina(ch.getCuenta(), AplicacionConstantes.CAMARA_ENTRANTE);
			int estadoVerilect=(cheque.getEstado()==ChequeDigitalizadoConstantes.CHEQUE_APROBADO )?AplicacionConstantes.VERILECT_VERIFICADO:AplicacionConstantes.VERILECT_INCONSISTENCIA_ARCHIVO;
			transaccion = " values ( ";
			transaccion += "'" + ch.getCodigoBanco() + "','" + ch.getCodigoBanco() + "','" + cheque.getEntidadProcesadora() + "',";
			transaccion += "1,'" + oficinaCheque + "','" + ch.getCuenta() + "','" + ChequeConstantes.CHEQUE_SALIENTE_RECIBIDO_REGIONAL + "',";
			transaccion += "'" + Utils.FormatDateToStringToFormat((Object)c,"yyyyMMdd hh:mm:s") + "','" + Utils.FormatDateToStringToFormat((Object)fechaCamara.getTime(),"yyyyMMdd hh:mm:s") + "','" + cheque.getReferenciaUnica() + "','" + cheque.getIdLote() + "','" + ch.getMonto() + "','" + cheque.getIdLote() + "','" + cheque.getReferenciaUnica() + ChequeConstantes.IMAGEN_ANVERSO + "',";
			transaccion += "'" + ch.getSerial() + "','" + ChequeConstantes.CHEQUE_CON_SOPORTE_FISICO + "','" + ChequeConstantes.CHEQUE_CON_SOPORTE_DIGITAL + "','" + ch.getTipoCheque() + "'," + cheque.getPosicionLote() + ",0," + cheque.getCodigoOficinaProcesadora()+",0,"+estadoVerilect+","+cheque.getIdLote();
			transaccion += ",'"+referenciaIBRN+"'";
			transaccion += " ) ";
		}
		return transaccion;
	}

	public static String createInsertQueryExterno(ChequesDigitalizados cheque) {
		String transaccion = null;
		if (cheque != null) {
			Date c = new Date(Calendar.getInstance().getTimeInMillis());
			ChequeCorregido ch = cheque.getCheque();
			transaccion = " values ( ";
			transaccion += "'" + ch.getCodigoBanco() + "','" + cheque.getEntidadProcesadora() + "','" + cheque.getEntidadProcesadora() + "',";
			transaccion += "'1','" + ch.getCodigoOficina() + "','" + ch.getCuenta() + "','" + ChequeConstantes.CHEQUE_SALIENTE_RECIBIDO_REGIONAL + "',";
			transaccion += "'" + Utils.FormatDateToStringToFormat((Object)c,"yyyyMMdd hh:mm:s") + "','" +Utils.FormatDateToStringToFormat((Object) cheque.getFecha(),"yyyyMMdd hh:mm:s") + "','" + cheque.getReferenciaUnica() + "','" + cheque.getIdLote() + "','" + ch.getMonto() + "','" + cheque.getIdLote() + "','" + cheque.getReferenciaUnica() + ChequeConstantes.IMAGEN_ANVERSO + "',";
			transaccion += "'" + Utils.fillZerosLeft(cr.tecnoware.sice.applets.utils.Utils.removeLeadingZeros(ch.getSerial()),8) + "','" + ChequeConstantes.CHEQUE_CON_SOPORTE_FISICO + "','" + ChequeConstantes.CHEQUE_CON_SOPORTE_DIGITAL + "','" + ch.getTipoCheque() + "'";
			transaccion +=","+cheque.getCodigoOficinaProcesadora()+",0,'"+cheque.getPosicionLote()+"',"+cheque.getIdLote()+" ) ";
		}
		return transaccion;
	}
	
	  public static String createInsertQueryExterno(ChequesDigitalizados cheque, String fechaRegistros)
	  {
	    String transaccion = null;
	    if (cheque != null)
	    {
	      Date c = new Date(Calendar.getInstance().getTimeInMillis());
	      ChequeCorregido ch = cheque.getCheque();
	      transaccion = " values ( ";
	      transaccion = transaccion + "'" + ch.getCodigoBanco() + "','" + cheque.getEntidadProcesadora() + "','" + cheque.getEntidadProcesadora() + "',";
	      transaccion = transaccion + "'1','" + ch.getCodigoOficina() + "','" + ch.getCuenta() + "','" + 0 + "',";
	      transaccion = transaccion + "'" + fechaRegistros + "','" + Utils.FormatDateToStringToFormat(cheque.getFecha(), "yyyyMMdd hh:mm:s") + "','" + cheque.getReferenciaUnica() + "','" + cheque.getIdLote() + "','" + ch.getMonto() + "','" + cheque.getIdLote() + "','" + cheque.getReferenciaUnica() + ".TCA" + "',";
	      transaccion = transaccion + "'" + Utils.fillZerosLeft(cr.tecnoware.sice.applets.utils.Utils.removeLeadingZeros(ch.getSerial()), 8) + "','" + 1 + "','" + 1 + "','" + ch.getTipoCheque() + "'";
	      transaccion = transaccion + "," + cheque.getCodigoOficinaProcesadora() + ",0,'" + cheque.getPosicionLote() + "'," + cheque.getIdLote() + " ) ";
	    }
	    return transaccion;
	  }
}
