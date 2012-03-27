/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author George
 */
public class Tool {
    
    public Image icon;
    public String name;
    
    public Tool(){
        
    }
    
    public void mouseEvent(MouseEvent e, boolean pressed, boolean released)
    {
        
    }
    
    public byte[] update(DrawingManager d){return null;}
    
    public JPanel getSelectPanel()
    {
        JPanel temp = new JPanel();
        temp.add(new JLabel(name));
        return temp;
    }
}
