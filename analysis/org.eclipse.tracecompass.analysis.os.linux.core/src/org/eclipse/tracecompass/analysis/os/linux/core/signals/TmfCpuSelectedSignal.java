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

/**
 * A signal to say a computer core was selected
 *
 * @author Matthew Khouzam
 * @since 2.0
 */
public class TmfCpuSelectedSignal extends TmfSignal {

    private final int fCore;

    /**
     * Constructor
     *
     * @param source
     *            the source
     * @param core
     *            the core number
     */
    public TmfCpuSelectedSignal(Object source, int core) {
        super(source);
        fCore = core;
    }

    /**
     * Get the thread ID
     *
     * @return the thead ID
     */
    public int getCore() {
        return fCore;
    }

}
