/*******************************************************************************
 * Copyright (c) 2012 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Patrick Tasse - Initial API and implementation
 *******************************************************************************/

package org.eclipse.linuxtools.internal.lttng2.kernel.ui.views.controlflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.linuxtools.internal.lttng2.kernel.ui.Messages;
import org.eclipse.linuxtools.lttng2.kernel.core.trace.Attributes;
import org.eclipse.linuxtools.lttng2.kernel.core.trace.CtfKernelTrace;
import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfTimestamp;
import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.event.TmfTimeRange;
import org.eclipse.linuxtools.tmf.core.event.TmfTimestamp;
import org.eclipse.linuxtools.tmf.core.exceptions.AttributeNotFoundException;
import org.eclipse.linuxtools.tmf.core.exceptions.StateValueTypeException;
import org.eclipse.linuxtools.tmf.core.exceptions.TimeRangeException;
import org.eclipse.linuxtools.tmf.core.interval.ITmfStateInterval;
import org.eclipse.linuxtools.tmf.core.signal.TmfExperimentSelectedSignal;
import org.eclipse.linuxtools.tmf.core.signal.TmfRangeSynchSignal;
import org.eclipse.linuxtools.tmf.core.signal.TmfSignalHandler;
import org.eclipse.linuxtools.tmf.core.signal.TmfTimeSynchSignal;
import org.eclipse.linuxtools.tmf.core.statesystem.IStateSystemQuerier;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;
import org.eclipse.linuxtools.tmf.core.trace.TmfExperiment;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.ITimeGraphRangeListener;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.ITimeGraphSelectionListener;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.ITimeGraphTimeListener;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.StateItem;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.TimeGraphCombo;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.TimeGraphPresentationProvider;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.TimeGraphRangeUpdateEvent;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.TimeGraphSelectionEvent;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.TimeGraphTimeEvent;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.model.ITimeEvent;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.model.ITimeGraphEntry;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.model.TimeEvent;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.widgets.Utils;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.widgets.Utils.Resolution;
import org.eclipse.linuxtools.tmf.ui.widgets.timegraph.widgets.Utils.TimeFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;

public class ControlFlowView extends TmfView {

    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------

    /**
     * View ID.
     */
    public static final String ID = "org.eclipse.linuxtools.lttng2.kernel.ui.views.controlflow"; //$NON-NLS-1$

    private static final String PROCESS_COLUMN    = Messages.ControlFlowView_processColumn;
    private static final String TID_COLUMN        = Messages.ControlFlowView_tidColumn;
    private static final String PPID_COLUMN       = Messages.ControlFlowView_ppidColumn;
    private static final String BIRTH_TIME_COLUMN = Messages.ControlFlowView_birthTimeColumn;
    private static final String TRACE_COLUMN      = Messages.ControlFlowView_traceColumn;

    private final String[] COLUMN_NAMES = new String[] {
            PROCESS_COLUMN,
            TID_COLUMN,
            PPID_COLUMN,
            BIRTH_TIME_COLUMN,
            TRACE_COLUMN
    };

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    // The timegraph combo
    private TimeGraphCombo fTimeGraphCombo;

    // The selected experiment
    private TmfExperiment<ITmfEvent> fSelectedExperiment;

    // The timegraph entry list
    private ArrayList<ControlFlowEntry> fEntryList;

    // The start time
    private long fStartTime;

    // The end time
    private long fEndTime;

    // The display width
    private int fDisplayWidth;

    // The zoom thread
    private ZoomThread fZoomThread;
    
    // The next resource action
    private Action fNextResourceAction;
    
    // The previous resource action
    private Action fPreviousResourceAction;

    // ------------------------------------------------------------------------
    // Classes
    // ------------------------------------------------------------------------

