/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturaelectronica;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Txus
 */
public class FacturaDena {


    private String tipoDocumento; // DD0101
    private int numeroDocumento;  // DD0102
    private int codigoComprador;      // DD0201
    private XMLGregorianCalendar fechaFactura;       // DD0202
    private String razonSocialComprador; // AQ0101   
    private String direccionComprador; // AQ00003001ABELLA 1, 1 2Âº 3  
    // AQ0000400139770 LAREDO Cantabria 
    private String codigoPostalComprador;
    private String poblacionComprador;
    private String provinciaComprador;

    private String telefonoComprador; // DD0301
    private String nifComprador;      // DD0302
    private String nombreComercialVendedor; // DQ0101
    private String razonSocialVendedor; // DQ0201
    private String direccionVendedor; // DQ0301
    // DQ0000400139770 LAREDO (Cantabria)
    private String codigoPostalVendedor;
    private String localidadVendedor;
    private String provinciaVendedor;

    // DQ00005001Cif: B39366398    Tlfn: 942605082 
    private String nifVendedor;
    private String telefonoVendedor;
    private String rmRegistroMercantil;
    private String rmSeccion;
    private String rmTomo;
    private String rmFolio;
    private String rmHoja;
    private String rmLibro;
    private String rmInscripcion;   

    private ArrayList<LineaFactura> lineasFactura;

    
    private double baseFactura; // LT0102
    private double porcentajeIva; // LT0103
    private double importeIva; // LT0104
    private double porcentajeRecargoEquivalencia; // LT0105
    private double importeRecargoEquivalencia; // LT0106
    private double descuentoTotal; // Calculado sumando cada linea   
    private double totalFactura; // LT0107

    // Constructor
    public FacturaDena() {
        tipoDocumento = "";
        numeroDocumento = 0;
        codigoComprador = 0;
        /*fechaFactura = "";*/
        razonSocialComprador = "";
        direccionComprador = "";

        codigoPostalComprador = "";
        poblacionComprador = "";
        provinciaComprador = "";

        telefonoComprador = "";
        nifComprador = "";
        nombreComercialVendedor = "";
        razonSocialVendedor = "";
        direccionVendedor = "";

        codigoPostalVendedor = "";
        localidadVendedor = "";
        provinciaVendedor = "";

        nifVendedor = "";
        telefonoVendedor = "";
        rmRegistroMercantil = "";
        rmSeccion = "";
        rmTomo = "";
        rmFolio = "";
        rmHoja = "";
        rmLibro = "";
        rmInscripcion = "";

        lineasFactura = new ArrayList<LineaFactura>();

        baseFactura = 0.0;
        porcentajeIva = 0.0;
        importeIva = 0.0;
        porcentajeRecargoEquivalencia = 0.0;
        importeRecargoEquivalencia = 0.0;
        descuentoTotal = 0.0;
        totalFactura = 0.0;
    }

