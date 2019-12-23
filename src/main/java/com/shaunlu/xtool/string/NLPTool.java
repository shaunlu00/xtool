package com.shaunlu.xtool.string;

import com.shaunlu.xtool.EmptyUtil;
import com.shaunlu.xtool.error.ErrorCode;
import com.shaunlu.xtool.error.XToolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class NLPTool {

    public static int getSingleDigitFromChinese(String singleChnDigit) {
        int digit = 0;
        switch (singleChnDigit) {
            case "一":
                digit = 1;
                break;
            case "二":
                digit = 2;
                break;
            case "三":
                digit = 3;
                break;
            case "四":
                digit = 4;
                break;
            case "五":
                digit = 5;
                break;
            case "六":
                digit = 6;
                break;
            case "七":
                digit = 7;
                break;
            case "八":
                digit = 8;
                break;
            case "九":
                digit = 9;
                break;
            default:
                throw new XToolException(ErrorCode.BAD_PARAMETER);
        }
        return digit;
    }

    public static String replaceChineseDigit(String text) {
        String result = text;
        Stack<CHNDigit> digits = new Stack<>();
        List<CHNDigitResult> chnDigitResults = new ArrayList<>();
        char[] chars = text.toCharArray();
        int digitStartOffset = -1;
        int digitEndOffset = digitStartOffset;
        for (int i = 0; i < chars.length; i++) {
            Character cha = chars[i];
            if (chaMap.containsKey(cha)) {
                // 如果是中文数字
                CHNDigit chnDigit = chaMap.get(cha);
                if (1 == chnDigit.type || (digits.isEmpty() && cha == '十')) {
                    if (-1 == digitStartOffset) {
                        digitStartOffset = i;
                    }
                    digits.push(chnDigit);
                } else if (2 == chnDigit.type && !digits.isEmpty()) {
                    CHNDigit lastDigit = digits.pop();
                    CHNDigit newDigit = new CHNDigit(1, chnDigit.value * lastDigit.value);
                    digits.push(newDigit);
                } else if (3 == chnDigit.type && !digits.isEmpty()) {
                    CHNDigit newDigit = new CHNDigit(1, 0);
                    while (!digits.isEmpty()) {
                        newDigit.value += digits.pop().value;
                    }
                    newDigit.value = newDigit.value * chnDigit.value;
                    digits.push(newDigit);
                }
            } else {
                // 如果不是中文数字，将栈中的中文数字进行计算
                if (-1 != digitStartOffset && digitEndOffset != digitStartOffset) {
                    digitEndOffset = i;
                    long value = 0;
                    while (!digits.isEmpty()) {
                        value += digits.pop().value;
                    }
                    chnDigitResults.add(new CHNDigitResult(digitStartOffset, digitEndOffset, value));
                    digitStartOffset = digitEndOffset = -1;
                }
            }
        }
        if (!digits.isEmpty()) {
            digitEndOffset = chars.length;
            long value = 0;
            while (!digits.isEmpty()) {
                value += digits.pop().value;
            }
            chnDigitResults.add(new CHNDigitResult(digitStartOffset, digitEndOffset, value));
        }

        if (!EmptyUtil.isEmpty(chnDigitResults)) {
            result = "";
            int i = 0;
            for (CHNDigitResult digitResult : chnDigitResults) {
                for (; i < digitResult.startOffset; i++) {
                    result += chars[i];
                }
                result += String.valueOf(digitResult.value);
                i = digitResult.endOffset;
            }
            for (; i < chars.length; i++) {
                result += chars[i];
            }
        }
        return result;
    }

    private static HashMap<Character, CHNDigit> chaMap = new HashMap<>();

    static {
        chaMap.put('零', new CHNDigit(1, 0));
        chaMap.put('一', new CHNDigit(1, 1));
        chaMap.put('二', new CHNDigit(1, 2));
        chaMap.put('三', new CHNDigit(1, 3));
        chaMap.put('四', new CHNDigit(1, 4));
        chaMap.put('五', new CHNDigit(1, 5));
        chaMap.put('六', new CHNDigit(1, 6));
        chaMap.put('七', new CHNDigit(1, 7));
        chaMap.put('八', new CHNDigit(1, 8));
        chaMap.put('九', new CHNDigit(1, 9));
        chaMap.put('十', new CHNDigit(2, 10));
        chaMap.put('百', new CHNDigit(2, 100));
        chaMap.put('千', new CHNDigit(2, 1000));
        chaMap.put('万', new CHNDigit(3, 10000));
        chaMap.put('亿', new CHNDigit(3, 100000000));
    }


    private static class CHNDigit {

        int type;   // 1为数字，2为一级单位（十、百、千）, 3为二级单位（万、亿）

        long value;

        public CHNDigit(int type, long value) {
            this.type = type;
            this.value = value;
        }
    }

    private static class CHNDigitResult {
        int startOffset;
        int endOffset;
        long value;

        public CHNDigitResult(int startOffset, int endOffset, long value) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.value = value;
        }
    }
}
