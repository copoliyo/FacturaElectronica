/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturaelectronica;

import es.facturae.facturae._2014.v3_2_1.facturae.*;
import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Txus
 */
public class FacturaElectronica {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        FacturaDena fd = new FacturaDena();
        if (fd.cargaDesdeFichero()) {            
            System.out.println("Factura Dena cargada.");
            try {
                generaXmlFacturaE(fd);
            } catch (JAXBException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        

    }
    
    public static void generaXmlFacturaE(FacturaDena facturaDena) throws JAXBException, IOException {
        Facturae facturae = new Facturae();

        FileHeaderType fileHeader = new FileHeaderType();
        

        fileHeader.setSchemaVersion("3.2.1");
        fileHeader.setModality(ModalityType.I);
        fileHeader.setInvoiceIssuerType(InvoiceIssuerTypeType.EM);

        BatchType batchType = new BatchType();
        batchType.setBatchIdentifier(String.valueOf(facturaDena.getNumeroDocumento()));
        batchType.setInvoicesCount(1L);

        AmountType totalInvoicesAmount = new AmountType();
        totalInvoicesAmount.setTotalAmount(facturaDena.getTotalFactura());
        totalInvoicesAmount.setEquivalentInEuros(facturaDena.getTotalFactura());        
        //AmountType totalAmount = new AmountType();
        //totalAmount.setTotalAmount(facturaDena.getTotalFactura());
        AmountType totalOutstandingAmount = new AmountType();
        totalOutstandingAmount.setTotalAmount(facturaDena.getTotalFactura());
        totalOutstandingAmount.setEquivalentInEuros(facturaDena.getTotalFactura());
        
        AmountType totalExecutableAmount = new AmountType();
        totalExecutableAmount.setTotalAmount(facturaDena.getTotalFactura());
        totalExecutableAmount.setEquivalentInEuros(facturaDena.getTotalFactura());
        
        
        batchType.setTotalInvoicesAmount(totalInvoicesAmount);
        batchType.setTotalOutstandingAmount(totalOutstandingAmount);
        batchType.setTotalExecutableAmount(totalExecutableAmount);
        batchType.setInvoiceCurrencyCode(CurrencyCodeType.EUR);

        fileHeader.setBatch(batchType);
        
        PartiesType parties = new PartiesType();
        
        // Vendedor
        BusinessType sellerParty = new BusinessType();
        
        TaxIdentificationType sellerIdentification = new TaxIdentificationType();
        // Si el CIF empieza por cualquiera de las siguientes letras, es una persona juerídica
        if("ABCDEFGHJNPQRSUVW".contains(facturaDena.getNifVendedor().substring(0, 1)))
            sellerIdentification.setPersonTypeCode(PersonTypeCodeType.J);
        // Si no, es una persona física        
        else
            sellerIdentification.setPersonTypeCode(PersonTypeCodeType.F);
        sellerIdentification.setResidenceTypeCode(ResidenceTypeCodeType.R);
        sellerIdentification.setTaxIdentificationNumber(facturaDena.getNifVendedor());
        
        sellerParty.setTaxIdentification(sellerIdentification);
        
        LegalEntityType sellerLegalEntity = new LegalEntityType();
        sellerLegalEntity.setCorporateName(facturaDena.getRazonSocialVendedor());
        if(facturaDena.getNombreComercialVendedor().trim().length() > 0)
            sellerLegalEntity.setTradeName(facturaDena.getNombreComercialVendedor());
        if(facturaDena.getRegistroMercantil().trim().length() > 0){
            RegistrationDataType sellerRegistroMercantil = new RegistrationDataType();
            sellerRegistroMercantil.setAdditionalRegistrationData(facturaDena.getRegistroMercantil());
            sellerLegalEntity.setRegistrationData(sellerRegistroMercantil);
        }
        
        AddressType sellerAddressInSpain = new AddressType();
        sellerAddressInSpain.setAddress(facturaDena.getDireccionVendedor());
        sellerAddressInSpain.setPostCode(facturaDena.getCodigoPostalVendedor());
        sellerAddressInSpain.setTown(facturaDena.getLocalidadVendedor());
        sellerAddressInSpain.setProvince(facturaDena.getProvinciaVendedor());
        sellerAddressInSpain.setCountryCode(CountryType.ESP);
        
        sellerLegalEntity.setAddressInSpain(sellerAddressInSpain);
                
        sellerParty.setLegalEntity(sellerLegalEntity);
        
        // COMPRADOR
        BusinessType buyerParty = new BusinessType();
        
        TaxIdentificationType buyerIdentification = new TaxIdentificationType();
        // Si el CIF empieza por cualquiera de las siguientes letras, es una persona juerídica
        if("ABCDEFGHJNPQRSUVW".contains(facturaDena.getNifComprador().substring(0, 1)))
            buyerIdentification.setPersonTypeCode(PersonTypeCodeType.J);
        // Si no, es una persona física        
        else
            buyerIdentification.setPersonTypeCode(PersonTypeCodeType.F);                
        buyerIdentification.setResidenceTypeCode(ResidenceTypeCodeType.R);
        buyerIdentification.setTaxIdentificationNumber(facturaDena.getNifComprador());
        
        buyerParty.setTaxIdentification(buyerIdentification);
        
        // Hay que ver sie es una empresa o una persona fisica para poner un tipo
        // de datos u otros. De momento, va todo como persona física. OOOOOOJJJJJJJOOOOOOO!!!!
        LegalEntityType buyerLegalEntity = new LegalEntityType();               
        buyerLegalEntity.setCorporateName(facturaDena.getRazonSocialComprador());
        
        AddressType buyerAddressInSpain = new AddressType();
        buyerAddressInSpain.setAddress(facturaDena.getDireccionComprador());
        buyerAddressInSpain.setPostCode(facturaDena.getCodigoPostalComprador());
        buyerAddressInSpain.setTown(facturaDena.getPoblacionComprador());
        buyerAddressInSpain.setProvince(facturaDena.getProvinciaComprador());
        buyerAddressInSpain.setCountryCode(CountryType.ESP);
        
        buyerLegalEntity.setAddressInSpain(buyerAddressInSpain);
        
        buyerParty.setLegalEntity(buyerLegalEntity);
        
        
        
        parties.setSellerParty(sellerParty);
        parties.setBuyerParty(buyerParty);
        facturae.setParties(parties);

        facturae.setFileHeader(fileHeader);

        
        //////////////////////
        AccountType account = new AccountType();
        account.setAccountNumber("ES1201234567890123456789");
        
        
        File f = new File("new.xml");

        JAXBContext context = JAXBContext.newInstance(Facturae.class);

        Marshaller jaxbMarshaller = context.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        //jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, javax.xml.XMLConstants.DEFAULT_NS_PREFIX);
        jaxbMarshaller.marshal(facturae, f);
        jaxbMarshaller.marshal(facturae, System.out);

    }
        
    
}