    public boolean cargaDesdeFichero() {

        boolean cargaCorrecta = true;
        String fichero = "C:\\DENAORIGINAL\\FORMST.TXT";
        LineaDena lineaDena = new LineaDena();
        int contadorPaginas = 0;

        try {
            FileReader fr = new FileReader(fichero);
            BufferedReader br = new BufferedReader(fr);

            String linea;
            while ((linea = br.readLine()) != null) {
                if (lineaDena.procesaLinea(linea)) {
                    // Si es IN = comienzo de página
                    if (lineaDena.getTipo() == 1) {
                        contadorPaginas++;
                    }
                    
                    // Si estamos en la segunda página, ya tenemos todos los datos de la cabecera, no tenemos que pasar por aquí.
                    if (contadorPaginas <= 1) {
                        switch (lineaDena.getTipo()) {
                            // DATOS_DOCUMENTO
                            case 2:
                                switch (lineaDena.getNivel()) {
                                    case 1:
                                        switch (lineaDena.getSubnivel()) {
                                            case 1:
                                                if (lineaDena.getValor().startsWith("FACTURA")) {
                                                    tipoDocumento = "Factura";
                                                }
                                                break;
                                            case 2:
                                                // Sacamos el número de documento sin espacios
                                                String strAux = lineaDena.getValor().trim();
                                                String strNumeroDocumento = "";
                                                for (int i = 0; i < strAux.length(); i++) {
                                                    if (strAux.charAt(i) != '.') {
                                                        strNumeroDocumento += strAux.charAt(i);
                                                    }
                                                }
                                                numeroDocumento = Integer.valueOf(strNumeroDocumento);
                                                break;
                                            case 3: // Número de hoja = irrelevante
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case 2:
                                        switch (lineaDena.getSubnivel()) {
                                            case 1:
                                                codigoComprador = Integer.valueOf(lineaDena.getValor().substring(0, 11).trim());
                                                break;
                                            case 2:
                                                fechaFactura = fechaDenaToXmlGregorianCalendar(lineaDena.getValor().substring(0, 8));
                                                break;                                            
                                        }
                                        break;
                                    case 3: switch (lineaDena.getSubnivel()) {
                                            case 1:
                                                telefonoComprador = lineaDena.getValor().trim();
                                                break;
                                            case 2:
                                                nifComprador = lineaDena.getValor().trim();
                                                break;                                            
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            // VARIOS
                            case 3:
                                switch (lineaDena.getNivel()) {
                                    case 1:
                                        switch (lineaDena.getSubnivel()) {
                                            case 1:
                                                break;
                                            case 2:
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                }
                                break;
                            // DATOS_VENDEDOR
                            case 4:
                                switch (lineaDena.getNivel()) {
                                    case 1:
                                        switch (lineaDena.getSubnivel()) {
                                            case 1:
                                                razonSocialVendedor = lineaDena.getValor().trim();
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case 2:
                                        switch (lineaDena.getSubnivel()) {
                                            case 1:
                                                nombreComercialVendedor = lineaDena.getValor().trim();
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case 3:
                                        switch (lineaDena.getSubnivel()) {
                                            case 1:
                                                direccionVendedor = lineaDena.getValor().trim();
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case 4:
                                        switch (lineaDena.getSubnivel()) {
                                            case 1:
                                                codigoPostalVendedor = lineaDena.getValor().substring(0, 5);

                                                // Obtenemos la línea sin el código postal
                                                String lineaStr = lineaDena.getValor().substring(5).trim();
                                                boolean esEspacio = false;
                                                int i = lineaStr.length() - 1;
                                                int separador = 0;
                                                char ch = lineaStr.charAt(i);
                                                // Vamos buscado los espacios hacia atrás. Cuando encontramos un,
                                                // Comprobamos si el caracter anterior está en Mayusculas, si es
                                                // Así, estamos en la 'parte de atrás' de la localidad.
                                                while (i > 0 && separador == 0) {
                                                    while (i > 0 && !Character.isSpaceChar(ch)) {
                                                        i--;
                                                        ch = lineaStr.charAt(i);
                                                    }
                                                    if (i > 0 && Character.isUpperCase(lineaStr.charAt(i - 1))) {
                                                        separador = i;
                                                    }
                                                }

                                                if (separador > 0) {
                                                    localidadVendedor = lineaStr.substring(0, separador).trim();
                                                    provinciaVendedor = lineaStr.substring(i).trim();
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                    case 5:
                                        switch (lineaDena.getSubnivel()) {
                                            case 1:
                                                int posNifVendedor = lineaDena.getValor().indexOf("Nif:");
                                                nifVendedor = lineaDena.getValor().substring(posNifVendedor + 4, posNifVendedor + 4 + 11).trim();

                                                int posTlfnVendedor = lineaDena.getValor().indexOf("Tlfn:");
                                                telefonoVendedor = lineaDena.getValor().substring(posTlfnVendedor + 5).trim();
                                                break;
                                            default:
                                                break;
                                        }
                                        break;
                                }
                                break;
                            // AQ A Quien
                            case 7:
                                switch (lineaDena.getNivel()) {
                                    case 1:
                                        razonSocialComprador = lineaDena.getValor().trim();
                                        break;
                                    case 2:

                                        break;
                                    case 3:
                                        direccionComprador = lineaDena.getValor().trim();
                                        break;
                                    case 4:
                                        codigoPostalComprador = lineaDena.getValor().substring(0, 5);

                                        // Obtenemos la línea sin el código postal
                                        String lineaStr = lineaDena.getValor().substring(5).trim();
                                        boolean esEspacio = false;
                                        int i = lineaStr.length() - 1;
                                        int separador = 0;
                                        char ch = lineaStr.charAt(i);
                                        // Vamos buscado los espacios hacia atrás. Cuando encontramos un,
                                        // Comprobamos si el caracter anterior está en Mayusculas, si es
                                        // Así, estamos en la 'parte de atrás' de la localidad.
                                        while (i > 0 && separador == 0) {
                                            while (i > 0 && !Character.isSpaceChar(ch)) {
                                                i--;
                                                ch = lineaStr.charAt(i);
                                            }
                                            if (i > 0 && Character.isUpperCase(lineaStr.charAt(i - 1))) {
                                                separador = i;
                                            }
                                        }

                                        if (separador > 0) {
                                            poblacionComprador = lineaStr.substring(0, separador).trim();
                                            provinciaComprador = lineaStr.substring(i).trim();
                                        }
                                        break;
                                }
                                break;
                        }
                    }
                    
                    // De todas formas, tenemos que ver si tenemos una LI, LT ó DR.
                    // Esto es: una Linea de Artículo, una Linea Total o los datos del Rgitro Mercantil.
                    // La Linea Total y la del Registro mercantil, sólo vienen una vez al final, no se repiten en cada página.
                    switch(lineaDena.getTipo()){                        
                        // Si tenemos una LI, en la primera linea, siempre viene el código
                        case 8: 
                            LineaFactura lineaFactura = new LineaFactura();
                            lineaFactura.setCodigoArticulo(lineaDena.getValor().trim());
                            // Ahora tenemos que leer el resto de partes de la línea, que siempre vienen seguidas.
                            for(int i = 0; i < 5; i++){
                                if((linea = br.readLine()) != null){
                                    lineaDena.procesaLinea(linea);
                                    if (lineaDena.getValor().trim().length() > 0) {
                                        switch (lineaDena.getSubnivel()) {
                                            case 2:
                                                lineaFactura.setDescripcionArticulo(lineaDena.getValor().trim());
                                                break;
                                            case 3:
                                                lineaFactura.setCantidad(Integer.valueOf(lineaDena.getValor().trim()));
                                                break;
                                            case 4:
                                                numeroDenaToDouble(lineaDena.getValor().trim());
                                                lineaFactura.setPrecio(numeroDenaToDouble(lineaDena.getValor().trim()));
                                                break;
                                            case 5:
                                                lineaFactura.setDescuento(Double.valueOf(lineaDena.getValor().trim()));
                                                descuentoTotal += lineaFactura.getDescuento();
                                                break;
                                            case 6:
                                                lineaFactura.setImporte(numeroDenaToDouble(lineaDena.getValor().trim()));
                                                break;
                                        }
                                    }
                                }
                            }
                            if(lineaFactura.getImporte() != 0.0)
                                lineasFactura.add(lineaFactura);
                            break;
                        // Linea total                        
                        case 9:
                            switch (lineaDena.getSubnivel()) {
                                case 1:
                                    break;
                                case 2: // Base IVA
                                    if (lineaDena.getValor().trim().length() > 0) {
                                        baseFactura = numeroDenaToDouble(lineaDena.getValor().trim());
                                    }
                                    break;
                                case 3: // Porcentaje IVA '21%'
                                    if (lineaDena.getValor().trim().length() > 0) {
                                        String strAux = lineaDena.getValor();
                                        strAux = strAux.replaceAll("%", " ").trim();
                                        porcentajeIva = Double.valueOf(strAux);
                                    }
                                    break;
                                case 4: // Importe IVA '3.242,98 *'
                                    if (lineaDena.getValor().trim().length() > 0) {
                                        importeIva = numeroDenaToDouble(lineaDena.getValor().trim());
                                    }
                                    break;
                                case 5: // Porcentaje recargo equivalencia
                                    break;
                                case 6: // Importe recargo equivalencia
                                    break;
                                case 7: // Total factura
                                    if (lineaDena.getValor().trim().length() > 0) {
                                        totalFactura = numeroDenaToDouble(lineaDena.getValor().trim());
                                    }
                                    break;
                        }                         
                            break;
                        // Linea registro mercantil     
                        // Siempre con el mismo formato
                        // R.M.Madrid S.8 T.7428 F.1 H.M-120178 L.0 I.27
                        case 10:
                                int pRegistroMercantil = 0;
                                int pSeccion = 0;
                                int pTomo = 0;
                                int pFolio = 0;
                                int pHoja = 0;
                                int pLibro = 0;
                                int pInscripcion = 0;
                                pRegistroMercantil = lineaDena.getValor().trim().indexOf("R.M.");
                                pSeccion = lineaDena.getValor().trim().indexOf("S.");
                                pTomo = lineaDena.getValor().trim().indexOf("T.");
                                pFolio = lineaDena.getValor().trim().indexOf("F.");
                                pHoja = lineaDena.getValor().trim().indexOf("H.");
                                pLibro = lineaDena.getValor().trim().indexOf("L.");
                                pInscripcion = lineaDena.getValor().trim().indexOf("I.");
                                
                                if(pRegistroMercantil != -1 && pSeccion != -1)
                                    rmRegistroMercantil = lineaDena.getValor().trim().substring(pRegistroMercantil, pSeccion).trim();
                                else
                                    rmRegistroMercantil = "No disponible";
                                
                                if(pSeccion != -1 && pTomo != -1)
                                    rmSeccion = lineaDena.getValor().trim().substring(pSeccion, pTomo).trim();
                                else
                                    rmSeccion = "No disponible";
                                
                                if(pTomo != -1 && pFolio != -1)
                                    rmTomo = lineaDena.getValor().trim().substring(pTomo, pFolio).trim();
                                else
                                    rmTomo = "No disponible";
                                
                                if(pFolio != -1 && pHoja != -1)
                                    rmFolio = lineaDena.getValor().trim().substring(pFolio, pHoja).trim();
                                else
                                    rmFolio = "No disponible";
                                
                                if(pHoja != -1 && pLibro != -1)
                                    rmHoja = lineaDena.getValor().trim().substring(pHoja, pLibro).trim();
                                else
                                    rmHoja = "No disponible";
                                
                                if(pLibro != -1 && pInscripcion != -1)
                                    rmLibro = lineaDena.getValor().trim().substring(pLibro, pInscripcion).trim();
                                else
                                    rmLibro = "No disponible";
                                
                                if(pInscripcion != -1)
                                    rmInscripcion = lineaDena.getValor().trim().substring(pInscripcion).trim();
                                else
                                    rmInscripcion = "No disponible";
                            break;
                    }
                }
            }

            fr.close();
        } catch (Exception e) {            
            System.out.println(e.getMessage());
            cargaCorrecta = false;
        }

        return cargaCorrecta;

    }

    private double numeroDenaToDouble(String numeroDenaStr) {

        // Convierte una cadena con un número formateado p.e.: '3.424,98 *' a un Double
        double resultado = 0.0;

        if(numeroDenaStr.contains("*"))
            numeroDenaStr = numeroDenaStr.replaceAll("\\*", " "); // '3.424.98 '        
        numeroDenaStr = numeroDenaStr.trim();               // '3.424,98'
                
        String strResultado = "";
        for (int i = 0; i < numeroDenaStr.length(); i++) {
            if (numeroDenaStr.charAt(i) != '.') {
                strResultado += numeroDenaStr.charAt(i);
            }
        }                                                   // '3424,98'
        
        strResultado = strResultado.replaceAll(",", ".");   // '3424.98'
        
        resultado = Double.valueOf(strResultado);
        

        return resultado;
    }
    
    private XMLGregorianCalendar fechaDenaToXmlGregorianCalendar(String strFechaDena) throws DatatypeConfigurationException{
        
        String strDia = "00";
        String strMes = "00";
        String strAnio = "00";
        GregorianCalendar fecha = new GregorianCalendar();
        
        if(strFechaDena.length() == 8){
            strDia = strFechaDena.substring(0, 2);
            strMes = strFechaDena.substring(3, 5);
            strAnio = strFechaDena.substring(6, 8);
        }
        
        if(Integer.valueOf(strAnio) > 50)
            strAnio = "19" + strAnio;
        else
            strAnio = "20" + strAnio;
                
        fecha.set(Integer.valueOf(strAnio), Integer.valueOf(strMes) - 1, Integer.valueOf(strDia));        
        
        
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(fecha.get(Calendar.YEAR), 
                                                                     fecha.get(Calendar.MONTH) + 1,
                                                                     fecha.get(Calendar.DAY_OF_MONTH),
                                                                     DatatypeConstants.FIELD_UNDEFINED,
                                                                     DatatypeConstants.FIELD_UNDEFINED, 
                                                                     DatatypeConstants.FIELD_UNDEFINED, 
                                                                     DatatypeConstants.FIELD_UNDEFINED, 
                                                                     DatatypeConstants.FIELD_UNDEFINED);        
        
    }
    
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public int getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(int numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public int getCodigoComprador() {
        return codigoComprador;
    }

    public void setCodigoComprador(int codigoComprador) {
        this.codigoComprador = codigoComprador;
    }

    public XMLGregorianCalendar getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(XMLGregorianCalendar fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public String getRazonSocialComprador() {
        return razonSocialComprador;
    }

    public void setRazonSocialComprador(String razonSocialComprador) {
        this.razonSocialComprador = razonSocialComprador;
    }

    public String getDireccionComprador() {
        return direccionComprador;
    }

    public void setDireccionComprador(String direccionComprador) {
        this.direccionComprador = direccionComprador;
    }

    public String getCodigoPostalComprador() {
        return codigoPostalComprador;
    }

    public void setCodigoPostalComprador(String codigoPostalComprador) {
        this.codigoPostalComprador = codigoPostalComprador;
    }

    public String getPoblacionComprador() {
        return poblacionComprador;
    }

    public void setPoblacionComprador(String poblacionComprador) {
        this.poblacionComprador = poblacionComprador;
    }

    public String getProvinciaComprador() {
        return provinciaComprador;
    }

    public void setProvinciaComprador(String provinciaComprador) {
        this.provinciaComprador = provinciaComprador;
    }

    public String getTelefonoComprador() {
        return telefonoComprador;
    }

    public void setTelefonoComprador(String telefonoComprador) {
        this.telefonoComprador = telefonoComprador;
    }

    public String getNifComprador() {
        return nifComprador;
    }

    public void setNifComprador(String nifComprador) {
        this.nifComprador = nifComprador;
    }

    public String getNombreComercialVendedor() {
        return nombreComercialVendedor;
    }

    public void setNombreComercialVendedor(String nombreComercialVendedor) {
        this.nombreComercialVendedor = nombreComercialVendedor;
    }

    public String getRazonSocialVendedor() {
        return razonSocialVendedor;
    }

    public void setRazonSocialVendedor(String razonSocialVendedor) {
        this.razonSocialVendedor = razonSocialVendedor;
    }

    public String getDireccionVendedor() {
        return direccionVendedor;
    }

    public void setDireccionVendedor(String direccionVendedor) {
        this.direccionVendedor = direccionVendedor;
    }

    public String getCodigoPostalVendedor() {
        return codigoPostalVendedor;
    }

    public void setCodigoPostalVendedor(String codigoPostalVendedor) {
        this.codigoPostalVendedor = codigoPostalVendedor;
    }

    public String getLocalidadVendedor() {
        return localidadVendedor;
    }

    public void setLocalidadVendedor(String localidadVendedor) {
        this.localidadVendedor = localidadVendedor;
    }

    public String getProvinciaVendedor() {
        return provinciaVendedor;
    }

    public void setProvinciaVendedor(String provinciaVendedor) {
        this.provinciaVendedor = provinciaVendedor;
    }

    public String getNifVendedor() {
        return nifVendedor;
    }

    public void setNifVendedor(String nifVendedor) {
        this.nifVendedor = nifVendedor;
    }

    public String getTelefonoVendedor() {
        return telefonoVendedor;
    }

    public void setTelefonoVendedor(String telefonoVendedor) {
        this.telefonoVendedor = telefonoVendedor;
    }
public String getRmRegistroMercantil() {
        return rmRegistroMercantil;
    }

    public void setRmRegistroMercantil(String rmRegistroMercantil) {
        this.rmRegistroMercantil = rmRegistroMercantil;
    }

    public String getRmSeccion() {
        return rmSeccion;
    }

    public void setRmSeccion(String rmSeccion) {
        this.rmSeccion = rmSeccion;
    }

    public String getRmTomo() {
        return rmTomo;
    }

    public void setRmTomo(String rmTomo) {
        this.rmTomo = rmTomo;
    }

    public String getRmFolio() {
        return rmFolio;
    }

    public void setRmFolio(String rmFolio) {
        this.rmFolio = rmFolio;
    }

    public String getRmHoja() {
        return rmHoja;
    }

    public void setRmHoja(String rmHoja) {
        this.rmHoja = rmHoja;
    }

    public String getRmLibro() {
        return rmLibro;
    }

    public void setRmLibro(String rmLibro) {
        this.rmLibro = rmLibro;
    }

    public String getRmInscripcion() {
        return rmInscripcion;
    }

    public void setRmInscripcion(String rmInscripcion) {
        this.rmInscripcion = rmInscripcion;
    }

    public ArrayList<LineaFactura> getLineasFactura() {
        return lineasFactura;
    }

    public void setLineasFactura(ArrayList<LineaFactura> lineasFactura) {
        this.lineasFactura = lineasFactura;
    }

    public double getBaseFactura() {
        return baseFactura;
    }

    public void setBaseFactura(double baseFactura) {
        this.baseFactura = baseFactura;
    }

    public double getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(double porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public double getImporteIva() {
        return importeIva;
    }

    public void setImporteIva(double importeIva) {
        this.importeIva = importeIva;
    }

    public double getPorcentajeRecargoEquivalencia() {
        return porcentajeRecargoEquivalencia;
    }

    public void setPorcentajeRecargoEquivalencia(double porcentajeRecargoEquivalencia) {
        this.porcentajeRecargoEquivalencia = porcentajeRecargoEquivalencia;
    }

    public double getImporteRecargoEquivalencia() {
        return importeRecargoEquivalencia;
    }

    public void setImporteRecargoEquivalencia(double importeRecargoEquivalencia) {
        this.importeRecargoEquivalencia = importeRecargoEquivalencia;
    }

    public double getTotalFactura() {
        return totalFactura;
    }

    public void setTotalFactura(double totalFactura) {
        this.totalFactura = totalFactura;
    }

    public double getDescuentoTotal() {
        return descuentoTotal;
    }

    public void setDescuentoTotal(double descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }
}
