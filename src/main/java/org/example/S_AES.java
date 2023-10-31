package org.example;

import java.util.Arrays;

public class S_AES {


    // 定义S盒
    static final int[][] s = {
            {9, 4, 10, 11},
            {13, 1, 8, 5},
            {6, 2, 0, 3},
            {12, 14, 15, 7}
    };
    static final int[][] replace = {
            {0, 0, 0, 0},
            {0, 0, 0, 1},
            {0, 0, 1, 0},
            {0, 0, 1, 1},
            {0, 1, 0, 0},
            {0, 1, 0, 1},
            {0, 1, 1, 0},
            {0, 1, 1, 1},
            {1, 0, 0, 0},
            {1, 0, 0, 1},
            {1, 0, 1, 0},
            {1, 0, 1, 1},
            {1, 1, 0, 0},
            {1, 1, 0, 1},
            {1, 1, 1, 0},
            {1, 1, 1, 1}
    };
    // 定义轮常数
    static int[] wheelConstant1 = {1, 0, 0, 0, 0, 0, 0, 0};
    static int[] wheelConstant2 = {0, 0, 1, 1, 0, 0, 0, 0};

    // 乘法
    static int[] multiply(int[] a, int[] b) {
        // 储存结果的系数
        int[] result = new int[4];
        Arrays.fill(result, 0);

        // 记录下x^nfx
        int[] xfx = new int[4];
        polynomialExponentiation(xfx, a);
        int[] x2fx = new int[4];
        polynomialExponentiation(x2fx, xfx);
        int[] x3fx = new int[4];
        polynomialExponentiation(x3fx, x2fx);

        // 现在需要根据多项式a和b开始异或
        if (b[0] == 1) {
            for (int i = 0; i < 4; i++) {
                result[i] ^= x3fx[i];
            }
        }
        if (b[1] == 1) {
            for (int i = 0; i < 4; i++) {
                result[i] ^= x2fx[i];
            }
        }
        if (b[2] == 1) {
            for (int i = 0; i < 4; i++) {
                result[i] ^= xfx[i];
            }
        }
        if (b[3] == 1) {
            for (int i = 0; i < 4; i++) {
                result[i] ^= a[i];
            }
        }
        return result;
    }

    // 实现x^nfx的函数
    static void polynomialExponentiation(int[] xfx, int[] a) {
        // 注意要取模
        // 既约多项式是 x^4 + x + 1
        if (a[0] == 0) {
            for (int i = 0; i < 3; i++) {
                xfx[i] = a[i + 1];
            }
        } else {
            // 如果乘数首项不为1就需要将 b1x^2+b0x 与 x+1 进行异或
            xfx[1] = a[2];
            xfx[2] = (a[3] == 1) ? 0 : 1;
            xfx[3] = 1;
        }
    }

    // 执行8位异或
    static int[] xor8(int[] a, int[] b) {
        int[] t = new int[8];
        for (int i = 0; i < 8; i++) {
            t[i] = a[i] ^ b[i];
        }
        return t;
    }

    // 执行4位异或
    static int[] xor4(int[] a, int[] b) {
        int[] t = new int[4];
        for (int i = 0; i < 4; i++) {
            t[i] = a[i] ^ b[i];
        }
        return t;
    }

    // 使用S盒替换的函数，8位换
    static void sBoxReplace(int[] temp) {
        int t1 = 2 * temp[0] + temp[1];
        int t2 = 2 * temp[2] + temp[3];
        int t3 = 2 * temp[4] + temp[5];
        int t4 = 2 * temp[6] + temp[7];
        int replace1 = s[t1][t2]; // 记录替换后的数字
        int replace2 = s[t3][t4];
        // 四位四位进行替换
        for (int i = 0; i < 4; i++) {
            temp[i] = replace[replace1][i];
        }
        for (int i = 0; i < 4; i++) {
            temp[i + 4] = replace[replace2][i];
        }
    }

    // 循环左移
    static void leftShift(int[][] temp) {
        for (int i = 4; i < 8; i++) {
            int t = temp[0][i];
            temp[0][i] = temp[1][i];
            temp[1][i] = t;
        }
    }

    // g函数
    static int[] g(int[] temp, int[] rcon) {
        // 注意这个temp是密钥，不能改动，要复制一个新的进行计算
        int[] t = Arrays.copyOf(temp, temp.length);

        // 循环左移
        for (int i = 0; i < 4; i++) {
            int tt = t[i + 4];
            t[i + 4] = t[i];
            t[i] = tt;
        }

        // 进行S盒替换
        sBoxReplace(t);

        // 进行轮常数异或
        return xor8(t, rcon);
    }

    // 列混淆
    static void mixColumns(int[][] mingwen) {
        int[] si_de2jinzhi = {0, 1, 0, 0};
        int[] m00 = new int[4];
        int[] m10 = new int[4];
        int[] m01 = new int[4];
        int[] m11 = new int[4];
        for (int i = 0; i < 4; i++) {
            m00[i] = mingwen[0][i];
            m10[i] = mingwen[0][i + 4];
            m01[i] = mingwen[1][i];
            m11[i] = mingwen[1][i + 4];
        }
        int[] n00 = xor4(m00, multiply(si_de2jinzhi, m10)); // 乘法结果是1011
        int[] n10 = xor4(multiply(si_de2jinzhi, m00), m10); // 0101
        int[] n01 = xor4(m01, multiply(si_de2jinzhi, m11)); // 0100
        int[] n11 = xor4(multiply(si_de2jinzhi, m01), m11); // 0010
        for (int i = 0; i < 4; i++) {
            mingwen[0][i] = n00[i];
            mingwen[0][i + 4] = n10[i];
            mingwen[1][i] = n01[i];
            mingwen[1][i + 4] = n11[i];
        }
    }

