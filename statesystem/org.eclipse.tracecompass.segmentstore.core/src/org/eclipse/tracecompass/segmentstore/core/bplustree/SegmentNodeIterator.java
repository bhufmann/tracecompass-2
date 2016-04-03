package org.eclipse.tracecompass.segmentstore.core.bplustree;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.segmentstore.core.ISegment;

class SegmentNodeIterator implements Iterator<@NonNull ISegment> {

    private SegmentNode fValue;
    private final int fStart;
    private Iterator<ISegment> fIter;
    private final long fClamp;
    /**
     * Unused
     */
    public final ISegmentAspect fAspect;

    public SegmentNodeIterator(SegmentNode value, long key, long clamp, ISegmentAspect aspect) {
        fValue = value;
        fStart = value.indexOf(key);
        fClamp = clamp;
        fAspect = aspect;
        fIter = value.get(fStart);

    }

    public SegmentNodeIterator(SegmentNode value, int i) {
        fValue = value;
        fStart = i;
        fClamp = Long.MAX_VALUE;
        fAspect = t -> Long.MIN_VALUE;
        fIter = value.get(fStart);
    }

    @Override
    public boolean hasNext() {
        return (fValue != null && (fValue.getNext() != null )|| (fIter!=null)&&fIter.hasNext());
    }

    @Override
    public ISegment next() {
        ISegment ret = innerNext();
        while (ret != null && ret.getEnd() < fClamp && fIter != null) {
            ret = innerNext();
        }
        if (ret == null) {
            throw new NoSuchElementException();
        }
        return ret;
    }

    private ISegment innerNext() {
        ISegment ret = fIter.next();
        if (!fIter.hasNext()) {
            fValue = fValue.getNext();
            if (fValue == null) {
                fIter = null;
            } else {
                fIter = fValue.get(0);
            }
        }
        return ret;
    }

}
