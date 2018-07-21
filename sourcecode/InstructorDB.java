import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

////////////////////////////////
// Instructor Class
// Instruct과 관련된 처리를 담당하는 Class입니다.
////////////////////////////////
public class InstructorDB {
	enum Semester {
		Spring(1), Summer(2), Fall(3), Winter(4);
		
		private Integer value;
		private Semester(Integer value) { this.value = value; }
		public Integer getSemester() { return this.value; }
		
	}
	
	public static boolean isInstructor(String id, String name, Connection conn) {
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT COUNT (*) "
					   + "FROM instructor "
					   + "WHERE ID = ? and NAME = ?");
			stmt.setString(1, id);
			stmt.setString(2, name);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			int result = rs.getInt(1);
			
			if (result == 1) return true;
			else return false;
			
		} catch(SQLException se) {
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} 
		
		return false;
	}
	
	public static void instructorMenu(UserInfoManager i_manager, Connection conn) {
		while (true) {
			System.out.println("");
			System.out.println("Please select instructor menu");
			System.out.println("1) Course report");
			System.out.println("2) Advisee report");
			System.out.println("0) exit");
			System.out.print(">> ");
			
			Scanner scan = new Scanner(System.in);
			String input = scan.nextLine();
				
			if (input.equals("0")) break;
			else if (input.equals("1")) {
				// print course report
				printCourseReport(i_manager, conn);
			}
			else if (input.equals("2")) {
				// print advisee report
				printAdviseeReport(i_manager, conn);
			}
			else {
				System.out.println("wrong input.");
			}
		}
	}
	public static void printCourseReport(UserInfoManager i_manager, Connection conn) {
		System.out.print("Course report - ");
		try {
			// A query that finds the list of courses provided by the instructor
			// in the most recent year by using MAX(year) amongst natural joins of tables
			// Tables teaches, course, section being joined to obtain various information at once
			// such as course id, building or room number, time slot id, and so on
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM teaches NATURAL JOIN "
					+ "course NATURAL JOIN section WHERE year = "
					+ "(SELECT MAX(year) FROM teaches WHERE id = ? ) AND id = ?");
			stmt.setString(1, i_manager.getId());
			stmt.setString(2, i_manager.getId());
			ResultSet rs = stmt.executeQuery();
			ArrayList<String[]> arrayList = new ArrayList<String[]>();
			String year = "";
			String semester = "";
			while (rs.next())
			{
				year = rs.getString("year");
				String[] string = new String[6];
				string[0] = rs.getString("course_id");
				string[1] = rs.getString("sec_id");
				string[2] = rs.getString("title");
				string[3] = rs.getString("building");
				string[4] = rs.getString("room_number");
				string[5] = rs.getString("time_slot_id");
				// A course with a later semester must be more recent course
				// because courses we have are held in the same year
				// To get detailed information on comparing semesters,
				// refer to enum Semester on the top of the file
				if (semester.equals(""))
				{
					semester = rs.getString("semester");
					arrayList.add(string);
				}
				else if (Semester.valueOf(rs.getString("semester")).getSemester() > Semester.valueOf(semester).getSemester())
				{
					arrayList.clear();
					semester = rs.getString("semester");
					arrayList.add(string);
				}
				else if (Semester.valueOf(rs.getString("semester")).getSemester() == Semester.valueOf(semester).getSemester())
				{
					arrayList.add(string);
				}
			}
			rs.close();
			
			// Do nothing were it not for courses
			if (arrayList.isEmpty()) return;
			
			System.out.print(year + " " + semester + "\n");
			for (String[] entry : arrayList)
			{
				// A query that gets the day that a course is on
				// and the starting- and ending-time of the course 
				stmt = conn.prepareStatement("SELECT * FROM time_slot WHERE time_slot_id = ?");
				stmt.setString(1, entry[5]);
				rs = stmt.executeQuery();
				String start = null, end = null;
				ArrayList<String> day = new ArrayList<String>();
				while (rs.next())
				{
					start = rs.getString("start_hr") + " : " + rs.getString("start_min"); 
					end = rs.getString("end_hr") + " : " + rs.getString("end_min");
					day.add(rs.getString("day"));
				}
				rs.close();
				
				System.out.print("\n" + entry[0] + "\t" + entry[2] + "\t" + "[" + entry[3] +
						" " + entry[4] + "] (");
				Iterator<String> iterator = day.iterator();
				while (iterator.hasNext())
				{
					String string = iterator.next();
					if (iterator.hasNext()) System.out.print(string + ", ");
					else System.out.print(string + " ");
				}
				System.out.print(start + " - " + end + ")\n");
				System.out.println("ID\tname\tdept_name\tgrade");
				
				// A query that finds the list of students who have taken the given course
				// in the given semester amongst the natural join of takes and student
				stmt = conn.prepareStatement("SELECT id, name, dept_name, grade FROM "
						+ "takes NATURAL JOIN student WHERE "
						+ "(course_id = ? AND sec_id = ? AND year = ? AND semester = ?)");
				stmt.setString(1, entry[0]);
				stmt.setString(2, entry[1]);
				stmt.setString(3, year);
				stmt.setString(4, semester);
				rs = stmt.executeQuery();
				
				while (rs.next())
				{
					System.out.println(rs.getString("id") + "\t" + rs.getString("name") + "\t" +
							rs.getString("dept_name") + "\t" + rs.getString("grade"));
				}
				rs.close();
			}
		} catch(SQLException se) {
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void printAdviseeReport(UserInfoManager i_manager, Connection conn)
	{
		System.out.println("ID\tname\tdept_name\ttot_cred");
		try {
			// A query which finds ids, names, department names and total credits of students
			// whom the instructor advises amongst the natural join of tables advisor and student
			// using the id of the instructor as a key
			// Natural join being achieved under constraint that ids of students in advisor
			// and ids in student
			PreparedStatement stmt = conn.prepareStatement("SELECT id, name, dept_name, tot_cred "
								       + "FROM advisor NATURAL JOIN student "
								      + "WHERE advisor.s_id = student.id AND advisor.i_id = ?");
			stmt.setString(1, i_manager.getId());
			ResultSet rs = stmt.executeQuery();
			
			// Output all students from the query
			while (rs.next())
			{
				System.out.print(rs.getString("id") + "\t");
				System.out.print(rs.getString("name") + "\t");
				System.out.print(rs.getString("dept_name") + "\t");
				System.out.print(rs.getString("tot_cred") + "\n");
			}

			rs.close();
			
		} catch(SQLException se) {
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
}

