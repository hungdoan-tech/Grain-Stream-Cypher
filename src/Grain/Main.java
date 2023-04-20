package Grain;

public class Main {

    public static void main(String[] args) {

        String plainText = "Hung Doan";
        Grain grain = new Grain("samplekey123","sampleiv123");
        int[] keyStream = grain.getKeystream(plainText);

        String cipherText = grain.encrypt(plainText, keyStream);
        System.out.println("Cipher text in Main " + cipherText);

        String decryptedText = grain.decrypt(cipherText, keyStream);
        System.out.println("Decrypt text in Main " + decryptedText);
    }
}
