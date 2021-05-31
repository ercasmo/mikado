package cr.tecnoware.sice.core.bean;

public class SolicitudServicioBean extends XMLBean {
	String id;
	String idTransaccion;
	String fechahora;
	String usuario;
	String locacionUsuario;
	String agencia;
	String sistema;
	String programa;
	String referencia;
	String cliente;
	String cuenta;
	String tarjeta;
	String bousr;

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getLocacionUsuario() {
		return locacionUsuario;
	}

	public void setLocacionUsuario(String locacionUsuario) {
		this.locacionUsuario = locacionUsuario;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	public String getPrograma() {
		return programa;
	}

	public void setPrograma(String programa) {
		this.programa = programa;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public String getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
	}

	public String getBousr() {
		return bousr;
	}

	public void setBousr(String bousr) {
		this.bousr = bousr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdTransaccion() {
		return idTransaccion;
	}

	public void setIdTransaccion(String idTransaccion) {
		this.idTransaccion = idTransaccion;
	}

	public String getFechahora() {
		return fechahora;
	}

	public void setFechahora(String fechahora) {
		this.fechahora = fechahora;
	}

	/*
	 * <idTransaccion></idTransaccion> <fechahora></fechahora>
	 * <usuario></usuario> <locacionUsuario></locacionUsuario>
	 * <agencia></agencia> <sistema></sistema> <programa></programa>
	 * <referencia></referencia> <cliente></cliente> <cuenta></cuenta>
	 * <tarjeta></tarjeta> <bousr></bousr>
	 */
}
