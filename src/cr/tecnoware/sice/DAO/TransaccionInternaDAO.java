package cr.tecnoware.sice.DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cr.tecnoware.sice.applets.bean.AuditoriaChequeInterno;
import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.applets.bean.ParametrosGenerales;
import cr.tecnoware.sice.applets.bean.TipoDocumento;
import cr.tecnoware.sice.applets.bean.TransaccionInterna;
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

public class TransaccionInternaDAO extends DAO implements ProcesarCamaraServices {
	// private final String
	// insertIntoSql="INSERT INTO cheque_interno(id, codigo_banco_cheque, codigo_entidad_emisora, codigo_entidad_procesadora, codigo_moneda,codigo_oficina_cheque, cuenta_cheque, estado, fecha, fecha_camara, ibrn_interno, id_lote, monto_cheque, numero_lote,referencia_imagen, serial_cheque, soporte_digital, soporte_fisico,  tipo_cheque) ";
	private final String insertIntoSql = "INSERT INTO cheque_interno( codigo_banco_cheque, codigo_entidad_emisora, codigo_entidad_procesadora, codigo_moneda,codigo_oficina_cheque, cuenta_cheque, estado, fecha, fecha_camara, ibrn_interno, id_lote, monto_cheque, numero_lote,referencia_imagen, serial_cheque, soporte_digital, soporte_fisico,  tipo_cheque,secuencia,codigo_motivo_devolucion,codigo_oficina_procesadora,digito_control_cheque,estado_verilect,lote,referencia_ibrn) ";
	private final String updateQuery = " UPDATE CHEQUE_INTERNO SET ";

	public int buscarDuplicados(ChequesDigitalizados cheque, int codigoEntidad) {
		int salida = 0;
		try {
			String referenciaIBRN = Utils.fillZerosLeft("" + cheque.getCheque().getTipoCheque(), 4) + Utils.fillZerosLeft("" + cheque.getCheque().getCodigoBanco(), 4) + cheque.getCheque().getSerial() + cheque.getCheque().getCuenta() + Utils.fillZerosLeft(Utils.removeLeadingZeros(cheque.getCodigoOficinaProcesadora() + ""), 4);
			String query = "select lote,secuencia from cheque_interno where referencia_ibrn='" + referenciaIBRN + "'";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);

			boolean enc = false;
			int lote = -1, secuencia = -1;
			if (rs.next()) {
				lote = rs.getInt("lote");
				secuencia = rs.getInt("secuencia");
				enc = true;
			}

			if (enc) {
				if (lote == cheque.getIdLote() && secuencia == cheque.getPosicionLote()) // se
																							// trata
																							// exactamente
																							// dle
																							// mismo
																							// registro
																							// por
																							// lo
																							// cual
																							// no
																							// se
																							// toma
																							// como
																							// duplicado
					salida = 1;
				else
					salida = 2;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			super.closeConnection();
		}
		return salida;
	}

