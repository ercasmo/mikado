package cr.tecnoware.sice.orm.bean;

import java.sql.Date;

public class Lotes extends DomainBase {

	private Date fecha;
	private Date hora;
	private String login;
	private int numLote;
	private int bancocliente;
	private int centronegocio;
	private int puntocomercial;
	private int codCajero;
	private int codMoneda;
	private boolean tipoCanje;
	private int numeroTransacciones;
	private float montoLote;
	private boolean amaestro;
	private boolean servidor;
	private String etiquetacd;
	private boolean interfaz;
	private boolean chequepagado;
	private int corregidos;
	private boolean enviado;
	private boolean historico;

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public int getNumLote() {
		return numLote;
	}

	public void setNumLote(int numLote) {
		this.numLote = numLote;
	}

	public int getBancocliente() {
		return bancocliente;
	}

	public void setBancocliente(int bancocliente) {
		this.bancocliente = bancocliente;
	}

	public int getCentronegocio() {
		return centronegocio;
	}

	public void setCentronegocio(int centronegocio) {
		this.centronegocio = centronegocio;
	}

	public int getPuntocomercial() {
		return puntocomercial;
	}

	public void setPuntocomercial(int puntocomercial) {
		this.puntocomercial = puntocomercial;
	}

	public int getCodCajero() {
		return codCajero;
	}

	public void setCodCajero(int codCajero) {
		this.codCajero = codCajero;
	}

	public int getCodMoneda() {
		return codMoneda;
	}

	public void setCodMoneda(int codMoneda) {
		this.codMoneda = codMoneda;
	}

	public boolean isTipoCanje() {
		return tipoCanje;
	}

	public void setTipoCanje(boolean tipoCanje) {
		this.tipoCanje = tipoCanje;
	}

	public int getNumeroTransacciones() {
		return numeroTransacciones;
	}

	public void setNumeroTransacciones(int numeroTransacciones) {
		this.numeroTransacciones = numeroTransacciones;
	}

	public float getMontoLote() {
		return montoLote;
	}

	public void setMontoLote(float montoLote) {
		this.montoLote = montoLote;
	}

	public boolean isAmaestro() {
		return amaestro;
	}

	public void setAmaestro(boolean amaestro) {
		this.amaestro = amaestro;
	}

	public boolean isServidor() {
		return servidor;
	}

	public void setServidor(boolean servidor) {
		this.servidor = servidor;
	}

	public String getEtiquetacd() {
		return etiquetacd;
	}

	public void setEtiquetacd(String etiquetacd) {
		this.etiquetacd = etiquetacd;
	}

	public boolean isInterfaz() {
		return interfaz;
	}

	public void setInterfaz(boolean interfaz) {
		this.interfaz = interfaz;
	}

	public boolean isChequepagado() {
		return chequepagado;
	}

	public void setChequepagado(boolean chequepagado) {
		this.chequepagado = chequepagado;
	}

	public int getCorregidos() {
		return corregidos;
	}

	public void setCorregidos(int corregidos) {
		this.corregidos = corregidos;
	}

	public boolean isEnviado() {
		return enviado;
	}

	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}

	public boolean isHistorico() {
		return historico;
	}

	public void setHistorico(boolean historico) {
		this.historico = historico;
	}

}
