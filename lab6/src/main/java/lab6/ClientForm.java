package lab6;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created by alexandr on 14.05.17.
 */
public class ClientForm implements Logger, WindowListener{
    public JPanel root;
    private JButton CONNECTTOTHESERVERButton;
    private JButton CALCEXPButton;
    private JButton FINDDEFINITIONButton;
    private JTextField exp;
    private JTextField textField2;
    private JButton ADDDEFINITIONButton;
    private JTextField textField3;
    private JTextField login;
    private JTextPane textPane1;
    private JPasswordField passwordField1;
    private Client client;

    public ClientForm() {
        textPane1.setContentType("text/html");
        CONNECTTOTHESERVERButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (client != null) {
                    client.stop();
                    client = null;
                    CONNECTTOTHESERVERButton.setText("START CLIENT");
                } else {
                    client = new Client.Builder()
                            .setAuthentication(login.getText(),passwordField1.getText())
                            .setPort(9090)
                            .setLogger(ClientForm.this)
                            .build();
                    client.start();
                    CONNECTTOTHESERVERButton.setText("STOP CLIENT");
                }
            }
        });
        CALCEXPButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (client != null) {
                    try {
                        client.calcExp(Double.parseDouble(exp.getText()));
                    } catch (Exception ex){
                        displayErrorMsg("Invalid Data","Invalid number: "+exp.getText());
                    }
                } else {
                    displayErrorMsg("No connection","You should connect to the server at first");
                }
            }
        });
    }

    @Override
    public void addEvent(String who, String event) {
        SwingUtilities.invokeLater(()-> {
                    HTMLDocument doc=(HTMLDocument) textPane1.getStyledDocument();
                    try {
                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), "<b>"+who+": </b>"+event+"<br>");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
        );
    }


    @Override
    public void addError(String who, String event) {
        SwingUtilities.invokeLater(()-> {
                    HTMLDocument doc=(HTMLDocument) textPane1.getStyledDocument();
                    try {
                        doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), "<font color='red'><b>"+who+": </b>"+event+"<br></font>");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
        );
    }

    @Override
    public void displayErrorMsg(String title, String text) {
        JOptionPane.showMessageDialog(new JFrame(), text, title,
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void displayResultMsg(String title, String text) {
        JOptionPane.showMessageDialog(new JFrame(), text, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void stop() {
        client.stop();
        client = null;
        client = null;
        CONNECTTOTHESERVERButton.setText("START CLIENT");
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        client.stop();
        client = null;
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
