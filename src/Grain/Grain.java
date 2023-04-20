package Grain;

public class Grain {
    private final int CLOCK_NUMBER = 160;
    private int[] lfsr;
    private int[] nfsr;
    private int[] keystream;
    private int fx;
    private int gx;
    private int hx;
    private int z;
    private String keyStreamInBinary;

    public Grain(String iv, String key) {
        this.lfsr = new int[160];
        this.nfsr = new int[160];
        this.keystream = new int[240];
        initTwoRegisters(iv, key);
        clock();
    }

    public void initTwoRegisters(String iv, String key) {
        String ivInBinary = Utils.stringToBinary(iv);
        String keyInBinary = Utils.stringToBinary(key);

        // lfsr structure
        // IV - 0(IV Length)... - 1 (64th) ... to 80th

        //put ivInBinary into array
        for (int i = 0; i < ivInBinary.length(); i++) {
            lfsr[i] = Byte.parseByte(String.valueOf(ivInBinary.charAt(i)));
        }

        // put 0 to the rest if the length of iv is no longer than 64
        if (ivInBinary.length() < 64) {
            for (int i = ivInBinary.length(); i < 64; i++) {
                lfsr[i] = 0;
            }
        }

        //fill the place (index) from 64 to 80 with value 1
        for (int i = 64; i < 80; i++) {
            lfsr[i] = 1;
        }

        // nfsr structure
        // IV - 0(Key Length) to 80th

        //put keyInBinary into array
        for (int j = 0; j < keyInBinary.length(); j++) {
            nfsr[j] = Byte.parseByte(String.valueOf(keyInBinary.charAt(j)));
        }

        // put 0 to the rest if the length of iv is no longer than 64
        if (keyInBinary.length() < 80) {
            for (int j = keyInBinary.length(); j < 80; j++) {
                nfsr[j] = 0;
            }
        }
    }

    public void clock() {
        int new_nfsr;
        int new_lfsr;
        int new_filter;

        for (int i = 0; i < CLOCK_NUMBER; i++) {
            fx = calculateFx();
            gx = calculateGx();
            hx = calculateHx();
            z = calculateZ();

            new_filter = hx ^ z;
            new_nfsr = gx ^ new_filter;
            new_lfsr = fx ^ new_filter;

            for (int j = 0; j < 159; j++) {
                this.lfsr[j] = this.lfsr[j + 1];
            }
            lfsr[79] = new_lfsr;

            for (int k = 0; k < 159; k++) {
                this.nfsr[k] = this.nfsr[k + 1];
            }
            nfsr[79] = new_nfsr;
        }
    }

    public int calculateFx() {
        fx = (byte) (lfsr[62] ^ lfsr[51] ^ lfsr[38] ^ lfsr[23] ^ lfsr[13] ^ lfsr[0]);
        return fx;
    }

    public int calculateGx() {
        gx = (byte) (
                          lfsr[0] ^ nfsr[62] ^ nfsr[60]
                        ^ nfsr[52] ^ nfsr[45] ^ nfsr[37]
                        ^ nfsr[33] ^ nfsr[28] ^ nfsr[21]
                        ^ nfsr[14] ^ nfsr[9] ^ nfsr[0]
                        ^ (nfsr[63] & nfsr[60]) ^ (nfsr[37] & nfsr[33])
                        ^ (nfsr[15] & nfsr[9]) ^ (nfsr[60] & nfsr[52] & nfsr[45])
                        ^ (nfsr[33] & nfsr[28] & nfsr[21])
                        ^ (nfsr[63] & nfsr[45] & nfsr[28] & nfsr[9])
                        ^ (nfsr[60] & nfsr[52] & nfsr[37] & nfsr[33])
                        ^ (nfsr[63] & nfsr[60] & nfsr[21] & nfsr[15])
                        ^ (nfsr[63] & nfsr[60] & nfsr[52] & nfsr[45] & nfsr[37])
                        ^ (nfsr[33] & nfsr[28] & nfsr[21] & nfsr[15] & nfsr[9])
                        ^ (nfsr[52] & nfsr[45] & nfsr[37] & nfsr[33] & nfsr[28] & nfsr[21])
        );
        return gx;
    }

