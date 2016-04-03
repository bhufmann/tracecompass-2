package org.eclipse.tracecompass.segmentstore.core.bplustree;

import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.segmentstore.core.ISegment;

class BPlusTreeNode implements Collection<@NonNull ISegment> {

    private long fSize = 0;
    private final NavigableMap<Long, SegmentNode> fNodes = new TreeMap<>();
    private final ISegmentAspect fSegmentAspect;

    public static BPlusTreeNode createStartTimeBPlusTree() {
        return new BPlusTreeNode(seg -> seg.getStart());
    }

    public static BPlusTreeNode createEndTimeBPlusTree() {
        return new BPlusTreeNode(seg -> seg.getEnd());
    }

    private BPlusTreeNode(ISegmentAspect segmentAspect) {
        fSegmentAspect = segmentAspect;
        fNodes.put((long) 0, new SegmentNode(fSegmentAspect));
    }

    @Override
    public int size() {
        return (int) (fSize > Integer.MAX_VALUE ? Integer.MAX_VALUE : fSize);
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return fSize == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof ISegment) {
            ISegment segment = (ISegment) o;
            SegmentNode node = search(fSegmentAspect.resolve(segment));
            if (node == null) {
                return false;
            }
            return node.contains(segment);
        }
        return false;
    }

    @Override
    public Iterator<@NonNull ISegment> iterator() {
        return new SegmentNodeIterator(fNodes.firstEntry().getValue(), 0);
    }

    @Override
    public Object @NonNull [] toArray() {
        return toArray(new ISegment[size()]);
    }

    @Override
    public <T> T @NonNull [] toArray(T @NonNull [] a) {
        Iterator<@NonNull ISegment> iter = iterator();
        for (int i = 0; i < a.length; i++) {
            if (iter.hasNext()) {
                a[i] = (T) iter.next();
            }
        }
        return a;
    }

    @Override
    public boolean add(ISegment e) {
        fSize++;
        SegmentNode node = search(fSegmentAspect.resolve(e));
        node.add(e);
        return true;
    }

    private SegmentNode search(long key) {
        return fNodes.floorEntry(key).getValue();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends ISegment> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();

    }

    public @NonNull Iterable<@NonNull ISegment> subSet(long position, long clamp) {
        return () -> new SegmentNodeIterator(search(position), position, clamp, fSegmentAspect);
    }

}
