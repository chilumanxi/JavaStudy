package hashmap;

/**
 * MyHashMap class
 *
 * @author zhanghaoran25
 * @date 2023/10/31 18:00
 */
public class MyHashMap<K, V> {

    class Node<K, V>{
        private K key;
        private V value;

        //next
        private Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    //default capacity
    private final int DEFAULT_CAPACITY = 16;
    //expand threshold
    private final float LOAD_FACTOR = 0.75f;
    //size
    private int size;
    //bucket
    Node<K, V>[] buckets;

    //no param constructor
    public MyHashMap() {
        buckets = new Node[DEFAULT_CAPACITY];
        size = 0;
    }

    //param constructor
    public MyHashMap(int capacity) {
        buckets = new Node[capacity];
        size = 0;
    }

    //hash function
    private int getIndex(K key, int length){
        //get hash code
        int hashCode = key.hashCode();
        //get remainder by bucket length
        int index = (length - 1) & hashCode;
        return Math.abs(index);
    }

    //resize
    public void resize(){
        // double bucket length
        Node<K, V>[] newBuckets = new Node[buckets.length * 2];
        //put all items to new bucket
        rehash(newBuckets);
        //let old bucket direct to new bucket
        buckets = newBuckets;
    }

    //rehash
    private void rehash(Node<K, V>[] newBuckets) {
        //init size
        size = 0;
        //rehash
        for (Node<K, V> kvNode : buckets) {
            //if empty, continue
            if (kvNode == null) {
                continue;
            }
            Node<K, V> node = kvNode;
            while (node != null) {
                putVal(node.key, node.value, newBuckets);
                node = node.next;
            }
        }
    }

    private void putVal(K key, V value, Node<K, V>[] bucket) {
        //get index
        int index = getIndex(key, bucket.length);
        Node<K, V> node = bucket[index];
        //if empty
        if(node == null){
            bucket[index] = new Node<>(key, value);
            size ++;
            return ;
        }
        //if not empty, conflict
        while(node != null){
            // if key equal, then cover
            if(node.key.hashCode() == key.hashCode() && (node.key == key || node.key.equals(key))){
                node.value = value;
                return ;
            }
            node = node.next;
        }
        //if not exist in bucket, add at head
        Node<K, V> newNode = new Node<>(key, value, bucket[index]);
        bucket[index] = newNode;
        size ++;


    }


    //put
    public void put(K key, V value){
        //check if need expand
        if(size >= buckets.length * LOAD_FACTOR){
            resize();
        }
        putVal(key, value, buckets);
    }

    //get
    public V get(K key){
        //get index
        int index = getIndex(key, buckets.length);
        if(buckets[index] == null){
            //can not get
            return null;
        }
        //search link
        Node<K, V> node = buckets[index];
        while(node != null){
            if(node.key.hashCode() == key.hashCode() && (node.key == key || node.key.equals(key))){
                return node.value;
            }
            node = node.next;
        }
        return null;
    }


    public static void main(String[] args){
        MyHashMap<String, String> mp = new MyHashMap<>();
        for(int i = 0; i < 100; i ++){
            mp.put("key_" + i,  "value_" + i);
        }
        System.out.println(mp.size);
        for(int i = 0; i < 100; i ++){
            System.out.println(mp.get("key_" + i));
        }
    }

}