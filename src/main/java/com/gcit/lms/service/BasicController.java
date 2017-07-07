package com.gcit.lms.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

	@RequestMapping(value = "/api/hello", method= RequestMethod.GET, produces="application/json" )
	public String helloWorld(){ 
		return "Hello from the API side";
	}
}
