/*******************************************************************************
 * Copyright (c) 2015, 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bernd Hufmann - Initial API and implementation
 *******************************************************************************/
package org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.statistics;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.tracecompass.analysis.timing.core.segmentstore.statistics.AbstractSegmentStatisticsAnalysis;
import org.eclipse.tracecompass.analysis.timing.core.segmentstore.statistics.SegmentStoreStatistics;
import org.eclipse.tracecompass.analysis.timing.ui.views.segmentstore.SubSecondTimeWithUnitFormat;
import org.eclipse.tracecompass.internal.analysis.timing.ui.Activator;
import org.eclipse.tracecompass.internal.analysis.timing.ui.views.segmentstore.statistics.Messages;
import org.eclipse.tracecompass.tmf.core.analysis.TmfAbstractAnalysisModule;
import org.eclipse.tracecompass.tmf.core.exceptions.TmfAnalysisException;
import org.eclipse.tracecompass.tmf.core.signal.TmfSelectionRangeUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalHandler;
import org.eclipse.tracecompass.tmf.core.signal.TmfWindowRangeUpdatedSignal;
import org.eclipse.tracecompass.tmf.core.timestamp.TmfTimestamp;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.AbstractTmfTreeViewer;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.ITmfTreeColumnDataProvider;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.ITmfTreeViewerEntry;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.TmfTreeColumnData;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.TmfTreeViewerEntry;

/**
 * An abstract tree viewer implementation for displaying segment store
 * statistics
 *
 * @author Bernd Hufmann
 *
 */
public abstract class AbstractSegmentStoreStatisticsViewer extends AbstractTmfTreeViewer {

    private static final Format FORMATTER = new SubSecondTimeWithUnitFormat();

    private @Nullable TmfAbstractAnalysisModule fModule;
    private MenuManager fTablePopupMenuManager;
    private @Nullable IProgressMonitor fMonitor;

    private static final String[] COLUMN_NAMES = new String[] {
            checkNotNull(Messages.SegmentStoreStatistics_LevelLabel),
            checkNotNull(Messages.SegmentStoreStatistics_Statistics_MinLabel),
            checkNotNull(Messages.SegmentStoreStatistics_MaxLabel),
            checkNotNull(Messages.SegmentStoreStatistics_AverageLabel),
            checkNotNull(Messages.SegmentStoreStatisticsViewer_StandardDeviation),
            checkNotNull(Messages.SegmentStoreStatisticsViewer_Count),
            checkNotNull(Messages.SegmentStoreStatisticsViewer_Total)
    };

    /**
     * Constructor
     *
     * @param parent
     *            the parent composite
     */
    public AbstractSegmentStoreStatisticsViewer(Composite parent) {
        super(parent, false);
        setLabelProvider(new SegmentStoreStatisticsLabelProvider());
        fTablePopupMenuManager = new MenuManager();
        fTablePopupMenuManager.setRemoveAllWhenShown(true);
        fTablePopupMenuManager.addMenuListener(manager -> {
            TreeViewer viewer = getTreeViewer();
            ISelection selection = viewer.getSelection();
            if (selection instanceof IStructuredSelection) {
                IStructuredSelection sel = (IStructuredSelection) selection;
                if (manager != null) {
                    appendToTablePopupMenu(manager, sel);
                }
            }
        });
        Menu tablePopup = fTablePopupMenuManager.createContextMenu(getTreeViewer().getTree());
        getTreeViewer().getTree().setMenu(tablePopup);
    }

    /** Provides label for the Segment Store tree viewer cells */
    protected static class SegmentStoreStatisticsLabelProvider extends TreeLabelProvider {

        @Override
        public String getColumnText(@Nullable Object element, int columnIndex) {
            String value = ""; //$NON-NLS-1$
            if (element instanceof HiddenTreeViewerEntry) {
                if (columnIndex == 0) {
                    value = ((HiddenTreeViewerEntry) element).getName();
                }
            } else if (element instanceof SegmentStoreStatisticsEntry) {
                SegmentStoreStatisticsEntry entry = (SegmentStoreStatisticsEntry) element;
                if (columnIndex == 0) {
                    return String.valueOf(entry.getName());
                }
                if (entry.getEntry().getNbSegments() > 0) {
                    if (columnIndex == 1) {
                        value = toFormattedString(entry.getEntry().getMin());
                    } else if (columnIndex == 2) {
                        value = String.valueOf(toFormattedString(entry.getEntry().getMax()));
                    } else if (columnIndex == 3) {
                        value = String.valueOf(toFormattedString(entry.getEntry().getAverage()));
                    } else if (columnIndex == 4) {
                        value = String.valueOf(toFormattedString(entry.getEntry().getStdDev()));
                    } else if (columnIndex == 5) {
                        value = String.valueOf(entry.getEntry().getNbSegments());
                    } else if (columnIndex == 6) {
                        value = String.valueOf(toFormattedString(entry.getEntry().getTotal()));
                    }
                }
            }
            return checkNotNull(value);
        }
    }

