
package cr.tecnoware.sice.core.bean;

import java.util.ArrayList;

import cr.tecnoware.sice.applets.bean.Firma;



/**
 *
 * @author PERSONAL
 */
public class ConsultaFirmaResponse extends XMLBean{

    ArrayList<Firma> listaFirmas;
    String condicion;

    public ConsultaFirmaResponse() {
        this.listaFirmas = new ArrayList<Firma>();
        this.condicion = "";
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public ArrayList<Firma> getListaFirmas() {
        return listaFirmas;
    }

    public void setLiataFirmas(ArrayList<Firma> listaFirmas) {
        this.listaFirmas = listaFirmas;
    }
    
    public void imprimir(){
		System.out.println("Consulta Firmas");
		System.out.println("Condicion: "+condicion);
		System.out.println("Total Firmas: "+listaFirmas.size());
		for (int i = 0; i <listaFirmas.size(); i++) {
			System.out.println("Tipo ["+i+"]"+listaFirmas.get(i).getTipo());
		}
		
	}
}
