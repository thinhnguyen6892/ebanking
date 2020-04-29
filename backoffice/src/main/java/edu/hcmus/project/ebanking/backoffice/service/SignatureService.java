package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.contranst.SignType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private Pattern PATTERN = Pattern.compile("(.*?)(?:\\((\\d+)\\))?(\\.[^.]*)?");

    @PostConstruct
    public void init() throws IOException {
        Resource privateKeyResource = resourceLoader.getResource(privateKeyPath);
        this.privateKey = privateKeyResource.getFile();
    }

    public String signWithPrivateKey(String content) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, SignatureException {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        byte[] keyBytes = Files.readAllBytes(privateKey.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);
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

    }


}
