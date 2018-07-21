import java.sql.Connection;
import java.util.Scanner;

////////////////////////////////
// UserInfoManager Class
//
// User 로그인을 담당하고, User 정보를 가지고 있는 Class 입니다.
////////////////////////////////

public class UserInfoManager {
	
	private Connection conn;
	private String id;
	private String name;
	private boolean isStudent;
	
	public UserInfoManager(Connection conn) {
		this.conn = conn;
	}
	
	public void userLogin() { // userLogin을 담당하는 method
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Welcome");
		System.out.println();
		
		while (true) {
			System.out.println("Please sign in");
			System.out.print("ID     :");
	        id = scan.nextLine();
	        System.out.print("Name   :");
	        name = scan.nextLine();
        
			if (StudentDB.isStudent(id, name, conn)) { // Log-in user가 Student 인지 체크 
				isStudent = true;
				break;
			} else if(InstructorDB.isInstructor(id, name, conn)) { // Log-in user가 Instructor 인지 체크 
				isStudent = false;
				break;
			} else {
				System.out.println("Wrong authentication.");
			} 
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isStudent() {
		return isStudent;
	}
}
