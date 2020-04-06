package com.renqiang.sensitive;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author zoro301
 * @Description 使用map保存子节点的字典树节点
 * @Date 2019/12/27 21:39
 */
public class MapTrieNode extends AbstractTrieNode{

    /**
     * 子节点
     */
    private Map<Character,AbstractTrieNode> children = new TreeMap<>();

    public MapTrieNode(char data) {
        super(data);
    }

    @Override
    public AbstractTrieNode getChildNode(Character data) {

        return children.get(data);
    }

    @Override
    public void setChildNode(Character data) {
        MapTrieNode node = new MapTrieNode(data);
        children.put(data,node);
    }

    @Override
    public Collection<Character> getChildrenDataList() {
        return children.keySet();
    }


}
