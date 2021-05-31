package cr.tecnoware.sice.interfaz;

import java.util.List;

import cr.tecnoware.sice.applets.bean.ChequesDigitalizados;



public interface ProcesarCamaraServices {
    public boolean registrarTransaccionesCorregidas(List<ChequesDigitalizados> transacciones,int codigoEntidad);
    public int buscarDuplicados(ChequesDigitalizados cheque, int codigoEntidad); //0 No(No hay mismo IBRN), 1-No(mismo lote misma posicion ) 2- Si; en el caso de 0 y 1 se actualizaen cheque Digitalizado como valido, 2 se marca como duplicado
    public boolean actualizarTransaccionesCorregidas(List<ChequesDigitalizados> transacciones,int codigoEntidad);
    
}
