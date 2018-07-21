import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.plaf.synth.SynthProgressBarUI;

////////////////////////////////
//Student Class
//Student�� ���õ� ó���� ����ϴ� Class�Դϴ�.
////////////////////////////////

public class StudentDB {
	public static boolean isStudent(String id, String name, Connection conn) {
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT COUNT (*) "
					   + "FROM student "
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
	
	public static void studentMenu(UserInfoManager i_manager, Connection conn) {
		while (true) {
			System.out.println("");
			System.out.println("Please select student menu");
			System.out.println("1) Student report");
			System.out.println("2) View time table");
			System.out.println("0) exit");
			System.out.print(">> ");
			
			String input;
			
			Scanner scan = new Scanner(System.in);
			input = scan.nextLine();
			
			System.out.println();
			if (input.equals("0")) break;
			else if (input.equals("1")) {
				// print student table
				printStudentTable(i_manager, conn);
			}
			else if (input.equals("2")) {
				// print time table
				printTimeTable(i_manager, conn);
			}
			else {
				System.out.println("wrong input.");
			}
		}
	}
	
	public static void printStudentTable(UserInfoManager i_manager, Connection conn)
	{
		System.out.println("Welcome " + i_manager.getName());
		
		String dept_name = "";
		int tot_cred = 0;
		
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * "
					   + "FROM student "
					   + "WHERE ID = ? and NAME = ?");
			stmt.setString(1, i_manager.getId());
			stmt.setString(2, i_manager.getName());
			ResultSet rs = stmt.executeQuery();
			rs.next();
			dept_name = rs.getString(3);
			tot_cred = rs.getInt(4);
					
		} catch(SQLException se) {
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} 
		
