package cr.tecnoware.sice.listener;



import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cr.tecnoware.sice.rmi.Server;

public class Listener implements ServletContextListener {

	
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void contextInitialized(ServletContextEvent arg0) {
		Server.init();		
	}

	

}
