package com.portal.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SetAnsStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session=request.getSession(false);
		
		response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
		
		String id=session.getAttribute("id").toString();
		
		int uid=Integer.parseInt(request.getParameter("uid"));
		int qid=Integer.parseInt(request.getParameter("qnid"));
		
		String status =request.getParameter("status");
		
		int pid=404;
		
		if(id.equals("admin"))
		{
			try
			{
				Class.forName("com.mysql.cj.jdbc.Driver");  
				Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/prmportal","root","CAPSlock007@");
				
				PreparedStatement p=con.prepareStatement("update answer set status=? where uid=? and qno=?");
				
				p.setString(1, status);
				p.setInt(2, uid);
				p.setInt(3, qid);
				
				p.executeUpdate();
				
				
				if(status.equals("correct") || status.equals("partial"))
				{	
					p=con.prepareStatement("update answer set end_time=current_time where uid=? and qno=?");
					p.setInt(1, uid);
					p.setInt(2, qid);
					
					p.executeUpdate();
					
					p=con.prepareStatement("select pid from candidate where id=?");
					p.setInt(1, uid);
					
					ResultSet rs=p.executeQuery();
					rs.next();
					
					pid=rs.getInt("pid");
					
					p=con.prepareStatement("select id from question where id>? and pid=?");
					
					p.setInt(1, qid);
					p.setInt(2, pid);
					
					rs=p.executeQuery();
					
					if(rs.next())
					{
						qid=rs.getInt("id");
						p=con.prepareStatement("update candidate set currentqn=? where id=?");
						
						p.setInt(1, qid);
						p.setInt(2, uid);
						
						System.out.println("Moved to next Question");
						p.executeUpdate();
					}
					else
					{
						p=con.prepareStatement("update candidate set finished=1 where id=?");
						p.setInt(1, uid);
						
						p.executeUpdate();
					}
				}
				response.sendRedirect("http://localhost:4200/viewquestions/"+uid);
			}
			
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
	}

}
