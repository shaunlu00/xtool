package com.shaunlu.xtool.test;

import com.shaunlu.xtool.string.NLPTool;
import com.shaunlu.xtool.string.StringTool;
import org.junit.Assert;
import org.junit.Test;

public class StringToolTest {

    @Test
    public void testReplaceChineseDigit() {
//        Assert.assertEquals("这套房子的单价是3904块", StringTool.replaceChineseDigit("这套房子的单价是三千九百零四块"));
//        Assert.assertEquals("说好的是60300000的呢？", StringTool.replaceChineseDigit("说好的是六千零三十万的呢？"));
//        Assert.assertEquals("百姓万亿资产", StringTool.replaceChineseDigit("百姓万亿资产"));
        Assert.assertEquals("10个人", NLPTool.replaceChineseDigit("十个人"));
    }
}
