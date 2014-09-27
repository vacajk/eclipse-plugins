/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel;

import java.util.LinkedList;
import java.util.List;

import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;

/**
 * As per SVD 1.1, <i>"A cluster describes a sequence of registers within a
 * peripheral. A cluster has an base offset relative to the base address of the
 * peripheral. All registers within a cluster specify their address offset
 * relative to the cluster base address. Register and cluster sections can occur
 * in an arbitrary order."</i>
 */
public class SvdClusterDMNode extends SvdDMNode {

	// ------------------------------------------------------------------------

	public SvdClusterDMNode(Leaf node) {

		super(node);
	}

	@Override
	public void dispose() {

		super.dispose();
	}

	// ------------------------------------------------------------------------

	@Override
	protected SvdDMNode[] prepareChildren(Leaf node) {

		if (node == null || !node.hasChildren()) {
			return null;
		}

		// System.out.println("prepareChildren(" + node.getProperty("name") +
		// ")");

		Leaf group = ((Node) node).findChild("registers");
		if (!group.hasChildren()) {
			return null;
		}

		List<SvdDMNode> list = new LinkedList<SvdDMNode>();
		for (Leaf child : ((Node) group).getChildren()) {

			// Keep only <register> and <cluster> nodes
			if (child.isType("register")) {
				// TODO: process dimElementGroup, for one node generate multiple
				// objects
				list.add(new SvdRegisterDMNode(child));
			} else if (child.isType("cluster")) {
				// TODO: process dimElementGroup, for one node generate multiple
				// objects
				list.add(new SvdClusterDMNode(child));
			}
		}

		SvdDMNode[] array = list.toArray(new SvdDMNode[list.size()]);

		// Preserve apparition order.
		return array;
	}

	// ------------------------------------------------------------------------
}
