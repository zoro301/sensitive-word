package com.renqiang.sensitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @Author zoro301
 * @Description 敏感词过滤器
 * @Date 2019/12/27 21:44
 */
public class SensitiveWordFilter {

    private static Logger logger = LoggerFactory.getLogger(SensitiveWordFilter.class);

    /**
     * 敏感词词库
     */
    private static String sensitivePath = "/sensitive-word-lib.txt";

    public static class SingletonHolder {
        private static final SensitiveWordFilter INSTANCE = new SensitiveWordFilter(1);
    }

    /**
     * 根节点
     */
    private AbstractTrieNode root;

    /**
     *
     * @param type 1:子节点map数据结构，2: 子节点数组结构(适合ascii)
     */
    public SensitiveWordFilter(Integer type){
        if(type == 1) {
            root = new MapTrieNode('/');
        }else{
            root = new MapTrieNode('/');
        }

        init();
    }

    public void init() {
        loadSensitive();
        buildFailNode();
    }

    public static SensitiveWordFilter getInstance(){
        return SingletonHolder.INSTANCE;
    }

    /**
     * 加载敏感词
     */
    private void loadSensitive() {
        InputStream in = SensitiveWordFilter.class.getResourceAsStream(sensitivePath);
        BufferedReader reader = null;
        String line;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                insertSensitiveWord(line);
            }
        }catch (Exception e){
            logger.error("读取文件错误");
        }finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("关闭BufferedReader报错",e);
                }
            }

            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("关闭InputStream报错",e);
                }
            }
        }
    }

    /**
     * 构建字典树
     * @param sensitiveWord 敏感词 模式串
     */
    private void insertSensitiveWord(String sensitiveWord){
        AbstractTrieNode p = root;
        for(int i=0; i<sensitiveWord.toCharArray().length; i++){
            char word = sensitiveWord.toCharArray()[i];
            AbstractTrieNode child = p.getChildNode(word);
            if(child == null){
                p.setChildNode(word);
            }
            p = p.getChildNode(word);
        }
        p.setLength(sensitiveWord.toCharArray().length);
        p.setEndChar(true);
    }

    /**
     * 构建失败节点
     */
    private void buildFailNode(){
        Queue<AbstractTrieNode> nodes = new LinkedList<>();
        root.setFailNode(null);
        nodes.add(root);
        while (!nodes.isEmpty()){//从跟节点逐级添加失败指针，子节点的失败指针可通过父节点查找，一个子节点的父节点的失败指针下的子节点和自己相等，则该节点既是该节点的失败指针
            AbstractTrieNode currentNode = nodes.remove();
            for(Character character: currentNode.getChildrenDataList()){
                AbstractTrieNode child = currentNode.getChildNode(character);

                //子节点加入队列中
                nodes.add(child);

                if(currentNode == root){//根节点的子节点失败指针都指向根节点
                    child.setFailNode(root);
                }else{
                    AbstractTrieNode nodeFail = currentNode.getFailNode();
                    while (nodeFail != null){
                        AbstractTrieNode childFail = nodeFail.getChildNode(child.getData());
                        if(childFail != null){//父节点的失败指针的子节点和自己相同，则是自己的失败指针
                            child.setFailNode(childFail);
                            break; //及时跳出，如果如果有下一个失败节点，该节点的失败节点会被指向到下一个失败节点，当前已找到的失败节点丢失
                        }

                        nodeFail = nodeFail.getFailNode();
                    }

                    if(nodeFail == null){//未找到失败节点，失败节点指向根节点
                        child.setFailNode(root);
                    }
                }
            }
        }
    }


    /**
     * 判断是否存在敏感词
     * @param content
     * @return
     */
    public boolean existSensitive(String content){
        AbstractTrieNode currentNode = this.root;
        for(int i=0; i<content.toCharArray().length; i++){
            char data = content.toCharArray()[i];
            //1.当前数据在子节点中不存在及查找当前接的失败节点知道根节点位置
            while (currentNode.getChildNode(data) == null &&  currentNode != root){
                currentNode = currentNode.getFailNode();
            }

            //2.如果当前数据在子节点中存在,则下次从子节点开始查找
            currentNode = currentNode.getChildNode(data);
            if(currentNode == null){//在当前模式串下没有匹配到, 则从根节点重新查找(换别的模式串继续匹配)
                currentNode = root;
            }

            AbstractTrieNode tmpNode = currentNode;
            boolean isSensitive = match(tmpNode,i,content);

            //如果匹配到了模式串，代表存在敏感词直接返回,不再继续匹配(还可能匹配到其他敏感词)
            //如需要匹配全部敏感词模式串，则不需要以下if代码
            if(isSensitive){
                return  isSensitive;
            }
        }

        return false;
    }

    /**
     * 检测一系列以失败节点为结尾的路径是否是模式串
     * @param currentNode
     * @param index 外层循环索引
     * @param content
     * @return
     */
    private boolean match(AbstractTrieNode currentNode, int index, String content) {
        boolean isSensitive = false;
        while(currentNode != root){
            if(currentNode.isEndChar()){
                //匹配到的模式串在主串中的起始位置下标, 用于对主串中匹配的关键字进行替换
                int pos = (index - currentNode.getLength()) + 1;
                isSensitive = true;
                logger.info("匹配到的关键字在主串中的起始位置下标: "+pos +" 关键字长度: "+currentNode.getLength());
                logger.info("匹配到敏感词: " + content.substring(pos, pos + currentNode.getLength()));
            }
            currentNode = currentNode.getFailNode();
        }

        return isSensitive;
    }

}