		System.out.println("You are a member of " + dept_name);
		System.out.println("You have taken total " + tot_cred + " credits");
		System.out.println("");
		System.out.println("Semester report");
		System.out.println("");
		
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT YEAR, SEMESTER "
													   	 + "FROM takes "
													   	 + "WHERE ID = ?"
													   	 + "ORDER BY YEAR DESC");
			stmt.setString(1, i_manager.getId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String year = rs.getString(1);
				String semester = rs.getString(2);
				
				// year, semester, gpa_total
				
				PreparedStatement stmt_semester = conn.prepareStatement("SELECT T.GRADE, C.CREDITS "
																	  + "FROM takes T, course C "
																	  + "WHERE T.ID = ? and "
																	  + "	   T.YEAR = ? and "
																	  + "	   T.SEMESTER = ? and "
																	  + "	   T.COURSE_ID = C.COURSE_ID");
				stmt_semester.setString(1, i_manager.getId());
				stmt_semester.setString(2, year);
				stmt_semester.setString(3, semester);
				ResultSet rs_semester = stmt_semester.executeQuery();
				
				float gpa = 0;
				float tot_gp = 0;
				int tot_cred_semester = 0;
				while (rs_semester.next()) {
					String gp_string = rs_semester.getString(1);
					int credits = rs_semester.getInt(2);
					if (gp_string == null) continue;
					else {
						float gp = gpToFloat(gp_string);	
						tot_gp += gp * credits;
					}
					tot_cred_semester += credits;
				}
				
				gpa = tot_gp / tot_cred_semester;
				
				System.out.println(year + '\t' + semester + '\t' + "GPA : " + String.format("%.5f", gpa));
				System.out.println("course_id" + '\t' + 
								   "title" + '\t' + 
								   "dept_name" + '\t' + 
								   "credits" + '\t' + 
								   "grade");
				
				PreparedStatement stmt_takes = conn.prepareStatement("SELECT T.COURSE_ID, C.TITLE, C.DEPT_NAME, C.CREDITS, T.GRADE "
																   + "FROM takes T, course C "
																   + "WHERE T.ID = ? and "
																   + "		T.YEAR = ? and "
																   + "		T.SEMESTER = ? and "
																   + "		T.COURSE_ID = C.COURSE_ID");
				stmt_takes.setString(1, i_manager.getId());
				stmt_takes.setString(2, year);
				stmt_takes.setString(3, semester);
				ResultSet rs_takes = stmt_takes.executeQuery();
				
				while (rs_takes.next()) {
					String takes_course_id = rs_takes.getString(1);
					String takes_title = rs_takes.getString(2);
					String takes_dept_name = rs_takes.getString(3);
					String takes_credits = rs_takes.getString(4);
					String takes_grade = rs_takes.getString(5);
					System.out.println(takes_course_id + '\t' + 
									   takes_title + '\t' + 
									   takes_dept_name + '\t' +
									   takes_credits + '\t' + 
									   takes_grade);
				}
				
				// course_id, title, dept_name, credits, grade
				
				// from takes where ID, year, semester -> course_id, grade
				// from course where course_id -> title, dept_name, credits 
				
				// select T.COURSE_ID, C.TITLE, C.DEPT_NAME, C.CREDITS, T.GRADE
				// from takes T, course C
				// where T.ID = '12345' and T.YEAR = '2009' and T.SEMESTER = 'Spring' and T.COURSE_ID = C.COURSE_ID;
				
			}
		
		} catch(SQLException se) {
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static float gpToFloat (String grade) {
		switch (grade) {
			case "A+": return 4.3f;
			case "A": return 4.0f;
			case "A-": return 3.7f;
			case "B+": return 3.3f;
			case "B": return 3.0f;
			case "B-": return 2.7f;
			case "C+": return 2.3f;
			case "C": return 2.0f;
			case "C-": return 1.7f;
			case "D+": return 1.3f;
			case "D": return 1.0f;
			case "D-": return 0.7f;
			case "F": return 0;
			default: return -1;
		}
	}

	public static void printTimeTable(UserInfoManager i_manager, Connection conn) { // TimeTable�� print�ϴ� method
		System.out.println("Please select semester to view");
		
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT YEAR, SEMESTER "
													   	 + "FROM takes "
													   	 + "WHERE ID = ?"
													   	 + "ORDER BY YEAR DESC");
			stmt.setString(1, i_manager.getId());
			ResultSet rs = stmt.executeQuery();
			
			//////////// �л��� ���Ǹ� ���������� �ִ� year�� semester�� �����ϴ�.
			
			String[][] arr = new String[100][2];
			int i = 0;
			while (rs.next()) { // year, semester ������ array�� ���� �� �޴��� ����մϴ�.
				arr[i][0] = rs.getString(1);
				arr[i][1] = rs.getString(2);
				i++;
				System.out.println(String.valueOf(i) + ") " + rs.getString(1) + " " + rs.getString(2));
			}
			
			System.out.print(">> ");
			
			String input;
			Scanner scan = new Scanner(System.in);
			input = scan.nextLine().trim();
			
			String year;
			String semester;
			try {
				int input_i = Integer.parseInt(input);
				if( input_i <= 0 || input_i > i) { // menu�� ���� ��ȣ�� �������� ��,
					System.out.println("Wrong Input!");
					return;
				}
				
				int select = input_i-1;
				year = arr[select][0];
				semester = arr[select][1];
			}
			catch(NumberFormatException e) {
				System.out.println("Wrong Input!");
				return;
			}
			
			
			
			PreparedStatement stmt2 = conn.prepareStatement("SELECT TAKES.COURSE_ID, "
																+ 		" COURSE.TITLE, "
																+ 		" TIME_SLOT.DAY, "
																+ 		" TIME_SLOT.START_HR, "
																+ 		" TIME_SLOT.START_MIN, "
																+		" TIME_SLOT.END_HR, "
																+ 		" TIME_SLOT.END_MIN "
																+ "FROM TAKES, COURSE, SECTION, TIME_SLOT "
																+ "WHERE TAKES.COURSE_ID = COURSE.COURSE_ID  and "
																+ 		"TAKES.ID = ? and "
																+ 		"TAKES.YEAR = ? and "
																+ 		"TAKES.SEMESTER = ? and "
																+ 		"SECTION.COURSE_ID = TAKES.COURSE_ID and "
																+ 		"TIME_SLOT.TIME_SLOT_ID = SECTION.TIME_SLOT_ID");
			// <SELECT �� ����>
			// ������ �ϴ� ���� ������ year,semester�� �л��� ������ course�� ���� �����Դϴ�.(courseID,title,time)
			// ���ϴ� ������ ��� SELECT ������ ����ϴ�.
			// TAKE.ID = ? -> ������ ���̵�,
			// TAKE.YEAR =? -> ������ year.
			// TAKE.SEMESTER =? -> ������ semester
			// �̷��� ���־��� ������ TAKES.COURSE_ID�� �ش� year,semester�� course_ID�� ���Ե˴ϴ�.
			// �� ���� �� Course_ID�� �´� �߰� ������ �ٸ� relation���� �����;��ϴµ�, 
			// TAKES.COURSE_ID = COURSE.COURSE_ID �� ���ָ�  <COURSE>���̺���� JOIN �������� COURSE_ID�� ���� ���� ����
			// �ش� COURES_ID�� �´� TITLE�� ������ �� �ְ�,
			// ���� ������ SECTION.COURSE_ID = TAKES.COURSE_ID , TIME_SLOT.TIME_SLOT_ID = SECTION.TIME_SLOT_ID
			// �� ���� ���ϴ� COURSE�� TIMESLOT �������� ������ �� �ֽ��ϴ�.
			
			System.out.println();
			stmt2.setString(1, i_manager.getId());
			stmt2.setInt(2, Integer.parseInt(year));
			stmt2.setString(3, semester);
			ResultSet rs2 = stmt2.executeQuery();
			
			
			StringBuilder sb = new StringBuilder();
			
			/// �Ʒ� ���ʹ� ������Ŀ� �°� ���� �۾��Դϴ�.
			sb.append("course_id");
			sb.append('\t');
			sb.append("title");
			sb.append('\t');
			sb.append("day");
			sb.append('\t');
			sb.append("start_time");
			sb.append('\t');
			sb.append("end_time");
			sb.append("\n");
			
			while (rs2.next()) {
				sb.append(rs2.getString(1));
				sb.append('\t');
				sb.append(rs2.getString(2));
				sb.append('\t');
				sb.append(rs2.getString(3));
				sb.append('\t');
				sb.append(rs2.getString(4));
				sb.append(" : ");
				sb.append(rs2.getString(5));
				sb.append('\t');
				sb.append(rs2.getString(6));
				sb.append(" : ");
				sb.append(rs2.getString(7));
				sb.append("\n");
			}
			
			System.out.print(sb.toString());
					
		} catch(SQLException se) {
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
}
