/**
 Simple Calendar Widget for GWT
 Copyright (C) 2006 Alexei Sokolov http://gwt.components.googlepages.com/

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.openkm.frontend.client.widget.searchin;

import java.util.Date;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.openkm.frontend.client.Main;

/**
 * CalendarWidget
 */
public class CalendarWidget extends Composite implements ClickHandler, HasChangeHandlers {

	private class NavBar extends Composite implements ClickHandler {
	
		public final DockPanel bar = new DockPanel();
		public final Button prevMonth = new Button("&lt;", this);
		public final Button prevYear = new Button("&lt;&lt;", this);
		public final Button nextYear = new Button("&gt;&gt;", this);
		public final Button nextMonth = new Button("&gt;", this);
		public final HTML title = new HTML();
		
		private final CalendarWidget calendar;
		
		public NavBar(CalendarWidget calendar) {
			initWidget(bar);
			
			this.calendar = calendar;

			bar.setStyleName("navbar");
			title.setStyleName("header");
		
			HorizontalPanel prevButtons = new HorizontalPanel();
			prevButtons.add(prevMonth);
			prevButtons.add(prevYear);
		
			HorizontalPanel nextButtons = new HorizontalPanel();
			nextButtons.add(nextYear);
			nextButtons.add(nextMonth);
			
			prevMonth.addStyleName("okm-Input");
			prevYear.addStyleName("okm-Input");
			nextYear.addStyleName("okm-Input");
			nextMonth.addStyleName("okm-Input");
		
			bar.add(prevButtons, DockPanel.WEST);
			bar.setCellHorizontalAlignment(prevButtons, DockPanel.ALIGN_LEFT);
			bar.add(nextButtons, DockPanel.EAST);
			bar.setCellHorizontalAlignment(nextButtons, DockPanel.ALIGN_RIGHT);
		  	bar.add(title, DockPanel.CENTER);
		  	bar.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
		  	bar.setCellHorizontalAlignment(title, HasAlignment.ALIGN_CENTER);
		  	bar.setCellVerticalAlignment(title, HasAlignment.ALIGN_MIDDLE);
		  	bar.setCellWidth(title, "100%");
		}
		
		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent event) {
			Widget sender = (Widget) event.getSource();
			if (sender == prevMonth) {
				calendar.prevMonth();
			} else if (sender == prevYear) {
				calendar.prevYear();
			} else if (sender == nextYear) {
				calendar.nextYear();
			} else if (sender == nextMonth) {
				calendar.nextMonth();
			}
		}
	}

	private static class CellHTML extends HTML {
		private int day;
	   
		public CellHTML(String text, int day) {
			super(text);
			this.day = day;
		}
	   
		public int getDay() {
			return day;
		}
	}
	
	private final NavBar navbar = new NavBar(this);
	private final DockPanel outer = new DockPanel();
	
	private final Grid grid = new Grid(7, 7) {
		public boolean clearCell(int row, int column) {
			boolean retValue = super.clearCell(row, column);
		       
			Element td = getCellFormatter().getElement(row, column);
			DOM.setInnerHTML(td, "");
			return retValue;
		}
	};
	
	private Date date = new Date();
	
	private String[] days = new String[] { "", "", "", "", "", "", "" };
	
	private String[] months = new String[] { "", "", "", "", "", "", "", "", "", "", "", "" };
	
	/**
	 * CalendarWidget
	 */
	public CalendarWidget() {
		initWidget(outer);
		grid.setStyleName("table");
		grid.setCellSpacing(0);
		outer.add(navbar, DockPanel.NORTH);
		outer.add(grid, DockPanel.CENTER);
		langRefresh(); // Sets language translations
		drawCalendar();
		setStyleName("CalendarWidget");
	}
	
	/**
	 * drawCalendar
	 */
	@SuppressWarnings("deprecation")
	private void drawCalendar() {
		int year = getYear();
		int month = getMonth();
		setHeaderText(year, month);
		grid.getRowFormatter().setStyleName(0, "weekheader");
		for (int i = 0; i < days.length; i++) {
			grid.getCellFormatter().setStyleName(0, i, "days");
			grid.setText(0, i, days[i].substring(0, 3));
		}
	
		Date now = new Date();
		int sameDay = now.getDate();
		int today = (now.getMonth() == month && now.getYear()+1900 == year) ? sameDay : 0;
		   
		int firstDay = new Date(year - 1900, month, 1).getDay();
		int numOfDays = getDaysInMonth(year, month);
	
		int j = 0;
		for (int i = 1; i < 7; i++) {
			for (int k = 0; k < 7; k++, j++) {
				int displayNum = (j - firstDay + 1);
				if (j < firstDay || displayNum > numOfDays) {
					grid.getCellFormatter().setStyleName(i, k, "empty");
					grid.setHTML(i, k, "&nbsp;");
				} else {
					HTML html = new CellHTML("<span>" + String.valueOf(displayNum) + "</span>",displayNum);
					html.addClickHandler(this);
					grid.getCellFormatter().setStyleName(i, k, "cell");
					if (displayNum == today) {
						grid.getCellFormatter().addStyleName(i, k, "today");
						grid.getCellFormatter().addStyleName(i, k, "day");
					} else if (displayNum == sameDay) {
						// not marks the day
						// grid.getCellFormatter().addStyleName(i, k, "day");
					}
					grid.setWidget(i, k, html);
				}
			}
		}
	}
	
	/**
	 * setHeaderText
	 * 
	 * @param year The year
	 * @param month The month
	 */
	protected void setHeaderText(int year, int month) {
		navbar.title.setText(months[month] + ", " + year);
	}
	
	/**
	 * getDaysInMonth
	 * 
	 * @param year The year
	 * @param month The month
	 * @return Number of day on a month
	 */
	private int getDaysInMonth(int year, int month) {
		switch (month) {
		  	case 1:
		  		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
		  			return 29; // leap year
		  		else
		  			return 28;
		  	case 3:
		  		return 30;
		  	case 5:
		  		return 30;
			case 8:
			    return 30;
			case 10:
				return 30;
			default:
				return 31;
		}
	}
	
	/**
	 * prevMonth
	 */
	public void prevMonth() {
		int month = getMonth() - 1;
		if (month < 0) {
			setDate(getYear() - 1, 11, getDay());
		} else {
			setMonth(month);
		}
		drawCalendar();
	}
	
	/**
	 * nextMonth
	 */
	public void nextMonth() {
		int month = getMonth() + 1;
		if (month > 11) {
			setDate(getYear() + 1, 0, getDay());
		} else {
			setMonth(month);
		}
		drawCalendar();
	}
	
	/**
	 * prevYear
	 */
	public void prevYear() {
		setYear(getYear() - 1);
		drawCalendar();
	}
	
	/**
	 * nextYear
	 */
	public void nextYear() {
		setYear(getYear() + 1);
		drawCalendar();
	}
	
	/**
	 * setDate
	 * 
	 * @param year The year
	 * @param month The month
	 * @param day The day
	 */
	@SuppressWarnings("deprecation")
	private void setDate(int year, int month, int day) {
		date = new Date(year - 1900, month, day);
	}
	
	/**
	 * setYear
	 * 
	 * @param year The year
	 */
	@SuppressWarnings("deprecation")
	private void setYear(int year) {
		date.setYear(year - 1900);
	}
	
	/**
	 * setMonth
	 * 
	 * @param month The month
	 */
	@SuppressWarnings("deprecation")
	private void setMonth(int month) {
		date.setMonth(month);
	}
	
	/**
	 * getYear
	 * 
	 * @return The year
	 */
	@SuppressWarnings("deprecation")
	public int getYear() {
		return 1900 + date.getYear();
	}
	
	/**
	 * getMonth
	 * 
	 * @return The month
	 */
	@SuppressWarnings("deprecation")
	public int getMonth() {
		return date.getMonth();	  
	}
	
	/**
	 * getDay
	 * 
	 * @return The day
	 */
	@SuppressWarnings("deprecation")
	public int getDay() {
		return date.getDate();
	}
	
	/**
	 * getDate
	 * 
	 * @return The date
	 */
	public Date getDate() {
		return date;
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		CellHTML cell = (CellHTML) event.getSource();
		setDate(getYear(), getMonth(), cell.getDay());
		drawCalendar();
		if (getHandlerCount(ChangeEvent.getType())>0) {
			fireChange();
		}
	}
	
	/**
	 * fire a change event
	 */
	private void fireChange() {
		 NativeEvent nativeEvent = Document.get().createChangeEvent();
		 ChangeEvent.fireNativeEvent(nativeEvent, this);
	}

	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasChangeHandlers#addChangeHandler(com.google.gwt.event.dom.client.ChangeHandler)
	 */
	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		days[0] = Main.i18n("calendar.day.sunday");
		days[1] = Main.i18n("calendar.day.monday");
		days[2] = Main.i18n("calendar.day.tuesday");
		days[3] = Main.i18n("calendar.day.wednesday");
		days[4] = Main.i18n("calendar.day.thursday");
		days[5] = Main.i18n("calendar.day.friday");
		days[6] = Main.i18n("calendar.day.saturday");
		months[0] = Main.i18n("calendar.month.january");
		months[1] = Main.i18n("calendar.month.february");
		months[2] = Main.i18n("calendar.month.march");
		months[3] = Main.i18n("calendar.month.april");
		months[4] = Main.i18n("calendar.month.may");
		months[5] = Main.i18n("calendar.month.june");
		months[6] = Main.i18n("calendar.month.july");
		months[7] = Main.i18n("calendar.month.august");
		months[8] = Main.i18n("calendar.month.september");
		months[9] = Main.i18n("calendar.month.october");
		months[10] = Main.i18n("calendar.month.november");
		months[11] = Main.i18n("calendar.month.december");
	}	
}  
