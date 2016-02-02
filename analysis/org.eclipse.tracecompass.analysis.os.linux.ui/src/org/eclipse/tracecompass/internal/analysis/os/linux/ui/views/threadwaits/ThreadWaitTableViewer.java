/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.internal.analysis.os.linux.ui.views.threadwaits;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.tracecompass.analysis.timing.core.segmentstore.ISegmentStoreProvider;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.table.AbstractSegmentStoreTableViewer;
import org.eclipse.tracecompass.internal.analysis.os.linux.core.threadwaits.ThreadWaitAnalysis;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceUtils;

/**
 * Displays the latency analysis data in a column table
 *
 * @author Matthew Khouzam
 */
public class ThreadWaitTableViewer extends AbstractSegmentStoreTableViewer {

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructor
     *
     * @param tableViewer
     *            The table viewer
     */
    public ThreadWaitTableViewer(TableViewer tableViewer) {
        super(tableViewer);
    }

    // ------------------------------------------------------------------------
    // Operations
    // ------------------------------------------------------------------------

    @Override
    protected @Nullable ISegmentStoreProvider getSegmentStoreAnalysisModule(ITmfTrace trace) {
        return TmfTraceUtils.getAnalysisModuleOfClass(trace, ThreadWaitAnalysis.class, ThreadWaitAnalysis.ID);
    }
}
