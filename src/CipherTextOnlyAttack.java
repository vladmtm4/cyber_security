import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.*;
import java.math.*;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.*;

public class CipherTextOnlyAttack {

    public static int factorialUsingForLoop(int n) {
        int fact = 1;
        for (int i = 2; i <= n; i++) {
            fact = fact * i;
        }
        return fact;
    }

    public static String break_the_text(String plain_text, Vector<String> plain_text_blocks) {
        String plain_text_first_ten_chars = plain_text.substring(0, 10);
        plain_text_blocks.add(plain_text_first_ten_chars);
        plain_text = plain_text.substring(10);
        return plain_text;
    }

    public static void read_file_scan_to_array(String file_name, Vector<String> data_set) {

        try {
            File myObj = new File(file_name);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data_set.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void read_file_scan_to_map(String file_name, Map<String, String> data_set) {

        try {
            File myObj = new File(file_name);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String next_line = myReader.nextLine();
                data_set.put(next_line, next_line);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static String read_file_txt(String file_name) {
        String data = "";
        try {
            data = new String(Files.readAllBytes(Paths.get(file_name)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static Map<Character, Character> build_map(Vector<String> read_key) {
        Map<Character, Character> map_key = new HashMap<Character, Character>();
        for (int i = 0; i < read_key.size(); i++) {
            map_key.put(read_key.elementAt(i).charAt(2), read_key.elementAt(i).charAt(0));
        }
        return map_key;
    }

    public static Vector<String> break_plain_text_vector(String plain_text, Vector<String> plain_text_blocks) {
        int plain_text_size = plain_text.length();
        int how_much_run = plain_text_size / 10 + 1;
        for (int i = 0; i < how_much_run; i++) // add filling last block
        {
            if (plain_text.length() > 10) {
                plain_text = break_the_text(plain_text, plain_text_blocks);
            } else {
                int how_much_char_to_add = 10 - plain_text.length();
                for (int j = 0; j < how_much_char_to_add; j++) {
                    plain_text += "\0";
                }
                plain_text = break_the_text(plain_text, plain_text_blocks);

            }
        }

        return plain_text_blocks;
    }

    public static String xoring_function(String plain_text, String init_vector) {
        String xor_plain_init = "";
        for (int i = 0; i < init_vector.length(); i++) {
            xor_plain_init += (char) ((int) plain_text.charAt(i) ^ (int) init_vector.charAt(i));
        }
        return xor_plain_init;
    }


    public static String cipher_text_generator(String plain_text, Map<Character, Character> map_key) {
        String cipher_text = "";
        String source = "abcdefgh";
        for (int i = 0; i < plain_text.length(); i++) {
            if (source.indexOf(plain_text.charAt(i)) != -1)
                cipher_text += map_key.get(plain_text.charAt(i));
            else
                cipher_text += plain_text.charAt(i);

        }
        return cipher_text;
    }

    public static String decrypt(String encrypted_text, String init_vector, Map<Character, Character> map_key) {
        String final_plain_text = "";
        Vector<String> cipher_text_blocks = new Vector<String>();
        break_plain_text_vector(encrypted_text, cipher_text_blocks);
        for (int i = 0; i < cipher_text_blocks.size(); i++) {
            String cipher_block = cipher_text_blocks.elementAt(i);
            String cipher_decrypted = cipher_text_generator(cipher_text_blocks.elementAt(i), map_key);
            String plain_text = xoring_function(cipher_decrypted, init_vector);
            init_vector = cipher_block;
            final_plain_text += plain_text;
        }

        return final_plain_text;
    }

    public static double compare_with_dictionary(String final_plain_text, Map<String, String> words) {
        double matches = 0;
        String[] split_text = final_plain_text.split("[ -.,():\n]");
        for (int i = 0; i < split_text.length; i++) {
            if (words.get(split_text[i]) != null) {
                matches++;
            }

        }
        return matches / split_text.length;
    }


    public static void run_generate_key() {
        try {
            Process p = Runtime.getRuntime().exec("cmd /c start D:\\Cyber_security\\Cyber_security_good\\KeyGenerator\\GenerateKey-Shortcut.lnk");

        } catch (IOException ex) {
            //Validate the case the file can't be accesed (not enought permissions)

        }
    }

    public static void shuffle_map(Map<Character, Character> map, Map<String, String> used_keys) {
        List<Character> valueList = new ArrayList<Character>(map.values());
        Collections.shuffle(valueList);
        if (used_keys.get(valueList.toString()) != null) {
            Collections.shuffle(valueList);
        }

        Iterator<Character> valueIt = valueList.iterator();
        for (Map.Entry<Character, Character> e : map.entrySet()) {
            e.setValue(valueIt.next());
        }
    }

    public static void write_to_txt_file(Map<Character, Character> final_key) {
        // new file object
        File file = new File("CipherText_Key.txt");
        BufferedWriter bf = null;
        try {
            // create new BufferedWriter for the output file
            bf = new BufferedWriter(new FileWriter(file));
            // iterate map entries
            for (Map.Entry<Character, Character> entry :
                    final_key.entrySet()) {
                // put key and value separated by a colon
                bf.write(entry.getKey() + " "
                        + entry.getValue());
                // new line
                bf.newLine();
            }
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // always close the writer
                bf.close();
            } catch (Exception e) {
            }
        }
    }
    public static void main(String[] args) {
        //run_generate_key();
        Map<String, String> words = new HashMap<String, String>();
        read_file_scan_to_map("Data_Set.txt", words);
        //System.out.println(words.elementAt(84098));
        Vector<String> read_key = new Vector<String>();
        read_file_scan_to_array("KeyGenerator/myKey.txt", read_key);
        double max_percentage = 0;
        double count = 0;
        Map<String, String> used_keys = new HashMap<String, String>();
        Map map_key = build_map(read_key);
        List<Character> map_values = new ArrayList(map_key.values());
        used_keys.put(map_values.toString(), map_values.toString());
        Map<Character, Character> best_key = new HashMap<Character, Character>(map_key);
        long start = System.nanoTime();
        while (count < factorialUsingForLoop(map_key.size()) && (System.nanoTime() - start) / 1000000000 <= 300) {
            String decrypted_text = decrypt(read_file_txt("CipherText_Example.txt"), read_file_txt("IV_Example.txt"), map_key);
            double percentage = compare_with_dictionary(decrypted_text.toLowerCase(), words);
            if (percentage > max_percentage) {
                max_percentage = percentage;
                best_key = new HashMap<Character, Character>(map_key);
                System.out.println("Switch ");
            }
            shuffle_map(map_key, used_keys);
            map_values = new ArrayList(map_key.values());
            used_keys.put(map_values.toString(), map_values.toString());
            count++;

        }
        System.out.println(max_percentage);
        List<Character> keys = new ArrayList(best_key.keySet());
        List<Character> values = new ArrayList(best_key.values());
        Map<Character, Character> final_key = new HashMap<Character, Character>();
        for (int i = 0; i < keys.size(); i++) {
            final_key.put(values.get(i), keys.get(i));
        }
        System.out.println("This is the Best key after change  ");
        System.out.println(final_key);
        System.out.println("i ran " + count + " times");
        write_to_txt_file(final_key);


    }
}
