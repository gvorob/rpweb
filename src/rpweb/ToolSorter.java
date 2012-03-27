/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.awt.image.BufferedImage;

/**
 *
 * @author George
 */
public class ToolSorter {
    public static int processIn(byte[] input, int i, int user, DrawingManager d)
    {
        if(input[i] == byteUtils.ERASER_BYTE)
        {
            i++;
            byte[] tempIn = new byte[EraseTool.BYTES_IN_TRANSFER-1];
            for (int j = 0;j<EraseTool.BYTES_IN_TRANSFER-1;j++)
            {
                tempIn[j] = input[i];
                i++;
            }
            
            EraseTool.processIn(tempIn,d);
            return EraseTool.BYTES_IN_TRANSFER; 
        }
        if(input[i] == byteUtils.BRUSH_BYTE)
        {
            i++;
            byte[] tempIn = new byte[BrushTool.BYTES_IN_TRANSFER-1];
            for (int j = 0;j<BrushTool.BYTES_IN_TRANSFER-1;j++)
            {
                tempIn[j] = input[i];
                i++;
            }
            
            BrushTool.processIn(tempIn,d);
            return BrushTool.BYTES_IN_TRANSFER; 
        }
        System.out.println("TOOL FLAG "+input[i]+" NOT RECOGNIZED");
        return -1;
    }
}