    // 轮密钥加
    static void addRoundKey(int[][] plaintext, int[][] key) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                plaintext[i][j] ^= key[i][j];
            }
        }
    }


    public static String encrypt(String plaintext, String key, boolean isStr) {
        // 输入明文和密钥
        int[][] plaintextArray = new int[2][8];
        for (int i = 0; i < 16; i++) {
            int digit = Character.getNumericValue(plaintext.charAt(i));
            int row = i / 8;
            int col = i % 8;
            plaintextArray[row][col] = digit;
        }
        int[][] keyArray = new int[2][8];
        for (int i = 0; i < 16; i++) {
            int digit = Character.getNumericValue(key.charAt(i));
            int row = i / 8;
            int col = i % 8;
            keyArray[row][col] = digit;
        }

        // 密钥扩展算法，由于只有三轮加密，第一轮还只使用了原始key
        int[][] key1 = new int[2][8];
        int[][] key2 = new int[2][8];

        key1[0] = xor8(keyArray[0], g(keyArray[1], wheelConstant1));
        key1[1] = xor8(key1[0], keyArray[1]);
        key2[0] = xor8(key1[0], g(key1[1], wheelConstant2));
        key2[1] = xor8(key2[0], key1[1]);

        // 第0轮的轮密钥加
        addRoundKey(plaintextArray, keyArray);


        // 第一轮
        // 明文用半字节代替
        sBoxReplace(plaintextArray[0]);
        sBoxReplace(plaintextArray[1]);
        // 明文的行移位
        leftShift(plaintextArray);
        // 明文的列混淆
        mixColumns(plaintextArray);
        // 明文的轮密钥加
        addRoundKey(plaintextArray, key1);

        // 第二轮
        // 明文用半字节代替
        sBoxReplace(plaintextArray[0]);
        sBoxReplace(plaintextArray[1]);
        // 明文的行移位
        leftShift(plaintextArray);
        // 明文的轮密钥加
        addRoundKey(plaintextArray, key2);

        // 输出结果
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                res.append(plaintextArray[i][j]);
            }
        }
        String binaryStr = res.toString();
        if (isStr) {
            // 将二进制字符串分为两部分
            String binaryPart1 = binaryStr.substring(0, 8);
            String binaryPart2 = binaryStr.substring(8, 16);

            // 将每个二进制字符串转换为字符
            char char1 = (char) Integer.parseInt(binaryPart1, 2);
            char char2 = (char) Integer.parseInt(binaryPart2, 2);

            // 合并两个字符为一个字符串
            String combinedString = String.valueOf(char1) + String.valueOf(char2);
            return combinedString;
        } else {
            return binaryStr;
        }
    }


    public static String decode(String plaintext, String key, boolean isStr) {
        // 输入密文和密钥
        int[][] plaintextArray = new int[2][8];
        for (int i = 0; i < 16; i++) {
            int digit = Character.getNumericValue(plaintext.charAt(i));
            int row = i / 8;
            int col = i % 8;
            plaintextArray[row][col] = digit;
        }
        int[][] keyArray = new int[2][8];
        for (int i = 0; i < 16; i++) {
            int digit = Character.getNumericValue(key.charAt(i));
            int row = i / 8;
            int col = i % 8;
            keyArray[row][col] = digit;
        }

        // 密钥扩展算法，由于只有三轮加密，第一轮还只使用了原始key
        int[][] key1 = new int[2][8];
        int[][] key2 = new int[2][8];

        key1[0] = xor8(keyArray[0], g(keyArray[1], wheelConstant1));
        key1[1] = xor8(key1[0], keyArray[1]);
        key2[0] = xor8(key1[0], g(key1[1], wheelConstant2));
        key2[1] = xor8(key2[0], key1[1]);

        // 第0轮的轮密钥加
        addRoundKey(plaintextArray, keyArray);


        // 第一轮
        // 明文用半字节代替
        sBoxReplace(plaintextArray[0]);
        sBoxReplace(plaintextArray[1]);
        // 明文的行移位
        leftShift(plaintextArray);
        // 明文的列混淆
        mixColumns(plaintextArray);
        // 明文的轮密钥加
        addRoundKey(plaintextArray, key1);

        // 第二轮
        // 明文用半字节代替
        sBoxReplace(plaintextArray[0]);
        sBoxReplace(plaintextArray[1]);
        // 明文的行移位
        leftShift(plaintextArray);
        // 明文的轮密钥加
        addRoundKey(plaintextArray, key2);

        // 输出结果
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                res.append(plaintextArray[i][j]);
            }
        }
        String binaryStr = res.toString();
        if (isStr) {
            // 将二进制字符串分为两部分
            String binaryPart1 = binaryStr.substring(0, 8);
            String binaryPart2 = binaryStr.substring(8, 16);

            // 将每个二进制字符串转换为字符
            char char1 = (char) Integer.parseInt(binaryPart1, 2);
            char char2 = (char) Integer.parseInt(binaryPart2, 2);

            // 合并两个字符为一个字符串
            String combinedString = String.valueOf(char1) + String.valueOf(char2);
            return combinedString;
        } else {
            return binaryStr;
        }
    }

}
