package com.iddw.datastore.service;

import java.util.List;

import com.iddw.datastore.dao.*;
import com.iddw.datastore.dataobject.AttributeBlob;

/**
 * @author avanderwoude
 * This file manages the abstraction of saving, reading, and deleting from the underlying database
 */

public class DataStoreService{
	CassandraDAOImpl CDI = new CassandraDAOImpl();
	
	//write the data to the database, using a DAO in case we switch underlying implementations
	public boolean write(List<AttributeBlob> blobList){
		System.out.println("Writing to cds " + blobList.size() +" AttributeBlob");
		//write
		try {
			CDI.write(blobList);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//grab all blobs for this row
		public List<AttributeBlob> read(String rowId){
			System.out.println("Getting from cds: " + rowId);
			//write
			try {
				return CDI.read(rowId);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		//grab a single blob
		public AttributeBlob read(String rowId, String attrId){
			System.out.println("Getting from cds: " + rowId + " : " +attrId);
			//write
			try {
				return CDI.read(rowId,attrId);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		//delete this entire row
		public boolean delete(String rowId){
			System.out.println("Deleting from cds: " + rowId);
			//write
			try {
				CDI.delete(rowId);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		//delete an attr
		public boolean delete(String rowId, String attrId){
			System.out.println("Deleting from cds: " + rowId + " : " +attrId);
			//write
			try {
				CDI.delete(rowId,attrId);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
}
