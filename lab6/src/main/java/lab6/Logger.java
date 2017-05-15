package lab6;

/**
 * Created by alexandr on 14.05.17.
 */
public interface Logger {
    void addEvent(String who, String event);
    void addError(String who, String event);
    void displayErrorMsg(String title, String text);
    void displayResultMsg(String title, String text);
    void stop();
}
