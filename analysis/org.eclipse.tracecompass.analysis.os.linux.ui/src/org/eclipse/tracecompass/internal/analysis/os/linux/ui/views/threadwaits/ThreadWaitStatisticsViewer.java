/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.internal.analysis.os.linux.ui.views.threadwaits;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.internal.analysis.os.linux.core.threadwaits.ThreadWaitStatisticsAnalysis;
import org.eclipse.tracecompass.internal.analysis.timing.core.segmentstore.statistics.SegmentStoreStatistics;
import org.eclipse.tracecompass.internal.analysis.timing.ui.views.segmentstore.statistics.AbstractSegmentStoreStatisticsViewer;
import org.eclipse.tracecompass.tmf.core.analysis.TmfAbstractAnalysisModule;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.ITmfTreeViewerEntry;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.TmfTreeViewerEntry;

/**
 * A tree viewer implementation for displaying latency statistics
 *
 * @author Bernd Hufmann
 *
 */
public class ThreadWaitStatisticsViewer extends AbstractSegmentStoreStatisticsViewer {

    /**
     * Constructor
     *
     * @param parent
     *            the parent composite
     */
    public ThreadWaitStatisticsViewer(@NonNull Composite parent) {
        super(parent);
    }

    /**
     * Gets the statistics analysis module
     *
     * @return the statistics analysis module
     */
    @Override
    protected @Nullable TmfAbstractAnalysisModule createStatisticsAnalysiModule() {
        ThreadWaitStatisticsAnalysis module = new ThreadWaitStatisticsAnalysis();
        module.addCallback(() -> {
            updateContent(1, 2, false);
        });
        return module;
    }

    @Override
    protected @Nullable ITmfTreeViewerEntry updateElements(long start, long end, boolean isSelection) {
        if (isSelection || (start == end)) {
            return null;
        }

        TmfAbstractAnalysisModule analysisModule = getStatisticsAnalysisModule();

        if (getTrace() == null || !(analysisModule instanceof ThreadWaitStatisticsAnalysis)) {
            return null;
        }

        ThreadWaitStatisticsAnalysis module = (ThreadWaitStatisticsAnalysis) analysisModule;

        TmfTreeViewerEntry root = new TmfTreeViewerEntry(""); //$NON-NLS-1$
        final SegmentStoreStatistics entry = module.getTotalStats();
        if (entry != null) {

            List<ITmfTreeViewerEntry> entryList = root.getChildren();

            TmfTreeViewerEntry child = new SegmentStoreStatisticsEntry("Total", entry); //$NON-NLS-1$
            entryList.add(child);
        }
        return root;
    }

}
