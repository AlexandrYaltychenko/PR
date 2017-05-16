import lab6.Client;
import lab6.ClientForm;
import lab6.Logger;
import lab6.Server;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by alexandr on 14.05.17.
 */
public class Main implements Logger {
    private JPanel root;
    private JButton clearServerLogButton;
    private JButton STOPServerButton;
    private JButton STARTCLIENTButton;
    private JTextPane textPane1;
    private JLabel serverStat;
    private Server server;
    private int clientIndexer = 1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new Main().root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    public Main(){
        System.out.println("FORM CREATED!");
        textPane1.setContentType("text/html");
        serverStat.setForeground(Color.RED);
        /**/
        /**/
        clearServerLogButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                addEvent("me","jora");
            }
        });
        STOPServerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (server == null) {
                    server = new Server(Main.this);
                    server.start(9090);
                    server.listen();
                    STOPServerButton.setText("STOP SERVER");
                    serverStat.setText("SERVER IS RUNNING");
                    serverStat.setForeground(Color.GREEN);
                } else {
                    server.stop();
                    server = null;
                    STOPServerButton.setText("START SERVER");
                    serverStat.setText("SERVER IS STOPPED");
                    serverStat.setForeground(Color.RED);
                }
            }
        });
        STARTCLIENTButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFrame frame = new JFrame("Client "+(clientIndexer++));
                ClientForm clientForm = new ClientForm();
                frame.setContentPane(clientForm.root);
                frame.addWindowListener(clientForm);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setResizable(false);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
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
        server.stop();
        server = null;
        STOPServerButton.setText("START SERVER");
        serverStat.setText("SERVER IS STOPPED");
        serverStat.setForeground(Color.RED);
    }
}
