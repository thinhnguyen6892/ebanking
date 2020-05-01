package edu.hcmus.project.ebanking.ws.service;

import com.google.common.io.ByteSource;
import edu.hcmus.project.ebanking.ws.model.SignType;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.bc.BcPGPObjectFactory;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Pattern;

@Service
public class SignatureService {

    @Autowired
    private ResourceLoader resourceLoader;
    private File privateKey;

    @Value(value = "${app.signature.private-key}")
    private String privateKeyPath;

    @Value(value = "{app.signature.key-dir}")
    private String keyDirPath;

    private Pattern PATTERN = Pattern.compile("(.*?)(?:\\((\\d+)\\))?(\\.[^.]*)?");



    @PostConstruct
    public void init() throws IOException {
        Resource privateKeyResource = resourceLoader.getResource(privateKeyPath);
        this.privateKey = privateKeyResource.getFile();
    }

/*    public String uploadKey(MultipartFile key) throws IOException {
        String extension = StringUtils.getFilenameExtension(key.getOriginalFilename());

        Path targetLocation = this.keyDir.resolve(fileName);
        Files.copy(key.getInputStream(), targetLocation, StandardCopyOption.ATOMIC_MOVE);
        return fileName;
    }*/


/*    public byte[] getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(privateKey.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey prKey = kf.generatePrivate(spec);
        RSAPrivateCrtKeyImpl rsaPrivateKey = (RSAPrivateCrtKeyImpl) prKey;

        return kf.generatePublic(new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent())).getEncoded();
    }*/

    public String signWithPrivateKey(String content) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, SignatureException {

        Signature rsa = Signature.getInstance("SHA256withRSA");
        byte[] keyBytes = Files.readAllBytes(privateKey.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);
//        this.cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//        this.cipher.doFinal(content.getBytes())
        rsa.initSign(privateKey);
        rsa.update(content.getBytes());
        return new String(Base64Utils.encode(rsa.sign()));
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
        try {
            byte[] data = content.getBytes();
            PGPPublicKey publicKey = loadPublicKey(keyBytes);
            Security.addProvider(new BouncyCastleProvider());
            PGPSignature sig = pgpExtractSignature(signature);
            sig.init(new BcPGPContentVerifierBuilderProvider(), publicKey);
            sig.update(data);
            if (!sig.verify()) {
                throw new SignatureException("PGP signature verification failed.");
            }
        } catch (PGPException e) {
            throw new SignatureException("PGP signature verification failed.");
        }

    }

    private PGPPublicKey loadPublicKey(byte[] keyBytes) {
        try (InputStream input = ByteSource.wrap(keyBytes).openStream();
             InputStream decoder = PGPUtil.getDecoderStream(input)) {
            return new BcPGPPublicKeyRing(decoder).getPublicKey();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PGPSignature pgpExtractSignature(byte[] signature)
            throws SignatureException {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(signature);
            PGPObjectFactory decoder = new BcPGPObjectFactory(PGPUtil.getDecoderStream(input));
            Object object = decoder.nextObject();
            if (object == null) {
                throw new SignatureException("No OpenPGP packets found in signature.");
            }
            if (!(object instanceof PGPSignatureList)) {
                throw new SignatureException("Expected PGPSignatureList packet but got");
            }
            PGPSignatureList sigs = (PGPSignatureList) object;
            if (sigs.isEmpty()) {
                throw new SignatureException("PGPSignatureList doesn't have a PGPSignature.");
            }
            return sigs.get(0);
        } catch (IOException e) {
            throw new SignatureException("Failed to extract PGPSignature object from .sig blob.", e);
        }
    }



}
