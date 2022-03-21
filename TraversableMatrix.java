import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This class implements adapter/wrapper/decorator design pattern
 */
public class TraversableMatrix implements Traversable<Index> {
    protected final Matrix matrix;
    protected Index startIndex;
    protected Index destinationIndex;


    public TraversableMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Index getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Index startIndex) {
        this.startIndex = startIndex;
    }

    public void setDestinationIndex(Index destinationIndex) {
        this.destinationIndex = destinationIndex;
    }

    /**
     * The function returns start index
     *
     * @return Node<Index>
     * @throws NullPointerException (throw)
     */
    @Override
    public Node<Index> getOrigin() throws NullPointerException {
        if (this.startIndex == null) throw new NullPointerException("start index is not initialized");
        return new Node<>(this.startIndex);

    }

    /**
     * The function returns end index
     *
     * @return Node<Index>
     * @throws NullPointerException (throw)
     */
    @Override
    public Node<Index> getEnd() throws NullPointerException {
        if (this.destinationIndex == null) throw new NullPointerException("Destination index is not initialized");
        return new Node<>(this.destinationIndex);
    }

    /**
     * The function returns all the neighbors with cross that their value is 1
     *
     * @param someNode type:Node<Index>
     * @return Collection<Node < Index>>
     */
    @Override
    public Collection<Node<Index>> getReachableNodes(Node<Index> someNode) {
        List<Node<Index>> reachableIndex = new ArrayList<>();
        for (Index index : this.matrix.getNeighbors(someNode.getData())) {
            if (matrix.getValue(index) == 1) {
                Node<Index> indexNode = new Node<>(index, someNode);
                reachableIndex.add(indexNode);
            }
        }
        return reachableIndex;
    }

    /**
     * The function returns all the neighbors without cross that their value is 1
     *
     * @param someNode type: Node<Index>
     * @return Collection<Node < Index>>
     */
    @Override
    public Collection<Node<Index>> getNeighborsNoCross(Node<Index> someNode) {
        List<Node<Index>> NeighborIndex = new ArrayList<>();
        for (Index index : this.matrix.getNeighborsWithoutCross(someNode.getData())) {
            Node<Index> indexNode = new Node<>(index, someNode);
            NeighborIndex.add(indexNode);
        }

        return NeighborIndex;
    }


    @Override
    public int getValueN(Index someNode) {
        return this.matrix.getValue(someNode);
    }

    @Override
    public void SetStartIndex(Index index) {
        this.setStartIndex(index);
    }

    @Override
    public String toString() {
        return matrix.toString();
    }


}