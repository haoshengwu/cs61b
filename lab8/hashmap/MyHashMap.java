package hashmap;

import java.util.*;
import java.util.HashSet;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {


    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int initialSize;
    private double loadFactor;
    private int currentSize;
    private HashSet<K> keySet;

    /** Constructors */
    public MyHashMap() {
        this.loadFactor = 0.75;
        this.initialSize = 16;
        this.buckets = createTable(initialSize);
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
        this.currentSize = 0;
        this.keySet = new HashSet<>();
    }

    public MyHashMap(int initialSize) {
        this.loadFactor = 0.75;
        this.initialSize = initialSize;
        this.buckets = createTable(initialSize);
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
        this.currentSize = 0;
        this.keySet = new HashSet<>();
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.loadFactor = maxLoad;
        this.initialSize = initialSize;
        this.buckets = createTable(initialSize);
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
        this.currentSize = 0;
        this.keySet = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */

    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    private int bucketIndex(K key) {
        return Math.floorMod(key.hashCode(),buckets.length);
    }

    private void resize(int newSize) {
        Collection<Node>[] newTable = createTable(newSize);
        for (int i = 0; i < newTable.length; i++) {
            newTable[i] = createBucket();
        }
        for (int i = 0; i < buckets.length; i++) {
            for (Node node : buckets[i]) {
                int index = Math.floorMod(node.key.hashCode(), newSize);
                newTable[index].add(node);
            }
        }
        this.buckets = newTable;
        this.initialSize = newSize;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        this.currentSize = 0;
        for(int i = 0; i < initialSize; i++) {
            buckets[i]=createBucket();
        }
    }

    @Override
    public boolean containsKey(K key) {
        for (Iterator<Node> it = buckets[bucketIndex(key)].iterator(); it.hasNext(); ) {
            Node node = it.next();
            if(node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        for (Iterator<Node> it = buckets[bucketIndex(key)].iterator(); it.hasNext(); ) {
            Node node = it.next();
            if(node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return currentSize;
    }

    @Override
    public void put(K key, V value) {
        int index = bucketIndex(key);

        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }

        buckets[index].add(createNode(key, value));
        keySet.add(key);
        currentSize++;

        if((double) currentSize/initialSize>loadFactor) {
            resize(initialSize*2);
        }
    }

    @Override
    public Set<K> keySet() {
        return keySet;
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
        return keySet.iterator();
    }
}
