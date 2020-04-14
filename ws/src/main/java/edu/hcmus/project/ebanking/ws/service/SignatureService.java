package edu.hcmus.project.ebanking.ws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
public class SignatureService {

    @Autowired
    private ResourceLoader resourceLoader;
    private File privateKey;
    private Cipher cipher;

    @Value(value = "${app.signature.private-key}")
    private String privateKeyPath;

    @Value(value = "${app.signature.public-key}")
    private String publicKeyPath;


    @PostConstruct
    public void init() throws IOException {
        Resource privateKeyResource = resourceLoader.getResource(privateKeyPath);
        this.privateKey = privateKeyResource.getFile();

        Resource publicKeyResource = resourceLoader.getResource(publicKeyPath);
    }

    public File getSamplePrivateKey() throws IOException {
        return privateKey;
    }

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

    public void verifyWithPublicKey(String content, byte[] signature, String key) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        byte[] keyBytes = Files.readAllBytes(resourceLoader.getResource(key).getFile().toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);
        sig.initVerify(publicKey);
        sig.update(content.getBytes());
        if(!sig.verify(signature)) {
            throw new SignatureException();
        }
    }




}