	public boolean registrarTransaccionesCorregidas(List<ChequesDigitalizados> transacciones, int codigoEntidad) {
		try {
			boolean valido = true;
			int numeroLote = -1;
			if (transacciones != null && transacciones.size() > 0) {
				ConexionRemotaBean miConexion = new ConexionRemotaBean();
				GestorArchivos gestor;
				miConexion = CacheUtils.getConexionRemota(Utils.fillZerosLeft(codigoEntidad + "", 4), AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_INTERNA);
				gestor = GestorArchivosFactory.obtenerGestor(miConexion);
				ChequesDigitalizadosDAO digitalizados = new ChequesDigitalizadosDAO();
				ArrayList<AuditoriaChequeInterno> listaAuditorias = new ArrayList<AuditoriaChequeInterno>();
				ParametrosGenerales parametros = (new ParametrosGeneralesDAO()).obtenerParametrosSistema();
				String fechaCamaraStr = parametros.getFechaCamara();
				Lecto02DAO dao = new Lecto02DAO();
				// Se marcan los cheques certificados y cheques de caja como
				for (ChequesDigitalizados cheque : transacciones) {
					TipoDocumento reservado = CacheUtils.getTcs().get(Integer.parseInt(cheque.getCheque().getTipoCheque().trim()));
					if (!reservado.isBoleta()) {
						dao.establecerTipoCheque(cheque);
						cheque.setReferenciaUnica(Utils.construirReferenciaUnica(cheque, AplicacionConstantes.CAMARA_INTERNA, fechaCamaraStr));
					}
				}

				for (ChequesDigitalizados cheque : transacciones) {
					TipoDocumento reservado = CacheUtils.getTcs().get(Integer.parseInt(cheque.getCheque().getTipoCheque().trim()));
					if (((cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_APROBADO && cheque.getEstado() < ChequeDigitalizadoConstantes.CHEQUE_RECHAZADO) || (cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_NO_EXISTE && cheque.getEstado() <= ChequeDigitalizadoConstantes.CHEQUE_CUENTA_INACTIVA)) && reservado != null && !reservado.isBoleta()) {
						// se crea el query y se sube las imagenes respectivas
						int duplicado = buscarDuplicados(cheque, codigoEntidad);
						if (duplicado == 0) {
							String directorio = Utils.obtenerRutaImagen(cheque);
							try {
								comprobarImagen(cheque, gestor);
								int res = 0;
								res = gestor.subirImagen(directorio, cheque.getReferenciaUnica() + ChequeConstantes.IMAGEN_ANVERSO, CryptoUtils.codificar(cheque.getImagenAnverso(), CryptoUtilsConstantes.METODO_AES));
								if (res == 0) {
									super.getLogger().error("subiendo imagen Anverso " + cheque.getReferenciaUnica());
									valido = false;
									continue;
								}
								res = gestor.subirImagen(directorio, cheque.getReferenciaUnica() + ChequeConstantes.IMAGEN_REVERSO, CryptoUtils.codificar(cheque.getImagenReverso(), CryptoUtilsConstantes.METODO_AES));
								if (res == 0) {
									super.getLogger().error("subiendo imagen Reverso " + cheque.getReferenciaUnica());
									valido = false;
									continue;
								}

							} catch (Exception ee) {
								super.getLogger().info("Error subiendo Imagenes ", ee);
								valido = false;
								continue;
							}

							String query = DAOUtils.createInsertQueryInterno(cheque, fechaCamaraStr);
							if (query != null) {
								query = insertIntoSql + query;
								try {
									Statement st = super.getConnection().createStatement();
									st.executeUpdate(query);
									String querySelectAuditoria = "Select id from cheque_interno WHERE ibrn_interno='" + cheque.getReferenciaUnica() + "'";
									ResultSet rs = st.executeQuery(querySelectAuditoria);
									int idCheque = -1;
									if (rs.next()) {
										idCheque = rs.getInt("id");
									}
									AuditoriaChequeInterno auditoria = new AuditoriaChequeInterno(cheque, AplicacionConstantes.ESTADO_APROBADO);
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
						} else if (duplicado == 2) {
							cheque.setEstado(ChequeDigitalizadoConstantes.CHEQUE_DUPLICADO);
						}
					} else {
						numeroLote = cheque.getIdLote();
					}

					if (cheque.getEstado() > ChequeDigitalizadoConstantes.CHEQUE_PENDIENTE_APROBAR && cheque.getEstado() != ChequeDigitalizadoConstantes.CHEQUE_TC_INVALIDO || reservado.isBoleta()) {
						digitalizados.actualizarChequeDigitalizado(cheque);
					}
				}
				// Se actualiza el lote.
				if (numeroLote != -1) {
					LoteChequeDAO loteDAO = new LoteChequeDAO();
					loteDAO.actualizarEstado(numeroLote, AplicacionConstantes.CAMARA_INTERNA, AplicacionConstantes.LOTE_ENTRANTE_CUADRADO);
				}

				AuditoriaTransaccionDAO auditor = new AuditoriaTransaccionDAO();
				auditor.registrarListaAuditoriasInternas(listaAuditorias);

			}
			return valido;
		} catch (Exception e) {
			super.getLogger().error("Error registrando internas", e);
			return false;
		}
	}

	// SELECT fecha, id_Lote, numero_Lote, codigo_Moneda,
	// codigo_Oficina_Procesadora, codigo_Oficina_Tenedora,
	// codigo_Entidad_Emisora, codigo_Cajero,codigo_Motivo_Devolucion,
	// tipo_Devolucion, referencia_Core, serial_Cheque, codigo_Banco_Cheque,
	// codigo_Oficina_Cheque, cuenta_Cheque, tipo_Cheque, monto_Cheque,
	// digito_Control_Cheque, referencia_Imagen, id_Archivo_Generado,
	// id_Archivo_Procesado, cuenta_Depositaria, fecha_Camara, estado, id,
	// ibrn_Interno, soporte_Digital, soporte_Fisico, supervisor, diferido FROM
	// cheque_interno

	public List<TransaccionInterna> findAllByQueryVerilect(String whereClausele) {
		try {

			String query = this.findByQuerySqlVerilect + " " + whereClausele + " ORDER BY ID ASC";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			List<TransaccionInterna> listado = new ArrayList<TransaccionInterna>();

			while (rs.next()) {
				TransaccionInterna transaccion = new TransaccionInterna();
				transaccion.setId(rs.getInt(1));
				transaccion.setCuentaCheque(rs.getString(2));
				transaccion.setSerialCheque(rs.getString(3));
				transaccion.setEstado(rs.getInt(4));
				transaccion.setMontoCheque(rs.getDouble(5));
				transaccion.setCodigoOficinaProcesadora(rs.getInt(6));
				transaccion.setCodigoOficinaProcesadora(rs.getInt(7));
				transaccion.setTipoCheque(rs.getInt(9));
				transaccion.setId(rs.getInt(10));
				transaccion.setFechaCamara(rs.getDate(11));

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

	public List<TransaccionInterna> findAllByQuery(String whereClausele) {
		try {
			String query = this.findByQuerySql + " " + whereClausele + " ORDER BY ID ASC";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			List<TransaccionInterna> listado = new ArrayList<TransaccionInterna>();

			while (rs.next()) {
				TransaccionInterna transaccion = new TransaccionInterna();
				transaccion.setFecha(rs.getDate(1));
				transaccion.setIdLote(rs.getInt(2));
				transaccion.setNumeroLote(rs.getInt(3));
				transaccion.setCodigoMoneda(rs.getInt(4));
				transaccion.setCodigoOficinaProcesadora(rs.getInt(5));
				transaccion.setCodigoOficinaTenedora(rs.getInt(6));
				transaccion.setCodigoEntidadEmisora(rs.getInt(7));
				transaccion.setCodigoCajero(rs.getString(8));
				transaccion.setCodigoMotivoDevolucion(rs.getInt(9));
				transaccion.setTipoDevolucion(rs.getInt(10));
				transaccion.setReferenciaCore(rs.getInt(11));
				transaccion.setSerialCheque(rs.getString(12));
				transaccion.setCodigoBancoCheque(rs.getInt(13));
				transaccion.setCodigoOficinaCheque(rs.getInt(14));
				transaccion.setCuentaCheque(rs.getString(15));
				transaccion.setTipoCheque(rs.getInt(16));
				transaccion.setMontoCheque(rs.getDouble(17));
				transaccion.setDigitoControlCheque(rs.getInt(18));
				transaccion.setReferenciaImagen(rs.getString(19));
				transaccion.setIdArchivoGenerado(rs.getString(20));
				transaccion.setIdArchivoProcesado(rs.getString(21));
				transaccion.setCuentaDepositaria(rs.getString(22));
				transaccion.setFechaCamara(rs.getDate(23));
				transaccion.setEstado(rs.getInt(24));
				transaccion.setId(rs.getInt(25));
				transaccion.setIbrnInterno(rs.getString(26));
				transaccion.setSoporteDigital(rs.getInt(27));
				transaccion.setSoporteFisico(rs.getInt(28));
				transaccion.setSupervisor(rs.getString(29));
				transaccion.setDiferido(rs.getInt(30));

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

	public List<TransaccionInterna> findAllByQueryHistorico(String whereClausele) {
		try {
			String query = this.findByQuerySqlHistorico + " " + whereClausele + " ORDER BY ID ASC";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			List<TransaccionInterna> listado = new ArrayList<TransaccionInterna>();

			while (rs.next()) {
				TransaccionInterna transaccion = new TransaccionInterna();
				transaccion.setFecha(rs.getDate(1));
				transaccion.setIdLote(rs.getInt(2));
				transaccion.setNumeroLote(rs.getInt(3));
				transaccion.setCodigoMoneda(rs.getInt(4));
				transaccion.setCodigoOficinaProcesadora(rs.getInt(5));
				transaccion.setCodigoOficinaTenedora(rs.getInt(6));
				transaccion.setCodigoEntidadEmisora(rs.getInt(7));
				transaccion.setCodigoCajero(rs.getString(8));
				transaccion.setCodigoMotivoDevolucion(rs.getInt(9));
				transaccion.setTipoDevolucion(rs.getInt(10));
				transaccion.setReferenciaCore(rs.getInt(11));
				transaccion.setSerialCheque(rs.getString(12));
				transaccion.setCodigoBancoCheque(rs.getInt(13));
				transaccion.setCodigoOficinaCheque(rs.getInt(14));
				transaccion.setCuentaCheque(rs.getString(15));
				transaccion.setTipoCheque(rs.getInt(16));
				transaccion.setMontoCheque(rs.getDouble(17));
				transaccion.setDigitoControlCheque(rs.getInt(18));
				transaccion.setReferenciaImagen(rs.getString(19));
				transaccion.setIdArchivoGenerado(rs.getString(20));
				transaccion.setIdArchivoProcesado(rs.getString(21));
				transaccion.setCuentaDepositaria(rs.getString(22));
				transaccion.setFechaCamara(rs.getDate(23));
				transaccion.setEstado(rs.getInt(24));
				transaccion.setId(rs.getInt(25));
				transaccion.setIbrnInterno(rs.getString(26));
				transaccion.setSoporteDigital(rs.getInt(27));
				transaccion.setSoporteFisico(rs.getInt(28));
				transaccion.setSupervisor(rs.getString(29));
				transaccion.setDiferido(rs.getInt(30));

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

	public List<TransaccionInterna> findAllByQueryHistoricoVerilect(String whereClausele) {
		try {
			String query = this.findByQuerySqlHistoricoVerilect + " " + whereClausele + " ORDER BY ID ASC";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);
			List<TransaccionInterna> listado = new ArrayList<TransaccionInterna>();

			while (rs.next()) {
				TransaccionInterna transaccion = new TransaccionInterna();
				transaccion.setId(rs.getInt(1));
				transaccion.setCuentaCheque(rs.getString(2));
				transaccion.setSerialCheque(rs.getString(3));
				transaccion.setEstado(rs.getInt(4));
				transaccion.setMontoCheque(rs.getDouble(5));
				transaccion.setCodigoOficinaProcesadora(rs.getInt(6));
				transaccion.setCodigoOficinaProcesadora(rs.getInt(7));
				transaccion.setTipoCheque(rs.getInt(9));
				transaccion.setId(rs.getInt(10));
				transaccion.setFechaCamara(rs.getDate(11));

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

	private final String findByQuerySql = "SELECT fecha, id_Lote, numero_Lote, codigo_Moneda, codigo_Oficina_Procesadora, codigo_Oficina_Tenedora, codigo_Entidad_Emisora, codigo_Cajero,codigo_Motivo_Devolucion, tipo_Devolucion, referencia_Core, serial_Cheque, codigo_Banco_Cheque, codigo_Oficina_Cheque, cuenta_Cheque, tipo_Cheque,  monto_Cheque, digito_Control_Cheque, referencia_Imagen, id_Archivo_Generado, id_Archivo_Procesado, cuenta_Depositaria, fecha_Camara, estado, id, ibrn_Interno, soporte_Digital, soporte_Fisico, supervisor, diferido FROM cheque_interno ";
	private final String findByQuerySqlHistorico = "SELECT fecha, id_Lote, numero_Lote, codigo_Moneda, codigo_Oficina_Procesadora, codigo_Oficina_Tenedora, codigo_Entidad_Emisora, codigo_Cajero,codigo_Motivo_Devolucion, tipo_Devolucion, referencia_Core, serial_Cheque, codigo_Banco_Cheque, codigo_Oficina_Cheque, cuenta_Cheque, tipo_Cheque,  monto_Cheque, digito_Control_Cheque, referencia_Imagen, id_Archivo_Generado, id_Archivo_Procesado, cuenta_Depositaria, fecha_Camara, estado, id, ibrn_Interno, soporte_Digital, soporte_Fisico, supervisor, diferido FROM cheque_interno_historico ";
	private final String findByQuerySqlVerilect = "	SELECT id, cuenta, documento, estado_cheque, monto, agencia, entidad_procesadora,id_archivo_procesado, tipo_cheque, id_cheque,fecha_proceso FROM custom_verilect  ";
	private final String findByQuerySqlHistoricoVerilect = "SELECT id, cuenta, documento, estado_cheque, monto, agencia, entidad_procesadora,id_archivo_procesado, tipo_cheque, id_cheque,fecha_proceso FROM custom_verilect_historico ";

	public boolean actualizarTransaccionesCorregidas(List<ChequesDigitalizados> transacciones, int codigoEntidad) {
		try {
			boolean valido = true;
			if (transacciones != null && transacciones.size() > 0) {

				ChequesDigitalizadosDAO digitalizados = new ChequesDigitalizadosDAO();
				ArrayList<AuditoriaChequeInterno> listaAuditorias = new ArrayList<AuditoriaChequeInterno>();
				Lecto02DAO dao = new Lecto02DAO();
				// Se marcan los cheques certificados y cheques de caja como
				for (ChequesDigitalizados cheque : transacciones) {
					TipoDocumento reservado = CacheUtils.getTcs().get(Integer.parseInt(cheque.getCheque().getTipoCheque().trim()));
					if (!reservado.isBoleta()) {
						dao.establecerTipoCheque(cheque);
					}
				}

				for (ChequesDigitalizados cheque : transacciones) {
					TipoDocumento reservado = CacheUtils.getTcs().get(Integer.parseInt(cheque.getCheque().getTipoCheque().trim()));
					if (((cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_APROBADO && cheque.getEstado() <= ChequeDigitalizadoConstantes.CHEQUE_RECHAZADO) || (cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_NO_EXISTE && cheque.getEstado() <= ChequeDigitalizadoConstantes.CHEQUE_CUENTA_INACTIVA)) && reservado != null) {

						if (!reservado.isBoleta()) {
							// buscar Registro en maestra, si existe lanzo el
							// update, si se realiza el update entonces hago la
							// auditoria
							Integer id = null;
							Integer bancoCheque = null, entidadEmisora = null, oficinaProcesadora = null, oficinaCheque = null;
							Integer tc = null;
							String serial = null;
							String cuenta = null;
							double monto = 0.00;
							try {
								Statement st = super.getConnection().createStatement();
								String query = "select id,serial_cheque,monto_cheque,cuenta_cheque,codigo_banco_cheque,tipo_cheque,codigo_entidad_emisora,codigo_oficina_cheque,codigo_oficina_procesadora from cheque_interno where numero_lote=" + cheque.getIdLote() + " and secuencia=" + cheque.getPosicionLote();
								ResultSet rs = st.executeQuery(query);
								if (rs.next()) {
									id = rs.getInt("id");
									serial = rs.getString("serial_cheque");
									cuenta = rs.getString("cuenta_cheque");
									monto = rs.getDouble("monto_cheque");
									bancoCheque = rs.getInt("codigo_banco_cheque");
									tc = rs.getInt("tipo_cheque");
									entidadEmisora = rs.getInt("codigo_entidad_emisora");
									oficinaCheque = rs.getInt("codigo_oficina_cheque");
									oficinaProcesadora = rs.getInt("codigo_oficina_procesadora");

								}
							} catch (Exception e) {
								System.out.println("Buscando el registro en la maestra de entrantes ");
								e.printStackTrace();
							} finally {
								super.closeConnection();
							}

							if (id != null) {
								Integer estado = ChequeConstantes.CHEQUE_SALIENTE_RECIBIDO_REGIONAL;
								Integer motivo = 0;
								if (cheque.getCodigoMotivoDevolucion() != 0) {
									estado = 2;
									motivo = cheque.getCodigoMotivoDevolucion();
								}
								String query = " serial_cheque='" + Utils.fillZerosLeft(Utils.removeLeadingZeros(cheque.getCheque().getSerial()), 8) + "' ";
								query += ", monto_cheque=" + cheque.getCheque().getMonto() + " ";
								query += ", cuenta_cheque='" + cheque.getCheque().getCuenta() + "' ";
								query += ", estado=" + estado;
								query += ", codigo_motivo_devolucion=" + motivo;
								query += ", codigo_entidad_emisora=" + cheque.getCodigoBancoEmisor();
								query += ", codigo_oficina_cheque=" + cheque.getCheque().getCodigoOficina();
								query += ", codigo_oficina_procesadora=" + cheque.getCodigoOficinaProcesadora();
								query += " where id=" + id;

								if (query != null) {
									query = updateQuery + query;
									try {
										Statement st = super.getConnection().createStatement();
										st.executeUpdate(query);

										AuditoriaChequeInterno auditoria = new AuditoriaChequeInterno(cheque, AplicacionConstantes.ESTADO_APROBADO);
										String modificado = "";
										if (serial.compareToIgnoreCase(Utils.fillZerosLeft(Utils.removeLeadingZeros(cheque.getCheque().getSerial()), 8)) != 0)
											modificado += "Serial Ant=" + serial + " Act=" + Utils.fillZerosLeft(Utils.removeLeadingZeros(cheque.getCheque().getSerial()), 8);
										if (cuenta.compareToIgnoreCase(cuenta) != 0)
											modificado += "Cuenta Ant=" + serial + " Act=" + cheque.getCheque().getCuenta();
										if (monto != cheque.getCheque().getMonto())
											modificado += "Monto Ant=" + monto + " Act=" + cheque.getCheque().getMonto();
										if (bancoCheque != Integer.parseInt(cheque.getCheque().getCodigoBanco()))
											modificado += "Banco Ant=" + bancoCheque + " Act=" + cheque.getCheque().getCodigoBanco();
										if (tc != Integer.parseInt(cheque.getCheque().getTipoCheque()))
											modificado += "TipoCheque Ant=" + tc + " Act=" + cheque.getCheque().getTipoCheque();
										if (entidadEmisora != cheque.getCodigoBancoEmisor())
											modificado += "Banco Emisor Ant=" + entidadEmisora + " Act=" + cheque.getCodigoBancoEmisor();
										if (oficinaCheque != Integer.parseInt(cheque.getCheque().getCodigoOficina().trim()))
											modificado += "OficinaCheque Ant=" + oficinaCheque + " Act=" + cheque.getCheque().getCodigoOficina();
										if (oficinaProcesadora != cheque.getCodigoOficinaProcesadora())
											modificado += "OficinaProcesadora Ant=" + oficinaProcesadora + " Act=" + cheque.getCodigoOficinaProcesadora();

										auditoria.setDescripcion(modificado);
										auditoria.setAccion(ChequeConstantes.ACCION_CHEQUE_POST_CORREGIDO);
										auditoria.setIdTransaccion(id);
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
				daoAuditoria.registrarListaAuditoriasInternas(listaAuditorias);
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

	private void comprobarImagen(ChequesDigitalizados cheque, GestorArchivos gestor) {
		try {
			if ((cheque.getImagenAnverso() == null) || (cheque.getImagenReverso() == null)) {
				int dia = cheque.getFecha().getDate();
				int mes = cheque.getFecha().getMonth() + 1;
				int year = cheque.getFecha().getYear() + 1900;
				String nombre = cheque.getImagenNombre();
				String directorio = Utils.fillZerosLeft(new StringBuilder(String.valueOf(cheque.getEntidadProcesadora())).toString(), 4) + "/" + Utils.fillZerosLeft(new StringBuilder(String.valueOf(year)).toString(), 4) + "/" + Utils.fillZerosLeft(new StringBuilder(String.valueOf(mes)).toString(), 2) + "/" + Utils.fillZerosLeft(new StringBuilder(String.valueOf(dia)).toString(), 2) + "/TEMP/";
				if (cheque.getImagenAnverso() == null) {
					byte[] img = gestor.traerImagen(directorio + nombre);
					cheque.setImagenAnverso(CryptoUtils.decodificar(img, 1));
				}
				if (cheque.getImagenReverso() == null) {
					nombre = nombre.substring(0, nombre.length() - 4);
					byte[] img = gestor.traerImagen(directorio + nombre + ".TCR");
					cheque.setImagenReverso(CryptoUtils.decodificar(img, 1));
				}
			}
		} catch (Exception e) {
			System.out.println("Error validando cheque");
			e.printStackTrace();
		}
	}
}
