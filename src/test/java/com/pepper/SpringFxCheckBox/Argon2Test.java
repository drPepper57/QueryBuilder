/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.pepper.SpringFxCheckBox;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.testfx.framework.junit5.ApplicationTest;


public class Argon2Test extends ApplicationTest
{
    @Test
    public void givenRawPassword_whenEncodedWithArgon2_thenMatchesEncodedPassword() 
    {
        String rawPassword = "Baeldung";
        Argon2PasswordEncoder arg2SpringSecurity = new Argon2PasswordEncoder(16, 32, 1, 60000, 10);
        String springBouncyHash = arg2SpringSecurity.encode(rawPassword);

        assertTrue(arg2SpringSecurity.matches(rawPassword, springBouncyHash));
    }
    
    @Test
    public void givenRawPasswordAndSalt_whenArgon2AlgorithmIsUsed_thenHashIsCorrect() 
    {
        byte[] salt = generateSalt16Byte(); // salt generálás
        String password = "Baeldung";

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

        Argon2BytesGenerator verifier = new Argon2BytesGenerator();
        verifier.init(builder.build());
        byte[] testHash = new byte[hashLength];
        verifier.generateBytes(password.getBytes(StandardCharsets.UTF_8), testHash, 0, testHash.length);
        
        assertTrue(Arrays.equals(result, testHash));
    }
    private byte[] generateSalt16Byte() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] salt = new byte[16];
    secureRandom.nextBytes(salt);
        
    return salt;
}
}
