package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.Transaction;
import edu.hcmus.project.ebanking.backoffice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private Environment env;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${mail.from}")
    private String from;

    @Value("${app.dev.mode}")
    private Boolean devMode;

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart, isHtml, to, subject, content);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, "UTF-8");
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);
            if(!devMode) {
                javaMailSender.send(mimeMessage);
            }
            log.debug("Sent e-mail from {} to User '{}'", from, to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent from {} to user '{}', exception is: {}", from, to, e.getMessage());
        }
    }

    public void sendUserPasswordEmail(User user, String password) {
        log.debug("Sending transaction confirmation e-mail to '{}'", user.getEmail());
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("name", String.format("%s %s", user.getFirstName(), user.getLastName()));
        context.setVariable("user_name", user.getUsername());
        context.setVariable("password", password);
        String content = templateEngine.process("user_creation", context);
        String subject = "Welcome to T2TrC e-Banking!";
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    public void sendTransactionConfirmationEmail(User user, String otp) {
        log.debug("Sending transaction confirmation e-mail to '{}'", user.getEmail());
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("otp_code", otp);
        String content = templateEngine.process("transaction_confimation", context);
        String subject = "Transaction Confirmation";
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    public void sendRecoverPasswordEmail(User user, String otp, String baseUrl){
        log.debug("Sending password recovery instruction e-mail to '{}'", user.getEmail());
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("otp_code", otp);
        context.setVariable("redirect_url", baseUrl);
        String content = templateEngine.process("password_recovery", context);
        String subject = "Password assistance";
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    public void sendDebtPaymentNotificationEmail(User user, String debtId, Transaction transaction){
        log.debug("Sending debt payment notification e-mail to '{}'", user.getEmail());
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("name", String.format("%s %s", user.getFirstName(), user.getLastName()));
        context.setVariable("date", transaction.getDate().format(DateTimeFormatter.ISO_TIME));
        context.setVariable("amount", transaction.getAmount());
        context.setVariable("debt", debtId);
        context.setVariable("account", transaction.getSource());
        String content = templateEngine.process("debt_payment", context);
        String subject = "Payment Acknowledgment";
        sendEmail(user.getEmail(), subject, content, false, true);
    }
}
