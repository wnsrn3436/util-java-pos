import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ItemDel extends JFrame implements ActionListener
{
	private JPanel contentPane;
	private JTextField itemName;
	private JTextField itemPrice;
	private JTextField itemNum;
	private JTextField itemStock;
	
	public ItemDel()
	{
		setTitle("상품삭제");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 457, 250);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(4, 1, 0, 0));
		
		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_3.getLayout();
		flowLayout_2.setVgap(10);
		flowLayout_2.setHgap(10);
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panel.add(panel_3);
		
		JLabel lblNewLabel_2 = new JLabel("상품번호");
		panel_3.add(lblNewLabel_2);
		
		itemNum = new JTextField();
		panel_3.add(itemNum);
		itemNum.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setVgap(10);
		flowLayout.setHgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(panel_1);
		
		JLabel lblNewLabel = new JLabel("상품명    ");
		panel_1.add(lblNewLabel);
		
		itemName = new JTextField();
		panel_1.add(itemName);
		itemName.setColumns(20);
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
		flowLayout_1.setVgap(10);
		flowLayout_1.setHgap(10);
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel.add(panel_2);
		
		JLabel lblNewLabel_1 = new JLabel("가격");
		panel_2.add(lblNewLabel_1);
		
		itemPrice = new JTextField();
		panel_2.add(itemPrice);
		itemPrice.setColumns(20);
		
		JPanel panel_4 = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel_4.getLayout();
		flowLayout_3.setVgap(10);
		flowLayout_3.setHgap(10);
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		panel.add(panel_4);
		
		JLabel lblNewLabel_3 = new JLabel("재고");
		panel_4.add(lblNewLabel_3);
		
		itemStock = new JTextField();
		panel_4.add(itemStock);
		itemStock.setColumns(20);
		
		JPanel panel_6 = new JPanel();
		getContentPane().add(panel_6, BorderLayout.SOUTH);
		
		JButton btnCheck = new JButton("조회");
		btnCheck.addActionListener(this);
		panel_6.add(btnCheck);
		
		JButton btnDelete = new JButton("삭제");
		btnDelete.addActionListener(this);
		panel_6.add(btnDelete);
		
		JButton btnClose = new JButton("닫기");
		btnClose.addActionListener(this);
		panel_6.add(btnClose);
	}

	public void actionPerformed(ActionEvent ae)
	{
		String str=ae.getActionCommand();
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("JDBC-ODBC 드라이버를 정상적으로 로드함");
		}
		catch(ClassNotFoundException e)
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
			ResultSet result;
			if (str.equals("조회"))
			{
				result=dbSt.executeQuery("select * from item where itemNum='"+itemNum.getText()+"';");
				while (result.next())
				{
					itemName.setText(result.getString("itemName"));
					itemPrice.setText(result.getString("price"));
					itemStock.setText(result.getString("stock"));
				}
					
			}
			else if (str.equals("삭제"))
			{
				dbSt.executeUpdate("DELETE FROM item WHERE itemNum = '"+itemNum.getText()+"';");
				(new MessageDialog(this, "상품정보 수정", true, "삭제되었습니다")).setVisible(true);
				
				Vector temp = MainFrame.instance.selectItemAll();
				MainFrame.instance.model[MainFrame.ITEM_TABLE].setDataVector(temp, MainFrame.instance.itemTableCol);
			}
			else if (str.equals("닫기"))
			{
				dispose();
			}
			
			dbSt.close();
			con.close();
		}
		catch (SQLException e) 
		{
			MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
			md.setVisible(true);
		}
	}
}
