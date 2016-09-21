/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.internal.tmf.analysis.xml.ui.views.latency;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.density.AbstractSegmentStoreDensityView;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.density.AbstractSegmentStoreDensityViewer;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.table.AbstractSegmentStoreTableViewer;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.internal.tmf.analysis.xml.ui.TmfXmlUiStrings;
import org.eclipse.tracecompass.internal.tmf.analysis.xml.ui.views.XmlLatencyViewInfo;

/**
 * Displays the latency density view for pattern analysis
 *
 * @author Jean-Christian Kouame
 */
public class PatternDensityView extends AbstractSegmentStoreDensityView {

    /** The view's ID */
    public static final @NonNull String ID = "org.eclipse.tracecompass.internal.tmf.analysis.xml.ui.views.density"; //$NON-NLS-1$

    private final XmlLatencyViewInfo fViewInfo = new XmlLatencyViewInfo(ID);

    private PatternDensityViewer fDensityViewer;
    private PatternLatencyTableViewer fTableViewer;

    /**
     * Constructor
     */
    public PatternDensityView() {
        super(ID);
        this.addPartPropertyListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(TmfXmlUiStrings.XML_LATENCY_OUTPUT_DATA)) {
                    Object newValue = event.getNewValue();
                    if (newValue instanceof String) {
                        String data = (String) newValue;
                        fViewInfo.setViewData(data);
                        loadTableViewer();
                        loadDensityViewer();
                    }
                }
            }
        });
    }

    private void loadTableViewer() {
        if (fTableViewer != null) {
            fTableViewer.updateViewer(fViewInfo.getViewAnalysisId());
        }
    }

    private void loadDensityViewer() {
        if (fDensityViewer != null) {
            fDensityViewer.updateViewer(fViewInfo.getViewAnalysisId());
        }
    }

    @Override
    protected @NonNull AbstractSegmentStoreTableViewer createSegmentStoreTableViewer(@NonNull Composite parent) {
        PatternLatencyTableViewer tableViewer = new PatternLatencyTableViewer(new TableViewer(parent, SWT.FULL_SELECTION | SWT.VIRTUAL)) {
            @Override
            protected void createProviderColumns() {
                super.createProviderColumns();
//                Table t = (Table) getControl();
//                t.setColumnOrder(new int[] { 0, 1, 2, 3, 4});
            }
        };
        fTableViewer = tableViewer;
        loadTableViewer();
        return tableViewer;
    }

    @Override
    protected @NonNull AbstractSegmentStoreDensityViewer createSegmentStoreDensityViewer(@NonNull Composite parent) {
        PatternDensityViewer densityViewer = new PatternDensityViewer(NonNullUtils.checkNotNull(parent));
        fDensityViewer = densityViewer;
        loadDensityViewer();
        return densityViewer;
    }
}
