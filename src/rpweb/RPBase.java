/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.MouseInputListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author George
 */
public abstract class RPBase extends KeyAdapter implements WindowListener, MouseInputListener{
    
    private JFrame screen;
        private JPanel content;
            private JPanel chatPane;
                private JScrollPane logScroller;
                    private JTextPane chatLog;
                private JTextField inputBox;
            private JPanel drawPanel;
                private java.awt.Canvas drawing;
                private JPanel toolInfo;
                    private JPanel subToolInfo;
    
    private BufferedImage b;    

    private Socket sock;
    private BufferedInputStream input;
    private OutputStream output;
    
    private DrawingManager drawMan;
    
    private Random rand;
    
    public RPBase() 
    {
        drawPanel = new JPanel();
        drawPanel.setLayout(new BoxLayout(drawPanel, BoxLayout.Y_AXIS));
        toolInfo = new JPanel();
        toolInfo.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        subToolInfo = new JPanel();
        toolInfo.add(subToolInfo);
        rand = new Random();
        screen = new JFrame("Temporary Base");
        drawing = new Canvas();
        drawing.setMaximumSize(new Dimension(400,400));
        drawing.setSize(new Dimension(400,400));
        drawing.addMouseListener(this);
        drawing.addMouseMotionListener(this);
        drawing.setBackground(Color.white);
        chatLog = new JTextPane();
        //chatLog.setMaximumSize(new Dimension(400,300));
        chatLog.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        chatLog.setEditable(false);
        chatLog.setBackground(Color.black);
        logScroller = new JScrollPane(chatLog);
        inputBox = new JTextField(40);
        inputBox.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        inputBox.setMaximumSize(new Dimension(1000,20));
        inputBox.addKeyListener(this);
        
        chatPane = new JPanel();
        chatPane.setLayout(new BoxLayout(chatPane, BoxLayout.Y_AXIS));
        chatPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        chatPane.add(logScroller);
        chatPane.add(Box.createRigidArea(new Dimension(0,5)));
        chatPane.add(inputBox);
        
        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        drawPanel.add(drawing);
        drawPanel.add(toolInfo);
        
        content.add(chatPane);
        content.add(drawPanel);
        
        b = new BufferedImage(drawing.getWidth(), drawing.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        
        
        screen.add(content);
        screen.setSize(900, 500);
        screen.addWindowListener(this);
        screen.setVisible(true);
        
        drawMan = new DrawingManager(drawing);
        toolInfo.add(drawMan.getToolSelectPanel());
        drawing.addMouseListener(drawMan);
        drawing.addMouseMotionListener(drawMan);
    }
    
    public void startClient(Socket s)
    {
        sock = s;
        if(s == null)
            System.out.println("Error: null socks");
        try {
            input = new BufferedInputStream(s.getInputStream());
            output = s.getOutputStream();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(RPBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true)
        {
            if(drawMan.isRedraw()){
                screen.validate();                
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(RPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            byte[] temp = receive();
            if(temp!=null)
            {
                processPacket(temp);
            }
            byte[] drawTemp = drawMan.update();
            if(drawTemp.length!=0)
            {
                send(drawTemp);
            }
            drawMan.draw();
        }
    }
    
    public void processPacket(byte[] pack)
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
                    processText(byteUtils.ByteTobyte(tempIn.toArray(new Byte[0])));
                    i++;
                }
                else if((pack[i]&byteUtils.TOOL_MASK)==byteUtils.TOOL_BYTE)
                {
                    int temp = ToolSorter.processIn(pack, i,-1,drawMan);
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
    
    public void processText(byte[] input)
    {
        printToChat("Server"+": "+String.valueOf(toChar(input)));
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
    
    public void send(byte[] b)
    {
        try {
            output.write(b);
        } catch (IOException ex) {
            Logger.getLogger(RPBase.class.getName()).log(Level.SEVERE, null, ex);
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
        }
            
        catch (IOException ex) 
        {
            Logger.getLogger(RPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temp;
    }
    
    public void printToChat(String s)
    {
        StyledDocument doc = chatLog.getStyledDocument();
        MutableAttributeSet attr = new SimpleAttributeSet(chatLog.getCharacterAttributes());
        StyleConstants.setForeground(attr, Color.cyan);
        doc.setCharacterAttributes(doc.getLength(), doc.getLength()+10,attr, true);
        try {
            doc.insertString(doc.getLength(), s + "\n", attr);
        } catch (BadLocationException ex) {
            Logger.getLogger(RPBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        chatLog.setForeground(Color.white);
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
