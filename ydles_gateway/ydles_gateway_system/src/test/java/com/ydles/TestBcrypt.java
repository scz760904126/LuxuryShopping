package com.ydles;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * @author Scz
 * @date 2022/4/12 10:32
 */
public class TestBcrypt {
    @Test
    public void test(){
        for (int i = 0; i < 10; i++) {
            String hashpw = BCrypt.hashpw("123456", BCrypt.gensalt());
            System.out.println(hashpw);
            boolean checkpw = BCrypt.checkpw("123456", hashpw);
            System.out.println(checkpw);
        }
    }
}
