import java.sql.Connection;

////////////////////////////////
// SchoolDBSyestem Class
// main Class 입니다.
////////////////////////////////
public class SchoolDBSystem {
	public static void main(String[] args) {
		ConnectionManager c_manager= new ConnectionManager();
		Connection conn = c_manager.tryConnect();
	
		UserInfoManager i_manager = new UserInfoManager(conn);
		i_manager.userLogin();
		
		boolean isStudent = i_manager.isStudent();
		if (isStudent) { // user가 student
			StudentDB.studentMenu(i_manager, conn);
		}
		else { // user가 instructor
			InstructorDB.instructorMenu(i_manager, conn);
		}
	}
}
