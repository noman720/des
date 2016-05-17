/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package des;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * File: Utils.java
 * Created on Feb 6, 2016, 1:49:23 PM
 * @author NOMAN
 */
public class Utils {
    private static int getBit(byte[] data, int pos) {
        int posByte = pos / 8;
        int posBit = pos % 8;       
        byte valByte = data[posByte];
        int valInt = valByte >> (8 - (posBit + 1)) & 0x0001;
        return valInt;
    }

    private static void setBit(byte[] data, int pos, int val) {
        int posByte = pos / 8;
        int posBit = pos % 8;
        byte oldByte = data[posByte];
        oldByte = (byte) (((0xFF7F >> posBit) & oldByte) & 0x00FF);
        byte newByte = (byte) ((val << (8 - (posBit + 1))) | oldByte);
        data[posByte] = newByte;
    }

    private static byte[] readBytes(String in) throws Exception {
        FileInputStream fis = new FileInputStream(in);
        int numOfBytes = fis.available();
        byte[] buffer = new byte[numOfBytes];
        fis.read(buffer);
        fis.close();
        return buffer;
    }

    private static void writeBytes(byte[] data, String out)
            throws Exception {
        FileOutputStream fos = new FileOutputStream(out);
        fos.write(data);
        fos.close();
    }

    private static void printBytes(byte[] data, String name) {
        System.out.println("");
        System.out.println(name + ":");
        for (int i = 0; i < data.length; i++) {
            System.out.print(byteToBits(data[i]) + " ");
        }
        System.out.println();
    }

    private static String byteToBits(byte b) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 8; i++) {
            buf.append((int) (b >> (8 - (i + 1)) & 0x0001));
        }
        return buf.toString();
    }
}
