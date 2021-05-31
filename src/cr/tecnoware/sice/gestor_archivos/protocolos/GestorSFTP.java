package cr.tecnoware.sice.gestor_archivos.protocolos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import cr.tecnoware.sice.applets.bean.ConexionRemotaBean;
import cr.tecnoware.sice.gestor_archivos.interfaces.GestorArchivos;

public class GestorSFTP implements GestorArchivos 
{
	private JSch cliente;
    private Session sesion;
    private ChannelSftp canal;
    
    public GestorSFTP()
    {
        cliente = new JSch();
        sesion = null;
        canal = null;
    }
	
	public boolean conectar(ConexionRemotaBean conexion) 
	{
		Properties propiedad = new Properties();
        if (this.cliente != null)
        {
            this.desconectar(true);
            try
            {
                sesion = cliente.getSession(conexion.getUsuario(), conexion.getDireccionIp(), conexion.getPuerto());
                sesion.setPassword(conexion.getClave());
                propiedad.put("StrictHostKeyChecking", "no");
                sesion.setConfig(propiedad);
                sesion.connect();
                if(sesion.isConnected())
                {
                    canal = (ChannelSftp) sesion.openChannel("sftp");
                    canal.connect();
                    return canal.isConnected();
                }
                else
                {
                    return false;
                }
            }
            catch (JSchException ex)
            {
                java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        else
        {
             return false;
        }
	}

	public boolean desconectar(boolean inmediato) 
	{
		boolean estatus1, estatus2;
        if (this.cliente != null)
        {
            if (this.sesion != null && this.sesion.isConnected())
            {
                if(this.canal != null && this.canal.isConnected())
                {
                    this.canal.disconnect();
                    estatus1 = !this.canal.isConnected();
                }
                else
                {
                    estatus1 = false;
                }
                this.sesion.disconnect();
                estatus2 = !this.sesion.isConnected();
            }
            else
            {
                estatus1 = false;
                estatus2 = false;
            }
        }
        else
        {
            estatus1 = false;
            estatus2 = false;
        }
        return estatus1 && estatus2;
	}

	public boolean hayConexionActiva() 
	{
		if (this.cliente != null)
        {
            if (this.sesion != null && this.sesion.isConnected())
            {
                if (this.canal != null && this.canal.isConnected())
                {
                    return true;
                }
            }
        }
        return false;
	}

	public byte[] traerImagen(String rutaCompleta) 
	{
		if (this.hayConexionActiva())
        {
            try
            {
                ByteArrayOutputStream contenidoArchivo = new ByteArrayOutputStream();
                rutaCompleta = this.verificarRutaArchivo(rutaCompleta);
                canal.get(rutaCompleta, contenidoArchivo);
                return contenidoArchivo.toByteArray();
            }
            catch (SftpException ex)
            {
                java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
	}

	@SuppressWarnings("static-access")
	public int subirImagen(String ruta, String nombreArchivo, byte[] contenidoArchivo) 
	{
		ByteArrayInputStream contArchivo = null;
        String auxiliarRuta = null;
        if (this.hayConexionActiva())
        {
            contArchivo = new ByteArrayInputStream(contenidoArchivo);
            try
            {
                if (ruta.indexOf("/") == 0)
                {
                    ruta = ruta.substring(1, ruta.length());
                }
               
                if (ruta.substring(ruta.length()-1).compareToIgnoreCase("/")==0)
                {
                    ruta = ruta.substring(0, ruta.length()-1);
                }

                auxiliarRuta = ruta;
                this.canal.cd("../");
                this.canal.cd(this.canal.getHome());
                while (auxiliarRuta.indexOf("/") != -1)
                {
                    try
                    {
                        this.canal.mkdir("./" + auxiliarRuta.substring(0, auxiliarRuta.indexOf("/")));
                    }
                    catch(Exception e){}
                    this.canal.cd(auxiliarRuta.substring(0, auxiliarRuta.indexOf("/")));
                    auxiliarRuta = auxiliarRuta.substring(auxiliarRuta.indexOf("/") + 1, auxiliarRuta.length());
                }
                try
                {
                    this.canal.mkdir("./" + auxiliarRuta);
                }
                catch(Exception e){}
                this.canal.cd(auxiliarRuta);
                canal.put(contArchivo, nombreArchivo, canal.OVERWRITE);
                try
                {
                    contArchivo.close();
                }
                catch (IOException ex)
                {
                    java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            catch (SftpException ex)
            {
                java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
                return 0;
            }
        }
        else
        {
            return 0;
        }
        return 1;
	}
	
	private String verificarRutaArchivo(String rutaActual)
    {
        if (rutaActual.indexOf("/") == 0)
        {
            return "." + rutaActual;
        }
        else
        {
            return "./" + rutaActual;
        }
    }
	
	public synchronized boolean existeArchivo(String rutaNombreArchivo)
    {
        String ruta, nombreArchivo;
        int indice;
        rutaNombreArchivo = this.verificarRutaArchivo(rutaNombreArchivo, "Izquierdo");
        indice = rutaNombreArchivo.lastIndexOf("/");
        if (indice != -1)
        {
            ruta = rutaNombreArchivo.substring(0, indice + 1);
            nombreArchivo = rutaNombreArchivo.substring(indice + 1, rutaNombreArchivo.length());
            if (this.hayConexionActiva())
            {
                try
                {
                    canal.rename(ruta + nombreArchivo, ruta + "_" + nombreArchivo);
                    canal.rename(ruta + "_" + nombreArchivo, ruta + nombreArchivo);
                    return true;
                }
                catch (SftpException ex)
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    public synchronized String[] listarArchivosDirectorio(String ruta)
    {
        String[] listaArchivos = null;
        int i = 0;
        Vector <Object> directorio = new Vector<Object>();
        if (this.hayConexionActiva())
        {
            try
            {
                ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
                directorio = canal.ls(ruta);
                listaArchivos = new String[directorio.size()];
                for (Object cadena : directorio)
                {
                    listaArchivos[i++] = cadena.toString();
                }
            }
            catch (SftpException ex)
            {
                java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
	return listaArchivos;
    }

    public synchronized byte[] obtenerBytesArchivo(String ruta)
    {
        return this.obtenerBytesArchivoBinario(ruta);
    }

    public synchronized byte[] obtenerBytesArchivoBinario(String ruta)
    {
        long inicio, fin;
        inicio = System.currentTimeMillis();
        if (this.hayConexionActiva())
        {
            try
            {
                ByteArrayOutputStream contenidoArchivo = new ByteArrayOutputStream();
                ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
                canal.get(ruta, contenidoArchivo);
                fin = System.currentTimeMillis();
                //System.out.println("Tiempo obteniendo bytes(seg): " + ((double)(fin - inicio) / 1000));
                //System.out.println("Tamaño archivo (bytes): " + contenidoArchivo.toByteArray().length);
                return contenidoArchivo.toByteArray();
            }
            catch (SftpException ex)
            {
                java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public synchronized byte[] obtenerBytesArchivo(String ruta, int numBytes)
    {
        long inicio, fin;
        byte [] contenidoArchivo = null;
        InputStream stream;
        boolean exito = false;
        inicio = System.currentTimeMillis();
        if (this.hayConexionActiva())
        {
            try
            {
                ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
                stream = canal.get(ruta);
                contenidoArchivo = new byte[numBytes];
                try
                {
                    stream.read(contenidoArchivo);
                    exito = true;
                    stream.close();
                }
                catch (IOException ex)
                {
                    java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
                    exito = false;
                }
            }
            catch (SftpException ex)
            {
                java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
                exito = false;
            }
        }
        else
        {
            exito = false;
        }
        fin = System.currentTimeMillis();
        if (exito)
        {
            //System.out.println("Tiempo obteniendo bytes(seg): " + ((double)(fin - inicio) / 1000));
            //System.out.println("Tamaño archivo (bytes): " + contenidoArchivo.length);
            return contenidoArchivo;
        }
        else
        {
            //System.out.println("No se pudo obtener el contenido del archivo.");
            return null;
        }
    }

    public synchronized boolean subirArchivo(String ruta, String nombreArchivo, byte[] contenidoArchivo, int tipoContenido)
    {
        return this.subirArchivo(ruta, nombreArchivo, contenidoArchivo);
    }

    @SuppressWarnings("static-access")
    public boolean subirArchivo(String ruta, String nombreArchivo, byte[] archivo)
    {
        ByteArrayInputStream contenidoArchivo = null;
        if (this.hayConexionActiva())
        {
            contenidoArchivo = new ByteArrayInputStream(archivo);
            try
            {
                ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
                ruta = this.verificarRutaArchivo(ruta, "Derecho");
                canal.put(contenidoArchivo, ruta + nombreArchivo, canal.OVERWRITE);
                try
                {
                    contenidoArchivo.close();
                }
                catch (IOException ex)
                {
                    java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            catch (SftpException ex)
            {
                java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        else
        {
            return false;
        }
        return true;
    }

    public synchronized boolean renombrarArchivo(String ruta, String nombreOriginal, String nuevoNombre)
    {
        if (this.hayConexionActiva())
        {
            try
            {
                ruta = this.verificarRutaArchivo(ruta, "Izquierdo");
                ruta = this.verificarRutaArchivo(ruta, "Derecho");
                canal.rename(ruta + nombreOriginal, ruta  + nuevoNombre);
                return true;
            }
            catch (SftpException ex)
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public void crearDirectorio(String ruta)
    {
        String auxiliarRuta = null;
        if (this.hayConexionActiva())
        {
            try
            {
                if (ruta.indexOf("/") == 0)
                {
                    ruta = ruta.substring(1, ruta.length());
                }
                if(ruta.lastIndexOf("/") == ruta.length() - 1)
                {
                    ruta = ruta.substring(0, ruta.length() - 1);
                }
                auxiliarRuta = ruta;
                this.canal.cd("../");
                this.canal.cd(this.canal.getHome());
                while (auxiliarRuta.indexOf("/") != -1)
                {
                    try
                    {
                        this.canal.mkdir("./" + auxiliarRuta.substring(0, auxiliarRuta.indexOf("/")));
                    }
                    catch(Exception e){}
                    this.canal.cd(auxiliarRuta.substring(0, auxiliarRuta.indexOf("/")));
                    auxiliarRuta = auxiliarRuta.substring(auxiliarRuta.indexOf("/") + 1, auxiliarRuta.length());
                }
                try
                {
                    this.canal.mkdir("./" + auxiliarRuta);
                }
                catch(Exception e){}
                this.canal.cd(auxiliarRuta);
            }
            catch (SftpException ex)
            {
                java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    public synchronized List [] bajarArchivosPorLote(List listadoRutas)
    {
        int i;
        List [] contenidoArchivo;
        contenidoArchivo = new List[2];
        contenidoArchivo[0] = new ArrayList();
        contenidoArchivo[1] = new ArrayList();
        if (this.hayConexionActiva())
        {
            for (i = 0; i < listadoRutas.size(); i++)
            {
                if (this.existeArchivo(listadoRutas.get(i).toString()))
                {
                    try
                    {
                        ByteArrayOutputStream contenido = new ByteArrayOutputStream();
                        canal.get(this.verificarRutaArchivo(listadoRutas.get(i).toString(), "Izquierdo"),contenido);
                        contenidoArchivo[0].add(listadoRutas.get(i).toString());
                        contenidoArchivo[1].add(contenido.toByteArray());
                    }
                    catch (SftpException ex)
                    {
                        java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return contenidoArchivo;
    }


    private String verificarRutaArchivo(String rutaActual, String extremo)
    {
        if (extremo.equals("Derecho"))
        {
            if (rutaActual.lastIndexOf("/") == rutaActual.length() - 1)
            {
                return rutaActual;
            }
            else
            {
                return rutaActual + "/";
            }
        }
        else if (extremo.equals("Izquierdo"))
        {
            if (rutaActual.indexOf("/") == 0)
            {
                return "." + rutaActual;
            }
            else
            {
                return "./" + rutaActual;
            }
        }
        return rutaActual;
    }

    public boolean borrarArchivos(String ruta,String[] listaArchivos) //lista de archivos a eliminar debe ser un [] de string
    {
        int i;
        boolean exito = false;
        if (this.hayConexionActiva())
        {
             ruta = this.verificarRutaArchivo(ruta,"Izquierdo");
             ruta = this.verificarRutaArchivo(ruta,"Derecho");
            if (listaArchivos != null)
            {
                for (i = 0; i < listaArchivos.length; i++)
                {
                    try
                    {
                        this.canal.rm(ruta + listaArchivos[i]);
                        exito = true;
                    }
                    catch (SftpException ex)
                    {
                        java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
                        exito = false;
                    }
                }
            }
        }
        return exito;
    }

    public boolean borrarArchivo(String ruta,String[] archivo)
    {
        int i;
        boolean exito = false;
        String[] listaArchivos = null;
        if (this.hayConexionActiva())
        {
             ruta = this.verificarRutaArchivo(ruta,"Izquierdo");
             ruta = this.verificarRutaArchivo(ruta,"Derecho");
            //listaArchivos = this.listarArchivosDirectorio(ruta);
            if (archivo != null)
            {
                    try
                    {
                        this.canal.rm(ruta + archivo);
                        exito = true;
                    }
                    catch (SftpException ex)
                    {
                        java.util.logging.Logger.getLogger(GestorSFTP.class.getName()).log(Level.SEVERE, null, ex);
                        exito = false;
                    }
            }
        }
        return exito;
    }
}
