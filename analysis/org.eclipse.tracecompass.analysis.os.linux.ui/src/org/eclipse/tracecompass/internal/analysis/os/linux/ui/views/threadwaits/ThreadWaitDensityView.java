/******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.internal.analysis.os.linux.ui.views.threadwaits;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.density.AbstractSegmentStoreDensityView;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.density.AbstractSegmentStoreDensityViewer;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.table.AbstractSegmentStoreTableViewer;
import org.eclipse.tracecompass.common.core.NonNullUtils;

/**
 * Thread Wait Density view
 *
 * @author Matthew Khouzam
 */
public class ThreadWaitDensityView extends AbstractSegmentStoreDensityView {

    /** The view's ID */
    public static final String ID = "org.eclipse.tracecompass.analysis.os.linux.views.threadwait.density"; //$NON-NLS-1$

    /**
     * Constructs a new density view.
     */
    public ThreadWaitDensityView() {
        super(ID);
    }

    @Override
    protected AbstractSegmentStoreTableViewer createSegmentStoreTableViewer(Composite parent) {
        return new ThreadWaitTableViewer(new TableViewer(parent, SWT.FULL_SELECTION | SWT.VIRTUAL)) {
            @Override
            protected void createAnalysisColumns() {
                super.createAnalysisColumns();
                Table t = (Table) getControl();
                t.setColumnOrder(new int[] { 1, 2, 0});
            }
        };
    }

    @Override
    protected AbstractSegmentStoreDensityViewer createSegmentStoreDensityViewer(Composite parent) {
        return new ThreadWaitDensityViewer(NonNullUtils.checkNotNull(parent));
    }

}
