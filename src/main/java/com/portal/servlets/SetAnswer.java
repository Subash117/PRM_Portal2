package com.portal.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;


public class SetAnswer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session= request.getSession(false);
        String id=session.getAttribute("id").toString();
        String pid=session.getAttribute("pid").toString();
        
        
        int qnid=Integer.parseInt(request.getParameter("qno"));
        
		
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");  
			Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/prmportal","root","CAPSlock007@");
		
			
			PreparedStatement p=con.prepareStatement("select * from answer where uid=? and qno=?");
			
			p.setInt(1, Integer.parseInt(id));
			p.setInt(2, qnid);
			
			ResultSet rs=p.executeQuery();
			
			if(!rs.next())
			{
				p=con.prepareStatement("insert into answer(uid,qno,start_time) values(?,?,current_time())");
				
				p.setInt(1,Integer.parseInt(id));
				p.setInt(2, qnid);
				
				p.executeUpdate();	
			}
			
			p=con.prepareStatement("select qndesc from question where id=?");
			p.setInt(1, qnid);
			
			rs=p.executeQuery();
			
			rs.next();
			
			String link=rs.getString("qndesc");
			
			response.sendRedirect("http://"+link);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
	}

}
