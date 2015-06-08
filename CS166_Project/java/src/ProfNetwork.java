/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
//for getting the date and time
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of ProfNetwork 
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end ProfNetwork

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
       
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup
   
   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      ProfNetwork esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the ProfNetwork object and creates a physical
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         
         esql = new ProfNetwork (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Goto Friend List");
                System.out.println("2. Update Password");
                System.out.println("3. Write a new message");
                System.out.println("4. View message");
                System.out.println("5. Send Friend Request");
                System.out.println("6. Search for User");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql); break;
                   case 2: UpdatePassword(esql, authorisedUser); break;
                   case 3: NewMessage(esql, authorisedUser); break;
                   case 4: ViewMessage(esql, authorisedUser); break;
                   case 5: SendRequest(esql); break;
                   case 6: SearchUser(esql); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and email
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user email: ");
         String email = in.readLine();
         System.out.print("\tEnter user name: ");
         String name = in.readLine();
         System.out.print("\tEnter user date of birth: ");
         String dob = in.readLine();

	 //Creating empty contact\block lists for a user
	 String query = String.format("INSERT INTO USR (userId, password, email, name, dateOfBirth) VALUES ('%s','%s','%s','%s','%s')", login, password, email, name, dob);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here
  public static void SearchUser(ProfNetwork esql){
    try{
         System.out.print("\tLook for user by name: ");
         String search_name = in.readLine();
         String query = String.format("SELECT * FROM USR WHERE name = '%s'", search_name);
         int userNum = esql.executeQuery(query);
         if (userNum > 0){
           System.out.print("\tUser exists!\n");
         }
         else System.out.print("\tInvalid user!\n");
      }catch(Exception e){}
  }
  /**
   * 0 - no delete
   * 1 - delete from sender
   * 2 - delete from receiver
   * 3 - delete by both
   * */
  public static void ViewMessage(ProfNetwork esql, String curUser){
    try{
      boolean in_or_out = false;
      int mode = 0;
      while(!in_or_out){
        System.out.print("\t1.View Inbox?\n");
        System.out.print("\t2.View Outbox?\n");
        String answer = in.readLine();
        if(answer.equals("1")){
          mode =1;
          in_or_out = true;
        }
        else if (answer.equals("2")){
          mode =2;
          in_or_out = true;
        }
      }
      if(mode == 1){
        String query = String.format("SELECT * FROM MESSAGE WHERE receiverId = '%s' AND deleteStatus = 0", curUser);
        String query2 = String.format("SELECT * FROM MESSAGE WHERE receiverId = '%s' AND deleteStatus = 2", curUser);
        esql.executeQueryAndPrintResult(query);
        esql.executeQueryAndPrintResult(query2);
      }
      else{
        String query = String.format("SELECT * FROM MESSAGE WHERE senderId = '%s' AND deleteStatus = 0", curUser);
        String query2 = String.format("SELECT * FROM MESSAGE WHERE senderId = '%s' AND deleteStatus = 1", curUser);
        esql.executeQueryAndPrintResult(query);
        esql.executeQueryAndPrintResult(query2);
      }
      boolean delete_yn = false;
      int d = 0;
      while(!delete_yn){
        System.out.print("\t1.Delete message?\n");
        System.out.print("\t2.Do not delete message?\n");
        String answer = in.readLine();
        if(answer.equals("1")){
          d =1;
          delete_yn = true;
        }
        else if (answer.equals("2")){
          d =2;
          delete_yn = true;
        }
      }
      if(d == 1){
          System.out.print("\tDelete which message? Type the messageId.\n");
          String answer = in.readLine();
          String query = String.format("SELECT * FROM MESSAGE WHERE senderId = '%s' AND msgId = %s", curUser, answer);
          int check = esql.executeQueryAndPrintResult(query);
          if (check == 0)return;
          else{
            System.out.print("Would you like to delete messageId?\n");
            System.out.print("1.Yes?\n");
            System.out.print("2.No?\n");
            String confirm = in.readLine();
            if(confirm.equals("1")){
              String q = String.format("SELECT deleteStatus FROM MESSAGE WHERE msgId = '%s'", answer);
              List<List<String>> check_del = esql.executeQueryAndReturnResult(q);
              int c = Integer.parseInt(check_del.get(0).get(0));
              if(c ==0 ){
                if (mode == 1){
                  String query_f = String.format("UPDATE MESSAGE SET deleteStatus = 2 WHERE msgId = '%s'", answer);
                  esql.executeUpdate(query_f);
                }
                else if (mode == 2){
                  String query_f = String.format("UPDATE MESSAGE SET deleteStatus = 1 WHERE msgId = '%s'", answer);
                  esql.executeUpdate(query_f);
                }
              }
              else if (c == 1 || c == 2){
                String query_f = String.format("UPDATE MESSAGE SET deleteStatus = 3 WHERE msgId = '%s'", answer);
                esql.executeUpdate(query_f);
              }
            }
          }
        }
      }catch(Exception e){}
  }


  public static void NewMessage(ProfNetwork esql, String sender){
       try{
         //assign msgId by number of messages
         System.out.print("\tSend message to: ");
         String recipient = in.readLine();
         //check if user exists
         String query = String.format("SELECT * FROM USR WHERE userId = '%s'", recipient);
         int userNum = esql.executeQuery(query);
         System.out.print("\tEnter message. Finish message by pressing ENTER : ");
         String message= in.readLine();
         String m;
         String q = "SELECT COUNT(*) FROM MESSAGE";
         List<List<String>> count = esql.executeQueryAndReturnResult(q);
         int c = Integer.parseInt(count.get(0).get(0)) + 1;
         System.out.println(c);
         
         DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
         Date today = Calendar.getInstance().getTime();
         String sendDate = df.format(today);        
         if(userNum > 0){
           m = String.format("INSERT INTO MESSAGE(msgId,senderId,receiverId,contents,sendTime,deleteStatus,status) VALUES (%s,'%s','%s','%s','%s',0,'sent')",c, sender, recipient, message, sendDate);
           System.out.println ("Message sent. \n");
           esql.executeUpdate(m);
         }
         else{
           m = String.format("INSERT INTO MESSAGE(msgId,senderId,receiverId,contents,sendTime,deleteStatus,status) VALUES (%s,'%s','%s','%s','%s',0,'Failed to Deliver')",c, sender, recipient, message, sendDate);
           System.out.println ("Message not sent. Recipient does not exist. \n");
           esql.executeUpdate(m);
         }
         //System.out.println ("Message sent");
      }catch(Exception e){
         //System.err.println (e.getMessage ());
      }
   }
   public static void FriendList(ProfNetwork esql){
       //print friends list
       //executeQueryAndPrintResult(" ");
   }
   public static void UpdatePassword(ProfNetwork esql, String user){
       //string update = "UPDATE ";
       //UPDATE Customers
       //SET ContactName='Alfred Schmidt', City='Hamburg'
       //WHERE CustomerName='Alfreds Futterkiste';
       try{
         boolean confirm = false;
         String choice;
         while(!confirm){
           System.out.print("\tAre you sure you want to edit the password?\n");
           System.out.print("\t1.Yes\n");
           System.out.print("\t2.No\n");
           choice = in.readLine();
           int c = Integer.parseInt(choice);
           if(c == 1) confirm = true;
           else if (c == 2) break;
           else System.out.println("Invalid Input!");
         }
         String newP = null;
         if(confirm){
           System.out.print("\tpassword: ");
           newP = in.readLine();
         }
         if(newP != "\n"){
           String query = String.format("UPDATE usr SET password = '%s' WHERE userId = '%s';", newP, user);
           esql.executeUpdate(query);
         }
       }catch(Exception e){
         System.err.println (e.getMessage ());
      }
       //run executeUpdate(" ")
   }
   
   public static void SendRequest(ProfNetwork esql){
   
   }

}//end ProfNetwork
