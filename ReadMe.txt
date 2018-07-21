<컴파일 방법>

source코드가 있는 폴더에서,
$ javac *.java
$ java -cp ".;ojdbc8.jar" SchoolDBSystem

compile 환경 : Windows 10

<실행하기 전에>

ConnectionManager.Class 에서

private static final String ID = "";
private static final String PASSWORD = "";
private static final String CONNECTION_INFO = "jdbc:oracle:thin:@localhost:1521:orcl";

이부분을 자신의 DB에 맞게 설정해주세요.	