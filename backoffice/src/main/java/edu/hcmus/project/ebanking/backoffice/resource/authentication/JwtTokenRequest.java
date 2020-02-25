package edu.hcmus.project.ebanking.backoffice.resource.authentication;

public class JwtTokenRequest {

    private String username;
    private String password;

    private String reCAPTCHA;

    public JwtTokenRequest() {
        super();
    }

    public JwtTokenRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReCAPTCHA() {
        return reCAPTCHA;
    }

    public void setReCAPTCHA(String reCAPTCHA) {
        this.reCAPTCHA = reCAPTCHA;
    }
}
