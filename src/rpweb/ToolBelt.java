/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
    
/** 
 *  
 * @author George
 */ 
public class ToolBelt implements ActionListener{
    
    private boolean isRedraw = true; //Used for thread safe redrawing of components
    
    private Tool[] tools;
    private JPanel toolPanel;
        private JPanel selectPanel;
            private JButton cycleBackButton;
            private JButton cycleForButton;
    private int currTool;
    
    public ToolBelt(){
        tools = new Tool[0];
        currTool = -1;
        cycleBackButton = new JButton("<<");
        cycleForButton = new JButton(">>");
        cycleBackButton.addActionListener(this);
        cycleForButton.addActionListener(this);
        selectPanel = new JPanel();
        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
        selectPanel.add(cycleBackButton);
        selectPanel.add(cycleForButton);
        toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.X_AXIS));
    }
    
    public boolean isValidTool(int i)
    {
        return (i<Array.getLength(tools)&&i>=0);
    }
    
    public boolean getRedraw()
    {
        if(isRedraw)
        {
            isRedraw = false;
            return true;
        }
        return false;
    }
    
    public void setTool(int i)
    {
        if(isValidTool(i))
        {currTool = i;}
        toolPanel.removeAll();
        toolPanel.add(selectPanel);
        toolPanel.add(tools[currTool].getSelectPanel());
        isRedraw = true;
    }
    
    public void cycleBack()
    {
        if(currTool == 0)
        {
            setTool(tools.length-1);
        }
        else
        {
            setTool(currTool-1);
        }
    }
    
    public void cycleForward()
    {
        if(currTool == tools.length-1)
        {
            setTool(0);
        }
        else
        {
            setTool(currTool+1);
        }
    }
    
    public JPanel getSelectPanel()
    {
        return toolPanel;
    }
    
    public void add(Tool t)
    {
        
        Tool[] tempList = new Tool[Array.getLength(tools)+1];
        System.arraycopy(tools, 0, tempList, 0, tools.length);
        tempList[tempList.length-1]=t;
        tools = tempList;
        if(currTool == -1)
        {
            setTool(0);
        }
    }
    
    public byte[] update(DrawingManager d)
    {
        return tools[currTool].update(d);        
    }
    
    public void mouseEvent(MouseEvent e, boolean pressed, boolean released)
    {
        tools[currTool].mouseEvent(e, pressed, released);
    }
    
    public void draw(BufferedImage im)
    {
        try{Graphics g = im.getGraphics();
        g.drawRect(10,300,100,100);
        g.drawImage(tools[currTool].icon,20,310,null);
        g.drawString(tools[currTool].name, 20, 350);}
        catch(Exception e){}
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(cycleBackButton))
        {
            cycleBack();
        }
        if(e.getSource().equals(cycleForButton))
        {
            cycleForward();
        }
    }
}   