    /**
     * Creates the statistics analysis module
     *
     * @return the statistics analysis module
     */
    @Nullable
    protected abstract TmfAbstractAnalysisModule createStatisticsAnalysiModule();

    /**
     * Gets the statistics analysis module
     *
     * @return the statistics analysis module
     */
    @Nullable
    public TmfAbstractAnalysisModule getStatisticsAnalysisModule() {
        return fModule;
    }

    @Override
    protected ITmfTreeColumnDataProvider getColumnDataProvider() {
        return new ITmfTreeColumnDataProvider() {

            @Override
            public List<@Nullable TmfTreeColumnData> getColumnData() {
                /* All columns are sortable */
                List<@Nullable TmfTreeColumnData> columns = new ArrayList<>();
                TmfTreeColumnData column = new TmfTreeColumnData(COLUMN_NAMES[0]);
                column.setAlignment(SWT.RIGHT);
                column.setComparator(new ViewerComparator() {
                    @Override
                    public int compare(@Nullable Viewer viewer, @Nullable Object e1, @Nullable Object e2) {
                        if ((e1 == null) || (e2 == null)) {
                            return 0;
                        }

                        SegmentStoreStatisticsEntry n1 = (SegmentStoreStatisticsEntry) e1;
                        SegmentStoreStatisticsEntry n2 = (SegmentStoreStatisticsEntry) e2;

                        return n1.getName().compareTo(n2.getName());

                    }
                });
                columns.add(column);
                column = new TmfTreeColumnData(COLUMN_NAMES[1]);
                column.setAlignment(SWT.RIGHT);
                column.setComparator(new ViewerComparator() {
                    @Override
                    public int compare(@Nullable Viewer viewer, @Nullable Object e1, @Nullable Object e2) {
                        if ((e1 == null) || (e2 == null)) {
                            return 0;
                        }

                        SegmentStoreStatisticsEntry n1 = (SegmentStoreStatisticsEntry) e1;
                        SegmentStoreStatisticsEntry n2 = (SegmentStoreStatisticsEntry) e2;

                        return Long.compare(n1.getEntry().getMin(), n2.getEntry().getMin());

                    }
                });
                columns.add(column);
                column = new TmfTreeColumnData(COLUMN_NAMES[2]);
                column.setAlignment(SWT.RIGHT);
                column.setComparator(new ViewerComparator() {
                    @Override
                    public int compare(@Nullable Viewer viewer, @Nullable Object e1, @Nullable Object e2) {
                        if ((e1 == null) || (e2 == null)) {
                            return 0;
                        }

                        SegmentStoreStatisticsEntry n1 = (SegmentStoreStatisticsEntry) e1;
                        SegmentStoreStatisticsEntry n2 = (SegmentStoreStatisticsEntry) e2;

                        return Long.compare(n1.getEntry().getMax(), n2.getEntry().getMax());

                    }
                });
                columns.add(column);
                column = new TmfTreeColumnData(COLUMN_NAMES[3]);
                column.setAlignment(SWT.RIGHT);
                column.setComparator(new ViewerComparator() {
                    @Override
                    public int compare(@Nullable Viewer viewer, @Nullable Object e1, @Nullable Object e2) {
                        if ((e1 == null) || (e2 == null)) {
                            return 0;
                        }

                        SegmentStoreStatisticsEntry n1 = (SegmentStoreStatisticsEntry) e1;
                        SegmentStoreStatisticsEntry n2 = (SegmentStoreStatisticsEntry) e2;

                        return Double.compare(n1.getEntry().getAverage(), n2.getEntry().getAverage());

                    }
                });
                columns.add(column);
                column = new TmfTreeColumnData(COLUMN_NAMES[4]);
                column.setAlignment(SWT.RIGHT);
                column.setComparator(new ViewerComparator() {
                    @Override
                    public int compare(@Nullable Viewer viewer, @Nullable Object e1, @Nullable Object e2) {
                        if ((e1 == null) || (e2 == null)) {
                            return 0;
                        }

                        SegmentStoreStatisticsEntry n1 = (SegmentStoreStatisticsEntry) e1;
                        SegmentStoreStatisticsEntry n2 = (SegmentStoreStatisticsEntry) e2;

                        return Double.compare(n1.getEntry().getStdDev(), n2.getEntry().getStdDev());

                    }
                });
                columns.add(column);
                column = new TmfTreeColumnData(COLUMN_NAMES[5]);
                column.setAlignment(SWT.RIGHT);
                column.setComparator(new ViewerComparator() {
                    @Override
                    public int compare(@Nullable Viewer viewer, @Nullable Object e1, @Nullable Object e2) {
                        if ((e1 == null) || (e2 == null)) {
                            return 0;
                        }

                        SegmentStoreStatisticsEntry n1 = (SegmentStoreStatisticsEntry) e1;
                        SegmentStoreStatisticsEntry n2 = (SegmentStoreStatisticsEntry) e2;

                        return Long.compare(n1.getEntry().getNbSegments(), n2.getEntry().getNbSegments());

                    }
                });
                columns.add(column);
                column = new TmfTreeColumnData(COLUMN_NAMES[6]);
                column.setAlignment(SWT.RIGHT);
                column.setComparator(new ViewerComparator() {
                    @Override
                    public int compare(@Nullable Viewer viewer, @Nullable Object e1, @Nullable Object e2) {
                        if ((e1 == null) || (e2 == null)) {
                            return 0;
                        }

                        SegmentStoreStatisticsEntry n1 = (SegmentStoreStatisticsEntry) e1;
                        SegmentStoreStatisticsEntry n2 = (SegmentStoreStatisticsEntry) e2;

                        return Double.compare(n1.getEntry().getTotal(), n2.getEntry().getTotal());

                    }
                });
                columns.add(column);
                column = new TmfTreeColumnData(""); //$NON-NLS-1$
                columns.add(column);
                return columns;
            }

        };
    }

