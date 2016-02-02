/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ********************************************************************************/

package org.eclipse.tracecompass.internal.analysis.os.linux.ui.views.threadwaits;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.scatter.AbstractSegmentStoreScatterGraphViewer;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.tmf.ui.viewers.xycharts.TmfXYChartViewer;
import org.eclipse.tracecompass.tmf.ui.views.TmfChartView;

/**
 * Thread waits scatter graph view
 *
 * @author Matthew Khouzam
 */
public class ThreadWaitScatterGraphView extends TmfChartView {
    // Attributes
    // ------------------------------------------------------------------------

    /** The view's ID */
    public static final String ID = "org.eclipse.tracecompass.analysis.os.linux.views.threadwait.scatter"; //$NON-NLS-1$

    private @Nullable AbstractSegmentStoreScatterGraphViewer fScatterViewer;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructor
     */
    public ThreadWaitScatterGraphView() {
        super(ID);
    }

    // ------------------------------------------------------------------------
    // ViewPart
    // ------------------------------------------------------------------------

    @Override
    protected TmfXYChartViewer createChartViewer(@Nullable Composite parent) {
        fScatterViewer = new ThreadWaitScatterGraphViewer(NonNullUtils.checkNotNull(parent), "Thread Delays", "Time",
                "Duration");
        return fScatterViewer;
    }

}
