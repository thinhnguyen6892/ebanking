package edu.hcmus.project.ebanking.backoffice.service;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import edu.hcmus.project.ebanking.data.model.contranst.SignType;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.bc.BcPGPObjectFactory;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

@Service
public class SignatureService {

    private Logger logger = LoggerFactory.getLogger(SignatureService.class);

    @Autowired
    private ResourceLoader resourceLoader;
    private File privateRSAKey;
    private File privatePGPKey;

    @Value(value = "${app.signature.rsa.private-key}")
    private String privateRSAKeyPath;

    @Value(value = "${app.signature.pgp.private-key}")
    private String privatePGPKeyPath;

    private Pattern PATTERN = Pattern.compile("(.*?)(?:\\((\\d+)\\))?(\\.[^.]*)?");



    @PostConstruct
    public void init() throws IOException {
        Security.addProvider(new BouncyCastleProvider());
        Resource privateRSAKeyResource = resourceLoader.getResource(privateRSAKeyPath);
        this.privateRSAKey = privateRSAKeyResource.getFile();

        Resource privatePGPKeyResource = resourceLoader.getResource(privatePGPKeyPath);
        this.privatePGPKey = privatePGPKeyResource.getFile();
    }

    private String signWithRSAPrivateKey(String content) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, SignatureException {

        Signature rsa = Signature.getInstance("SHA256withRSA");
        byte[] keyBytes = Files.readAllBytes(privateRSAKey.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);
        rsa.initSign(privateKey);
        rsa.update(content.getBytes());
        return new String(Base64Utils.encode(rsa.sign()));
    }

