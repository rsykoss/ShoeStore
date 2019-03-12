// Saved as "ebookshop\WEB-INF\classes\QueryMultiValueServlet.java".
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class QueryMultiValueServlet extends HttpServlet {  // JDK 6 and above only
 
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
            "jdbc:mysql://localhost:3306/ebookshop?useSSL=false&serverTimezone=UTC", "myuser", "xxxx");  // <<== Check
 
         // Step 2: Create a "Statement" object inside the "Connection"
         stmt = conn.createStatement();

         // Step 3: Execute a SQL SELECT query
        String[] authors = request.getParameterValues("author");  // Returns an array
        if (authors == null) {
            out.println("<h2>Please go back and select an author</h2>");
            return; // Exit doGet()
        } 
        String sqlStr = "SELECT * FROM books WHERE author IN (";
        sqlStr += "'" + authors[0] + "'";  // First author
        for (int i = 1; i < authors.length; ++i) {
        sqlStr += ", '" + authors[i] + "'";  // Subsequent authors need a leading commas
        }
        sqlStr += ") AND qty > 0 ORDER BY author ASC, title ASC";
        if (authors == null) {
            out.println("<h2>Please go back and select an author</h2>");
            return; // Exit doGet()
        } 
         // Print an HTML page as output of query
         out.println("<html><head><title>Query Results</title></head><body>");
         out.println("<h2>Thank you for your query.</h2>");
         out.println("<p>You query is: " + sqlStr + "</p>"); // Echo for debugging
         ResultSet rset = stmt.executeQuery(sqlStr); // Send the query to the server
 
         // Step 4: Process the query result
         int count = 0;
         while(rset.next()) {
            // Print a paragraph <p>...</p> for each row
            out.println("<p>" + rset.getString("author")
                  + ", " + rset.getString("title")
                  + ", $" + rset.getDouble("price") + "</p>");
            ++count;
         }
         out.println("<p>==== " + count + " records found ====</p>");
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