package cr.tecnoware.sice.core.bean;

public class ConsultaCuentaDepositariaResponse {
	private String cuentaDepositaria;
	private String estado;
	private String tipoCuenta;
	
	public String getCuentaDepositaria() {
		return cuentaDepositaria;
	}
	public void setCuentaDepositaria(String cuentaDepositaria) {
		this.cuentaDepositaria = cuentaDepositaria;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getTipoCuenta() {
		return tipoCuenta;
	}
	public void setTipoCuenta(String tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}
	
	public void imprimir(){
		System.out.println("Cuenta Depositaria Response");
		System.out.println("Cuenta Depositaria: "+cuentaDepositaria);
		System.out.println("Estado: "+estado);
		System.out.println("Tipo Cuenta: "+tipoCuenta);
	}
}
