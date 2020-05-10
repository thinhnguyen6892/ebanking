package edu.hcmus.project.ebanking.backoffice.service.restclient.dto;


public class PGPTransactionResponseDto {

    private Content message;

    public Content getMessage() {
        return message;
    }

    public void setMessage(Content message) {
        this.message = message;
    }


    public class Content {
        private Boolean active;
        private String message;

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}



