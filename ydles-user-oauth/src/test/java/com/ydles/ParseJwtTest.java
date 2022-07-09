package com.ydles;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/**
 * @author Scz
 * @date 2022/4/16 17:27
 */
public class ParseJwtTest {
    @Test
    public void parseJwt(){
        //基于公钥去解析jwt
        String jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZGRyZXNzIjoidGFpeXVhbiIsImNvbnBhbnkiOiJpdGxpbHMifQ.n2yK1EXV3HiX8lNgLLWOZGExUBlz4EOQpvbtJoiQ4G1Cw9fk8WtCxQ1_SqyoYAF1jAUuC5vq1j4lAxgfrSrlq0wt421tIQFAKAn1uGMFkUmLM3CI6r4rXpDclEsxiD67G6UM9T8YZsrGkw4-gI9hlxHNk4AIw9H1vXIF2Uk-Vih2QCvpOKECMjtUyzXPgeaEBQ49mTtUiPi-lV1wCmosiunNEDfgjSNlAqCFxO0LSycBcBHxdcB81T4UqeWzZR-3HcyXYYL3JET5D5_NEzJ2cOYy2ak7hYNNFKcFzuWX-Ji2_2WsRE66d11vRUJUGX4qmvLBUiLQZE2oNNYpC4vQRQ";
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArw2sALq2Z32zto1w4QqmQ6J0yf0ZN/MhyTN8b4bOwPo676UUfWS7yMLVBiju1DRqD3UlBD0ndOc5mPjG+E0VOkxcSKUzYNE3jI+z0HOixZ222PJdJqO5TxjkuWEG3FFVLRSUqyFriJsMWaygEM1H/yAmIdTmnbLHOE+OK1KmlHVirxih3cbQcRS7Brta3aZ5OZ46rIQxLehbduwfHRhNnUQpfelPhxVjs6AmJvjhX09i0gRXUxVTsn/PvyfPPZ6xUJ9ZQKNEzONdjIcU+/oeTX26q84N55nht8a/SDd08/DBkvC5V3lXH+Wj84wf9mut73SPFLZUePBQpO446f51uwIDAQAB-----END PUBLIC KEY-----";
        //解析令牌
        Jwt token = JwtHelper.decodeAndVerify(jwt, new RsaVerifier(publicKey));
        //获取负载
        String claims = token.getClaims();
        System.out.println(claims);

        String passwd = BCrypt.hashpw("123456", BCrypt.gensalt());
        System.out.println(passwd);
    }
}
