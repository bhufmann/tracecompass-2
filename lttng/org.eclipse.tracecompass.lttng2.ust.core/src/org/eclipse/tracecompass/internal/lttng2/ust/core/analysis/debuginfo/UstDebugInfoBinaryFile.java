/*******************************************************************************
 * Copyright (c) 2015 EfficiOS Inc., Alexandre Montplaisir
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.internal.lttng2.ust.core.analysis.debuginfo;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Wrapper class to reference to a particular binary, which can be an
 * executable or library. It contains both the complete file path (at the
 * time the trace was taken) and the build ID of the binary.
 */
public final class UstDebugInfoBinaryFile implements Comparable<UstDebugInfoBinaryFile> {

    private final String filePath;
    private final String buildId;
    private final String toString;

    /**
     * Constructor
     *
     * @param filePath
     *            The binary's path on the filesystem
     * @param buildId
     *            The binary's unique buildID (in base16 form).
     */
    public UstDebugInfoBinaryFile(String filePath, String buildId) {
        this.filePath = filePath;
        this.buildId = buildId;
        this.toString = new String(filePath + " (" + buildId + ')'); //$NON-NLS-1$
    }

    /**
     * Get the file's path, as was referenced to in the trace.
     *
     * @return The file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Get the build ID of the binary. It should be a unique identifier.
     *
     * On Unix systems, you can use <pre>eu-readelf -n [binary]</pre> to get
     * this ID.
     *
     * @return The file's build ID.
     */
    public String getBuildId() {
        return buildId;
    }

    @Override
    public String toString() {
        return toString;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof UstDebugInfoBinaryFile)) {
            return false;
        }
        UstDebugInfoBinaryFile other = (UstDebugInfoBinaryFile) obj;
        if (this.filePath == other.filePath &&
                this.buildId == other.buildId) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + buildId.hashCode();
        result = prime * result +  filePath.hashCode();
        return result;
    }

    /**
     * Used for sorting. Sorts by using alphabetical order of the file
     * paths.
     */
    @Override
    public int compareTo(@Nullable UstDebugInfoBinaryFile o) {
        if (o == null) {
            return 1;
        }
        return this.filePath.compareTo(o.filePath);
    }
}