    public int calculateHx() {
        int x0 = lfsr[3];
        int x1 = lfsr[25];
        int x2 = lfsr[46];
        int x3 = lfsr[64];
        int x4 = nfsr[63];
        hx = (x1 ^ x4 ^ (x0 & x3) ^ (x2 & x3) ^ (x3 & x4) ^ (x0 & x1 & x2)
                 ^ (x0 & x2 & x3) ^ (x0 & x2 & x4) ^ (x1 & x2 & x4) ^ (x2 & x3 & x4));
        return hx;
    }

    public int calculateZ() {
        int z0 = nfsr[1];
        int z1 = nfsr[2];
        int z2 = nfsr[4];
        int z3 = nfsr[10];
        int z4 = nfsr[31];
        int z5 = nfsr[43];
        int z6 = nfsr[56];

        z = z0 ^ z1 ^ z2 ^ z3 ^ z4 ^ z5 ^ z6;
        return z;
    }

    public String getPlainTextInBinaryAndKeystream(String plainText) {

        int new_filter;
        String plainTextInBinary = Utils.stringToBinary(plainText);
        System.out.println("Plain Text In Binary = " + plainTextInBinary);
        System.out.print("Keystream = ");
        for (int i = 0; i < plainTextInBinary.length(); i++) {
            fx = calculateFx();
            gx = calculateGx();
            hx = calculateHx();
            z = calculateZ();

            new_filter = hx ^ z;
            keystream[i] = new_filter;
            System.out.print(keystream[i]);

            for (int j = 0; j < 79; j++) {
                this.lfsr[j] = this.lfsr[j + 1];
            }
            lfsr[79] = fx;

            for (int k = 0; k < 79; k++) {
                nfsr[k] = nfsr[k + 1];
            }
            nfsr[79] = gx;
        }
        System.out.println("");
        encrypt(plainTextInBinary);
        return plainTextInBinary;
    }


    public String encrypt(String plainTextInBinary) {
        String cypherText = "";
        Byte[] result_array = new Byte[plainTextInBinary.length()];
        Byte[] result_xor_array = new Byte[plainTextInBinary.length()];
        keyStreamInBinary = "";

        for (int i = 0; i < result_array.length; i++) {
            result_array[i] = Byte.parseByte(String.valueOf(plainTextInBinary.charAt(i)));
            result_xor_array[i] = (byte) (this.keystream[i] ^ result_array[i]);
            cypherText += result_xor_array[i];
            keyStreamInBinary += keystream[i];
        }

        return cypherText;
    }

    public String decrypt(String cipherText) {
        Byte[] a_array = new Byte[cipherText.length()];
        Byte[] b_array = new Byte[keyStreamInBinary.length()];

        String plain_binary = "";
        String plain = "";
        Byte[] result = new Byte[cipherText.length()];

        for (int i = 0; i < a_array.length; i++) {
            a_array[i] = Byte.parseByte(String.valueOf(cipherText.charAt(i)));
        }

        for (int i = 0; i < b_array.length; i++) {
            b_array[i] = Byte.parseByte(String.valueOf(keyStreamInBinary.charAt(i)));
        }

        for (int i = 0; i < cipherText.length(); i++) {
            result[i] = (byte) (b_array[i] ^ a_array[i]);
            plain_binary += result[i];
        }

        System.out.println("Decrypt = " + plain_binary);
        System.out.println("");
        for (int i = 0; i < plain_binary.length(); i += 8) {
            int k = Integer.parseInt(plain_binary.substring(i, i + 8), 2);
            plain += (char) k;
        }
        return plain;
    }
}
