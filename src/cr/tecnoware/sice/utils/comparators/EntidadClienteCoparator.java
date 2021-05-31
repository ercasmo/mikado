package cr.tecnoware.sice.utils.comparators;

import java.util.Comparator;

import cr.tecnoware.sice.applets.bean.ReporteDataGeneral;



public class EntidadClienteCoparator implements Comparator<Object>{
	public int compare(Object uno, Object dos) {
		return ((ReporteDataGeneral)uno).getEntidadCliente().compareTo(((ReporteDataGeneral)dos).getEntidadCliente());
	}
}
