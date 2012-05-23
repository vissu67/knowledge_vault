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

package com.openkm.servlet.admin;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.ColumnArrangement;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.StatsInfo;
import com.openkm.core.Config;
import com.openkm.core.RepositoryInfo;
import com.openkm.util.FormatUtil;
import com.openkm.util.WebUtils;

/**
 * Stats graphical servlet
 */
public class StatsGraphServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(StatsGraphServlet.class);
	private static final String DOCUMENTS = "0";
	private static final String DOCUMENTS_SIZE = "1";
	private static final String FOLDERS = "2";
	private static final String MEMORY = "3";
	private static final String DISK = "4";

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String type = WebUtils.getString(request, "t");
		JFreeChart chart = null;
		updateSessionManager(request);
		
		try {
			response.setContentType("image/png");
			OutputStream out = response.getOutputStream();

			if (DOCUMENTS.equals(type) || DOCUMENTS_SIZE.equals(type) || FOLDERS.equals(type)) {
				chart = repoStats(type);
			} else if (DISK.equals(type)) {
				chart = diskStats();
			} else if (MEMORY.equals(type)) {
				chart = memStats();
			}

			if (chart != null) {
				// Customize title font
				chart.getTitle().setFont(new Font("Tahoma", Font.BOLD, 16));
				
				// Match body {	background-color:#F6F6EE; }
				chart.setBackgroundPaint(new Color(246, 246, 238));
				
				// Customize no data
				PiePlot plot = (PiePlot) chart.getPlot();
				plot.setNoDataMessage("No data to display");
				
				// Customize labels
				plot.setLabelGenerator(null);

				// Customize legend
				LegendTitle legend = new LegendTitle(plot, new ColumnArrangement(), new ColumnArrangement());
				legend.setPosition(RectangleEdge.BOTTOM);
				legend.setFrame(BlockBorder.NONE);
				legend.setItemFont(new Font("Tahoma", Font.PLAIN, 12));
				chart.removeLegend();
				chart.addLegend(legend);

				if (DISK.equals(type) || MEMORY.equals(type)) {
					ChartUtilities.writeChartAsPNG(out, chart, 225, 225);
				} else {
					ChartUtilities.writeChartAsPNG(out, chart, 250, 250);
				}
			}

			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate disk statistics
	 */
	public JFreeChart diskStats() throws IOException, ServletException {
		String title = "Disk usage";
		String repHome = null;
		
		// Allow absolute repository path
		if ((new File(Config.REPOSITORY_HOME)).isAbsolute()) {
			repHome = Config.REPOSITORY_HOME;
		} else {
			repHome = Config.HOME_DIR + File.separator + Config.REPOSITORY_HOME;
		}
		
		File df = new File(repHome);
		long total = df.getTotalSpace();
		long usable = df.getUsableSpace();
		long used = total - usable;
		
		log.debug("Total space: {}", FormatUtil.formatSize(total));
		log.debug("Usable space: {}", FormatUtil.formatSize(usable));
		log.debug("Used space: {}", FormatUtil.formatSize(used));
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Available (" + FormatUtil.formatSize(usable) + ")", usable * 100 / total);
		dataset.setValue("Used (" + FormatUtil.formatSize(used) + ")", used * 100 / total);

		return ChartFactory.createPieChart(title, dataset, true, false, false);
	}

	/**
	 * Generate memory statistics
	 * http://blog.codebeach.com/2008/02/determine-available-memory-in-java.html
	 */
	public JFreeChart memStats() throws IOException, ServletException {
		String title = "Memory usage";
		Runtime runtime = Runtime.getRuntime();
		long max = runtime.maxMemory(); // maximum amount of memory that the JVM will attempt to use
		long available = runtime.totalMemory(); // total amount of memory in the JVM
		long free = runtime.freeMemory(); // amount of free memory in the JVM
		long used = max - available;
		long total = free + used;
		
		log.debug("Maximun memory: {}", FormatUtil.formatSize(max));
		log.debug("Available memory: {}", FormatUtil.formatSize(available));
		log.debug("Free memory: {}", FormatUtil.formatSize(free));
		log.debug("Used memory: {}", FormatUtil.formatSize(used));
		log.debug("Total memory: {}", FormatUtil.formatSize(total));
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Available (" + FormatUtil.formatSize(free) + ")", free * 100 / total);
		dataset.setValue("Used (" + FormatUtil.formatSize(used) + ")", used * 100 / total);

		return ChartFactory.createPieChart(title, dataset, true, false, false);
	}

	/**
	 * Generate repository statistics
	 */
	public JFreeChart repoStats(String type) throws IOException, ServletException {
		String title = null;
		String[] sizes = null;
		double[] percents = null;
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		if (DOCUMENTS.equals(type)) {
			StatsInfo si = RepositoryInfo.getDocumentsByContext();
			percents = si.getPercents();
			sizes = si.getSizes();
			title = "Documents by context";
		} else if (DOCUMENTS_SIZE.equals(type)) {
			StatsInfo si = RepositoryInfo.getDocumentsSizeByContext();
			percents = si.getPercents();
			sizes = si.getSizes().clone();

			for (int i = 0; i < sizes.length; i++) {
				sizes[i] = FormatUtil.formatSize(Long.parseLong(sizes[i]));
			}

			title = "Documents size by context";
		} else if (FOLDERS.equals(type)) {
			StatsInfo si = RepositoryInfo.getFoldersByContext();
			percents = si.getPercents();
			sizes = si.getSizes();
			title = "Folders by context";
		}
		
		if (title != null && sizes.length > 0 && percents.length > 0) {
			dataset.setValue("Taxonomy (" + sizes[0] + ")", percents[0]);
			dataset.setValue("Personal (" + sizes[1] + ")", percents[1]);
			dataset.setValue("Template (" + sizes[2] + ")", percents[2]);
			dataset.setValue("Trash (" + sizes[3] + ")", percents[3]);
		}
		
		return ChartFactory.createPieChart(title, dataset, true, false, false);
	}
	
	/**
	 * Convert a piechartdata to xml
	 * 
	 * @author puspendu.banerjee@gmail.com 
	 */
	public String repoStatsXML(final String title, final DefaultPieDataset dataset) throws 
			IOException, ServletException {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("RepoStats");
		root.addElement("Title").addCDATA(title);
		Element dataSetElement =root.addElement("DataSet");
		
		for (int i=0; i<dataset.getItemCount(); i++) {
			Element itemElement= dataSetElement.addElement("Item");
			itemElement.addElement("name").addCDATA(dataset.getKey(i).toString());
			itemElement.addAttribute("percent", dataset.getValue(i).toString());
			dataSetElement.add(itemElement);
		}
		
		return document.asXML();
	}
}
