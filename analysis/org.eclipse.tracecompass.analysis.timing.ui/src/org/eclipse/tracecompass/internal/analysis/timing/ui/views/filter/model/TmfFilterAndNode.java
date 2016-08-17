package org.eclipse.tracecompass.internal.analysis.timing.ui.views.filter.model;

import org.eclipse.tracecompass.segmentstore.core.ISegment;

/**
 * @author Jean-Christian Kouame
 * @since 1.1
 *
 */
public class TmfFilterAndNode extends TmfFilterTreeNode {

    /** and node name */
    public static final String NODE_NAME = "AND"; //$NON-NLS-1$
    /** not attribute name */
    public static final String NOT_ATTR = "not"; //$NON-NLS-1$

    private boolean fNot = false;

    private boolean fActive = true;

    /**
     * @param parent the parent node
     */
    public TmfFilterAndNode(ITmfFilterTreeNode parent) {
        super(parent);
    }

    /**
     * @return the NOT state
     */
    public boolean isNot() {
        return fNot;
    }

    /**
     * @param not the NOT state
     */
    public void setNot(boolean not) {
        this.fNot = not;
    }

    @Override
    public String getNodeName() {
        return NODE_NAME;
    }

    @Override
    public boolean matches(ISegment event) {
        for (ITmfFilterTreeNode node : getChildren()) {
            if (! node.matches(event)) {
                return false ^ fNot;
            }
        }
        return true ^ fNot;
    }

    @Override
    public String toString(boolean explicit) {
        StringBuffer buf = new StringBuffer();
        if (fNot) {
            buf.append("not "); //$NON-NLS-1$
        }
        if (getParent() != null && !(getParent() instanceof TmfFilterRootNode) && !(getParent() instanceof TmfFilterNode)) {
            buf.append("( "); //$NON-NLS-1$
        }
        for (int i = 0; i < getChildrenCount(); i++) {
            ITmfFilterTreeNode node = getChildren()[i];
            buf.append(node.toString(explicit));
            if (i < getChildrenCount() - 1) {
                buf.append(" and "); //$NON-NLS-1$
            }
        }
        if (getParent() != null && !(getParent() instanceof TmfFilterRootNode) && !(getParent() instanceof TmfFilterNode)) {
            buf.append(" )"); //$NON-NLS-1$
        }
        return buf.toString();
    }

    @Override
    public boolean isActive() {
        return fActive;
    }

    @Override
    public void setActive(boolean isActive) {
        fActive = isActive;
    }
}
