package cr.tecnoware.sice.core.test;

import cr.tecnoware.sice.DAO.EntidadDAO;

public class HiloTest extends Thread{
  private int id;
  
  public HiloTest(int _iod){
	 id=_iod;
  }
	
	public void run() {				
		EntidadDAO dao= new EntidadDAO();
		try {
			sleep(1000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		System.out.println("id: "+id+" --> "+dao.getByCodigo(101));
	}
	
}
