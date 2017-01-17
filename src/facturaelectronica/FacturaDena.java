/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturaelectronica;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author Txus
 */
public class FacturaDena {

    private static final String PROVINCIA[] = {"ALAVA", "ALBACETE", "ALICANTE", "ALMERIA", "ASTURIAS", "AVILA", "BADAJOZ",
        "BALEARES", "BARCELONA", "BURGOS", "CACERES", "CADIZ", "CANTABRIA",
        "CASTELLON", "CIUDAD REAL", "CORDOBA", "CUENCA", "GERONA", "GRANADA",
        "GUADALAJARA", "GUIPUZCOA", "HUELVA", "HUESCA", "JAEN", "LA CORUÑA",
        "LA RIOJA", "LAS PALMAS", "LEON", "LERIDA", "LUGO", "MADRID", "MALAGA",
        "MURCIA", "NAVARRA", "ORENSE", "PALENCIA", "PONTEVEDRA", "SALAMANCA",
        "sANTA CRUZ TENERIFE", "SEGOVIA", "SEVILLA", "SORIA", "TARRAGONA",
        "TERUEL", "TOLEDO", "VALENCIA", "VALLADOLID", "VIZCAYA", "ZAMORA",
        "ZARAGOZA", "CEUTA", "MELILLA"};

    private String tipoDocumento; // DD0101
    private int numeroDocumento;  // DD0102
    private int codigoComprador;      // DD0201
    private String fechaFactura;       // DD0202
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

    private ArrayList<LineaFactura> lineasFactura;

    private double baseFactura; // LT0102
    private double porcentajeIva; // LT0103
    private double importeIva; // LT0104
    private double porcentajeRecargoEquivalencia; // LT0105
    private double importeRecargoEquivalencia; // LT0106
    private double totalFactura; // LT0107

    // Constructor
    public FacturaDena() {
        tipoDocumento = "";
        numeroDocumento = 0;
        codigoComprador = 0;
        fechaFactura = "";
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

        lineasFactura = new ArrayList<LineaFactura>();

        baseFactura = 0.0;
        porcentajeIva = 0.0;
        importeIva = 0.0;
        porcentajeRecargoEquivalencia = 0.0;
        importeRecargoEquivalencia = 0.0;
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
                                                fechaFactura = lineaDena.getValor().substring(0, 8);
                                                break;
                                            case 3:
                                                telefonoComprador = lineaDena.getValor().trim();
                                                break;
                                            case 4:
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
                                                telefonoVendedor = lineaDena.getValor().substring(posTlfnVendedor + 4).trim();
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
                }
            }

            fr.close();
        } catch (Exception e) {
            System.out.println("Excepcion leyendo fichero " + fichero + ": " + e);
            cargaCorrecta = false;
        }

        return cargaCorrecta;

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

    public String getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(String fechaFactura) {
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

}
