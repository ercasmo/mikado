package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cr.tecnoware.sice.applets.bean.AuditoriaChequeSaliente;
import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.applets.bean.ParametrosGenerales;
import cr.tecnoware.sice.applets.bean.TipoDocumento;
import cr.tecnoware.sice.applets.bean.TransaccionSaliente;
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

public class TransaccionSalienteDAO extends DAO implements ProcesarCamaraServices {

	private final String findByQuerySql = "SELECT codigo_entidad_procesadora,serial_cheque,codigo_banco_cheque,codigo_oficina_cheque,digito_control_cheque,cuenta_cheque,tipo_cheque,monto_cheque,cuenta_depositaria,fecha_camara,referencia_imagen,numero_lote,codigo_oficina_tenedora, codigo_entidad_emisora, id, referencia_ibrn, codigo_motivo_devolucion FROM cheque_saliente ";
	private final String findByQuerySqlHistorico = "SELECT codigo_entidad_procesadora,serial_cheque,codigo_banco_cheque,codigo_oficina_cheque,digito_control_cheque,cuenta_cheque,tipo_cheque,monto_cheque,cuenta_depositaria,fecha_camara,referencia_imagen,numero_lote,codigo_oficina_tenedora, codigo_entidad_emisora, id, referencia_ibrn, codigo_motivo_devolucion FROM cheque_saliente_historico ";
	// private final String
	// insertIntoSql="INSERT INTO cheque_saliente(id, codigo_banco_cheque, codigo_entidad_emisora, codigo_entidad_procesadora, codigo_moneda,codigo_oficina_cheque, cuenta_cheque, digito_control_cheque, estado, fecha, fecha_camara, ibrn_interno, id_lote, monto_cheque, numero_lote,referencia_imagen, serial_cheque, soporte_digital, soporte_fisico,  tipo_cheque) ";
	private final String insertIntoSql = "INSERT INTO cheque_saliente( codigo_banco_cheque, codigo_entidad_emisora, codigo_entidad_procesadora, codigo_moneda,codigo_oficina_cheque, cuenta_cheque, estado, fecha, fecha_camara, ibrn_interno, id_lote, monto_cheque, numero_lote,referencia_imagen, serial_cheque, soporte_digital, soporte_fisico,  tipo_cheque,lote,secuencia,codigo_oficina_procesadora,codigo_motivo_devolucion,digito_control_cheque,referencia_ibrn) ";
	private final String updateQuery = " UPDATE CHEQUE_SALIENTE SET ";

	public TransaccionSalienteDAO() {
		super();
	}

	public List<TransaccionSaliente> findAllByQuery(String whereClausele) {
		try {
			String query = this.findByQuerySql + " " + whereClausele + " ORDER BY ID ASC";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			List<TransaccionSaliente> listado = new ArrayList<TransaccionSaliente>();

			while (rs.next()) {
				TransaccionSaliente transaccion = new TransaccionSaliente();
				transaccion.setEntidadProcesadora(rs.getInt(1));
				transaccion.setSerial(rs.getString(2));
				transaccion.setEntidadCheque(rs.getInt(3));
				transaccion.setCodigoOficina(rs.getInt(4));
				transaccion.setDigitoChequeo(rs.getInt(5));
				transaccion.setCuenta(rs.getString(6));
				transaccion.setTc(rs.getInt(7));
				transaccion.setMonto(rs.getDouble(8));
				transaccion.setCuentaDepositaria(rs.getString(9));
				transaccion.setFecha(rs.getTimestamp(10));
				transaccion.setImagen(rs.getString(11));
				transaccion.setNumeroLote(rs.getString(12));
				transaccion.setOficinaTenedora(rs.getInt(13));
				transaccion.setEntidadEmisora(rs.getInt(14));
				transaccion.setId(rs.getInt(15));
				transaccion.setReferenciaIBRN(rs.getString(16));
				transaccion.setMotivoDevolucion(rs.getInt(17));

				listado.add(transaccion);
			}
			return listado;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			super.closeConnection();
		}
	}

