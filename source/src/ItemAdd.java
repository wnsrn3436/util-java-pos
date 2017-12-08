import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.sql.*;

public class ItemAdd extends JFrame implements ActionListener
{
	private JPanel contentPane;
	private JTextField itemName;
	private JTextField itemPrice;
	private JTextField itemStock;
	
	public ItemAdd()
	{
		setTitle("상품등록");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 511, 200);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1, 0, 0));
		contentPane.add(panel, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		panel.add(panel_1);
		
		JLabel lblNewLabel = new JLabel("상품명");
		panel_1.add(lblNewLabel);
		
		itemName = new JTextField();
		itemName.setColumns(30);
		panel_1.add(itemName);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		panel.add(panel_2);
		
		JLabel lblNewLabel_1 = new JLabel("가격");
		panel_2.add(lblNewLabel_1);
		
		itemPrice = new JTextField();
		itemPrice.setColumns(30);
		panel_2.add(itemPrice);
		
		JPanel panel_3 = new JPanel();
		panel_3.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		panel.add(panel_3);
		
		JLabel lblNewLabel_2 = new JLabel("재고");
		panel_3.add(lblNewLabel_2);
		
		itemStock = new JTextField();
		itemStock.setColumns(30);
		panel_3.add(itemStock);
		
		JPanel panel_4 = new JPanel();
		contentPane.add(panel_4, BorderLayout.SOUTH);
		
		JPanel panel_5 = new JPanel();
		panel_4.add(panel_5);
		
		JButton itemAddBtn= new JButton("등록");
		itemAddBtn.addActionListener(this);
		panel_5.add(itemAddBtn);
		
		JButton cancelBtn = new JButton("닫기");
		cancelBtn.addActionListener(this);
		panel_4.add(cancelBtn);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		String str = ae.getActionCommand();
		if (str.equals("등록"))
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
				
				dbSt.executeUpdate("INSERT INTO item (itemName,price,stock) VALUES ('"
						+ itemName.getText() + "','" + itemPrice.getText() + "',"
						+ itemStock.getText()+ ");"
				);
				System.out.println("데이터 삽입 완료");
				
				ResultSet result = dbSt.executeQuery("select * from item where itemName='" + itemName.getText() + "';");
				while (result.next())
				{
					String num = result.getString("itemNum");
					String name = result.getString("itemName");
					String price = result.getString("price");
					String stock = result.getString("stock");
					Object[] data = {num, name, price, stock};
					MainFrame.instance.model[MainFrame.ITEM_TABLE].addRow(data);
				}
				
				
				dbSt.close();       
				con.close();
			}
			catch (SQLException e) 
			{
				MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
				md.setVisible(true);
			}
			
			itemName.setText("");
			itemPrice.setText("");
			itemStock.setText("");
		}
		else {
			dispose();
		}
	}
}