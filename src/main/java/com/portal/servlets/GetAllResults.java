package com.portal.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class GetAllResults extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int pid=Integer.parseInt(request.getParameter("pid"));
		
		response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
		
			try
			{
				ArrayList<Integer> id=new ArrayList<>();
				ArrayList<String> name=new ArrayList<>();
				Class.forName("com.mysql.cj.jdbc.Driver");  
				Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/prmportal","root","CAPSlock007@");
				
				PreparedStatement p=con.prepareStatement("select id,name from candidate where pid=?");
				
				p.setInt(1, pid);
				
				ResultSet rs=p.executeQuery();
				
				while(rs.next())
				{
					id.add(rs.getInt("id"));
					name.add(rs.getString("name"));
				}
				
				JSONObject mainObj=new JSONObject();
				JSONArray mainArr=new JSONArray();
				
				
				p=con.prepareStatement("select status,qno,start_time,end_time from answer where uid=?");
				
				for(int i=0;i<id.size();i++)
				{
					JSONObject jo=new JSONObject();
					
					jo.put("name",name.get(i));
					jo.put("id", id.get(i));
					
					p.setInt(1, id.get(i));
					
					rs=p.executeQuery();
					
					JSONArray resultArr=new JSONArray();
					
					while(rs.next())
					{
						JSONObject resultjson=new JSONObject();
						resultjson.put("qno", rs.getInt("qno"));
						resultjson.put("status", rs.getString("status"));
						resultjson.put("start_time", rs.getTime("start_time"));
						resultjson.put("end_time", rs.getTime("end_time"));
						
						resultArr.put(resultjson);
					}
					jo.put("result", resultArr);
					
					mainArr.put(jo);
				}
				mainObj.put("results",mainArr);
				
				p=con.prepareStatement("select qndesc from question where pid=? order by id");
				p.setInt(1, pid);
				
				rs=p.executeQuery();
				
				mainArr=new JSONArray();
				while(rs.next())
				{
					mainArr.put(rs.getString("qndesc"));
				}
				mainObj.put("questions", mainArr);
				
				
				response.getWriter().print(mainObj);
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
	}

}