	public List<TransaccionSaliente> findAllByQueryHistorico(String whereClausele) {
		try {
			String query = this.findByQuerySqlHistorico + " " + whereClausele + " ORDER BY ID ASC";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			List<TransaccionSaliente> listado = new ArrayList<TransaccionSaliente>();

			while (rs.next()) {
				TransaccionSaliente transaccion = new TransaccionSaliente();
				transaccion.setEntidadProcesadora(rs.getInt(1));
				transaccion.setSerial(rs.getString(2));
				transaccion.setEntidadCheque(rs.getInt(3));
				transaccion.setCodigoOficina(rs.getInt(4));
				transaccion.setDigitoChequeo(rs.getInt(5));
				transaccion.setCuenta(rs.getString(6));
				transaccion.setTc(rs.getInt(7));
				transaccion.setMonto(rs.getDouble(8));
				transaccion.setCuentaDepositaria(rs.getString(9));
				transaccion.setFecha(rs.getTimestamp(10));
				transaccion.setImagen(rs.getString(11));
				transaccion.setNumeroLote(rs.getString(12));
				transaccion.setOficinaTenedora(rs.getInt(13));
				transaccion.setEntidadEmisora(rs.getInt(14));
				transaccion.setId(rs.getInt(15));
				transaccion.setReferenciaIBRN(rs.getString(16));
				transaccion.setMotivoDevolucion(rs.getInt(17));

				listado.add(transaccion);
			}
			return listado;

		} catch (Exception e) {
			super.getLogger().error(e);
			return null;
		} finally {
			super.closeConnection();
		}
	}

