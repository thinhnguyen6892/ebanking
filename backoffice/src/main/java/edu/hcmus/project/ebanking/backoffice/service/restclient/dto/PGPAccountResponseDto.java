package edu.hcmus.project.ebanking.backoffice.service.restclient.dto;


public class PGPAccountResponseDto {

    private Content account;

    public Content getAccount() {
        return account;
    }

    public void setAccount(Content account) {
        this.account = account;
    }

    public class Content {
        private String fullName;
        private String phoneNumner;
        private String email;
        private String birthday;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhoneNumner() {
            return phoneNumner;
        }

        public void setPhoneNumner(String phoneNumner) {
            this.phoneNumner = phoneNumner;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }
    }

}



