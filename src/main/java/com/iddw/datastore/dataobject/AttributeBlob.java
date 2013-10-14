package com.iddw.datastore.dataobject;

import java.io.Serializable;

import javax.persistence.Id;

public class AttributeBlob implements Serializable {

	private static final long serialVersionUID = -6943299870252424123L;

	@Id
	Long id;

	private String rowId;
	private String attrId;
	private String blob;
	private int expiry;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRowId() {
		return rowId;
	}
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	public String getAttrId() {
		return attrId;
	}
	public void setAttrId(String attrId) {
		this.attrId = attrId;
	}
	public String getBlob() {
		return blob;
	}
	public void setBlob(String blob) {
		this.blob = blob;
	}
	public int getExpiry() {
		return expiry;
	}
	public void setExpiry(int expiry) {
		this.expiry = expiry;
	}
	
	
}
