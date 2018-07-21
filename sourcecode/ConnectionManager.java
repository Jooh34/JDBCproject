import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

////////////////////////////////
// ConnectionManager Class
// DB�� Connection�� �����ϴ� class�Դϴ�.
// �Ʒ� ID,PASSWORD,CONNECTION_INFO ����
// �ڽ��� DB�� �´�
// ID -> DB ���̵�
// PASSWORD -> DB PASSWORD
// CONNECTION_INFO -> ���Ӱ��� ����.
// �� �������ּ���.
////////////////////////////////

public class ConnectionManager {

	private static final String ID = "jooh";
	private static final String PASSWORD = "jooh";
	private static final String CONNECTION_INFO = "jdbc:oracle:thin:@localhost:1521:orcl";

	public Connection tryConnect() {
		System.out.println("-------- Oracle JDBC Connection Testing ------");

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return null;

        }

        System.out.println("Oracle JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    CONNECTION_INFO,ID,PASSWORD);


        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return null;

        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }

        return connection;
	}
}
