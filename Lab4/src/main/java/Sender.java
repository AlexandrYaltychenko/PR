import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.io.File;

/**
 * Created by Alexandr on 30.04.2017.
 */
public class Sender implements ISender{
    private String adr;
    private IForm form;
    public Sender(String adr, IForm form){
        this.adr = adr;
        this.form = form;
    }
    @Override
    public void send(String subject, String msg){
        System.out.println("Client simplu de posta – trimitere mesaj");
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("127.0.0.1");
            email.setSmtpPort(25);
            email.setFrom(adr);
            email.setSubject(subject);
            email.setHtmlMsg(msg);
            email.addTo("aa@ialticenco.name");
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String subject, String msg, File file){
        System.out.println("SENDING WITH ATTACHMENT!");
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(file.getAbsolutePath());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("127.0.0.1");
            email.setSmtpPort(25);
            email.setFrom(adr);
            email.setSubject(subject);
            email.attach(attachment);
            email.setHtmlMsg(msg);
            email.addTo("aa@ialticenco.name");
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}
