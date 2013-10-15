package com.iddw.datastore.dao;

import java.util.List;

import com.iddw.datastore.dataobject.AttributeBlob;

/*
 * @author avanderwoude
 * 
 * A Generic DAO with the required datastore actions. Implemented for specific database implementations
 */

public interface GenericDAO<T> {
	public void write(List<T> t) throws Exception;
	public List<AttributeBlob> read(String key) throws Exception;
	public T read(String key, String attr) throws Exception;
	public void delete(String rowId) throws Exception;
	public void delete(String rowId, String attrId) throws Exception;
}