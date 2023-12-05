package com.poly.service;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class SessionService {
	@Autowired
	HttpSession session;

	/**
	* Đọc giá trị của attribute trong session
	* @param name tên attribute
	* @return giá trị đọc được hoặc null nếu không tồn tại
	*/
	public void setSessionAttribute(String attributeName, Object attributeValue, HttpSession session) {
        session.setAttribute(attributeName, attributeValue);
    }

    public Object getSessionAttribute(String attributeName, HttpSession session) {
        return session.getAttribute(attributeName);
    }

    public void removeSessionAttribute(String attributeName, HttpSession session) {
        session.removeAttribute(attributeName);
    }
    public void set(String name, Object value) {
		session.setAttribute(name, value);
		
		
		
	}
    public <T> T get(String name) {
		return (T)session.getAttribute(name);
		
	}
}
