package edu.hcmus.project.ebanking.ws;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.bc.BcPGPObjectFactory;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class InstantTest {

    private static final String keyFile = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
            "Version: BCPG C# v1.6.1.0\n" +
            "\n" +
            "mQENBF40kdYBCACCVK51Dxywh9ty3/gj3Y5oPYvNfTKfCHfHlLPJonIJBVZcAUX2\n" +
            "/I42Evzr2Rh97LALJ0k/JXD6QWucl6v0uba1WH6bOmY2u6wZcElDo+KZBdsyE6WX\n" +
            "GzSEmaFdeBB01LAAON8i4N/FjU8ONXkH1J2AslGKelq2S+4fFAmCQYxIxu1Osz27\n" +
            "ve4b+lfLGMxr6NnJyF7HPOWelxSlnQU52Dr8NX1zLfqF/0ARbeR1T97ICS7UHd3B\n" +
            "6vCPxCU55PQMce+2bGh9ZCaDCx/rs+kkZvj7ODdhEKKo1esr6NRijExRckRSI+UU\n" +
            "5BLgtZxybdoH/EC2CfMdhgBNfkp2X3ke8/tlABEBAAG0E2Zha2VlbWFpbEBnbWFp\n" +
            "bC5jb22JARwEEAECAAYFAl40kdYACgkQ57rufV6L2f7l2ggAgCURQH1wtYxoZlfz\n" +
            "HGJG3pvaB7J8eolUTlusZ8gnMFvauQDdrJSGD+UtGnQ6+6u2zh6P9iZyB5yiLzhB\n" +
            "uDZCaxuLt9HFJCEdQHV4YyaST5BY8Nh3LR/LOFLtgnYui1OydgD+yLM2lSHoSOWz\n" +
            "xN0AJ+nfbM1c7z6wDlDs/9rPaEnkR0wjhUN1nDKC1dDa6Pmrl97KohRZDVawXr0g\n" +
            "We0IWfHkh+2+F7loymqcpFsp7iNBZoEKM1M/teqqACmkXaq2T+IzAbdpMJ1sj1nc\n" +
            "7jTRbeojh0KrLekM/za85ufqE3Vpq9FMP0+50wCcUOBwAfadORmEzKz/zIT9UT7Q\n" +
            "MOdJXg==\n" +
            "=B9Du\n" +
            "-----END PGP PUBLIC KEY BLOCK-----";

    private static final String content= "Internetbanki1ng_NDK_FiveSao";
    private static final String sign = "-----BEGIN PGP MESSAGE-----\n" +
            "Version: DidiSoft OpenPGP Library for .NET 1.9.1\n" +
            "\n" +
            "owEBbgGR/pANAwACAee67n1ei9n+AcspYghzaWduLnR4dF5WI9hJbnRlcm5ldGJh\n" +
            "bmtpbmdfTkRLX0ZpdmVTYW+JATEEAAECABsFAl5WI9gUHGZha2VlbWFpbEBnbWFp\n" +
            "bC5jb20ACgkQ57rufV6L2f56xgf/Yy26+Km7wJAqWOYCjdHSVKURI6iKu+SP2S7z\n" +
            "s1R6oZpEs67vwf8hoI/ynfmbBscRBvtqTkRccf4Q/XBqDukHnE85oSL0lO9MZ1CY\n" +
            "nKOh6dNAtKH47ommdJpoUyeb1GS9peHJr6DoYvz47evDJAllk5T+B7Yr7t/xhUO3\n" +
            "B6laPoQzZOb8mGOYZNIpFTs9y4mpsIiFZ73sZD4NMVjUQXc3RcPaav8DvAY4Hp33\n" +
            "S28y6LKVzDSYCK8mm5G+L+Zt52nMob41vfHA6IEtZsfHBidPVWyEWEtpf/9Bcwai\n" +
            "oZ5lYZpCyctR+i+SjmU84IpfmpXrkWswQNNrKgBud1MA0eDd0g==\n" +
            "=Z738\n" +
            "-----END PGP MESSAGE-----";

    private static final String tonkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmvc+zzrqg9OYPThf35GIsMLracOrw1HiMbvnzEyL7kglSY4MmPyFBALHI7evYCplBWQonQ/0Zv7WXTsNquuOjK1KufEm8R8kn9c6Q+/Udh3vKrBmshEuq0j8+lAUzUBcZxS2nNf0p3CjgqNX4I6gKfQjP+SgI04CIASQmw5hDYOa4scNbDn+jHyFlaBId+B5BD1qUdl9igESibjFwZJW/ldL8sHd4UmexWt65ywIdaL08PcExu/rQMbOGtXM8X76SriODp2VNXh5kheGle1kecOBOCM0+VmtY4xmJjRFDy12y7yCH0SsBFjTT+RuGFg9SVQNYnUB0t3dd2i/GbEXjQIDAQAB";

    public static void main(String[] args) throws SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        verifyWithPGPPublicKey(content, sign.getBytes(), keyFile.getBytes());
    }



    public static void verifyWithPGPPublicKey(String content, byte[] signature, byte[] keyBytes) throws SignatureException {
        byte[] data = content.getBytes();
        PGPPublicKey publicKey = loadPublicKey(keyBytes);
        Security.addProvider(new BouncyCastleProvider());
        PGPSignature sig = pgpExtractSignature(signature,content.getBytes(),  publicKey);

    }

    private static PGPPublicKey loadPublicKey(byte[] keyBytes) {
        try (InputStream input = ByteSource.wrap(keyBytes).openStream();
             InputStream decoder = PGPUtil.getDecoderStream(input)) {
            return new BcPGPPublicKeyRing(decoder).getPublicKey();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static PGPSignature pgpExtractSignature(byte[] signature, byte[] content, PGPPublicKey pgpPublicKey)
            throws SignatureException {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(signature);
            PGPObjectFactory decoder = new BcPGPObjectFactory(PGPUtil.getDecoderStream(input));
            Object message = decoder.nextObject();
            if (message == null) {
                throw new SignatureException("No OpenPGP packets found in signature.");
            }
            BcKeyFingerprintCalculator  calculator = new BcKeyFingerprintCalculator();
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
                return null;
            } else {
                throw new PGPException("message is not a simple encrypted file - type unknown.");
            }
//            return new ByteArrayInputStream(bytes);
        } catch (IOException | PGPException e) {
            throw new SignatureException("Failed to extract PGPSignature object from .sig blob.", e);
        }
    }
}
