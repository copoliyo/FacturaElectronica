/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturaelectronica;

/**
 *
 * @author Txus
 */
public enum TipoDeLinea {
    NUEVA_PAGINA("IN", 1),
    DATOS_DOCUMENTO("DD", 2),
    VARIOS("VR", 3),
    DATOS_VENDEDOR("DQ", 4),
    VARIOS_AUX1("V1", 5),
    VARIOS_AUX2("V2", 6),
    DATOS_COMPRADOR("AQ", 7),
    LINEA_VENTA("LI", 8),
    LINEA_TOTAL("LT", 9);
    
    private final String tipoLineaStr;
    private final int tipoLineaInt;
    
    TipoDeLinea(String tipoLineaStr, int tipoLineaInt){
        this.tipoLineaStr = tipoLineaStr;
        this.tipoLineaInt = tipoLineaInt;
    }
    
    public String getTipoLineaStr() { return tipoLineaStr; }
    public int getTipoLineaInt() { return tipoLineaInt; }
}
