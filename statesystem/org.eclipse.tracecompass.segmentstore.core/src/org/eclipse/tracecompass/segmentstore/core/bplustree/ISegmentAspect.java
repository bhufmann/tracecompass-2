package org.eclipse.tracecompass.segmentstore.core.bplustree;

import org.eclipse.tracecompass.segmentstore.core.ISegment;

interface ISegmentAspect {

    long resolve(ISegment seg);
}
