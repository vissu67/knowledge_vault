/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2011  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.frontend.client.widget.searchresult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.gen2.table.client.SortableGrid;
import com.google.gwt.gen2.table.client.SortableGrid.ColumnSorter;
import com.google.gwt.gen2.table.client.SortableGrid.ColumnSorterCallback;
import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortList;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTObjectToOrder;
import com.openkm.frontend.client.bean.GWTQueryResult;
import com.openkm.frontend.client.util.ColumnComparatorDate;
import com.openkm.frontend.client.util.ColumnComparatorDouble;
import com.openkm.frontend.client.util.ColumnComparatorText;

/**
 * ExtendedColumnSorter
 * 
 * @author jllort
 *
 */
public class ExtendedColumnSorter extends ColumnSorter {
	
	private String selectedRowDataID = "";

	/* (non-Javadoc)
	 * @see com.google.gwt.widgetideas.table.client.SortableGrid$ColumnSorter#onSortColumn(com.google.gwt.widgetideas.table.client.SortableGrid, com.google.gwt.widgetideas.table.client.TableModel.ColumnSortList, com.google.gwt.widgetideas.table.client.SortableGrid.ColumnSorterCallback)
	 */
	public void onSortColumn(SortableGrid grid,
			ColumnSortList sortList, ColumnSorterCallback callback) {
		
		// Get the primary column, sort order, number of rows, number of columns
		int column = sortList.getPrimaryColumn();
	    boolean ascending = sortList.isPrimaryAscending();
	    int rows = Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.getDataTable().getRowCount();
	    int columns = Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.getDataTable().getColumnCount();
	    int selectedRow = Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.getSelectedRow();
	    Map<Integer,GWTQueryResult> data = new HashMap<Integer,GWTQueryResult>(Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.data);
	    
	    List<String[]> elementList = new ArrayList<String[]>(); 					// List with all data
	    List<GWTObjectToOrder> elementToOrder = new ArrayList<GWTObjectToOrder>(); 	// List with column data, and actual position
	    
	    // Gets the data values and set on a list of String arrays ( element by column )
	    for (int i=0; i<rows;i++) {
	    	String[] rowI= new String[columns];
	    	GWTObjectToOrder rowToOrder = new GWTObjectToOrder();
	    	for (int x=0; x<columns; x++) {
	    		rowI[x] = Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.getDataTable().getHTML(i, x);
	    	}
	    	elementList.add(i,rowI);
	    	
	    	switch(column) {
		    	case 0 :
		    	case 1 :
		    	case 2 :
		    	case 5 :
		    	case 6 :
			    		// Text
				    	rowToOrder.setObject(rowI[column].toLowerCase());		// Lower case solves problem with sort ordering
				    	rowToOrder.setDataId(""+ i);							// Actual position value
				    	elementToOrder.add(rowToOrder);
		    		break;
		    	
		    	case 3 :
		    		// Bytes
		    		if (((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getDocument()!=null) {
		    			rowToOrder.setObject(new Double(((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getDocument().getActualVersion().getSize()));
		    		} else if (((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getAttachment()!=null) {
		    			rowToOrder.setObject(new Double(((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getAttachment().getActualVersion().getSize()));
		    		} else if (((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getFolder()!=null) {
		    			rowToOrder.setObject(new Double(0));
		    		} else if (((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getMail()!=null) {
		    			rowToOrder.setObject(new Double(((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getMail().getSize()));
		    		}
				    rowToOrder.setDataId(""+ i);												// Actual position value
				    elementToOrder.add(rowToOrder);
		    		break;
		    		
		    	case 4 :
		    		// Date
		    		if (((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getDocument()!=null) {
		    			rowToOrder.setObject(((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getDocument().getLastModified()); // Date value
		    		} else if (((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getAttachment()!=null) {
		    			rowToOrder.setObject(((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getAttachment().getLastModified()); // Date value
		    		} else if (((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getFolder()!=null) {
		    			rowToOrder.setObject(((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getFolder().getCreated());
		    		} else if (((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getMail()!=null) {
		    			rowToOrder.setObject(((GWTQueryResult) data.get(Integer.parseInt(rowI[7]))).getMail().getReceivedDate());
		    		}
			    	rowToOrder.setDataId(""+ i);																 // Actual position value
			    	elementToOrder.add(rowToOrder);
		    		break;
	    	}
	    	
	    	// Saves the selected row
	    	if (selectedRow==i) {
	    		selectedRowDataID = rowToOrder.getDataId();
	    	}
	    }
	    
	    switch(column) {
	    	case 0 :
	    	case 1 :
	    	case 2 :
	    	case 5 :
	    	case 6 :
	    		// Text
	    		Collections.sort(elementToOrder, ColumnComparatorText.getInstance());
	    		break;
	    	
	    	case 3 :
	    		// Bytes
	    		Collections.sort(elementToOrder, ColumnComparatorDouble.getInstance());
	    		break;
	    		
	    	case 4 :
	    		// Date
	    		Collections.sort(elementToOrder, ColumnComparatorDate.getInstance());
	    		break;
	    }
	    
	    // Reversing if needed
	    if (!ascending) {
			Collections.reverse(elementToOrder);
		}
	    
	    applySort(elementList, elementToOrder);
	    
	    callback.onSortingComplete();
	}
    
	/**
	 * @param elementList
	 * @param elementToOrder
	 */
	private void applySort(List<String[]>  elementList, List<GWTObjectToOrder> elementToOrder) {
		// Removing all values
		while (Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.getDataTable().getRowCount()>0 ){
			Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.getDataTable().removeRow(0);
		}
		
		// Data map
		Map<Integer,GWTQueryResult> data = new HashMap<Integer,GWTQueryResult>(Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.data);
		Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.reset();
		
		int column = 0;
		for (Iterator<GWTObjectToOrder> it =  elementToOrder.iterator(); it.hasNext();) {
			GWTObjectToOrder orderedColumn = it.next();
    		String[] row = elementList.get(Integer.parseInt(orderedColumn.getDataId()));
    		
    		Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.addRow((GWTQueryResult) data.get(Integer.parseInt(row[7])));
    		
    		// Sets selectedRow
    		if (!selectedRowDataID.equals("") && selectedRowDataID.equals(row[7])) {
    			Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.setSelectedRow(column);
    			selectedRowDataID = "";
    		}
    		
    		column++;
    	}
	}
}
