package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cr.tecnoware.sice.applets.bean.AuditoriaChequeExterno;
import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.applets.bean.ParametrosGenerales;
import cr.tecnoware.sice.applets.bean.TipoDocumento;
import cr.tecnoware.sice.applets.constantes.AplicacionConstantes;
import cr.tecnoware.sice.applets.constantes.ChequeConstantes;
import cr.tecnoware.sice.applets.constantes.ChequeDigitalizadoConstantes;
import cr.tecnoware.sice.gestor_archivos.archivos.GestorArchivosFactory;
import cr.tecnoware.sice.gestor_archivos.interfaces.GestorArchivos;
import cr.tecnoware.sice.interfaz.ProcesarCamaraServices;
import cr.tecnoware.sice.utils.CacheUtils;
import cr.tecnoware.sice.utils.DAOUtils;
import cr.tecnoware.sice.utils.Utils;
import cr.tecnoware.sice.utils.encryption.CryptoUtils;
import cr.tecnoware.sice.utils.encryption.CryptoUtilsConstantes;

public class TransaccionExternaDAO extends DAO implements ProcesarCamaraServices {
	//private final String insertIntoSql="INSERT INTO cheque_externo(id, codigo_banco_cheque, codigo_entidad_emisora, codigo_entidad_procesadora, codigo_moneda,codigo_oficina_cheque, cuenta_cheque, estado, fecha, fecha_camara, ibrn_interno, id_lote, monto_cheque, numero_lote,referencia_imagen, serial_cheque, soporte_digital, soporte_fisico,  tipo_cheque) ";
	private final String insertIntoSql="INSERT INTO cheque_externo( codigo_banco_cheque, codigo_entidad_emisora, codigo_entidad_procesadora, codigo_moneda,codigo_oficina_cheque, cuenta_cheque, estado, fecha, fecha_camara, ibrn_interno, id_lote, monto_cheque, numero_lote,referencia_imagen, serial_cheque, soporte_digital, soporte_fisico,  tipo_cheque,codigo_oficina_procesadora,digito_control_cheque,secuencia,lote) ";
	private final String updateQuery = " UPDATE CHEQUE_EXTERNO SET ";

	
	public int buscarDuplicados(ChequesDigitalizados cheque, int codigoEntidad) {
		int salida=0;
		try
		{
			String query="select lote,secuencia from cheque_externo where ibrn_interno='"+cheque.getReferenciaUnica()+"'";
			Statement st = super.getConnection().createStatement();
			ResultSet rs=st.executeQuery(query);								
			
			boolean enc=false;
			int lote=-1,secuencia=-1;
			if (rs.next()) {
				lote = rs.getInt("lote");
				secuencia=rs.getInt("secuencia");
				enc=true;
			}
			
			if(enc)
			{
			    if(lote==cheque.getIdLote() && secuencia==cheque.getPosicionLote()) // se trata exactamente dle mismo registro por lo cual no se toma como duplicado
			    	salida=1;
			    else
			    	salida=2;
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			super.closeConnection();
		}
		return salida;
	}

	
	public boolean registrarTransaccionesCorregidas(List<ChequesDigitalizados> transacciones, int codigoEntidad) {
		// TODO Auto-generated method stub
		try
		{
			boolean valido=true;
			int numeroLote=-1;
			if(transacciones!=null && transacciones.size()>0)
			{
				ConexionRemotaBean miConexion = new ConexionRemotaBean();
				GestorArchivos gestor;
				miConexion = CacheUtils.getConexionRemota(Utils.fillZerosLeft(codigoEntidad + "", 4), AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_EXTERNA);
				gestor = GestorArchivosFactory.obtenerGestor(miConexion);
				ChequesDigitalizadosDAO digitalizados=new ChequesDigitalizadosDAO();
				ParametrosGenerales parametros = (new ParametrosGeneralesDAO()).obtenerParametrosSistema();
				String fechaCamaraStr = parametros.getFechaCamara();								
				ArrayList<AuditoriaChequeExterno> listaAuditorias = new ArrayList<AuditoriaChequeExterno>();
				String fechaRegistro = "";
				for(ChequesDigitalizados cheque:transacciones)
				{
					TipoDocumento reservado=CacheUtils.getTcs().get(Integer.parseInt(cheque.getCheque().getTipoCheque().trim()));
					if (((cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_APROBADO && cheque.getEstado() < ChequeDigitalizadoConstantes.CHEQUE_RECHAZADO) || (cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_NO_EXISTE && cheque.getEstado() <= ChequeDigitalizadoConstantes.CHEQUE_CUENTA_INACTIVA)) && reservado != null && !reservado.isBoleta()) 
					{
						if (fechaRegistro.equalsIgnoreCase("")) {
				              fechaRegistro = consultarFechaLoteRegistro(cheque.getIdLote(), 4);
				         }
						//Se crea la referencia unica de la transaccion.						
						cheque.setReferenciaUnica(Utils.construirReferenciaUnica(cheque, AplicacionConstantes.CAMARA_EXTERNA, fechaCamaraStr));
						// se crea el query y se sube las imagenes respectivas
						int duplicado=buscarDuplicados(cheque, codigoEntidad);
						if(duplicado==0)
						{
						String directorio = Utils.obtenerRutaImagen(cheque);
						try {
							int res=0;
							res=gestor.subirImagen(directorio, cheque.getReferenciaUnica() + ChequeConstantes.IMAGEN_ANVERSO, CryptoUtils.codificar(cheque.getImagenAnverso(), CryptoUtilsConstantes.METODO_AES));
							if(res==0)
							{
								super.getLogger().error("subiendo imagen Anverso "+cheque.getReferenciaUnica());
								valido = false;
								continue;
							}
							res=gestor.subirImagen(directorio, cheque.getReferenciaUnica() + ChequeConstantes.IMAGEN_REVERSO, CryptoUtils.codificar(cheque.getImagenReverso(), CryptoUtilsConstantes.METODO_AES));
							if(res==0)
							{
								super.getLogger().error("subiendo imagen Reverso "+cheque.getReferenciaUnica());
								valido=false;
								continue;
							}
							
						} catch (Exception ee) {
							super.getLogger().info("Error subiendo Imagenes ", ee);
							valido = false;
							continue;
						}
						
						String query = DAOUtils.createInsertQueryExterno(cheque, fechaRegistro);
						if(query!=null)
						{
							query=insertIntoSql+query;
							try
							{
							  Statement st = super.getConnection().createStatement();
							  st.executeUpdate(query);		
							  
							  AuditoriaChequeExterno auditoria = new AuditoriaChequeExterno(cheque, AplicacionConstantes.ESTADO_APROBADO);								
							  listaAuditorias.add(auditoria);
							}catch(Exception e)
							{
								e.printStackTrace();
								valido=false;
								continue;
							}finally{
								super.closeConnection();
							}
							
						}
					 }else if(duplicado==2)
						{
							cheque.setEstado(ChequeDigitalizadoConstantes.CHEQUE_DUPLICADO);
						}
					}else{
						numeroLote=cheque.getIdLote();
					}
					
					if(cheque.getEstado()>ChequeDigitalizadoConstantes.CHEQUE_PENDIENTE_APROBAR || reservado.isBoleta())
					{
						digitalizados.actualizarChequeDigitalizado(cheque);
					}
				}
				if (numeroLote != -1) {
					LoteChequeDAO loteDAO = new LoteChequeDAO();
					loteDAO.actualizarEstado(numeroLote, AplicacionConstantes.CAMARA_EXTERNA, AplicacionConstantes.LOTE_ENTRANTE_CUADRADO);
					loteDAO.marcarDatosMigrados(numeroLote, AplicacionConstantes.CAMARA_EXTERNA);
				}
				
				AuditoriaTransaccionDAO daoAuditoria = new AuditoriaTransaccionDAO();
				daoAuditoria.registrarListaAuditoriasExternass(listaAuditorias);
			}
		   	return valido;
		}catch(Exception e)
		{
			System.out.println("registrando transacciones ");
			if(this.getConn()!=null)
				super.closeConnection();
				
			e.printStackTrace();
			return false;
		}

	}


	@Override
	public boolean actualizarTransaccionesCorregidas(List<ChequesDigitalizados> transacciones, int codigoEntidad) {
		try {
			boolean valido = true;
			if (transacciones != null && transacciones.size() > 0) {

				ChequesDigitalizadosDAO digitalizados = new ChequesDigitalizadosDAO();
				int codigoEntidadOrigen = -1;

				ArrayList<AuditoriaChequeExterno> listaAuditorias = new ArrayList<AuditoriaChequeExterno>();
				for (ChequesDigitalizados cheque : transacciones) {
					TipoDocumento reservado = CacheUtils.getTcs().get(Integer.parseInt(cheque.getCheque().getTipoCheque().trim()));
					if (((cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_APROBADO && cheque.getEstado() <= ChequeDigitalizadoConstantes.CHEQUE_RECHAZADO) || (cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_CUENTA_INVALIDA && cheque.getEstado() <= ChequeDigitalizadoConstantes.CHEQUE_CUENTA_INACTIVA)) && reservado != null ) {
						
						if( !reservado.isBoleta())
						{
						// buscar Registro en maestra, si existe lanzo el update, si se realiza el update entonces hago la auditoria
						Integer id = null;
						Integer bancoCheque = null,entidadEmisora=null,oficinaProcesadora=null;
						String serial=null;
						String cuenta=null;
						double monto=0.00;
						try {
							Statement st = super.getConnection().createStatement();
							String query = "select id,serial_cheque,monto_cheque,cuenta_cheque,codigo_banco_cheque,codigo_entidad_emisora,codigo_oficina_procesadora from cheque_externo where numero_lote=" + cheque.getIdLote() + " and secuencia=" + cheque.getPosicionLote();
							ResultSet rs = st.executeQuery(query);
							if (rs.next()) {
								id = rs.getInt("id");
								serial=rs.getString("serial_cheque");
								cuenta=rs.getString("cuenta_cheque");
								monto=rs.getDouble("monto_cheque");
								bancoCheque = rs.getInt("codigo_banco_cheque");
								entidadEmisora = rs.getInt("codigo_entidad_emisora");
								oficinaProcesadora = rs.getInt("codigo_oficina_procesadora");
							}
						} catch (Exception e) {
							System.out.println("Buscando el registro en la maestra de entrantes ");
							e.printStackTrace();
						}finally {
							super.closeConnection();
						}

						if (id != null) {
							Integer estado=ChequeConstantes.CHEQUE_SALIENTE_RECIBIDO_REGIONAL;
							Integer motivo=0;
							if(cheque.getCodigoMotivoDevolucion()!=0)
							{
								estado=2;
								motivo=cheque.getCodigoMotivoDevolucion();
							}
							String query = " serial_cheque='" + Utils.fillZerosLeft(Utils.removeLeadingZeros(cheque.getCheque().getSerial()), 8) + "' ";
							query += ", monto_cheque=" + cheque.getCheque().getMonto() + " ";
							query += ", cuenta_cheque='" + cheque.getCheque().getCuenta() + "' ";
							query += ", estado="+estado;
							query += ", codigo_motivo_devolucion="+motivo;
							query += ", codigo_entidad_emisora="+cheque.getCodigoBancoEmisor();
							query += ", codigo_oficina_procesadora="+cheque.getCodigoOficinaProcesadora();
							query += " where id="+id;

							if (query != null) {
								query = updateQuery + query;
								try {
									Statement st = super.getConnection().createStatement();
									st.executeUpdate(query);
									AuditoriaChequeExterno auditoria = new AuditoriaChequeExterno(cheque, AplicacionConstantes.ESTADO_APROBADO);
									String modificado="";
									if(serial.compareToIgnoreCase(Utils.fillZerosLeft(Utils.removeLeadingZeros(cheque.getCheque().getSerial()), 8))!=0)
										 modificado+="Serial Ant="+serial+" Act="+Utils.fillZerosLeft(Utils.removeLeadingZeros(cheque.getCheque().getSerial()), 8);
									if(cuenta.compareToIgnoreCase(cuenta)!=0)
										 modificado+="Cuenta Ant="+serial+" Act="+cheque.getCheque().getCuenta();
									if(monto!=cheque.getCheque().getMonto())
										modificado+="Monto Ant="+monto+" Act="+cheque.getCheque().getMonto();
									if (bancoCheque != Integer.parseInt(cheque.getCheque().getCodigoBanco()))
										modificado += "Banco Ant=" + bancoCheque + " Act=" + cheque.getCheque().getCodigoBanco();
									if (entidadEmisora != cheque.getCodigoBancoEmisor())
										modificado += "Banco Emisor Ant=" + entidadEmisora + " Act=" + cheque.getCodigoBancoEmisor();
									if (oficinaProcesadora != cheque.getCodigoOficinaProcesadora())
										modificado += "Oficina Proc Ant=" + oficinaProcesadora + " Act=" + cheque.getCodigoOficinaProcesadora();
									
									auditoria.setIdTransaccion(id);
									auditoria.setDescripcion(modificado);
									auditoria.setAccion(ChequeConstantes.ACCION_CHEQUE_POST_CORREGIDO);
									listaAuditorias.add(auditoria);
								} catch (Exception e) {
									e.printStackTrace();
									valido = false;
									continue;
								} finally {
									super.closeConnection();
								}

							}
						}

					}
						if (cheque.getEstado() > ChequeDigitalizadoConstantes.CHEQUE_PENDIENTE_APROBAR && cheque.getEstado() != ChequeDigitalizadoConstantes.CHEQUE_TC_INVALIDO || reservado.isBoleta()) {
							digitalizados.actualizarChequeDigitalizado(cheque);
						}
					}
				}
				AuditoriaTransaccionDAO daoAuditoria = new AuditoriaTransaccionDAO();
				daoAuditoria.registrarListaAuditoriasExternass(listaAuditorias);
			}
			return valido;
		} catch (Exception e) {
			System.out.println("actualizando transacciones ");
			if (this.getConn() != null)
				super.closeConnection();

			e.printStackTrace();
			return false;
		}

	}
	
	public String consultarFechaLoteRegistro(int numLote, int tipoCamara)
	  {
	    String fechaRegistro = "";
	    try
	    {
	      Date c = new Date(Calendar.getInstance().getTimeInMillis());
	      Statement st = super.getConnection().createStatement();
	      String query = " SELECT fecha_registros FROM lote_cheque WHERE id_usuario = " + numLote + " AND tipo_camara=" + tipoCamara;
	      ResultSet rs = st.executeQuery(query);
	      if (rs.next())
	      {
	        Timestamp fechaRegistros = rs.getTimestamp("fecha_registros");
	        if (fechaRegistros != null) {
	          c.setTime(fechaRegistros.getTime());
	        }
	      }
	      fechaRegistro = Utils.FormatDateToStringToFormat(c, "yyyyMMdd hh:mm:s");
	    }
	    catch (Exception e)
	    {
	      super.getLogger().error("Error consultado Lote EXTERNO", e);
	    }
	    finally
	    {
	      super.closeConnection();
	    }
	    return fechaRegistro;
	  }

}
