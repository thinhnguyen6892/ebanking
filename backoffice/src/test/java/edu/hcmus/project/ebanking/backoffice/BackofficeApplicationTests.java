package edu.hcmus.project.ebanking.backoffice;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Locale;

@SpringBootTest()
@EnableAutoConfiguration
@ComponentScan(basePackages= {"edu.hcmus.project.ebanking.backoffice.resource",
		"edu.hcmus.project.ebanking.backoffice.service"})
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
public class BackofficeApplicationTests {

	@MockBean
	public JavaMailSender javaMailSender;

	@Bean
	public UserDetailsService userDetailsService() {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(User.withUsername("user").password("password").roles("'USER'", "'ADMIN'", "'STAFF'").build());
		return manager;
	}

	@MockBean
	public PasswordEncoder passwordEncoderBean;

}
