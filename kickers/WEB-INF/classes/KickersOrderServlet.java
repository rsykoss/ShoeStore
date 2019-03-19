import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class KickersOrderServlet extends HttpServlet {  // JDK 6 and above only
 
   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
                     throws ServletException, IOException {
      // Set the MIME type for the response message
      response.setContentType("text/html");
      // Get a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
 
      Connection conn = null;
      Statement stmt = null;
      try {
         // Step 1: Create a database "Connection" object
         // For MySQL
         Class.forName("com.mysql.jdbc.Driver");  // Needed for JDK9/Tomcat9
         conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/kickers?useSSL=false&serverTimezone=UTC", "myuser", "xxxx");  // <<== Check
 
         // Step 2: Create a "Statement" object inside the "Connection"
         stmt = conn.createStatement();

         // Step 3: Execute a SQL SELECT query
          out.println("<html><head><title>Order Results</title></head><body>");

         // Retrieve the shoes' id. Can order more than one shoes.
         String[] ids = request.getParameterValues("id");
         if (ids != null) {
            String sqlStr;
            int count;
      
            // Process each of the shoes
            for (int i = 0; i < ids.length; ++i) {
                     // Update the qty of the table shoes
                     sqlStr = "UPDATE shoes SET qty = qty - 1 WHERE id = " + ids[i];
                     out.println("<p>" + sqlStr + "</p>");  // for debugging
                     count = stmt.executeUpdate(sqlStr);
                     out.println("<p>" + count + " record updated.</p>");

                     // String[] files = request.getParameterValues("fileToUpload"); //file upload
                     // if (files != null){
                     //    String fileString = files[i]; //assuming can only upload one file
                     //    out.println("<p>The file was uploaded: " + fileString + "</p>");
                     //    fileString = "INSERT INTO files (id, image) VALUES("+ ids[i] + ", LOAD_FILE('C:/Users/iisonya/Downloads/"+ fileString +"'))";
                     //    out.println("<p>" + fileString + "</p>");
                     //    count = stmt.executeUpdate(fileString);
                     //   out.println("<p>" + count + " record inserted.</p>");
                     // } else out.println("<p>The file was NOT uploaded </p>");

                     // Create a transaction record
                     sqlStr = "INSERT INTO order_records (id, qty_ordered) VALUES ("
                           + ids[i] + ", 1)";
                     out.println("<p>" + sqlStr + "</p>");  // for debugging
                     count = stmt.executeUpdate(sqlStr);
                     out.println("<p>" + count + " record inserted.</p>");
                     out.println("<h3>Your order for shoes id=" + ids[i]
                           + " has been confirmed.</h3>");
            }
            out.println("<h3>Thank you.<h3>");
         } else { // No book selected
            out.println("<h3>Please go back and select a book...</h3>");
         }
         out.println("</body></html>");
      } catch (SQLException ex) {
         ex.printStackTrace();
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      } finally {
         out.close();
         try {
            // Step 5: Close the Statement and Connection
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
         } catch (SQLException ex) {
            ex.printStackTrace();
         }
      }
   }
}