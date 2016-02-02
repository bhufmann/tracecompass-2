/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.internal.analysis.os.linux.core.threadwaits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.analysis.os.linux.core.kernelanalysis.Attributes;
import org.eclipse.tracecompass.analysis.os.linux.core.kernelanalysis.KernelAnalysisModule;
import org.eclipse.tracecompass.analysis.os.linux.core.kernelanalysis.StateValues;
import org.eclipse.tracecompass.analysis.os.linux.core.signals.TmfThreadSelectedSignal;
import org.eclipse.tracecompass.analysis.timing.core.segmentstore.IAnalysisProgressListener;
import org.eclipse.tracecompass.analysis.timing.core.segmentstore.ISegmentStoreProvider;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.internal.analysis.os.linux.core.Activator;
import org.eclipse.tracecompass.segmentstore.core.BasicSegment;
import org.eclipse.tracecompass.segmentstore.core.ISegment;
import org.eclipse.tracecompass.segmentstore.core.ISegmentStore;
import org.eclipse.tracecompass.segmentstore.core.treemap.TreeMapStore;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateSystemDisposedException;
import org.eclipse.tracecompass.statesystem.core.exceptions.TimeRangeException;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.statesystem.core.statevalue.ITmfStateValue;
import org.eclipse.tracecompass.tmf.core.analysis.IAnalysisModule;
import org.eclipse.tracecompass.tmf.core.analysis.TmfAbstractAnalysisModule;
import org.eclipse.tracecompass.tmf.core.segment.ISegmentAspect;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalHandler;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

/**
 * An analysis that aggregates all the thread wait periods
 *
 * @author Matthew Khouzam
 */
public class ThreadWaitAnalysis extends TmfAbstractAnalysisModule implements ISegmentStoreProvider {

    /**
     * The ID of this analysis
     */
    public static final @NonNull String ID = "org.eclipse.tracecompass.analysis.os.linux.latency.threadwait"; //$NON-NLS-1$
    private @Nullable ITmfTrace fTrace = null;
    private int fThreadId;
    private final ListenerList fListenerList = new ListenerList(ListenerList.IDENTITY);

    @Override
    public @NonNull String getId() {
        return ID;
    }

    @Override
    protected @Nullable ITmfTrace getTrace() {
        if (fTrace == null) {
            return super.getTrace();
        }
        return fTrace;
    }

    private final ISegmentStore<@NonNull ISegment> fStore = new TreeMapStore<>();
    private @NonNull Job fSSJob = new Job("") { //$NON-NLS-1$
        @Override
        protected IStatus run(IProgressMonitor monitor) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
        }
    };

    @Override
    public @NonNull Iterable<@NonNull ISegmentAspect> getSegmentAspects() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public @Nullable ISegmentStore<@NonNull ISegment> getResults() {
        return fStore;
    }

    private Iterable<IAnalysisProgressListener> getListeners() {
        List<IAnalysisProgressListener> listeners = new ArrayList<>();
        for (Object listener : fListenerList.getListeners()) {
            if (listener != null) {
                listeners.add((IAnalysisProgressListener) listener);
            }
        }
        return listeners;
    }

    /**
     * Signal handler that launches an analysis
     *
     * @param signal
     *            the thread selected signal
     */
    @TmfSignalHandler
    public void updateThread(TmfThreadSelectedSignal signal) {
        fSSJob.cancel();
        fTrace = signal.getTrace();
        fThreadId = signal.getThreadId();
        fSSJob = new Job("Thread wait analysis : for thread " + fThreadId) { //$NON-NLS-1$
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                ISegmentStore<@NonNull ISegment> store = fStore;
                if (monitor == null||store == null) {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Cannot have a null monitor"); //$NON-NLS-1$
                }
                executeAnalysis(monitor);
                for (IAnalysisProgressListener listener : getListeners()) {
                    listener.onComplete(ThreadWaitAnalysis.this, store);
                }
                return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
            }
        };
        fSSJob.schedule();
    }

    @Override
    protected boolean executeAnalysis(@NonNull IProgressMonitor monitor) {
        String threadString = Integer.toString(fThreadId);
        ITmfTrace trace = getTrace();
        if (trace == null) {
            return false;
        }
        IAnalysisModule module = trace.getAnalysisModule(KernelAnalysisModule.ID);
        if (!(module instanceof KernelAnalysisModule)) {
            return false;
        }
        KernelAnalysisModule analysisModule = (KernelAnalysisModule) module;
        ITmfStateSystem ss = analysisModule.getStateSystem();
        if (ss == null) {
            return false;
        }
        ISegmentStore<@NonNull ISegment> store = fStore;
        if (store == null) {
            return false;
        }
        store.clear();
        try {
            int thread = ss.getQuarkAbsolute(Attributes.THREADS, threadString, Attributes.STATUS);
            long curTime = ss.getStartTime();
            do {
                long limit = ss.getCurrentEndTime();
                while (curTime < limit) {
                    if (monitor.isCanceled()) {
                        return false;
                    }
                    ITmfStateInterval interval = ss.querySingleState(curTime, thread);
                    ITmfStateValue stateValue = interval.getStateValue();
                    if (!stateValue.isNull() && (stateValue.unboxInt() == StateValues.PROCESS_STATUS_WAIT_FOR_CPU | stateValue.unboxInt() == StateValues.PROCESS_STATUS_WAIT_BLOCKED)) {
                        long intervalStart = interval.getStartTime();
                        long intervalEnd = interval.getEndTime();
                        while (!stateValue.isNull() && (stateValue.unboxInt() == StateValues.PROCESS_STATUS_WAIT_FOR_CPU | stateValue.unboxInt() == StateValues.PROCESS_STATUS_WAIT_BLOCKED)) {
                            if (monitor.isCanceled()) {
                                return false;
                            }
                            intervalEnd = interval.getEndTime();
                            curTime = interval.getEndTime() + 1;
                            if (curTime < limit) {
                                interval = ss.querySingleState(curTime, thread);
                                stateValue = interval.getStateValue();
                            } else {
                                break;
                            }
                        }
                        store.add(new BasicSegment(intervalStart, intervalEnd));
                    } else {
                        curTime = interval.getEndTime() + 1;
                    }
                }
            } while (!ss.waitUntilBuilt(500));
        } catch (AttributeNotFoundException | StateSystemDisposedException | TimeRangeException e) {
            String message = NonNullUtils.nullToEmptyString(e.getMessage());
            Activator.getDefault().logError(message, e);
            return false;
        }
        for (IAnalysisProgressListener listener : getListeners()) {
            listener.onComplete(this, store);
        }
        return true;

    }

    @Override
    protected void canceling() {
        // do nothing
    }

    @Override
    public void addListener(@NonNull IAnalysisProgressListener listener) {
        fListenerList.add(listener);
    }

    @Override
    public void removeListener(@NonNull IAnalysisProgressListener listener) {
        fListenerList.remove(listener);
    }

}
