/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpweb;

import java.util.Arrays;

/**
 *
 * @author George
 */
public class byteUtils {
    
    public static final byte TOOL_BYTE = (byte)0xE0;
    public static final byte TOOL_MASK = (byte)0xE0;
    public static final byte BRUSH_BYTE = (byte)0xE0;
    public static final byte ERASER_BYTE = (byte)0xE1;
    public static final byte TEXT_BYTE = (byte)0x80;
    public static final byte TEXT_END_BYTE = (byte) 0x81;
    
    public static void printBytes(byte[] b)
    {
        String temp = getByteString(b);
        if(temp!=null)
        System.out.println(temp);
    }
    
    public static String getByteString(byte[] b)
    {
        //Prints the binary for a byte[]. e.g. byte[]{0x1f} in, "00011111" out.
        String temp = "";
        for(int i = 0; i<b.length;i++)
        {
            char[] bits = new char[8];
            for(int j = 0;j<8;j++)
            {
                bits[j] = (((b[i] >> (7-j)) & 0x01)==0x01)? '1' : '0';
            }
            temp = temp + (String.valueOf(bits)+" ");
        }
        return (temp.equals(""))? null : temp;
    }
    
    public static int byteToInt(byte[] b, int index)
    {
        if(index+3>=b.length)
        {
            System.out.println("not Enough bytes to parse int");
            return -1;
        }
        int temp;
        int a1 = (((int)b[index+3] << 24)   &0xff000000);
        int a2 = (((int)b[index+2] << 16)    &0x00ff0000);
        int a3 = (((int)b[index+1] << 8)      &0x0000ff00);
        int a4 = (((int)b[index])             &0x000000ff);
        
        temp = a1|a2|a3|a4 ;
        return temp;
    }
    
    public static byte[] getBytes(int foo)
    {
        byte[] temp = new byte[4];
        
        temp[0] = (byte)foo;
        temp[1] = (byte)(foo >> 8);
        temp[2] = (byte)(foo >> 16);
        temp[3] = (byte)(foo >> 24);
        return temp;
    }
    public static byte[] ByteTobyte(Byte[] input)
    {
        byte[] temp = new byte[input.length];
        for(int i = 0;i<input.length;i++)
        {
            temp[i] = (byte)input[i];
        }
        return temp;
    }
}
