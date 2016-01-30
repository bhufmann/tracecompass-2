/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.analysis.os.linux.core.signals;

import org.eclipse.tracecompass.tmf.core.signal.TmfSignal;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;

/**
 * A signal to say a thread was selected
 *
 * @author Matthew Khouzam
 * @since 2.0
 */
public class TmfThreadSelectedSignal extends TmfSignal {

    private final int fThreadId;
    private final ITmfTrace fTrace;

    /**
     * Constructor
     *
     * @param source
     *            the source
     * @param threadId
     *            the thread id (normally under 32768)
     * @param trace
     *            the trace
     */
    public TmfThreadSelectedSignal(Object source, int threadId, ITmfTrace trace) {
        super(source);
        fThreadId = threadId;
        fTrace = trace;
    }

    /**
     * Get the thread ID
     *
     * @return the thead ID
     */
    public int getThreadId() {
        return fThreadId;
    }

    /**
     * Get the trace
     *
     * @return the trace
     */
    public ITmfTrace getTrace() {
        return fTrace;
    }

}
