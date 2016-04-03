package org.eclipse.tracecompass.segmentstore.core.bplustree;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.segmentstore.core.ISegment;
import org.eclipse.tracecompass.segmentstore.core.ISegmentStore;

public class BPlusSegmentStore implements ISegmentStore<@NonNull ISegment> {

    BPlusTreeNode fSortedFirst = BPlusTreeNode.createStartTimeBPlusTree();

    @Override
    public int size() {
        return fSortedFirst.size();
    }

    @Override
    public boolean isEmpty() {
        return fSortedFirst.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        boolean contains = false;
        synchronized (fSortedFirst) {
            contains = fSortedFirst.contains(o);
        }
        return contains;
    }

    @Override
    public Iterator<@NonNull ISegment> iterator() {
        return fSortedFirst.iterator();
    }

    @Override
    public Object @NonNull [] toArray() {
        return fSortedFirst.toArray();
    }

    @Override
    public <T> T @NonNull [] toArray(T @NonNull [] a) {
        return fSortedFirst.toArray(a);
    }

    @Override
    public boolean add(@NonNull ISegment e) {
        synchronized (fSortedFirst) {
            fSortedFirst.add(e);
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends @NonNull ISegment> c) {
        c.forEach(a->add(a));
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    @Override
    public @NonNull Iterable<@NonNull ISegment> getIntersectingElements(long position) {
        return fSortedFirst.subSet( position, position);
    }

    @Override
    public @NonNull Iterable<@NonNull ISegment> getIntersectingElements(long start, long end) {
        return fSortedFirst.subSet( start, end);
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

}
