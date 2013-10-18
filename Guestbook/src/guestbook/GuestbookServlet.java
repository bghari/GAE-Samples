package guestbook;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import com.mysql.jdbc.Driver;

import javax.servlet.http.*;
/*import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
*/
import com.google.appengine.api.utils.SystemProperty;

//Cloud SQl example.
@SuppressWarnings("serial")
public class GuestbookServlet extends HttpServlet {
	  @Override
	  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    String url = null;
	    try {
	      if (SystemProperty.environment.value() ==
	          SystemProperty.Environment.Value.Production) {
	        // Load the class that provides the new "jdbc:google:mysql://" prefix.
	        Class.forName("com.mysql.jdbc.GoogleDriver");
	        url = "jdbc:google:mysql://your-project-id:your-instance-name/guestbook?user=root";
	      } else {
	        // Local MySQL instance to use during development.
	        Class.forName("com.mysql.jdbc.Driver");
	        url = "jdbc:mysql://127.0.0.1:3306/guestbook?user=root";
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	      return;
	    }

	    PrintWriter out = resp.getWriter();
	    try {
	      Connection conn = DriverManager.getConnection(url,"root","root");
	      try {
	        String fname = req.getParameter("fname");
	        String content = req.getParameter("content");
	        if (fname == "" || content == "") {
	          out.println(
	              "<html><head></head><body>You are missing either a message or a name! Try again! " +
	              "Redirecting in 3 seconds...</body></html>");
	        } else {
	          String statement = "INSERT INTO entries (guestName, content) VALUES( ? , ? )";
	          PreparedStatement stmt = conn.prepareStatement(statement);
	          stmt.setString(1, fname);
	          stmt.setString(2, content);
	          int success = 2;
	          success = stmt.executeUpdate();
	          if (success == 1) {
	            out.println(
	                "<html><head></head><body>Success! Redirecting in 3 seconds...</body></html>");
	          } else if (success == 0) {
	            out.println(
	                "<html><head></head><body>Failure! Please try again! " +
	                "Redirecting in 3 seconds...</body></html>");
	          }
	        }
	      } finally {
	        conn.close();
	      }
	    } catch (SQLException e) {
	      e.printStackTrace();
	    }
	    resp.setHeader("Refresh", "3; url=/guestbook.jsp");
	  }
	}
