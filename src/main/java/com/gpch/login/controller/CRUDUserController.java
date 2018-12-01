package com.gpch.login.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gpch.login.model.Room;
import com.gpch.login.model.User;
import com.gpch.login.service.UserService;

@Controller
public class CRUDUserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = { "/admin/cruduser" }, method = RequestMethod.GET)
	public ModelAndView navigateToCRUDUser() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("admin/cruduser");
		return modelAndView;
	}
	
	 @RequestMapping(value = "/api/admin/getall", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	    public @ResponseBody Map<String, ? extends Object> getAllUsers(HttpServletRequest request) {
	        
	    	Map<String, Object> result = new HashMap<String, Object>();
	    	User user = (User) request.getAttribute("user");
	    	if(user == null) {
	    		return null;
	    	}else if(user.getRoles().iterator().next().getRole().equals("ADMIN")) {
	    		List<Map<String, Object>> users = userService.GetAllUser(user.getUsername());
		        result.put("code", 0);
				result.put("message", HttpStatus.OK.name());
				result.put("data", users);
	    	}
	    	
	        return result;
	    }
	 
	 @RequestMapping(value = "/api/getinformation", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	    public @ResponseBody Map<String, ? extends Object> getInformation(HttpServletRequest request) {
	        
	    	Map<String, Object> result = new HashMap<String, Object>();
	    	User user = (User) request.getAttribute("user");
	    	if(user == null) {
	    		return null;
	    	}else {
	    		Map<String,Object> userMap = new HashMap<>();
	    		userMap.put("id", user.getId());
	    		userMap.put("firstname", user.getFirstName());
	    		userMap.put("lastname", user.getLastName());
	    		userMap.put("username", user.getUsername());
	    		userMap.put("phone", user.getPhone());
		        result.put("code", 0);
				result.put("message", HttpStatus.OK.name());
				result.put("data", userMap);
	    	}
	    	
	        return result;
	    }
	 
}
