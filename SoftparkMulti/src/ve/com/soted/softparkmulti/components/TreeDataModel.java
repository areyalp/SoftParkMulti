package ve.com.soted.softparkmulti.components;

import java.util.ArrayList;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ve.com.soted.softparkmulti.objects.Station;
import ve.com.soted.softparkmulti.objects.Summary;

public class TreeDataModel implements TreeModel{
	
	private DefaultTreeModel model;
	
	private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Cierres");
	
	private DefaultMutableTreeNode station;
	private DefaultMutableTreeNode user;
	private DefaultMutableTreeNode report;
	
	public TreeDataModel(ArrayList<Station> stationsWithSummary, ArrayList<Summary> summaries) {
		model = new DefaultTreeModel(rootNode);
		
		for(Station st: stationsWithSummary) {
			station = new DefaultMutableTreeNode(st.getName());
			model.insertNodeInto(station, rootNode, 0);
			for(Summary su: summaries) {
				if(su.getStationId() == st.getId()) {
					user = new DefaultMutableTreeNode(su.getUserName());
					model.insertNodeInto(user, station, 0);
					report = new DefaultMutableTreeNode(su.getId());
					model.insertNodeInto(report, user, 0);
				}
			}
		}
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		model.addTreeModelListener(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		return model.getChild(parent, index);
	}

	@Override
	public int getChildCount(Object parent) {
		return model.getChildCount(parent);
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return model.getIndexOfChild(parent, child);
	}

	@Override
	public Object getRoot() {
		return model.getRoot();
	}

	@Override
	public boolean isLeaf(Object node) {
		return model.isLeaf(node);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		model.removeTreeModelListener(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		model.valueForPathChanged(path, newValue);
	}

}
