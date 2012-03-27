/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;

/**
 *
 * @author George
 */
public class User implements ActionListener{
    
    Socket sock;
    BufferedInputStream input;
    OutputStream output;
    public String name;
    public JCheckBox userSelectButton;
    public boolean isPressed = false;
    
    public User(Socket s,String n)
    {
        name = n;
        userSelectButton = new JCheckBox(n);
        userSelectButton.addActionListener(this);
        sock = s;
        try {
            input = new BufferedInputStream(s.getInputStream());
            output = s.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, "Failed to get in/outputs from socket", ex);
        }
    }
    
    public void send(byte[] b)
    {
        try {
            output.write(b);
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, "Failed to send", ex);
        }
    }
    
    public byte[] receive()
    {
        
        byte[] temp = new byte[1];
        try {
            if((input.available()==0))
                return null;
            int tempIn = input.read();
            temp[0] = (byte)tempIn;
            while(true)
            {
                if((input.available()==0))
                    break;
                temp = Arrays.copyOf(temp, temp.length+1);
                tempIn = input.read();
                temp[temp.length - 1] = (byte)tempIn;
            }
        } catch (IOException ex) {
            Logger.getLogger(RPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp;
    }

    public void actionPerformed(ActionEvent e) {
        isPressed = userSelectButton.isSelected();
    }
    
}
