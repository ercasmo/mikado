package cr.tecnoware.sice.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ApplicationParameters
{
  private static ApplicationParameters instance = new ApplicationParameters();
  private Properties prop = new Properties();
  
  private ApplicationParameters()
  {
    String resource = System.getenv("SICE_CCE_CONF") + File.separator + "sice_config.properties";
    InputStream is = null;
    try
    {
      System.out.println(resource);
      is = new FileInputStream(new File(resource));
      this.prop.load(is);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
  }
  
  public static ApplicationParameters getInstance()
  {
    return instance;
  }
  
  public String getString(String key)
  {
    return this.prop.getProperty(key);
  }
  
  public int getInt(String key)
  {
    return Integer.parseInt(this.prop.getProperty(key));
  }
  
  public boolean getBoolean(String key)
  {
    return Boolean.valueOf(this.prop.getProperty(key)).booleanValue();
  }
  
  public double getDouble(String key)
  {
    return Double.parseDouble(this.prop.getProperty(key));
  }
}
