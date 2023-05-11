import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;


/**
 * Mad-Zip: Zip and Unzip class which will break down a file into frequencies use that to build a
 * huffman tree and then encode that.
 * 
 * @author Walker Todd
 * @version 5/2/2023
 * 
 *          My work complies with the JMU Honor Code and if any help was recieved it was from a TA
 *          and was listed where the help was recieved
 *
 */

public class MadZip {

  /**
   * Zip Method - to build a tree and compress the file.
   * 
   * @param read - file to read
   * @param write file to write
   * @throws IOException if the files cannot be read
   */
  @SuppressWarnings("resource")
  public static void zip(File read, File write) throws IOException {
    // It must determine the frequencies of all bytes in the source file.
    if (read.length() == 0) {
      FileWriter myWriter = new FileWriter(write);
      myWriter.write("");
      myWriter.close();
      return;
    }

    HashMap<Byte, Long> frequencies = new HashMap<Byte, Long>();

    if (!read.canRead()) {
      throw new IOException();
    }


    FileInputStream input = new FileInputStream(read);

    BufferedInputStream reader = new BufferedInputStream(input);

    int byteReader;

    while ((byteReader = reader.read()) != -1) {
      byte byte1 = (byte) byteReader;
      if (!frequencies.containsKey(byte1)) {
        frequencies.put(byte1, (long) 1);
      } else {
        frequencies.put(byte1, frequencies.get(byte1) + 1);
      }
    }

    // Build a Huffman tree with the help of a Min-Heap
    HashMap<Byte, String> map = new HashMap<Byte, String>();
    Node root = buildTree(frequencies);

    huffmanTreeEncoder(root, "", map);

    // Result to top of the File
    input = new FileInputStream(read);

    BufferedInputStream readerCheck = new BufferedInputStream(input);

    BitSequence sequence = new BitSequence();
    int byteCheck;
    while ((byteCheck = readerCheck.read()) != -1) {
      // System.out.println((char) letterVal);
      sequence.appendBits(map.get((byte) byteCheck));
    }

    HuffmanSave encodingMap = new HuffmanSave(sequence, frequencies);
    try {
      FileOutputStream output = new FileOutputStream(write);
      ObjectOutputStream serializing = new ObjectOutputStream(output);
      serializing.writeObject(encodingMap);

      output.close();
      serializing.close();
    } catch (IOException i) {
      i.printStackTrace();
      return;
    }

  }

  /**
   * Unzip Method - Take a huffman save object out the file and use that information to uncompress.
   * 
   * @param read file to read
   * @param write file to write
   * @throws IOException if the files cannot be read
   * @throws ClassNotFoundException if the file does not exist
   */
  public static void unzip(File read, File write) throws IOException, ClassNotFoundException {
    // It must determine the frequencies of all bytes in the source file.
    if (read.length() == 0) {
      FileWriter myWriter = new FileWriter(write);
      myWriter.write("");
      myWriter.close();
      return;
    }
    if (!read.canRead()) {
      throw new IOException();
    }

    // Deserialize
    HuffmanSave hmSave = new HuffmanSave(null, null);
    try {
      FileInputStream fileIn = new FileInputStream(read);
      ObjectInputStream in = new ObjectInputStream(fileIn);

      hmSave = (HuffmanSave) in.readObject();

      in.close();
      fileIn.close();
    } catch (IOException i) {
      i.printStackTrace();
      return;
    } catch (ClassNotFoundException c) {
      c.printStackTrace();
      return;
    }

    // Tree traversal values getting messed up
    Node rootNode = buildTree(hmSave.getFrequencies());

    // Create a table that has the bit value and the translate value
    FileOutputStream myWriter = new FileOutputStream(write);
    if (hmSave.getFrequencies().size() == 1) {
      myWriter.write(rootNode.getValue());
      myWriter.close();
      return;
    }
    huffmanDecoder(hmSave.getEncoding(), rootNode, myWriter);

    myWriter.close();
  }

