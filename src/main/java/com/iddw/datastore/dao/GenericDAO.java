package com.iddw.datastore.dao;

import java.util.List;

import com.iddw.datastore.dataobject.AttrUIObject;
import com.iddw.datastore.dataobject.AttributeBlob;
import com.iddw.datastore.dataobject.RowUIObject;

/*
 * @author avanderwoude
 * 
 * A Generic DAO with the required datastore actions. Implemented for specific database implementations
 */

public interface GenericDAO<T> {
	public void write(List<T> t) throws Exception;
	public List<AttributeBlob> read(String key) throws Exception;
	public T read(String key, String attr) throws Exception;
	public void deleteRow(List<RowUIObject> rowList) throws Exception;
	public void deleteAttr(List<AttrUIObject> attrList) throws Exception;
}