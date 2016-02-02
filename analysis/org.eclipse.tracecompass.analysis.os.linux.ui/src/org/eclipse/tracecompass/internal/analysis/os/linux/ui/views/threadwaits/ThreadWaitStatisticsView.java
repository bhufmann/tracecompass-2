/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.internal.analysis.os.linux.ui.views.threadwaits;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.internal.analysis.timing.ui.views.segmentstore.statistics.AbstractSegmentStoreStatisticsView;
import org.eclipse.tracecompass.internal.analysis.timing.ui.views.segmentstore.statistics.AbstractSegmentStoreStatisticsViewer;

/**
 * View to display latency statistics.
 *
 * @author Bernd Hufmann
 *
 */
public class ThreadWaitStatisticsView extends AbstractSegmentStoreStatisticsView {

    /** The view ID*/
    public static final String ID = "org.eclipse.tracecompass.analysis.os.linux.ui.views.threadwait.statsview"; //$NON-NLS-1$

    @Override
    protected AbstractSegmentStoreStatisticsViewer createSegmentStoreStatisticsViewer(Composite parent) {
        return checkNotNull((AbstractSegmentStoreStatisticsViewer) new ThreadWaitStatisticsViewer(checkNotNull(parent)));
    }

}
