package bstmap;

import java.util.Iterator;
import java.util.Set;


public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    private BSTNode root;
    private int size;

    private class BSTNode <K, V> {
        private K key;
        private V value;
        private BSTNode <K, V> left;
        private BSTNode <K, V> right;

        public BSTNode (K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    public BSTMap() {
        root = null;
        size = 0;
    }


    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }


    @Override
    public boolean containsKey(K key) {
        return containsKey_help(root, key);
    }

    private boolean containsKey_help(BSTNode node, K key) {
        if (node == null) {
            return false;
        }
        if (node.key.equals(key)) {
            return true;
        }
        int cmp=key.compareTo((K) node.key);
        if (cmp<0) {
            return containsKey_help(node.left, key);
        } else  {
            return containsKey_help(node.right, key);
        }
    }

    @Override
    public V get(K key) {
        return get_help(root, key);
    }

    private V get_help(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        if (node.key.equals(key)) {
            return (V) node.value;
        }
        int cmp=key.compareTo((K) node.key);
        if (cmp<0) {
            return (V) get_help(node.left, key);
        } else  {
            return (V) get_help(node.right, key);
        }
    }


    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void put(K key, V value) {
        root = put_help(root, key, value);
    }
    private BSTNode put_help(BSTNode node, K key, V value) {
        if (node == null) {
            size++;
            return new BSTNode(key, value);
        }

        int cmp = key.compareTo((K) node.key);

        if (cmp == 0) {
            node.value = value;
        } else if (cmp < 0) {
            node.left = put_help(node.left, key, value);
        } else {
            node.right = put_help(node.right, key, value);
        }

        return node;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("unexpected method.");
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("unexpected method.");
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("unexpected method.");
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("unexpected method.");
    }
}
