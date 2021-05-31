package cr.tecnoware.sice.core.bean;

public class RegistroNotificacionResponse {

	private String codigoRespuesta;
	private String descripcionRespuesta;
	
	public String getCodigoRespuesta() {
		return codigoRespuesta;
	}
	public void setCodigoRespuesta(String codigoRespuesta) {
		this.codigoRespuesta = codigoRespuesta;
	}
	public String getDescripcionRespuesta() {
		return descripcionRespuesta;
	}
	public void setDescripcionRespuesta(String descripcionRespuesta) {
		this.descripcionRespuesta = descripcionRespuesta;
	}
	
	public void imprimir(){
		System.out.println("Registro Notificacion Response");
		System.out.println("Codigo Respuesta: "+codigoRespuesta);
		System.out.println("Descripcion Respuesta: "+descripcionRespuesta);
	}
	
}
