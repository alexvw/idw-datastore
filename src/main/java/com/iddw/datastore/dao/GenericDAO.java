package com.iddw.datastore.dao;

/*
 * @author avanderwoude
 * 
 * A Generic DAO with the required datastore actions. Implemented for specific database implementations
 */

public interface GenericDAO<T> {
	public void write(T t);
	public T read(String key);
	public T read(String key, String attr);
	public void delete(T t);
}