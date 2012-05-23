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
@Table(name="OKM_DB_METADATA_SEQUENCE", uniqueConstraints={
		@UniqueConstraint(columnNames={"DMS_TABLE", "DMS_COLUMN"})})
public class DatabaseMetadataSequence implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="DMS_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="DMS_TABLE", length=32)
	private String table;
	
	@Column(name="DMS_COLUMN", length=32)
	private String column;
	
	@Column(name="DMS_VALUE")
	private long value;

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

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id="); sb.append(id);
		sb.append(", table="); sb.append(table);
		sb.append(", column="); sb.append(column);
		sb.append(", value="); sb.append(value);
		sb.append("}");
		return sb.toString();
	}
}