    @Override
    public void initializeDataSource() {
        ITmfTrace trace = getTrace();
        if (trace != null) {
            TmfAbstractAnalysisModule module = createStatisticsAnalysiModule();
            if (module == null) {
                return;
            }
            try {
                module.setTrace(trace);
                module.schedule();
                fModule = module;
            } catch (TmfAnalysisException e) {
                Activator.getDefault().logError("Error initializing statistics analysis module", e); //$NON-NLS-1$
            }
        }
    }

    /**
     * Method to add commands to the context sensitive menu.
     *
     * @param manager
     *            the menu manager
     * @param sel
     *            the current selection
     */
    protected void appendToTablePopupMenu(IMenuManager manager, IStructuredSelection sel) {
        Object element = sel.getFirstElement();
        if ((element instanceof SegmentStoreStatisticsEntry) && !(element instanceof HiddenTreeViewerEntry)) {
            final SegmentStoreStatisticsEntry segment = (SegmentStoreStatisticsEntry) element;
            IAction gotoStartTime = new Action(Messages.SegmentStoreStatisticsViewer_GotoMinAction) {
                @Override
                public void run() {
                    long start = segment.getEntry().getMinSegment().getStart();
                    long end = segment.getEntry().getMinSegment().getEnd();
                    broadcast(new TmfSelectionRangeUpdatedSignal(AbstractSegmentStoreStatisticsViewer.this, TmfTimestamp.fromNanos(start), TmfTimestamp.fromNanos(end)));
                    updateContent(start, end, true);
                }
            };

            IAction gotoEndTime = new Action(Messages.SegmentStoreStatisticsViewer_GotoMaxAction) {
                @Override
                public void run() {
                    long start = segment.getEntry().getMaxSegment().getStart();
                    long end = segment.getEntry().getMaxSegment().getEnd();
                    broadcast(new TmfSelectionRangeUpdatedSignal(AbstractSegmentStoreStatisticsViewer.this, TmfTimestamp.fromNanos(start), TmfTimestamp.fromNanos(end)));
                    updateContent(start, end, true);
                }
            };

            manager.add(gotoStartTime);
            manager.add(gotoEndTime);
        }
    }

