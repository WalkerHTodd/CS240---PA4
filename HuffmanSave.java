import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Simple container class that stores a bit sequence representing a Huffman
 * encoded file along with frequency information that may be used to reconstruct
 * the Huffman tree used for the encoding. 
 * 
 * <p>The frequency HashMap maps from bytes to the number of
 * occurrences of those bytes in the source document.
 *
 * @author Nathan Sprague
 * @version 4/2023
 *
 */
public class HuffmanSave implements Serializable {

  @Serial
  private static final long serialVersionUID = 2L;

  private BitSequence encoding;
  private HashMap<Byte, Long> frequencies;

  /**
   * Create a HuffmanSave object from a bit sequence and a frequency HashMap.
   */
  public HuffmanSave(BitSequence encoding, HashMap<Byte, Long> frequencies) {
    this.encoding = encoding;
    this.frequencies = frequencies;
  }

  /**
   * Return the bit sequence.
   */
  public BitSequence getEncoding() {
    return encoding;
  }

  /**
   * Return the frequency map.
   */
  public HashMap<Byte, Long> getFrequencies() {
    return frequencies;
  }

}