    private class TreeContentProvider implements ITreeContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return (ITimeGraphEntry[]) inputElement;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            ITimeGraphEntry entry = (ITimeGraphEntry) parentElement;
            return entry.getChildren();
        }

        @Override
        public Object getParent(Object element) {
            ITimeGraphEntry entry = (ITimeGraphEntry) element;
            return entry.getParent();
        }

        @Override
        public boolean hasChildren(Object element) {
            ITimeGraphEntry entry = (ITimeGraphEntry) element;
            return entry.hasChildren();
        }
        
    }

    private class TreeLabelProvider implements ITableLabelProvider {

        @Override
        public void addListener(ILabelProviderListener listener) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener listener) {
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            ControlFlowEntry entry = (ControlFlowEntry) element;
            if (columnIndex == 0) {
                return entry.getName();
            } else if (columnIndex == 1) {
                return Integer.toString(entry.getThreadId());
            } else if (columnIndex == 2) {
                if (entry.getPPID() > 0) {
                    return Integer.toString(entry.getPPID());
                }
            } else if (columnIndex == 3) {
                return Utils.formatTime(entry.getBirthTime(), TimeFormat.ABSOLUTE, Resolution.NANOSEC);
            } else if (columnIndex == 4) {
                return entry.getTrace().getName();
            }
            return ""; //$NON-NLS-1$
        }
        
    }

    private class ZoomThread extends Thread {
        private long fStartTime;
        private long fEndTime;
        private long fResolution;
        private boolean fCancelled = false;

        public ZoomThread(long startTime, long endTime) {
            super("ControlFlowView zoom"); //$NON-NLS-1$
            fStartTime = startTime;
            fEndTime = endTime;
            fResolution = Math.max(1, (fEndTime - fStartTime) / fDisplayWidth);
        }

        @Override
        public void run() {
            if (fEntryList == null) {
                return;
            }
            for (ControlFlowEntry entry : fEntryList) {
                if (fCancelled) {
                    return;
                }
                zoom(entry);
            }
            redraw();
        }

        private void zoom(ControlFlowEntry entry) {
            List<ITimeEvent> zoomedEventList = getEventList(entry, fStartTime, fEndTime, fResolution);
            entry.setZoomedEventList(zoomedEventList);
            for (ControlFlowEntry child : entry.getChildren()) {
                if (fCancelled) {
                    return;
                }
                zoom(child);
            }
        }

        public void cancel() {
            fCancelled = true;
        }
    }

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    public ControlFlowView() {
        super(ID);
        fDisplayWidth = Display.getDefault().getBounds().width;
    }

    // ------------------------------------------------------------------------
    // ViewPart
    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.eclipse.linuxtools.tmf.ui.views.TmfView#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        fTimeGraphCombo = new TimeGraphCombo(parent, SWT.NONE);

        fTimeGraphCombo.setTreeContentProvider(new TreeContentProvider());

        fTimeGraphCombo.setTreeLabelProvider(new TreeLabelProvider());

        fTimeGraphCombo.setTimeGraphProvider(new TimeGraphPresentationProvider() {
            private static final String UNKNOWN = "UNKNOWN"; //$NON-NLS-1$
            private static final String WAIT = "WAIT"; //$NON-NLS-1$
            private static final String USERMODE = "USERMODE"; //$NON-NLS-1$
            private static final String SYSCALL = "SYSCALL"; //$NON-NLS-1$
            private static final String INTERRUPTED = "INTERRUPTED"; //$NON-NLS-1$

            @Override 
            public String getStateTypeName() {
                return Messages.ControlFlowView_stateTypeName;
            }
            
            @Override
            public StateItem[] getStateTable() {
                return new StateItem[] {
                        new StateItem(new RGB(100, 100, 100), UNKNOWN),
                        new StateItem(new RGB(150, 150, 0), WAIT),
                        new StateItem(new RGB(0, 200, 0), USERMODE),
                        new StateItem(new RGB(0, 0, 200), SYSCALL),
                        new StateItem(new RGB(200, 100, 100), INTERRUPTED)
                };
            }

            @Override
            public int getEventTableIndex(ITimeEvent event) {
                if (event instanceof ControlFlowEvent) {
                    int status = ((ControlFlowEvent) event).getStatus();
                    if (status == Attributes.STATUS_WAIT) {
                        return 1;
                    } else if (status == Attributes.STATUS_RUN_USERMODE) {
                        return 2;
                    } else if (status == Attributes.STATUS_RUN_SYSCALL) {
                        return 3;
                    } else if (status == Attributes.STATUS_INTERRUPTED) {
                        return 4;
                    }
                }
                return 0;
            }

            @Override
            public String getEventName(ITimeEvent event) {
                if (event instanceof ControlFlowEvent) {
                    int status = ((ControlFlowEvent) event).getStatus();
                    if (status == Attributes.STATUS_WAIT) {
                        return WAIT;
                    } else if (status == Attributes.STATUS_RUN_USERMODE) {
                        return USERMODE;
                    } else if (status == Attributes.STATUS_RUN_SYSCALL) {
                        return SYSCALL;
                    } else if (status == Attributes.STATUS_INTERRUPTED) {
                        return INTERRUPTED;
                    }
                }
                return UNKNOWN;
            }
            
            @Override
            public Map<String, String> getEventHoverToolTipInfo(ITimeEvent event) {
                return new HashMap<String, String>();
            }
        });

        fTimeGraphCombo.setTreeColumns(COLUMN_NAMES);

        fTimeGraphCombo.getTimeGraphViewer().addRangeListener(new ITimeGraphRangeListener() {
            @Override
            public void timeRangeUpdated(TimeGraphRangeUpdateEvent event) {
                final long startTime = event.getStartTime();
                final long endTime = event.getEndTime();
                TmfTimeRange range = new TmfTimeRange(new CtfTmfTimestamp(startTime), new CtfTmfTimestamp(endTime));
                TmfTimestamp time = new CtfTmfTimestamp(fTimeGraphCombo.getTimeGraphViewer().getSelectedTime());
                broadcast(new TmfRangeSynchSignal(ControlFlowView.this, range, time));
                if (fZoomThread != null) {
                    fZoomThread.cancel();
                }
                fZoomThread = new ZoomThread(startTime, endTime);
                fZoomThread.start();
            }
        });

        fTimeGraphCombo.getTimeGraphViewer().addTimeListener(new ITimeGraphTimeListener() {
            @Override
            public void timeSelected(TimeGraphTimeEvent event) {
                long time = event.getTime();
                broadcast(new TmfTimeSynchSignal(ControlFlowView.this, new CtfTmfTimestamp(time)));
            }
        });

        fTimeGraphCombo.addSelectionListener(new ITimeGraphSelectionListener() {
            @Override
            public void selectionChanged(TimeGraphSelectionEvent event) {
                //ITimeGraphEntry selection = event.getSelection();
            }
        });

        fTimeGraphCombo.getTimeGraphViewer().setTimeCalendarFormat(true);

        final Thread thread = new Thread("ControlFlowView build") { //$NON-NLS-1$
            @Override
            public void run() {
                if (TmfExperiment.getCurrentExperiment() != null) {
                    selectExperiment(TmfExperiment.getCurrentExperiment());
                }
            }
        };
        thread.start();
        
        // View Action Handling
        makeActions();
        contributeToActionBars();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        fTimeGraphCombo.setFocus();
    }

    // ------------------------------------------------------------------------
    // Signal handlers
    // ------------------------------------------------------------------------

    @TmfSignalHandler
    public void experimentSelected(final TmfExperimentSelectedSignal<? extends ITmfEvent> signal) {
        if (signal.getExperiment().equals(fSelectedExperiment)) {
            return;
        }

        final Thread thread = new Thread("ControlFlowView build") { //$NON-NLS-1$
            @Override
            public void run() {
                selectExperiment(signal.getExperiment());
            }};
        thread.start();
    }

    @TmfSignalHandler
    public void synchToTime(final TmfTimeSynchSignal signal) {
        if (signal.getSource() == this) {
            return;
        }
        final long time = signal.getCurrentTime().normalize(0, -9).getValue();
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (fTimeGraphCombo.isDisposed()) {
                    return;
                }
                fTimeGraphCombo.getTimeGraphViewer().setSelectedTime(time, true, signal.getSource());
            }
        });
    }

    @TmfSignalHandler
    public void synchToRange(final TmfRangeSynchSignal signal) {
        if (signal.getSource() == this) {
            return;
        }
        final long startTime = signal.getCurrentRange().getStartTime().normalize(0, -9).getValue();
        final long endTime = signal.getCurrentRange().getEndTime().normalize(0, -9).getValue();
        final long time = signal.getCurrentTime().normalize(0, -9).getValue();
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (fTimeGraphCombo.isDisposed()) {
                    return;
                }
                fTimeGraphCombo.getTimeGraphViewer().setStartFinishTime(startTime, endTime);
                fTimeGraphCombo.getTimeGraphViewer().setSelectedTime(time, false, signal.getSource());
            }
        });
    }

    // ------------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private void selectExperiment(TmfExperiment<?> experiment) {
        fStartTime = Long.MAX_VALUE;
        fEndTime = Long.MIN_VALUE;
        fSelectedExperiment = (TmfExperiment<ITmfEvent>) experiment;
        fEntryList = new ArrayList<ControlFlowEntry>();
        for (ITmfTrace<?> trace : experiment.getTraces()) {
            if (trace instanceof CtfKernelTrace) {
                CtfKernelTrace ctfKernelTrace = (CtfKernelTrace) trace;
                IStateSystemQuerier ssq = ctfKernelTrace.getStateSystem();
                long start = ssq.getStartTime();
                long end = ssq.getCurrentEndTime();
                fStartTime = Math.min(fStartTime, start);
                fEndTime = Math.max(fEndTime, end);
                List<Integer> threadQuarks = ssq.getQuarks(Attributes.THREADS, "*"); //$NON-NLS-1$
                for (int threadQuark : threadQuarks) {
                    String threadName = ssq.getAttributeName(threadQuark);
                    int threadId = -1;
                    try {
                        threadId = Integer.parseInt(threadName);
                    } catch (NumberFormatException e1) {
                        continue;
                    }
                    if (threadId == 0) { // ignore the swapper thread
                        continue;
                    }
                    int execNameQuark = -1;
                    try {
                        try {
                            execNameQuark = ssq.getQuarkRelative(threadQuark, Attributes.EXEC_NAME);
                        } catch (AttributeNotFoundException e) {
                            continue;
                        }
                        int ppidQuark = ssq.getQuarkRelative(threadQuark, Attributes.PPID);
                        List<ITmfStateInterval> execNameIntervals = ssq.queryHistoryRange(execNameQuark, start, end);
                        long birthTime = -1;
                        for (ITmfStateInterval execNameInterval : execNameIntervals) {
                            if (!execNameInterval.getStateValue().isNull() && execNameInterval.getStateValue().getType() == 1) {
                                String execName = execNameInterval.getStateValue().unboxStr();
                                long startTime = execNameInterval.getStartTime();
                                long endTime = execNameInterval.getEndTime() + 1;
                                if (birthTime == -1) {
                                    birthTime = startTime;
                                }
                                int ppid = -1;
                                if (ppidQuark != -1) {
                                    ITmfStateInterval ppidInterval = ssq.querySingleState(startTime, ppidQuark);
                                    ppid = ppidInterval.getStateValue().unboxInt();
                                }
                                ControlFlowEntry entry = new ControlFlowEntry(threadQuark, ctfKernelTrace, execName, threadId, ppid, birthTime, startTime, endTime);
                                fEntryList.add(entry);
                                entry.addEvent(new TimeEvent(entry, startTime, endTime - startTime));
                            } else {
                                birthTime = -1;
                            }
                        }
                    } catch (AttributeNotFoundException e) {
                        e.printStackTrace();
                    } catch (TimeRangeException e) {
                        e.printStackTrace();
                    } catch (StateValueTypeException e) {
                        e.printStackTrace();
                    }
                }
            }
            buildTree();
            refresh();
            ControlFlowEntry[] entries = fEntryList.toArray(new ControlFlowEntry[0]);
            Arrays.sort(entries);
            for (ControlFlowEntry entry : entries) {
                buildStatusEvents(entry);
            }
        }
    }

    private void buildTree() {
        ArrayList<ControlFlowEntry> rootList = new ArrayList<ControlFlowEntry>();
        for (ControlFlowEntry entry : fEntryList) {
            boolean root = true;
            if (entry.getPPID() > 0) {
                for (ControlFlowEntry parent : fEntryList) {
                    if (parent.getThreadId() == entry.getPPID() &&
                            entry.getStartTime() >= parent.getStartTime() &&
                            entry.getStartTime() <= parent.getEndTime()) {
                        parent.addChild(entry);
                        root = false;
                        break;
                    }
                }
            }
            if (root) {
                rootList.add(entry);
            }
        }
        fEntryList = rootList;
    }

    private void buildStatusEvents(ControlFlowEntry entry) {
        IStateSystemQuerier ssq = entry.getTrace().getStateSystem();
        long start = ssq.getStartTime();
        long end = ssq.getCurrentEndTime();
        long resolution = Math.max(1, (end - start) / fDisplayWidth);
        List<ITimeEvent> eventList = getEventList(entry, entry.getStartTime(), entry.getEndTime(), resolution);
        entry.setEventList(eventList);
        redraw();
        for (ITimeGraphEntry child : entry.getChildren()) {
            buildStatusEvents((ControlFlowEntry) child);
        }
    }

    private List<ITimeEvent> getEventList(ControlFlowEntry entry, long startTime, long endTime, long resolution) {
        startTime = Math.max(startTime, entry.getStartTime());
        endTime = Math.min(endTime, entry.getEndTime());
        if (endTime <= startTime) {
            return null;
        }
        IStateSystemQuerier ssq = entry.getTrace().getStateSystem();
        List<ITimeEvent> eventList = null;
        try {
            int statusQuark = ssq.getQuarkRelative(entry.getThreadQuark(), Attributes.STATUS);
            List<ITmfStateInterval> statusIntervals = ssq.queryHistoryRange(statusQuark, startTime, endTime - 1, resolution);
            eventList = new ArrayList<ITimeEvent>(statusIntervals.size());
            long lastEndTime = -1;
            for (ITmfStateInterval statusInterval : statusIntervals) {
                long time = statusInterval.getStartTime();
                long duration = statusInterval.getEndTime() - time + 1;
                int status = -1;
                try {
                    status = statusInterval.getStateValue().unboxInt();
                } catch (StateValueTypeException e) {
                    e.printStackTrace();
                }
                if (lastEndTime != time && lastEndTime != -1) {
                    eventList.add(new ControlFlowEvent(entry, lastEndTime, time - lastEndTime, 0));
                }
                eventList.add(new ControlFlowEvent(entry, time, duration, status));
                lastEndTime = time + duration;
            }
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        } catch (TimeRangeException e) {
            e.printStackTrace();
        }
        return eventList;
    }

    private void refresh() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (fTimeGraphCombo.isDisposed()) {
                    return;
                }
                ITimeGraphEntry[] entries = fEntryList.toArray(new ITimeGraphEntry[0]);
                Arrays.sort(entries);
                fTimeGraphCombo.setInput(entries);
                fTimeGraphCombo.getTimeGraphViewer().setTimeBounds(fStartTime, fEndTime);
                fTimeGraphCombo.getTimeGraphViewer().setStartFinishTime(fStartTime, fEndTime);
                for (TreeColumn column : fTimeGraphCombo.getTreeViewer().getTree().getColumns()) {
                    column.pack();
                }
            }
        });
    }

    private void redraw() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (fTimeGraphCombo.isDisposed()) {
                    return;
                }
                fTimeGraphCombo.redraw();
                fTimeGraphCombo.update();
            }
        });
    }

    private void makeActions() {
        fPreviousResourceAction = fTimeGraphCombo.getTimeGraphViewer().getPreviousItemAction();
        fPreviousResourceAction.setText(Messages.ControlFlowView_previousProcessActionNameText);
        fPreviousResourceAction.setToolTipText(Messages.ControlFlowView_previousProcessActionToolTipText);
        fNextResourceAction = fTimeGraphCombo.getTimeGraphViewer().getNextItemAction();
        fNextResourceAction.setText(Messages.ControlFlowView_nextProcessActionNameText);
        fNextResourceAction.setToolTipText(Messages.ControlFlowView_nextProcessActionToolTipText);
    }
    
    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(fTimeGraphCombo.getTimeGraphViewer().getShowLegendAction());
        manager.add(new Separator());
        manager.add(fTimeGraphCombo.getTimeGraphViewer().getResetScaleAction());
        manager.add(fTimeGraphCombo.getTimeGraphViewer().getPreviousEventAction());
        manager.add(fTimeGraphCombo.getTimeGraphViewer().getNextEventAction());
        manager.add(fPreviousResourceAction);
        manager.add(fNextResourceAction);
        manager.add(fTimeGraphCombo.getTimeGraphViewer().getZoomInAction());
        manager.add(fTimeGraphCombo.getTimeGraphViewer().getZoomOutAction());
        manager.add(new Separator());
    }
}
