/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author George
 */
public class Launcher extends MouseInputAdapter implements WindowListener, ActionListener{
    
    private JFrame loader;
    private JTextField inputIP,inputPort;
    private JRadioButton buttonHost,buttonClient;
    private JButton buttonConfirm;
    private ButtonGroup hostClientGroup;
    private JPanel content;
    private JPanel row2;
    private JLabel errorLab;
    private JCheckBox defPort;
    
    private ServerSocket serverSock = null;
    
    public boolean IPClicked = false;
    public boolean portClicked = false;
    public boolean isSubmitted = false;
    public boolean isHost = false;
    
    public Launcher()
    {
        loader = new JFrame("Launcher");
        //inputIP = new JTextField("Enter IP here.",12);    DEBUG,PUT IN LATER
        inputIP = new JTextField("localhost",12);  //TAKE THIS OUT
        inputPort = new JTextField("Enter port (optional)", 10);
        inputPort.setMaximumSize(new Dimension(1000,20));        
        inputPort.setVisible(false);
        buttonHost = new JRadioButton("Host");
        buttonClient = new JRadioButton("Client");
        hostClientGroup = new ButtonGroup();
        hostClientGroup.add(buttonHost);
        hostClientGroup.add(buttonClient);
        buttonClient.doClick();
        buttonConfirm = new JButton("Connect");
        defPort = new JCheckBox("Use default port", true);
        content = new JPanel();
        errorLab = new JLabel();
        row2 = new JPanel();
        
        content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
        row2.setLayout(new BoxLayout(row2,BoxLayout.X_AXIS));
        row2.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttonConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loader.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        loader.addWindowListener(this);
        
        inputIP.setPreferredSize(new Dimension (200,20));
        inputIP.setMaximumSize(new Dimension(1000,20));
        
        inputIP.addMouseListener(this);
        inputPort.addMouseListener(this);
        buttonConfirm.addActionListener(this);
        defPort.addActionListener(this);
        buttonHost.addActionListener(this);
        buttonClient.addActionListener(this);
        
        row2.add(buttonClient);
        row2.add(buttonHost);
        row2.add(Box.createHorizontalGlue());
        row2.add(defPort);
        row2.validate();
        
        content.add(inputIP);
        content.add(Box.createRigidArea(new Dimension(0,5)));
        content.add(row2);
        content.add(Box.createRigidArea(new Dimension(0,5)));
        content.add(inputPort);
        content.add(Box.createVerticalGlue());
        content.add(buttonConfirm);
        content.validate();
        
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loader.setSize(300, 170);
        loader.setResizable(false);
        loader.add(content);        
        
    }    
    
    public Socket launch()
    {
        loader.setVisible(true);
        while(true)
        {
            try{Thread.sleep(10);}
            catch (InterruptedException e){}
            if(isSubmitted)
            {
                if(isHost)
                {
                    startListening();
                    return null;
                }
                else
                {
                    return connect();
                    
                }
            }
        }
    }
    
    public ServerSocket hostInfo()
    {
        return serverSock;
    }
    
    public void startListening()
    {
        System.out.println("Hosting...");
        int port;
        if(defPort.isSelected())
            port = 20736;
        else
            port = Integer.parseInt(inputPort.getText());
        System.out.println("Listening on port: "+port);
        try {
            serverSock = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, "Error in startlistening method", ex);
        }
    }
    
    public Socket connect()
    {
        Socket s = new Socket();
        try {
            InetAddress ip4;
            int port;
            if(inputIP.getText().equals("localhost"))
                ip4 = Inet4Address.getLocalHost();
            else
                ip4 = Inet4Address.getByName(inputIP.getText());
            if(defPort.isSelected())
                port = 20736;
            else
                port = Integer.parseInt(inputPort.getText());
            InetSocketAddress ip = new InetSocketAddress(ip4, port);
            System.out.println("Connecting to IP "+ip.getAddress().getHostAddress()+ " on port "+port);
            s.connect(ip, 0);
            System.out.println("Successful.");
            return s;
        }
        catch (UnknownHostException ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE,"Connection failed", ex);
            System.exit(-1);
        }
        return null;
    }

    public void close()
    {
        loader.setEnabled(false);
        loader.setVisible(false);
    }
    
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {System.exit(0);}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    
    public void mouseClicked(MouseEvent e)
    {
        if(e.getComponent().equals(inputIP))
        {
            if(!IPClicked)
            {
                IPClicked = true;
                inputIP.setText("");                
            }
        }
        if(e.getComponent().equals(inputPort))
        {
            if(!portClicked)
            {
                portClicked = true;
                inputPort.setText("");
            }
        }
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(defPort))
        {
            inputPort.setVisible(!defPort.isSelected());
            loader.validate();
        }
        if(e.getSource().equals(buttonConfirm))
        {
            isSubmitted = true;
        }
        if(e.getSource().equals(buttonClient))
        {
            isHost = false;
        }
        if(e.getSource().equals(buttonHost))
        {
            isHost = true;
        }
    }
}
