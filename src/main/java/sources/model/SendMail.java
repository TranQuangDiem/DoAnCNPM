package sources.model;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.Properties;
@Component
public class SendMail {
    public static void sendEmail(String to, String subject, String content) throws Exception {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("trandiem1006@gmail.com");
        mailSender.setPassword("xxsaxaphjysbrfyt");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.starttls.enable", "true");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.transport.protocol", "smtp");
        javaMailProperties.put("mail.debug", "true");
        javaMailProperties.put("mail.smtp.allow8bitmime", "true");
        javaMailProperties.put("mail.smtps.allow8bitmime", "true");

        mailSender.setJavaMailProperties(javaMailProperties);
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                message.setTo(to);
                message.setFrom(mailSender.getUsername());
                message.setSubject(subject);
                message.setBcc(mailSender.getUsername());
                message.setText(content, true);
            }
        };
        mailSender.send(preparator);
    }
//    public static void sendMailMultiRecipients(String[] to, String subject, String content) {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//        mailSender.setUsername("trandiem1006@gmail.com");
//        mailSender.setPassword("xxsaxaphjysbrfyt");
//        Properties javaMailProperties = new Properties();
//        javaMailProperties.put("mail.smtp.starttls.enable", "true");
//        javaMailProperties.put("mail.smtp.auth", "false");
//        javaMailProperties.put("mail.transport.protocol", "smtp");
//        javaMailProperties.put("mail.debug", "true");
//        javaMailProperties.put("mail.smtp.allow8bitmime", "true");
//        javaMailProperties.put("mail.smtps.allow8bitmime", "true");
//
//        mailSender.setJavaMailProperties(javaMailProperties);
//        MimeMessagePreparator preparator = new MimeMessagePreparator() {
//            public void prepare(MimeMessage mimeMessage) throws Exception {
//                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//                message.setTo(to);
//                message.setFrom(mailSender.getUsername());
//                message.setSubject(subject);
//                message.setBcc(mailSender.getUsername());
//                message.setText(content, true);
//            }
//        };
//        mailSender.send(preparator);
//    }
}
