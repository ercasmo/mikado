package cr.tecnoware.sice.services;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import cr.tecnoware.sice.applets.constantes.ReporteConstantes;
import cr.tecnoware.sice.utils.Utils;

public class ManejadorReportesService {

	
	
	public byte[] generarReporte(List<Object> lista, int tipoReporte, Map<String, String> masterParams) {
		byte[] reporte = null;
		List<Object> listaData = null;
		
		try {
			String nombreReporte = ReporteConstantes.NOMBRE_REPORTES[tipoReporte];
			System.out.println("reporte_:" + nombreReporte);
			URL urlMaestro = this.getClass().getResource("/cr/tecnoware/sice/jasper/" + nombreReporte + ".jasper");
			if (urlMaestro == null) {
				System.out.println("No encuentro el archivo del reporte maestro.");
				System.exit(2);
			}
			
			JasperReport masterReport = null;
			masterReport = (JasperReport) JRLoader.loadObject(urlMaestro);
			masterParams.put("urlImagen", this.getClass().getResource("/cr/img/logo.png").toString());
			JasperPrint masterPrint = null;
			
			switch (tipoReporte) {
			case ReporteConstantes.TIPO_AGRUPAR_POR_ENTIDAD_EMISORA:
				listaData = Utils.calcularDataGeneral(lista, ReporteConstantes.TIPO_AGRUPAR_POR_ENTIDAD_EMISORA);
				break;
			case ReporteConstantes.TIPO_AGRUPAR_POR_ENTIDAD_CLIENTE:
				listaData = Utils.calcularDataGeneral(lista, ReporteConstantes.TIPO_AGRUPAR_POR_ENTIDAD_CLIENTE);
				break;
			case ReporteConstantes.TIPO_AGRUPAR_POR_OFICINA_SALIENTE:
				listaData = Utils.calcularDataGeneral(lista, ReporteConstantes.TIPO_AGRUPAR_POR_OFICINA);
				break;
			default:
				listaData = lista;
				break;
			}
			
        	JRDataSource jrd = new JRBeanCollectionDataSource(listaData);
        	masterPrint = JasperFillManager.fillReport(masterReport, masterParams, jrd);
        	File archivoReporteTemporal = File.createTempFile("rep", ".TMP");
        	JasperExportManager.exportReportToPdfFile(masterPrint, archivoReporteTemporal.getAbsolutePath());
        	reporte = Utils.pdfToByte(archivoReporteTemporal);
        	archivoReporteTemporal.delete();
        	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("View report error: " + e.getMessage());
		}
		return reporte;
	}
}