    private String signWithPGPPrivateKey(String content) throws IOException, PGPException {
        InputStream keyIn = new BufferedInputStream(new FileInputStream(privatePGPKey));
        PGPSecretKey pgpSec = readPGPSecretKey();
        PGPPrivateKey pgpPrivKey = pgpSec.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build("mySecret".toCharArray()));
        PGPSignatureGenerator sGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(pgpSec.getPublicKey().getAlgorithm(), PGPUtil.SHA1).setProvider("BC"));
        sGen.init(PGPSignature.CANONICAL_TEXT_DOCUMENT, pgpPrivKey);
        Iterator it = pgpSec.getPublicKey().getUserIDs();
        if (it.hasNext()) {
            PGPSignatureSubpacketGenerator  spGen = new PGPSignatureSubpacketGenerator();
            spGen.setSignerUserID(false, (String)it.next());
            sGen.setHashedSubpackets(spGen.generate());
        }
        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedData.ZLIB);
        ByteArrayOutputStream encOut = new ByteArrayOutputStream();
        OutputStream out = new ArmoredOutputStream(encOut);
        BCPGOutputStream bOut = new BCPGOutputStream(comData.open(out));
        sGen.generateOnePassVersion(false).encode(bOut);
        PGPLiteralDataGenerator lGen = new PGPLiteralDataGenerator();
        byte[] messageCharArray = content.getBytes();
        OutputStream lOut = lGen.open(bOut, PGPLiteralData.BINARY, PGPLiteralData.CONSOLE, messageCharArray.length, new Date());
        for (byte c : messageCharArray) {
            lOut.write(c);
            sGen.update(c);
        }
        lOut.close();
        lGen.close();
        sGen.generate().encode(bOut);
        comData.close();
        out.close();
        return Base64Utils.encodeToString(encOut.toByteArray());
    }

    private PGPSecretKey readPGPSecretKey() throws IOException, PGPException  {
        InputStream keyIn = new BufferedInputStream(new FileInputStream(privatePGPKey));
        PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
                PGPUtil.getDecoderStream(keyIn), new JcaKeyFingerprintCalculator());
        Iterator keyRingIter = pgpSec.getKeyRings();
        while (keyRingIter.hasNext()) {
            PGPSecretKeyRing keyRing = (PGPSecretKeyRing)keyRingIter.next();
            Iterator keyIter = keyRing.getSecretKeys();
            while (keyIter.hasNext()) {
                PGPSecretKey key = (PGPSecretKey)keyIter.next();
                if (key.isSigningKey()) {
                    keyIn.close();
                    return key;
                }
            }
        }
        keyIn.close();
        throw new IllegalArgumentException("Can't find signing key in key ring.");
    }

    public String signWithPrivateKey(SignType signType, String content) throws SignatureException {
        try {
            switch (signType) {
                case RSA:
                    return signWithRSAPrivateKey(content);
                case PGP:
                    return signWithPGPPrivateKey(content);
            }
        } catch (Exception e) {
            logger.error("Cannot performing signing", e);
        }
        throw new SignatureException("Invalid signature type");
    }

    public void verifyWithPublicKey(SignType signType, String content, byte[] signature, byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException, SignatureException {
        switch (signType) {
            case RSA:
                verifyWithRSAPublicKey(content, signature, keyBytes);
                break;
            case PGP:
                verifyWithPGPPublicKey(content, signature, keyBytes);
                break;
            default:
                throw new SignatureException("Invalid signature type");
        }
    }

    private void verifyWithRSAPublicKey(String content, byte[] signature, byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);
        sig.initVerify(publicKey);
        sig.update(content.getBytes());
        if(!sig.verify(signature)) {
            throw new SignatureException("RSA signature verification failed.");
        }
    }

    public void verifyWithPGPPublicKey(String content, byte[] signature, byte[] keyBytes) throws SignatureException {
        PGPPublicKey publicKey = pgploadPublicKey(keyBytes);
        pgpExtractSignature(signature, publicKey);
    }

    private PGPPublicKey pgploadPublicKey(byte[] keyBytes) {
        try (InputStream input = ByteSource.wrap(keyBytes).openStream();
             InputStream decoder = PGPUtil.getDecoderStream(input)) {
            return new BcPGPPublicKeyRing(decoder).getPublicKey();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void pgpExtractSignature(byte[] signature, PGPPublicKey pgpPublicKey) throws SignatureException {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(signature);
            PGPObjectFactory decoder = new BcPGPObjectFactory(PGPUtil.getDecoderStream(input));
            Object message = decoder.nextObject();
            if (message == null) {
                throw new SignatureException("No OpenPGP packets found in signature.");
            }
            BcKeyFingerprintCalculator calculator = new BcKeyFingerprintCalculator();
            calculator.calculateFingerprint(pgpPublicKey.getPublicKeyPacket());
            PGPObjectFactory pgpFact = null;
            if (message instanceof PGPCompressedData) {
                PGPCompressedData cData = (PGPCompressedData) message;
                pgpFact = new PGPObjectFactory(cData.getDataStream(), calculator);
                message = pgpFact.nextObject();
            }
            PGPOnePassSignature ops = null;
            if (message instanceof PGPOnePassSignatureList) {
                PGPOnePassSignatureList p1 = (PGPOnePassSignatureList) message;
                ops = p1.get(0);
                ops.init(new BcPGPContentVerifierBuilderProvider(), pgpPublicKey);
                message = pgpFact.nextObject();
            }

            if (message instanceof PGPLiteralData) {
                PGPLiteralData ld = (PGPLiteralData) message;
                InputStream is = ld.getInputStream();
                byte[] bytes =  ByteStreams.toByteArray(is);

                PGPSignatureList p3 = (PGPSignatureList) pgpFact.nextObject();
                PGPSignature sig =  p3.get(0);
                sig.init(new BcPGPContentVerifierBuilderProvider(), pgpPublicKey);
                sig.update(bytes);
                if (!sig.verify()) {
                    throw new SignatureException("PGP signature verification failed.");
                }
            } else {
                throw new PGPException("message is not a simple encrypted file - type unknown.");
            }
        } catch (IOException | PGPException e) {
            throw new SignatureException("Failed to extract PGPSignature object from .sig blob.", e);
        }
    }

}
