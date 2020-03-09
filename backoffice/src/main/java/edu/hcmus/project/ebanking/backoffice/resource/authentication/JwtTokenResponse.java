package edu.hcmus.project.ebanking.backoffice.resource.authentication;

import edu.hcmus.project.ebanking.backoffice.resource.user.dto.UserDto;

public class JwtTokenResponse {

    private final String token;
    private final String rl;
    private final UserDto userInfo;

    public JwtTokenResponse(String token, String rl, UserDto userInfo) {
        this.token = token;
        this.rl = rl;
        this.userInfo = userInfo;
    }

    public String getToken() {
        return this.token;
    }

    public String getRl() {
        return rl;
    }

    public UserDto getUserInfo() {
        return userInfo;
    }
}
