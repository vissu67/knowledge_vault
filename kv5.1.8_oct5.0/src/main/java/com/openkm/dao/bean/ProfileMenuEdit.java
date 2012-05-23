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

public class ProfileMenuEdit implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean lockVisible;
	private boolean unlockVisible;
	private boolean deleteVisible;
	private boolean checkInVisible;
	private boolean checkOutVisible;
	private boolean cancelCheckOutVisible;
	private boolean renameVisible;
	
	private boolean zohoVisible;	//added by vissu on feb19 for zohoapi
	
	private boolean copyVisible;
	private boolean moveVisible;
	private boolean addPropertyGroupVisible;
	private boolean removePropertyGroupVisible;
	private boolean addSubscriptionVisible;
	private boolean removeSubscriptionVisible;

	public boolean isLockVisible() {
		return lockVisible;
	}

	public void setLockVisible(boolean lockVisible) {
		this.lockVisible = lockVisible;
	}

	public boolean isUnlockVisible() {
		return unlockVisible;
	}

	public void setUnlockVisible(boolean unlockVisible) {
		this.unlockVisible = unlockVisible;
	}

	public boolean isDeleteVisible() {
		return deleteVisible;
	}

	public void setDeleteVisible(boolean deleteVisible) {
		this.deleteVisible = deleteVisible;
	}

	public boolean isCheckInVisible() {
		return checkInVisible;
	}

	public void setCheckInVisible(boolean checkInVisible) {
		this.checkInVisible = checkInVisible;
	}

	public boolean isCheckOutVisible() {
		return checkOutVisible;
	}

	public void setCheckOutVisible(boolean checkOutVisible) {
		this.checkOutVisible = checkOutVisible;
	}
	
	//added by vissu on feb19 for zohoapi
	public boolean isZohoVisible() {
		return zohoVisible;
	}

	public void setZohoVisible(boolean zohoVisible) {
		this.zohoVisible = zohoVisible;
	}

	public boolean isCancelCheckOutVisible() {
		return cancelCheckOutVisible;
	}

	public void setCancelCheckOutVisible(boolean cancelCheckOutVisible) {
		this.cancelCheckOutVisible = cancelCheckOutVisible;
	}

	public boolean isRenameVisible() {
		return renameVisible;
	}

	public void setRenameVisible(boolean renameVisible) {
		this.renameVisible = renameVisible;
	}

	public boolean isCopyVisible() {
		return copyVisible;
	}

	public void setCopyVisible(boolean copyVisible) {
		this.copyVisible = copyVisible;
	}

	public boolean isMoveVisible() {
		return moveVisible;
	}

	public void setMoveVisible(boolean moveVisible) {
		this.moveVisible = moveVisible;
	}
	
	public boolean isAddPropertyGroupVisible() {
		return addPropertyGroupVisible;
	}

	public void setAddPropertyGroupVisible(boolean addPropertyGroupVisible) {
		this.addPropertyGroupVisible = addPropertyGroupVisible;
	}

	public boolean isRemovePropertyGroupVisible() {
		return removePropertyGroupVisible;
	}

	public void setRemovePropertyGroupVisible(boolean removePropertyGroupVisible) {
		this.removePropertyGroupVisible = removePropertyGroupVisible;
	}

	public boolean isAddSubscriptionVisible() {
		return addSubscriptionVisible;
	}

	public void setAddSubscriptionVisible(boolean addSubscriptionVisible) {
		this.addSubscriptionVisible = addSubscriptionVisible;
	}

	public boolean isRemoveSubscriptionVisible() {
		return removeSubscriptionVisible;
	}

	public void setRemoveSubscriptionVisible(boolean removeSubscriptionVisible) {
		this.removeSubscriptionVisible = removeSubscriptionVisible;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("lockVisible="); sb.append(lockVisible);
		sb.append(", unlockVisible="); sb.append(unlockVisible);
		sb.append(", checkInVisible="); sb.append(checkInVisible);
		sb.append(", checkOutVisible="); sb.append(checkOutVisible);
		sb.append(", cancelCheckOutVisible="); sb.append(cancelCheckOutVisible);
		sb.append(", deleteVisible="); sb.append(deleteVisible);
		sb.append(", renameVisible="); sb.append(renameVisible);
		
		sb.append(", zohoVisible="); sb.append(zohoVisible);	//added by vissu on feb19 for zohoapi
		
		sb.append(", copyVisible="); sb.append(copyVisible);
		sb.append(", moveVisible="); sb.append(moveVisible);
		sb.append(", addPropertyGroupVisible="); sb.append(addPropertyGroupVisible);
		sb.append(", removePropertyGroupVisible="); sb.append(removePropertyGroupVisible);
		sb.append(", removeSubscriptionVisible="); sb.append(removeSubscriptionVisible);
		sb.append(", addSubscriptionVisible="); sb.append(addSubscriptionVisible);
		sb.append("}");
		return sb.toString();
	}
}
