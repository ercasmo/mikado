package cr.tecnoware.sice.utils.comparators;

import java.util.Comparator;

import cr.tecnoware.sice.applets.bean.ReporteDataGeneral;


public class OficinaClienteComparator implements Comparator<Object> {

	
	public int compare(Object uno, Object dos) {
		// TODO Auto-generated method stub
		return ((ReporteDataGeneral)uno).getOficinaCliente().compareTo(((ReporteDataGeneral)dos).getOficinaCliente());
	}

}
