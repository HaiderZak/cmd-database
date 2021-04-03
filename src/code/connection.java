package code;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class connection {
	public static void main(String[] args) throws SQLException {
		Scanner uName = new Scanner(System.in);
		Scanner pwd = new Scanner(System.in);
		System.out.println("Enter username for SQL database entry:");
		String user = uName.nextLine();
		System.out.println("Enter password for SQL database entry:");
		String pass = pwd.nextLine();
		try {
			Class.forName("org.postgresql.Driver");
		}
		catch(ClassNotFoundException e) {
			System.err.println("Where is your PostGreSQL JDBC Driver?" + " Include in your library path!");
			e.printStackTrace();
		}

		try(Connection db = DriverManager.getConnection("jdbc:postgresql://web0.site.uottawa.ca:15432/group_b02_g27", user, pass)){
			System.out.println("Logged in!" + "\n");
			Statement st = db.createStatement();

			System.out.println("Enter 'e' for employee or 'c' for customer: ");
			Scanner eOrc = new Scanner(System.in);
			String getRole = eOrc.nextLine();	

			String getCustomerSIN;
			String getEmployeeSIN;
			String getEmployeeID;
			Integer getIDforBooking;
			Integer getIDforRenting;
			Integer getIDforDateRent;
			Integer getIDforDateBook;
			String getCheckoutRent;
			String getCheckoutBook;
			Integer archID;
		//FOR EMPLOYEE
			if(getRole.equals("e")) {
				Scanner g = new Scanner(System.in);
				System.out.println("Enter a concierge's SIN followed by Employee ID to login. e.g => 231380491 836");
				String[] getEmployeeLogin = g.nextLine().split(" ");
				getEmployeeSIN = getEmployeeLogin[0];
				getEmployeeID = getEmployeeLogin[1];
				//VERIFY SIN AND EMPLOYEE ID
				PreparedStatement ps5 = db.prepareStatement("SELECT sin,employee_id FROM \"Hotel_Management_DBMS\".employee WHERE sin IN (SELECT c.sin FROM \"Hotel_Management_DBMS\".concierge as c WHERE sin=?);");
				ps5.setInt(1, Integer.parseInt(getEmployeeSIN));
				ResultSet rs5 = ps5.executeQuery();
				boolean flag = false;
				while(rs5.next()) {
					if(rs5.getInt(1) == Integer.parseInt(getEmployeeSIN) && rs5.getInt(2) == Integer.parseInt(getEmployeeID)) {
						flag = true;
					}
				}
				if(flag == false) {
					System.out.println("SIN and/or Employee ID not found in list of concierges! Try using the example one. Exiting...");
					System.exit(0);
				}
				System.out.println("Here is a list of available rooms:" + "\n");
				ResultSet rs = st.executeQuery("SELECT room_type, room_num, room_view, can_be_extended, room_price, hotel_address FROM \"Hotel_Management_DBMS\".Room EXCEPT (SELECT room_type, room_num, room_view, can_be_extended, room_price, hotel_address FROM \"Hotel_Management_DBMS\".Archives WHERE arch_id IN (SELECT r_arch_id FROM \"Hotel_Management_DBMS\".Rentals WHERE r_arch_id = arch_id AND has_checked=false) OR arch_id IN (SELECT b_arch_id FROM \"Hotel_Management_DBMS\".Bookings WHERE b_arch_id = arch_id AND b_arch_id IN (SELECT t.b_arch_id FROM \"Hotel_Management_DBMS\".Transform as t WHERE t.b_arch_id = b_arch_id)));");
				int i = 0;
				List<Room> arr = new ArrayList<Room>();
				while(rs.next()) {
					String t = ("ID: " + i  + " -- Addr: " + rs.getString(6) + "--Type: " + rs.getString(1) + "--Num: " + rs.getString(2) + "--View: " + rs.getString(3) + "--Extendable:" + rs.getString(4) + "--Price: $" + rs.getString(5));
					System.out.println(t);
					Room room = new Room(rs.getInt(2),rs.getString(1),rs.getDouble(5),rs.getString(3),rs.getString(6),rs.getBoolean(4));
					arr.add(room);
					i++;
				}
				System.out.println("Type 'b' if you wish to transform a booking to a rental or anything else to quit: ");
				String nL = g.nextLine();
				if(nL.equals("b")) {
					System.out.println("\n" + "Here is a list of all booked rooms: " + "\n");
					ResultSet rs2 = st.executeQuery("SELECT * FROM \"Hotel_Management_DBMS\".Archives A, \"Hotel_Management_DBMS\".Bookings B, \"Hotel_Management_DBMS\".Makes M WHERE A.arch_ID = B.b_arch_ID and B.b_arch_ID = M.arch_ID AND B.b_arch_ID NOT IN (Select b_arch_id FROM \"Hotel_Management_DBMS\".transform) AND B.b_arch_id NOT IN (SELECT r_arch_id FROM \"Hotel_Management_DBMS\".rentals);");
					int h = 0;
					List<Transform> transformList = new ArrayList<Transform>();
					List<Rental> rentalList = new ArrayList<Rental>();
					Integer cSIN = 0;
					while(rs2.next()) {
						String t = ("ID: " + h + " -- Addr: " + rs2.getString("hotel_address") + "--Type: " + rs2.getString("room_type") + "--Num: " + rs2.getString("room_num") + "--View: " + rs2.getString("room_view") + "--Extendable:" + rs2.getBoolean("can_be_extended") + "--Price: $" + rs2.getString("room_price") + "--Check-in: " + rs2.getDate("check_in_date") + "--Check-out: " + rs2.getDate("check_out_date") + "--SIN: " + rs2.getInt("SIN") + ");");
						System.out.println(t + "\n");
						cSIN = rs2.getInt("SIN");
						Transform transform = new Transform(rs2.getInt("b_arch_id"),rs2.getInt("arch_id"),Integer.parseInt(getEmployeeSIN),rs2.getString("entry_date"));
						transformList.add(transform);
						Rental rental = new Rental(rs2.getInt("b_arch_id"),0,rs2.getInt("room_price"),false,false,rs2.getDate("entry_date"));
						rentalList.add(rental);
						h++;
					}
					System.out.println("Type the ID of the customer you wish to transform to a rental: ");
					int index = g.nextInt();
					st = db.createStatement();
					String sql2 = "INSERT INTO \"Hotel_Management_DBMS\".Rentals VALUES (" + rentalList.get(index).getArchID() + ",0," + rentalList.get(index).getAmountOwing() + ",false,false,'" + rentalList.get(index).getDate() + "');";
					st.executeUpdate(sql2);	
					st = db.createStatement();
					String sql3 = "INSERT INTO \"Hotel_Management_DBMS\".Transform VALUES (" + transformList.get(index).getBArchID() + "," + transformList.get(index).getAArchID() + 
							"," + transformList.get(index).getEmpID() + ",'" + transformList.get(index).getDate() + "');";
					st.executeUpdate(sql3);	
					System.out.println("Customer with SIN: " + cSIN + " successfully transformed booking to rental. Exiting...");
					System.exit(0);
				}
//				if(nL.equals("r")) {
//					System.out.println("\n" + "Here is a list of all rentals not checked in yet: " + "\n");
//					ResultSet rs2 = st.executeQuery("SELECT * From \"Hotel_Management_DBMS\".Archives A, \"Hotel_Management_DBMS\".Rentals R WHERE A.arch_ID = R.r_arch_id and R.r_arch_id NOT IN (SELECT c.arch_id FROM \"Hotel_Management_DBMS\".check_in as c)");
//					int h = 0;
//					List<CheckIn> checkList = new ArrayList<CheckIn>();
//					while(rs2.next()) {
//						String t = ("ID: " + h + " -- Addr: " + rs2.getString("hotel_address") + "--Type: " + rs2.getString("room_type") + "--Num: " + rs2.getString("room_num") + "--View: " + rs2.getString("room_view") + "--Extendable:" + rs2.getBoolean("can_be_extended") + "--Price: $" + rs2.getString("room_price") + "--Check-in: " + rs2.getDate("check_in_date") + "--Check-out: " + rs2.getDate("check_out_date") + "--SIN: " + rs2.getInt("SIN"));
//						System.out.println(t + "\n");
//						CheckIn check = new CheckIn(rs2.getInt("SIN"),rs2.getInt("r_arch_id"),Integer.parseInt(getEmployeeSIN));
//						checkList.add(check);
//						h++;
//					}
//					System.out.println("Type the ID of the customer you wish to check in: ");
//					int index = g.nextInt();
//					st = db.createStatement();
//					String sql3 = "INSERT INTO \"Hotel_Management_DBMS\".Check_In VALUES (" + checkList.get(index).getSIN() + "," + checkList.get(index).getArchID() + 
//							"," + checkList.get(index).getEmpID() + ");";
//					st.executeUpdate(sql3);	
//					System.out.println("Customer with SIN: " + checkList.get(index).getSIN() + " successfully checked in rental. Exiting...");
//				}
				else {
					System.out.println("Unknown command. Exiting..");
					System.exit(0);
				}
				rs5.close();
				g.close();
			}
		//FOR CUSTOMER
			if(getRole.equals("c")){
				Scanner f = new Scanner(System.in);
				System.out.println("Enter SIN to login. e.g => 863478592");
				getCustomerSIN = f.nextLine();
				//VERIFY SIN
				PreparedStatement ps5 = db.prepareStatement("SELECT sin FROM \"Hotel_Management_DBMS\".customer WHERE sin=?");
				ps5.setInt(1, Integer.parseInt(getCustomerSIN));
				ResultSet rs5 = ps5.executeQuery();
				boolean flag = false;
				while(rs5.next()) {
					if(rs5.getInt(1) == Integer.parseInt(getCustomerSIN)) {
						flag = true;
					}
				}
				if(flag == false) {
					System.out.println("SIN not found! Try using the example one. Exiting...");
					System.exit(0);
				}
				System.out.println("Here is a list of available rooms:" + "\n");
				ResultSet rs = st.executeQuery("SELECT room_type, room_num, room_view, can_be_extended, room_price, hotel_address FROM \"Hotel_Management_DBMS\".Room EXCEPT (SELECT room_type, room_num, room_view, can_be_extended, room_price, hotel_address FROM \"Hotel_Management_DBMS\".Archives WHERE arch_id IN (SELECT r_arch_id FROM \"Hotel_Management_DBMS\".Rentals WHERE r_arch_id = arch_id AND has_checked=false) OR arch_id IN (SELECT b_arch_id FROM \"Hotel_Management_DBMS\".Bookings WHERE b_arch_id = arch_id AND b_arch_id IN (SELECT t.b_arch_id FROM \"Hotel_Management_DBMS\".Transform as t WHERE t.b_arch_id = b_arch_id)));");
				int i = 0;
				List<Room> arr = new ArrayList<Room>();
				while(rs.next()) {
					String t = ("ID: " + i  + " -- Addr: " + rs.getString(6) + "--Type: " + rs.getString(1) + "--Num: " + rs.getString(2) + "--View: " + rs.getString(3) + "--Extendable:" + rs.getString(4) + "--Price: $" + rs.getString(5));
					System.out.println(t);
					Room room = new Room(rs.getInt(2),rs.getString(1),rs.getDouble(5),rs.getString(3),rs.getString(6),rs.getBoolean(4));
					arr.add(room);
					i++;
				}

				System.out.println("\n" + "Here is a list of rooms you have booked: " + "\n");
				ResultSet rs2 = st.executeQuery("SELECT room_type, room_num, room_view, can_be_extended, room_price, hotel_address, check_in_date, check_out_date FROM \"Hotel_Management_DBMS\".Archives "
						+ "WHERE arch_id IN (SELECT c.arch_id FROM \"Hotel_Management_DBMS\".check_in as c "
						+ "WHERE arch_id = c.arch_id AND c.sin IN (SELECT sin FROM \"Hotel_Management_DBMS\".customer WHERE sin=" + Integer.parseInt(getCustomerSIN) + "))"
						+ "OR arch_id IN (SELECT m.arch_id FROM \"Hotel_Management_DBMS\".makes as m WHERE arch_id = m.arch_id AND m.sin IN "
						+ "(SELECT sin FROM \"Hotel_Management_DBMS\".customer WHERE sin=" + Integer.parseInt(getCustomerSIN) + "))");
				int h = 0;
				while(rs2.next()) {
					String t = ("ID: " + h + " -- Addr: " + rs2.getString(6) + "--Type: " + rs2.getString(1) + "--Num: " + rs2.getString(2) + "--View: " + rs2.getString(3) + "--Extendable:" + rs2.getBoolean(4) + "--Price: $" + rs2.getString(5) + "--Check-in: " + rs2.getDate(7) + "--Check-out: " + rs2.getDate(8));
					System.out.println(t + "\n");
					h++;
				}
				System.out.println("Type 'b' to book a room or 'r' to rent a room: ");
				String tt = f.nextLine();

				//BOOKING 
				if(tt.equals("b")) {
					System.out.println("Enter ID of Room you wish to book: ");
					getIDforBooking = f.nextInt();
					ResultSet bookSet = st.executeQuery("WITH booked_dates AS (Select check_in_date, check_out_date FROM \"Hotel_Management_DBMS\".archives WHERE (room_num,hotel_address,room_type,room_view,can_be_extended,room_price) = ("+arr.get(getIDforBooking).getRoomNum()+",'" + arr.get(getIDforBooking).getHotelAddress() +"','" + arr.get(getIDforBooking).getRoomType()+"','" + arr.get(getIDforBooking).getRoomView()+"'," + arr.get(getIDforBooking).can_be_extended() + "," + arr.get(getIDforBooking).getRoomPrice()+") AND check_out_date IN (select dates FROM \"Hotel_Management_DBMS\".date_bank)) SELECT dates FROM \"Hotel_Management_DBMS\".date_bank WHERE dates not in (select dates FROM \"Hotel_Management_DBMS\".date_bank cross join booked_dates where dates between check_in_date and check_out_date);");
					List<String> listOfDates = new ArrayList<String>();
					int k = 0;
					while(bookSet.next()) {
						listOfDates.add(bookSet.getString(1));
						System.out.println("ID: " + k + " -- " + bookSet.getString(1));
						k++;
					}
					System.out.println("Choose an ID for the booking date: ");
					getIDforDateBook = f.nextInt();
					System.out.println("How many days would you like stay? ");
					int computeDate = f.nextInt();
					getCheckoutBook = getResultDate(listOfDates.get(getIDforDateBook),computeDate);
					System.out.println("Booking room... " + arr.get(getIDforBooking).toString() + " on " + listOfDates.get(getIDforDateBook) + " and you will be checking out on " + getCheckoutBook);						

					//GET ALL ARCHID'S AND ADD THEM INTO A LIST
					ResultSet bookSet2 = st.executeQuery("SELECT arch_id FROM \"Hotel_Management_DBMS\".Archives;");
					List<Integer> listOfArchID = new ArrayList<Integer>();
					while(bookSet2.next()) {
						listOfArchID.add(bookSet2.getInt(1));
					}
					archID = getUniqueArchID(listOfArchID);

					st = db.createStatement();
					String sql = "INSERT INTO \"Hotel_Management_DBMS\".Archives VALUES (" + archID + ",'" + listOfDates.get(getIDforDateBook) + "','" + arr.get(getIDforBooking).getRoomType() + "','" + listOfDates.get(getIDforDateBook) + "','" + getCheckoutBook + "'," + getNumOfOcc() + "," + arr.get(getIDforBooking).getRoomNum() + ",'" + arr.get(getIDforBooking).getHotelAddress() + "','" + arr.get(getIDforBooking).getRoomView() + "'," + arr.get(getIDforBooking).can_be_extended() + "," + arr.get(getIDforBooking).getRoomPrice() + ");";
					st.executeUpdate(sql);
					st = db.createStatement();
					String sql2 = "INSERT INTO \"Hotel_Management_DBMS\".Bookings VALUES (" + archID + ",false);";
					st.executeUpdate(sql2);
					st = db.createStatement();
					String sql3 = "INSERT INTO \"Hotel_Management_DBMS\".Makes VALUES (" + getCustomerSIN + "," + archID + ");";
					st.executeUpdate(sql3);
					System.out.println("You have booked the room! Now you must wait for a concierge to transfer you to a rental. Exiting program...");
					System.exit(0);
					bookSet.close();
				}
				//RENTING
				if(tt.equals("r")) {
					System.out.println("Enter ID of Room you wish to rent: ");
					getIDforRenting = f.nextInt();
					ResultSet rentSet = st.executeQuery("WITH booked_dates AS (Select check_in_date, check_out_date FROM \"Hotel_Management_DBMS\".archives WHERE (room_num,hotel_address,room_type,room_view,can_be_extended,room_price) = ("+arr.get(getIDforRenting).getRoomNum()+",'" + arr.get(getIDforRenting).getHotelAddress() +"','" + arr.get(getIDforRenting).getRoomType()+"','" + arr.get(getIDforRenting).getRoomView()+"'," + arr.get(getIDforRenting).can_be_extended() + "," + arr.get(getIDforRenting).getRoomPrice()+") AND check_out_date IN (select dates FROM \"Hotel_Management_DBMS\".date_bank)) SELECT dates FROM \"Hotel_Management_DBMS\".date_bank WHERE dates not in (select dates FROM \"Hotel_Management_DBMS\".date_bank cross join booked_dates where dates between check_in_date and check_out_date);");
					List<String> listOfDates = new ArrayList<String>();
					int k = 0;
					while(rentSet.next()) {
						listOfDates.add(rentSet.getString(1));
						System.out.println("ID: " + k + " -- " + rentSet.getString(1));
						k++;
					}
					System.out.println("Choose an ID for the renting date: ");
					getIDforDateRent = f.nextInt();
					System.out.println("How many days would you like stay? ");
					int computeDate = f.nextInt();
					getCheckoutRent = getResultDate(listOfDates.get(getIDforDateRent),computeDate);
					System.out.println("Renting room... " + arr.get(getIDforRenting).toString() + " on " + listOfDates.get(getIDforDateRent) + " and you will be checking out on " + getCheckoutRent);								

					//GET ALL ARCHID'S AND ADD THEM INTO A LIST
					ResultSet rentSet2 = st.executeQuery("SELECT arch_id FROM \"Hotel_Management_DBMS\".Archives;");
					List<Integer> listOfArchID = new ArrayList<Integer>();
					while(rentSet2.next()) {
						listOfArchID.add(rentSet2.getInt(1));
					}
					archID = getUniqueArchID(listOfArchID);

					st = db.createStatement();
					String sql = "INSERT INTO \"Hotel_Management_DBMS\".Archives VALUES (" + archID + ",'" + listOfDates.get(getIDforDateRent) + "','" + arr.get(getIDforRenting).getRoomType() + "','" + listOfDates.get(getIDforDateRent) + "','" + getCheckoutRent + "'," + getNumOfOcc() + "," + arr.get(getIDforRenting).getRoomNum() + ",'" + arr.get(getIDforRenting).getHotelAddress() + "','" + arr.get(getIDforRenting).getRoomView() + "'," + arr.get(getIDforRenting).can_be_extended() + "," + arr.get(getIDforRenting).getRoomPrice() + ");";
					st.executeUpdate(sql);
					st = db.createStatement();
					String sql2 = "INSERT INTO \"Hotel_Management_DBMS\".Rentals VALUES (" + archID + ",0," + arr.get(getIDforRenting).getRoomPrice() + ",false,false,'" + getCheckoutRent + "');";
					st.executeUpdate(sql2);
					PreparedStatement ps = db.prepareStatement("SELECT sin FROM \"Hotel_Management_DBMS\".concierge WHERE sin IN (SELECT e.sin FROM \"Hotel_Management_DBMS\".employee as e WHERE e.sin = sin AND e.sin IN (SELECT w.sin FROM \"Hotel_Management_DBMS\".works_at as w WHERE e.sin = w.sin AND hotel_address=?))");
					getEmployeeSIN = "";
					ps.setString(1,arr.get(getIDforRenting).getHotelAddress());
					ResultSet test = ps.executeQuery();
					while(test.next()) {
						getEmployeeSIN = test.getString(1);
					}
					st = db.createStatement();
					String sql3 = "INSERT INTO \"Hotel_Management_DBMS\".Check_In VALUES (" + Integer.parseInt(getCustomerSIN) + "," + archID + 
							"," + Integer.parseInt(getEmployeeSIN) + ");";
					st.executeUpdate(sql3);	
					System.out.println("You have rented the room! Exiting program...");
					System.exit(0);
					rentSet.close();
				}
				rs.close();
				rs2.close();
				f.close();
			}
			st.close();
			if(getRole.equals("q")) {
				System.out.println("Quitting...");
				System.exit(0);
			}
		}
		catch(SQLException ex) {
			System.out.println("Something went wrong! Exiting..");
			ex.printStackTrace();
			System.exit(0);
			Logger lgr = Logger.getLogger(connection.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	public static int getNumOfOcc() {
		Random random = new Random();
		return random.nextInt(5) + 1;
	}
	//Generate Unique Arch_ID => Starting at 1, we will look for the first positive integer that is not in archIDList and return this
	public static int getUniqueArchID(List<Integer> archIDList) {
		Set<Integer> set = new HashSet<>();
		for (int a : archIDList) {
			if (a > 0) {
				set.add(a);
			}
		}
		for (int i = 1; i <= archIDList.size() + 1; i++) {
			if (!set.contains(i)) {
				return i;
			}
		}
		return 0;
	}
	public static String getResultDate(String oldDate, int daysAdded) { 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try{
			c.setTime(sdf.parse(oldDate));
		}catch(ParseException e){
			e.printStackTrace();
		}
		c.add(Calendar.DAY_OF_MONTH, daysAdded);  
		String newDate = sdf.format(c.getTime());  
		return newDate;
	}
}


