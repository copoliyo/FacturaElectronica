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
public class LineaDena {

    private static final int LONGITUD_LINEA_MAXIMA = 90;
    
    private int tipo;
    private int nivel;
    private int subnivel;
    private String valor;

    public LineaDena() {
        tipo = 0;
        nivel = 0;
        subnivel = 0;
        valor = "";

    }

    public boolean procesaLinea(String lineaStr) {
        boolean procesoCorrecto = true;

        String tipoStr = "";
        String nivelStr = "";
        String subnivelStr = "";
        String valorStr = "";

        // La linea siempre viene con una longitud de 90 caracteres, si no es así, fallo.
        if (lineaStr.length() != LONGITUD_LINEA_MAXIMA) {
            procesoCorrecto = false;
        } else {
            tipoStr = lineaStr.substring(0, 2);
            nivelStr = lineaStr.substring(2, 7);
            subnivelStr = lineaStr.substring(7, 10);
            valorStr = lineaStr.substring(10, 90);

            switch (tipoStr) {
                case "IN":
                    this.tipo = TipoDeLinea.NUEVA_PAGINA.getTipoLineaInt();
                    break;
                case "DD":
                    this.tipo = TipoDeLinea.DATOS_DOCUMENTO.getTipoLineaInt();
                    break;
                case "VR":
                    this.tipo = TipoDeLinea.VARIOS.getTipoLineaInt();
                    break;
                case "DQ":
                    this.tipo = TipoDeLinea.DATOS_VENDEDOR.getTipoLineaInt();
                    break;
                case "V1":
                    this.tipo = TipoDeLinea.VARIOS_AUX1.getTipoLineaInt();
                    break;
                case "V2":
                    this.tipo = TipoDeLinea.VARIOS_AUX2.getTipoLineaInt();
                    break;
                case "AQ":
                    this.tipo = TipoDeLinea.DATOS_COMPRADOR.getTipoLineaInt();
                    break;
                case "LI":
                    this.tipo = TipoDeLinea.LINEA_VENTA.getTipoLineaInt();
                    break;
                case "LT":
                    this.tipo = TipoDeLinea.LINEA_TOTAL.getTipoLineaInt();
                    break;
                default:
                    this.tipo = 0;
                    System.out.println("Tipoo de linea no reconocido");
                    break;
            }
            
            // Si la linea es de un tipo correcto, seguimos
            if(this.tipo != 0){
                this.nivel = Integer.valueOf(nivelStr);
                this.subnivel = Integer.valueOf(subnivelStr);
                this.valor = valorStr;
            }
        }

        return procesoCorrecto;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getSubnivel() {
        return subnivel;
    }

    public void setSubnivel(int subnivel) {
        this.subnivel = subnivel;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}
