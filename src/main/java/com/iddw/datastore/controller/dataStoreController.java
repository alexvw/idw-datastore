package com.iddw.datastore.controller;
 
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.iddw.datastore.service.DataStoreService;
import com.iddw.datastore.dataobject.AttrUIObject;
import com.iddw.datastore.dataobject.AttributeBlob;
import com.iddw.datastore.dataobject.RowUIObject;
    

/*
 * @author avanderwoude
 * 
 * This controller takes in the JSON requests to store, retrieve, or delete data, and hands it off to the appropriate service
 */

@Controller   
public class DataStoreController {  
	
	@Autowired
	@Qualifier("envProperties")
	private Properties envProperties;
	
	DataStoreService DSS = new DataStoreService();
    
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public ModelAndView test(/*@RequestBody List<AttributeBlob> blobList,*/ HttpServletRequest request, ModelMap modelMap) throws InterruptedException {		
		System.out.println("keyspace: " +envProperties +" - " + envProperties.getProperty("c.keyspace"));
		
		String rowid = "mbun98374598";
		AttributeBlob newBlob = new AttributeBlob();
		newBlob.setRowId(rowid);newBlob.setAttrId("test");newBlob.setBlob("blobtext");
		List<AttributeBlob> writeList = new ArrayList<AttributeBlob>();
		writeList.add(newBlob);
		DSS.write(writeList);
		Thread.sleep(500);
		
		System.out.println(DSS.read(rowid).get(0).getAttrId());
		System.out.println(DSS.read("mbun90909090"));
		
		return new ModelAndView("view/viewer", modelMap);
	}
	
	/**
	 * WRITE
	 * This function takes in the parameters for Row ID, Attribute ID, and Blob (ideally encrypted on the other end).
	 *
	 * @param request A JSON list of AttributeBlob objects, to be saved to the db
	 * @returnstring success or fail. TODO: Improve the return status
	 */
	@RequestMapping(value = "/write", method = RequestMethod.POST)
	public @ResponseBody
	String write(@RequestBody AttributeBlob[] blobList,
			HttpServletRequest request, HttpServletResponse response) {	
		System.out.println("Received "+ blobList.length +" write requests");
		//list of blobs to write to cds
		List<AttributeBlob> writeList = new ArrayList<AttributeBlob>();
		try{
			//grab each of the attributeblobs from the JSON request
			for (AttributeBlob AB : blobList){
				AttributeBlob newBlob = new AttributeBlob();
	        	//rowId, column.getName(), column.getStringValue()
	        	newBlob.setAttrId(AB.getAttrId());
	        	newBlob.setBlob(AB.getBlob());
	        	newBlob.setRowId(AB.getRowId());
				writeList.add(newBlob);
			}
			
			DSS.write(writeList);
			
			return "success";
		} catch (Exception e){
			e.printStackTrace();
			return "fail";
		}
		
	}
	
	/**
	 * ReadATTR
	 * This function takes in the parameters for Row ID, Attribute ID, and returns a list of attributeBlob objects
	 *
	 * @param UIRequest A JSON list of AttrUIObjects, each one a request for a specific attributeblob
	 * @return returnlist a list of AttributeBlobs with data requested
	 */
	@RequestMapping(value = "/readAttr", method = RequestMethod.POST)
	public @ResponseBody
	List<AttributeBlob> readAttr(@RequestBody AttrUIObject[] UIRequest,
			HttpServletRequest request, HttpServletResponse response) {	
		if (UIRequest != null && UIRequest.length > 0){
			System.out.println("Received "+UIRequest.length+" Attribute read requests");
			//list of blobs
			List<AttributeBlob> returnList = new ArrayList<AttributeBlob>();
			
			//grab each of the attributeblobs from the JSON request
			for (AttrUIObject Object : UIRequest){
				AttributeBlob newBlob = DSS.read(Object.getRowId(), Object.getAttrId());
				if (newBlob != null)
				returnList.add(newBlob);
			}
			
			return returnList;
			
		} else return null;
		
	}
	/**
	 * ReadRow
	 * This function takes in the parameters for Row ID, and returns a list of attributeBlob objects
	 *
	 * @param UIRequest A JSON list of RowUIObjects, each one a request for a row of AttributeBlobs
	 * @return returnlist a List of (A list of AttributeBlobs). Confusing, I know, but this way we can do large batches if needed
	 */
	@RequestMapping(value = "/readRow", method = RequestMethod.POST)
	public @ResponseBody
	List<List<AttributeBlob>> readRow(@RequestBody RowUIObject[] UIRequest,
			HttpServletRequest request, HttpServletResponse response) {	
		if (UIRequest != null && UIRequest.length > 0){
			System.out.println("Received "+UIRequest.length+" Attribute read requests");
			//list of blobs
			List<List<AttributeBlob>> returnList = new ArrayList<List<AttributeBlob>>();
			
			//grab each of the List<attributeblobs> from the JSON request
			for (RowUIObject Object : UIRequest){
				List<AttributeBlob> newList = DSS.read(Object.getRowId());
				if (newList != null && newList.size() > 0)
				returnList.add(newList);
			}
			
			return returnList;
			
		} else return null;
		
	}
	
	/**
	 * deleteROW
	 * This function takes in the parameters for Row ID, and deletes those rows
	 *
	 * @param UIRequest A JSON list of RowUIObjects, each one a request for a row to be deleted
	 * @return string success or fail. TODO: Improve the return status
	 */
	@RequestMapping(value = "/deleteRow", method = RequestMethod.POST)
	public @ResponseBody
	String deleteRow(@RequestBody RowUIObject[] UIRequest,
			HttpServletRequest request, HttpServletResponse response) {	
		try{
			if (UIRequest != null && UIRequest.length > 0){
				System.out.println("Received "+UIRequest.length+" row delete requests");
				
				DSS.deleteRow(new ArrayList<RowUIObject>(Arrays.asList(UIRequest)));
				
				return "success";
				
			} else return "no request";
		}catch (Exception e){
			e.printStackTrace();
			return "fail";
		}
	}
	
	/**
	 * deleteATTR
	 * This function takes in the parameters for Row ID, and Attr ID, and deletes those attrs
	 *
	 * @param UIRequest A JSON list of RowUIObjects, each one a request for an attr to be deleted
	 * @return string success or fail. TODO: Improve the return status
	 */
	@RequestMapping(value = "/deleteAttr", method = RequestMethod.POST)
	public @ResponseBody
	String deleteAttr(@RequestBody AttrUIObject[] UIRequest,
			HttpServletRequest request, HttpServletResponse response) {	
		try{
			if (UIRequest != null && UIRequest.length > 0){
				System.out.println("Received "+UIRequest.length+" Attribute delete requests");
				
				DSS.deleteAttr(new ArrayList<AttrUIObject>(Arrays.asList(UIRequest)));
				
				return "success";
				
			} else return "no request";
		}catch (Exception e){
			e.printStackTrace();
			return "fail";
		}
	}
	
}