	public TransaccionSaliente findByQuery(String whereClausele) {
		try {
			String query = this.findByQuerySql + " " + whereClausele;
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);

			if (rs.next()) {
				TransaccionSaliente transaccion = new TransaccionSaliente();
				transaccion.setEntidadProcesadora(rs.getInt(1));
				transaccion.setSerial(rs.getString(2));
				transaccion.setEntidadCheque(rs.getInt(3));
				transaccion.setCodigoOficina(rs.getInt(4));
				transaccion.setDigitoChequeo(rs.getInt(5));
				transaccion.setCuenta(rs.getString(6));
				transaccion.setTc(rs.getInt(7));
				transaccion.setMonto(rs.getDouble(8));
				transaccion.setCuentaDepositaria(rs.getString(9));
				transaccion.setFecha(rs.getTimestamp(10));
				transaccion.setImagen(rs.getString(11));
				transaccion.setNumeroLote(rs.getString(12));
				transaccion.setOficinaTenedora(rs.getInt(13));
				transaccion.setEntidadEmisora(rs.getInt(14));
				transaccion.setId(rs.getInt(15));
				transaccion.setReferenciaIBRN(rs.getString(16));
				transaccion.setMotivoDevolucion(rs.getInt(17));
				return transaccion;
			} else
				return null;

		} catch (Exception e) {
			super.getLogger().error(e);
			return null;
		} finally {
			super.closeConnection();
		}
	}

	public boolean actualizarEstado(TransaccionSaliente transaccion, int estado) {
		try {
			String query = "UPDATE cheque_saliente SET SOPORTE_FISICO = " + estado + " WHERE ID=" + transaccion.getId();
			Statement st = super.getConnection().createStatement();
			st.executeUpdate(query);
			return true;
		} catch (Exception e) {
			super.getLogger().error(e);
			return false;
		} finally {
			super.closeConnection();
		}
	}

	public boolean updateImage(TransaccionSaliente transaccion) {
		try {
			String query = "UPDATE cheque_saliente SET referencia_imagen='" + transaccion.getImagen() + "' WHERE id=" + transaccion.getId();
			Statement st = super.getConnection().createStatement();
			st.executeUpdate(query);
			return true;
		} catch (Exception e) {
			super.getLogger().error(e);
			return false;
		} finally {
			super.closeConnection();
		}
	}

	public boolean updateReferenciasArchivo(TransaccionSaliente transaccion, int idReferenciaCaja, int posicionLote) {
		try {
			String query = "UPDATE cheque_saliente SET id_archivador=" + idReferenciaCaja + " , posicion_lote=" + posicionLote + " WHERE id=" + transaccion.getId();
			Statement st = super.getConnection().createStatement();
			st.executeUpdate(query);
			return true;
		} catch (Exception e) {
			super.getLogger().error(e);
			return false;
		} finally {
			super.closeConnection();
		}
	}

	public int buscarDuplicados(ChequesDigitalizados cheque, int codigoEntidad) {
		// TODO Auto-generated method stub
		int salida=0;
		try
		{
			String referenciaIBRN = Utils.fillZerosLeft(""+cheque.getCheque().getTipoCheque(),4)+Utils.fillZerosLeft(""+cheque.getCheque().getCodigoBanco(),4)+cheque.getCheque().getSerial() + cheque.getCheque().getCuenta();
			String query="select lote,secuencia from cheque_saliente where referencia_ibrn='"+referenciaIBRN+"'";
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
		try {
			boolean valido = true;
			int numeroLote=-1;
			if (transacciones != null && transacciones.size() > 0) {
				ConexionRemotaBean miConexion = new ConexionRemotaBean();
				GestorArchivos gestor;
				miConexion = CacheUtils.getConexionRemota(Utils.fillZerosLeft(codigoEntidad + "", 4), AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_SALIENTE);
				gestor = GestorArchivosFactory.obtenerGestor(miConexion);
				ChequesDigitalizadosDAO digitalizados = new ChequesDigitalizadosDAO();
				ArrayList<AuditoriaChequeSaliente> listaAuditorias = new ArrayList<AuditoriaChequeSaliente>();
				ParametrosGenerales parametros = (new ParametrosGeneralesDAO()).obtenerParametrosSistema();
				String fechaCamaraStr = parametros.getFechaCamara();
				
				for (ChequesDigitalizados cheque : transacciones) {
					TipoDocumento reservado = CacheUtils.getTcs().get(Integer.parseInt(cheque.getCheque().getTipoCheque().trim()));
					if (((cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_APROBADO && cheque.getEstado() < ChequeDigitalizadoConstantes.CHEQUE_RECHAZADO) || (cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_NO_EXISTE && cheque.getEstado() <= ChequeDigitalizadoConstantes.CHEQUE_CUENTA_INACTIVA)) && reservado != null && !reservado.isBoleta()) {
						// se crea el query y se sube las imagenes respectivas
						cheque.setReferenciaUnica(Utils.construirReferenciaUnica(cheque, AplicacionConstantes.CAMARA_SALIENTE, fechaCamaraStr));
						String directorio = Utils.obtenerRutaImagen(cheque);
						int duplicado=buscarDuplicados(cheque, codigoEntidad);
						if(duplicado==0)
						{
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

						String query = DAOUtils.createInsertQuerySaliente(cheque,fechaCamaraStr);
						if (query != null) {
							query = insertIntoSql + query;
							try {
								Statement st = super.getConnection().createStatement();
								st.executeUpdate(query);								
								String querySelectAuditoria = "Select id from cheque_saliente WHERE ibrn_interno='" + cheque.getReferenciaUnica() + "'";
								ResultSet rs = st.executeQuery(querySelectAuditoria);
								int idCheque = -1;
								if (rs.next()) {
									idCheque = rs.getInt("id");
								}
								AuditoriaChequeSaliente auditoria = new AuditoriaChequeSaliente(cheque, AplicacionConstantes.ESTADO_APROBADO);
								auditoria.setIdTransaccion(idCheque);
								listaAuditorias.add(auditoria);
							
							} catch (Exception e) {
								e.printStackTrace();
								valido = false;
								continue;
							} finally {
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

					if (cheque.getEstado() > ChequeDigitalizadoConstantes.CHEQUE_PENDIENTE_APROBAR || reservado.isBoleta()) {
						digitalizados.actualizarChequeDigitalizado(cheque);
					}
				}
				
				if (numeroLote != -1) {
					LoteChequeDAO loteDAO = new LoteChequeDAO();
					loteDAO.actualizarEstado(numeroLote, AplicacionConstantes.CAMARA_SALIENTE, AplicacionConstantes.LOTE_ENTRANTE_CUADRADO);
					loteDAO.marcarDatosMigrados(numeroLote, AplicacionConstantes.CAMARA_SALIENTE);
				}
				AuditoriaTransaccionDAO auditor = new AuditoriaTransaccionDAO();
				auditor.registrarListaAuditoriasSalientes(listaAuditorias);
			}
			return valido;
		} catch (Exception e) {
			System.out.println("registrando transacciones ");
			if (this.getConn() != null)
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

				ArrayList<AuditoriaChequeSaliente> listaAuditorias = new ArrayList<AuditoriaChequeSaliente>();
				for (ChequesDigitalizados cheque : transacciones) {
					TipoDocumento reservado = CacheUtils.getTcs().get(Integer.parseInt(cheque.getCheque().getTipoCheque().trim()));
					if (((cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_APROBADO && cheque.getEstado() <= ChequeDigitalizadoConstantes.CHEQUE_RECHAZADO) || (cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_CUENTA_INVALIDA && cheque.getEstado() <= ChequeDigitalizadoConstantes.CHEQUE_CUENTA_INACTIVA)) && reservado != null ) {
						if(!reservado.isBoleta())
						{
						// buscar Registro en maestra, si existe lanzo el update, si se realiza el update entonces hago la auditoria
						Integer id = null;
						Integer bancoCheque = null,entidadEmisora=null,oficinaProcesadora=null;
						String serial=null;
						String cuenta=null;
						double monto=0.00;
						try {
							Statement st = super.getConnection().createStatement();
							String query = "select id,serial_cheque,monto_cheque,cuenta_cheque,codigo_banco_cheque,codigo_entidad_emisora,codigo_oficina_procesadora from cheque_saliente where lote=" + cheque.getIdLote() + " and secuencia=" + cheque.getPosicionLote();
							ResultSet rs = st.executeQuery(query);
							if (rs.next()) {
								id = rs.getInt("id");
								serial=rs.getString("serial_cheque");
								cuenta=rs.getString("cuenta_cheque");
								monto=rs.getDouble("monto_cheque");
								bancoCheque = rs.getInt("codigo_banco_cheque");
								entidadEmisora=rs.getInt("codigo_entidad_emisora");
								oficinaProcesadora=rs.getInt("codigo_oficina_procesadora");
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
							query +=",  codigo_banco_cheque="+cheque.getCheque().getCodigoBanco();
							query +=",  codigo_entidad_emisora="+cheque.getCodigoBancoEmisor();
							query +=",  codigo_oficina_procesadora="+cheque.getCodigoOficinaProcesadora();
							query += " where id="+id;

							if (query != null) {
								query = updateQuery + query;
								try {
									Statement st = super.getConnection().createStatement();
									st.executeUpdate(query);
									AuditoriaChequeSaliente auditoria = new AuditoriaChequeSaliente(cheque, AplicacionConstantes.ESTADO_APROBADO);
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
										modificado += "BancoEmisor Ant=" + entidadEmisora + " Act=" + cheque.getCodigoBancoEmisor();
									if (oficinaProcesadora != cheque.getCodigoOficinaProcesadora())
										modificado += "OficinaProcesadora Ant=" + oficinaProcesadora + " Act=" + cheque.getCodigoOficinaProcesadora();
									
									auditoria.setDescripcion(modificado);
									auditoria.setIdTransaccion(id);
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
				daoAuditoria.registrarListaAuditoriasSalientes(listaAuditorias);
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

}
