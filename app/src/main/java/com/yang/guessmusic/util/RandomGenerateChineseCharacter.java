package com.yang.guessmusic.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/*GB2312 中对所收汉字进行了"分区"处理，每区含有 94 个汉字/符号。这种表示方式也称为区位码。
01 - 09 区为特殊符号。
16 - 55 区为一级汉字，按拼音排序。
56 - 87 区为二级汉字，按部首/笔画排序。�*/
public class RandomGenerateChineseCharacter {
    public static char getRandomChar() {
        String character = "";
        int high_order;
        int low_order;
        Random random = new Random();
        //高位字节使用了 0xA1 - 0xF7
        high_order = (176 + Math.abs(random.nextInt(39)));//39以后很多生僻字
        //低位字节使用了 0xA1 - 0xFE
        low_order = (160 + 1 + Math.abs(random.nextInt(93)));
        byte[] b = new byte[2];
        b[0] = Integer.valueOf(high_order).byteValue();
        b[1] = Integer.valueOf(low_order).byteValue();
        try {
            character = new String(b, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return character.charAt(0);
    }
}
