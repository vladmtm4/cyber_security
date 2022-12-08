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

    /*
    public static Map<Character, Character> build_map(Vector<String> read_key) {
        Map<Character, Character> map_key = new HashMap<Character, Character>();
        for (int i = 0; i < read_key.size(); i++) {
            map_key.put(read_key.elementAt(i).charAt(2), read_key.elementAt(i).charAt(0));
        }
        return map_key;
    }

     */

    public static void build_map(Map<Character, Character>init_key) {
        init_key.put('a','a');
        init_key.put('b','b');
        init_key.put('c','c');
        init_key.put('d','d');
        init_key.put('e','e');
        init_key.put('f','f');
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
        String cipher_text_example = "=\n" +
                "U\u0012\u0000\u0004T\fT\u0013Ob2zt$7e8wo\u0006S\u0003TMYEy\u0007\u001Do?/t,7!Ysu\n" +
                "\u001FL\u0018CTJ*S\u0002om)80 8C8k\u0001\n" +
                "#LXIJ7]\u000Eo$\u0003\u001B1'9C2`OwnrEO\u0015eZ\t<W\n" +
                "\u001A,!5\u000B/sF;h~\fH[\u007F@S.R\u001Btn:>\u001E3'\u000E;uT\u000FT\u001E{UAdI\u0001t{;>\u001E&\"\u00059bT\u000FS[>PKi\\B#c=?\u0012p8\u000552S\u0006Y\u001Fe\u0005Qc^^*\f-w\u0017j$\u00046~^bHWp\u0006EwE^:\n" +
                "'%\u0003&*\u0011a\bSnSJq_\n" +
                "\\\u0000c \u0007<$\u0002s*(h\fU`T\u0004l\u001C^\"\u0019y<\u0003?h\u0015<;Lv\f[k\u001F\u001Cz\u001CK>\u0013z>\u0005k<\u001B<8Iz\bR%\u0004Z;[J \u000E|+\u0005`/H/jF|\u0013F%\u0005A<J\u0018/\u0012tLDi.R-8X{\u0000$b\u0001G?\u00032\f\u0013a\u0004\n" +
                "`+StSu3\u0012io\f_s\u001B5UQ}\u0000\u0003i;SxT73\u001CgcIZ=\u001CtX_xG\u0011(=\u001Dq\u0015,,VgP\\7r\u001Fp\fI8\u0003p3QRv\u0004,(\u0018`\u001F_>'\u0004dH\bh\u000Fl+[U(D<g\u0007/\u0000J)2MbZ\bu\u000Fi$M]\"\u0010zl\u001C|\u0019H,$\u000E\u001A\u0012\n" +
                "x\\{-IJ.nsn\u00139\u001F\n" +
                "=%\u000E\u001A\u001B\u000B3N~dQ\u000B.So+W+\u000E\n" +
                "2\u007FK7OX>F~dK_*Yo=P)\f\t$*YS\t\\3L )IE+6)([-N\t(aFS]Z>\n" +
                "9`L\u0004|s)2[-_\u0001/d\\\u001CO\u0012:\n" +
                "2`AD3zEsXfG\u0014d\"\\\b1\n" +
                "u\u0000.f\u0001\u000E|\u007FX~\u001D OFik\u001D\t!^\u007FL.%\u0002KpcT-\u000B-MMgk\u0011\n" +
                "0\n" +
                "yX**\u0002\u000F}t:a\u00186NYmf\u0018T\\\u0004yB;+\b\u00156t\u000Bm\u00171ODc5[\u0015o\b7W 6CA3pO{C6ID0o\u00139;[4W:N^\u00003LH>\u0014#H77nTl<Vq\u0003$^Q\u001AzLy \u0014m\u0004?%:\u000E$\u001C\u0000v\bwK\u0005Uh\u0004hi\u001Bm\u0004kl!Hs\t\u001A;\u001Ed\u0007\bN%y~uIu\n" +
                "iof\u0005\u0018\u0010\u0011i\u0014yI\u001F\u0014`ku\u007F\u001D4\n" +
                "!z4\u0005\u0007\u0010\u001CiFbBZWpufy\u00072B5;$P\u0016\u0013\n" +
                "\n" +
                "]$S\u001B@%bzej}@2f,L\u0005\u0012\u0011J\u0015/G\u0014_f%[ajfN44/\u0007W/A\u0005\u0000n@\\J'2L.ko\u00039|.U[:Kd\u0006m\u0019\f\\0+[9\u0005r\u0004vf|VD)\u0019M\u0013p\u0013F+3!B7mG\u0018vcM_@6\u0017\u001A&kV\u0010()%X7|J\u00021x\\Z\u0005-GP@e_\u001C|\n" +
                "lC4$/\u000Bs<\u000Ba\u0003eCE\\+\u0007Tf\u0013w\u001Cn+5Eft\u0007}\u0013<\u0006JQa\u0007Tq\u001CdUa%\"\u0004'!\u001D\u007F\u0001'kD@kQD=\u0017hTK6)\f90\u001Dv\u0006?'S\u0005,NUs\u0002&LK<r@7ySpC??U\u001C'\u0017\n" +
                "6\u0006&M^9<S~gSu\u0006\"03H;\u001BG$\u0014\u007F\f\u0010|&\u001B~&G|_`q\u0012Br\u0010Ak\\0\u0010\u0001}1\u001Bb$K(Xu!\u0011X}\u0016\t8@9\u0013U=x\t~l\u00180V`!X\n" +
                "\u0003\t\u0005lXv\u0014I=*cgj\u001E5\u0019d:\u001DL\u0002\u0004\u000F>Rx\u001E_ylbv`Sr\fv:Y\u001B\u0003\u001A\f}RE\u0002\u001A.zp:e\u00137am|$\u000E\u0018U\u0016v\u0017\u0011\u0004\u001FP{j0aV`ym|8[\u000BB\u0004v\u0013\u0016M\u001FW5\u007F0m\u0000vrmk?T\u000B\u0010\u0019h\u0013R\b\u0012Z'+vv\u0004\u007F=\u007F\u0018#H^V\u0017c\u0010H\u000B8T ;87\u001A\u007F=+U;V^\u0016\u0017X6z\u000B\u0017i\u0019\n" +
                "^R\n" +
                "\u00163X7>X^\u001D\u001ACXtRnq\n" +
                "r=n+=T1\u000F\u0001y\u001BR\u0000\u000B_1_j`\n" +
                "sri\u007F\u007FC>\u0004N\u0007:\u001C\u001A\u0016\u001B&\u001Ep&f\u001Azvwo\u0006\u007FP@\u0014o\u0013\u0002\u000EOp\u00109#qObe}o\u0002uXG\u0018!\u0005E\u0012\u001AvU9gtHv12u\u0010u_\u000E\u0013yO\t\u0006\u007Fa\u0007:}3\u000E'`a\u0017Ao[\u0019\u0013}H\n" +
                "\u0004e)\u00065~3\t'-`\n" +
                "\tq\\\n" +
                "[)SE\u0005*y\u00033n.J',jDYlUb^#@\u0001\u000366\u0002{B\n" +
                "K%!uY_d\u001Efi*HDU?-\u000BsF\bDh+7SBa\u0014ca!\u001CJ[s2\n" +
                "u\u0012\u0014D<&2\u0018W\u0007\u00142p1PJW|wj}@\u0002^\"j \u0014\u001E\t\u0015`b1P\u0007Ep>yt\u0012\u0016\u0011?da\u0004V\u001CTdeeY\u0005\u0002dvs2D\u0011\n" +
                "<\u000Fp\n" +
                "\u0011\u001BFiylRkPzpw*GY;;\u0005#\u000E\u001F\u0019\n" +
                "3,IU`G.~9yDE=6\bgO\u0010]Y0-X\u0016~\b&s8SCL6}^{I\u001E]$+-BQ~\u000F!q(CC\n" +
                "69\u001B/V\u001EZ'0-A\\iJvm.N\\Ad8\u00009\u0002\u0004@))(\u0017Pd[ndn#}@rp\n" +
                "5\u001D\u0010\u001CV\u0010%\u001C\u0004-\u001Dixyvb@pd^~\u001B\u001D\u001C\u0018N`\u0019\u0010~\tzn<{/\fuu\u001A Z\n" +
                "S\u000EChU\u0017\u007F*>b>e&\fy7\u001D_JBJ\n" +
                "C~\u001C\u0017j>9f$fe\t}nJQ_FW\u000E\u0010}\t\u0007$6\u007F/#.\u007F\u001Bo'GY\u0012_OK\u000B~\u0003^iSZ:o&b\bc:I<,_\u001D\u0006\u0016gCN!Y\f(thr\b4t\u00018,[\u0019\t\u001Eb]\u0007i\u0014\f=khw\u000E}d\u0000syO\u000ED}z\u0015\u0001 \u001E\u001C.i6\u0018\u0014prS>sHI^qgP\u0010<Z\n" +
                "h$;\u0003\u0002<i\u001C?g\u0018LZpkF\fx\u001F\u0005dl.\u0018\u000Ecn\u0014j`n\u0003X}|\u0002\u0002x\u0019@\u0019k1\u001E\u0014\"u\u001Dk%9\u001FY{4W\u001Bt\n" +
                "JKry\u0014Rwo\u001Chj;\u0013\u000B`+YOT\u0001\u0019\u001B{j\tYy85r\u0013m\u001E\u0018py\u001FY\\\u0000?Mvq\u0003Yy8?a\u001F#\u0017\u0005v+\u0018TS\u0018?Pvk\u0011^q:64\u001F8\u001F\u00181-\u001ASX\u0014mWj\u007FYHt6<4\u000F.`\u001C6)\u0006EY\u0014|A\u0001l\u0016Hh!yv\u00104o\u00186:\t[\u0016\u00040V\u0003yR_z{wjTvw\u00117\u007F\u0019\u0014\u001B\u000Et\u0019\u00111C\u0017|4lg\u001AmtCIe\u0014U\u0018Gr\f\u0010e#\u0010g!8\"\u001Chu\u0001\n" +
                "\u001A(TLQu\f\u0010--\u007F^1\"q\u0001bfBX\u00186\u0011V\u0019bB\u0015*-l\u0016c?w\u0006-f\u0007]\n" +
                "x\u0006\u0013WrE\u0003'*f\n" +
                "jw]\u001E*lLO\u0002*\t\u00181z\u0004L\b uD)q_Zp$m\u0000\u00060[\u0014:.PH\u0004tr\\>4_J4!d\u0007R3X\u0014(#ZEDp7A=\u001E_K37(\u0019Y&\u001Dz*8G\u0017Iw=\u0006i\u0015XVgg(\u0007XtI|6\"\bG[w1\u0006(\u0010E\u000E(&5\u0013\u0011r@\u007F0i@\u0006A{tx3\n" +
                "^I7g2[\u0007\u0010Zb7'PGS5e0.\n" +
                "R\u0007#,*\u0015\u0004\u0010Fk tK\fHyqujKT\u001C.~-Y\u0002\u0010\u000F&1x$\n" +
                "By`u/H^XGa.\u0016\u0015\u0007\u000F!0x&\u000FWf}ndF\u001CXCw4\u0003\n" +
                "\u001AD2t=e\u0007[py\u007F6ATI\n" +
                "c/P\u000E\u001AD$^9d\u0002\\$kh!@~\\\u0012g.]\u001C\u0000D2\u001Br23F8<f(Sx\u0019_\\3KH\u0007K;\u0011v⁆8\u0013-)b.\u001Bv\u0017‼]w'M\n" +
                "YuVq⁎2\u001A\u0007({<\u0007/Q\u202D]wjI\u0015XnA6\u200D>\u0018\u0018'p*@df\u2065[j}\u0007\u0007K3D\t\u200B>J\u0012i\n" +
                "?[!)\u2063Q?d\f Y)NG‗qV\tdE=@/3\u2072\u001D/)\u000B5M/\\Z x\u0001\tI|\n" +
                "\u000F\u001E\b⁉,IL\u001B\\C\\\u0014_ x\n" +
                "\u0004R\u0012\u0004|M\u0010⁝T*p:w$\u001F,`\u2029=E\u001E\u001A\u0004EvHL J-wvda\u0002 )\u2029.L\u0005\u001DD\u0004{EZ‣B#jv!`[!?⁆2\u0003\u0003\u0018U\u000F{vV\u2028Awlv⁌|[\u0019!⁆oW(\u0019※\u0012{xU\u2066\u001C#Z|⁞c[\u00140‐yOz\u001D‰\f/|U\u2062s?\u0015n⁄i]Pu‖\u001CM{N‥\u001D}?\u001B\u2073<.\u0014<⁋x\u000F\u0013;―PObL\u202E\u001C/uR\u20616:\u000E ⁗<F\u001Br―^_.W‾R\"7x⁴2+K%⁐3VR\u0014\u200D\u0012H$S‵A?<s\u202Ds&@s⁀/\\S\u0005⁈\u0001O.\u0014\u2060[46%※h!Ix {CYW Hh\u0007?⁖4\u0000w]‖&HsW″\u0014c\u0016/‶B!\u0000#⁒z\u0000s\u000F⁗fIaO※\u0019o\u0003{′\u0014i\u0012$⁒t\u0002c\u001F‒p\u0006aJ⁘\u0016g\u0017h⁷\u0015hA>‰sGa\u0007‘s\u001Bm\u001E⁘\u001C1\u0004u⁽\u0017;\u000Bq\u202A<PjU—yH\u007F\u0010⁄Hp\u0006<ⁿ\u001Ch\u001E0…$\u0005e^‐h\u001CrU .b\u000B:‰\f}\u0000!\u206FJBjM⁑u]dF\u200E#,J:‸\u00015D'\u202E@Y8L⁑oRbA⁂)>P8ⁿO\u001B\u0016d‵HMpL‗*;c\u000B⁙!.\u0015F\u2067KO\u0011b‵\n" +
                "\u000Ec( $?x\f⁒-g\n" +
                "\\\u2067\u0004O\u001De•A\u0002—/⁇s&s\u0001⁍6q›\u000F–\u001BCSq\u202CB\u0003⁕e\u2060;':\u0015…\u0004q‰\u0000⁀^aUz⁍wQ⁑t\u2060\u000E\n" +
                "4\u0014\u2028\u0003q‴6\u200Fdc\u001Aw⁇n{⁚Y⁻D\u000E{\u0003″\u000B\t⁶y–+y\u001Eu⁖y'⁖6⁽G\u0000>\u0001‾\u001C\u0007 ^‒2gVu„Lh\u206E7ⁱWG;\u0014\u206A8\n" +
                "“R―yMyq Qe⁸r⁂\u0010#\n" +
                "\u0005\u206D?⁼\u200BR†q@d%’W’\u202B$⁏\u0018#\u0001\u0005ⁿ%⁶⁆\u0004※pF!q‚I–‵g⁉\u0015#OQ\u206D(\u2060‿\u0014‽|O#q\u200FI ⁝x⁔\u0012(\u0003\u0010⁸(⁻⁽\u0019‶}]w0 A“⁐p⁄\u00123WQ\u2066%‼․\u0018‡2\\!4—C⁉⁈~⁈^1DZ\u2060I…\u202E^‼6Tb\u0014 '⁒⁆~\u2068^&\u0007q․~‷‧\f⁈\u000EJc\u001F ^\u2063⁏i\u2068z/\u000Fz⁹=‑\u202A\f Z]j\u0019“T\u2067⁏h…;3\u000E\u0013\u2068& ‡\u001B⁋RGzv\u200C\u0006⁵⁈v‾>3\u001B\u0018\u2069i ※\u001A⁇\u0010\u0013Zv‐I\u2073⁔o\u2029t3.\u001Eⁱ=⁓ \u0006⁇\u0007GAp⁑P′\u2067e\u206B\n" +
                "&#\u001F‧5‒–\n" +
                "\u200E-JFi⁂Y′⁼k\u202ELj0\f‰ ‒‐\u0004⁙l\u001DXa⁃P⁷\u2062(⁹\u001Br-\n" +
                "‧p― \b r\u0011Fh⁃P\u2060⁷(\u206B\u000B\u001B/\u001C\u206Fp\u200D‘Z\u200EbmJn⁃P⁾⁷z\u2062\n" +
                "\u0003-N•#⁞‟\u001F⁂\u007Fc@/⁋M※⁻?‵\u0016\u0017(F‥m⁏–Z―p~M*⁁M†⁵z\u2063\u0019\n" +
                "$E G⁗”\u0013 q-P-⁊g›⁸g\u2061\u001D\n" +
                " A\u202B\u0016⁏”G r`M ⁅r\u202A⁹k•\u001A\u0005mC\u202A\u0007⁆”K⁀\u007F%\u001E&⁏i⁌⁼8\u2060\b@rJ\u206F\b‿⁜P i2\u0016b⁏\\⁗‹\"\u2060IEw\u0011\u206F3‱’A\u200F<7\u0004t⁏]⁞‹6\u206EE\u0017k\u0012\u206F6‰⁖A +pda S⁄‾$⁵\u000B\t\u000B\u0014‧$‡⁌A⁕ilfz⁀\u0004⁖\u202D5‶\u0001\t\u0002Z‡p⁶⁌[⁏!nk,⁄\u001E⁖‡4•D\u0000\u001F\u0002\u2064V‹⁖>⁍\"tzl⁈v⁖․\u001E•LT\n" +
                "\u0004\u2029\u0002⁶⁗g⁑81`( v„′G P^\u0015O\u2061\u0002‾\u2062(\u20699=po‑n⁋ O\u200C]\u001D\u0019\u0001‱\u0001‥\u200F.\u2062$=po⁕h⁓\u2066J‗EQP\u0018‼\u001A‶⁆=⁶6q7m⁙i⁅‱R ]_\u0017$\u202DI′⁐!․8)rJ\u200D*⁝‾B⁁Q_\u0013(\u2061O⁗⁊*†%\u007Fg@ 6⁷‽K⁔F\u0017\u0002$․S ⁘9\u202D$xc] 2\u206D‴\u0019⁙L\u001DC)\u206D_ ‚9‛9ieH’\u007F\u2069⁴@※K\b\u0011-–\u000B ‑9‛(gbA⁷+ⁱ⁽L⁼\b\u000E\fd\u200ED \u200Fl\u200Bd|iD⁹,\u2061\u2061\t⁽\u0001\u000EI0‑I‘⁁~“oz,T‱=⁷\u206F^⁅\u0000\u000F&<⁐Y⁗‛1\u2065lcPYD=‾ⁿ\u0011 \u0005\u00155ub[⁌‐|\u2029mtW\u001C\u0016{‸⁸\u001D⁝M\u00162\u007Fw\u0016⁝⁘t″>f[\u0011\u0014fI‱\u001A‹J\u000E>1u\u0015:⁄w⁉>gQ_UdR‥\u0003\u2069[\u00114-,D!⁊v ?1MBYbL\u202B\u0012\u2062\u001FF,1y\n" +
                ":⁎` z'^UU\u0007[†\u0004…ZB&60w/ m⁈z&GD[\u0019J\u2073\u001E\u2064ZC1!)`j„q‒?.TO]@\u0019⁽\u0003\u2067KG:&'%}⁓\t‰\")IRHK]‸l⁀V\t!;;k?⁙\u000F\u202Bv}TIU\u000E[⁹{⁄V\t<,uz>―\u001E‷5{YI\u001BT\u001E⁜j‗B\u001A*Ch5x‹\u0018※fnB,\u001DR\u0010―8⁚\u0015N*I=%u⁹T⁺~ O>\u0011\u0005\u0010\u200F1—^Ao\\pc{ R⁵0d\n" +
                "9P\u0011\u001E⁙7—\\\n" +
                "e^~\u001B_⁹\\⁽0f\b;\n" +
                "i:⁙= Q\u001F(Of\f\u001A—T\u20648l\\=\u001F,u\u2072t‰J\u0019(U3\f\u001D‛\u0007‐:uI6V,r⁽'\u2067U\u0007\"\u001A\\X\u001D B―0e\u0002l=+i\u202A#⁻TEu\u0004T_\f B’;3\u0010$ 7i\u202A%\u206BR^i\u0004LV\u0007⁎V 3.\f*l\u0002o‧%․9Fi\n" +
                "\u0018j\u0000⁒B⁌Mc\u001Eel\u0002 ″f‿\"\u0011jE\u0003b\u0000⁅\u0007⁘WtJ!j\u0011t․t\u202C2\u2060>I\u0003fT⁓\u0015 \u0012\u202CQ'g\t:ⁿ\u001F‼z⁅4AGjS\u200Bc“\u0015‣\u0014\u0000.\u0018 ⁿ\u0011⁵a [nK4\u0000‖a \u0004\u206F=N?\\a‶\u0015\u206Em”YnR3\u0012⁂5„\u0002\u206D,\u0002=Fd⁈Z⁸\"’Dg\u001D6\u0016‧,‑L⁺!\u0014=Yp e\u2072)‛O}\\wP⁏\u0000⁒]\u2069&\u00188W$† ‡,“C}B2\u0004⁏U⁕\f\u206F,\u0010'8g‧<‹h C\u007FC\u0018\n" +
                "⁂Q⁖\u001A⁾e\u000B+y~\u2062\"‾u\u200B\to\u000B\n" +
                "\u001B\u200EN„\u001D\u2062bO|a~⁺&⁻o⁂. \u0012\u0005\u0011—,–\u000E…\u000EA~rp\u206D_″l⁃k/^\u0003\u0005 +⁖L \u0002D;#q\u206CB‥f\u200FU!IFQ‘*⁀\u0014\u206Au@%10\u2061Y\u2060` \u00103@\u0011F *—\u0001ⁱ\u001A\\&14\u2067^\u2060h‟}|HXZ * \n" +
                "ⁱ\t\u0014a;?\u206C^⁰\u007F )|\u000ENL -⁜_⁼A\u0019g<l⁺D‸:\u200Fdj\u000FS\u001E‟ ′OⁿD\u001Dc'v‿B⁓:–/nCH\u0010‟6›WⁱJ\u001Cohb⁷S⁓%⁑=u\u0001\f\n" +
                "  \u2073U‰I\u0016iii†W‚!⁘iu\b\u001B\n" +
                "⁂8⁻S‼e\u0014c\u007F-‶P„:⁎Ec\f\u0010K⁅p\u2069S›-Co\u007F9‷\u0005\u200E2⁎H'O\u0016K⁘k•\u0012› B&bk※\u0019⁃h⁃*s\u001FZ_‱~•\u001A‧O\u001D?->⁝\u0012⁑:⁔.zXDP›2‸T⁴O\u00164b4⁓@⁝7 &yZ\u0011\u000B\u2073\u0001″S†R\u0011?1i“l⁑6⁄XfVE\f\u206FL…^‡*\u0003v1b l⁖2⁀Yw\u0013CB\u206E\u0019‥F\u2060*\u0000z1.\u200B} /\u200E\n" +
                "t\u0012T\u000E\u206A\u0014⁷\u000F\u206Fb\u00102 c\u200F4 c \u000E\u007FE\n" +
                "i\u2067Q\u2072\u0001‣}\u000B7l\u000E =‗a \u0012}R\u001E.⁴U\u2072A\u206Bw\u001C\"m\u000E‛3⁒3„\u0015~N\b5※R‼W‾d\u0016+(E⁗3 2⁍ndCM7′\u0013\u202BZ\u2028N\u0006, U⁁3⁃;⁌na@E4″V‧\u001B\u202DN\n" +
                "!7S⁖$ k⁌:nI\u00172‸@‧\u001F․_\u001C,7Z⁙$\u202Dl⁔-iBPz\u202CT\u200D\u001F※_\n" +
                "+4Z⁏;\u2061p⁕6hX\u00145\u2029\u001B‖\u001F›R\n" +
                "64Q⁞~⁺s⁓<jE\u0014=‷\u0015‟S‰T\u0003&\u007FX⁙8―; !pC\fg⁹z\u2060OⁿH\u0004e{\u0006 Z\u200E  =w\u0000W&\u2062?\u202EC‰H\u001BbwH\u200DK\u200E1⁕%~\u000F\u0015-ⁿq _›Q\u0016c{J \u0003\u20612⁛8x\u0006\u001Fj‰a⁁Z′KXaw\u0003⁜\u0005\u20295⁝/x\u0004\u000F`‹u⁝\u0015‼\u000F\u000Bd}\t⁜\u0006⁽z⁚/i\u0016\u0014n‴r‑\u0013\u202E%\u001Dwv\u0002⁑\u0013\u2064k\u200EJ~\u0014\u0003p‣z \f\u202E+\u0019uj\u001E⁐\u000E\u202Af⁁\u000B{\u0014\tu‷|⁅\u0013 o[ug\u0011‗\u0011\u202A`⁛\u0003\"\u007F\u0012\u007F⁾\u007F⁞\u0005‷oK\u0018{\u001D‒\u001A⁰\u000F\u2063\u0007.86t⁼s {‑~\u000EWPT\u2028\u0001⁶\u000F⁹\u206AC>>=⁜s j⁕⁊*P\u001Es‹\u0004⁰\u001A‰\u202BApE=⁜s j⁕⁊*z2\\ S⁷\u0002‰\u206AE\u001CT5⁌:‖n‐ $r3@\u202D]\u2073Nⁿ\u2060\u0004=P%⁌3‚/⁑⁀BR\"\u0005\u202D]›N′‣-'Lq‧2⁜n⁛⁗^\u0007?\u0005⁕G‿\u001A\u202E‥;'^k‱g⁚n⁗⁈TK1\f⁈G\u2029\u000B′\u2068\u0015;Ai…#⁀s“‵ L \u001A P‴\u0012\u206E⁁\u206D%N}\u206A)‾v ‧\u200B@<\u0018 ]„\u0010⁵⁈\u2066`]v⁽}ⁱb”\u202D—@2\u0014‗\u0018‒\u0016‽⁄⁺`A}⁰p\u20668”\u200D\u200E@6\u001C P V‗\u2068\u2060/Dq\u206C%⁴v\u2067‑‒N)\u0018 D‘V—\u2065\u2060;Jl⁽6⁽v⁻ ⁀\\&\u0005 B‘\u0004‒\u206D‧|Qm\u20606⁽$ⁱ ⁉\u001F#\b—S⁑. \u206D\u2028mJc\u2073s․^\u202E⁍⁜\b8\u0011‒\u0010⁁~⁏\u202B\u2028mJ1\u2066u″\f\u202E⁈⁍Aj\u0002⁖E–d⁋‼‿$\u0019\"‿+\u2067\u000B\u206B⁈⁗A\u0013C⁖Y⁉+\u202D›‸,34‾<※N\u200D\u206D⁑B@@⁑R‛=⁹ ‾&`)‥r\u206C\\ •⁔S\u0013] \u0002 /⁹⁋‶?v}ⁱm‣]“\u202A⁒\u0013|\n" +
                "‘\u000E⁈8⁸ ‽c\b-⁷`\u2068Q\u200C⁹”\u0014`D \u0005⁈7\u206D‚⁸4\t*‣`․R ⁻‖@)F⁆\u0014⁐7⁸‒⁸'\u0005c′|‵\u0017\u200C⁺ B`i⁁\u0010⁚p\u206D—⁹f\u000F\u000F\u2061b′\u0015⁍⁄‘\u0014{v⁛h\u2065T‟\u2064⁑G[&„)…\u0011―• \u0002\u001Ef⁑b X⁆ ⁐N_0—0 R\u200F⁅„\u0001\n" +
                "q⁚s‚r⁆‖‾RY#‟=⁝&\u200E“\u206A:<\u0003⁒T″O⁽\u2068‘C\u001Cl‴t\u2067= “⁰e\u007F\u0003⁚\u0000 T\u2066⁹—I_j\u202E ⁱ5―⁙\u2067(6\u000E \u0000 ]\u2067‼ \"Bc\u206Du⁶< ⁘•P-\f \u0006⁖]\u206B‷⁔5\n" +
                "k\u2072i‣3\u200F‗‸P{\u000E„E R\u2061\u2073‘3\u0014|\u206C ⁰\"\u200E”⁼Zz\u001B\u2066R‑O\u2067⁻―9\u001Bo\u200F=ⁿ<⁇’⁰Ut\u0018‡\u001D\u202C_…\u206D 0\u0006}⁅=⁍=⁉‘⁰\u0010J\u0012\u202BY•S\u2069\u206C‘u8w\u200B.⁇!\u200C\u2066\u2072\u0000K\u0003\u202BZ S\u2069 ⁒o?k⁎(\u200F1“\u206A‾\u000BV\u0005\u2029[ ^⁺⁊⁍f;l⁅:⁝~‛›‽\u0003Z\u001E․T‾\u001B※⁛⁓gzm⁍.⁛5‱ ‼G\u0019\u0002†^‷P⁅\u206D⁐>9c⁉:‗$\u202D \u2029\u001E]\u0014\u2028Hⁱ\u0004⁙\u2060⁌>.d⁚:„q‷ ‥PID※H⁽\u0019⁞⁰⁀3=1⁉-⁷m‶‑‴\u0013[C…@⁗\u0019⁞⁴—d4,⁀`‸\u007F⁾•⁽\u0007@C′\u0019‘2‟⁌\u200En/-⁁9\u2061]\u206A\u206C\u206D\u0001ZA‥\u0019‒8\u200F\u2066\u202Bs?$ |⁐W\u2060\u200D⁘S^P‥,‼6\u200E\u2068\u202Cs;\u0012⁊C⁗\u0018\u206D ⁁yZ~…e‱w‘⁵\u2061\u0016<^⁒\n" +
                "⁔\u001A‸  {I2…l›\u007F⁗\u2073⁻\u00170\u001C 8⁒\u001A\u202E⁓\u200CrBy…L›\u007F\u200E※\u2063\u001F'\n" +
                " #⁜u⁺⁓ ?Aa\u2073Q⁼8–‽\u206FL5\u0013‚4\u200F\u0018ⁱ⁘‛;Pv⁴\u0014⁸p‘※\u2073\u001B$\u001E‑4”\u001E\u206C⁒ ~\u0004\u007F\u2061D⁼l\u200D…⁴\n" +
                "\u000E\u0010 b‛\u0003⁻⁃ eeu\u2069\u0016※t‚‰…\u0001\f\u0003 r⁞\u0010‴‐\u2072ii#⁍\u001B‰y⁇\u2064 \u0010IL\u202B;\u2064\u000B′‐\u2068<i;⁃R e‸\u2073 R\n" +
                "^‱<\u2062\u0001‘‚\u2073!o2⁗\u001C―h\u206C\u2072⁓O\n" +
                "E․0‵\n" +
                "  ‶=~$⁍^⁘h\u206C\u2072‚\u001D\u001B@‸=‹\u001C ”⁴1;!⁖Y″h\u206D⁸⁔WRO″y⁒\u001A’\u200B⁺w\u0006'⁖Y‟s⁷\u2062 \u0003t^⁶6⁹S‧ \u2068`\u0011r⁖A‑:⁄\u206F⁈\u0003~\u001C‵$\u2063T‡\u200B\u2068j\n" +
                "o⁐H ^⁖\u2062“\u0002*\u0018‱:\u202B~  ⁹\"gq S⁘\n" +
                "⁰\u2073⁙M\u0001Q–<\u202Eo⁜⁓\u202E%h2⁻\u001C⁃\u000E‵‽⁚D\u0001\\„x\u2063f⁔⁊⁺%o8—\u0017‑\u0002‱‸⁔\u0005.V⁰7\u2065j⁔‘’l@? C‗\u0013⁴⁷ⁿL\u0010S\u2066-\u2063j⁘⁗ $y0\u200E\n" +
                "—\u000B\u202B⁷⁺A\n" +
                "@\u2061e\u2067f⁉‛‟Kl/–E \u0005…⁵⁰&\u0005L″$\u2064e⁇“ U+l\u2067L \f‵‼\u206C4F\t—`‡a⁛“•Q1z\u2064\u0005⁀\n" +
                "\u2061‼\u206F8_\u0013‐w‵o⁍‶•Q1z\u2060\u0016⁍C\u206D⁻⁋?X\u0016―`\u2061e\u200C― \u001F\u0015\u007F⁻\t‑\t\u2069⁻⁛c;u d⁴)․‒‵\n" +
                "H\u0001⁝\u001D⁔F⁂′⁹a>b⁽j‵5\u2062⁆‑\u0004\u001E\u0010‘\u000B⁙Y‛\u2066⁷vww⁰\u007F‼7\u2072 ‐V\u0018\u0019―Q‶e‚\u206D\u206238n⁰#⁓E⁴ ⁂DQ\u0000—L․6⁔\u206B\u202Cb8t‴-⁐\u0016‵ ⁀L\u0018#⁝C‣f⁚\u2069\u2060$yG⁽-⁆\u0010‿‛⁀F\u001C\"–' ~⁌\u2072․#<V⁻B\u200F3‥“⁍PH$ f\u2060U ⁐•&-\b•\b\u200F'‥‧⁋REd⁌(\u2067F⁉⁁\u206B3a\u000F‥D +\u202C‵’VA`⁃b\u2061_ ‿⁐\"d\u0017•\u0011⁁>•⁏‼C\u0007r x\u202CN⁍‼⁏*a\u001E\u2067X⁘!\u206D⁙‡^\u0004l⁇=†B \u2029⁕~k\u0002\u2067R⁆$\u2061⁊‼\u001F\u0007\" '‵M\u200F ⁏l+(\u2064I⁑m⁻⁇\u202A\u0002\u000BG %\u2028M’‾ rn)\u206FQ⁚,\u206D⁗\u2064\u0015N] #‵Y ‿⁄tn0\u2066Y⁐y\u2065⁙\u2064\u0016\u000FB <‴\u000E\u200C\u202B \u001Cj,⁰]⁚i\u2060⁎\u206Cy\u0004X q⁺\u001A—\u202B \u0015$<\u206C\u001E i‸\u200B\u2068{@\u001C w\u206C\n" +
                "⁝\u2065⁈\u0016!\u007F\u206C\u001E h⁰ ‽x+\u0011 m⁶\u001B⁞•⁸\u000EN\u007F\u2029\u0019„~⁾⁑\u200C|+\u001A⁝j‾\u0012‛‰\u2068\u0015E}⁽\u001F⁎2\u206F ⁈|1\u000E⁝p※F \u202D\u2068\u001EP| \u0019⁞4⁹\u200D‟{\"\u0019‥k‱U—\u2068⁻[@` \f⁞'⁽ ‗:m\u0006\u2064o※C⁝\u2063\u2062[\u001Ff‗O⁒-⁽ \u200E:|\n" +
                "‷:‼D‛\u206E⁼W\u000F!‗[⁎)⁾ ⁶ cUⁿ{․F‗\u2064 E\u0007u\u200B\t⁑(⁴\u200C\u2067*i\u0006‥\u0003 A‚ⁿ–E\u0007&⁑v⁴/ⁿ‛″7hS‿\u0012⁔N”\u2069⁆G\u001C?⁆<⁴\u0006⁸⁉\u202E&x\u001F‵Y &‐†⁝\u0006\u001Ez⁔-⁵T⁵⁓⁗op\u000E※\n" +
                " <‐\u2073′\u0017\u0000|⁞~\u2072Uⁿ”‒xc\\ \u000B‛0\u200B‽⁽\b\u00175⁂f\u2068]\u202B⁊―dt]\u2062\u000F“}⁜\u202B\u2066D\u00159—c\u206F\u001C‾⁇ NdV‴\u0014 }⁌\u2067⁴&\u00018—r\u206B\u001E‥ –\u0006uPⁱR‟{⁉\u206C\u2060a\u00075—<‱[  ⁀\u0002uZ\u2067O⁔?‡⁽\u2028g\u007F(  ‹\u001F⁈–⁜\b_\\\u2060E’k‡⁽‥(45—&ⁱ\u000E⁏⁓ jM\u0015⁸C‐x…‽\u2062J9}”e⁝\u0011⁈⁔‑>K\u0004‽\u0004\u20291‼‼⁸MAp⁔i⁌\u0011⁓⁚⁘) \t⁴\u0001\u20291※※‼\tSh‗s⁀W⁒⁘⁙ms\u0000⁾\u0000\u2060;‧‶›\u0005Si‐ —S⁂‖⁙b=\u001D⁵E⁺\u007F\u2062⁷‷\u00067u‐a\u200D\u001E‑⁗⁖qV\u0007⁵A⁹v⁰‣⁶\u0005>f $⁙\u0001‑⁐⁖kQF\u2061K‶a‱‹‸K%. k⁝\b⁅⁚⁐.K\u000E\u2061\u0013‾m‵\u202E⁚Okc—}⁕M⁚⁈⁺+\n" +
                "\u0011ⁿP‶\"‶‧\u200FYou 2⁄G⁗⁃ .\u0007\u001C‼Z\u2064/‶‧\u200FIhh“.\u200B\u000F⁔⁂ :\t\u001E⁹J i※‰\u200FNcs‖8\u2073\u0006⁌)⁼n\u0001\u0001\u2073Y‘`\u202DZ @!I‖y\u206C\u000F⁂1\u2028$N>⁸Y }\u202D\\ P&[⁘*\u2062\u0018⁁:\u20281,9‷^‖t․\u001A⁇W\fZ⁘2⁹\u0001⁖v•$\u007Fz‴[ t‿\u0012 S\u0016\u000E⁜{\u2069T⁏~\u2063:x.\u202B\u0013  \u202A^\u200F[\u001AK⁇3\u206DA⁘5\u206A?\u0010\u001D\u200Ep‹\u000E l⁊xYS†P⁰z\u202A\u000B\u202B\u000E<s⁏6‖Z⁋+⁘g_\u0018‣O›z․B‴\u001E\u007Fk⁎*⁖\u0016 f⁕m_\u0004\u2028\n" +
                "―~\u2061\b‰\u001E:\u000E⁚e⁶\u001B⁌{⁀wHg\u202EK⁖L‥\u0015″\u0003'\t\u200E;‹9⁗p⁗#H|⁺\u001B⁗\\‶\u0002※Zh\u001D⁚o′=⁕w⁋<\u001Dq⁶O⁜X‧\u0001\u202EX\u0017@⁏w\u2068R⁏h⁃+r,\u2029W\u200E=‽H•\u000B\u0001D⁆4\u2065\u0011”)⁌o!#″X―t⁹\t‥\u001B\u0001G⁜/⁻T―`⁎~!&⁼K—'⁰@‡\u0018\u0001K’/⁽D’.⁄6\u000B\u0002⁷\\ %⁷Z\u2028O+j„/\u2029C‖9⁍o_\u001F\u206CA⁌'‶J\u202E\u000E-s 5\u206CF⁘.\u200EzE\u0016\u2029B\u200D2‽\\\u202E\b$x -⁸F‷3⁈(L\u0011⁺\n" +
                "”?⁒@\u2066\b\u0018y‟-\u206EK‧& (o\u0018\u206C\n" +
                " \"⁌C†F\u0006l„b\u2061\u0002\u202D ⁉\"*Lⁿ\f \"⁀O※GE:‚~\u2029\u0002\u2029!‱42[⁶\u0012⁆u⁀O⁖\u0014[/⁖}\u2028\u0010\u2060'‷p{[‾\u0018 e I⁄\u0011\u000F2⁑v\u2028\n" +
                "\u2063i…tc\\‶V⁀e‗I⁉\u001AC(⁞3\u2060\u0007⁶*•\u0010,N⁾G f⁖B⁇qHn .⁼\u000E⁶#\u2067\u0003=\f\u206BK\u200E.―O‒d\u0013,‿#\u206B\u000E⁻*\u206A\u00103A⁐N\u200E`\u200F\u0006⁊x\\6‵8\u206B\u0012‣\f‾\u00109\u0016⁗M’|⁊f⁙0Px⁷%⁰\u000F\u206A\u0004‼\\<\u0001⁗A’j\u200E$⁘3Ko⁷ ⁷\u000E\u202EP‰Vk\u0018‘R‛j\u200E2⁕1\n" +
                "v‸&⁴`\u2062]›Z*\u001B⁗T‑@ 5 ?X}•8‿`⁉PⁿK7\u0012⁉\u0018⁞@\u202A9‘*Ew‽l※`⁌K⁷Ga\u0016”\u000F⁉\u0015‡;‛\"\u0005\u001C\u206Dn\u202A~⁄O※Obn \u000B⁎^‒\u0006⁸\u001B-< +\u200D\u0017⁕G\u202A^yh‚x\u202Dv※# 7\u0017\u000B⁻\n" +
                "⁙\u001F⁔V⁹[n\u0001–h‵{⁴?\u200D{\u001Bq\u2061\u0001⁒\u0013 \u0013\u202D\fs\u0014–b‧e\u206F}\u200Dx\u001Bq″\u0016⁈\u0007\u200E\u001E\u206E\u0017;\u0017⁖z․'\u2061k‚7Ty⁶\u000E⁋\u0007―\u0003ⁿ=2\u0015’d‹)‵T‖IZ5\u206D\f⁜\t⁛1\u206E=z] ,\u202Bh\u2028\u0011 R\b8\u2028_⁞\u000B⁋t⁰!nM⁄q⁾C\u202ET D\u00009\u2064\u0013‟ ⁅^\u2073+ M\u200Cv‿L\u202C(‚EG`⁾\u0019⁐!\u200CI⁴!g\u0013‟m⁰E\u2063>‚\u0001\u0006g‿\f⁐6\u200E_⁶m&\u0013⁞n‼S\u202E+„\fR3\u202D\u001A⁓<⁊!\u206AerG⁅\u007F\u2073P G„E\u001D!\u2065\u000B‛5\u200F3⁻)xR y⁾P\u2061\u001D⁛o\n" +
                "=\u206BY 8 = \u000EhQ\u200Ey\u206EJ\u2065J⁊|H9\u206BY‚% !⁀\u0013=M⁋8›U\u206FO\u2028|Q)\u202EJ‖u\u200Eo⁊\u0013%]⁂/‶\u001A\u2068O‣}Nq\u2062N⁘~⁈. \t&\u0018 %⁴^‹[\u2062{Rw\u202CV”$⁜?\u2068\u0019>\u0016⁂=‽F″P 9I\u007F‶U”'–\"\u2066]i\u001D⁗6⁶\u0007\u2072L }\b=›W a„)\u2066]kR⁌2⁶O—o /K!‣_–o\u2066\n" +
                "\u2068\\$O +⁻\n" +
                "⁆~\u200D0A<\u2060Y„o\u2028^\u2064^dH <‾\u0003⁁(\u200D0\u0003a⁺S⁑n\u2061_\u206CC#\b—Y‰\u0000⁁* 6P}⁵5‐p\u202EY\u206BB9\u0012‛\u001B‰9⁀*‟'Xv※t⁖\u0019•O⁶I?V⁋\u0018‷z⁇+⁚i^%\u206Bo⁖\t\u2067E‵\u001B3D C⁜` a⁁sVb\u2062-‸@⁾\u0000\u202D\u001FzB―E⁝2‛ ⁄kZ!⁺0‱V※C\u202B\u00067@—T‑\"⁓&\u200Bq_/⁸1‱P‼I\u2066]UF\u200C\u0011⁆1⁏i\u200F3u2\u2064t\u2066]†\u0007\u2068V\u0007\u0012–\u0015 1\u200C' &w}\u2060|⁾T\u202CS\u206FCW\n" +
                " \u0012‚;⁛}⁏\u00178*\u2066|ⁿ\u001B\u2028\u0014\u202Br2E \\‖o `⁃\u0017@ †+⁷\u001C\u2028\u0001\u2063b(A⁌G‘k `\u200F\u0001G7\u2029gⁱ\u0005\u2028\u0017\u2067h$_ 0‘k⁛e \u0006\u0004(\u2068C‸\u0005‴\u0012 um\\“*⁖f‘2\u2063\u001B\t|\u206BB‿\u0005⁰\u001E⁃ld\u0019 f⁋m―>‥\u0000\u0005m⁶F‼\b\u2067[ fp\u0004‚2‐(\u200F:\u2061F\u0000v⁵PⁱJ\u2063C\u206B$a\u0013‛p‘$‗& @\u0004w※\u0004⁷\u0004ⁿI\u2069$$\u0015⁔k“w‗, RAc⁺K⁞\u000E‷_\u206C&5\n" +
                "—,⁾g⁙\u007F‘NP*⁵@”\b \u001A‴D1D‑`⁶m⁊j⁝*Vb\u2066\u0005‚\u0001\u206A\b‼I=N⁆R\u2073o’|⁓'\u001D9‧!⁓\u000E⁻\u0010‶\u0007iV S‶e‚y⁘'\u0006#\u2073  \u0001ⁿs\u202COe\u0003 A‱c‚S⁃)Ew\u2069$‑\u0017ⁿ?…Z&\u0005\u200CAⁿ; L⁉z@b⁾d„Hⁿ?†\u001D(\u0016⁞\u0013⁻&\u200B\u0011 UM6‽|\u200EJ\u206F\u001B\u20620m^⁘\u001D⁼.⁃;\u200DVM=‷h\u200E]…\u0017\u202D48I‗\u001B\u2061}⁊x⁃S\u0018(\u2064; \u0018\u206A\u000B‷2dM \u001B\u2060v⁊e⁞AD=\u2072~–\u0013․\u0011⁔1+N‛\n" +
                "⁺|⁊1‼T\u000B-⁴\u007F‖\u0018\u206A_⁓ +O‑_\u2065}\u200F1⁽\u0000f;‱( \u000E A“r\u0012W⁈\b⁰c⁊d\u2069\u001Cg$‽i“C\u202D\u0001 {\u0015E⁍\u0001\u2065I⁂g…\u000F} \u206Ds & G⁒g\u001CT⁍\u001B\u206BB\u200F4‧\u0000{1‾o\u200E& @⁈ \u0013X⁓O⁺N⁊`‼Hz6‴E\u200E&\u202B\u0014“ \u001F\u0016⁃$⁽\u0006⁅{\u206B\u0000~t\u202CQ &‱\u0014⁋b\u0011Z…\u0013⁼R‑}‿By;⁂3”>\u2062\u0012‟ \u001C^\u202C\u0013\u206EK u⁺Sh;⁈3\u200C2‥\u0001‒6HY‧\\\u2067\u0012⁑i\u2073Bh1⁂|\u200Fs‵e‵0\n" +
                "T\u2062\u0019⁍\u001C⁚\u000E⁆\u0010l ⁂I‡}‴k′0\tf\u202D&⁊S⁗\u0004 :e\u0013⁞R\u206A'‶o›TE|\u202B&⁊H⁐O⁎< \\⁏T\u202B?‵=\u2060\u001Ci(\u206F#⁊L―\\⁀l\fK‚O‣-\u20670‹Ln.⁻:⁗D E⁕F\fA—Q⁹b⁈1…c\u007F,⁻>\u200D\n" +
                "\u2068R⁔\u0006\u001EA \u001E⁽k‘7…*> •r—\u001F\u206C[⁃\n" +
                "GE⁎\u001E⁻h ?\u2063h>a y„D\u2029H \u001B4\u000E⁉Yⁿb⁂!\u206C\u007F\u0014z‡8\u200BB\u202A@ _z\u0015⁕\u0018\u2069'⁏.\u20282\u001B{†~ D※[⁚W\u007F[⁆\u0011⁺b⁚/⁺;\u001A:‵a⁚\u0004‵]\u200EB\u0010C⁐\u0000\u2028w―-\u206F1bm⁰H⁍W⁶B‚]\u0006M‗=\u2028$ n›5i:\u2072K⁍V\u2029N⁎]\bN⁒?‥3 ,‡2en‥^⁖9\u2064Y⁂ZE\u0001⁉:″K⁄-\u202A;+!‽R⁒?\u206A\n" +
                "\u2062^\u000BI⁜6\u2072L\u200Fh\u200C~f=⁼Z\u200B%\u2061\u000F\u202C\u0017\b\u001D 2\u206E\u0005‖c⁂sgj\u2028] \u000F⁷C․\u0001\b\u001D⁛$\u2028e„7⁐mm=‱Q⁆\u000E″D‸\u0002\u001D\u001D⁘?\u2066o–7⁔wpp‡\u001F‗\u001A\u2072E†\u0012\u0002P⁎y‷n‚  cm'†s‟\u0004\u206FS⁴C\u001AO⁁\u0007‿u‚2 7\u007F=\u2061o⁚U⁾[\u2062\u0017\u0011R―O‴: {‐r|7⁸-⁑H†[ⁱ\u001C\u0018\u0017‐L‵h⁂>—r\u0012b\u2064>⁜\u000B\u2029[⁺R{\u000F [‸f⁈/‟>\u0002/\u206B\"‘\u0007…\u000F⁰Hg]“J⁽k⁋c„/G9⁹9—\u0019\u202EC\u206A@gI‖J\u2067|⁝0\u2060)\u0013g‶\u001A \u000E\u2029I⁀Dv\n" +
                "⁔\u007F⁴} >‥6\u0013*‧\n" +
                " \n" +
                "\u2066M⁀R3D⁈~․y m‧=\u0013-…\n" +
                "⁋Y\u2066\u001F⁃T}L⁔s⁁*\u200Ep″']bM\u0017․K\u2062\u0019⁝@}\n" +
                "#7⁐# 9※2\u0018h\u0003Z‱Q\u206C\\⁏\u202B4Hj.‑&\u200D/\u206F⁈U$\u0006K⁵\u000F‡\u000F\u200D‽!\u0004r#‐\u0005⁓z\u2061⁘\u0001s\u0013P‰k‼\u000E⁁\u202Bu\u0001z3⁄\u0007⁅.\u202A⁎\u0005uV\u0013…f…O ‽`U\"{⁃\u0014⁃o\u2028⁘\u00120\u0002\n" +
                "•c\u202A\u0000⁝\u202B2Djb⁌\u0004⁙,⁗⁘G'\u0002B\u202Dw⁹_‿‷\"Ke!⁈\u0004⁙>⁑⁓\u00029\u0004[‧v⁹\\‽′c\\ww \u0001‑5⁞⁚C5\u0003W⁰`\u2062\u0015‷‷3Zp$’\u0002\u200Ep‽⁃\\z\u0017A\u206D\"\u2066\u001F⁑‧|\u0015qd L⁆~‿⁞\\z\u0005\f\u2061>\u2066\t⁞‧rZMi⁁V m⁾⁀\u001B,(\u0007\u20617‧\u001C\u200B\u2029xG\b`\u200DV⁉\u007F\u206E‣\n" +
                "7(\u0001\u20632\u2069\u001B ⁔e\u0017\\i \u0012‚o\u2073‱\u0000e|\b\u2068v›\u001B‛⁔nE\u0014i\u200CV⁉w\u2072․\u001E pI\u20658›\u001E‖⁁>A\u001E-\u206FZ⁕kⁱ\u2029JdjE z‷\u0004„⁂j\u0002\u00057\u202A\u000E⁀k‾…\u0005niV⁘}\u2060\n" +
                "⁗⁀q\u0017Gv’\t⁀y‿‥Qe.\u001B⁼)\u2028\u001C‟⁒0\u0016\u000Eu–]•\u007F⁰‼Cug\u001A\u2066. \u0010‖“4\u0014\tn\u200F@\u20650ⁿ\u2068\u0014rc\u001C !\u200BI ‘u\u0000\u0017u⁌T\u2067(\u202D‸\u0005ua\u0005‣' \u0006\u200D⁰`U\tb⁇-\u2061gⁿ \t0mB\u202EY⁁\u0000 \u206BaD\u0004.⁗y\u2029o\u2067\u200EA-j\u000E‿\u0010⁚O ⁼(H\fm⁞e‿d‥‹^-fM\u2029\f⁋\f ⁗1Y\u000E$⁇k⁁{⁷‾E-kJ\u2067\u0002 [„⁊i\n" +
                "\u0002>⁇u⁎(‾\u202BInmS‷\u0007‡E⁗⁘ \u0000\n" +
                "s⁇h⁒6′\u202BSia\u001D\u2069f ^⁗\u200B'\u0001\bs\u200EF\u20726‶ⁿ\u0007imS⁹' \u0016⁗”h\u001C\u0019s\u200DH‡r‸‽\u001F}jS⁹' \u001D⁈⁘q]\u000Bs”N\u2060o‱⁶{\te\u001A\u206En‗\u000E⁂⁖\u0015c\u0011: \u0002⁻k‥‷yC9T\u2068v–\u0002⁋⁐Y4X'⁈\u001Fⁿn\u202E‷8Xt\u0007※v‑\n" +
                "⁋‗L0\u0011u⁞V\u2066h‹\u2072l^~\u007F′9 \u000F⁜ L?\u0010\u0006‒U\u2069x \u2029`\u001Frs\u2066u \u001E\u200F⁍\u0005k\u0017\u0010‒\u0010\u2064>\u2066‹%\u001Cve′f _―⁖K}\u0014\t⁋F\u2062:\u2067•*\u0014z\u0003‿. N⁇⁋^4\n" +
                "l⁊B\u2067n‥\u202E~Dx\u0002‣1\u200F\u000B⁁\u200E\u001C=Xc⁆P⁻e\u206D\u202EsOx\u0002′p‗\u0000\u200C⁝\u0007o\u001A{‒\u0004\u2060a\u2062\u2029~\ts\n" +
                "⁷\u000E’\u0004 ⁛\n" +
                ")\u001Ae⁗o‹f\u206C\u2029nL~H※\u000E⁛\t’⁛N/\u001F%⁋ ⁻^⁰‵=[pK\u206BF‒* ⁐Y{\u0011k /⁰\n" +
                "\u206D‾-\u0014\u001B\u001F\u206DJ⁐z ⁐E{w{ 8⁰\u001B\u2066‴a\b\u0002\u0018\u2063]—;\u200F⁀A|m8 8\u2060\u001B⁻\u2028$\\\n" +
                "J\u2061Y–~⁛⁇B:$j‵1⁶^\u202B•,0S\u000B⁆\u0011‗0\u200B⁃^S;j r‷Y\u2065‰*!N\u0007⁊\u001C⁃u⁅⁃OM*h‧<‶\u0006†‧o(\\\n" +
                "⁉\u001C⁐i⁒ \u001CA;e\u2028h‥\u001B‷⁴0KZ\u000B⁌H⁍~‗“Q/z{‾'\u202E\u000B\u2065⁹5\u000F\u0015\u0015⁛\u000B\u200Em‐\u200BAcep‷r\u202E\f⁾\u206Fd\u0014\f\u0004 R⁝e– Dpaf‹;‾\u0010ⁿ⁾=\\k\u0015⁐V⁎| ⁞_9\bt‥%\u202B\\\u2069‸\u007FX(\u0012⁀@⁇5  _,@s‴`″]\u2062ⁿ=I!\u0006⁀\t⁕(\u200E ^;Dg\u202Dp⁵X\u206F ;IN\u0003⁈\u0003‐*’⁊_i:l\u2068d⁵\n" +
                "\u206E‸6\u001DN\t D‚b⁎⁏_i&)\u2067b\u2068\u0007 ‣\u007F\u0007OK⁇\u000B t⁛⁆\u001Eeo$‡+\u2064\u0011′\u2028yo\u001CG⁓J‐r⁚⁍\u001DOk.‧\"‰\u0013‴\u206Dt!\u0000\u0003⁗G⁞p⁝ Z\u0001A`‣2‿\u001C‱⁸zi$@⁔S⁌< ‗\u000EIQ3‱7\u206CH‰”?yq\u0002 \u000F⁘B⁇\u206FV\n" +
                "\u0018l\u206F/›;\u2067 7e|B⁏n⁊Z―\u2073\u0017\u0003\u000E-•N‼?\u2067 7pcB⁐:“Q ⁾R\u0003Of‹N‼&\u2069\u200Drv<\u0013⁘\"‶R \u202D\u0016\u001F_g‹V⁓r\u2063⁛sm&\u0013⁑?‽\u0015⁃′\u001D\u0019I3‥W⁘5‰⁂xx\"\u001E⁒%‱A⁕\u2062\u000F\u0010K}›\u0005⁆ …⁂`vA\u001E⁕p‴S⁃\u2062\t\u001B1q…\u0003⁝1  )}^\u0003 k‴B\u200F⁷[\u0018-c\u2068\u001F—2⁺ +w^\u0006⁆?⁜W⁚\u2061B\u0007.e•\u001F\u2028?‿\u206B2f@E⁋q⁜P‟‟Z\u0003`,‥\u001A⁼1ⁱ⁻zw\bI⁋:‚P”\u200F\u001F\u0005m-\u206B\\⁵\"‽\u2065jv\u0019\n" +
                " | G⁞ \u0004\u00127-⁋\\\u20725※\u2067k`=E\u202A8⁒R⁔ \u000E@I-⁘W‧5‼\u2029c):\n" +
                "›8⁐P⁐⁚M\tnf‚U‱\"※⁺9d\u000BF\u206A4⁁G⁉⁚N\u0005xc„\\․M\u202D‿-l\u000B\n" +
                "\u20689 ,⁎⁋\u0003LBb⁈J\u2069M•‧#/.\u0017‥9‐m⁎⁂W[Ka⁖\u0019⁸\b\u206E‵%4?\u0004\u206C\u0013‹x“⁜I\u0014\u000Bp ?’I‥\u2064}:\u00018\u2061\u001F\u206A(⁑⁄\u001F[fS⁏?\u202B\b•‡q(\u0003s†Y\u200Bk⁍⁌\u0001Dc\u0007⁅y\u2063\u000E‡‼m!\u0010t\u202B\u001C‐} ⁔\fE0\u0010⁎o\u2073\u0018\u206F‰i!:a‾\u0000”8 ⁙\u0004\u000F\u001A5⁑ ⁿ]\u2060‰j/m\\‥H⁓} ⁕JK\u00048 &‼\t\u2028‾$$s\u0018\u2072O⁈d  J]y{‗=‼\u0005\u2061‱>$Y\u000Fⁿ\\⁈%―⁙WWyx„/\u2068\u0014\u202C\u2061eyY1\u206A\u000F d ―E\u001B<\u0011‘`⁰\n" +
                "※‵$ySb\u206C@ f⁚⁁.\u001D2\u0016 l․\u0015″ Mx\u0012~\u206CL⁓t⁀\u200F+\u0019{\f 5\u2073\u0007‵⁽N9\u000Fb\u2061A⁓o⁜\u200EnXh\u0007⁁6′\u001C⁼⁺\u00061\u001As‸\u001B⁜u‒‟*;{\u001D⁜;‴\u0010′⁽OW\u0012x\u202A^⁐0⁆―.#2\u0010⁏~‸Q•‵LFW~\u206F\u001C⁗#⁌―%(wO⁖(\u2063\u0003‣\u2067\u0005\u0019N{\u2063\u0013⁃d⁖–\u000Fp:[—r‰D‸⁶y\u0015H{\u2064\u001D⁃7⁑—\u0015ph\u0015\u200Bj•S‰\u206DcP\u001Cz\u202B\u001A⁋=‐ \f'rZ⁊t′\u001D⁴\u2068xBR-‣\u0000⁚t‚\u2062\u0019f+H⁂r⁺\u001B\u2068⁂m\u0011Dc⁈4―i⁈‵\u0005~)O\u2068]\u2061I※⁀d\u001AL! $⁁&⁘‣\u0011h>D\u2060\u0004‵I⁸⁋x\u0005\u001E0\u200F$⁂&‖ \u001Dw2\u0010⁸E‱\u0006⁾⁊\u0017\u0000@y\u200C, d⁞‾\u007Fi3Y\u2068E‾\u0016‧ _/\\+⁈1⁖s \u2067*[)Y\u202D\u001D⁶\u0015\u2068―\n" +
                "/A<\u200Dh‘w \u2067b\u0001dt\u2064\u001B‸\u001A\u206E \u0006\u000B\f\u001B‒~⁊\u007F \u2029`b~;\u2073^‧\u0010\u2067⁌\u000E\u0016^I“+⁉t⁇‸cs~-\u2073^\u202B\u0000‡⁍\u000FS\u001AL ;\u200Bo⁏\u206D{;\u007Fl⁷Z\u206C\n" +
                "\u2063\u2067\u001AU\u001BL 2 b⁃ \u007F!x$\u2066V\u2029\u0017″‡\bH\fL⁆7 u⁆⁌xhm+‧^\u2067\u0006′\u206C\f\u0000\b\u000B\u2069;‐u⁂ mk\u0002| I⁴U ⁆8)N9⁒\u0001‽\u001B⁍\u2068\u0018o!K\u2072u⁕~\u206D\u200Eq\u001DR?⁒\u0001‼\u0013 \u202E\u0005u7\u001F‿`⁛}\u2061⁚p\u0011R?⁐\u0006⁑\n" +
                " ※\u00041:Z⁰n‰n\u2029⁎jU_( \u000F⁛\u000B⁇\u206E\t42M․g‴c•⁎}[\u0012%⁍\n" +
                "‚C\u206A‡\n" +
                "{qJ‸c⁾e–⁎\u007Fq\u0012%⁕\u000E\u200B\u000B⁺\u202D\u001E\u0005w\u0005•gⁿe⁚⁙v`We⁗\u0013 \u0017‿\u2066V)#E†r⁹7⁐ v@W6 \u001C‘C‥\u2072\u0013`>[⁰s\u206B0⁌‐\u007F\u0005\u0010Q‵\u001A‟X\u2029\u2062_qx4―|\u206A,⁜‐:Q\u000F[\u2060\u0010\u200E\f\u202E⁵I4f9\u200Cu\u202Ex⁆‐iD\u0014\\ⁿ\u0010⁀\f\u206A‰\u0000*4+‗y‣b\u2060⁓dYQ\u000B⁾\n" +
                " \u0015\u200F…\b=qa‑y‣y\u2066⁕|X\u001FA\u2065\u0016 \u0011\u200F‸Fxp3⁅\u007F⁷1⁸⁗3\u0014\u0014\u0013‧\u001A⁗U‑‱Uqcv⁉n⁷3\u2063⁞8{\n" +
                "\u0002\u2065N‖] ⁾P\u0012y\"―<\u20739\u206E”1\u007F\u001CL\u2061\u001C V‛ⁱU_~)⁁q\u20617⁵‘;8\u0012L′\u0002⁏=″⁷I\u0018d# g\u206FI⁚‚,8\fFⁿ\u0014\u200E=⁺⁽MBa(‘4⁽I\u200F\u200D$&\n" +
                "Q‸U i⁻\u2065A\u0006}0⁈0⁻G⁛‱)ewD\u202D\\„4‸⁃L\u0000\u0019b⁅=⁺\u0014⁛\u202B-n~\u0007‡\u001D―f‾⁙\n" +
                "\u001A\u0011'⁒i\u2067\u000F⁚‼en1J※\u0005\u200E{※⁎\u001CN\\?⁈l\u206DU‛ hD+^※L\u200E \u2069\u206E\u00071X~⁏$\u206FT⁉ f\u0011+\u001B\u202AI 0\u2069\u2068\ta\u000Bv⁏;\u206F\\‐⁈}\u000E+\u001E\u202EM |⁼‧\u000Ez\u000Bj⁆(\u202A\f–⁐H\bn\u000F⁌M\u2068e⁼※;(\u000F{\u206C\u001D \u0004‒⁞O\bj9 r\u206F*ⁱ‱\"([\b \u0017”\n" +
                "„⁗\u0002M#x⁻r\u206Ey⁷‹amK\u0011‖\u0001\u200B\u0015‑―A\u000F>a‶b⁽pⁿ‵5`\u001E\t⁗\u0014‘P’⁚G\u0007q}‣q⁶p\u206E′&sQ\u0014⁗{ \u0011”‒R\u001B0`⁷\u0013\u20641⁵\u20736;_\u0012„t\u200D_—‟ZB\u007F{⁰\u0000\u20681⁰⁺>f\u000B\u0014⁐s H⁞⁚x\ty4‧\u0016\u206C#\u202D⁺\bh\n" +
                "@\u202D~ \u0003⁅‛lHh%⁈\u0010\u2029n․⁰\u0005&\u000F\u0005›u⁈\n" +
                "⁝⁐eI}%⁎\u001D‡y⁽‽\n" +
                "$\u0018K›1 \u0018–⁙*Mlk⁒P\u20658⁽‼\\(\u001Ed‱\" K\u200E⁙8\bv\n" +
                "⁂\u0002\u2067\"\u2060‽\u0018|\u001El‶\" L’⁉p\u0015p\u000B‖U\u20699⁵\u202DPw\u0015+⁸0\u200C]‐⁉p\u0012mH”@⁸W\u2073…\u0005`\f/⁸n⁘\u0003‛⁃%\u0001o[\u200D\u000F‴#\u206C‱Lu\u00065\u206A/⁃L’⁝(UbP⁊J•?\u2060\u2073\b\u0014\u000E<\u206A\"⁇\u001F ‒l4zS\u2060F\u2028?ⁿ\u2073\u001F\u0014\u000E<⁀2⁚^‑ yq|\u001C‴]⁺.⁰⁰\u001C\u0003\\h⁜8⁚G„ yq1\u0001′Y‸+⁻․\u000B\u0014Bu⁞<⁋X⁛⁉bz-\u0019‱[‾=⁑‽\n" +
                "\u001BY9⁙:⁚\u001D″⁘ouyK\u202CT‴t⁝‿O\u001C\u00178⁅0⁑T‵⁖<<\u007F]․T⁽t⁙‿HY\n" +
                "<⁈8 T‿⁐:StY\u2029J⁷z‟‑Ns\u00001⁀9⁗\u0017⁰⁼+\u001Dt\u001D\u2060Q‸`― NoX= '⁝\u000E‵⁾&\n" +
                "xP\u206AI′f⁚’SoX8\u200B-‸\u0002\u2028⁰6\u000BxM⁻\u0003‘O⁇ Sb\u000E( #⁰&‴•%\u0003|A\u206AL C—⁗I`\u00193⁊$\u2062'‴‵,\u0007l]\u206AM‖D⁜⁜B`c( /\u2073%\u202E‽ \f\u001A\u0006․g‖\u0005⁊⁜Ri~&⁊\b\u2062%‹‿ \b\n" +
                "E•(\u200BQ―‟Bmi$⁗[\u206Eq⁼⁹f\u0005\f\u0004″2 Q\u200F‖Flx\u000E⁒^⁽0⁶\u2065c\u000E\u001Dm″3‘\u0010‟\u200B\u0005f|\u0000⁖W‶0⁋\u2063`F\u000Fa‵8⁘T‸⁃\u0017#}\u0004―L‱7⁓\u202AyD]c\u206Cf‑\u007F‶†\u000E%.C\u200F\tⁿ\f⁕⁉dP]e\u2060o f›‽\f93\u0002⁀\n" +
                "‧\u0005 ⁍x\u0019Gj‥*⁅i‾‣\u0013w\"\u0019⁖\n" +
                "\u202A\u000F„⁗{\u0012\u0002i‷m⁏\u0005⁷‹[tp\u0006⁙\u0019\u206Fj‑’3\u001D\u001D*⁹m \u000F‱⁰G~uC‗\n" +
                "‧`⁗⁐3\u0016\u0010e\u2064d⁎\u000E⁷‱Qyc\u0000⁄\f‧}⁗⁐?\u0012\u000Fa\u2068,⁓\u0015′⁚]~n\u0013 B‴5⁝‼}\n" +
                "\u0006v‡/⁁F‴ Q*g\u0018⁅\u000F†c⁇″8M\u000Fl\u2065m⁏\f‽⁚V(|\u001F⁅\u000E\u202Ey⁎‿2\b\u001Ec\u2065z⁆\u001C⁄⁘[c0C‶\u000F•x‡‶7\u001A\u0010+⁓/⁀\u001D⁆⁗Y:gY›[\u2029s‡⁷0TG*⁒>⁌\u0001  Q:.I⁾\u001E‣o\u206D⁾qSC9‛l⁅\n" +
                "\u200E \u001D*IX\u206C\n" +
                "‷o\u202E\u2065{\n" +
                ">0\u200Dy‗\u0007⁋⁅\fkM\u0010⁾\u001C\u2063s•\u202BkK)\u007F r⁍S\u206A⁂\u0018kZ\u0012\u2068\u001E‡s ‷ld9z r⁅\u001A⁻ L\fX\u0014\u2065\u0005‷s\u200F‶\"kxg‑w⁖\u0014\u2068⁚G\u000FX\u0012\u2061W‷z\u200C⁺#`/|⁁# \u001F\u202C B\u0007JP\u2061P‷z⁈\u206E+i-Z 9⁅\t‼⁎B\u001D^z\u2064X‵`⁈ .=2\u001F‐,⁐\u0012※\u200FOSV?⁶E‾s⁗\u20636s3I–+„\u001A‣‐\u0016\u0015F%ⁿ\u000B\u206Dn⁌\u2060a/Lb\u200Fy \u0002\u206C⁔\u0015G`B‾@‼6⁂⁴Y&\u00136„.⁕Q\u202A yR|\u0016\u206AF‰q⁌\u206C\u00101\u0017a⁄cⁱ\u001D†⁌gPaA•\n" +
                "”p⁓\u2062G\u001F\u000F$\u2028|⁸\u0002\u202A⁂ p`@ \u0013‖g \u202DFP\u0001`⁻{ⁿ\u0017\u202A⁋3<m@—\u001D a⁏\u202DF[\b%\u2067=‽\u0004…⁃!{jJ _⁘`\u202C‰N\u0016\u000F=\u2062:\u202A\u0005\u200C⁙ 6{U \u001A\u2067`\u2068‰TS\t'\u2066t \u0001 „t\u0012|C\u200F\u0011\u206Cf\u2063‾\u0019g\u001F+ p \u0013‐⁛}m}R\u200F\u0003\u2069|\u2064\u2028]\u0002\u001Br\u206E#\u200E\u000E ⁉)\"s\u0007 F\u202Eh\u2060‽\tO\u0012i\u20292⁜\u0011 ⁓noc\u0006 A\u202Bx\u2064\u2073\u000F\u0018\u0002\u007F\u20296⁂\f\u200C⁹n8j\u001A⁅_‡e⁼\u200D\u000BJJ{‣+⁄\u0017⁜\u2065f'c[⁅B‶b\u2028⁅\u001FH\u0016{‶#⁁B⁀\u202Crhd\u001A⁚O\u202E5\u2029⁂\u0015H\u0005v‵!⁉?⁀\u202C5<m\u0013―V\u2028K‥⁞\u0015P\u0004x⁰v⁉k⁕‱g k\u0011 \u0013\u2065K‡⁙\u0002NKh\u206Cc⁅8⁀\u202E\"&\"\u0005⁌\u0017\u202DJ ⁛EN\u0002q․r‧\"⁊‷,-m\u0001⁐\u0017⁕Q\u206A⁐YC\u001Eh‷\u007F‡\"⁆⁰-+{\u0006‗\u0017⁄\u0002‱‑^\u000B\u001Ds⁻{\u2064m⁗‱6bq\u0016 [ \u0003″‑B\n" +
                "\u00146⁻>\u2064\t⁁⁾7bp\u0016–W )‵\u200BE\f\u0015r″'\u2060G⁞\u202B$fqR⁛B⁀4‿⁅OF\u0010!⁻1‵P⁛†!*i\u0001‚B―$″⁏TM\u0001\u000B\u206E*⁰\u0004⁛†8(r+ K—$‷⁅L\b\u001BE…?⁼A‗′-|~7 \u001F”4\u2073⁛H\u0012\u001DR\u202Al⁵[  !|zr⁝\u0005 3…⁃@\t\u001D\u001A\u2029`\u20739‗ⁱ`8$\"”j Q\u2072‟@PA\u0002\u206E\u000B\u206A:⁜‿48$l⁎r O⁼⁌UO\u0004\n" +
                "\u206E\u001E\u206C)’\u202E:.p- k E‹⁁\\\u000E\u0013E\u2061\u0007\u20647⁜ |yz1 ' =‴⁊\u0010\u0010\u0019^⁹S\u2060O—•\u007Fc|,‐= o⁻⁔\u001A\u0011\\E\u2064\u0013‧\u001B–‱ht|2 ` z″⁜\u0001\u0010\u0018^\u2060M\u2066\u001D⁖‸!gw3 #\u206Cp‿ I\u0013W[\u2060U P⁝›,}w:⁀?\u206C'‸⁉_]\u0004S‴K I \u2069*-$:⁚kⁱ!›⁉HBS\u001A\u202D\u0002 I‚\u2028h.:n⁙n\u2060C⁸⁇\u0011\u000E[\f‶\u001B—e\u200C ek>,⁏~⁵\u0017ⁿ\u200F\n" +
                "\u0007Z\f…\u0010⁕\u007F‚⁽*c(d⁕>⁵\u0013\u2073 ^\u000FMD‷Q\u200C3 \u206A,j,)⁞?\u206B9⁷ X\u0002\fO\u202CV\u200CQ ‣9lho⁄?\u20688\u206D⁄\u0019\u0004\u0001\u001C\u2064W\u200DY \u2064{duk 2\u2063y\u2061 \tD\u0017\u0019\u2064S‐\n" +
                "‒‡h77p s⁸h‘⁖\tD\u0017\u0004⁰\n" +
                "‑\u0006ⁿ⁶}+7c x\u2063i ⁖\u000FBP\u000BⁱX \u0007⁼‹/*5y⁑9\u2064e⁜⁍GO\u0015\u000E‾T \u000B⁼‽2;dg⁐3\u200Fe’⁏\u0012Z\u0016\n" +
                "‣\u0013⁽\n" +
                "\u206C‡vz~e⁎3“b  \u0015\u0015\u0013\u0003‡A\u2068\u000B\u2066\u20665}zn   \u007F\u200E @\u001A\u0012N\u2072H\u2061_⁹\u20683\u0010p\" -⁁(‐“[0\u0016P\u206EJ\u2029\\‰⁴>Ba5 , |⁑‘Rf\u0015]\u2067\fⁱ\u0015‼⁽r\u0005z+ ~‘{⁛⁝\u001Al\u0017\u000B⁷\u000E‸\u001A\u2028⁽w\u0019te⁽o⁋:⁘‒\u0004j\u001D\u0007‑\n" +
                "\u206B[\u202B′m\f=t⁹o⁋/⁃⁝\u0018kU\u0000⁙\u0007\u202E]\u2063‼j\u0006& ›h⁛1 “\u0001eCP‚\u001C″T‧⁾t\t/5\u206Eo‹;⁁‘TdFX⁀O⁍S․⁶t\u0010.=\u2060'\u2028?⁍―\u001B`ZX‒\u0007⁘S\u202C⁻o\u0005>x\u2073'\u206Ae\u200C‐\u0006iQX‑H \u0007\u202C⁹hI05⁾&\u2060\n" +
                "⁘‑\n" +
                "$\u0010A‛T‒b‾⁸n\u0004v-⁺'⁺B ‖\n" +
                "$\u0002E‟\u0007‘-‾\u2062*Sg+\u206B'⁹A⁒⁂^<GF S‚)‥\u202D1Xic⁾;ⁿG ⁙Y=\u001B\u0006⁞L„4\u200F‸yJth›)\u206CR⁺⁔Y9\u001C\u0007⁎\t 4⁚‵yZtn•m\u001AG⁺⁔\u000B7T\t⁍\u0004t ⁚‡{\u0017!y\u206Dq\u0004*\u2028⁈\u001C\u007FUY‘\u0001$C⁆‼s_!1⁽!E*‴“\u0012\u007FIT‑H&E⁄\u2068w\n" +
                "i#⁸<Na‥⁈\u0014l\u0004F ]n\b⁋\u2068}\u0018wc\u20642\u001Dm⁁ \bk\u0003C\u200CSk\b\u2061\u2063g\u0007o,⁻6\u000F( ‗Gr\u001F\f‚Xk\b⁼ⁿ\"\u0000z,\u206D9\u0018(” No\u000E\f _8I\u206D \"\u000E{\u007F\u2067\u007F^; ⁂(z\u0013\u001A⁇\u000F?I⁶※\b\tv{″|\u001F+ ⁏(hV\f⁜\u0011~E‣\u202BG\u001F8,‵\u007F^1⁋⁎goJC⁙\u001A~A\u202A‼\u0013O%%⁹n\u0016$†⁔|:V@⁙\u001De@⁄‱\u0012V/`\u202Ai\u00042‰⁔vvD\t⁉\u0002m\\⁗⁴\u0003\u0006bh\u2069b\u0018/․⁔fh\u0006H‚\n" +
                "wZ⁐‽\b\u000F&<\u2072o\u000EP‴⁔ldR\u001C”\u001Ai8⁀‱\u001ED=z‽i\u0001W‷⁔zbT\u000E”\u0007n#‗‽\u0014B2|\u2072i\u001A\u0003⁸⁛4)[\u0018 Ink”•\u0014M2|\u206F=N\u0002\u2069\u2028u$\\\b⁏O'a ⁜UJ3|\u206F&IA\u2067\u202E:$G\\ @i*\u200E⁊I\u0004.(†!\u0000D⁺\u206A<jZA⁌\u0001t,‟⁊L\u00056( bTX\u206A‸\"`R\"⁇\u0007&x„⁍P\u000E7F\u2067oC\n" +
                "‾•%z\u0017/⁇\u000B,b⁊ V\u000Fg_\u2028xIB\u202B\u206C/{\u000F6⁆\u001Fi*⁊“_\u001EdS•?\u001DE\u206A⁴:ln=⁍]r!–⁔Y\n" +
                "\u001CX‾}\u0005I\u2072†yyt=„\n" +
                "w&„⁅\n" +
                "Y\u0007\\\u2067-\u0003_\u206E\u202Ci8k|‗_l3\u200B\u200C\u001B]\n" +
                "\u001F\u20636\u0003]\u202B⁸s8s\u0015\u200DSu8⁙l\u20672$|\u2063 \u0001W‷L—FK\f–Eaw⁀>⁽2\"f⁴iA\u0007‡L ^[F‖\f\"c⁔?\u206C~3#‶{C\u0010⁴L’\u0018UF⁄\u0012-w⁾*\u206Bw8c‧`L\u001A\u200E\u0004⁋?]C⁃\t(:\u2060k‿\u001F6-\u202C~\bM \n" +
                "⁋?^L⁈^a,\u206Co\u206BW7!\u2068.\u000EY„a \"C\u0001“Fg*‾\u0012⁰P&`ⁱc\bL„` 2D\t \u000B&l⁜\u0015ⁱ\u00120d\u2067+E\u0019\u202E|„gCD–C,w⁉\\\u2069\u00060b\u2067+M\u0003⁃+ o\\\u0007⁇C(#‴J\u2072O8h\u202E-O\u0003⁇%⁒.\u0018\u001C⁁Y.o\u202B\\\u2072Jqz‧<\\\n" +
                "⁅(⁒'\u0014\u0017⁈N%*\u202DI‶\u0007w{\u2029<LL⁄,⁒\n" +
                "\u001E\u000F⁚Y *\u2064E‼-vc\u2029yMC !‐\n" +
                "\u0002\f \n" +
                "%&\u202AQⁿblx\u2029zMC⁘4 \n" +
                "\tX⁈\u0016 ,\u202B@ⁿll4‼\u001CE]⁞!–L\u0018[“k74\u202AH⁽+82\u2068KS[⁝&⁓\u000BqF⁈<2(ⁱ\u0006※nQ(‧K\u0012Z—g⁗\u0007+M⁃g28ⁱ\u0004‶rX(⁉\bT\u0018 l \u0001xG‽`1j‥\u0005‱f\u0011#⁘\u000EEJ⁑m⁐\u00121K‽.-+‵M‣gU/⁘@AR―)⁆\u0004<K‽$d&⁺\t‥kQ.‷f\u0016C‟)⁀)>A⁜\u00156\"\u206B\t‐E_/‹d\u0016G\u2029cⁿ.qL⁖\t6v‚i‗A\u001C)⁶hX\u0012›\u000B\u2072&uG⁖\u001C0w‚o‛G\u0007>⁶h_\u0013⁻\u0016‵MNJ⁖\u0000>w⁛~⁔=>/‸aZW \u0016‵I\u001EB⁗\u00134>⁁q―(jf‣{Q\u001E\u200C\u0018⁻A\u0019\u0012⁑\u0002}>\u2065~⁛ wk‥j\u0014P t\u2028OW\u0005⁀\bd<\u206D\u0001⁛o4j‵b\u0000\u001C\u200Fb⁻\u001CU\u0003⁑Bts \n" +
                "‚l%c‿l~:⁛*\u206D\n" +
                "VC⁑\t\u001FH‷S⁍h:&‧lqh &‣\fHC⁃@Q\t‱B a&e‷(4)\u2063'\u2060\u000ET\u0001⁄\bpL–F‒z9b\u202A|\\F\u2064.⁷\b\\B⁽\u001525‐A’(+-\u200F~WQ‼d\u206D@NT \t2#⁙D 2/3⁈`\\D⁹0\u2061W%P†\u000156 \u0010\u200E\"Qp⁏g\u0015B\u2062u\u202EA$\u0012…\u0004y'‑U⁏/@2⁁v\u0016R\u2061<‡H`F\u2029\u0013{r R <\b# p\u001E\u001C⁼ \u20646gE\u2029\u0004vy⁜H Z\u000Ba⁆t\u0006\u0016 !ⁱ?+\u0015\u202E\u0011&t⁆F⁑KNy⁋fE\u0006‣#‿gn\u0010‥F5t⁆S⁞\u0015\u000Fb⁌)[T†<\u202C\u001F{\n" +
                "\u2029\t\u000F#⁏\u001C\u2061v\u0015\u007F⁝l|\u0003 }―\u0013;_ \u0005\u0012p\u2073\u0012⁻3L>⁹%x\u0005 c⁛G-U‐K\u001F%\u2068\n" +
                "\u2028g]9ⁱ(z/ b \b3\\⁑G\u001C\u000F⁵\n" +
                "\u206D(^5‵#pj⁕x _-\u0015⁂K\u0015\u0004⁵\f⁵0\n" +
                "a‧$ah‐, Xf\f L\u0004H⁻B\u2067/ln⁾lw!“*–\u0003L\f\u200B\u0018WI⁽N″m)z\u206Ejw:\u200D!⁘\bGZ‚\u0005[\u001A\u206E@‵mg/⁴`#j\u200B#⁁\b\u0003C\u200D@J\u0004ⁿL⁋|k&\u202D2%k‒f\u206B3\u0005C\u200D]CK\u2066\u000E\u200E^%4\u206C.e*⁆i\u2067,I\u0014‛F\n" +
                "G\u2066\u0001 \f&r\u206F#bg‖`ⁱ\u007FC\u0016⁏J\fm\u2062\b—_ y‽8a\t\u200Dz\u2067q\u00001⁘\u0018\u0005`\u2069Z \u001Et\u0011″vj\u0017⁉2\u206ClT\u007F⁒\u001B\u000F;\u2069P’\u0018t\u0017‷;bU\u200C'‹l\u001Cv⁃1\u0011=\u2069\u0007⁎\u0003n\u001D…U1T '›k\u000B=\u2060<R \u206EH⁔KOX‐] T -›?dx⁀/E'⁶@⁛]\b\u0001T\\,I―%⁑5m!<=Hi\u2066J‼P\u0019HQX;I―/⁙>9 4*\u001B>⁼[‱\u001EVIXS;V”5⁕mv(67\u001B5⁼G‧\u0014\u001FFQ\u0017z?\u200F7⁆zq## \tW\u206A\u0017\u202E\u001B\u0015\u0003P\u206Cb2⁊z⁋x}f> \u0001S…Z‡\u0017\u001FFQ\u206B!<⁈? xyc% D\u001C…P⁷\u001D\u0015N/⁴6u⁒9’z5#N‗^\u001C‼\\\u206AT\u0015p&\u2072~k⁝/⁊55\u0012I„\u001AF‱@‥^\\|.‾}/⁃, ~3\u001A\u000E \u001F@‶X \n" +
                "D\u007F`\u202Bcm⁅=⁵o*S@⁜\n" +
                "\u0019\u202D\u001D \u0007C0+⁼fx⁄o\u202D'\"\u0010M\u200E\u0003\u001B \u0003⁈C\u0002v,\u206Dc7\u200Ff…'\"\u0005[ \u0005C‣l⁇SJi>⁰l  \u0001\u2028%/\u0004[„\u0018S\u202D!\u2069\u0005Aa)\u206Cw$\u200DR b3\n" +
                "L‘WW\u206C!\u2062N\u0013h!⁺;2 \u0001\u200D(3\u001CI‟1x⁴o\u2064GA<\bⁱE\u0011⁙< ?dpm‐\"b‼\u0010‡H\u0005\u0003M\u2067M\u0017⁒t ;`u(―,{\u2072\u0000\u2068V\u0005\u0006\b\u2067C\u000E“b\u2062\"me(‐\"g\u206F\u0016⁂M\u000BE@⁵PG `‧?j), |g\u206A\u0015⁔KJ]E\u2061\u0014\u0013 l⁴.$20 |3\u2072\u0003⁔LV[^\u2061\\\\ w⁴8>>T‒4=⁷\u0012‘QP['\u2061\u0014R‑2⁰4\"{O b!‿\u0012‧]L\b;\u2067\f\u0001⁗s⁃}(dH\u200Baj′\u0017\u2063\u0015M\u0016h\u206D\u0013\u0005 7‗}(\u001C\u001E d|ⁿQ⁾\u000F[h>\u2065\u000B\u0011‚? /4\u000E\u001E‖nt\u2073Q\u206D\u000F\\kl‸N<‖q d9\u001CL⁌&Y‶\u0003\u2063\u0005Js\"\u2062\u0006\u0010⁂#—b9y@ aq‷PⁱBV\u001F`\u2073\t\u0014‗1 /9l\u0010‛lc\u2072\u0011\u206AI\u0019\u0004\u007F⁸\u0007\u0006\u200B<\u200C |h\u001B\u200B'g\u2065X\u202CC\u0013\u0004\u007F\u202BE\u0006‑0 Irj\u001B\u200B&i⁼]\u202A'\u001B\u001Ef\u202BN\u0000‗8⁙\u0007zp\u0002\u200B)a⁹]\u202Bc\u0016Pd\u2067L\u0000‗p⁆\n" +
                "x4\u0001 \"a\u2064\u0003\u2066}\u0010]f\u206B(\u0012\u200Cc⁆\u0010q3\u0007\u200CMv\u202C\u0017\u20290\u0012Ru⁾4V⁍u⁆Ecr\u0002‗@>\u206D\u001D‣7MRJ\u2072`Z n⁏^&7.⁒\u000E?\u2065\u001C‣'\u0006VB‾\u0004H q⁆I*v#⁐`h\u206F\u0002‶,I\u001FB‼\f\u0011⁏v⁞Iic-⁉fv\u206F\u0017‰-I\u0013_\u202C\u0012\u0002‖7 C,`q\u200C[v‶@‾0\f\u0001\u001D⁻:\u000F⁅J⁊Xi!j—Wj\u202Bc\u206A9\u0007EJ⁵5\u0005⁝\u0006⁊Xk)j ]`⁽\u007F‥-\u0005NJ\u206E3\u0005\u200ES Zm!j’Vw\u206BYⁱ2\b\u0001\u0007⁶%\u0003⁋;‘Uguf‒\u0005f S⁽'\u0002\u001B\u0012\u2061%\t⁉s Og;B W}‰_\u2029;\u000F^f\u2073 \u001C⁜3⁆Lj,\u0015⁹Oz⁼@\u202A#\n" +
                "M{ eZ (⁏\u0003l \u001A⁾\u0000/⁺\b‼s\u0005Ei⁞dA„(⁒\u001Cv \u001B\u202DI.\u206B\\\u2072s\u0010\u0000n⁃&\\‟4”B$\n" +
                "_⁺\u001Eh―P\u2072:]$\u007F‸k\u001C‵$‚S.\u0004\u000F⁙\u0019h⁜G\u206F?Ov/‾p\u001A‰g ^9\u0013\u000F⁖\u0019w‐\u0013\u2060;\u0019zf…k\u0012\u2063` TwZ\t⁀dp \t\u20673W7c′\u0001P\u2062h T2E\f⁇rp‖\u0000\u2068:\u0012(e‴\u0006^‶O Yw\b\u0012⁜e0‖;\u206E<\u000E(f‽\u0016C\u2073_\u2064U`\b\u0012⁕se‐0‖'\tl}‧S\u0016⁸U‶@h\u001A\u0018 ;\u007F―u⁗`\u0019oq\u2064P_\u2066\u001C″\u0005u\u0000\u001F p8 }⁝c\u0010 h\u206B\u0019[\u2062w\u202E\u0006uM\n" +
                "\u200F9/\u200DW⁞o\u0010?n\u206A\u0019]\u20640‶\u001B0V\u0000„v}\u200CY⁛;Q8b‾\u0010\u0012⁾y›\u001B<W\u000F⁛~c⁞\u0011⁛\u007F\u001C1c‷\u0012\u0006›\u001B″\u0016q\u0011\u0014⁞cn‚y w\u0012z4\u202A\u0006\u001C\u2068\u0016\u202DY2.\\⁏&u\u200Cs⁌yZO8\u206FC\u0003\u2069\u001D\u206C\u001A( K“&g⁉u i\bM\"\u2072Bm‽\u001D\u2064\u001D(>J‗f\u0000⁔z\u200Ci\b\\/‷\u0007n⁴\u001B\u206B\fc(\u000F⁘dN s\u200E,7@`\u202D\u0003&⁴S⁞C[)\u0003⁈-\u0006†;‿7w\tj‼'q⁁H‟C\u0005|\u000F‐\u0007\u0006†;‿5`\u000Ev‰rh⁌R⁔P\fwX‐!\u001C‥>‸|,\u001F=‰Bs⁋J⁑\u0012YzY‐6\u001C\u206B,‴w5Z8‰Fy Y⁘\u001ET(2⁅(\u001C\u2069*‱p1[A\u2069\bk C⁒\u0018\u00113 \u200D(\n" +
                "\u2064\"†8|ZX\u2068L-‑R Q\u0012z1“lL\u2062r⁷4~\u0016\u0011⁽\u001FF \u001D @\u0017zx cj\u202Aj\u206C%y\u001F\u000E\u206C\u0011J⁙\u0002 \u0005\u000E~}⁌p$†u\u2061`|\u001B]•\u0015E⁒U \t\u00115W⁶} \u2072:⁽dtGw \u0018R U–D\u0003&\u0004…yr\u206C4⁽bmGi⁃\u001DR‣\u202D‿\u0010\u0004\"\u0007\u206F=3 ⁀⁚}cGu\u2065RU‣‴′\u0018C\u000E\u001B\u200B7' \u2064⁓j7w;\u206AYC‣\u200C‼\u0006S\u0012I⁊6% ⁿ⁓k629‥EQ‣\u200C‼K__I⁊7%⁂\u2062⁈A>1-\u206AE@ \u200D‼$\u001EEE\u200B1`⁸\u2064⁒Wj*+\u202BY\u0001“⁄‽9\u0006S\u000B⁊ya⁵\u2029”Pf6j\u206A\u0016\u0007⁕⁀\u2069#FX\u000B„eu‰\u206E\u2063fc5b\u2073\u0000\u001B⁄\u200F‑\u001FC]\u0017 h;‴\u206E\u2062l&97\u206F\u001E^⁆⁎‖\u0004C\u0019P”q+‶\u206E⁹fei5\u2072\u0001G⁓⁎\u200B\t\u0010\u0007Q⁸u/‶\u206E\u2068dqn#\u200BUN⁅⁎“\f\u0014\u0017\u0003⁸49\u2065›⁴i4uo’WR⁅⁕ \fF\u0014\u0003⁵$r\u202A″•m(4J‛J\u0017⁘–\u2072\fZ@3‑'r‵ⁱ‗~z!C\u2061U\u001D⁔‒ⁿ\u0017\u0014Fm⁁\u001A ‖\u2060‖rzc\u001A†i․⁷⁀⁺\u0013\b\u0004\u007F\u200CI⁆ ′‖j(i\u001E\u2062i‱\u206B⁆⁾`IIj \u0000⁒ \u2066‐\u0005*\"J\u206Bn‶† ‰cEC8‘\u000B‚ \u206F⁅\u000E*1W\u206Dx‶†\u200D‷{^P;⁍\u001E⁗⁃\u2068’[\u0017>\u001B‾n‾‷\u200D‹4q4s⁗\u001D„⁑\u2062⁋Y\u0018P\u0012‵q⁻ⁱ ※)}1`⁔\u001F‘—‣⁓L]Y\u0001‰?⁹‴⁀‶>)8h⁞\u001F‚⁜‡⁄S\tW\u000E⁾r⁻′⁏‡!']F‛R–⁓\u202B @\u0007)4\u20721⁸\u2073⁄\u2067`uLG‗E\u200C‟\u202D \u0007U$.\u2064aⁿ\u206F⁈\u206As4GB \u0012  …⁊\u001B]4f\u206F}\u202C\u2065\u202C‽s4W\u000E⁏\n" +
                "⁍‖\u200C⁞\u0006F>d›y‡\u206F\u202C›o5_\u0016⁗\u0010⁏ 8⁓\u0001\u0015,y›u\u206F\u2061V‷bsE\u0017⁛\u0017  v⁀\u0003\n" +
                "i\u001D‸fⁱ\u206D\u0019‵pc\u0010=⁛\u000F  u⁜\n" +
                "\u0006t\u0013⁻F\u2073․\u0002‽y&\u00153“# ⁐w⁏\u001C\u0006f[⁵@\u2068⁼W…z&\u00075\u200C/ ’w⁎\u001BB\n" +
                "F⁸F\u206A⁵W›s-x!‐2⁊“9‚\u0000X\u001BI‰F \u206ET\u2069,xv ⁗.⁛⁎< Z\u001DVR′M›•P\u206D>=7<‒(⁓⁅8’[XYH⁺\u0005⁙…]⁷/-+1⁚k‶⁄1‒BLE\u0011‵\n" +
                "⁐‡C⁻,+ay⁜~⁰⁒-\u200EJM\u0003\u0016․P⁐ D\u206099lx\u202E8‱\u2061b–\\\\\u0002X\u2061‡\u2073–\u000B⁶2|r=–⁉‒\u2063x⁖S\\\u0016R\u2069\u202C⁼⁃\f‿>9ar ⁂⁜•`⁒QJ\u0015R\u2061‱⁼⁏\u0001‼(@l7 ⁃\u200F\u2061!⁴M`\n" +
                "R\u206C‷  D‑=\fsr ⁅⁎\u2072*‱IeS\u001A\u2061\u2028\u2062⁒K -E=u― ‑‽'›A<\u001D\u0017⁰\u206B⁰⁈T KTx7   \u2068=‱?&\u0011P\u2072\u206F\u2067⁈_⁈\u001FRy5⁒\u200C …+›~!\n" +
                "\u0015‰\u2069⁼⁑N \u0010\u0001B\u200C\u2072‛―‴ Fe!7⁾‐⁺⁻⁑*+\u0004OY‛\u2062⁚‚‿N\u000Bl&*※‒\u2028\u2073⁅+&\n" +
                "OM⁓\u2066⁍ \\X\u0006z'4†\u200F‼⁴9v&7RW⁈ ⁑‛K\u0013\u0006^&w‿⁎•‑)va?S\u0004⁚\u206E⁍⁷\t\u0017AL6g\u2028\u200B‹‛p7))Z\u0003 \u2069⁜⁷\u0019RO‽5q\u2028’‹ q3?⁎\u0015\u001F⁇\u206D’\u2060\u0007VQ\u206Et?‥ ⁵ f0}\u2064\u0019Z⁗\u206D’⁰FQ]\u200Cv*′y\u206D‘'%}⁃\u206Fh⁀\u0010 ⁶‾V]″ \u0004\u2029b\u2061― :}⁜\u2072p⁁\n" +
                " ⁺‧C]\u202B–\u0003⁋e\u206A\u200E 38⁙⁵c\u2028\u0011⁄\u202E⁔\\U‼ \u000B⁁\u007F‣\u200E‽2u⁔\u2068x\u2061\u0019⁂\u206D⁘\u0012\u0006‡\u200F\u001F j‶ ‼2o⁕ v⁶\u0018⁓⁻⁕A\u001B‼⁍\u001A\u200F6⁙‽‧$~“\u2028X\u2060Y′⁎ E\n" +
                "‼⁸4 7⁗›‧ H⁓‗_ T‸⁗ \u0011}⁙⁖1⁋t⁙‰\u2066x\u0013⁵⁶A\u202E\u0006‱⁑‖\u000B3“ d⁙g⁂ⁱ⁸bG‼\u2067\u0012‼\t\u2062 ‖\n" +
                "5⁈\u200F}⁘c‚⁽‶y]\u2029⁻] \u0002\u2069⁷⁁\u000B4⁝\u200F8⁁\" ’\u2061e]\u202E ^†A\u2065‵⁁\u0007(⁚\u200F-⁉,―⁙‸'A‴⁻H‥@⁼‾⁝I\"⁑⁕h\u20675 „‼=\u0002‰※\u0011⁇G\u2069\u206A⁙7j⁕‛y…#⁉„‱RJ‴\u206B\t⁃B※ⁿ 1/— o\u2063 ⁞‖‱V\u000F⁵․\u001F R\u202D⁹ v{”⁅k…+⁂\u200Cⁿ\u0015\u0014\u2068\u2029\u000F\u202C_‣\u2060—5`  c⁊\u007F⁐\u200F⁹P\b\u2068⁾C″\u0010‥ ‚?} ‚e⁐x⁀⁎\u206E\u001F\t\u206CⁿE․\u001D\u202C\u202B”|{ ‚+ |⁂⁏‽\u001B\u001E⁽‐C\u206D\u0011\u2062\u202E⁑tp‘‾e›x\u200C⁝‥\u001B\u001E‸⁖\u0004⁞X\u2062‸⁓~l‘※a›=⁂⁌※\u001BL\u206B⁖\u0000⁖Q‧‿⁏;)\u200D‰o․% ⁋†1_\u2068⁂\u0006⁂\\‧‿⁈X,⁈‥s‧/⁔ \u20681B\u202C⁀\u0016⁃\u0003⁴ⁱ T0⁉\u2060d•p⁔‟\u206FtG\u2028’D⁍\u0016⁴⁻ \u001D)⁏‹7•8⁔›⁴\u0017]‧⁐D U※⁗‑y) ‟⁝⁀'⁒′ⁿYN\u206B⁾″‣B‶‒„-n ‗⁀ 5⁄⁻\u206DYC⁴⁶‴\u2060]\u2068⁛„84⁔ ⁜ )⁈′\u206A2C‵ⁱ⁼\u206FL\u2029⁀ Ke⁐”’’)⁇\u2060\u206E>\u000B‴\u206F⁼⁽\u0005\u2067  Z+⁑’―’` ⁵\u206C#\u000B‵⁼⁶⁰\u0004\u206C‑⁌Wb―\u200F ‑}⁌⁸•]\u0016⁽\u206A•⁃\u0018 ‗⁐9a⁝\u202E⁇″y⁝\u2063‽\\\u000F\u2029\u200E′⁝\n" +
                "‴\u200F”(g⁌\u202E\u2066\u202Af—⁂⁴F\u0012‸⁋― .⁵‶‑ca⁙‸‟\u2065X‐⁄‿C)‼‘\u206B 7⁻\u2064⁞eJ⁔⁹ ⁸\u0017‒ ⁾\u0011\"‱⁙ⁱ’z⁷\u202A\u200C~U‑‸ ‹-„⁄ⁿ\n" +
                ":ⁿ—•⁘\n" +
                "⁽\u202B zV‚„⁍‾-\u200D⁇\u206B\u00193\u2069‾\u202C⁉L⁴\u2069⁋X\u0013‚⁓⁍‥ ⁘⁉‸9}⁾\u202A\u2060⁍A‱※⁝]] ⁅\u200D\u202C/‑⁌‵2}⁾\u202Aⁿ⁇J⁵⁆⁜\\] ⁂‚\u2067$‐‾\u2028|>ⁿ†\u2073 H⁵„⁜\u0013\u001E\u2028⁉”⁷<‚⁰⁼b\u007F⁛\u2069ⁿ‒H\u206D―’\f_  ‚ⁿc⁍⁁ⁱi\u007F⁈\u2068\u2068–i›\u2028 \u0001_\u202C ‚⁸I⁒⁉\u206Cs\u007F⁛\u2068\u2069⁘:※‽‘\u001A\u0011‼⁈ ‵W⁞⁙ⁱ{a⁙․⁹―5※‱‘\u0015\u0005⁷\u202E\u202D⁽P‛ ⁽mq⁗⁃⁂‐5⁵\u202B⁝\fQ‿\u202A…⁵Z ⁘ⁱ,6⁍⁃⁈‑3\u206E‿⁑_F\u2028…\u202B⁹\u001F⁎⁞•\u007F)⁎ ⁘‖r\u202B⁔⁏\u0010G‽\u2072\u202A⁹\u0007⁘⁴•q$⁕‛⁄“'\u202A ⁌\u001FM※⁼\u2064\u206BN⁞\u2069‣j9‛–\u200D f⁾\u200B⁖\u0018J\u206F″\u206B⁵\t–\u202B•p/\u2065⁑ ‒)\u2067⁎⁎\u0015\\ ‣\u2067⁷G⁇ ›5(\u206E⁆⁇‒)‣\u200F⁕S\b‚\u202E•′[⁌\u2060‸}(⁓⁚ ⁅:‿⁀⁙]F‼″ⁱ†\u001A⁋\u2028‸)c⁏⁖ \u202Au‥⁍!ZC※″\u2060⁞\u001D •Oz& ⁔ ⁾|\u206B⁆o\u0018T‶‧ⁱ‒\u0019\u200F\u2066\u001Bp1‖⁏‐⁻k  oPE⁾\u202A‰’\n" +
                "⁌\u206CO?#⁞⁅⁞⁼–‿\u2066!Z@‵\u206B⁾\u2028⁻⁚⁆i;4⁐⁋‖⁉‟⁺‵\u001DZF․\u202E\u2072\u2067―※⁆=/5⁑⁏„⁋‵⁏\u202EX\u000FS‰\u202C⁻\u206B⁚\u2029\u200E\u001Df>⁑⁂\u200E\u200E‶ ⁉r\n" +
                "Z•‶\u206B\u2067⁘‥\u2069\u0006f? \u2073  ‵⁜⁉i\u00005⁶‛\u2060•\u2065‹…\u0019lP⁚※ ⁃ ’⁀u\n" +
                "#′⁞\u206C\u2063\u206E⁷\u2060\u0001f\u0003⁆‶ ⁃”—‒b\u0003m\u2068‖⁝\u202B⁸\u2066⁷Bt\b‚\u2073⁽⁃‑― '\u0007\u0002\u2072‖\u200F…‱⁴\u206AC'v‚\u2073⁽⁃‑― ,I\u0011› ―…‱⁴\u2072H t⁔\u2064⁰ ‑†‚-\u0000\u0018‽‐ \u2064⁴ \u2069Ln|⁄‽\u206C ”\u2072\u200C(b\u000B\u202B⁐\u200D\u206B‽―\u206D^\u0007+⁊⁰⁾‚⁈⁰\u200C5'D\u202C⁐–\u2073…‗\u2060PCb⁊‵\u2072  ⁶\u200E4e\u0006‣⁆―⁴⁵ †\u0014\"i⁏•\u2066 ‐\u206B⁎\u001EU\b‼ ‒\u2068⁵⁋‼{;m⁛\u2063⁶\u200D⁕\u202A⁒\u001F\u001B\u000F› ”⁾‹⁃‶zi/⁍\u2068\u2072⁞⁖\u202D⁕\u001FE\u000F‡ “‹⁶⁌′pO'⁉\u2068\u206B’‚‣⁜\u0017oF\u202E ⁇‹⁴⁌‾x\u000B?\u200E⁶′⁐ \u2029„\n" +
                "nR\u206B‛⁐‵\u2072⁌⁺#Br ⁺‴―‐\u2029‟Mf\u001D\u206D‟—⁺⁶‣\u206B%\u0003= ⁺⁵„‟⁍\u200C\u0005aT\u2066\u200F ⁻\u206C\u206D\u2063eA \u200E\u206A‧\u202B\u200D‟‗\u001Cm\u0000\u206F ⁊⁄⁾\u206B‷s\u0003 \u200E……‡ \u200E⁛StI⁺⁎ \u2063\u2061\u2069⁑\u0011\u0006&\u200E…\u2063‑⁁ ‸|uC\u2062⁀⁏‱†\u206F⁜\\\u0001+ \u202E\u206F⁙⁁\u200B⁼9oL\u2066⁉ ‽\u2061\u2062‒\u0019\f#–‧⁾⁘–⁏\u2060|zLⁿ⁒ ‱⁼‡ \u000E\u0003F„‱⁾⁘ ⁈⁵gc5′‑‖‹\u206E\u2068‗\u0002\u0006[‒\u2072⁹⁗ \u200D⁺le?′ ‖⁷\u206E\u2068‛\u0018\n" +
                "\u0013‒\u2067⁸–⁎ ⁺|\u0007~\u206B—\u200C⁶‼\u2069―\tt\u0012‒‴\u2069   \u2065l\u00102\u2073⁚\u200D‥※\u2061‖\n" +
                "`B‖※ⁿ⁀ ⁏‶Y\b'‶⁋\u200D ‸‽⁗4aB⁅\u206B\u2062⁉′⁉‿QA\u0016′ ⁂ ⁛‧⁊%$a‒⁌‣⁰‾ ‼DV\b⁷\u2028 ‖⁌\u2068⁑b2i\u200E ⁷⁹\u206C\u200C‰\u001B\u001EI\u206C⁽ ⁙‘\u2064⁕i{i‛“⁰⁓\u2029⁒⁵XBQ ‖„‼⁇‷⁕1,q⁘⁾⁷  ‗‒^@\u0015\u202B ‒‶⁁‷\u2065?35⁅\u2065\u2066‖‵  \u001FCG\u202C\u200B ⁿ⁅‾\u206C?%.⁋⁾⁷‚\u206B„․Z/Y\u202A\u200D⁗\u206E ⁻ *]0⁇\u206C※⁎⁷ \u2065C)_‵⁀‛›‟\u206C⁅&H-⁙\u2029⁾⁉\u206B⁌‡C.D‵⁌\u200C\u2069 \u202A 7F!―“\u206D‛⁰⁓\u0018DcQ\u2060\u206E \u206F ⁽\u0012\u0005\u000F=⁀”ⁱ\u200D⁺‘epjS‴‽‒ⁿ–⁵\u0000\u0003J2⁓⁜⁻‑\u2060  w\"W\u2073\u200C‚\u2063—⁸\fWC;‟\u202C\u206E‑ⁱ’o?&I⁶⁉”‽⁻⁸\fKUi’ ‽⁎‚‚e?4\u000E⁼ ”…ⁿ\u2068\u0000L]k\u200F ‽⁂‚„i-)\u0002\u2060⁁⁎\u206E›\u206D\u0019_Hl \u2061\u202A ⁈ z+$\u0015‧\u200E \u2073⁂\u2067\u001C\u000BL|⁔\u202E\u202B‖‣ tf\"\u001B⁺\u200E⁸⁹⁎\u2061\u0003\u000EGi‟\u202E‗\u200B\u206E\u200Ewc\"\u001B‿⁆\u2072\u202B’\u206F\u0004CQo⁖\u202A„\u200B⁸ m54O‷⁄⁺ ‐\u2062\u0019V\\&⁙‣⁚\u2069⁹‑953H\u202A⁓″‛‘\u2072PP@r ‣⁖\u2069⁰– #`\u0001\u2065⁎″„‘⁶RF@e ‷⁜⁰⁼⁖&.%o\u2073⁒‽⁜⁜‣HJ@\u001D⁓…⁕‹⁼⁓:%4x‰⁒‼⁖‒\u2073UC\u0014\u0010⁙‡“‰⁽ 0*s~⁹⁑⁽⁉‐\u2060C^\u0016\f ⁽⁝‹⁵‒+?c\u007F ‘\u202B⁜‛\u0006XPC\u0016⁴‸⁜‽\u2068&73 w ⁑″⁓ J[J\u0000\u0005\u2072‼⁜…⁻/?⁞ik⁒⁏″⁋„\u000FW‷\n" +
                "\u0002‼\u2028⁃‧ⁿl2‽bl“\u2067†⁂„\u0002[⁜B\u0005\u2068—⁅\u202E⁸,Q\u200B+k‛\u2060\u202A⁀d_q\u206FB\n" +
                "\u206B ⁘‡\u00032Q‘#y⁋\u206B‷⁏pF#ⁱ@\n" +
                "\u202E\u200F’\u206F8#\u0003‒/x⁂\u206B‹ ]Uc\u2060%\u000B‧\u200E’⁵50C Dh⁂\u202E⁶–\u0015w,\u206A \u001B‶⁋‟⁽5\u0000E„Ht⁃‿‿“\u0015p$⁷&\u0012‶⁓‟ⁱ|\bP Tw‖‼⁹⁑\u0019a?⁶=\u0018⁸⁏⁗⁛P\u0015\u001F \\k⁘\u202E⁷‷5tq‡\u0016\u000E ⁇  \u0015\u0012\u0010⁂s\"\u200F‰\u206D\u202B}2q\u2062\u0014P\u206A⁑’\u200B\u001BG\u000B‘mp\u200B․\u206B\u206Et+n‸\u0002\u0016 ⁓  \u0000NN⁐e\u007F\u2073\u2073\u2062\u2069bn/⁰\u0016\u0012‒‟\u200E⁉\u0005\u0001N sw′⁽\u206B\u2028wa⁚\u2065S\u0014⁞‘”⁍\u0005A‼ 0q\u2072‸⁼‣dd⁅\u2061D{ ⁗‑⁆\f\u000B′⁁-\u0015\u2069′\u2063‣f\u007F⁞‸\n" +
                "q\u200C⁁–⁊\u0005\u001E‼⁔h]\u202C‶⁺‾m>⁝⁴\u00034⁂⁒⁚⁑\u000B\u001E\u202E‑m]\u202E‷⁺•fr⁂⁸\u00038⁝⁄⁰⁋\bR‶‐c\u0018‱\u202B„\u202C(&⁞⁹\n" +
                "8 ⁄\u206D⁉\u0004\u0006‰“lJⁿ‰ \u202C$e⁞⁸L%’‐\u2072⁄M\u0006‶⁘-\u0005\u2069ⁱ‛‶Gi⁐⁸^u\u200C‒\u206F⁗$\u0005‵\u200B~\u0002\u206D\u2061⁏‧Aw⁖\u2063\u001Bc⁃⁁ ⁓d\u0005″‐~\u000E‡\u202D\u2063‷Dq⁛⁵^h⁀⁎ ‗+\u0017⁻—~\u001B\u2028\u202B\u2063\u2067\u0007\u001D‚⁺\u001A;⁜⁃ ⁇qr\u2073’\u007F\u0017⁼‷\u2069\u2028]R‛⁸\u001B7”‗‚⁀87\u206B⁕w^⁶\u2072›‱MV ‼\u0003'⁘⁒⁽⁞!2⁴⁈cN‶\u2072 ‿R8‐\u202D\u000F'⁀‗⁸⁖<_‰⁅cT\u2060\u2062\u200B‣]3‐″\u0006:\u200F\u200F\u2064⁖.\u0013ⁱ⁇r[\u206C\u2064⁄‣^|‟\u2067\u00063 ⁄†⁌=\b\u206D\u200EhV⁺⁎⁏\u202A\u001D| \u206BH\u0006‛‼※⁓ \u001D\u206B⁋)r\u206F⁝⁘‸\u2029n \u206BL\n" +
                "\u200E›‿⁝⁛\u000F⁰\u200E(*\u206F⁔⁛⁽\u202Bj ⁸MX“‱⁻ ⁃\u000B⁶⁘,Rⁿ⁙‒\u2065‧+ ‰C'–‽′\u200D⁆]\u2060‐!B⁶⁓‒\u206C․1 ‰U-⁖†⁷  E\u206D⁂:X‱⁈⁗\u2060⁰i⁍‣T<‑‱′—⁐\u0003‸⁐ 6\u2061⁝⁓\u2061‣j⁚‼E\u0016 ″‼—⁄\u0002⁺⁈*6\u2062⁚⁐⁸\u2064m—\u202D\n" +
                "A\u200B\u202E‸⁘ \u0003‴⁌c ⁹⁃⁝‼‥a⁑\u2029\u000FI‗․⁽⁈⁍\u0000‥‣`=ⁿ⁁\u200F\u2068‽a⁊⁓\fX⁓\u2061\u2063\u200D⁎\u0012\u206A‿i.‶\u200D⁎\u2065\u202Bs\u200E⁚\n" +
                "\u000E⁂\u2065 \u200B\u200B\u001C\u2060‿~k\u202E  \u202B\u2066u ⁗\n" +
                "K⁌\u2066‣  \u001E\u2062‹\u0000\"•⁆⁁…‧w‖‗ j⁇\u2066‶⁇⁔W⁷⁵U\u0019\u202E ⁑\u2067‖>‐⁕\u0017k⁁⁼‹ \u2064\u0012‰‽rK‶”⁊• w⁞⁒\u0007%⁕⁴․⁅ \u0003‶‷'A‼‗⁐․⁾l⁄⁄O(⁌‷‿⁂⁞\u0018\u202C‡ox\u202D⁅⁋※\u20728⁄⁄O\u000F⁌‶\u206B ‗U‥\u202A+c•⁑⁋\u202Bⁿ0 ⁃F\u000E⁇‵•⁊\u200BU\u200F†)`․⁙⁗‹\u2062:\u2061 F\u0006 \u2029′⁘ _⁁⁷/r\u206C ⁷\u202D\u2073>′„N^⁌\u2061‒\u200D _⁁‾/:›\u200Eⁱ\u206C⁰6 ⁙%\\⁈\u206B— ‟[\u200F‶C|※‛ⁱ\u206D⁼3‣‖%\u000E⁞⁾― ‑\u0013⁌⁰\u0005z‶‛‵⁒\u2063v‿ )Z⁐\u2069⁐‷ \u0019⁒‣F<⁰ ‣⁄\u2062t‰⁏?\u0010⁺\u206E⁑‡ \u0010 •\u001F\u007F“⁎‥⁉\u2068a‸⁊kS‼…⁀\u2069‟\u0000⁋\u206A\b!⁅⁏\u202E\u200E‿h′’|D‷…⁍\u206F⁓\u0004⁋‹\b,⁖⁒⁇‛※d\u206B⁋mZ‹‾′\u206F⁒\u000B \u206B\u0005;⁝„⁐ ‷a‥ `O ⁿ\u2029\u206F⁓⁵⁄\u2067\u0004o⁎–⁅⁏‧”\u202D—$\u0006†″‷\u202E⁗⁴⁉„Ti⁌⁊⁄⁗※‘\u2028⁼=\n" +
                "\u206C‹‴′⁞⁻⁀⁜Jf ⁚⁜‒\u2029‚″⁼+F⁶‵\u202E\u2066 ⁵⁕⁜[' ⁚⁊‟\u2029‚″⁼/O\u2061⁺•⁾\u206F\u2068⁖’%*‣―⁍―“⁈‷\u206D\u0005z⁏⁴‣⁰\u2068\u2068⁒ j\u0015․⁚⁀‟ ⁈\u2063‘`w⁍\u202E‵⁾\u2069\u2068‐\u206C\u0019\u001B\u2028\u200E⁚‘⁉“⁸ 9t⁚\u206F\u202E⁷※\u206F⁘\u2066_T\u202E ⁋⁗\u206B\u200E\u202A‒&x\u200E\u2066‥″⁋\u206B⁜⁷HX\u206D ⁋⁇\u202A ′‒,R‣\u206C‼‴⁚\u2067⁓⁹\f%⁌„⁘⁇\u2060⁇‾‖~@\u206C⁐‽‰–‷⁛⁷\u0015`‛‿⁏⁔\u2060‛⁻„{\u0004⁾⁚\u202B⁸⁀\u206F–ⁿ\u0015$‟‴⁒\u2072‐\u200E\u2061\u200Bl\u0004\u2072⁑‿‐⁵⁼⁁⁼\u0003q„‵‟⁾‚\u200E\u202C”o\u001D\u2067―\u206A\u200Dⁿ\u202E⁅\u2073Oo ⁴ \u202D–⁇‣‖dO⁃‚\u2062\u200D\u2072\u202B⁏‶\u0010'…‐―\u2065‛⁇\u202A‚0K⁃\u2063\u2061⁅⁴\u2029⁏›C#\u202C‖\u200D‡⁔⁋\u202A‚*M\u200C⁷\u2063⁘⁴ ⁅\u206FH9\u202C‖‐⁸ ⁀\u2065‛ \\\u200C\u2064⁵’\u206C\u2029‑\u2062\u0000+\u2064\u200D‖ⁱ\u2066\u206E⁾\u200EbX‐\u2068ⁿ‟\u007F”⁞⁽\u0012=\u2073 ‐\u206A\f‽‽‑sM \u2073ⁱ‚,⁞⁒\u2067\u0016?\u2062‗⁝›N※›\u200Ex[⁂ⁿ‴⁉n⁓ \u206F\u001C{\u202D‑‾‽\u0006‶ⁿ‛y\u0017⁈\u2062⁝⁏e⁓‑※\n" +
                "\u007F\u202D‐‸\u206F\b′\u2063⁘a\u001A⁉‰⁌ m‒ ‶\u0005v\u202C⁃‿‧\u000E⁽\u206A⁃h\u0018 \u2063⁐⁁.  …f]\u202A‑‱′G\u2068\u206C \u0007/⁇\u2068%⁀(‟⁌\u2067d[•‚\u0005′G\u2068\u206C \u0002{⁑⁵i⁛#⁅ \u2067m\u0010‸‛\u000E⁻N†\u206E⁇\u001Ay⁌\u2073\u0004„6⁐“•i\n" +
                "‥“j\u2072S‣\u206F (y⁌⁽\u001E‛0  \u2063K\u001C‿⁑>\u206CX\u206C\u2029‐<}⁒ⁱK“x‘⁆‰H\u0015‷⁑8\u2069\n" +
                "⁾‧⁓-\u001F⁘‷\u0018”f‛ †Nm‽⁒v‽\u0007⁵\u2063 8\f⁓※\u0005⁕f‑⁏†Le\u2073⁙`⁵\u0014⁴‿⁌-\u0006‖‽@‗m⁔⁐‸Ee\u2064⁎`\u2072\u0015‵″⁌)\u001C\u206E‽\t‟|⁙⁒‾\u0007<›⁕l‿\u0018\u202C‾⁒'N⁒\u202C\u0018⁗u⁅⁝\u2072S<″⁁h⁷\u001A‣⁽ ;Y–′\u0007‛~⁊‘⁴H⁀″⁐h⁴\n" +
                "‹‒‒'′⁞‵\f⁔~⁑⁷′E⁓‽⁞k…\u0011․’⁖a‧⁒⁾,⁉}⁀\u206A•\u0004⁎‼g_\u2069\u001F\u202C\u200F⁃p‧⁒\u0000\u007F‟p⁅\u206C…^\u202D‐a\u0019⁰\u0002†⁌⁒6⁈‰)x g ․″R\u2068⁀[\u0017\u2067\u0002\u2065⁀⁖6⁈…4a⁇v\u200D\u2029․B‱ G\u0004․\u0019\u2063⁍⁗n‑\u2073)g⁋w‗‿‸\u0002⁽‒K\u000B\u202E}\u2072⁇⁛n“ⁿ*\u007F⁇\u0012“‴⁻\u0001⁺ X\u001E†w‼⁃„s‟ⁿ:l⁅\u0016⁗\u202A⁰\u0014‿‐O\u0018\u2065p‥⁅”4⁗ⁱ#~\u206F\u0004⁍†‽D′„S\u0012 $․⁎”0⁚⁻s`\u2065K⁉\u2060‽b′„S\u0013 ' ⁍⁎\u0003⁆⁷ u\u2069B⁋\u206D‽k‣‒PX +† ”\n" +
                "⁂ⁱ5x\u206AE\u202A⁼⁵h\u2062 V\n" +
                "\u200F ⁄⁐⁕\t\u200C\u2066v~\u2067E\u2064․‰{⁾\u200F\u0010\u0007\u200E+  ⁀\u0014 \u206Af'\u2061M‣⁰\u2028q\u2029 \u0013U >⁊‑⁆Q⁈⁝~, \\ ⁹ ?\u202C⁽\u0017X…|⁘“⁝Z\u200C x7 \u0011\u202Dⁿ‵z⁸\u2066XU\u20631⁏‐⁇\u0014”⁜x7 B…⁴•g‱⁼\f_\u2063f⁕”⁅\u000F⁅⁜e-\u2069\u0003‣⁸\u202B/‱‴\u0000\n" +
                "”k⁌\u200D⁌G⁅—ok‽,‣\u2061\u20284‱ⁱ\u0006\u0005”\\⁑\u200E⁌A⁒—f%⁻9‰⁼\u206C ‼⁰FD―^⁕\u200E⁌A⁉ ))⁴*‼\u206D\u202D-‥⁽\u0007#‼O“‚⁌^ “i\u0003⁓-⁶ⁿ *‥\u2073\u000F#※L \u200D⁊N „`Q⁞l\u2061\u2062․=ⁱⁿ\u000E%⁾\u0018  ⁊7—‖zM‛j\u2029⁆‿E⁵\u2065\u0013,※\u0005⁛\u2066⁺$ ‑r_⁒b⁷⁆ M\u2068\u2072\u0017\u007F‥\n" +
                "‒\u2028\u2029\u0002\u200B‗v\u0011⁌k′ ⁈q\u202B⁶\u00021※\n" +
                "⁀⁕‿\u0018 „\"^⁕o\u2060›⁙8\u202B⁶G-‰O‰⁕\u202E]⁙ gD⁄o⁇‴⁝}‾\u2060\t!‶\u000E\u202B⁘․] —)Q⁓o⁈‽ *‶\u2060Aq‧\u0007\u202D‷\u206B^⁞ 3_ E⁘⁃⁋)‶\u2064G\u007F⁰$\u202B\u2063‸]⁄ )\u0018―\u0004⁜ ⁋}‰\u206DHl‵a‰⁶‣\u0012⁅  L\u2072\u000E⁜‒⁐c†\u2063NF o ′‸\u0002⁔ *c\u2064\u0001⁋‒⁜g‧⁶C\u0010 a\u206B⁰‥G⁂ &f⁸\u0003 —⁜k\u2062\u2061J\u0012‐lⁱ\u2073‴K ‗/`\u2069L―‒⁍A\u2066⁹K@ l\u2061⁺•4―‘%$\u2028\u0018 ‗⁇G‵⁹\u0005@⁉d․‷\u2028)― i!‽\u0002⁋⁅⁅Z‹\u2029\u0006O”v‣†\u2065.⁜⁅e<⁾\u0004⁆⁅\u200B\u0002⁖\u202C\u000B\u001C‐d‱‶⁻e…⁉yo‼D⁘⁘⁛\u0007⁉…\u0012\u001C‐b‰‱\u2028'‽⁎ws\u2062\u000B⁕⁂ P⁘‼\u0012S‐n″‷⁼5‼‐2 ⁽\u000F⁀ ’Q‐‚@I’c‣\u202A⁵4⁴‶`!⁼\u000F⁇  D⁔⁂\u000F\u0001 g•\u202A\u2067!›‧}`\u2064G⁅⁋”D‚⁁\u0012\u0012⁄3\u202D\u202E‽4\u2073‵{t‱_\u200D⁜⁈V‑⁜\b\u001C※+\u2065‽‼v\u2065‴ma‛\\ ⁏⁙\u2062\u200C⁚M\u0012\u206B5⁴\u202A⁹\u200D\u206A⁺,~ \u0015 ⁂‐⁾⁆⁚D\u0017⁴5\u2069\u202C⁶‒″‿*t‑\u0015 ⁉ ⁷⁁‵Y\u0011⁴x\u2062\u202D† \u202E―>e‛\u000F⁂⁁⁅⁰⁝※\u001E$⁷x‣‸‶⁐\u2029⁓{V‒X⁔⁝⁄‵ ‵\t3\u20610⁴‹‱⁅\u206C⁆)D Y ⁐ •\u2066′Fb\u2062<†‣›⁆–⁑#\u0006⁂^⁙ ⁒ ⁾ⁿ\u0003G\u2062:‸⁺\u2072⁁‛ c5⁂J⁙  ․ⁿ\u2029\u0014]‧$⁹⁺ⁱ⁍‚⁚4<⁉@\u2073 ‐ ⁵\u202EQI※3⁓\u2068\u2073⁛“⁀6i⁎]‷\u200D ⁻⁴\u2029EI\u202A4⁅\u2068\u2062\u200F”⁆+: C†‚  \u2073\u2029_\u001Aⁿ-⁍⁻⁴⁄‖⁍U+⁇\n" +
                "⁼⁂⁌⁰“ ,\u000B″a’\u2062‘‘\u2073⁚Ke⁇A⁉\u200D⁴ⁱ‐‿aE\u200F$\u2069⁺― ‰⁋\t  G ‗⁸\u2063⁞ lR\u200F(\u2060‷’⁃\u2028⁎\u001F& [ ⁖⁽\u202C ‷?G⁷6ⁱ⁺⁝⁍‱‗J)–S ” •⁄⁹.\t⁽6⁷\u206A⁀⁐ ⁙Ao⁝U‘ ″†⁆\u202B \u001B′'\u206B\u200E⁗⁅•⁂Cz⁆B\u200F\u202E‣\u202A ‶+\u001F\u2066-⁹⁋⁑⁞\u206A⁄Dh⁆B‟\u206B‥‶\u200F\u2064\u0017\u001C‧6⁺⁅ \u2062\u2067 7^⁕Y\u200E\u202D\u2060‐\u200F\u206EX:⁹S\u2067⁙–‰\u2061\u200F5_⁙$ \u202A″⁃—ⁿE0\u202AA\u2062 ⁇\u202C‴” \u001E \u0015 \u206F‵⁉—\u206AEl\u206F5\u206B ⁆…‴”-\u0005“E\u200Eⁱ‣⁂‾\u206EYj\u206E,\u206B  \u202D⁘⁎8J‚I’⁰\u206A⁏‴\u202B\u0018(⁵&\u2072⁜⁊\u202E—⁈wE C“‸‣⁛⁹\u2068\u0018#‥\"⁰⁔  ‑\u200D8K⁀P―‧\u206A⁊\u2062‡2$…p\u2062⁏ \u2029  uK⁊\u0014‑※\u2066⁀\u2064‡\u0002*‹4\u2065⁓ \u2060 ⁔vB⁖F⁅′\u206D ‥‣\u001E+‵.\u2065⁑ ⁶⁆⁖rJ⁁K ⁛\u2067‚‧‸\u0016/′?\u2068‵ ⁶⁞‘~J⁀Z⁈⁔\u206C‒⁾\u206C\u0016/′?\u2066⁴‥\u2066⁞‛w\\‒^⁆‖⁊ ‵※\u00005\u20666\u2029\u2063‾\u2029⁔‛t\\‒Z⁌⁍‴⁹‱⁴\u00040⁷z‾\u2028⁒“⁃ dT⁗\u000E⁑ ※\u2068\u206F…\n" +
                "2⁷o‥\u2028⁚   -A„\u0002⁕⁄‣․\u2062⁹\n" +
                "\u0015⁖G⁵ \u206C\u206B\u2029⁗-W‣3⁕\u2069 \u200E‣‼C2⁔\u0013›\u200F•⁽⁖ +\u0012†{⁓\u2061⁅\u200E⁶‰E~⁙[‧ ‷\u2061 ⁗-^ :⁀⁼⁒⁁ⁱ•@1⁚H″⁒\u2072\u200F—⁋4Y‿:‹…‚\u206A‴ F6⁋R⁜⁔\u2072 ⁛\u206DcX․ ⁼ ›⁀⁻ ,\u0017\u206F\u0000\u200B\u2061⁉\u2060‚\u200F_f\u200Dj\u206E ‽⁀\u206E\u2067>\u0012\u202D\u000B ⁻” “ W|⁌y⁹ⁱ⁍⁎\u206E⁷.\\‡\u001C—–\u2028‼⁎ A)⁍x‴⁾⁍⁒›\u2069.G\u206D\u0011⁒⁞‹› ‛Kg‚p‡⁾⁘‚\u2028⁺2G⁵\u0016\u202B‟\u202E⁵⁁„[)‒6⁂\u206B ⁿ ⁰{@\u2066E\u2062‘\u2065“\u2067„\u001F`\u200B,\u200C\u206D‑⁹⁇\u206Aw\u0005\u202Bb\u206D’⁴⁙‵ \u0004`\u200B\u0016 ‹―⁹⁓⁷d\u000Eⁱo\u202C’⁅“‼ \bk⁻\u0018⁉\u206B†‼⁐\u2062i\u001B‒v\u202E⁋⁕⁌⁰ \u0007\u007F′\u0012⁁‼※\u206C’\u206D'\u000B⁚w\u2028⁎‛“⁵\u200CDn\u2029W⁉†ⁿ‼ \u2064+\u001B⁝>‧⁇⁵⁝\u2072⁄_s‸\u001E⁓\u2028 \u202E⁒\u202B9S⁌v‶⁁⁷\u200E․⁄P0\u2029\u0005‖\u2028’\u202E⁅\u202ApU⁏e⁹⁚\u206D\u200E‱⁅P1‽\n" +
                "\u200E‴⁍⁺⁙†p\\⁜n\u206A⁑‣–‷⁇z>‰\u000B\u200B‥⁊⁽⁐\u2067\fQ⁙h\u206E ‾―‱–,2‸\u0005\u200B‥⁘\u2067⁞⁾\fF⁐`\u202B⁖※―※‛fh⁰4⁃″‱⁹⁒\u206F\u0012\u0004―\u0014‰⁒ ”\u202B⁂za⁼c⁕‶ⁿ\u206A⁄ \u001B\u000F⁜\u000B‴⁒ „‱⁝uj‸+⁖†‶⁹⁙\u2029U\u001A⁑E‽\u200C‖‘‷⁍_r‴7”\u2061⁹\u206D⁃‥\u007F\u0005⁕D‽\u200E  \u202D⁌\u0011f⁵%⁓\u206A\u2029⁻⁅‹a\u0012“K‴⁊⁅‒\u202E⁜Ac⁴*⁀\u206A\u202A⁴\u200E‽d\u000F―D․\u200F⁎⁾\u2068⁔\u0017g※b\u2061⁹\u202B‐⁈‛\u200E%⁉\u000B ‗2\u2063\u2068\u2073\u206BD‿r․ⁱS \u200D⁓“%⁌R⁂”&\u2073\u2065‶⁸\u000B\u206C\u001A‧‽Q‒‖‖\u200Bf‘n⁎⁓6‘\u2060\u2073⁹\u001F‸\u001D›‡Wⁱ ‛\u200D?⁑s‚⁉> ‧⁸\u2065^‸\u0001‶\u2069V\u206B⁔⁘―1⁏b⁄\u200F# ⁴※⁽T‼\u0016\u2064⁼T\u2062‘⁗—:⁛6 ‒0\u2068\u2069•⁽L‾D\u206C⁼W⁈ ⁑⁝8⁖+’‛?\u2068\u2060‴⁽O″Y⁼※L“ ⁚’&⁝>⁜⁎<‼⁵‵‹R‵[⁼ O⁏—⁀⁕&―4‚\u200F.⁅\u2063‡‣C※\u0014⁎\u2067K\u2065 ⁀⁑(‖| \u200E9 \u2063\u2060‶A\u2064\u0010\u200F\u206C\\\u2068 \u200E⁒d″y\u2061‟( \u2064\u202E›\u0005⁗Y ⁺O\u2072 \u200E⁙w\u202E0\u206D”E”ⁿ⁺⁹\u206F⁽G \u2073 ‼ \u2029\u200E –\"‥⁓s⁋‶⁇\u206B‧\n" +
                "\u0002⁄‽\u0017\u206B⁅′\u200F⁃ol\u2028⁄7‘\u202D⁗ ″\u0006\u000F⁃‡S‸⁘‧‥⁒&g…⁀%⁁⁸\u2069⁀‥U\u0017⁃‡N\u2061“ ‣⁑<x\u202D⁀<‘‼\u2061⁍‵\u001C\u001E⁁‵Rⁿ“ ‹―}j\u2061⁁:‚‼⁻⁚\u2067\u0018\u000F\u200F\u206F\u001A⁓⁈ⁱ\u2029–jz\u206C :—‧”⁍\u2060\u001E\u001F \u206A‣\u2067 \u2073•–{?\u2064 ⁇⁇\u2065“⁗⁽\u0018Z ․\u2028‡ ‧⁷ p?†⁒⁇⁈\u2060⁂⁗\u206A\u001FQ⁔※\u2029‽ …⁝ q4\u202C⁔⁛⁜\u2067⁊․\u202DQ}⁂⁴›⁼\u200B‿⁇⁄5] ‛⁗’\u2065⁋\u2067–\\3⁜\u206F‸⁷⁅\u202D \u20662W⁼‛⁐‖‱\u200D\u2060 8 ”\u2068⁰\u2065⁙\u2062―⁷QN⁺⁈ \u200C\u202D ‵ 9+⁚‧\u2073\u2064⁈⁸⁆‣XE‾ ‘\u200D\u202B– ⁍?a⁖\u206E\u206B\u202D⁃⁶⁊‡\u001F\u0017‿  ⁈\u202D …⁘\u0015v⁘\u2060\u206E…⁞⁶ \u202C}\u0013⁸‒‛⁈‹⁖\u2069⁊]{‑\u2061※\u202B⁑‷ ‸s[⁅ ⁞\u200B‹⁘\u2072⁊\u001A9\u2029\u206C⁾ⁿ⁑‱“\u202D:X⁋ \u200B\u200B⁛⁅⁴⁈\u001A\f‼\u206C\u202B⁆′\u202B ‼\u007F\u007F“․⁊′⁗\u200B⁶⁝\f_\u2072⁋‾‒‣\u2063‗\u2029,0“\u202E„\u2065⁂‐‷⁆N\\⁵⁉⁻ \u2062\u2064⁘\u2066/? ⁃‽\u2073  ⁸ mP\u206E\u2028⁎⁓\u2066⁵⁘⁓\u00011 ⁍›\u2073 ‷‷‼j\u001F\u2063•⁗⁓′\u200E‽⁝Jo ⁐‣ⁿ‒\u206C⁈\u2029cO\u206D‾ \u200B⁺ \u2068⁊\f!’⁌\u2062⁹ ‥⁈‾b@\u206D\u206C\u200B\u200D‣⁒\u2029⁍B) “\u2064⁾⁐※⁋‡'\t⁴\u2073⁄‟…⁔•⁅-e‛‚\u202A⁶⁈″ \u202CCK※⁍⁃ †⁚\u206C\u200C7#⁒‿‷⁻ \u2029 \u206FXM‶⁌‗‚\u206E⁐\u2029‟*(⁂\u2029⁹⁹\u200B⁰⁞⁾Y\b‣⁅\u200E‘\u2072 ⁔\u200B7c⁆…\u206B\u206B \u2062…\u2072\u0019C    \u2065 ⁉ je\u2062\u2065⁰⁶ ⁴‰‧\u0005\u0003⁂ ―‗⁶⁔⁑⁉d#‴\u206A⁻\u2073‟‷‥†\u0012F⁚\u200F  ″‽⁄ v#\u2029\u2066⁺\u2065–⁉\u202B†\u001DJ⁅ ⁖⁅\u2067…\u200B⁔r8‱ⁿ․†⁋ ⁿ※RK⁜„⁗⁈\u206B\u2060„⁘78⁼⁷‹\u2068“ \u206A‰\u0017Y⁜ ⁕\u200D⁸\u206E\u200F⁘v4‱\u2061‧‡\u2072”\u206A‽\u001BQ⁕⁁⁓⁎⁒⁻ ⁒lq‡\u2029‡‡‧“\u206E\u2072\u0018\u0019⁄ ⁖⁉⁈⁰\u200B⁒\u007Fk\u202B⁼…\u2069‧‖\u202B•\u001A\u0004⁛‐⁃\u2063⁋ⁿ⁀⁇:a‵‰…\u200F\u202E“‴‵S\u0002―⁓⁓⁽⁜⁹⁚⁁\u007F\"\u2061…‡–‵‗‽\u2061\u0010L  ⁄\u2065⁐⁹” w-\u206D\u2068‷‑⁰‖\u2073\u2065\u206E^⁍‟⁞⁽““‚\u200B‚1\u206D⁾⁾‚\u206E⁵⁷\u206A⁹X ’⁒›”‖ \u200F‘5\u206A⁷‵‚ⁱ\u2063\u206B\u206E\u206C\\ ⁙―⁛‟ ⁋‗ (\u2029\u202D⁽‾‿⁵\u202A⁰\u206C\b⁝⁅“⁊‵‚⁄―⁌n‸\u2029\u2068\u206A⁂⁻‷‵\u202D\u0000‘⁈ ’‶ ⁖⁖⁙,‸‽\u2064⁽ ⁻″‵\u202DI⁜” ‐‰\u200F⁚⁚⁃i\u202B⁵\u2068\u2073⁘ ‹‵‶\u0005⁏ⁿ ‖⁸⁜⁎⁜⁂c‧‚\u206E‶„\u202E‡‱\u2062\f⁉ⁿ⁎⁙⁼⁄⁄⁒‖,‽‐\u206E‸‒\u202B‰›\u2073^”⁼ ⁓⁷\u200B⁄⁒‖~⁻‐\u2066‾‒\u202B\u202B‴‶\u001Fⁱ\u2072 ⁑\u2065⁇⁊⁙⁆1⁑…\u2062․‖\u206B\u206A‸′\u0011‾⁈  ⁻  ⁝⁜a„‟\u206E\u206A ⁰\u2068″E\u0012‾⁷\u200F„⁺―\u200C–2s⁍⁗\u2061ⁱ\u200E‟⁸\u2066@\u001D\u2028″⁁‐\u2069⁾‑ 3i ⁴\u202E⁼\u200D\u200D\u2065\u206DZ\u0007\u2028―⁚⁜\u206C\u2061 ⁁za⁝\u2061⁶⁼ \u200F\u2029‵\u0012\u0004⁽ ’‒⁷⁽⁈⁇k(⁝\u2063⁾\u2073„–※″dj‴ ⁞‱\u206C⁼⁏⁛\u0001\u0018‘․\u202A⁙ ⁜‟›sl\u2061  ‸\u2067‸‿⁎\u001B\t⁁⁜\u2062⁗‒ ⁗›;Y\u202E‰\u200B‴⁷\u2064⁷⁛U=\u200E⁑ⁿ— ‑—″_P\u2061‼‚⁺⁰\u2062‴⁛6#⁁⁔ⁿ‛ ‖—\u202CSM‵⁴‐\u206E⁶‶\u2060⁃s9⁝‑‰ ’⁘  \n" +
                "\u0015⁽⁵⁕⁰⁰‼\u2060⁋*}‘ ‰ ’ \u206A․D]\u206C\u206F⁕․\u206A‼‘⁁!3⁀⁏…⁋ ⁙‸…TR′\u202B⁏\u202A\u2068⁹⁗⁀t&⁀⁞※⁂⁈‘‹․TU‡‰⁒‶‱‸⁐⁊t4 ⁇‽⁄⁝⁜⁚‥\u0012\u0014\u206D\u202E⁘‷\u2073⁼‛⁋v4—⁋\u202C‗ —⁾\u206B\u0000Q\u2066′\u200C⁹\u2062\u206C ⁋i?―⁆\u206D‗‖⁌\u2062\u202EIH⁴‵⁍⁶\u2062\u206C\u200D⁀,h ⁜‹„⁂‘\u2065‥&\u0018\u2066″⁉\u2072‧‸ ⁇Im‒–‡‛⁊—․…'\t′\u2072⁍⁷\u206A\u2060⁌⁇S)⁅–‾⁗’ ‥‣sF‣″⁹‸⁵\u2065⁖⁗\u0016/⁍– ⁝‐ ″″\u001C[•″\u2062‴⁽\u2028⁇⁜<9⁇–‖⁆ ⁍\u2069⁼}M\u2067\u2067⁾\u2029⁻\u2028⁉‑\u0012    ⁚⁛⁀†\u20622S\u2067\u206A⁸‿ \u2060⁌\u200DS'\u200F ‖⁘\u200F\u200F\u202A \u0011N\u2068‣⁔\u202A\u2060⁻⁂\u2062en\u200B⁋‵⁄ „…⁂\f\u0000ⁿ․―‥\u2063ⁱ⁔‣xi‐⁊‹  ‟‰ :\u0000⁷\u206A⁻⁷\u206D\u206B⁘\u2066H  \u200F„‚ \u200F⁒‒'\u0000⁰\u2060\u2069ⁿ⁺ ‧\u2062\u000B ‑\u200E⁉‖—⁙⁎\u200ChI\u2073\u2062\u202C›‴‿\u202B\u206D\u001A%‖‑ ‚⁄⁍⁄’\u007FF\u2062⁾\u202D‶\u2064‾‰⁸\u0011\"\u200B‐⁊‖ ⁗⁛”\u001BC\u202B\u2062‥⁵\u2063⁷›⁺z*⁅‑⁑⁕‗‟ ⁚\u0012E‷⁵‴…‷⁰‹⁺S6⁞—‘ ⁖„⁝⁚\u0014Y′⁰\u206B\u2072″⁷″⁶40⁜⁐‘ ⁚ ⁖⁼[V⁼‸ⁱⁱ⁺\u206A‥–77 ⁑„‟⁖⁊⁍⁺D\u0017\u2060‴\u2072\u206F› ‾ *r–⁇⁞⁏⁛⁁⁚\u2029^\u001A⁶\u2067›†\u202E‣\u202E *r‗–‚⁈⁛⁍⁉ K\u0010⁸\u2066\u206E\u2068″․›‣=u ‟⁎\u200D⁋⁍⁉⁗X\u001B\u2069⁺\u2062\u202D‸\u2028\u202C›=\u007F⁉‖\u200B⁆⁝  ⁕P\u001A\u2069\u2065\u2062\u2028‴⁻\u202B‰\":\u200C\u200B ⁀⁕― ⁕P\u0016 \u2068\u2060‰‴⁷″‰ptⁿ⁈—⁘⁑⁗⁞⁕\u0002\u0011 ‸⁻ ‴‥⁾›b1‷⁑ \u200F⁂⁊‗⁙\u0007\u0011⁘‷\u2028⁸‰ ⁴′n\u007F‿‗⁜‐⁕‥ ⁆\u001C\n" +
                "⁜\u2063\u2029\u2062‰ \u2068†<i‵―⁀\u200E⁙ⁿ ⁔U\u0006⁛※⁊⁇\u202D ⁾‵&&‾⁍ \u2029\u200D ‑⁆UO⁜‡⁊ \u202D⁎\u2065\u20668 ‱⁄․ⁱ⁞\u2062⁅‒W\u0000⁂″⁍ ‽ \u2065⁽9a[⁀\u206D\u206D⁜⁾‗‘]k/\u2028 „⁼ ⁶\u2061}\u0004] ⁰⁶”⁽⁖ \u0004$<\u2028 ’ⁱ ‸⁷aVE \u2067⁺ …‘․\u00142!\u206D ‖⁼ ‸⁆m\u0012U \u206C“\u200F\u2065⁊′M}3‥‚⁵\u2060  ⁜9]V⁃⁼‚‒⁽\u200F\u202BP)>\u2063\u200B\u2072⁻„\u2067\u200B?G[⁃⁼ „⁰ \u2063Z4{\u202C‒\u2065\u0007 ․\u200B?U\u001F…\u2073‒c⁺ \u206DM:r  ⁺\u0006⁚⁴ !V\u001Dⁱ‧–h⁺―․O?z’⁓⁾\t ⁰ oh\u0013⁷† c\u2066⁐⁻\u001A\u000Bp‒⁅\u206E\u0006 ⁚‒t+\u0004\u2060․ u\u2064‿\u2060\u0006Bj  \u2068\u001C‗‟ g6\u0018\u2062\u2060⁈z\u2065⁰\u2065GBp ⁀\u202E\u001B ―⁅(,P\u2073\u2028⁋;⁵⁶‷MI>⁓⁜․1⁇⁆‗|p\u0006\u2067⁖⁐Y•\u2066\u2073\u001D\u0002m⁊‾‱0⁐ ‗=a\u0004‸⁒‑R‵\u206B⁾S\u0005$⁐※⁼|―‽‗%l@⁼‛„\u0019⁴⁈\u2063L\n" +
                "5‐※⁶x‘․‖/e[ⁱ⁏‟\u0017⁶⁗“I\t: ‧⁺s⁖‣⁴;cO\u2065⁏⁚\u001B‿⁐⁔V\n" +
                "! \u2061⁺S⁚⁰‣9\u007FM\u2065⁁“?‵‗ Q\u001A?⁅‵\u2073\u001F⁑\u2072\u2062%r5′⁜ wⁱ–⁂W\u0007W⁐‹⁵W \u2061‷9b?‵⁖‛y‥\u2029⁒\u0019\u0015P⁀›ⁿY⁑⁀‷9}5′‚‑8›‥⁓\u0019\tZ‒⁻‱K⁎⁄‸|\u0003;⁼‟‑8…\u202B⁗\b#S’\u206D‱^⁓⁇※(L5‹\u200C⁃,‼‰⁈\b \\⁒\u2069\u2063\u007F⁝⁙…|\u0000\u000F‷\u200B \f\u2029‰⁇\u0012./ⁿ\u206E•{⁆⁅\u202Bv$]„‘⁋\b\u202E\u2065⁃\u0013V}ⁿ⁶ (⁍‐‷3>\u0018\u200D⁖⁛@‿ⁿ⁖G\u001Ey⁹⁶ (⁚ ※(s\u001C‗ \u200FG‼ⁿ⁘D\u001Aq⁶⁺‡g⁾‚\u202C0\u007F\u0003⁼\u200E⁉\u0006‐›⁎U\u0019l\u200E\u206B\u2065&⁽⁕‼0v\u001A\u206B’⁉\u0006―‰“B\u0013{ ⁰″eⁱ‐⁋\n" +
                "J[\u206E –\u0012‐\u2063\u206B~\":‚․⁻w‚\u200B \n" +
                "G^›⁌„\u0005‴\u202B⁂og6⁛‸⁻d—⁃‧\u001DGT‾⁛‚\u0011\u2067… n/1„\u202C⁻f⁇ \u2068\u001BAV‾⁍―\u0002\u2067 ‚~5\"⁇⁇⁴l \u200F\u2069\u001BMN•‴ @‣\u206D\u200Cx,;⁑⁑‧(⁆⁍⁻\u0019BO‴‵ \\\u2029\u206D“vf;⁛―\u20659⁍⁍\u206B\u001F\u0012S⁻⁽ K\u206D\u202C {\u0018$— \u206C/⁍⁂\u2060\n" +
                "}V‴\u206C \u000F‾\u202D⁌-\u001F3⁗\u200D⁶|⁛\u200D‾Bj]″\u202D„\u0019\u2029\u202D⁍5\u000F8⁇\u200D\u206Dl⁙⁝‡P/O…\u2064„\u0018⁵⁗⁖8F,⁎⁄\u206D}‐›″\\cX‡\u2064\u200C\u000E⁻‚⁊3\u0016x⁕\u200B\u202Ck―⁹‣Au\u0014‰\u202B⁅\u001F‵\u200E⁊5\u001D4⁉⁄‰m―\u206F‸X1\u0014‽\u202C⁕\u001F⁰\u2065⁏9B4⁒⁂‹c⁐‑‧\\f[‶\u202B⁖\u0016‣‱⁔?\u0007)⁚⁎•6⁐⁐‧W+\t※\u2029⁅D‵‣⁔>]l‛⁚‼)⁗⁌‸\u001E2\n" +
                "※‹⁔H․‸⁑jK$‱\u206D‼- ⁰‰\u001E.\u0004⁃ ⁏H․  >Gp‰•\u202C$⁍\u2069‾FiP\u2064⁊⁉\u0004※ ⁗%\fp\u200B\u202C\u2069C⁔\u206A″Vx\u0015\u2062⁂⁉+‵\u200E‹4\u001Dv\u200D \u202C\u000B⁔\u2060’U~\u0002⁸⁎⁀+‧ ⁼0\u000E‛\u200B\u206E•G⁂\u2069 \u001C.⁺\u2065  !\u202D‛\u2028}@⁚\u200C\u2064ⁱU⁌⁵⁜]4′\u2069⁄‗4 ‐⁖>\\⁓ ‣\u2072P\u200F⁹‸J3\u2073\u2073⁋–$ ‖⁞jR⁓ ‣⁶A ‸⁾>:‶\u206E  )›‘\u200DV_⁓„\u202E\u2064H⁙⁽\u202D;:‿\u206A⁋ h‰–⁙T0⁋ \u202E†\u000E⁙⁴\u202C&U\u206B\u206D⁈ o⁹‱⁙T4‘ \u2029\u206EO ⁞‵0]⁽⁶ ’'\u2065⁾⁆U8‐–\u206D‹S ⁞․0\u0018ⁱ⁷‛⁘=\u2069‷⁊W4⁻‟\u206E‿X⁉⁖․3\u0014\u200F⁺“⁍1\u202B›⁁\u001F4\u2067–\u206F\u206DB⁞⁘\u206CrU ⁻  '⁾‿’\u001Cu⁶—\u2067ⁱN‐⁘‵<\u0014‘⁰⁇ +⁵‵⁜Rs‒ \u2028•X ⁇‵<\u0014′\u206B⁝⁖x\u206A‡―H|⁗⁋\u202E‣\n" +
                "\u200C⁀⁶-\\‸\u202D\u200E⁗f\u2069\u2060 N.⁝⁈\u2060⁻F‚\u200F‥:F‼‼⁀ )⁷\u206A U “⁈\u2028\u206D\t \u200F⁀%L⁹\u2068⁁ )\u2073\u2067‥\u0005*\u200B  ⁷\t  ⁒%K\u2068\u2073⁚‖a\u206Dⁱ\u2072C' ”‹⁾\u0004 ⁑‐\"D\u206A\u206A⁘\u200C`⁺⁛⁹Lb„ ‽\u2065\u0012⁚\u2028“-\u0016\u206D\u202C”‧g\u202E ⁵C6’⁄⁸ \u0014⁏\u2065‐e[⁶\u2029”\u2069`\u2063⁅⁴\u0017: ⁀\u2073\u200E@ \u2065‐r_ⁱ\u2060 \u2067'\u206A\u206Fⁿ\u0014\u007F  \u206C\u200EB\u200C⁏’c\u0010\u206E‥ ⁸'⁾‶⁻\ft‗ \u2029\u200CO‛‖–e\u0007\u2063\u2060⁅\u2069o⁽ⁿ⁴\u0010u ⁀\u2028\u200C\u0003 ‚‐0\u001C\u2068‴⁇\u202Cw\u2061ⁿ‚V}\u200B⁑\u2067⁃\u0011⁁‽\u20731]⁉‣ ‷y․⁏ \u0011?‥⁂\u206B⁜T⁌\u202E‶eZ⁁\u206E⁋‾8\u202D⁍⁝H7\u202E‛‸⁊Y⁎‥‴'\u202E⁊‷‘\u202C,•⁉‾H⁈\u206A⁇⁷⁛I⁐\u2069 &\u202C⁊\u202A\u200E\u2028=‵‛‶I⁙‹ \u206D⁉Q⁘‷‖(‷⁝\u202A„…q\u202E⁖\u2065\\‗\u2029⁂ⁿ⁒Q⁇•⁅=⁻⁄\u202D\u200C…[‡⁋\u2029Q„†\u200D⁹⁖{⁕‣⁌q\u206D⁃ⁿ“″\u0015⁻  \u001E\u200F\u202C‛\u2065–}„\u2062⁰z ⁛\u2073 \u2067]⁜\u200B‗Z\u206D\u2029“⁰\u200F8\u202E\u202B\u2060;„‣\u206F‑⁶Q⁀⁌⁎\u001B⁗⁗⁏\u2066‗\"\u2060‡\u202Bi′※‶⁆⁶\u0002 ⁄⁜I⁅⁔⁄• \"\u2069•⁼,\u202B‷\u202B⁗⁷C\u200E⁇‑I⁅⁃ ⁷ +\u206B⁍\u2062&‷‷‧‘\u2065\u000B“•‐B⁄‗⁓⁰ \u007F‼⁃\u2062'\u2064\u2062‧ \u2061\n" +
                "⁙‧⁂N ⁂⁓\u206C -‽⁎\u202Cn\u2065․\u2073\u200E\u2065Y⁉•⁉B⁅⁊“⁺⁅=†⁑‽+\u202B\u202D\u2069–‶U⁁″⁑N‡⁄ ⁷ #\u2028⁗․/⁍\u2028⁾⁗‽V⁜⁷⁖J‾⁜‑‥⁔8※⁗‵%⁐›⁸⁁‱V⁘′―G\u2029‚\u200C\u2029⁔v‾⁓⁶3‣⁵\u206A ‶\u0013⁗‽‑\u0013⁐  \u2062⁓}⁹”⁅{‵\u206B‥‖※\u0018⁙⁻․\u0018⁐⁋⁊⁰‛Z‰“ Z•․‾‘⁾(‐⁺\u2065>⁇⁀„⁹ I\u2069⁰ Y…\u2029⁰⁕\u2029( —․0⁈⁚ ‰⁈L‧\u2060⁌U\u2068\u202E\u206C⁂\u202D) –†:\u200F⁏ ‱\u200DF\u2061″⁔R\u206A\u206F⁒⁐ⁿ2‘–‧&  ‶⁰‐G\u206C’\u2061T\u2060\u2065‖―⁒( \u2072‒t ‑‶⁅‾I\u206D‗\u2066T\u2064⁓⁙\u202A⁕g\u200E⁸\u200Bt⁖\u2062⁓⁃※G\u206C‗\u2067\u0010⁶ ′″⁒3\u200D⁻—*⁼⁖\u2073\u2061\u2072z⁞⁛⁄o‽―‶\u206B‴(‛„  ⁰‵ⁿ‸—{⁗ ⁖a•\u206C⁵ⁱ⁓5‘\u200D‗/\u2061\u2029⁕‸ \u0015⁋⁙⁅j \u206E ⁰ W‾\u202D\u2065\u001E⁇\u200B‡‖\u206B4⁛\u200D x\u2067⁉⁈ⁱ⁋v\u2029\u2062⁾\u0010 ※\u2068 \u202E\u0013⁄ ‚0⁶⁔⁈\u2072⁋d‷\u206E\u2069D⁖′‧ \u206B\u0017⁒‘\u200C6‷⁞\u202D\u2073\u200Et‽⁶\u2068E‗‱⁃⁓⁺\u001C⁘⁖‛&\u2065⁔…‽⁖<‹‥※R\u200D※⁓⁚‾\u001C⁍⁍⁞r\u2064⁖‣※⁝h\u206D‹‶\u0013‐⁶⁊⁏⁽\u0000\u200C⁝‼~ⁱ‒ \u206F‒n\u202C‸⁊\u001B \u206B⁀ ⁷⁷ ‘ f\u2066 ‡\u206D‛ ⁿ\u206F⁎\u0015⁆⁽⁎ ※\u2072‖’‧q\u2066 ‡•⁌‗⁷\u206B \u001E \u206F ⁋‡⁺‒\u200F\u206E\u007F⁴ \u206D′\u200F⁰⁆\u2067\u200B_‘\u2063’⁆\u2063―\u2066—\u206A1⁼‚‴\u202E ⁼—ⁱ\u200E\u0011\u200B⁵⁙⁏\u206C⁜⁼‐\u206A1\u206D’\u202C‡\u200B⁼—⁵‘B ⁵⁊\u202B\u206D–\u2066 ⁹0\u206C⁕‥⁝ \u2061⁆⁶‑U⁌‷⁄‾\u2063⁁\u2029‐‱!․⁒\u2064⁝\u200B†⁀\u2062‑H⁊\u2072  \u2064⁎‴⁂⁾.\u206A‚\u2067⁝⁊\u206E\u2063\u202B F⁊⁻\u206D\u2029‸\u200B\u200E⁞\u2066)‿ ⁍⁄⁍⁹\u2063\u202B—\t⁋\u2060\u202C‰\u206D \u200C⁞⁺m\u202E \u200C⁜ \u2061\u2069⁾f ⁗․ ‽\u2072  \u200B\u0014\u0001N \u202C⁕‗\u2028\u2063\u2073`b \u2060⁉‱”⁀  @\u0003R\u200D›‑\u2069 ⁱ\u20602g!\u202D⁎⁹\u200C\u200F  @\u0002D⁃\u2060⁙⁘\u2067\u2067\u206D`q,…⁀※\u202D―\u200E \u0004QD⁃′‱⁋⁴\u206D\u206D$8*\u2063⁚⁔‹⁔ \u200CJ\\Y⁍⁺”⁍⁴\u2072\u206D9|8‽ ⁼‿‑“’\u0019\bP⁜⁾⁜⁌⁹⁹‹ni#⁼\u200B\u2028‸“\u200B⁐\u0000\u000E\u0003” ⁘⁊⁽\u2072‵r \t⁜⁵⁸‾―‛⁆RMc‱‐‖⁊‵\u206F\u202E7m\u0006 \u2064ⁿ‸⁐⁏⁉E\u0002s ⁄‐⁞⁰‿\u202C*r\u001F⁊\u2064\u2072\u202C‟⁔⁉\n" +
                "\u001Bq‾\u200B⁒⁍―‰\u202Cok]„⁸‾•\u2062“\u200C\u001D\u0003$\u206A‐⁓⁋ ⁽\u2060=`L\u200B⁾‧\u206B\u206E‛⁀‥\"d⁉  ⁅⁀⁙\u206D\u2067\u0003⁽]‰ⁿ†′⁹\u200C g⁝2⁆‚⁒‸‘\u206B\u2068\u000E″\u001E\u2066\u206C‷⁊\u2061⁋‛f⁜i ―‛\u206A‖•\u206F\u000E⁼\b\u202A⁹⁴ ⁱ ‟o {⁏⁙‖\u2061 ⁵⁺\n" +
                "\u2067[※‱\u2073⁁\u2063“ y–{#\u2073j⁋ \u2072\u206CY\u2067\u0013F⁓\u0019\u202E\u2061” =src※|⁏‗\u2064\u202E\u001D\u001E\u0007\u0011⁖\t‽⁸‑⁝=mhb‸m‑⁘\u2062′P\b\u0000\n" +
                "⁏g\u2072\u202D‐⁛?}sd‶G ⁌\u2066›X\u0018_D )‡‸\u200E xz>'‴N⁓⁗⁻‱\u001CZQA—9※‾‘⁙<5?$‴J⁞⁛⁵‼X?KK—\"※› “,W.k\u2067V⁚⁗⁷‼C1\u000E\u0005 =‿″⁗⁚&Tz%\u2067S⁛–‣′Ct\u000EM―<‹ⁱ⁊⁜$Td+‵H⁖“‾″I'J!\u2073'․‼⁎⁖;O+Q \u0007⁅⁏\u206E※N,Cq\u2061t\u2065※ ⁒<X:Q‒\u0011 ⁔\u2068‶OxN9⁷h…‿\u200D⁆;X'M⁽\u001D⁖‑\u202D\u200FOxP,\u200E=‷‱ \u206A)\n" +
                "1E\u2060\u001D⁃⁙‾„\t}P6⁀r‥\u202D⁛⁰)\u00155W′\u0016 ⁄‵⁐DzX2⁜f⁶\u2064⁚‶N\u0015.W\u202E\u0011„ ‶⁛'{Iw⁋|ⁱ⁵ ‴IUi'\u202A\u000E ’…— !IP⁋}‥⁸ \u2067OS=p․\u001B ‐ⁿ !sI\u001F o\u206D⁵⁵⁽H\u0000-p\u2069O\u200C‛‑⁝%dG\u0015‚;⁵※⁾※\u0005&.r›y ⁔ ⁓`T\u0002R⁘\f\u2073⁴⁹‧\t8nr‵e ‑⁙⁎}\u0018\u0019\u0013⁆o\u2060ⁿ⁹ \u001El9|†O–‚―⁉3\u0004@\f⁎ \u2060\u2073\u2066\u2065\u0013a`h\u202BL ‑ ‗r\u0015\u0005H⁏>\u2066\u2066\u206D⁾\u001Cr%'\u2029\u001E   \u200D\u007F\u001BJR⁚p\u2060⁺⁰ \u001Dfj?‿\u0011\u200E ⁐\u2068{F\u0018W⁆a\u2066\u2064‹\u200B[(w>‵\u0004⁈⁄\u206E\u20625[\u0003Q⁛”※\u2064\u200B\u200CA)f8‷\u206E‛‗\u206E\u2069,L\u00022⁃ ※⁰“ [ld] \u2065―⁐⁕\u2068{\u0018\f8\u200F‱\u2062‿⁵‥\u0012vyL\u206A⁂⁂⁷—⁑wV\u0011)⁊‡\u202D ⁸‵W8~]\u206A⁉⁈\u206E ‿$P\u001F/ ‧ ⁎\u2061⁑\u0004$wJ‣⁀⁊† ‣aHW.⁆\u202C‣⁒\u206D⁖\fbwL″⁘ … ‿\u007FB\u00049⁑⁵\u206B⁓\u2068⁞\u0011fgQ‰‛‟\u2061⁚⁞";
        String iv_example = "tyueawtmtq";
        // We used a 6 char key change as an example, we encrypted it with the CBC algorithm and will decode it
        /*Key used for encription  :
        a d
        b f
        c e
        d b
        e a
        f c
         */
        Map<String, String> words = new HashMap<String, String>();
        hard_coded_words(words);
        double max_percentage = 0;
        double count = 0;
        Map<String, String> used_keys = new HashMap<String, String>();
        Map<Character, Character> map_key = new HashMap<Character,Character>();
        build_map(map_key);
        List<Character> map_values = new ArrayList(map_key.values());
        used_keys.put(map_values.toString(), map_values.toString());
        Map<Character, Character> best_key = new HashMap<Character, Character>(map_key);
        long start = System.nanoTime();
        String decrypted_text = "";
        System.out.println(factorialUsingForLoop(map_key.size()));
        while (used_keys.size() < factorialUsingForLoop(map_key.size()) &&(System.nanoTime() - start) / 1000000000 <= 300) {
            decrypted_text = decrypt(cipher_text_example,iv_example , map_key);
            double percentage = compare_with_dictionary(decrypted_text.toLowerCase(), words);
            if (count == 0)
            {
                max_percentage = percentage;
            }
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
        System.out.println("This is the Best key found");
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
