/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.tecnoware.sice.core.bean;

/**
 *
 * @author PERSONAL
 */
public class RegistroNotificacionRequest extends XMLBean {

    String idProceso;
    String descripcion;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(String idProceso) {
        this.idProceso = idProceso;
    }


}
