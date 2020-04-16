package edu.hcmus.project.ebanking.ws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.security.rsa.RSAPrivateCrtKeyImpl;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SignatureService {

    @Autowired
    private ResourceLoader resourceLoader;
    private File privateKey;
    private Path keyDir;

    @Value(value = "${app.signature.private-key}")
    private String privateKeyPath;

    @Value(value = "{app.signature.key-dir}")
    private String keyDirPath;

    private Pattern PATTERN = Pattern.compile("(.*?)(?:\\((\\d+)\\))?(\\.[^.]*)?");



    @PostConstruct
    public void init() throws IOException {
        Resource privateKeyResource = resourceLoader.getResource(privateKeyPath);
        this.privateKey = privateKeyResource.getFile();
        this.keyDir = resourceLoader.getResource(keyDirPath).getFile().toPath();
    }

/*    public String uploadKey(MultipartFile key) throws IOException {
        String extension = StringUtils.getFilenameExtension(key.getOriginalFilename());

        Path targetLocation = this.keyDir.resolve(fileName);
        Files.copy(key.getInputStream(), targetLocation, StandardCopyOption.ATOMIC_MOVE);
        return fileName;
    }*/


    public byte[] getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(privateKey.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey prKey = kf.generatePrivate(spec);
        RSAPrivateCrtKeyImpl rsaPrivateKey = (RSAPrivateCrtKeyImpl) prKey;

        return kf.generatePublic(new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent())).getEncoded();
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

    public void verifyWithPublicKey(String content, byte[] signature, byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException, SignatureException {
        verifyWithRSAPublicKey(content, signature, keyBytes);
    }

    private void verifyWithRSAPublicKey(String content, byte[] signature, byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withRSA");
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
