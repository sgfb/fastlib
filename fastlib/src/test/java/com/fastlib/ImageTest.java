package com.fastlib;

import android.os.MemoryFile;
import android.util.Base64;

import com.fastlib.db.MemoryPool;
import com.fastlib.image_manager.request.CallbackParcel;
import com.fastlib.image_manager.ImageManager;
import com.fastlib.image_manager.request.ImageRequest;
import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ImageTest{
    @Test
    public void testIp() throws UnknownHostException, SocketException {
        Gson gson = new Gson();
        Token token = new Token();
        token.scope = "document:pig.jpg";
        token.deadline = System.currentTimeMillis() + 60 * 60 * 24;
        String json = gson.toJson(token);
        String base64 = Base64.encodeToString(json.getBytes(), Base64.DEFAULT);
        String encodedSign = Base64.encodeToString(genHMAC(base64, "25zrRYcIPtjPQiT_5nORGiIYZKHA7cEloV6XcIXo"), Base64.DEFAULT);
        String tokenStr = "pZrGdZzYyHF4QHjjsK8H_Dss-alTcLpwN9Wzl-ku" + ":" + encodedSign + ":" + base64;
        System.out.println(tokenStr);
    }

    public static class Token {
        public String scope;
        public long deadline;
    }

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public static byte[] genHMAC(String data, String key) {
        byte[] result = null;
        try {
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            //用给定密钥初始化 Mac 对象
            mac.init(signinKey);
            //完成 Mac 操作
            return mac.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        } catch (InvalidKeyException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
