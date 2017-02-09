package com.dub.site;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.web.bind.annotation.ExceptionHandler;
import com.dub.config.annotation.WebControllerAdvice;

@WebControllerAdvice
public class WebExceptionHandler
{ 
	private static final Logger log = LogManager.getLogger();
    
    @ExceptionHandler(RuntimeException.class)
    public String handleCatchAll(RuntimeException e)
    {
    	log.warn("Catch All Handler begin " + e.getMessage());	        
        return "error";  
    }
   
} 