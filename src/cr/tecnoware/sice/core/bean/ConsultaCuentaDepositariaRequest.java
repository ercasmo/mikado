/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.tecnoware.sice.core.bean;

/**
 * 
 * @author PERSONAL
 */
public class ConsultaCuentaDepositariaRequest extends XMLBean {

	String cuentaAsociadaAlCheque;
	String numeroDocumentoAsociadoAlCheque;
	
	public String getCuentaAsociadaAlCheque() {
		return cuentaAsociadaAlCheque;
	}
	public void setCuentaAsociadaAlCheque(String cuentaAsociadaAlCheque) {
		this.cuentaAsociadaAlCheque = cuentaAsociadaAlCheque;
	}
	public String getNumeroDocumentoAsociadoAlCheque() {
		return numeroDocumentoAsociadoAlCheque;
	}
	public void setNumeroDocumentoAsociadoAlCheque(String numeroDocumentoAsociadoAlCheque) {
		this.numeroDocumentoAsociadoAlCheque = numeroDocumentoAsociadoAlCheque;
	}

}