  private static Node buildTree(HashMap<Byte, Long> frequencies) {

    MinHeap<Node> heap = new MinHeap<>(frequencies.size());

    for (byte bytes : frequencies.keySet()) {
      long byteVal = frequencies.get(bytes);
      Node val = new Node(null, null, bytes, (long) byteVal);

      heap.enqueue(val);
    }
    // Taking 2 nodes off of the heap
    // Getting their sum of the frequncies and then making new node

    while (heap.size() > 1) {
      Node left = heap.dequeue();
      Node right = heap.dequeue();

      long value = left.getFrequency() + right.getFrequency();

      Node val = new Node(left, right, value);
      // System.out.println("MY VALUE " + val.getFrequency());

      heap.enqueue(val);
    }

    // Correct
    // Root node allowing access to whole tree
    Node root = heap.dequeue();

    return root;
  }

  // I used Zybook for inspiration for this method
  private static void huffmanTreeEncoder(Node node, String bytes, HashMap<Byte, String> map) {
    if (node.isLeaf()) {
      // System.out.println(bytes);

      map.put(node.getValue(), bytes);
    } else {
      // Recursive case
      huffmanTreeEncoder(node.getLeft(), bytes + "0", map);

      huffmanTreeEncoder(node.getRight(), bytes + "1", map);
    }
  }

  // I used Zybook for inspiration for this method
  // I got ta help with the restoration by just putting the fileoutputstream
  // Straight into this method so i dont have to store a long string
  private static void huffmanDecoder(BitSequence sequence, Node treeRoot, FileOutputStream mywriter)
      throws IOException {
    Node node = treeRoot;

    for (Integer val : sequence) {
      if (val == 0) {
        node = node.getLeft();
      } else {
        node = node.getRight();
      }

      if (node.isLeaf()) {
        mywriter.write(node.getValue());
        node = treeRoot;
      }
    }
  }

  /**
   * Private inner Node class.
   */
  private static class Node implements Comparable<Node> {

    private Node left;
    private Node right;

    private Byte byte1;
    private Long frequencies;

    public Node(Node left, Node right, byte bit, long freq) {
      this.left = left;
      this.right = right;
      this.byte1 = bit;
      this.frequencies = freq;
    }

    // Parent Node Constructor
    // No need for byte value
    public Node(Node left, Node right, long freq) {
      this.left = left;
      this.right = right;
      this.frequencies = freq;
    }

    public boolean isLeaf() {
      return (left == null && right == null);
    }

    public Node getLeft() {
      return left;
    }

    public Node getRight() {
      return right;
    }

    public byte getValue() {
      return byte1;
    }

    public long getFrequency() {
      return frequencies;
    }

    /**
     * CompareTo Method.
     */
    @Override
    public int compareTo(MadZip.Node other) {
      // I got TA help with this
      int val = this.frequencies.compareTo(other.frequencies);
      if (val == 0 && this.byte1 != null && other.byte1 != null) {
        return this.byte1.compareTo(other.byte1);
      }
      return val;
    }
  }

  public byte leastValueHelper(Node node) {
    if (node.isLeaf()) {
      return node.getValue();
    } else {
      byte leftByte = leastValueHelper(node.getLeft());
      byte rightByte = leastValueHelper(node.getRight());

      if (leftByte < rightByte) {
        return leftByte;
      } else {
        return rightByte;
      }
    }
  }
  // public static void main(String[] args) throws Exception {
  // // zip(new File("./src/mary.txt"), new File("mary_test.madzip"));
  // File bytes = new File("./src/bytes.dat");
  //
  // // zip(bytes, new File("bytes.dat.mz"));
  //
  // // System.out.println("\nSize" + bytes.length());
  // System.out.println(bytes.length());
  // unzip(new File("./src/bytes.dat.mz"), bytes);
  // System.out.println(bytes.length());
  //
  // // unzip(new File("./src/mary.txt.mz"), new File("./src/mary.txt"));
  //
  // // System.out.println(new File("./src/mary.txt").length());
  // // System.out.println(new File("mary_test.madzip").length());
  //
  // }

}
