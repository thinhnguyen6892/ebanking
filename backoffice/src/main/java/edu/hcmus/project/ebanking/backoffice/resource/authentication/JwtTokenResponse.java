package edu.hcmus.project.ebanking.backoffice.resource.authentication;

public class JwtTokenResponse {

    private final String token;
    private final String rl;

    public JwtTokenResponse(String token, String rl) {
        this.token = token;
        this.rl = rl;
    }

    public String getToken() {
        return this.token;
    }

    public String getRl() {
        return rl;
    }
}
