/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.internal.analysis.os.linux.core.threadwaits;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.analysis.os.linux.core.signals.TmfThreadSelectedSignal;
import org.eclipse.tracecompass.analysis.timing.core.segmentstore.IAnalysisProgressListener;
import org.eclipse.tracecompass.analysis.timing.core.segmentstore.ISegmentStoreProvider;
import org.eclipse.tracecompass.internal.analysis.timing.core.segmentstore.statistics.SegmentStoreStatistics;
import org.eclipse.tracecompass.segmentstore.core.ISegment;
import org.eclipse.tracecompass.segmentstore.core.ISegmentStore;
import org.eclipse.tracecompass.tmf.core.analysis.IAnalysisModule;
import org.eclipse.tracecompass.tmf.core.analysis.TmfAbstractAnalysisModule;
import org.eclipse.tracecompass.tmf.core.exceptions.TmfAnalysisException;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalHandler;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceUtils;

import com.google.common.collect.ImmutableList;

/**
 * Analysis module to calculate statistics of a latency analysis
 *
 * @author Matthew Khouzam
 */
public class ThreadWaitStatisticsAnalysis extends TmfAbstractAnalysisModule {

    /** The analysis module ID */
    public static final String ID = "org.eclipse.tracecompass.analysis.os.linux.core.theadwait.statistics"; //$NON-NLS-1$

    private @Nullable ThreadWaitAnalysis fThreadWaitModule;

    private @Nullable SegmentStoreStatistics fTotalStats;

    private ITmfTrace fTrace;

    private ListenerList fListeners = new ListenerList();

    /**
     * Add a listener for the viewers
     *
     * @param listener
     *            listener for each type of viewer
     */
    public void addCallback(Runnable runner) {
        fListeners.add(runner);
    }

    /**
     * Remove listener for the viewers
     *
     * @param listener
     *            listener for each type of viewer
     */
    public void removeListener(Runnable runner) {
        fListeners.remove(runner);
    }

    private int fThreadId;
    private @NonNull Job fJob = new Job("") { //$NON-NLS-1$
        @Override
        protected IStatus run(IProgressMonitor monitor) {
            return Status.CANCEL_STATUS;
        }
    };

    private Iterable<Runnable> getListeners() {
        List<Runnable> listeners = new ArrayList<>();
        for (Object listener : fListeners.getListeners()) {
            if (listener instanceof Runnable) {
                listeners.add((Runnable) listener);
            }
        }
        return listeners;
    }

    private @NonNull final IAnalysisProgressListener fListener = new IAnalysisProgressListener() {

        @Override
        public void onComplete(@NonNull ISegmentStoreProvider activeAnalysis, @NonNull ISegmentStore<@NonNull ISegment> data) {
            Job job = fJob;
            job.cancel();
            job = new Job("Stats for " + fTrace.getName() + "/" + fThreadId) { //$NON-NLS-1$ //$NON-NLS-2$
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        boolean executeAnalysis = executeAnalysis(monitor);
                        for (Runnable listener : getListeners()) {
                            listener.run();
                        }
                        return executeAnalysis ? Status.OK_STATUS : new Status(IStatus.ERROR, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
                    } catch (TmfAnalysisException e) {
                        return new Status(IStatus.ERROR, "", e.getMessage()); //$NON-NLS-1$
                    }
                }
            };
            job.schedule();
        }
    };

    @Override
    protected Iterable<IAnalysisModule> getDependentAnalyses() {
        ITmfTrace trace = fTrace;
        if (trace != null) {
            ThreadWaitAnalysis module = TmfTraceUtils.getAnalysisModuleOfClass(trace, ThreadWaitAnalysis.class, checkNotNull(ThreadWaitAnalysis.ID));
            if (module != null) {
                fThreadWaitModule = module;
                return checkNotNull(ImmutableList.of((IAnalysisModule) module));
            }
        }
        return super.getDependentAnalyses();
    }

    /**
     * Handle a new thread being selected
     *
     * @param signal
     */
    @TmfSignalHandler
    public void updateThread(TmfThreadSelectedSignal signal) {
        ITmfTrace trace = signal.getTrace();
        fTrace = trace;
        fThreadId = signal.getThreadId();
        ThreadWaitAnalysis module = TmfTraceUtils.getAnalysisModuleOfClass(trace, ThreadWaitAnalysis.class, checkNotNull(ThreadWaitAnalysis.ID));
        if (module != null) {
            module.removeListener(fListener);
            module.addListener(fListener);
            fThreadWaitModule = module;
        }
    }

    @Override
    protected boolean executeAnalysis(@Nullable IProgressMonitor monitor) throws TmfAnalysisException {
        ISegmentStoreProvider latency = fThreadWaitModule;
        ITmfTrace trace = getTrace();
        if ((latency == null) || (trace == null) || (monitor == null)) {
            return false;
        }

        ISegmentStore<@NonNull ISegment> store = latency.getResults();

        if (store != null) {
            return calculateTotalManual(store, monitor);
        }
        return true;
    }

    private boolean calculateTotalManual(ISegmentStore<@NonNull ISegment> store, IProgressMonitor monitor) {
        SegmentStoreStatistics total = new SegmentStoreStatistics();
        for (ISegment segment : store) {
            if (monitor.isCanceled()) {
                return false;
            }
            total.update(checkNotNull(segment));
        }
        fTotalStats = total;
        return true;
    }

    @Override
    protected void canceling() {
    }

    /**
     * The total statistics
     *
     * @return the total statistics
     */
    public @Nullable SegmentStoreStatistics getTotalStats() {
        return fTotalStats;
    }

}
