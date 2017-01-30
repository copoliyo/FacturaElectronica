/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturaelectronica;

import es.facturae.facturae._2014.v3_2_1.facturae.*;
import es.facturae.facturae._2014.v3_2_1.facturae.InvoiceType.TaxesOutputs;
import org.w3._2000._09.xmldsig.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 *
 * @author Txus
 */
public class FacturaElectronica {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyStoreException, CertificateException, UnrecoverableEntryException, SAXException, MarshalException, XMLSignatureException {
        // TODO code application logic here

        FacturaDena fd = new FacturaDena();
        if (fd.cargaDesdeFichero()) {
            System.out.println("Factura Dena cargada.");
            try {
                generaXmlFacturaE(fd);
            } catch (JAXBException | IOException | ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static void generaXmlFacturaE(FacturaDena facturaDena) throws JAXBException, IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyStoreException, CertificateException, UnrecoverableEntryException, ParserConfigurationException, SAXException, MarshalException, XMLSignatureException {
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
        // Poner la cantidad en EUROS provoca un error de formato, exige siempre dos
        // decimales, aunque sean .00
        //totalInvoicesAmount.setEquivalentInEuros(facturaDena.getTotalFactura());        

        AmountType totalOutstandingAmount = new AmountType();
        totalOutstandingAmount.setTotalAmount(facturaDena.getTotalFactura());
        //totalOutstandingAmount.setEquivalentInEuros(facturaDena.getTotalFactura());

        AmountType totalExecutableAmount = new AmountType();
        totalExecutableAmount.setTotalAmount(facturaDena.getTotalFactura());
        //totalExecutableAmount.setEquivalentInEuros(facturaDena.getTotalFactura());

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
        if ("ABCDEFGHJNPQRSUVW".contains(facturaDena.getNifVendedor().substring(0, 1))) {
            sellerIdentification.setPersonTypeCode(PersonTypeCodeType.J);
        } // Si no, es una persona física        
        else {
            sellerIdentification.setPersonTypeCode(PersonTypeCodeType.F);
        }
        sellerIdentification.setResidenceTypeCode(ResidenceTypeCodeType.R);
        sellerIdentification.setTaxIdentificationNumber(facturaDena.getNifVendedor());

        sellerParty.setTaxIdentification(sellerIdentification);

        LegalEntityType sellerLegalEntity = new LegalEntityType();
        sellerLegalEntity.setCorporateName(facturaDena.getRazonSocialVendedor());
        if (facturaDena.getNombreComercialVendedor().trim().length() > 0) {
            sellerLegalEntity.setTradeName(facturaDena.getNombreComercialVendedor());
        }
        if (facturaDena.getRmRegistroMercantil().length() > 0) {
            RegistrationDataType sellerRegistroMercantil = new RegistrationDataType();
            sellerRegistroMercantil.setRegisterOfCompaniesLocation(facturaDena.getRmRegistroMercantil());
            sellerRegistroMercantil.setBook(facturaDena.getRmLibro());
            sellerRegistroMercantil.setSheet(facturaDena.getRmHoja());
            sellerRegistroMercantil.setFolio(facturaDena.getRmFolio());
            sellerRegistroMercantil.setSection(facturaDena.getRmSeccion());
            sellerRegistroMercantil.setVolume(facturaDena.getRmTomo());
            sellerRegistroMercantil.setAdditionalRegistrationData("Inscripcion " + facturaDena.getRmInscripcion());

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
        if ("ABCDEFGHJNPQRSUVW".contains(facturaDena.getNifComprador().substring(0, 1))) {
            buyerIdentification.setPersonTypeCode(PersonTypeCodeType.J);
        } // Si no, es una persona física        
        else {
            buyerIdentification.setPersonTypeCode(PersonTypeCodeType.F);
        }
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

        LineaFactura lineaFactura = new LineaFactura();
        InvoicesType invoices = new InvoicesType();
        InvoiceType invoice;

        invoice = new InvoiceType();

        InvoiceHeaderType invoiceHeader = new InvoiceHeaderType();
        invoiceHeader.setInvoiceNumber(String.valueOf(facturaDena.getNumeroDocumento()));
        invoiceHeader.setInvoiceDocumentType(InvoiceDocumentTypeType.FC);
        invoiceHeader.setInvoiceClass(InvoiceClassType.OO);

        invoice.setInvoiceHeader(invoiceHeader);

        InvoiceIssueDataType invoiceIssueData = new InvoiceIssueDataType();
        invoiceIssueData.setIssueDate((XMLGregorianCalendar) facturaDena.getFechaFactura());

        invoice.setInvoiceIssueData(invoiceIssueData);
        invoiceIssueData.setInvoiceCurrencyCode(CurrencyCodeType.EUR);
        invoiceIssueData.setTaxCurrencyCode(CurrencyCodeType.EUR);
        invoiceIssueData.setLanguageName(LanguageCodeType.ES);

        TaxesOutputs taxesOutputs = new TaxesOutputs();
        TaxOutputType taxOutput = new TaxOutputType();
        //TaxesOutputs taxesOutputs = new TaxesOutputs();

        TaxType tax = new TaxType();
        AmountType importeIva = new AmountType();
        // En la factura de Dena, no viene el importe del IVA, lo calculamos, pero
        // si hay mezclados tipos de IVA distintos --> NO FUNCIONARA
        importeIva.setTotalAmount(redondearDecimales(facturaDena.getImporteIva()));

        AmountType tableBase = new AmountType();
        tableBase.setTotalAmount(facturaDena.getBaseFactura());

        AmountType taxAmount = new AmountType();
        taxAmount.setTotalAmount(facturaDena.getImporteIva());
        
        taxOutput.setTaxTypeCode("01");
        taxOutput.setTaxAmount(taxAmount);
        taxOutput.setTaxableBase(tableBase);
        taxOutput.setTaxRate(facturaDena.getPorcentajeIva());

        taxesOutputs.getTax().add(taxOutput);
        invoice.setTaxesOutputs(taxesOutputs);

        InvoiceTotalsType invoiceTotals = new InvoiceTotalsType();

        invoiceTotals.setTotalGrossAmount(facturaDena.getBaseFactura());
        invoiceTotals.setTotalGrossAmountBeforeTaxes(facturaDena.getBaseFactura());
        invoiceTotals.setTotalTaxOutputs(facturaDena.getImporteIva() + facturaDena.getImporteRecargoEquivalencia());
        invoiceTotals.setTotalTaxOutputs(facturaDena.getImporteIva());
        invoiceTotals.setTotalTaxesWithheld(facturaDena.getImporteRecargoEquivalencia());
        invoiceTotals.setInvoiceTotal(facturaDena.getTotalFactura());
        invoiceTotals.setTotalOutstandingAmount(facturaDena.getTotalFactura());
        invoiceTotals.setTotalExecutableAmount(facturaDena.getTotalFactura());
        
        invoice.setInvoiceTotals(invoiceTotals);
        
        ItemsType items = new ItemsType();
        
        
        
        Iterator<LineaFactura> itLineaFactura = facturaDena.getLineasFactura().iterator();

        ////////////////////////  Lineas de factura
        while (itLineaFactura.hasNext()) {
            lineaFactura = itLineaFactura.next();
            InvoiceLineType invoiceLine = new InvoiceLineType();
                        
            invoiceLine.setItemDescription(lineaFactura.getDescripcionArticulo());
            invoiceLine.setQuantity((double)lineaFactura.getCantidad());
            invoiceLine.setUnitPriceWithoutTax(lineaFactura.getPrecio());
            invoiceLine.setTotalCost(lineaFactura.getImporte());
            // Ojo si hay descuentos, no está implementado
            invoiceLine.setGrossAmount(lineaFactura.getImporte());
            
            InvoiceLineType.TaxesOutputs invoiceLineTaxesOutputs = new InvoiceLineType.TaxesOutputs();
            
            
            InvoiceLineType.TaxesOutputs.Tax invoiceLineTax = new InvoiceLineType.TaxesOutputs.Tax();

            //TaxOutputType invoiceLineTax = new TaxOutputType();
            invoiceLineTax.setTaxTypeCode("01");
            invoiceLineTax.setTaxRate(facturaDena.getPorcentajeIva());
            // Ojo si hay descuentos, no está implementado
            AmountType invoiceLinetaxableBase = new AmountType();
            invoiceLinetaxableBase.setTotalAmount(redondearDecimales(lineaFactura.getImporte()));            
            invoiceLineTax.setTaxableBase(invoiceLinetaxableBase);
            
            AmountType invoiceLineTaxAmount = new AmountType();
            // double lineaIva = redondearDecimales((lineaFactura.getImporte() * facturaDena.getPorcentajeIva() / 100.00), 8);
            double lineaIva = redondearDecimales(((lineaFactura.getImporte() * facturaDena.getPorcentajeIva()) / 100.00));
            invoiceLineTaxAmount.setTotalAmount(lineaIva);
            invoiceLineTax.setTaxAmount(invoiceLineTaxAmount);                        
            
            invoiceLineTaxesOutputs.getTax().add(invoiceLineTax);           
            invoiceLine.setTaxesOutputs(invoiceLineTaxesOutputs);
            
            items.getInvoiceLine().add(invoiceLine);
        }

        invoice.setItems(items);
        invoices.getInvoice().add(invoice);

        facturae.setInvoices(invoices);
        
         File f = new File("new.xml");

        JAXBContext context = JAXBContext.newInstance(Facturae.class);

        Marshaller jaxbMarshaller = context.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        //jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, javax.xml.XMLConstants.DEFAULT_NS_PREFIX);
        jaxbMarshaller.marshal(facturae, f);
        jaxbMarshaller.marshal(facturae, System.out);
        
        
        
        
        ///// A partir de aquí, la firma....
        
        // Create a DOM XMLSignatureFactory that will be used to
        // generate the enveloped signature.
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");       

        // Create a Reference to the enveloped document (in this case,
        // you are signing the whole document, so a URI of "" signifies
        // that, and also specify the SHA1 digest algorithm and
        // the ENVELOPED Transform.
        Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                null, null);
        
        // Create the SignedInfo.
        SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                (C14NMethodParameterSpec) null),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(ref));                     
        
       
       
        // Load the KeyStore and get the signing key and certificate.
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("C:\\Users\\Txus\\.keystore"), "copoliyo".toCharArray());
        
