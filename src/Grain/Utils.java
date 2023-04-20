package Grain;

public class Utils {

    public static String stringToBinary(String string) {
        String result = "";
        String tmpStr;
        int tmpLenght;
        char[] messChar = string.toCharArray();
        for (int i = 0; i < messChar.length; i++) {
            tmpStr = Integer.toBinaryString(messChar[i]);

            tmpLenght = tmpStr.length();

            if (tmpLenght != 8) {
                tmpLenght = 8 - tmpLenght;
                if (tmpLenght == 8) {
                    result += tmpStr;
                } else if (tmpLenght > 0) {
                    for (int j = 0; j < tmpLenght; j++) {
                        result += "0";

                    }
                } else {
                    System.out.println("arguments bits to small ...");
                }
            }
            result += tmpStr;
            //
        }
        result += "";

        return result;
    }
}