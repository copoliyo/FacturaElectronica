/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturaelectronica;

import java.util.ArrayList;

/**
 *
 * @author Txus
 */
public class FacturaDena {

    
    
    private String tipoDocumento; // DD0101
    private int numeroDocumento;  // DD0102
    private int codigoComprador;      // DD0201
    private int fechaFactura;       // DD0202
    private String razonSocialComprador; // AQ0101   
    private String direccionComprador; // AQ00003001ABELLA 1, 1 2º 3  
    // AQ0000400139770 LAREDO Cantabria 
    private String codigoPostalComprador;
    private String poblacionComprador;
    private String provinciaComprador;
    
    private String telefonoComprador; // DD0301
    private String nifComprador;      // DD0302
    private String nombreComercialVendedor; // DQ0101
    private String razonSOcialVendedor; // DQ0201
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
    
    public FacturaDena() {
        tipoDocumento = "";
        numeroDocumento = 0;
        codigoComprador = 0;
        fechaFactura = 0;
        razonSocialComprador = "";
        direccionComprador = "";

        codigoPostalComprador = "";
        provinciaComprador = "";

        telefonoComprador = "";
        nifComprador = "";
        nombreComercialVendedor = "";
        razonSOcialVendedor = "";
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

    public int getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(int fechaFactura) {
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

    public String getRazonSOcialVendedor() {
        return razonSOcialVendedor;
    }

    public void setRazonSOcialVendedor(String razonSOcialVendedor) {
        this.razonSOcialVendedor = razonSOcialVendedor;
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
