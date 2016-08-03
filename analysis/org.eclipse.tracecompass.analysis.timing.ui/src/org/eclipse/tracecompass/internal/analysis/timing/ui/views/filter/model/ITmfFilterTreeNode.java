/*******************************************************************************
 * Copyright (c) 2010, 2014 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Francois Godin (copelnug@gmail.com) - Initial API
 *   Yuriy Vashchuk (yvashchuk@gmail.com) - Initial implementation
 *******************************************************************************/

package org.eclipse.tracecompass.internal.analysis.timing.ui.views.filter.model;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * <h4>This is Node Interface in the Filter Tree</h4>
 * @since 1.1
 */
public interface ITmfFilterTreeNode extends ISegmentFilter {

    /**
     * <h4>Get the parent node of current node</h4>
     *
     * @return The parent node (null when the node has no parent).
     */
    public @Nullable ITmfFilterTreeNode getParent();

    /**
     * <h4>Get the current node name</h4>
     *
     * @return The name of the current node.
     */
    public String getNodeName();

    /**
     * <h4>Tell if the node has children</h4>
     *
     * @return True if the node has children.
     */
    public boolean hasChildren();

    /**
     * <h4>Return the number of children</h4>
     *
     * @return The number of children.
     */
    public int getChildrenCount();

    /**
     * <h4>Get the array of children</h4>
     *
     * @return The array (possibly empty) of children nodes.
     */
    public @NonNull ITmfFilterTreeNode[] getChildren();

    /**
     * <h4>Get the node by index</h4>
     *
     * @param index The index of node to return.
     * @return The desired node (null if the node is not exists).
     */
    public ITmfFilterTreeNode getChild(int index);

    /**
     * <h4>Remove the node from its parent</h4>
     *
     * <p>Shifts all nodes after the removed one to prevent having an empty spot.
     * See {@link #replaceChild(int, ITmfFilterTreeNode)} to replace a node.</p>
     *
     * @return The removed node.
     */
    public ITmfFilterTreeNode remove();

    /**
     * <h4>Remove the child from the current node</h4>
     *
     * <p>Shifts all nodes after the removed one to prevent having an empty spot.
     * See {@link #replaceChild(int, ITmfFilterTreeNode)} to replace a node.</p>
     *
     * @param node the parent node
     *
     * @return The removed node.
     */
    public @Nullable ITmfFilterTreeNode removeChild(ITmfFilterTreeNode node);

    /**
     * <h4>Append a node to the current children</h4>
     *
     * @param node Node to append.
     * @return Index of added node (-1 if the node cannot be added).
     */
    public int addChild(ITmfFilterTreeNode node);

    /**
     * <h4>Replace a child node</h4>
     *
     * @param index Index of the node to replace.
     * @param node Node who will replace.
     * @return Node replaced.
     */
    public ITmfFilterTreeNode replaceChild(int index, ITmfFilterTreeNode node);

    /**
     * <h4>Sets the parent of current node</h4>
     *
     * @param parent The parent of current node.
     */
    public void setParent(@Nullable ITmfFilterTreeNode parent);

    /**
     * <h4>Gets the list of valid children node names that could be added to the node</h4>
     *
     * @return The list of valid children node names.
     */
    public @NonNull List<String> getValidChildren();

    /**
     * <h4>Returns a string representation of the filter tree node object.</h4>
     *
     * @param explicit
     *            true if ambiguous fields should explicitly include additional
     *            information that can differentiate them from other fields with
     *            the same name
     *
     * @return a string representation of the filter tree node object
     */
    public String toString(boolean explicit);

    /**
     * @return a clone of the node
     */
    public @Nullable ITmfFilterTreeNode clone();
}
