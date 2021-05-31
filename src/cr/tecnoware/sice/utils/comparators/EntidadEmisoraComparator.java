package cr.tecnoware.sice.utils.comparators;

import java.util.Comparator;

import cr.tecnoware.sice.applets.bean.ReporteDataGeneral;



public class EntidadEmisoraComparator implements Comparator<Object>{

	
	public int compare(Object uno, Object dos) {
		return ((ReporteDataGeneral)uno).getEntidadEmisora().compareTo(((ReporteDataGeneral)dos).getEntidadEmisora());
	}
}
