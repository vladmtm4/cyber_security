import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DiffieHellman {


    public static BigInteger primitive_key(BigInteger q)
    {
        boolean flag=false;
        BigInteger zero = BigInteger.valueOf(0);
        BigInteger power = BigInteger.valueOf(1);
        BigInteger one = BigInteger.valueOf(1);
        BigInteger base = BigInteger.valueOf(2);
        List<BigInteger> check_primitive = new ArrayList<BigInteger>();
        while(!flag)
        {
                    while(!q.subtract(power).equals(zero))
                    {
                        check_primitive.add(base.modPow(power,q));
                        power=power.add(one);
                    }
                    Set<BigInteger> check_primitive_set = new HashSet<BigInteger>(check_primitive);

                    if(check_primitive_set.size() == check_primitive.size())
                    {
                       flag=true;
                       return base;
                    }
                    check_primitive.clear();
                    power = one;
                 base=base.add(one);
                 if(base.equals(q))
                 {
                     return zero;
                 }
        }
        return base;

    }


    public static BigInteger[] diffie_hellman(BigInteger q, BigInteger private_Xa, BigInteger private_Xb)
    {
        BigInteger primitive_root = primitive_key(q);
        BigInteger ya = primitive_root.modPow(private_Xa,q);
        BigInteger yb = primitive_root.modPow(private_Xb,q);
        BigInteger ka = yb.modPow(private_Xa,q);
        BigInteger kb = ya.modPow(private_Xb,q);
        if (ka.equals(kb))
        {
            System.out.println("You made i MF");
        }

        BigInteger [] diffie_hellman_arr  ={primitive_root,private_Xa,ya,private_Xb,yb,ka,kb};

        return diffie_hellman_arr;
   }

    public static byte[] get_digest(String sender_info, BigInteger sender_public_key, String CA_info)
    {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String  input=sender_info+new String(sender_public_key.toByteArray())+CA_info;// this string is the certificate
        return md.digest(input.getBytes());

    }

    public static byte[] sign_certificate(byte[] hash, BigInteger private_key, BigInteger n)
    {
        return RSA.encrypt(new String(hash), private_key, n);//encrypt the certificate usin RSA
    }

    public static byte[] get_hash_from_certificate(byte[] signed_certificate,BigInteger public_key,BigInteger n)
    {
        return RSA.decrypt(signed_certificate,public_key,n).getBytes();
    }

}
