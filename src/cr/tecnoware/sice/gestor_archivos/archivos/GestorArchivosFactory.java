package cr.tecnoware.sice.gestor_archivos.archivos;

import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.gestor_archivos.interfaces.GestorArchivos;
import cr.tecnoware.sice.gestor_archivos.protocolos.GestorFTP;
import cr.tecnoware.sice.gestor_archivos.protocolos.GestorSFTP;

public class GestorArchivosFactory 
{
	public static GestorArchivos obtenerGestor(ConexionRemotaBean conexion)
    {
        GestorArchivos gestor;
        switch (conexion.getConexionSegura())
        {
            case GestorArchivos.GESTOR_FTP:
                gestor = new GestorFTP();
                break;
            case GestorArchivos.GESTOR_SFTP:
                gestor = new GestorSFTP();
                break;
            default:
                gestor = null;
                break;
        }
        if (gestor != null)
        {
            gestor.desconectar(true);
            if(gestor.conectar(conexion))
            {
                return gestor;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
}
