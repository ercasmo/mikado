package cr.tecnoware.sice.DAO;

import cr.tecnoware.sice.applets.bean.AuditoriaChequeEntrante;
import cr.tecnoware.sice.applets.bean.ChequeCorregido;
import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;
import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.applets.bean.ParametrosGenerales;
import cr.tecnoware.sice.applets.bean.TipoDocumento;
import cr.tecnoware.sice.applets.bean.TransaccionEntrante;
import cr.tecnoware.sice.applets.constantes.AplicacionConstantes;
import cr.tecnoware.sice.applets.constantes.ChequeConstantes;
import cr.tecnoware.sice.applets.constantes.ChequeDigitalizadoConstantes;
import cr.tecnoware.sice.context.ApplicationParameters;
import cr.tecnoware.sice.gestor_archivos.archivos.GestorArchivosFactory;
import cr.tecnoware.sice.gestor_archivos.interfaces.GestorArchivos;
import cr.tecnoware.sice.interfaz.ProcesarCamaraServices;
import cr.tecnoware.sice.services.AsignarFirmasService;
import cr.tecnoware.sice.utils.CacheUtils;
import cr.tecnoware.sice.utils.DAOUtils;
import cr.tecnoware.sice.utils.Utils;
import cr.tecnoware.sice.utils.encryption.CryptoUtils;
import cr.tecnoware.sice.utils.encryption.CryptoUtilsConstantes;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class TransaccionEntranteDAO extends DAO implements ProcesarCamaraServices {

	private final String findByQuerySql = "SELECT codigo_entidad_emisora,codigo_entidad_procesadora,codigo_oficina_cheque,cuenta_cheque,digito_control_cheque,fecha_camara,referencia_ibrn,referencia_imagen,monto_cheque,tipo_cheque,serial_cheque,id, codigo_banco_cheque, codigo_motivo_devolucion, estado, secuencia FROM cheque_entrante ";
	private final String findByQuerySqlHistorico = "SELECT codigo_entidad_emisora,codigo_entidad_procesadora,codigo_oficina_cheque,cuenta_cheque,digito_control_cheque,fecha_camara,referencia_ibrn,referencia_imagen,monto_cheque,tipo_cheque,serial_cheque,id, codigo_banco_cheque, codigo_motivo_devolucion FROM cheque_entrante_historico ";
	private final String insertIntoSql = "INSERT INTO cheque_entrante( codigo_banco_cheque, codigo_entidad_emisora, codigo_entidad_procesadora,codigo_moneda, codigo_oficina_cheque, cuenta_cheque, estado, fecha, fecha_camara,ibrn_interno,lote, monto_cheque, posicion_lote,  referencia_imagen, secuencia, serial_cheque,tipo_cheque," + "codigo_motivo_devolucion,digito_control_cheque,id_archivo_procesado,referencia_entidad_emisora,referencia_ibrn,proceso_devolucion,tipo_devolucion,estado_lecto02,usuario_revisor) ";
	private final String updateQuery = " UPDATE CHEQUE_ENTRANTE SET ";

	public TransaccionEntranteDAO() {
		super();
	}

	public List<TransaccionEntrante> findAllByQuery(String query) {
		try {
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(findByQuerySql + " " + query);
			List<TransaccionEntrante> listado = new ArrayList<TransaccionEntrante>();

			while (rs.next()) {
				TransaccionEntrante transaccion = new TransaccionEntrante();
				transaccion.setEntidadEmisora(rs.getInt(1));
				transaccion.setEntidadProcesadora(rs.getInt(2));
				transaccion.setCodigoOficina(rs.getInt(3));
				transaccion.setCuenta(rs.getString(4));
				transaccion.setDigitoChequeo(rs.getInt(5));
				transaccion.setFecha(rs.getTimestamp(6));
				transaccion.setReferenciaIBRN(rs.getString(7));
				transaccion.setImage(rs.getString(8));
				transaccion.setMonto(rs.getDouble(9));
				transaccion.setTc(rs.getInt(10));
				transaccion.setSerial(rs.getString(11));
				transaccion.setId(rs.getInt(12));
				transaccion.setMotivoDevolucion(rs.getInt(14));
				transaccion.setEntidadCheque(rs.getInt(13));
				transaccion.setEstado(rs.getInt(14));
				transaccion.setSecuencia(rs.getInt("secuencia"));
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

	public List<TransaccionEntrante> findAllByQuery2(String query, int limit) {
		try {
			String findByQuerySql2 = "SELECT top(" + limit + ") codigo_entidad_emisora,codigo_entidad_procesadora,codigo_oficina_cheque,cuenta_cheque,digito_control_cheque,fecha_camara,referencia_ibrn,referencia_imagen,monto_cheque,tipo_cheque,serial_cheque,id, codigo_banco_cheque, codigo_motivo_devolucion, estado,lote, secuencia FROM cheque_entrante ";
			// String findByQuerySql2 =
			// "SELECT codigo_entidad_emisora,codigo_entidad_procesadora,codigo_oficina_cheque,cuenta_cheque,digito_control_cheque,fecha_camara,referencia_ibrn,referencia_imagen,monto_cheque,tipo_cheque,serial_cheque,id, codigo_banco_cheque, codigo_motivo_devolucion, estado,lote, secuencia FROM cheque_entrante ";
			// "SELECT  codigo_entidad_emisora,codigo_entidad_procesadora,codigo_oficina_cheque,cuenta_cheque,digito_control_cheque,fecha_camara,referencia_ibrn,referencia_imagen,monto_cheque,tipo_cheque,serial_cheque,id, codigo_banco_cheque, codigo_motivo_devolucion, estado, lote, secuencia FROM cheque_entrante ";
			// String findByQuerySql2 =
			// "SELECT codigo_entidad_emisora,codigo_entidad_procesadora,codigo_oficina_cheque,cuenta_cheque,digito_control_cheque,fecha_camara,referencia_ibrn,referencia_imagen,monto_cheque,tipo_cheque,serial_cheque,id, codigo_banco_cheque, codigo_motivo_devolucion, estado FROM cheque_entrante ";

			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(findByQuerySql2 + " " + query);
			List<TransaccionEntrante> listado = new ArrayList<TransaccionEntrante>();

			while (rs.next()) {
				TransaccionEntrante transaccion = new TransaccionEntrante();
				transaccion.setEntidadEmisora(rs.getInt(1));
				transaccion.setEntidadProcesadora(rs.getInt(2));
				transaccion.setCodigoOficina(rs.getInt(3));
				transaccion.setCuenta(rs.getString(4));
				transaccion.setDigitoChequeo(rs.getInt(5));
				transaccion.setFecha(rs.getTimestamp(6));
				transaccion.setReferenciaIBRN(rs.getString(7));
				transaccion.setImage(rs.getString(8));
				transaccion.setMonto(rs.getDouble(9));
				transaccion.setTc(rs.getInt(10));
				transaccion.setSerial(rs.getString(11));
				transaccion.setId(rs.getInt(12));
				transaccion.setMotivoDevolucion(rs.getInt(14));
				transaccion.setEntidadCheque(rs.getInt(13));
				transaccion.setEstado(rs.getInt(14));
				transaccion.setLote(rs.getInt("lote"));
				transaccion.setSecuencia(rs.getInt("secuencia"));
				listado.add(transaccion);
			}

			return listado;

		} catch (Exception e) {
			super.getLogger().error(e);
			e.printStackTrace();
			return null;
		} finally {
			super.closeConnection();
		}
	}

	public List<TransaccionEntrante> findAllByQueryHistorico(String query) {
		try {
			System.out.println(findByQuerySqlHistorico + query);
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(findByQuerySqlHistorico + " " + query);
			List<TransaccionEntrante> listado = new ArrayList<TransaccionEntrante>();

			while (rs.next()) {
				TransaccionEntrante transaccion = new TransaccionEntrante();
				transaccion.setEntidadEmisora(rs.getInt(1));
				transaccion.setEntidadProcesadora(rs.getInt(2));
				transaccion.setCodigoOficina(rs.getInt(3));
				transaccion.setCuenta(rs.getString(4));
				transaccion.setDigitoChequeo(rs.getInt(5));
				transaccion.setFecha(rs.getTimestamp(6));
				transaccion.setReferenciaIBRN(rs.getString(7));
				transaccion.setImage(rs.getString(8));
				transaccion.setMonto(rs.getDouble(9));
				transaccion.setTc(rs.getInt(10));
				transaccion.setSerial(rs.getString(11));
				transaccion.setId(rs.getInt(12));
				transaccion.setMotivoDevolucion(rs.getInt(14));
				transaccion.setEntidadCheque(rs.getInt(13));
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

	public TransaccionEntrante findByQuery(String whereClausele) {
		try {
			String query = this.findByQuerySql + " ";
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(query);

			if (rs.next()) {
				TransaccionEntrante transaccion = new TransaccionEntrante();
				transaccion.setEntidadEmisora(rs.getInt(1));
				transaccion.setEntidadProcesadora(rs.getInt(2));
				transaccion.setCodigoOficina(rs.getInt(3));
				transaccion.setCuenta(rs.getString(4));
				transaccion.setDigitoChequeo(rs.getInt(5));
				transaccion.setFecha(rs.getTimestamp(6));
				transaccion.setReferenciaIBRN(rs.getString(7));
				transaccion.setImage(rs.getString(8));
				transaccion.setMonto(rs.getDouble(9));
				transaccion.setTc(rs.getInt(10));
				transaccion.setSerial(rs.getString(11));
				transaccion.setId(rs.getInt(12));
				transaccion.setMotivoDevolucion(rs.getInt(14));
				transaccion.setEntidadCheque(rs.getInt(13));
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

	public boolean updateImage(TransaccionEntrante transaccion) {
		try {

			String query = "UPDATE cheque_entrante SET referencia_imagen='" + transaccion.getImage() + "'";
			if (transaccion.getEstado() != ChequeConstantes.CHEQUE_ENTRANTE_PENDIENTE_INTERCAMBIO_CCE)
				query += ",estado=" + ChequeConstantes.CHEQUE_ENTRANTE_PENDIENTE_APLICAR_CORE;
			query += " WHERE id=" + transaccion.getId();
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

	public boolean updateMotivoDevolucion(TransaccionEntrante transaccion) {
		try {
			int bit = (transaccion.getMotivoDevolucion() == 0) ? AplicacionConstantes.REVISION_FIRMAS_ESTADO_REVISADO : AplicacionConstantes.CHEQUE_ENTRANTE_FORMA_FIRMA_RECHAZADA;
			String query = "UPDATE cheque_entrante SET codigo_motivo_devolucion=" + transaccion.getMotivoDevolucion() + " , firma_revisada=" + bit + " WHERE id=" + transaccion.getId();
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

	public boolean updateMotivoDevolucionFirma(TransaccionEntrante transaccion) {
		try {
			int bit = (transaccion.getMotivoDevolucion() == 0) ? AplicacionConstantes.REVISION_FIRMAS_ESTADO_REVISADO : AplicacionConstantes.CHEQUE_ENTRANTE_FORMA_FIRMA_RECHAZADA;
			String query = "";
			String querySELECT = "SELECT codigo_motivo_devolucion as MOTIVO FROM cheque_entrante where id=" + transaccion.getId();
			Statement st = super.getConnection().createStatement();
			ResultSet rs = st.executeQuery(querySELECT);
			int codigoMotivoActual = 0;
			if (rs.next()) {
				codigoMotivoActual = rs.getInt("MOTIVO");
			}

			if (codigoMotivoActual > 0) {// Solamente se marca el cheque como
											// firma revisada
				query = "UPDATE cheque_entrante SET firma_revisada=" + AplicacionConstantes.REVISION_FIRMAS_ESTADO_REVISADO + " WHERE id=" + transaccion.getId();
			} else {
				if ((transaccion.getTc() == ChequeConstantes.CHEQUE_CAJA || transaccion.getTc() == ChequeConstantes.CHEQUE_CERTIFICADO) && (bit == AplicacionConstantes.CHEQUE_ENTRANTE_FORMA_FIRMA_RECHAZADA)) {
					query = "UPDATE cheque_entrante SET codigo_motivo_devolucion=" + transaccion.getMotivoDevolucion() + " , firma_revisada=" + bit + ",proceso_devolucion=" + AplicacionConstantes.PROCESO_DEVOLUCION_FIRMA + ",estado=" + ChequeConstantes.CHEQUE_ENTRANTE_RECHAZADO + " WHERE id=" + transaccion.getId();
				} else {
					query = "UPDATE cheque_entrante SET codigo_motivo_devolucion=" + transaccion.getMotivoDevolucion() + " , firma_revisada=" + bit + ",proceso_devolucion=" + AplicacionConstantes.PROCESO_DEVOLUCION_FIRMA + " WHERE id=" + transaccion.getId();
				}
			}

			st.executeUpdate(query);
			return true;
		} catch (Exception e) {
			super.getLogger().error(e);
			return false;
		} finally {
			super.closeConnection();
		}
	}

	public boolean updateReferenciasArchivo(TransaccionEntrante transaccion, int idReferenciaCaja, int posicionLote) {
		try {
			String query = "UPDATE cheque_entrante SET id_archivador=" + idReferenciaCaja + " , posicion_lote=" + posicionLote + " WHERE id=" + transaccion.getId();
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
		int salida = 0;
		try {
			String query = "select lote,secuencia from cheque_entrante where referencia_ibrn='" + cheque.getReferenciaIbrn() + "'";
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
			int numeroLoteAplicar=-1;
			int numeroLote = -1;
			if (transacciones != null && transacciones.size() > 0) {
				numeroLoteAplicar=transacciones.get(0).getIdLote();
				ConexionRemotaBean miConexion = new ConexionRemotaBean();
				GestorArchivos gestor;
				miConexion = CacheUtils.getConexionRemota(Utils.fillZerosLeft(codigoEntidad + "", 4), AplicacionConstantes.CONEXION_REPOSITORIO_IMAGENES_ENTRANTE);
				gestor = GestorArchivosFactory.obtenerGestor(miConexion);
				ChequesDigitalizadosDAO digitalizados = new ChequesDigitalizadosDAO();
				ParametrosGenerales parametros = (new ParametrosGeneralesDAO()).obtenerParametrosSistema();
				String fechaCamaraStr = parametros.getFechaCamara();
				Lecto02DAO dao = new Lecto02DAO();
				ArrayList<AuditoriaChequeEntrante> listaAuditorias = new ArrayList<AuditoriaChequeEntrante>();
				// Se marcan los cheques certificados y cheques de caja como
				// recibidos.
				for (ChequesDigitalizados cheque : transacciones) {
					TipoDocumento reservado = CacheUtils.getTcs().get(Integer.parseInt(cheque.getCheque().getTipoCheque().trim()));
					if (!reservado.isBoleta()) {
						dao.establecerTipoCheque(cheque);
						cheque.setReferenciaUnica(Utils.construirReferenciaUnica(cheque, AplicacionConstantes.CAMARA_ENTRANTE, fechaCamaraStr));
					}
				}

				// RevisarComportamiento de boletas
				AsignarFirmasService servicioAsignarFirmas = new AsignarFirmasService();
				servicioAsignarFirmas.asignarFirmas((ArrayList<ChequesDigitalizados>) transacciones);

				for (ChequesDigitalizados cheque : transacciones) {
					TipoDocumento reservado = CacheUtils.getTcs().get(Integer.parseInt(cheque.getCheque().getTipoCheque().trim()));
					if (((cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_APROBADO && cheque.getEstado() < ChequeDigitalizadoConstantes.CHEQUE_RECHAZADO) || (cheque.getEstado() >= ChequeDigitalizadoConstantes.CHEQUE_NO_EXISTE && cheque.getEstado() <= ChequeDigitalizadoConstantes.CHEQUE_CUENTA_INACTIVA)) && reservado != null && !reservado.isBoleta()) {
						String referenciaIBRN = Utils.fillZerosLeft("" + cheque.getCheque().getTipoCheque(), 4) + Utils.fillZerosLeft("" + cheque.getCheque().getCodigoBanco(), 4) + cheque.getCheque().getSerial() + cheque.getCheque().getCuenta();
						cheque.setReferenciaIbrn(referenciaIBRN);
						int duplicado = buscarDuplicados(cheque, codigoEntidad);
						// se crea el query y se sube las imagenes respectivas
						if (duplicado == 0) {
							String directorio = Utils.obtenerRutaImagen(cheque);
							try {
								int res = 0;
								res = gestor.subirImagen(directorio, cheque.getReferenciaUnica() + ChequeConstantes.IMAGEN_ANVERSO, CryptoUtils.codificar(cheque.getImagenAnverso(), CryptoUtilsConstantes.METODO_AES));
								if (res == 0) {
									super.getLogger().error("subiendo imagen Anverso " + cheque.getReferenciaUnica());
									valido = false;
									continue;
								} else {
									res = gestor.subirImagen(directorio, cheque.getReferenciaUnica() + ChequeConstantes.IMAGEN_REVERSO, CryptoUtils.codificar(cheque.getImagenReverso(), CryptoUtilsConstantes.METODO_AES));
									if (res == 0) {
										super.getLogger().error("subiendo imagen Reverso " + cheque.getReferenciaUnica());
										valido = false;
										continue;
									}
								}

							} catch (Exception ee) {
								super.getLogger().info("Error subiendo Imagenes ", ee);
								valido = false;
								continue;
							}

							String query = DAOUtils.createInsertQueryEntrante(cheque, fechaCamaraStr);
							if (query != null) {
								query = insertIntoSql + query;
								try {
									Statement st = super.getConnection().createStatement();
									st.executeUpdate(query);
									String querySelectAuditoria = "Select id from cheque_entrante WHERE referencia_ibrn='" + cheque.getReferenciaIbrn() + "'";
									ResultSet rs = st.executeQuery(querySelectAuditoria);
									int idCheque = -1;
									if (rs.next()) {
										idCheque = rs.getInt("id");
									}
									AuditoriaChequeEntrante auditoria = new AuditoriaChequeEntrante(cheque, AplicacionConstantes.ESTADO_APROBADO);
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
				if (numeroLote != -1) {
					LoteChequeDAO loteDAO = new LoteChequeDAO();
					loteDAO.actualizarEstado(numeroLote, AplicacionConstantes.CAMARA_ENTRANTE, AplicacionConstantes.LOTE_ENTRANTE_CUADRADO);
				}
				AuditoriaTransaccionDAO daoAuditoria = new AuditoriaTransaccionDAO();
				daoAuditoria.registrarListaAuditoriasEntrantes(listaAuditorias);
				Lecto02DAO lectoDao = new Lecto02DAO();
				lectoDao.marcarRegistrosLote();
				marcarChequesExcentos(numeroLoteAplicar);

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
				ArrayList<AuditoriaChequeEntrante> listaAuditorias = new ArrayList<AuditoriaChequeEntrante>();
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
							Integer bancoCheque = null;
							Integer tc = null;
							String serial = null;
							String cuenta = null;
							double monto = 0.00;
							Integer bancoEmisor = null;
							Integer oficinaCheque = null;
							try {
								Statement st = super.getConnection().createStatement();
								String query = "select id,serial_cheque,monto_cheque,cuenta_cheque,codigo_banco_cheque,tipo_cheque,codigo_entidad_emisora,codigo_oficina_cheque from cheque_entrante where lote=" + cheque.getIdLote() + " and secuencia=" + cheque.getPosicionLote();
								ResultSet rs = st.executeQuery(query);
								if (rs.next()) {
									id = rs.getInt("id");
									serial = rs.getString("serial_cheque");
									cuenta = rs.getString("cuenta_cheque");
									monto = rs.getDouble("monto_cheque");
									bancoCheque = rs.getInt("codigo_banco_cheque");
									tc = rs.getInt("tipo_cheque");
									bancoEmisor = rs.getInt("codigo_entidad_emisora");
									oficinaCheque = rs.getInt("codigo_oficina_cheque");
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
								Integer procesoDevo = 0;
								if (cheque.getCodigoMotivoDevolucion() != 0) {
									estado = 2;
									motivo = procesoDevo = 1;
								}
								String query = " serial_cheque='" + Utils.fillZerosLeft(Utils.removeLeadingZeros(cheque.getCheque().getSerial()), 8) + "' ";
								query += ", monto_cheque=" + cheque.getCheque().getMonto() + " ";
								query += ", cuenta_cheque='" + cheque.getCheque().getCuenta() + "' ";
								query += ", estado=" + estado;
								query += ", codigo_motivo_devolucion=" + motivo;
								query += ", proceso_devolucion=" + procesoDevo;
								query += ", tipo_cheque=" + cheque.getCheque().getTipoCheque();
								query += ", codigo_entidad_emisora= " + cheque.getCodigoBancoEmisor();
								query += ", codigo_oficina_cheque=" + cheque.getCheque().getCodigoOficina().trim();
								query += " where id=" + id;

								if (query != null) {
									query = updateQuery + query;
									try {
										Statement st = super.getConnection().createStatement();
										st.executeUpdate(query);

										AuditoriaChequeEntrante auditoria = new AuditoriaChequeEntrante(cheque, AplicacionConstantes.ESTADO_APROBADO);
										auditoria.setIdTransaccion(id);
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
										if (bancoEmisor != cheque.getCodigoBancoEmisor())
											;
										modificado += "Banco Emisor Ant=" + bancoEmisor + " Act=" + cheque.getCodigoBancoEmisor();
										if (oficinaCheque != Integer.parseInt(cheque.getCheque().getCodigoOficina()))
											modificado += "OficinaCheque Ant=" + tc + " Act=" + cheque.getCheque().getCodigoOficina();
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
				daoAuditoria.registrarListaAuditoriasEntrantes(listaAuditorias);
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

	public void marcarChequesExcentos(int lote) {
		try {
			Statement st = super.getConnection().createStatement();
			List<Double> listaMontos = new MontoLiofDAO().obtenerLimites();
			int motivoMarca = ApplicationParameters.getInstance().getInt("liof.motivo.proceso.automatico");
			if (null != listaMontos && listaMontos.size() > 0) {
				double limiteInferior = listaMontos.get(0);
				double limiteSuperior = listaMontos.get(1);
				String sqlBase = "UPDATE cheque_entrante set motivo_exonerar_impuesto = (Select motivo_asociado from custom_conexLiof where custom_conexLiof.cuenta = cheque_entrante.cuenta_cheque) WHERE cheque_entrante.aplica_impuesto is null ";
				String sqlAdicional = "UPDATE cheque_entrante set aplica_impuesto=1 WHERE motivo_exonerar_impuesto=" + motivoMarca;
				if (limiteInferior > 0.0 && limiteSuperior > 0.0) {
					sqlBase += " and cheque_entrante.monto_cheque between " + Utils.doubleToString(limiteInferior) + " AND " + Utils.doubleToString(limiteSuperior);
					sqlAdicional += " and monto_cheque between " + Utils.doubleToString(limiteInferior) + " AND " + Utils.doubleToString(limiteSuperior);
				} else if (limiteInferior > 0.0) {
					sqlBase += " and cheque_entrante.monto_cheque >=" + Utils.doubleToString(limiteInferior);
					sqlAdicional += " and monto_cheque >=" + Utils.doubleToString(limiteInferior);
				} else if (limiteSuperior > 0.0) {
					sqlBase += " and cheque_entrante.monto_cheque <=" + Utils.doubleToString(limiteSuperior);
					sqlAdicional += " and monto_cheque <=" + Utils.doubleToString(limiteSuperior);
				}
				if (lote > 0) {
					sqlBase += " and cheque_entrante.lote =" + lote;
					sqlAdicional += " and lote =" + lote;
				}
				System.out.println(sqlBase);
				System.out.println(sqlAdicional);
				st.executeUpdate(sqlBase);
				st.executeUpdate(sqlAdicional);
			}
		} catch (Exception e) {
			super.getLogger().error(e);
		} finally {
			super.closeConnection();
		}
	}

}
