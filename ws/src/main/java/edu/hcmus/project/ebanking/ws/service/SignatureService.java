package edu.hcmus.project.ebanking.ws.service;

import org.springframework.beans.factory.annotation.Autowired;
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
    private File publicKey;
    private Cipher cipher;

    @PostConstruct
    public void init() throws IOException {
        Resource privateKeyResource = resourceLoader.getResource("classpath:static/privatekey.pem");
        this.privateKey = privateKeyResource.getFile();

        Resource publicKeyResource = resourceLoader.getResource("classpath:static/publickey.pem");
        this.publicKey = publicKeyResource.getFile();
    }

    public String signWithPrivateKey(String content) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, SignatureException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        Signature rsa = Signature.getInstance("SHA256withRSA");
        byte[] keyBytes = Files.readAllBytes(privateKey.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PrivateKey privateKey = kf.generatePrivate(spec);
//        this.cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//        this.cipher.doFinal(content.getBytes())
        rsa.initSign(keyPair.getPrivate());
        rsa.update(content.getBytes());
        return new String(Base64Utils.encode(rsa.sign()));
    }

    public boolean verifyWithPublicKey(String content, byte[] signature) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        byte[] keyBytes = Files.readAllBytes(publicKey.toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);
//        this.cipher.init(Cipher.DECRYPT_MODE, publicKey)
//        cipher.doFinal(content.getBytes())
        sig.initVerify(publicKey);
        sig.update(content.getBytes());
        return sig.verify(signature);
    }




}
