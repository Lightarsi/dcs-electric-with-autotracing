
package com.sun.electric.tool.scripts;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import java.util.regex.Pattern;

import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.topology.ArcInst;
import com.sun.electric.database.topology.Connection;
import com.sun.electric.tool.user.Highlighter;
import com.sun.electric.database.EditingPreferences;

import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.tool.user.ui.EditWindow;
import com.sun.electric.tool.user.CircuitChangeJobs;
import com.sun.electric.tool.user.CircuitChanges;
import com.sun.electric.tool.Job; 

/**
 *
 * @author diivanov
 */
public class DeleteKeysFromBlock {

    /**
     *
     */
    public static void doDelete() {
		Cell curcell = Job.getUserInterface().getCurrentCell();
		Pattern key = Pattern.compile(".*(\\.[noprstquvw])+(.*)");
		Pattern block = Pattern.compile(".*((CAU)|(PAU)|(PADDR)|(PPC)|(SPM)|(CB))+(.*)");
		Set<ArcInst> ArcToDelete = new HashSet<ArcInst>();

		EditWindow wnd = EditWindow.needCurrent();
		if (wnd == null) return;
		Highlighter highlighter = wnd.getHighlighter();

		List<NodeInst> theNodes = highlighter.getHighlightedNodes();
		
		/*if(theNodes.size() > 400) {
			System.out.println("Too many objects.");
			return;
		}*/
		
		ListIterator<NodeInst> itr = theNodes.listIterator();
		
		while(itr.hasNext()) {
			NodeInst ni = itr.next();
			if(!block.matcher(ni.toString()).matches()) {
				continue;
			}
			Iterator<PortInst> itrPort = ni.getPortInsts();
			while(itrPort.hasNext()) {
				PortInst pi = itrPort.next();
				if(pi.hasConnections()) {
					if(key.matcher(pi.toString()).matches()) {
						Iterator<Connection> itrCon = pi.getConnections();
						while(itrCon.hasNext()) {
							Connection cntn = itrCon.next();
							ArcInst ai = cntn.getArc();
							if(ArcToDelete.contains(ai)) {
								continue;
							}
							ArcToDelete.add(ai);
						}
					}
				}
			}
		}
		Iterator<ArcInst> itrSet = ArcToDelete.iterator();
		while(itrSet.hasNext()) {
			ArcInst ai2 = itrSet.next();
			System.out.println("delete " + ai2.toString());
		}
		
		new CircuitChangeJobs.DeleteArcs(ArcToDelete);
	}
}
	