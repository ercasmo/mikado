/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cr.tecnoware.sice.core.bean;

/**
 * 
 * @author PERSONAL
 */
public class ConsultaCuentaResponse extends XMLBean{
	private String cuenta;
	private String estado;
	private String tipoCuenta;
	

	public String getTipoCuenta() {
		return tipoCuenta;
	}

	public void setTipoCuenta(String tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public void imprimir(){
		System.out.println("Cuenta Response");
		System.out.println("Cuenta: "+cuenta);
		System.out.println("Estado: "+estado);
		System.out.println("Tipo Cuenta: "+tipoCuenta);
	}

}
