package com.renqiang.sensitive;

import java.util.Collection;

/**
 * @Author zoro301
 * @Description 节点抽象类,使用不同数据结构类型的子节点可实现相应子类
 * @Date 2019/12/27 21:22
 */
public abstract class AbstractTrieNode {

    /**
     * 节点值
     */
    private char data;

    /**
     * 是否是模式串的结束节点
     */
    private boolean isEndChar = false;

    /**
     * 失败节点
     */
    private AbstractTrieNode failNode;

    /**
     * 模式串长度
     */
    private int length = -1;//模式串长度

    public AbstractTrieNode(char data){
        this.data = data;
    }

    public abstract AbstractTrieNode getChildNode(Character data);

    public abstract void setChildNode(Character data);

    public abstract Collection<Character> getChildrenDataList();

    public char getData() {
        return data;
    }

    public void setData(char data) {
        this.data = data;
    }

    public boolean isEndChar() {
        return isEndChar;
    }

    public void setEndChar(boolean endChar) {
        isEndChar = endChar;
    }

    public AbstractTrieNode getFailNode() {
        return failNode;
    }

    public void setFailNode(AbstractTrieNode failNode) {
        this.failNode = failNode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