        //KeyStore.PrivateKeyEntry keyEntry
        //        = (KeyStore.PrivateKeyEntry) ks.getEntry("mykey", new KeyStore.PasswordProtection("copoliyo".toCharArray()));
        
        KeyStore.TrustedCertificateEntry keyEntry = (KeyStore.TrustedCertificateEntry) ks.getEntry("mykey", null);
        //X509Certificate cert = (X509Certificate) keyEntry.getCertificate();
        
        X509Certificate cert = (X509Certificate) keyEntry.getTrustedCertificate();

        // Create the KeyInfo containing the X509Data.
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List x509Content = new ArrayList();
        x509Content.add(cert.getSubjectX500Principal().getName());
        x509Content.add(cert);
        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
        
        
        // Instantiate the document to be signed.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new FileInputStream("new.xml"));

        // Create a DOMSignContext and specify the RSA PrivateKey and
        // location of the resulting XMLSignature's parent element.
        DOMSignContext dsc = new DOMSignContext((Key) keyEntry.getTrustedCertificate(), doc.getDocumentElement());
        
        // Create the XMLSignature, but don't sign it yet.
        XMLSignature signature = fac.newXMLSignature(si, ki);

        // Marshal, generate, and sign the enveloped signature.
        signature.sign(dsc);
        
        
       
        

       

    }

    public static double formatoConComaDecimal(double doubleSinFormato) {

        String strDoubleFormateado = "";
        double doubleFormateado = 0.0;

        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        DecimalFormat myFormatter = new DecimalFormat("########0.00", simbolos);
        strDoubleFormateado = myFormatter.format(doubleSinFormato);

        doubleFormateado = Double.valueOf(strDoubleFormateado);
        return doubleFormateado;
    }

    public static double redondearDecimales(double valorInicial) {
                
        DecimalFormat df = new DecimalFormat("0.00");
        String formate = df.format(valorInicial);
        double finalValue = 0.0;
        try {
            finalValue = (Double) df.parse(formate);
        } catch (ParseException ex) {
            Logger.getLogger(FacturaElectronica.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return finalValue;        
    }

}
