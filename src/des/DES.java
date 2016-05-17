/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package des;

/**
 * File: DES.java Created on Feb 4, 2016, 02:05:39 AM
 *
 * @author NOMAN
 */
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DES {
    // Initial Permutation table
    private static final byte[] IP = {
        58, 50, 42, 34, 26, 18, 10, 2,
        60, 52, 44, 36, 28, 20, 12, 4,
        62, 54, 46, 38, 30, 22, 14, 6,
        64, 56, 48, 40, 32, 24, 16, 8,
        57, 49, 41, 33, 25, 17, 9, 1,
        59, 51, 43, 35, 27, 19, 11, 3,
        61, 53, 45, 37, 29, 21, 13, 5,
        63, 55, 47, 39, 31, 23, 15, 7
    };
    
    // Permuted Choice 1 table
    private static final byte[] PC1 = {
        57, 49, 41, 33, 25, 17, 9,
        1, 58, 50, 42, 34, 26, 18,
        10, 2, 59, 51, 43, 35, 27,
        19, 11, 3, 60, 52, 44, 36,
        63, 55, 47, 39, 31, 23, 15,
        7, 62, 54, 46, 38, 30, 22,
        14, 6, 61, 53, 45, 37, 29,
        21, 13, 5, 28, 20, 12, 4
    };
    // Permuted Choice 2 table
    private static final byte[] PC2 = {
        14, 17, 11, 24, 1, 5,
        3, 28, 15, 6, 21, 10,
        23, 19, 12, 4, 26, 8,
        16, 7, 27, 20, 13, 2,
        41, 52, 31, 37, 47, 55,
        30, 40, 51, 45, 33, 48,
        44, 49, 39, 56, 34, 53,
        46, 42, 50, 36, 29, 32
    };
    // Array to store the number of rotations that are to be done on each round
    private static final byte[] rotations = {
        1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };
    // Expansion (aka E-box) table
    private static final byte[] E = {
        32, 1, 2, 3, 4, 5,
        4, 5, 6, 7, 8, 9,
        8, 9, 10, 11, 12, 13,
        12, 13, 14, 15, 16, 17,
        16, 17, 18, 19, 20, 21,
        20, 21, 22, 23, 24, 25,
        24, 25, 26, 27, 28, 29,
        28, 29, 30, 31, 32, 1
    };
    // S-boxes (i.e. Substitution boxes)
    private static final byte[][] S = {{
            14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
            0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
            4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
            15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
        }, {
            15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
            3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
            0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
            13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
        }, {
            10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
            13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
            13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
            1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
        }, {
            7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
            13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
            10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
            3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
        }, {
            2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
            14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
            4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
            11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
        }, {
            12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
            10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
            9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
            4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
        }, {
            4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
            13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
            1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
            6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
        }, {
            13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
            1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
            7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
            2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
        }};
    // Permutation table
    private static final byte[] P = {
        16, 7, 20, 21,
        29, 12, 28, 17,
        1, 15, 23, 26,
        5, 18, 31, 10,
        2, 8, 24, 14,
        32, 27, 3, 9,
        19, 13, 30, 6,
        22, 11, 4, 25
    };
    // Final permutation (aka Inverse permutation) table
    private static final byte[] FP = {
        40, 8, 48, 16, 56, 24, 64, 32,
        39, 7, 47, 15, 55, 23, 63, 31,
        38, 6, 46, 14, 54, 22, 62, 30,
        37, 5, 45, 13, 53, 21, 61, 29,
        36, 4, 44, 12, 52, 20, 60, 28,
        35, 3, 43, 11, 51, 19, 59, 27,
        34, 2, 42, 10, 50, 18, 58, 26,
        33, 1, 41, 9, 49, 17, 57, 25
    };
//    // 28 bits each, used as storage in the KS (Key Structure) rounds to 
//    // generate round keys (aka subkeys)
//    private static int[] C = new int[28];
//    private static int[] D = new int[28];
    // Decryption requires the 16 subkeys to be used in the exact same process
    // as encryption, with the only difference being that the keys are used
    // in reverse order, i.e. last key is used first and so on. Hence, during
    // encryption when the keys are first generated, they are stored in this
    // array. In case we wish to separate the encryption and decryption
    // programs, then we need to generate the subkeys first in order, store
    // them and then use them in reverse order.
    private static int[][] subkey = new int[16][48];

    public static void main(String args[]) {       
    
        /*****************************/
        /***** For Key Generation ****/
        /*****************************/

        /**
         * 1.   Get input key from a file key.txt of maximum 8 characters.
         * 2.   Generate key bits [array] to store the key in binary by 64 bit (8 char * 8 bit = 64 bit)
         * 3.   Generate sub keys from the key bits
         */

        try {
            //step-1
            File file = new File("key.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            
            
            String strKey = readFile("key.txt");
                    
            //step-2
            int [] keyBits = stringToBinaryArray64Bit(strKey);
            display64Bits(keyBits);
            System.out.println("reconstract key: "+binaryArray64BitToStringUTF_8(keyBits)); //reconstract string from bit array
            
            //step-3
            generateSubKeys(keyBits);
            
            
        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(DES.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            

        /*****************************/
        /******* For Encryption ******/
        /*****************************/
        /**
         * 1.   Get input text from a file input.txt
         * 2.   Generate input text blocks [array] to store all inputs text by 8 character.
         * 3.   Generate input bits blocks [array] to store all inputs text block in binary by 64 bit (8 char * 8 bit = 64 bit)
         * 4.   Encrypt every input bits block and convert it to String.
         * 5.   Write the output string to output.txt
         */
        
        try {
            //step-1
            File file = new File("input.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            
            String plainText = readFile("input.txt");
            System.out.println("plainText: "+plainText+"\nlenght: "+plainText.length());
            
            //step-2 & step-3
            //store in 8 cahracters blocks
            int input_text_length = plainText.length();
            int block_size = (int) Math.ceil((double)plainText.length()/8);
            System.out.println("block size: "+block_size);
            String [] input_text_blocks = new String[block_size];
            
            int [][] input_bit_blocks = new int [block_size][64];
            
            int firstIndex = 0;
            int lastIndex = 8;
            for (int i = 0; i < block_size; i++) {
                if (lastIndex > input_text_length) {
                    lastIndex = input_text_length;
                }

                System.out.println("firstIndex :"+firstIndex+" lastIndex: "+lastIndex);
                
                input_text_blocks[i] = plainText.substring(firstIndex, lastIndex);
                
                input_bit_blocks[i] = stringToBinaryArray64Bit(input_text_blocks[i]);
                
                display64Bits(input_bit_blocks[i]);
                
                System.out.println("block ["+i+"]: "+input_text_blocks[i]);
                
                firstIndex = lastIndex;
                lastIndex = firstIndex + 8;                
            }
            
            //step-4
            System.out.println("********* Encryption ************");
            int [][] encrypt_bit_blocks = new int [block_size][64];
            String [] encrypt_text_blocks = new String[block_size];
            
            String finalEncryptedStr = null;
            
            for (int i = 0; i < block_size; i++) {
                
                encrypt_bit_blocks[i] = encrypt(input_bit_blocks[i]);
                
                display64Bits(encrypt_bit_blocks[i]);
                
                encrypt_text_blocks[i] = binaryArray64BitToStringUTF_8(encrypt_bit_blocks[i]);
                
                finalEncryptedStr += encrypt_text_blocks[i];
                
                System.out.println("ciper block ["+i+"]: "+encrypt_bit_blocks[i]);
                
            }
            
            //step-5
            File outFile = new File("output.txt");
            if(!outFile.exists()){
                outFile.createNewFile();
            }
            writeFile("output.txt", finalEncryptedStr);
            
            
//            int [] keyBits = stringToBinaryArray64Bit(strKey);
//            display64Bits(keyBits);
            //System.out.println(binaryStringToStringUTF_8(keyBits)); //reconstract string from bit array
            
            
            
            
        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(DES.class.getName()).log(Level.SEVERE, null, ex);
        }
        


        /*****************************/
        /******* For Decryption ******/
        /*****************************/
        /**
         * 1.   Get input text from a file output.txt
         * 2.   Generate input text blocks [array] to store all inputs text by 8 character.
         * 3.   Generate input bits blocks [array] to store all inputs text block in binary by 64 bit (8 char * 8 bit = 64 bit)
         * 4.   Decrypt every input bits block and convert it to String.
         * 5.   Write the output string to decrypt_output.txt
         */



        
//        System.out.println("Enter the input as a 16 character hexadecimal value:");
//        String input = new Scanner(System.in).nextLine();
//        int inputBits[] = new int[64];
//        // inputBits will store the 64 bits of the input as a an int array of
//        // size 64. This program uses int arrays to store bits, for the sake
//        // of simplicity. For efficient programming, use long data type. But
//        // it increases program complexity which is unnecessary for this
//        // context.
//        for (int i = 0; i < 16; i++) {
//            // For every character in the 16 bit input, we get its binary value
//            // by first parsing it into an int and then converting to a binary
//            // string
//            String s = Integer.toBinaryString(Integer.parseInt(input.charAt(i) + "", 16));
//
//            // Java does not add padding zeros, i.e. 5 is returned as 111 but
//            // we require 0111. Hence, this while loop adds padding 0's to the
//            // binary value.
//            while (s.length() < 4) {
//                s = "0" + s;
//            }
//            // Add the 4 bits we have extracted into the array of bits.
//            for (int j = 0; j < 4; j++) {
//                inputBits[(4 * i) + j] = Integer.parseInt(s.charAt(j) + "");
//            }
//        }
//
//        // Similar process is followed for the 16 bit key
//        System.out.println("Enter the key as a 16 character hexadecimal value:");
//        String key = new Scanner(System.in).nextLine();
//        int keyBits[] = new int[64];
//        for (int i = 0; i < 16; i++) {
//            String s = Integer.toBinaryString(Integer.parseInt(key.charAt(i) + "", 16));
//            while (s.length() < 4) {
//                s = "0" + s;
//            }
//            for (int j = 0; j < 4; j++) {
//                keyBits[(4 * i) + j] = Integer.parseInt(s.charAt(j) + "");
//            }
//        }
//
//        // permute(int[] inputBits, int[] keyBits, boolean isDecrypt)
//        // method is used here. This allows encryption and decryption to be
//        // done in the same method, reducing code.
//        System.out.println("\n+++ ENCRYPTION +++");
//        int outputBits[] = permute(inputBits, keyBits, false);
//        System.out.println("\n+++ DECRYPTION +++");
//        permute(outputBits, keyBits, true);
    }
    
    static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }
    
    static void writeFile(String path, String value) throws IOException {
        byte[] bytes = value.getBytes(Charset.forName("utf-8"));
        Files.write(Paths.get(path), bytes, StandardOpenOption.WRITE);
    }
    
    
    private static String roundBinaryStringTo8Bit(String str) {
        String newStr;
        if (str.length() < 8) {
            newStr = "00000000".substring(str.length()) + str;
        } else if (str.length() > 8) {
            newStr = str.substring(0, 7);
        } else {
            newStr = str;
        }
        return newStr;
    }
    
    
    private static int[] stringToBinaryArray64Bit(String str) throws NullPointerException {
        try {
            int[] binArray = new int[64];
            String binaryStr = null;

            int i = 0;
            byte[] infoBin = str.getBytes("ASCII");
            if (infoBin.length < 8) {
                int dummyArray[] = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
                for (i = 0; i < (8 - infoBin.length); i++) {
                    System.arraycopy(dummyArray, 0, binArray, 8 * i, 8);
                }
            }
            for (byte b : infoBin) {
                //convert every char to binary string
                binaryStr = roundBinaryStringTo8Bit(Integer.toBinaryString(b));

                //convert binary string to int array
                int[] intArray = new int[binaryStr.length()];

                for (int j = 0; j < binaryStr.length(); j++) {
                    intArray[j] = Character.digit(binaryStr.charAt(j), 2);
                }

                //copy block array to main array
                System.arraycopy(intArray, 0, binArray, 8 * i, 8);
                i++;
            }

            return binArray;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DES.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private static String binaryArray64BitToStringUTF_8(int[] intArray) throws NullPointerException {
        byte[] byteArray = new byte[8];
        int index = 0;
        for (int i = 0; i < intArray.length; i += 8) {

            String output = new String();
            for (int j = 0; j < 8; j++) {
                output += intArray[i + j];
            }            
            
            //if not empty byte then add to byte array
            //remove padding bits
            if(!output.equals("00000000")){
                byteArray[index] = (byte) Integer.parseInt(output, 2);// Byte.parseByte(output, 2);
                index++;
            }
            
        }
        try {
            return new String(byteArray, "ASCII");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DES.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    

    private static int[] permute(int[] inputBits, int[] keyBits, boolean isDecrypt) {
        // Initial permutation step takes input bits and permutes into the
        // newBits array
        int newBits[] = new int[inputBits.length];
        for (int i = 0; i < inputBits.length; i++) {
            newBits[i] = inputBits[IP[i] - 1];
        }

        // 16 rounds will start here
        // L and R arrays are created to store the Left and Right halves of the
        // subkey respectively
        int L[] = new int[32];
        int R[] = new int[32];
        int i;

        // generate sub keys
        generateSubKeys(keyBits);
        
        
        // After PC1 the first L and R are ready to be used and hence looping
        // can start once L and R are initialized
        System.arraycopy(newBits, 0, L, 0, 32);
        System.arraycopy(newBits, 32, R, 0, 32);
        System.out.print("\nL0 = ");
        displayBits(L);
        System.out.print("R0 = ");
        displayBits(R);
        for (int n = 0; n < 16; n++) {
            System.out.println("\n-------------");
            System.out.println("Round " + (n + 1) + ":");
            // newR is the new R half generated by the Fiestel function. If it
            // is encrpytion then the KS method is called to generate the
            // subkey otherwise the stored subkeys are used in reverse order
            // for decryption.
            int fR[] = new int[0];
            if (isDecrypt) {
                //for decryption
                fR = fiestel(R, subkey[15 - n]);
                System.out.print("Round key = ");
                displayBits(subkey[15 - n]);
            } else {
                //for encryption
                fR = fiestel(R, getSubKey(n));
                System.out.print("Round key = ");
                displayBits(subkey[n]);
            }
            // xor-ing the L and new R gives the new L value. new L is stored
            // in R and new R is stored in L, thus exchanging R and L for the
            // next round.
            int newR[] = xor(L, fR);
            L = R; //newL
            R = newR;
            System.out.print("L = ");
            displayBits(L);
            System.out.print("R = ");
            displayBits(R);
        }

        // R and L has the two halves of the output before applying the final
        // permutation. This is called the "Preoutput".
        int output[] = new int[64];
        System.arraycopy(R, 0, output, 0, 32);
        System.arraycopy(L, 0, output, 32, 32);
        int finalOutput[] = new int[64];
        // Applying FP table to the preoutput, we get the final output:
        // Encryption => final output is ciphertext
        // Decryption => final output is plaintext
        for (i = 0; i < 64; i++) {
            finalOutput[i] = output[FP[i] - 1];
        }

        // Since the final output is stored as an int array of bits, we convert
        // it into a hex string:
        String hex = new String();
        for (i = 0; i < 16; i++) {
            String bin = new String();
            for (int j = 0; j < 4; j++) {
                bin += finalOutput[(4 * i) + j];
            }
            int decimal = Integer.parseInt(bin, 2);
            hex += Integer.toHexString(decimal);
        }
        if (isDecrypt) {
            System.out.print("Decrypted text: ");

        } else {
            System.out.print("Encrypted text: ");
        }
        System.out.println(hex.toUpperCase());
        return finalOutput;
    }
    
    /**
     * This method is used to encrypt 64 bit input block to 64 bit output block
     * @param inputBits - 64 bit
     * @return outputBits - 64 bit
     */
    private static int[] encrypt(int[] inputBits) {
        // Initial permutation step takes input bits and permutes into the
        // newBits array
        int newBits[] = new int[inputBits.length];
        for (int i = 0; i < inputBits.length; i++) {
            newBits[i] = inputBits[IP[i] - 1];
        }

        // 16 rounds will start here
        // L and R arrays are created to store the Left and Right halves of the
        // subkey respectively
        int L[] = new int[32];
        int R[] = new int[32];

        // After PC1 the first L and R are ready to be used and hence looping
        // can start once L and R are initialized
        System.arraycopy(newBits, 0, L, 0, 32);
        System.arraycopy(newBits, 32, R, 0, 32);
        //System.out.print("\nL0 = ");
        //displayBits(L);
        //System.out.print("R0 = ");
        //displayBits(R);
        for (int n = 0; n < 16; n++) {
            //System.out.println("\n-------------");
            //System.out.println("Round " + (n + 1) + ":");
            // newR is the new R half generated by the Fiestel function. If it
            // is encrpytion then the KS method is called to generate the
            // subkey otherwise the stored subkeys are used in reverse order
            // for decryption.
            int fR[] = new int[0];
            //for encryption
            fR = fiestel(R, getSubKey(n));
            //System.out.print("Round key = ");
            //displayBits(getSubKey(n));
            // xor-ing the L and new R gives the new L value. new L is stored
            // in R and new R is stored in L, thus exchanging R and L for the
            // next round.
            int newR[] = xor(L, fR);
            L = R; //newL
            R = newR;
            //System.out.print("L = ");
            //displayBits(L);
            //System.out.print("R = ");
            //displayBits(R);
        }

        // R and L has the two halves of the output before applying the final
        // permutation. This is called the "Preoutput".
        int output[] = new int[64];
        System.arraycopy(R, 0, output, 0, 32);
        System.arraycopy(L, 0, output, 32, 32);
        int finalOutput[] = new int[64];
        // Applying FP table to the preoutput, we get the final output:
        // Encryption => final output is ciphertext
        // Decryption => final output is plaintext
        for (int i = 0; i < 64; i++) {
            finalOutput[i] = output[FP[i] - 1];
        }

//        // Since the final output is stored as an int array of bits, we convert
//        // it into a hex string:
//        String hex = new String();
//        for (int i = 0; i < 16; i++) {
//            String bin = new String();
//            for (int j = 0; j < 4; j++) {
//                bin += finalOutput[(4 * i) + j];
//            }
//            int decimal = Integer.parseInt(bin, 2);
//            hex += Integer.toHexString(decimal);
//        }
//        
//        System.out.print("Encrypted text: ");
//        
//        System.out.println(hex.toUpperCase());
        return finalOutput;
    }
    
    /**
     * This method is used to decrypt 64 bit input block to 64 bit output block
     * @param inputBits - 64 bit
     * @return outputBits - 64 bit
     */
    private static int[] decrypt(int[] inputBits) {
        // Initial permutation step takes input bits and permutes into the
        // newBits array
        int newBits[] = new int[inputBits.length];
        for (int i = 0; i < inputBits.length; i++) {
            newBits[i] = inputBits[IP[i] - 1];
        }

        // 16 rounds will start here
        // L and R arrays are created to store the Left and Right halves of the
        // subkey respectively
        int L[] = new int[32];
        int R[] = new int[32];

        // After PC1 the first L and R are ready to be used and hence looping
        // can start once L and R are initialized
        System.arraycopy(newBits, 0, L, 0, 32);
        System.arraycopy(newBits, 32, R, 0, 32);
        System.out.print("\nL0 = ");
        displayBits(L);
        System.out.print("R0 = ");
        displayBits(R);
        for (int n = 0; n < 16; n++) {
            System.out.println("\n-------------");
            System.out.println("Round " + (n + 1) + ":");
            // newR is the new R half generated by the Fiestel function. If it
            // is encrpytion then the KS method is called to generate the
            // subkey otherwise the stored subkeys are used in reverse order
            // for decryption.
            int fR[] = new int[0];
            //for encryption
            fR = fiestel(R, getSubKey(15 - n)); //use sub key in reverse order
            System.out.print("Round key = ");
            displayBits(getSubKey(15 - n));
            // xor-ing the L and new R gives the new L value. new L is stored
            // in R and new R is stored in L, thus exchanging R and L for the
            // next round.
            int newR[] = xor(L, fR);
            L = R; //newL
            R = newR;
            System.out.print("L = ");
            displayBits(L);
            System.out.print("R = ");
            displayBits(R);
        }

        // R and L has the two halves of the output before applying the final
        // permutation. This is called the "Preoutput".
        int output[] = new int[64];
        System.arraycopy(R, 0, output, 0, 32);
        System.arraycopy(L, 0, output, 32, 32);
        int finalOutput[] = new int[64];
        // Applying FP table to the preoutput, we get the final output:
        // Encryption => final output is ciphertext
        // Decryption => final output is plaintext
        for (int i = 0; i < 64; i++) {
            finalOutput[i] = output[FP[i] - 1];
        }

//        // Since the final output is stored as an int array of bits, we convert
//        // it into a hex string:
//        String hex = new String();
//        for (int i = 0; i < 16; i++) {
//            String bin = new String();
//            for (int j = 0; j < 4; j++) {
//                bin += finalOutput[(4 * i) + j];
//            }
//            int decimal = Integer.parseInt(bin, 2);
//            hex += Integer.toHexString(decimal);
//        }
//        
//        System.out.print("Encrypted text: ");
//        
//        System.out.println(hex.toUpperCase());
        return finalOutput;
    }
    
    
    /**
     * This method is used to get round/sub keys
     * @param round 0 to 15
     * @return subkey[round] 48 bit
     */
    private static int[] getSubKey(int round){
        return subkey[round];
    }

    /**
     * This method is used to generate round keys
     * @param keyBits 56 bit key
     */
    private static void generateSubKeys(int[] keyBits) {
        int[] C = new int[28];
        int[] D = new int[28];
        int i;
        // Permuted Choice 1 is done here for keyBits
        // and stored first 28 bit into C and last 28 bit into D.
        for (i = 0; i < 28; i++) {
            C[i] = keyBits[PC1[i] - 1];
        }
        for (; i < 56; i++) {
            D[i - 28] = keyBits[PC1[i] - 1];
        }
        
        /**************************************************/
        /* generate 16 round keys and store into subkey[] */
        /**************************************************/
        
        //do for 16 round
        for (int round = 0; round < 16; round++) {
            // C1 and D1 are the new values of C and D which will be generated in
            // every round.
            int[] newC = new int[28];
            int[] newD = new int[28];
            
            // The rotation array is used to set how many rotations are to be done
            int rotationTimes = (int) rotations[round];
            // leftShift() method is used for rotation (the rotation is basically)
            // a left shift operation, hence the name.
            newC = leftShift(C, rotationTimes);
            newD = leftShift(D, rotationTimes);
            // CnDn stores the combined C1 and D1 halves
            int CnDn[] = new int[56];
            System.arraycopy(newC, 0, CnDn, 0, 28);
            System.arraycopy(newD, 0, CnDn, 28, 28);
            // Kn stores the subkey, which is generated by applying the PC2 table
            // to CnDn
            int Kn[] = new int[48];
            for (int index = 0; index < Kn.length; index++) {
                Kn[index] = CnDn[PC2[index] - 1]; // [0--55]
            }

            //storing subkey into subkey[]
            subkey[round] = Kn;
            
            //update C & D for next round
            C = newC;
            D = newD;

        }
    }

    private static int[] fiestel(int[] R, int[] roundKey) {
        // Method to implement Fiestel function.
        // First the 32 bits of the R array are expanded using E table.
        int expandedR[] = new int[48];
        for (int i = 0; i < 48; i++) {
            expandedR[i] = R[E[i] - 1];
        }
        // We xor the expanded R and the generated round key
        int temp[] = xor(expandedR, roundKey);
        // The S boxes are then applied to this xor result and this is the
        // output of the Fiestel function.
        int output[] = sBlock(temp);
        return output;
    }

    private static int[] xor(int[] a, int[] b) {
        // Simple xor function on two int arrays
        int answer[] = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            answer[i] = a[i] ^ b[i];
        }
        return answer;
    }

    private static int[] sBlock(int[] bits) {
        // S-boxes are applied in this method.
        int output[] = new int[32];
        // We know that input will be of 32 bits, hence we will loop 32/4 = 8
        // times (divided by 4 as we will take 4 bits of input at each
        // iteration).
        for (int i = 0; i < 8; i++) {
            // S-box requires a row and a column, which is found from the
            // input bits. The first and 6th bit of the current iteration
            // (i.e. bits 0 and 5) gives the row bits.
            int row[] = new int[2];
            row[0] = bits[6 * i];
            row[1] = bits[(6 * i) + 5];
            String sRow = row[0] + "" + row[1];
            // Similarly column bits are found, which are the 4 bits between
            // the two row bits (i.e. bits 1,2,3,4)
            int column[] = new int[4];
            column[0] = bits[(6 * i) + 1];
            column[1] = bits[(6 * i) + 2];
            column[2] = bits[(6 * i) + 3];
            column[3] = bits[(6 * i) + 4];
            String sColumn = column[0] + "" + column[1] + "" + column[2] + "" + column[3];
            // Converting binary into decimal value, to be given into the
            // array as input
            int iRow = Integer.parseInt(sRow, 2);
            int iColumn = Integer.parseInt(sColumn, 2);
            int x = S[i][(iRow * 16) + iColumn];
            // We get decimal value of the S-box here, but we need to convert
            // it into binary:
            String s = Integer.toBinaryString(x);
            // Padding is required since Java returns a decimal '5' as '111' in
            // binary, when we require '0111'.
            while (s.length() < 4) {
                s = "0" + s;
            }
            // The binary bits are appended to the output
            for (int j = 0; j < 4; j++) {
                output[(i * 4) + j] = Integer.parseInt(s.charAt(j) + "");
            }
        }
        // P table is applied to the output and this is the final output of one
        // S-box round:
        int finalOutput[] = new int[32];
        for (int i = 0; i < 32; i++) {
            finalOutput[i] = output[P[i] - 1];
        }
        return finalOutput;
    }

    private static int[] leftShift(int[] bits, int n) {
        // Left shifting takes place here, i.e. each bit is rotated to the left
        // and the leftmost bit is stored at the rightmost bit. This is a left
        // shift operation.
        int answer[] = new int[bits.length];
        System.arraycopy(bits, 0, answer, 0, bits.length);
        for (int i = 0; i < n; i++) {
            int temp = answer[0];
            for (int j = 0; j < bits.length - 1; j++) {
                answer[j] = answer[j + 1];
            }
            answer[bits.length - 1] = temp;
        }
        return answer;
    }

    private static void displayBits(int[] bits) {
        // Method to display int array bits as a hexadecimal string.
        for (int i = 0; i < bits.length; i += 4) {
            String output = new String();
            for (int j = 0; j < 4; j++) {
                output += bits[i + j];
            }
            System.out.print(Integer.toHexString(Integer.parseInt(output, 2)));
        }
        System.out.println();
    }
    
    private static void display64Bits(int[] bits) {
        // Method to display int array bits as a hexadecimal string.
        for (int i = 0; i < bits.length; i += 8) {
            for (int j = 0; j < 8; j++) {
                System.out.print(bits[i + j]);//output += bits[8*i + j];
            }
            System.out.print(" ");
        }
        System.out.println();
    }
    
    
    
}