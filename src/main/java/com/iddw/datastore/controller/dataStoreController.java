package com.iddw.datastore.controller;
 
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.iddw.datastore.util.Constants;
import com.iddw.datastore.service.dataStoreService;
import com.iddw.datastore.dataobject.AttributeBlob;
    

/*
 * @author avanderwoude
 * 
 * This controller takes in the JSON requests to store, retrieve, or delete data, and hands it off to the appropriate service
 */

@Controller   
public class dataStoreController {  
	
	@Autowired
	@Qualifier("envProperties")
	private Properties envProperties;
    
	/**
	 * SAVE
	 * This function takes in the parameters for Row ID, Attribute ID, and Blob (ideally encrypted on the other end).
	 *
	 * @param request A JSON list of AttributeBlob objects, to be saved to the db
	 * @return outcome If for some reason this fails, it returns an object that contains the failure information
	 */
	@RequestMapping(value = "/write", method = RequestMethod.GET)
	public void write(/*@RequestBody List<AttributeBlob> blobList,*/ HttpServletRequest request) {
		//grab parameters
		
		System.out.println("keyspace: " +envProperties +" - " + envProperties.getProperty("c.keyspace"));
		//check list of blobs
		
		//pass object to service to save
		
		//return outcome
	}
}

