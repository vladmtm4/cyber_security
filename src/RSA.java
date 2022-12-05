import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class RSA
{

    public static HashMap<BigInteger,BigInteger> generate_p_q_different_primes(String plain_message)
    {
        byte [] byted_plain_message = plain_message.getBytes();
        int message_bit_length = byted_plain_message.length*8;

        Random rnd = new Random();

        BigInteger prime_number_1 = BigInteger.probablePrime((message_bit_length/2)+1,rnd);
        BigInteger prime_number_2 = BigInteger.probablePrime((message_bit_length/2)+1,rnd);
        HashMap res_map = new HashMap<BigInteger,BigInteger>();
        res_map.put(prime_number_1,prime_number_2);
        return res_map;
    }


    public static byte[] encrypt(String plain_message, BigInteger public_key, BigInteger n)
    {
        BigInteger big_int_byted_message = new BigInteger(plain_message.getBytes());
        BigInteger encrypted_messgae_big_int = big_int_byted_message.modPow(public_key,n);
        byte [] encrypted_byted = encrypted_messgae_big_int.toByteArray();
        return encrypted_byted;

    }

    public static String decrypt(byte[] encrypted_message,BigInteger private_key,BigInteger n)
    {
        BigInteger encrypted_message_big_ing = new BigInteger(encrypted_message);
        BigInteger decripted_messgae_big_int = encrypted_message_big_ing.modPow(private_key,n);
        byte [] encrypted_byted = decripted_messgae_big_int.toByteArray();
        String decoded_message = new String(encrypted_byted, StandardCharsets.UTF_8);
        return decoded_message;


    }

 /*   a function we used to generate the whole RSA algorithm for testing

 public static BigInteger[] get_n_e_d_phiN_of_RSA (String plain_text)
    {
        byte [] byted_plain_message = plain_text.getBytes();
        Random rnd = new Random();
        int message_bit_length = byted_plain_message.length*8;
        BigInteger one = BigInteger.valueOf(1);
        HashMap<BigInteger,BigInteger> p_q= new  HashMap<BigInteger,BigInteger>();
        p_q= generate_p_q_different_primes(plain_text);
        List <BigInteger> p = new ArrayList(p_q.keySet());
        List <BigInteger> q = new ArrayList(p_q.values());
        BigInteger phi_n = (p.get(0).subtract(one)).multiply(q.get(0).subtract(one));
        BigInteger n = q.get(0).multiply(p.get(0));
        BigInteger e = BigInteger.probablePrime((message_bit_length/2)+1, rnd);
        while (phi_n.gcd(e).compareTo(one) > 0 && e.compareTo(phi_n) < 0)
        {//Greatest common divisor with phi=1 and e<phi
            e.add(one);
        }
        BigInteger d = e.modInverse(phi_n);//d*e mod phi=1
        BigInteger[] RSA_components = {n,e,d,phi_n};
        return RSA_components;

    }
  */
}
