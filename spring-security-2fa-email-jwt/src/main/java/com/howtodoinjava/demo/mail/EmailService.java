package com.howtodoinjava.demo.mail;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.howtodoinjava.demo.mail.properties.EmailConfigurationProperties;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@EnableConfigurationProperties(EmailConfigurationProperties.class)
public class EmailService {

	private final JavaMailSender javaMailSender;
	private final EmailConfigurationProperties emailConfigurationProperties;

	@Async
	public void sendEmail(String toMail, String subject, String messageBody) {
		final var simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(emailConfigurationProperties.getUsername());
		simpleMailMessage.setTo(toMail);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(messageBody);
		javaMailSender.send(simpleMailMessage);
	}
}