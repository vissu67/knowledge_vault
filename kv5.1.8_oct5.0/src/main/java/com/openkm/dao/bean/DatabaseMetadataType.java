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

package com.openkm.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="OKM_DB_METADATA_TYPE", uniqueConstraints={
		@UniqueConstraint(columnNames={"DMT_TABLE", "DMT_REAL_COLUMN"})})
public class DatabaseMetadataType implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String TEXT = "text";
	public static final String BOOLEAN = "boolean";
	public static final String INTEGER = "integer";
	public static final String LONG = "long";
	public static final String FILE = "file";
	
	@Id
	@Column(name="DMT_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="DMT_TABLE", length=32)
	private String table;
	
	@Column(name="DMT_VIRTUAL_COLUMN", length=32)
	private String virtualColumn;
	
	@Column(name="DMT_REAL_COLUMN", length=6)
	private String realColumn;
	
	@Column(name="DMT_TYPE", length=32)
	private String type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getVirtualColumn() {
		return virtualColumn;
	}

	public void setVirtualColumn(String virtualColumn) {
		this.virtualColumn = virtualColumn;
	}

	public String getRealColumn() {
		return realColumn;
	}

	public void setRealColumn(String realColumn) {
		this.realColumn = realColumn;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id="); sb.append(id);
		sb.append(", table="); sb.append(table);
		sb.append(", virtualColumn="); sb.append(virtualColumn);
		sb.append(", realColumn="); sb.append(realColumn);
		sb.append(", type="); sb.append(type);
		sb.append("}");
		return sb.toString();
	}
}
