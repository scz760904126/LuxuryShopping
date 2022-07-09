package com.ydles.oauth.service;

import com.ydles.oauth.util.AuthToken;

/**
 * @author Scz
 * @date 2022/4/16 22:21
 */
public interface AuthService {

    AuthToken login(String username, String password, String clientId, String clientSecret);
}
