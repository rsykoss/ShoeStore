import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class CategoryQueryServlet extends HttpServlet {  // JDK 6 and above only
 
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
         String[] category = request.getParameterValues("category");  // Returns an array
         if (category == null) {
               out.println("<h2>Please go back and select an author</h2>");
               return; // Exit doGet()
         } else out.println(category[0]);
         String sqlStr = "SELECT shoes.name, shoes.brand, shoes.category, shoes.price, shoes.qty, shoes.colour, shoe_image.location FROM shoes, shoe_image WHERE shoes.id = shoe_image.id AND category = "
               + "'" + request.getParameter("category") + "'"
               + " AND qty > 0 ORDER BY name ASC, brand ASC";
 
         // Print an HTML page as output of query
         out.println("<html><head><h1>Query Results</h1></head><body>");
         out.println("<h2>Thank you for your query.</h2>");
         out.println("<p>You query is: " + sqlStr + "</p>"); // Echo for debugging
         ResultSet rset = stmt.executeQuery(sqlStr); // Send the query to the server
 
         // Step 4: Process the query result
         int count = 0;
         while(rset.next()) {
            // Print a paragraph <p>...</p> for each row
            out.println("<p>" + rset.getString("shoes.name"));
            out.println("<p>$" + rset.getDouble("shoes.price") + "</p>");
            out.println("<img src='ShoeImage/" + rset.getString("shoe_image.location") + "' alt ='picture'/><br></br>");
            //  out.println("<p>" + rset.getString("name")
            //       + ", " + rset.getString("brand") 
            //       + ", " + rset.getString("colour")
            //       + ", $" + rset.getDouble("price") + "</p>");
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
   @Override
   public void doPost (HttpServletRequest request, HttpServletResponse response)
                   throws ServletException, IOException {
      doGet(request, response);  // Re-direct POST request to doGet()
   }
}