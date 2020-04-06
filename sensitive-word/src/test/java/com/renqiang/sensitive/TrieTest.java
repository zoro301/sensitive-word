package com.renqiang.sensitive;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author renqiang
 * @Description
 * @Date 2019/12/5 18:42
 */
@RunWith(SpringRunner.class)


public class TrieTest {

    @Test
    public void testExistSensitive(){
        SensitiveWordFilter trie = SensitiveWordFilter.getInstance();
        String content = "成为海贼王的男人";
        boolean exist = trie.existSensitive(content);
        System.out.println(exist);
        Assert.assertTrue(exist);
    }
}
