import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.sql.*;

public class MemberAdd extends JFrame implements ActionListener
{
	private JPanel contentPane;
	private JTextField textFieldName;
	private JTextField textFieldTel;
	
	private final int STARTPOINT = 100;
	private int accPoint = STARTPOINT;
	private String grade = "일반";
	
	public MemberAdd() 
	{
		setTitle("회원등록");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 533, 200);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1, 0, 0));
		contentPane.add(panel, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		panel.add(panel_1);
		
		JLabel nameLabel = new JLabel("이름");
		panel_1.add(nameLabel);
		
		textFieldName = new JTextField();
		textFieldName.setColumns(30);
		panel_1.add(textFieldName);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		panel.add(panel_2);
		
		JLabel telLabel = new JLabel("전화번호");
		panel_2.add(telLabel);
		
		textFieldTel = new JTextField();
		textFieldTel.setColumns(30);
		panel_2.add(textFieldTel);
		
		JPanel panel_4 = new JPanel();
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		contentPane.add(panel_4, BorderLayout.SOUTH);
		
		JButton commitBtn = new JButton("등록");
		commitBtn.addActionListener(this);
		panel_4.add(commitBtn);
		
		JButton cancelBtn = new JButton("닫기");
		cancelBtn.addActionListener(this);
		panel_4.add(cancelBtn);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		String str = ae.getActionCommand();
		if(str.equals("등록"))
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				System.out.println("JDBC-ODBC 드라이버를 정상적으로 로드함");
			}
			catch (ClassNotFoundException e)
			{
				MessageDialog md = new MessageDialog(this, "DB 오류", true, "드라이버 로드에 실패했습니다.");
				md.setVisible(true);
			}
			
  			try
			{
  				Connection con = DriverManager.getConnection(MainFrame.instance.dbServer, MainFrame.instance.dbID, MainFrame.instance.dbPW);
				System.out.println("DB 연결 완료.");
				
				Statement dbSt = con.createStatement();
				System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");
				
				dbSt.executeUpdate("INSERT INTO member (name,tel,nowPoint,accPoint,grade) VALUES ('"
						+ textFieldName.getText() + "','" + textFieldTel.getText() + "',"
						+ STARTPOINT + "," + accPoint + ",'" + grade + "');"
				);
				System.out.println("데이터 삽입 완료");
				
				ResultSet result = dbSt.executeQuery("select * from member where name='" + textFieldName.getText() + "'AND tel='" + textFieldTel.getText() + "';");
				
				while (result.next())
				{
					String num = result.getString("memberNum");
					String name = result.getString("name");
					String tel = result.getString("tel");
					String point = result.getString("nowPoint");
					String grade = result.getString("grade");
					Object[] data = {num, name, tel, point, grade};
					MainFrame.instance.model[MainFrame.MEMBER_TABLE].addRow(data);
				}
				MainFrame.instance.model[MainFrame.MEMBER_TABLE].fireTableDataChanged();
				
				dbSt.close();
				con.close();
			}
			catch (SQLException e)
  			{
				MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
				md.setVisible(true);
			}
  			
			textFieldName.setText("");
			textFieldTel.setText("");
		}
		else {
			dispose();
		}
	}
}