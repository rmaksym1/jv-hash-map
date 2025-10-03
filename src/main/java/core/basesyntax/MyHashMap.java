package core.basesyntax;

import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int RESIZE_MULTIPLIER = 2;
    private Node<K, V>[] buckets;
    private int size = 0;
    private int capacity;
    private double loadFactor;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        buckets = (Node<K,V>[]) new Node[DEFAULT_CAPACITY];
        size = 0;
        capacity = DEFAULT_CAPACITY;
        loadFactor = DEFAULT_LOAD_FACTOR;
    }

    private int getBucketIndex(K key) {
        if (key == null) {
            return 0;
        }
        int hash = key.hashCode();
        return (hash & 0x7FFFFFFF) % buckets.length;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * RESIZE_MULTIPLIER;;
        Node<K, V>[] newBuckets = (Node<K,V>[]) new Node[newCapacity];

        for (Node<K, V> head : buckets) {
            Node<K, V> current = head;
            while (current != null) {
                Node<K, V> next = current.next;
                int newIndex = current.key == null
                        ? 0
                        : (current.key.hashCode() & 0x7FFFFFFF) % newCapacity;

                current.next = newBuckets[newIndex];
                newBuckets[newIndex] = current;

                current = next;
            }
        }
        buckets = newBuckets;
        capacity = newCapacity;
    }

    @Override
    public void put(K key, V value) {
        int index = getBucketIndex(key);

        if (buckets[index] == null) {
            buckets[index] = new Node<>(key, value, null);
            size++;
            if (size >= capacity * loadFactor) {
                resize();
            }
            return;
        }

        Node<K, V> current = buckets[index];
        while (true) {
            if (Objects.equals(current.key, key)) {
                current.value = value;
                return;
            }

            if (current.next == null) {
                current.next = new Node<>(key, value, null);
                size++;
                if (size >= capacity * loadFactor) {
                    resize();
                }
                return;
            }

            current = current.next;
        }
    }

    @Override
    public V getValue(K key) {
        int index = getBucketIndex(key);

        Node<K, V> current = buckets[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public int getSize() {
        return size;
    }

    private static class Node<K, V> {
        private V value;
        private final K key;
        private Node<K, V> next;

        private Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
