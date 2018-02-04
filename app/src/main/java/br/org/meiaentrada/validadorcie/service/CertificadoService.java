package br.org.meiaentrada.validadorcie.service;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.util.encoders.Base64;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.Security;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import br.org.meiaentrada.validadorcie.configuration.GlobalConstants;
import br.org.meiaentrada.validadorcie.model.RetornoValidacao;


public class CertificadoService {

    // Executa validacao  do certificado localmente ( chave publica e CRl sao mandatorias,
    // mesmo que a CRL nao tenha certif. revogados )
    public RetornoValidacao validaCertificado(String certDNE, String chavepublica, String crl) {

        RetornoValidacao retornov = new RetornoValidacao();
        retornov.setResultado(GlobalConstants.ERRO_INVALIDO);
        retornov.setErro(true);

        try {
            if (!chavepublica.isEmpty() && !crl.isEmpty()) {
                Security.addProvider(new BouncyCastleProvider());
                X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.decode(chavepublica));
                KeyFactory kf = KeyFactory.getInstance("RSA");
                RSAPublicKey chapub = (RSAPublicKey) kf.generatePublic(keySpecX509);
                certDNE = "-----BEGIN ATTRIBUTE CERTIFICATE-----\n" + certDNE + "\n-----END ATTRIBUTE CERTIFICATE-----";
                PEMParser pemattr = new PEMParser(new StringReader(certDNE));
                Object objattr2 = pemattr.readObject();
                pemattr.close();
                X509AttributeCertificateHolder attr2 = (X509AttributeCertificateHolder) objattr2;
                crl = "-----BEGIN X509 CRL-----\n" + crl + "\n-----END X509 CRL-----";
                PEMParser pemacrl = new PEMParser(new StringReader(crl));
                Object objcrl = pemacrl.readObject();
                pemacrl.close();
                X509CRLHolder jceCRL = (X509CRLHolder) objcrl;
                if (jceCRL.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(chapub))) {
                    if (attr2.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(chapub))) {
                        if (attr2.isValidOn(new Date())) {
                            AttributeCertificateHolder h = attr2.getHolder();
                            X500Name[] nomex = h.getIssuer();
                            String nomefull = nomex[0] + "";
                            Integer indicenome = nomefull.indexOf("CN=");
                            nomefull = nomefull.substring(indicenome + 3, nomefull.length());
                            retornov.setResultado("\n" + nomefull + "\n");
                            retornov.setErro(false);
                            Attribute[] attribs = attr2.getAttributes();
                            Attribute a = attribs[0];
                            String apar = a.getAttrValues() + "";
                            retornov.setResultado(retornov.getResultado().concat("CPF: " + apar.substring(9, 20) + GlobalConstants.DOC_VALIDO));
                            JcaX509CRLConverter converter = new JcaX509CRLConverter();
                            converter.setProvider("BC");
                            X509CRL crlconv = converter.getCRL(jceCRL);
                            X509CRLEntry xentry = crlconv.getRevokedCertificate(attr2.getSerialNumber());
                            if (xentry != null) {
                                retornov.setResultado(GlobalConstants.ERRO_REVOGADO);
                                retornov.setErro(true);
                            }

                        } else {
                            retornov.setResultado(GlobalConstants.ERRO_EXPIRADO);
                            retornov.setErro(true);
                        }
                    }
                }
            }
            return retornov;
        } catch (Exception e) {
            e.printStackTrace();
            return retornov;
        }

    }

    // busca emissor dentro do certificado
    public RetornoValidacao pegaEmissor(String certDNE) {

        RetornoValidacao retornove = new RetornoValidacao();
        retornove.setResultado(GlobalConstants.ERRO_INVALIDO);
        retornove.setErro(true);

        try {

            certDNE = "-----BEGIN ATTRIBUTE CERTIFICATE-----\n" + certDNE + "\n-----END ATTRIBUTE CERTIFICATE-----";
            PEMParser pemattr = new PEMParser(new StringReader(certDNE));
            Object objattr2 = pemattr.readObject();
            pemattr.close();
            X509AttributeCertificateHolder attr2 = (X509AttributeCertificateHolder) objattr2;
            AttributeCertificateHolder h = attr2.getHolder();
            X500Name[] nomex = h.getIssuer();
            String nomefull = nomex[0] + "";
            Integer indice1 = nomefull.indexOf("OU=");
            Integer indice2 = nomefull.indexOf("CN=");
            nomefull = nomefull.substring(indice1 + 3, indice2 - 1);
            nomefull = nomefull.replaceAll("\\s+", "");
            retornove.setResultado(nomefull);
            retornove.setErro(false);
            return retornove;

        } catch (Exception e) {
            return retornove;
        }

    }

}
