/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.MouseInputListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author George
 */
public class RPServer extends KeyAdapter implements MouseInputListener,WindowListener{
    
    private JFrame screen;
        private JPanel content;
            private JPanel inputLogPanel;
                private JScrollPane logScroller;
                    private JTextPane chatLog;
                private JTextField inputBox;
            private JPanel userSelect;
                private JLabel userSelectLab;
                private Component glue;
            private JPanel drawPanel; 
                private java.awt.Canvas drawing;
                    private BufferedImage b;
                private JPanel toolInfo;
                
    private DrawingManager drawMan;
                
    Thread hostThread;
    
    private User[] users;
    
    
    public RPServer()
    {
        drawPanel = new JPanel();
        drawPanel.setLayout(new BoxLayout(drawPanel, BoxLayout.Y_AXIS));
        drawing = new Canvas();
        drawing.setMaximumSize(new Dimension(400,400));
        drawing.setSize(new Dimension(400,400));
        drawing.setBackground(Color.WHITE);
        toolInfo = new JPanel();
        toolInfo.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        b = new BufferedImage(drawing.getWidth(), drawing.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        drawPanel.add(drawing);
        drawPanel.add(toolInfo);
        
        glue = Box.createVerticalGlue();
        users = new User[0];
        screen = new JFrame("Chat Server");
        chatLog = new JTextPane();
        //chatLog.setMaximumSize(new Dimension(400,300));
        chatLog.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        chatLog.setEditable(false);
        StyledDocument doc = chatLog.getStyledDocument();
        MutableAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, Color.white);
        chatLog.setBackground(Color.black);
        chatLog.setCharacterAttributes(attr, true);
        logScroller = new JScrollPane(chatLog);
        inputBox = new JTextField(40);
        inputBox.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        inputBox.setMaximumSize(new Dimension(1000,20));
        inputBox.addKeyListener(this);
        inputLogPanel = new JPanel();
        inputLogPanel.setLayout(new BoxLayout(inputLogPanel, BoxLayout.Y_AXIS));
        inputLogPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        userSelectLab = new JLabel("Users");
        
        inputLogPanel.add(logScroller);
        inputLogPanel.add(Box.createRigidArea(new Dimension(0,5)));
        inputLogPanel.add(inputBox);
        inputLogPanel.setMaximumSize(new Dimension(400,490));
        
        userSelect = new JPanel();
        userSelect.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        userSelect.setLayout(new BoxLayout(userSelect, BoxLayout.Y_AXIS));
        userSelect.add(userSelectLab);
        userSelect.add(glue);
        
        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        content.add(inputLogPanel);
        content.add(userSelect);
        content.add(drawPanel);
        screen.add(content);
        screen.setSize(1100, 500);
        screen.setVisible(true);
        screen.addWindowListener(this);
        screen.setResizable(false);
        
        drawMan = new DrawingManager(drawing);
        toolInfo.add(drawMan.getToolSelectPanel());
        drawing.addMouseListener(drawMan);
        drawing.addMouseMotionListener(drawMan);
    }
    
    public void initHosting(Thread t)
    {
        hostThread = t;
        while(true)
        {
            if(drawMan.isRedraw());
                screen.validate();
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(RPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            byte[] drawTemp = drawMan.update();
            if(drawTemp.length!=0)
            {
                sendToAll(drawTemp);
            }
            drawMan.draw();
            
            for(int i = 0; i<users.length;i++)
            {
                byte[] temp = receive(i);
                if(temp!=null)
                {
                    processPacket(temp,i);
                }
            }
        }
    }
    
    
    
    public void processPacket(byte[] pack, int user)
    {
        int i = 0;
        try
        {
            while(i<pack.length)
            {
                if(pack[i]==byteUtils.TEXT_BYTE)
                {
                    i++;
                    ArrayList<Byte> tempIn = new ArrayList<Byte>(0);
                    while(pack[i]!=byteUtils.TEXT_END_BYTE)
                    {
                        tempIn.add(pack[i]);
                        i++;
                    }
                    processText(byteUtils.ByteTobyte(tempIn.toArray(new Byte[0])),user);
                    i++;
                }
                if((pack[i]&byteUtils.TOOL_MASK)==byteUtils.TOOL_BYTE)
                {
                    int temp = drawMan.processIn(pack, i,user);
                    if(temp == -1)
                    {
                        i = pack.length;
                    }
                    else i+= temp;
                }
                
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Error parsing packet");
        }
    }
    
    
    public void processText(byte[] input, int user)
    {
        printToChat(users[user].name+": "+String.valueOf(toChar(input)));
    }
    
    
    public char[] toChar(byte[] b)
    {
        char[] temp = new char[b.length];
        for(int i = 0;i<b.length;i++)
        {
            temp[i] = (char)b[i];
        }
        return temp;
    }
    
    public void addConnection(Socket s)
    {
        System.out.println("Connection Accepted");
        users = Arrays.copyOf(users, users.length+1);
        users[users.length - 1] = new User(s,"User "+users.length);
        userSelect.remove(glue);
        userSelect.add(users[users.length-1].userSelectButton);
        userSelect.add(glue);
        screen.validate();
        screen.repaint();
    }
    
    public void send(byte[] b)
    {
        for(int i = 0;i<users.length;i++){
            if(users[i].isPressed)
            users[i].send(b);
        }
    }
    
    public void sendText(byte[] b)
    {
        byte[] temp = new byte[b.length+2];
        temp[0] = byteUtils.TEXT_BYTE;
        System.arraycopy(b, 0, temp, 1, b.length);
        temp[temp.length-1] = byteUtils.TEXT_END_BYTE;
        send(temp);
    }
    
    public void sendToAll(byte[] b)
    {
        for(int i = 0;i<users.length;i++){
            users[i].send(b);
        }
    }
    
    public byte[] receive(int index)
    {
        if(index>=users.length||index<0)
        {
            System.out.println("Selected user does not exist");
            return null;
        }
        byte[] temp = users[index].receive();
        //System.out.println(temp==null);
        return temp;
    }
    
    public void printToChat(String s)
    {
        chatLog.setText(chatLog.getText()+"\n"+s);
    }
    
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {System.exit(0);}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    
    public void keyPressed(KeyEvent e)
    {
        if(e.getKeyCode()==KeyEvent.VK_ENTER)
        {
            sendText(inputBox.getText().getBytes());
            printToChat("Sent: " + inputBox.getText());
            inputBox.setText("");
        }
    }
    
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}
