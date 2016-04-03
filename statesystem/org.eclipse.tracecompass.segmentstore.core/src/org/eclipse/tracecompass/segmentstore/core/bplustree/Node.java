package org.eclipse.tracecompass.segmentstore.core.bplustree;

public abstract class Node {
    public abstract long getMin();

    public abstract long getMax();

    public boolean contains(long val){
        return val >= getMin() && val <= getMax();
    }
}
