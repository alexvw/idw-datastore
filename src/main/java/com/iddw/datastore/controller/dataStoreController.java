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
import com.iddw.datastore.dataobject.AttributeBlob;
    

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
    
	/**
	 * SAVE
	 * This function takes in the parameters for Row ID, Attribute ID, and Blob (ideally encrypted on the other end).
	 *
	 * @param request A JSON list of AttributeBlob objects, to be saved to the db
	 * @return outcome If for some reason this fails, it returns an object that contains the failure information
	 * @throws InterruptedException 
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public ModelAndView testWrite(/*@RequestBody List<AttributeBlob> blobList,*/ HttpServletRequest request, ModelMap modelMap) throws InterruptedException {		
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
	
	@RequestMapping(value = "/write", method = RequestMethod.POST)
	public @ResponseBody
	String write(@RequestBody AttributeBlob[] blobList,
			HttpServletRequest request, HttpServletResponse response) {	
		System.out.println("Received "+ blobList.length +" write requests");
		//list of blobs to write to cds
		List<AttributeBlob> writeList = new ArrayList<AttributeBlob>();
		
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
	}
	
	@RequestMapping(value = "/write", method = RequestMethod.POST)
	public @ResponseBody
	String write(@RequestBody AttributeBlob[] blobList,
			HttpServletRequest request, HttpServletResponse response) {	
		System.out.println("Received "+ blobList.length +" write requests");
		//list of blobs to write to cds
		List<AttributeBlob> writeList = new ArrayList<AttributeBlob>();
		
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
	}
	
	/*
	@RequestMapping(value = "/getMaskedValues.max", method = RequestMethod.POST)
	public @ResponseBody
	MaskedValuesResponse getMasked(@RequestBody SimpleUIRequest[] simpleRequestList,
			HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		//TODO: Add security, check session, mbun etc
		
		HttpSession session = request.getSession();
		String apiKey = (String) session.getAttribute(SessionConstants.RP_API_KEY);
		String idpType = (String) session.getAttribute(SessionConstants.RP_IDP_TYPE);
		String credential = (String) session.getAttribute(SessionConstants.CREDENTIAL);
		String mbun = (String) session.getAttribute(SessionConstants.MBUN);		
		
		//get info
		//TODO: get data from session instead
		Company thisRP = companyService.getAXNServiceByApiKey(apiKey).getCompany();
		User thisUser = userService.getUserByMbun(mbun);
		
		System.out.println("\n\n\n"+ mbun +", "+ apiKey +" "+thisRP+", "+thisUser);
		
		//grab data
		return umcService.getMaskedValues(thisUser, simpleRequestList);
	}*/
}

