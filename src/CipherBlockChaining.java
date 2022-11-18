import java.util.Vector;
import java.util.*;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class CipherBlockChaining {

    public static String break_the_text (String plain_text ,Vector<String> plain_text_blocks )
    {
        String plain_text_first_ten_chars  = plain_text.substring(0,10);
        plain_text_blocks.add(plain_text_first_ten_chars);
        plain_text = plain_text.substring(10);
        return plain_text;
    }
    public static Vector<String> break_plain_text_vector(String plain_text ,Vector<String> plain_text_blocks)
    {
        int plain_text_size = plain_text.length();
        int how_much_run = plain_text_size/10+1;
        for (int i = 0; i < how_much_run; i++) // add filling last block
        {
            if (plain_text.length()>10)
            {
                plain_text = break_the_text(plain_text,plain_text_blocks);
            }
            else
            {
               int how_much_char_to_add = 10 - plain_text.length();
                for (int j = 0; j < how_much_char_to_add; j++)
                {
                    plain_text += "0";
                }
                plain_text = break_the_text(plain_text,plain_text_blocks);

            }
        }

        return plain_text_blocks;
    }

    public static String xoring_function(String plain_text,String init_vector)
    {
        String xor_plain_init="";
        for (int i = 0; i <init_vector.length() ; i++)
        {
            xor_plain_init +=(char)((int)plain_text.charAt(i) ^ (int)init_vector.charAt(i));
        }
        return xor_plain_init;
    }


    public static String cipher_text_generator(String plain_text,Map<Character,Character>map_key)
    {
        String cipher_text = "";
        String source = "abcdefgh";
        for (int i = 0; i < plain_text.length(); i++)
        {
            if (source.indexOf(plain_text.charAt(i))!=-1)
                cipher_text+=map_key.get(plain_text.charAt(i));
            else
                cipher_text+=plain_text.charAt(i);

        }
        return cipher_text;
    }
    public static Map<Character,Character> build_map(String key)
    {
        Map<Character,Character> map_key = new HashMap<Character,Character>();
        String source = "abcdefgh";
        for (int i = 0; i <key.length() ; i++)
        {
            map_key.put(source.charAt(i),key.charAt(i));
        }
        return map_key;
    }
        public static Map<Character,Character> inverse_build_map(String key)
        {
            Map<Character,Character> inverse_map_key = new HashMap<Character,Character>();
            String source = "abcdefgh";
            for (int i = 0; i <key.length() ; i++)
            {
                inverse_map_key.put(key.charAt(i),source.charAt(i));
            }
            return inverse_map_key;
        }

    public static String read_file(String file_name) {
        String data = "";
        try {
             data =  new String(Files.readAllBytes(Paths.get(file_name)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return data;
    }
    public static String encrypt(String plain_text, String init_vector, String key)
    {
        String final_cipher = "";
        Vector<String> plain_text_blocks = new Vector<String>();
        break_plain_text_vector(plain_text,plain_text_blocks);
        Map map_key=build_map(key);
        String cipher_good = read_file("CipherText_Example.txt");
        for (int i = 0; i < plain_text_blocks.size(); i++ )
        {
            plain_text_blocks.set(i,xoring_function(plain_text_blocks.elementAt(i),init_vector));
            String cipher_block = cipher_text_generator(plain_text_blocks.elementAt(i),map_key);
            init_vector = cipher_block;
            final_cipher += cipher_block ;
        }

        return final_cipher;
    }

    public static String decrypt(String encrypted_text, String init_vector, String key)
    {
        String final_plain_text = "";
        Vector <String> cipher_text_blocks = new Vector<String>();
        break_plain_text_vector(encrypted_text,cipher_text_blocks);
        Map map_key=inverse_build_map(key);
        String plain_text_good = read_file("PlainText_Example.txt");
        for (int i = 0; i < cipher_text_blocks.size(); i++ )
        {
            String cipher_block = cipher_text_blocks.elementAt(i);
            String cipher_decrypted = cipher_text_generator(cipher_text_blocks.elementAt(i),map_key);
            String plain_text = xoring_function(cipher_decrypted,init_vector);
            init_vector = cipher_block;
            final_plain_text += plain_text ;
        }

     return final_plain_text;
    }


    public static void main(String[] args)
    {
        String key_read = read_file("Key_Example.txt");
        String plain_text_good = read_file("PlainText_Example.txt");
        String cipher_good = read_file("CipherText_Example.txt");
        System.out.println("this is the amount of ciphered text " + cipher_good.length());
        System.out.println("this is the amount of plain text " + plain_text_good.length());
        String our_cipher = encrypt(read_file("PlainText_Example.txt"),read_file("IV_Example.txt"),"dhagcfbe");
        String our_plain_text = decrypt(read_file("CipherText_Example.txt"),read_file("IV_Example.txt"),"dhagcfbe");
        System.out.println( " well ? ");

        /*
        change the read file when gil answer
        remove last charaters when decrypting
         */

    }
}