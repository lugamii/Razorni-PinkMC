package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class MinecraftEncryption {
    private static final Logger a = LogManager.getLogger();

    public static KeyPair b() {
        try {
            KeyPairGenerator var0 = KeyPairGenerator.getInstance("RSA");
            var0.initialize(1024);
            return var0.generateKeyPair();
        } catch (NoSuchAlgorithmException var1) {
            var1.printStackTrace();
            a.error("Key pair generation failed!");
            return null;
        }
    }

    public static byte[] a(String var0, PublicKey var1, SecretKey var2) {
        try {
            return a("SHA-1", var0.getBytes("ISO_8859_1"), var2.getEncoded(), var1.getEncoded());
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    private static byte[] a(String var0, byte[]... var1) {
        try {
            MessageDigest var2 = MessageDigest.getInstance(var0);
            for (byte[] var6 : var1) {
                var2.update(var6);
            }
            return var2.digest();
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public static PublicKey a(byte[] var0) {
        try {
            X509EncodedKeySpec var1 = new X509EncodedKeySpec(var0);
            KeyFactory var2 = KeyFactory.getInstance("RSA");
            return var2.generatePublic(var1);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException var3) {
        }

        a.error("Public key reconstitute failed!");
        return null;
    }

    public static SecretKey a(PrivateKey var0, byte[] var1) {
        return new SecretKeySpec(b(var0, var1), "AES");
    }

    public static byte[] b(Key var0, byte[] var1) {
        return a(2, var0, var1);
    }

    private static byte[] a(int var0, Key var1, byte[] var2) {
        try {
            return a(var0, var1.getAlgorithm(), var1).doFinal(var2);
        } catch (IllegalBlockSizeException | BadPaddingException var4) {
            var4.printStackTrace();
        }

        a.error("Cipher data failed!");
        return null;
    }

    private static Cipher a(int var0, String var1, Key var2) {
        try {
            Cipher var3 = Cipher.getInstance(var1);
            var3.init(var0, var2);
            return var3;
        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException var4) {
            var4.printStackTrace();
        }

        a.error("Cipher creation failed!");
        return null;
    }

    public static Cipher a(int var0, Key var1) {
        try {
            Cipher var2 = Cipher.getInstance("AES/CFB8/NoPadding");
            var2.init(var0, var1, new IvParameterSpec(var1.getEncoded()));
            return var2;
        } catch (GeneralSecurityException var3) {
            throw new RuntimeException(var3);
        }
    }
}