    /**
     * Formats a double value string
     *
     * @param value
     *            a value to format
     * @return formatted value
     */
    protected static String toFormattedString(double value) {
        // The cast to long is needed because the formatter cannot truncate the
        // number.
        String percentageString = String.format("%s", FORMATTER.format(value)); //$NON-NLS-1$
        return percentageString;
    }

    /**
     * Class for defining an entry in the statistics tree.
     */
    protected class SegmentStoreStatisticsEntry extends TmfTreeViewerEntry {

        private final SegmentStoreStatistics fEntry;

        /**
         * Constructor
         *
         * @param name
         *            name of entry
         *
         * @param entry
         *            segment store statistics object
         */
        public SegmentStoreStatisticsEntry(String name, SegmentStoreStatistics entry) {
            super(name);
            fEntry = entry;
        }

        /**
         * Gets the statistics object
         *
         * @return statistics object
         */
        public SegmentStoreStatistics getEntry() {
            return fEntry;
        }

    }

    @Override
    protected @Nullable ITmfTreeViewerEntry updateElements(long start, long end, boolean isSelection) {

        TmfAbstractAnalysisModule analysisModule = getStatisticsAnalysisModule();

        if (getTrace() == null || !(analysisModule instanceof AbstractSegmentStatisticsAnalysis)) {
            return null;
        }

        AbstractSegmentStatisticsAnalysis module = (AbstractSegmentStatisticsAnalysis) analysisModule;

        module.waitForCompletion();

        TmfTreeViewerEntry root = new TmfTreeViewerEntry(""); //$NON-NLS-1$
        List<ITmfTreeViewerEntry> entryList = root.getChildren();

        if (isSelection) {
            if (fMonitor != null) {
                fMonitor.setCanceled(true);
            }
            fMonitor = new NullProgressMonitor();
            module.updateSelectionStats(start, end, fMonitor);
            setStats(entryList, module, getSelectionLabel());
        }
        setStats(entryList, module, getTotalLabel());
        return root;
    }

    /**
     * @since 1.2
     */
    protected void setStats(List<ITmfTreeViewerEntry> entryList, AbstractSegmentStatisticsAnalysis module, String rootName) {
        boolean isSelection = !rootName.equals(getTotalLabel()) ? true : false;
        final SegmentStoreStatistics entry = module.getTotalStats(isSelection);
        if (entry != null) {

            if (entry.getNbSegments() == 0) {
                return;
            }
            TmfTreeViewerEntry child = new SegmentStoreStatisticsEntry(checkNotNull(rootName), entry);
            entryList.add(child);

            final Map<@NonNull String, @NonNull SegmentStoreStatistics> perTypeStats = module.getPerSegmentTypeStats(isSelection);
            if (perTypeStats != null) {
                for (Entry<@NonNull String, @NonNull SegmentStoreStatistics> statsEntry : perTypeStats.entrySet()) {
                    child.addChild(new SegmentStoreStatisticsEntry(statsEntry.getKey(), statsEntry.getValue()));
                }
            }
        }
    }

    @Override
    @TmfSignalHandler
    public void windowRangeUpdated(@Nullable TmfWindowRangeUpdatedSignal signal) {
        //Do nothing
    }

    /**
     * Get the type label
     *
     * @return the label
     * @since 1.2
     */
    protected String getTypeLabel() {
        return checkNotNull(Messages.AbstractSegmentStoreStatisticsViewer_types);
    }

    /**
     * Get the total column label
     *
     * @return the totals column label
     * @since 1.2
     */
    protected String getTotalLabel() {
        return checkNotNull(Messages.AbstractSegmentStoreStatisticsViewer_total);
    }

    /**
     * Get the selection column label
     *
     * @return The selection column label
     * @since 1.2
     */
    protected String getSelectionLabel() {
        return checkNotNull(Messages.AbstractSegmentStoreStatisticsViewer_selection);
    }

    /**
     * Class to define a level in the tree that doesn't have any values.
     */
    protected class HiddenTreeViewerEntry extends SegmentStoreStatisticsEntry {
        /**
         * Constructor
         *
         * @param name
         *            the name of the level
         */
        public HiddenTreeViewerEntry(String name) {
            super(name, new SegmentStoreStatistics());
        }
    }

}
