package org.eclipse.tracecompass.segmentstore.core.bplustree;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.segmentstore.core.ISegment;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

class SegmentNode extends Node {

    private static final int MAX_SIZE = 1000;

    private @Nullable Node fParent;
    private @Nullable SegmentNode fNext;
    private NavigableMap<Long, ISegment> fData;

    private final ISegmentAspect fSegmentAspect;

    public SegmentNode(ISegmentAspect segmentAspect) {
        fSegmentAspect = segmentAspect;
        fData = new TreeMap<>();
    }

    private SegmentNode split() {
        SegmentNode split = new SegmentNode(fSegmentAspect);
        int count = fData.size() / 2;
        long middle = Iterables.get(fData.keySet(), count);
        split.addAll(fData.tailMap(middle));
        split.fNext = fNext;
        fNext = split;
        split.fParent = fParent;
        return split;
    }

    private void addAll(SortedMap<Long, ISegment> map) {
        map.forEach((l,s)->add(s));
    }

    public void add(ISegment seg) {
        fData.put(seg.getStart(), seg);
        if (fData.size() == MAX_SIZE) {
            split();
        }
    }

    public boolean contains(ISegment seg) {
        long key = fSegmentAspect.resolve(seg);
        return fData.subMap(key, key).containsValue(seg);
    }

    @Override
    public long getMin() {
        return fData.firstKey();
    }

    @Override
    public long getMax() {
        return fData.lastKey();
    }

    public int size() {
        return fData.size();
    }

    public SegmentNode getNext() {
        return fNext;
    }

    public Iterator<ISegment> get(int pos) {
        Iterator<ISegment> iterator = fData.values().iterator();
        if (pos != 0) {
            Iterators.advance(iterator, pos);
        }
        return iterator;
    }

    public int indexOf(long parentKey) {
        int i = 0;
        for (long key : fData.keySet()) {
            if (key > parentKey) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
