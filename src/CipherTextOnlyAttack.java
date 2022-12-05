import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.*;
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


    public static String cipher_text_generator(String plain_text,Map<Character,Character>map_key)
    {
        String cipher_text = "";
        for (int i = 0; i < plain_text.length(); i++)
        {
            if (map_key.get(plain_text.charAt(i)) != null)
                cipher_text+=map_key.get(plain_text.charAt(i));
            else
                cipher_text+=plain_text.charAt(i);

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
        double missed = 0;
        String[] split_text = final_plain_text.split("[ -.,():\n]");
        for (int i = 0; i < split_text.length; i++) {
            if (words.get(split_text[i]) != null) {
                matches++;
            }
            else
                missed++;
            if(i>100 && missed/i>0.2) {
                break;
            }
        }
        return matches / split_text.length;
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
        String cipher_text_example = read_file_txt("CipherText_Example.txt");
        String iv_example = read_file_txt("IV_Example.txt");
        Map<String, String> words = new HashMap<String, String>();
        hard_coded_words(words);
        Vector<String> read_key = new Vector<String>();
        read_file_scan_to_array("KeyGenerator/myKey.txt", read_key); //please put path to
        double max_percentage = 0;
        double count = 0;
        Map<String, String> used_keys = new HashMap<String, String>();
        Map map_key = build_map(read_key);
        List<Character> map_values = new ArrayList(map_key.values());
        used_keys.put(map_values.toString(), map_values.toString());
        Map<Character, Character> best_key = new HashMap<Character, Character>(map_key);
        long start = System.nanoTime();
        String decrypted_text = "";
        while (count < factorialUsingForLoop(map_key.size()) && (System.nanoTime() - start) / 1000000000 <= 300) {
            decrypted_text = decrypt(cipher_text_example,iv_example , map_key);
            double percentage = compare_with_dictionary(decrypted_text.toLowerCase(), words);
            if (percentage > max_percentage) {
                max_percentage = percentage;
                best_key = new HashMap<Character, Character>(map_key);
            }
            shuffle_map(map_key, used_keys);
            map_values = new ArrayList(map_key.values());
            used_keys.put(map_values.toString(), map_values.toString());
            count++;
        }
        List<Character> keys = new ArrayList(best_key.keySet());
        List<Character> values = new ArrayList(best_key.values());
        Map<Character, Character> final_key = new HashMap<Character, Character>();
        for (int i = 0; i < keys.size(); i++) {
            final_key.put(values.get(i), keys.get(i));
        }
        System.out.println("This is the Best key after change  ");
        System.out.println(final_key);
        write_to_txt_file(final_key);

    }
    public static void hard_coded_words(Map<String, String> data_set)
    {
        data_set.put("the","the");
        data_set.put("of","of");
        data_set.put("and","and");
        data_set.put("that","that");
        data_set.put("was","was");
        data_set.put("his","his");
        data_set.put("he","he");
        data_set.put("with","with");
        data_set.put("for","for");
        data_set.put("as","as");
        data_set.put("had","had");
        data_set.put("be","be");
        data_set.put("her","her");
        data_set.put("at","at");
        data_set.put("by","by");
        data_set.put("which","which");
        data_set.put("have","have");
        data_set.put("from","from");
        data_set.put("this","this");
        data_set.put("him","him");
        data_set.put("but","but");
        data_set.put("all","all");
        data_set.put("she","she");
        data_set.put("they","they");
        data_set.put("were","were");
        data_set.put("are","are");
        data_set.put("me","me");
        data_set.put("one","one");
        data_set.put("their","their");
        data_set.put("an","an");
        data_set.put("said","said");
        data_set.put("them","them");
        data_set.put("we","we");
        data_set.put("who","who");
        data_set.put("would","would");
        data_set.put("been","been");
        data_set.put("when","when");
        data_set.put("there","there");
        data_set.put("if","if");
        data_set.put("more","more");
        data_set.put("do","do");
        data_set.put("any","any");
        data_set.put("what","what");
        data_set.put("has","has");
        data_set.put("man","man");
        data_set.put("could","could");
        data_set.put("other","other");
        data_set.put("than","than");
        data_set.put("some","some");
        data_set.put("very","very");
        data_set.put("time","time");
        data_set.put("about","about");
        data_set.put("may","may");
        data_set.put("like","like");
        data_set.put("little","little");
        data_set.put("then","then");
        data_set.put("can","can");
        data_set.put("should","should");
        data_set.put("made","made");
        data_set.put("did","did");
        data_set.put("such","such");
        data_set.put("a","a");
        data_set.put("great","great");
        data_set.put("before","before");
        data_set.put("these","these");
        data_set.put("see","see");
        data_set.put("over","over");
        data_set.put("much","much");
        data_set.put("down","down");
        data_set.put("after","after");
        data_set.put("first","first");
        data_set.put("good","good");
        data_set.put("men","men");
        data_set.put("never","never");
        data_set.put("old","old");
        data_set.put("shall","shall");
        data_set.put("day","day");
        data_set.put("where","where");
        data_set.put("those","those");
        data_set.put("came","came");
        data_set.put("come","come");
        data_set.put("himself","himself");
        data_set.put("way","way");
        data_set.put("life","life");
        data_set.put("without","without");
        data_set.put("go","go");
        data_set.put("make","make");
        data_set.put("well","well");
        data_set.put("through","through");
        data_set.put("being","being");
        data_set.put("long","long");
        data_set.put("say","say");
        data_set.put("might","might");
        data_set.put("how","how");
        data_set.put("am","am");
        data_set.put("even","even");
        data_set.put("def","def");
        data_set.put("again","again");
        data_set.put("many","many");
        data_set.put("back","back");
        data_set.put("here","here");
        data_set.put("think","think");
        data_set.put("every","every");
        data_set.put("people","people");
        data_set.put("went","went");
        data_set.put("same","same");
        data_set.put("last","last");
        data_set.put("thought","thought");
        data_set.put("away","away");
        data_set.put("under","under");
        data_set.put("take","take");
        data_set.put("found","found");
        data_set.put("hand","hand");
        data_set.put("eyes","eyes");
        data_set.put("place","place");
        data_set.put("while","while");
        data_set.put("also","also");
        data_set.put("young","young");
        data_set.put("yet","yet");
        data_set.put("though","though");
        data_set.put("against","against");
        data_set.put("things","things");
        data_set.put("get","get");
        data_set.put("ever","ever");
        data_set.put("give","give");
        data_set.put("god","god");
        data_set.put("years","years");
        data_set.put("off","off");
        data_set.put("face","face");
        data_set.put("nothing","nothing");
        data_set.put("right","right");
        data_set.put("once","once");
        data_set.put("another","another");
        data_set.put("left","left");
        data_set.put("part","part");
        data_set.put("saw","saw");
        data_set.put("house","house");
        data_set.put("world","world");
        data_set.put("head","head");
        data_set.put("three","three");
        data_set.put("new","new");
        data_set.put("love","love");
        data_set.put("always","always");
        data_set.put("night","night");
        data_set.put("each","each");
        data_set.put("king","king");
        data_set.put("between","between");
        data_set.put("tell","tell");
        data_set.put("mind","mind");
        data_set.put("heart","heart");
        data_set.put("few","few");
        data_set.put("because","because");
        data_set.put("thing","thing");
        data_set.put("whom","whom");
        data_set.put("far","far");
        data_set.put("seemed","seemed");
        data_set.put("looked","looked");
        data_set.put("called","called");
        data_set.put("whole","whole");
        data_set.put("de","de");
        data_set.put("set","set");
        data_set.put("both","both");
        data_set.put("got","got");
        data_set.put("find","find");
        data_set.put("done","done");
        data_set.put("heard","heard");
        data_set.put("name","name");
        data_set.put("days","days");
        data_set.put("told","told");
        data_set.put("let","let");
        data_set.put("lord","lord");
        data_set.put("country","country");
        data_set.put("asked","asked");
        data_set.put("going","going");
        data_set.put("seen","seen");
        data_set.put("better","better");
        data_set.put("having","having");
        data_set.put("home","home");
        data_set.put("knew","knew");
        data_set.put("side","side");
        data_set.put("something","something");
        data_set.put("moment","moment");
        data_set.put("father","father");
        data_set.put("among","among");
        data_set.put("course","course");
        data_set.put("hands","hands");
        data_set.put("woman","woman");
        data_set.put("enough","enough");
        data_set.put("words","words");
        data_set.put("mother","mother");
        data_set.put("full","full");
        data_set.put("end","end");
        data_set.put("gave","gave");
        data_set.put("almost","almost");
        data_set.put("small","small");
        data_set.put("thou","thou");
        data_set.put("cannot","cannot");
        data_set.put("water","water");
        data_set.put("want","want");
        data_set.put("however","however");
        data_set.put("light","light");
        data_set.put("quite","quite");
        data_set.put("brought","brought");
        data_set.put("word","word");
        data_set.put("whose","whose");
        data_set.put("given","given");
        data_set.put("door","door");
        data_set.put("best","best");
        data_set.put("turned","turned");
        data_set.put("taken","taken");
        data_set.put("does","does");
        data_set.put("use","use");
        data_set.put("morning","morning");
        data_set.put("myself","myself");
        data_set.put("Gutenberg","Gutenberg");
        data_set.put("felt","felt");
        data_set.put("since","since");
        data_set.put("power","power");
        data_set.put("themselves","themselves");
        data_set.put("used","used");
        data_set.put("rather","rather");
        data_set.put("began","began");
        data_set.put("present","present");
        data_set.put("voice","voice");
        data_set.put("others","others");
        data_set.put("white","white");
        data_set.put("less","less");
        data_set.put("money","money");
        data_set.put("next","next");
        data_set.put("death","death");
        data_set.put("stood","stood");
        data_set.put("form","form");
        data_set.put("within","within");
        data_set.put("together","together");
        data_set.put("thy","thy");
        data_set.put("large","large");
        data_set.put("matter","matter");
        data_set.put("kind","kind");
        data_set.put("often","often");
        data_set.put("certain","certain");
        data_set.put("herself","herself");
        data_set.put("year","year");
        data_set.put("friend","friend");
        data_set.put("half","half");
        data_set.put("order","order");
        data_set.put("round","round");
        data_set.put("TRUE","TRUE");
        data_set.put("anything","anything");
        data_set.put("keep","keep");
        data_set.put("sent","sent");
        data_set.put("wife","wife");
        data_set.put("means","means");
        data_set.put("believe","believe");
        data_set.put("passed","passed");
        data_set.put("feet","feet");
        data_set.put("near","near");
        data_set.put("public","public");
        data_set.put("state","state");
        data_set.put("hundred","hundred");
        data_set.put("children","children");
        data_set.put("thus","thus");
        data_set.put("hope","hope");
        data_set.put("alone","alone");
        data_set.put("above","above");
        data_set.put("case","case");
        data_set.put("dear","dear");
        data_set.put("thee","thee");
        data_set.put("says","says");
        data_set.put("person","person");
        data_set.put("high","high");
        data_set.put("read","read");
        data_set.put("city","city");
        data_set.put("already","already");
        data_set.put("received","received");
        data_set.put("fact","fact");
        data_set.put("gone","gone");
        data_set.put("girl","girl");
        data_set.put("hear","hear");
        data_set.put("times","times");
        data_set.put("least","least");
        data_set.put("perhaps","perhaps");
        data_set.put("sure","sure");
        data_set.put("indeed","indeed");
        data_set.put("english","english");
        data_set.put("open","open");
        data_set.put("body","body");
        data_set.put("itself","itself");
        data_set.put("along","along");
        data_set.put("land","land");
        data_set.put("return","return");
        data_set.put("leave","leave");
        data_set.put("air","air");
        data_set.put("nature","nature");
        data_set.put("answered","answered");
        data_set.put("either","either");
        data_set.put("law","law");
        data_set.put("help","help");
        data_set.put("lay","lay");
        data_set.put("child","child");
        data_set.put("letter","letter");
        data_set.put("four","four");
        data_set.put("wish","wish");
        data_set.put("fire","fire");
        data_set.put("cried","cried");
        data_set.put("women","women");
        data_set.put("speak","speak");
        data_set.put("number","number");
        data_set.put("therefore","therefore");
        data_set.put("hour","hour");
        data_set.put("friends","friends");
        data_set.put("held","held");
        data_set.put("free","free");
        data_set.put("war","war");
        data_set.put("during","during");
        data_set.put("several","several");
        data_set.put("business","business");
        data_set.put("whether","whether");
        data_set.put("er","er");
        data_set.put("manner","manner");
        data_set.put("second","second");
        data_set.put("reason","reason");
        data_set.put("replied","replied");
        data_set.put("united","united");
        data_set.put("call","call");
        data_set.put("general","general");
        data_set.put("why","why");
        data_set.put("behind","behind");
        data_set.put("became","became");
        data_set.put("john","john");
        data_set.put("become","become");
        data_set.put("dead","dead");
        data_set.put("earth","earth");
        data_set.put("boy","boy");
        data_set.put("forth","forth");
        data_set.put("thousand","thousand");
        data_set.put("looking","looking");
        data_set.put("family","family");
        data_set.put("feel","feel");
        data_set.put("coming","coming");
        data_set.put("England","England");
        data_set.put("question","question");
        data_set.put("care","care");
        data_set.put("truth","truth");
        data_set.put("ground","ground");
        data_set.put("really","really");
        data_set.put("rest","rest");
        data_set.put("mean","mean");
        data_set.put("different","different");
        data_set.put("making","making");
        data_set.put("possible","possible");
        data_set.put("fell","fell");
        data_set.put("towards","towards");
        data_set.put("human","human");
        data_set.put("kept","kept");
        data_set.put("short","short");
        data_set.put("following","following");
        data_set.put("need","need");
        data_set.put("cause","cause");
        data_set.put("met","met");
        data_set.put("evening","evening");
        data_set.put("returned","returned");
        data_set.put("five","five");
        data_set.put("strong","strong");
        data_set.put("able","able");
        data_set.put("french","french");
        data_set.put("live","live");
        data_set.put("lady","lady");
        data_set.put("subject","subject");
        data_set.put("answer","answer");
        data_set.put("sea","sea");
        data_set.put("fear","fear");
        data_set.put("understand","understand");
        data_set.put("hard","hard");
        data_set.put("terms","terms");
        data_set.put("doubt","doubt");
        data_set.put("around","around");
        data_set.put("ask","ask");
        data_set.put("arms","arms");
        data_set.put("sense","sense");
        data_set.put("seems","seems");
        data_set.put("black","black");
        data_set.put("bring","bring");
        data_set.put("followed","followed");
        data_set.put("beautiful","beautiful");
        data_set.put("close","close");
        data_set.put("dark","dark");
        data_set.put("hold","hold");
        data_set.put("character","character");
        data_set.put("sight","sight");
        data_set.put("ten","ten");
        data_set.put("show","show");
        data_set.put("party","party");
        data_set.put("fine","fine");
        data_set.put("ye","ye");
        data_set.put("ready","ready");
        data_set.put("common","common");
        data_set.put("book","book");
        data_set.put("electronic","electronic");
        data_set.put("talk","talk");
        data_set.put("account","account");
        data_set.put("mark","mark");
        data_set.put("interest","interest");
        data_set.put("written","written");
        data_set.put("bed","bed");
        data_set.put("necessary","necessary");
        data_set.put("age","age");
        data_set.put("else","else");
        data_set.put("force","force");
        data_set.put("idea","idea");
        data_set.put("longer","longer");
        data_set.put("art","art");
        data_set.put("spoke","spoke");
        data_set.put("across","across");
        data_set.put("brother","brother");
        data_set.put("early","early");
        data_set.put("ought","ought");
        data_set.put("sometimes","sometimes");
        data_set.put("line","line");
        data_set.put("saying","saying");
        data_set.put("table","table");
        data_set.put("appeared","appeared");
        data_set.put("river","river");
        data_set.put("continued","continued");
        data_set.put("eye","eye");
        data_set.put("ety","ety");
        data_set.put("information","information");
        data_set.put("later","later");
        data_set.put("everything","everything");
        data_set.put("reached","reached");
        data_set.put("suddenly","suddenly");
        data_set.put("past","past");
        data_set.put("hours","hours");
        data_set.put("strange","strange");
        data_set.put("deep","deep");
        data_set.put("change","change");
        data_set.put("miles","miles");
        data_set.put("feeling","feeling");
        data_set.put("act","act");
        data_set.put("meet","meet");
        data_set.put("paid","paid");
        data_set.put("further","further");
        data_set.put("purpose","purpose");
        data_set.put("happy","happy");
        data_set.put("added","added");
        data_set.put("seem","seem");
        data_set.put("taking","taking");
        data_set.put("blood","blood");
        data_set.put("rose","rose");
        data_set.put("south","south");
        data_set.put("beyond","beyond");
        data_set.put("cold","cold");
        data_set.put("neither","neither");
        data_set.put("forward","forward");
        data_set.put("view","view");
        data_set.put("sound","sound");
        data_set.put("none","none");
        data_set.put("entered","entered");
        data_set.put("clear","clear");
        data_set.put("road","road");
        data_set.put("late","late");
        data_set.put("stand","stand");
        data_set.put("suppose","suppose");
        data_set.put("la","la");
        data_set.put("daughter","daughter");
        data_set.put("real","real");
        data_set.put("nearly","nearly");
        data_set.put("mine","mine");
        data_set.put("laws","laws");
        data_set.put("knowledge","knowledge");
        data_set.put("comes","comes");
        data_set.put("toward","toward");
        data_set.put("bad","bad");
        data_set.put("cut","cut");
        data_set.put("copy","copy");
        data_set.put("husband","husband");
        data_set.put("France","France");
        data_set.put("living","living");
        data_set.put("peace","peace");
        data_set.put("north","north");
        data_set.put("remember","remember");
        data_set.put("effect","effect");
        data_set.put("natural","natural");
        data_set.put("pretty","pretty");
        data_set.put("fall","fall");
        data_set.put("fair","fair");
        data_set.put("service","service");
        data_set.put("below","below");
        data_set.put("except","except");
        data_set.put("American","American");
        data_set.put("hair","hair");
        data_set.put("London","London");
        data_set.put("laid","laid");
        data_set.put("pass","pass");
        data_set.put("led","led");
        data_set.put("copyright","copyright");
        data_set.put("doing","doing");
        data_set.put("army","army");
        data_set.put("horse","horse");
        data_set.put("future","future");
        data_set.put("opened","opened");
        data_set.put("pleasure","pleasure");
        data_set.put("history","history");
        data_set.put("west","west");
        data_set.put("pay","pay");
        data_set.put("red","red");
        data_set.put("hath","hath");
        data_set.put("note","note");
        data_set.put("although","although");
        data_set.put("wanted","wanted");
        data_set.put("gold","gold");
        data_set.put("makes","makes");
        data_set.put("desire","desire");
        data_set.put("play","play");
        data_set.put("master","master");
        data_set.put("office","office");
        data_set.put("tried","tried");
        data_set.put("front","front");
        data_set.put("big","big");
        data_set.put("Dr","Dr");
        data_set.put("lived","lived");
        data_set.put("certainly","certainly");
        data_set.put("wind","wind");
        data_set.put("receive","receive");
        data_set.put("attention","attention");
        data_set.put("government","government");
        data_set.put("church","church");
        data_set.put("strength","strength");
        data_set.put("length","length");
        data_set.put("company","company");
        data_set.put("placed","placed");
        data_set.put("paper","paper");
        data_set.put("letters","letters");
        data_set.put("probably","probably");
        data_set.put("glad","glad");
        data_set.put("important","important");
        data_set.put("especially","especially");
        data_set.put("greater","greater");
        data_set.put("yourself","yourself");
        data_set.put("fellow","fellow");
        data_set.put("bear","bear");
        data_set.put("window","window");
        data_set.put("ran","ran");
        data_set.put("faith","faith");
        data_set.put("ago","ago");
        data_set.put("agreement","agreement");
        data_set.put("charge","charge");
        data_set.put("beauty","beauty");
        data_set.put("remained","remained");
        data_set.put("arm","arm");
        data_set.put("latter","latter");
        data_set.put("duty","duty");
        data_set.put("send","send");
        data_set.put("distance","distance");
        data_set.put("silence","silence");
        data_set.put("foot","foot");
        data_set.put("wild","wild");
        data_set.put("object","object");
        data_set.put("die","die");
        data_set.put("save","save");
        data_set.put("gentleman","gentleman");
        data_set.put("trees","trees");
        data_set.put("green","green");
        data_set.put("trouble","trouble");
        data_set.put("smile","smile");
        data_set.put("books","books");
        data_set.put("wrong","wrong");
        data_set.put("various","various");
        data_set.put("sleep","sleep");
        data_set.put("persons","persons");
        data_set.put("blockquote","blockquote");
        data_set.put("happened","happened");
        data_set.put("particular","particular");
        data_set.put("drew","drew");
        data_set.put("minutes","minutes");
        data_set.put("hardly","hardly");
        data_set.put("walked","walked");
        data_set.put("chief","chief");
        data_set.put("chance","chance");
        data_set.put("according","according");
        data_set.put("beginning","beginning");
        data_set.put("action","action");
        data_set.put("deal","deal");
        data_set.put("loved","loved");
        data_set.put("thinking","thinking");
        data_set.put("follow","follow");
        data_set.put("standing","standing");
        data_set.put("presence","presence");
        data_set.put("heavy","heavy");
        data_set.put("sweet","sweet");
        data_set.put("plain","plain");
        data_set.put("donations","donations");
        data_set.put("immediately","immediately");
        data_set.put("wrote","wrote");
        data_set.put("mouth","mouth");
        data_set.put("rich","rich");
        data_set.put("thoughts","thoughts");
        data_set.put("months","months");
        data_set.put("afraid","afraid");
        data_set.put("Paris","Paris");
        data_set.put("single","single");
        data_set.put("enemy","enemy");
        data_set.put("broken","broken");
        data_set.put("unless","unless");
        data_set.put("states","states");
        data_set.put("ship","ship");
        data_set.put("condition","condition");
        data_set.put("carry","carry");
        data_set.put("exclaimed","exclaimed");
        data_set.put("including","including");
        data_set.put("filled","filled");
        data_set.put("seeing","seeing");
        data_set.put("influence","influence");
        data_set.put("write","write");
        data_set.put("boys","boys");
        data_set.put("appear","appear");
        data_set.put("outside","outside");
        data_set.put("secret","secret");
        data_set.put("parts","parts");
        data_set.put("please","please");
        data_set.put("appearance","appearance");
        data_set.put("evil","evil");
        data_set.put("march","march");
        data_set.put("George","George");
        data_set.put("whatever","whatever");
        data_set.put("tears","tears");
        data_set.put("horses","horses");
        data_set.put("places","places");
        data_set.put("caught","caught");
        data_set.put("stay","stay");
        data_set.put("instead","instead");
        data_set.put("struck","struck");
        data_set.put("blue","blue");
        data_set.put("impossible","impossible");
        data_set.put("period","period");
        data_set.put("sister","sister");
        data_set.put("battle","battle");
        data_set.put("school","school");
        data_set.put("Mary","Mary");
        data_set.put("raised","raised");
        data_set.put("occasion","occasion");
        data_set.put("married","married");
        data_set.put("former","former");
        data_set.put("food","food");
        data_set.put("youth","youth");
        data_set.put("learned","learned");
        data_set.put("merely","merely");
        data_set.put("reach","reach");
        data_set.put("system","system");
        data_set.put("twenty","twenty");
        data_set.put("dinner","dinner");
        data_set.put("quiet","quiet");
        data_set.put("easily","easily");
        data_set.put("moved","moved");
        data_set.put("afterwards","afterwards");
        data_set.put("giving","giving");
        data_set.put("walk","walk");
        data_set.put("stopped","stopped");
        data_set.put("laughed","laughed");
        data_set.put("language","language");
        data_set.put("expression","expression");
        data_set.put("week","week");
        data_set.put("hall","hall");
        data_set.put("danger","danger");
        data_set.put("property","property");
        data_set.put("wonder","wonder");
        data_set.put("usual","usual");
        data_set.put("figure","figure");
        data_set.put("born","born");
        data_set.put("court","court");
        data_set.put("generally","generally");
        data_set.put("grew","grew");
        data_set.put("showed","showed");
        data_set.put("getting","getting");
        data_set.put("ancient","ancient");
        data_set.put("respect","respect");
        data_set.put("third","third");
        data_set.put("worth","worth");
        data_set.put("simple","simple");
        data_set.put("tree","tree");
        data_set.put("leaving","leaving");
        data_set.put("remain","remain");
        data_set.put("society","society");
        data_set.put("fight","fight");
        data_set.put("wall","wall");
        data_set.put("result","result");
        data_set.put("heaven","heaven");
        data_set.put("William","William");
        data_set.put("started","started");
        data_set.put("command","command");
        data_set.put("tone","tone");
        data_set.put("regard","regard");
        data_set.put("expected","expected");
        data_set.put("mere","mere");
        data_set.put("month","month");
        data_set.put("beside","beside");
        data_set.put("silent","silent");
        data_set.put("perfect","perfect");
        data_set.put("experience","experience");
        data_set.put("street","street");
        data_set.put("writing","writing");
        data_set.put("goes","goes");
        data_set.put("circumstances","circumstances");
        data_set.put("entirely","entirely");
        data_set.put("fresh","fresh");
        data_set.put("duke","duke");
        data_set.put("covered","covered");
        data_set.put("bound","bound");
        data_set.put("east","east");
        data_set.put("wood","wood");
        data_set.put("stone","stone");
        data_set.put("quickly","quickly");
        data_set.put("notice","notice");
        data_set.put("bright","bright");
        data_set.put("Christ","Christ");
        data_set.put("boat","boat");
        data_set.put("noble","noble");
        data_set.put("meant","meant");
        data_set.put("somewhat","somewhat");
        data_set.put("sudden","sudden");
        data_set.put("value","value");
        data_set.put("c.","c.");
        data_set.put("direction","direction");
        data_set.put("chair","chair");
        data_set.put("due","due");
        data_set.put("date","date");
        data_set.put("waiting","waiting");
        data_set.put("Christian","Christian");
        data_set.put("village","village");
        data_set.put("lives","lives");
        data_set.put("reading","reading");
        data_set.put("agree","agree");
        data_set.put("lines","lines");
        data_set.put("considered","considered");
        data_set.put("field","field");
        data_set.put("observed","observed");
        data_set.put("scarcely","scarcely");
        data_set.put("wished","wished");
        data_set.put("wait","wait");
        data_set.put("greatest","greatest");
        data_set.put("permission","permission");
        data_set.put("success","success");
        data_set.put("piece","piece");
        data_set.put("British","British");
        data_set.put("ex","ex");
        data_set.put("Charles","Charles");
        data_set.put("formed","formed");
        data_set.put("speaking","speaking");
        data_set.put("trying","trying");
        data_set.put("conversation","conversation");
        data_set.put("proper","proper");
        data_set.put("hill","hill");
        data_set.put("music","music");
        data_set.put("German","German");
        data_set.put("afternoon","afternoon");
        data_set.put("cry","cry");
        data_set.put("cost","cost");
        data_set.put("allowed","allowed");
        data_set.put("girls","girls");
        data_set.put("considerable","considerable");
        data_set.put("c","c");
        data_set.put("broke","broke");
        data_set.put("honour","honour");
        data_set.put("seven","seven");
        data_set.put("private","private");
        data_set.put("news","news");
        data_set.put("scene","scene");
        data_set.put("discovered","discovered");
        data_set.put("marriage","marriage");
        data_set.put("step","step");
        data_set.put("garden","garden");
        data_set.put("race","race");
        data_set.put("begin","begin");
        data_set.put("per","per");
        data_set.put("individual","individual");
        data_set.put("sitting","sitting");
        data_set.put("learn","learn");
        data_set.put("political","political");
        data_set.put("difficult","difficult");
        data_set.put("bit","bit");
        data_set.put("speech","speech");
        data_set.put("Henry","Henry");
        data_set.put("lie","lie");
        data_set.put("cast","cast");
        data_set.put("eat","eat");
        data_set.put("authority","authority");
        data_set.put("etc.","etc.");
        data_set.put("floor","floor");
        data_set.put("ways","ways");
        data_set.put("officers","officers");
        data_set.put("offered","offered");
        data_set.put("original","original");
        data_set.put("happiness","happiness");
        data_set.put("flowers","flowers");
        data_set.put("produced","produced");
        data_set.put("summer","summer");
        data_set.put("provide","provide");
        data_set.put("study","study");
        data_set.put("religion","religion");
        data_set.put("picture","picture");
        data_set.put("walls","walls");
        data_set.put("personal","personal");
        data_set.put("America","America");
        data_set.put("watch","watch");
        data_set.put("pleased","pleased");
        data_set.put("leaves","leaves");
        data_set.put("declared","declared");
        data_set.put("hot","hot");
        data_set.put("understood","understood");
        data_set.put("effort","effort");
        data_set.put("prepared","prepared");
        data_set.put("escape","escape");
        data_set.put("attempt","attempt");
        data_set.put("supposed","supposed");
        data_set.put("killed","killed");
        data_set.put("fast","fast");
        data_set.put("author","author");
        data_set.put("Indian","Indian");
        data_set.put("brown","brown");
        data_set.put("determined","determined");
        data_set.put("pain","pain");
        data_set.put("spring","spring");
        data_set.put("takes","takes");
        data_set.put("drawn","drawn");
        data_set.put("soldiers","soldiers");
        data_set.put("houses","houses");
        data_set.put("beneath","beneath");
        data_set.put("talking","talking");
        data_set.put("turning","turning");
        data_set.put("century","century");
        data_set.put("steps","steps");
        data_set.put("intended","intended");
        data_set.put("soft","soft");
        data_set.put("straight","straight");
        data_set.put("matters","matters");
        data_set.put("likely","likely");
        data_set.put("corner","corner");
        data_set.put("trademark","trademark");
        data_set.put("justice","justice");
        data_set.put("produce","produce");
        data_set.put("appears","appears");
        data_set.put("Rome","Rome");
        data_set.put("laugh","laugh");
        data_set.put("forget","forget");
        data_set.put("Europe","Europe");
        data_set.put("passage","passage");
        data_set.put("eight","eight");
        data_set.put("closed","closed");
        data_set.put("ourselves","ourselves");
        data_set.put("gives","gives");
        data_set.put("dress","dress");
        data_set.put("passing","passing");
        data_set.put("terrible","terrible");
        data_set.put("required","required");
        data_set.put("medium","medium");
        data_set.put("efforts","efforts");
        data_set.put("sake","sake");
        data_set.put("breath","breath");
        data_set.put("wise","wise");
        data_set.put("ladies","ladies");
        data_set.put("possession","possession");
        data_set.put("pleasant","pleasant");
        data_set.put("perfectly","perfectly");
        data_set.put("memory","memory");
        data_set.put("usually","usually");
        data_set.put("grave","grave");
        data_set.put("fixed","fixed");
        data_set.put("modern","modern");
        data_set.put("rise","rise");
        data_set.put("break","break");
        data_set.put("fifty","fifty");
        data_set.put("island","island");
        data_set.put("meeting","meeting");
        data_set.put("camp","camp");
        data_set.put("nation","nation");
        data_set.put("existence","existence");
        data_set.put("reply","reply");
        data_set.put("copies","copies");
        data_set.put("touch","touch");
        data_set.put("equal","equal");
        data_set.put("fortune","fortune");
        data_set.put("shore","shore");
        data_set.put("domain","domain");
        data_set.put("named","named");
        data_set.put("situation","situation");
        data_set.put("promise","promise");
        data_set.put("orders","orders");
        data_set.put("degree","degree");
    }
}
