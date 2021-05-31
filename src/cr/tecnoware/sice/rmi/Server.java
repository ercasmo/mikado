package cr.tecnoware.sice.rmi;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;

import cr.tecnoware.sice.context.ApplicationParameters;

// Referenced classes of package cr.tecnoware.sice.rmi:
//            RemoteServicesImpl

public class Server
{
	
    private static Registry registry;
    private static final ApplicationParameters app = ApplicationParameters.getInstance();
    private static final Logger log = Logger.getLogger("application");
    public static final int STATUS_INICIADO = 1;
    public static final int STATUS_DETENIDO = 0;
    private static int status = 0;


    public Server()
    {
    }

    public static boolean init()
    {
        try
        {
            registry = LocateRegistry.createRegistry(1099);
            RemoteServicesImpl impl = new RemoteServicesImpl();
            String ipRmi[] = InetAddress.getLocalHost().toString().split("/");
            Naming.rebind((new StringBuilder("rmi://")).append(ipRmi[1]).append("/").append(app.getString("configuracion.recursoCompartido")).toString(), impl);
            log.info("Iniciado Correctamente el Server");
            System.out.println((new StringBuilder("Servicio iniciado correctamente... rmi://")).append(ipRmi[1]).append("/").append(app.getString("configuracion.recursoCompartido")).toString());
            mostrarBindings(app.getString("configuracion.recursoCompartido"));
            setStatus(1);
        }
        catch(Exception e)
        {
            log.error("Error en el inicio del server ", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean close()
    {
        try
        {
            String ipRmi[] = InetAddress.getLocalHost().toString().split("/");
            log.info("Servidor Detenido");
            Naming.unbind((new StringBuilder("rmi://")).append(ipRmi[1]).append("/").append(app.getString("configuracion.recursoCompartido")).toString());
            UnicastRemoteObject.unexportObject(registry, true);
            setStatus(0);
        }
        catch(Exception e)
        {
            log.error("Error deteniendo el server ", e);
            return false;
        }
        return true;
    }

    public static void mostrarBindings(String nombre_registro)
    {
        try
        {
            String bindings[] = Naming.list(nombre_registro);
            log.info("Vinculos disponibles:");
            for(int i = 0; i < bindings.length; i++)
                log.info(bindings[i]);

        }
        catch(Exception e)
        {
            log.error("Ocurrio un error al listar los enlaces ", e);
        }
    }

    public static void setStatus(int status)
    {
        status = status;
    }

    public static int getStatus()
    {
        return status;
    }
}
