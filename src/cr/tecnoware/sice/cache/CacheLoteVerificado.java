package cr.tecnoware.sice.cache;

import java.util.LinkedHashMap;

public class CacheLoteVerificado
{

    public CacheLoteVerificado()
    {
    }

    public static Long obtenerFechaRegistros(String llaveBusqueda)
    {
        return (Long)mapaLotes.get(llaveBusqueda);
    }

    public static void agregarFechaRegistros(String llave, Long valor)
    {
        try
        {
            mapaLotes.put(llave, valor);
        }
        catch(Exception exception) { }
    }

    private static LinkedHashMap<String,Long> mapaLotes = new LinkedHashMap<String,Long>();

}
