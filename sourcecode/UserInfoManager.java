import java.sql.Connection;
import java.util.Scanner;

////////////////////////////////
// UserInfoManager Class
//
// User �α����� ����ϰ�, User ������ ������ �ִ� Class �Դϴ�.
////////////////////////////////

public class UserInfoManager {
	
	private Connection conn;
	private String id;
	private String name;
	private boolean isStudent;
	
	public UserInfoManager(Connection conn) {
		this.conn = conn;
	}
	
	public void userLogin() { // userLogin�� ����ϴ� method
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Welcome");
		System.out.println();
		
		while (true) {
			System.out.println("Please sign in");
			System.out.print("ID     :");
	        id = scan.nextLine();
	        System.out.print("Name   :");
	        name = scan.nextLine();
        
			if (StudentDB.isStudent(id, name, conn)) { // Log-in user�� Student ���� üũ 
				isStudent = true;
				break;
			} else if(InstructorDB.isInstructor(id, name, conn)) { // Log-in user�� Instructor ���� üũ 
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
