import java.util.NoSuchElementException;

/**
 * Min-Heap Class.
 * 
 * @author Walker Todd
 * @version 5/2/2023
 * 
 *          My work complies with the JMU Honor Code and if any help was recieved it was from a TA
 *          and was listed where the help was recieved
 *
 */

public class MinHeap<E extends Comparable<E>> {

  public final E[] items;

  public int size;

  /**
   * MinHeap Constructor.
   * 
   * @param capacity the capacity of the minheap
   */
  @SuppressWarnings("unchecked")
  public MinHeap(int capacity) {
    items = (E[]) new Comparable[capacity];
    this.size = 0;
  }

  /**
   * size.
   * 
   * @return the size
   */
  public int size() {
    return size;
  }

  /**
   * IsEmpty.
   * 
   * @return return if it is empty
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * enqueue.
   * 
   * @param item the item to queued
   */
  public void enqueue(E item) {
    // -2?
    if (size == items.length) {
      throw new IllegalStateException();
    }
    items[size] = item;
    size++;
    percolateUp(size - 1);
  }

  /**
   * dequeue.
   * 
   * @return the item that is removed
   */
  public E dequeue() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    E minItem = items[0];
    size--;
    items[0] = items[size];
    items[size] = null;
    percolateDown(0);
    return minItem;
  }

  /**
   * peek.
   * 
   * @return the front item
   */
  public E peek() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return items[0];
  }


  /**
   * percolateUp.
   * 
   * @param nodeIndex the node index
   */
  public void percolateUp(int nodeIndex) {
    while (nodeIndex > 0) {
      // Compute the parent node's index
      int parentIndex = (nodeIndex - 1) / 2;

      // Check for a violation of the max heap property
      if (items[nodeIndex].compareTo(items[parentIndex]) > 0) {
        // No violation, so percolate up is done.
        return;
      } else {
        // Swap heapArray[nodeIndex] and heapArray[parentIndex]
        E temp = items[nodeIndex];
        items[nodeIndex] = items[parentIndex];
        items[parentIndex] = temp;

        // Continue the loop from the parent node
        nodeIndex = parentIndex;
      }
    }
  }

  /**
   * percolateDown.
   * 
   * @param nodeIndex the node index
   */
  public void percolateDown(int nodeIndex) {
    int childIndex = 2 * nodeIndex + 1;
    E value = items[nodeIndex];

    while (childIndex < size) {
      // Find the max among the node and all the node's children
      E maxValue = value;
      int maxIndex = -1;
      int i = 0;
      while (i < 2 && i + childIndex < size) {
        if (items[i + childIndex].compareTo(maxValue) < 0) {
          maxValue = items[i + childIndex];
          maxIndex = i + childIndex;
        }
        i++;
      }

      // Check for a violation of the max heap property
      if (maxValue == value) {
        return;
      } else {
        // Swap heapArray[nodeIndex] and heapArray[maxIndex]
        E temp = items[nodeIndex];
        items[nodeIndex] = items[maxIndex];
        items[maxIndex] = temp;

        // Continue loop from the max index node
        nodeIndex = maxIndex;
        childIndex = 2 * nodeIndex + 1;
      }
    }
  }
}
