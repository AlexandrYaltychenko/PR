import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * Created by Alexandr on 30.04.2017.
 */
public interface IForm {
    void clearTable();
    void addMsgToTable(String subject, String from, String date, String attachment);
    void displayMessage(MimeMessage mimeMessage);
    void clearSenderForm();
    void deleteMessage(int pos);
}
