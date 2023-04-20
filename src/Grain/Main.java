package Grain;

public class Main {

    public static void main(String[] args) {

        Grain grain = new Grain("iv123", "key123");
        String plainTextInBinary = grain.getPlainTextInBinaryAndKeystream("hungdoan");

        String cipherText = grain.encrypt(plainTextInBinary);
        System.out.println("Cipher text in Main " + cipherText);

        String decryptedText = grain.decrypt(cipherText);
        System.out.println("Decrypt text in Main " + decryptedText);
    }
}
