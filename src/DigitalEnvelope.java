import java.math.BigInteger;
import java.util.*;


public class DigitalEnvelope
{
    public static HashMap<byte[],String> create_envelope(String init_vector, String plain_text, String symmetric_key,
                                                         BigInteger public_key, BigInteger n)
    {
        String ciphered_text = CipherBlockChaining.encrypt(plain_text,init_vector,symmetric_key);
        byte [] ciphered_symmetric_key = RSA.encrypt(symmetric_key,public_key,n);
        HashMap digital_envelope = new HashMap<byte[],String>();
        digital_envelope.put(ciphered_symmetric_key,ciphered_text);
        return digital_envelope;
    }

    public static String open_envelope(HashMap<byte[],String> envelope, String init_vector, BigInteger private_key,
                                       BigInteger n)
    {
        List <byte []> symmetric_key = new ArrayList(envelope.keySet());
        List <String> ciphered_text = new ArrayList(envelope.values());
        String decrypted_symmetric_key = RSA.decrypt(symmetric_key.get(0),private_key,n);
        String decrypted_plain_text = CipherBlockChaining.decrypt(ciphered_text.get(0),init_vector,decrypted_symmetric_key);
        return decrypted_plain_text;

    }

}
