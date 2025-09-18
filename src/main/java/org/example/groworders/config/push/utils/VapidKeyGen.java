package org.example.groworders.config.push.utils;

import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

@Slf4j
public class VapidKeyGen {
    public static void main(String[] args) throws Exception {
        // 1) P-256 키쌍 생성
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair kp = kpg.generateKeyPair();

        // 2) PUBLIC: 0x04 || X(32) || Y(32)
        ECPublicKey pub = (ECPublicKey) kp.getPublic();
        byte[] x = toFixedLength(pub.getW().getAffineX(), 32);
        byte[] y = toFixedLength(pub.getW().getAffineY(), 32);
        ByteBuffer bb = ByteBuffer.allocate(65);
        bb.put((byte) 0x04).put(x).put(y);
        String publicKeyBase64Url = base64Url(bb.array());

        // 3) PRIVATE: 32바이트 S 값 (PKCS#8 DER 아님!)
        ECPrivateKey priv = (ECPrivateKey) kp.getPrivate();
        String privateKeyBase64Url = base64Url(toFixedLength(priv.getS(), 32));

        log.debug(publicKeyBase64Url);
        log.debug(privateKeyBase64Url);
    }

    private static String base64Url(byte[] b) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
    private static byte[] toFixedLength(BigInteger bi, int len) {
        byte[] tmp = bi.toByteArray();                 // 부호비트 포함 가능
        if (tmp.length == len) return tmp;
        if (tmp.length == len + 1 && tmp[0] == 0x00)   // leading 0 제거
            return java.util.Arrays.copyOfRange(tmp, 1, tmp.length);
        byte[] out = new byte[len];
        System.arraycopy(tmp, Math.max(0, tmp.length - len), out, Math.max(0, len - tmp.length), Math.min(len, tmp.length));
        return out;
    }
}
