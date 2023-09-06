package Security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;


public class PasswordFactory {    
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String HashPassword(String password, byte[] salt) 
    {
        //byte[] salt = generateSalt16Byte();
        
        int iterations = 2; // paraméterek az Argon2 höz
        int memLimit = 66536;
        int hashLength = 32;
        int parallelism = 1;
        
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id) // erőforrások beállítása
          .withVersion(Argon2Parameters.ARGON2_VERSION_13)
          .withIterations(iterations)
          .withMemoryAsKB(memLimit)
          .withParallelism(parallelism)
          .withSalt(salt);
        
        Argon2BytesGenerator generate = new Argon2BytesGenerator(); // hash generátor
        generate.init(builder.build()); // generátor inicializálása a builderrel
        byte[] result = new byte[hashLength];
        generate.generateBytes(password.getBytes(StandardCharsets.UTF_8), result, 0, result.length); // itt történik meg a HASH
        
        String pass = bytesToHex(result);
        
        return pass;
    }
    public static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02X", b));
    }
    return sb.toString();
}

    
    public static byte[] generateSalt16Byte() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] salt = new byte[16];
    secureRandom.nextBytes(salt);
        
    return salt;
    }
}
