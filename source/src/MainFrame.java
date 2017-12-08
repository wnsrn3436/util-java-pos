import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.sql.*;
import java.text.*;
import java.util.*;

public class MainFrame extends JFrame implements ChangeListener
{
	public static final int ITEM_TABLE = 0;
	public static final int MEMBER_TABLE = 1;
	public static final int SALES_TABLE = 2;
	
	public static void main(String[] args)
	{
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
	}
	
	public static MainFrame instance;
	
	public String dbServer = "jdbc:mysql://localhost:3306/pos";
	public String dbID = "root";
	public String dbPW = "1234";
	
	public JTable itemTable;
	public JTable memberTable;
	public JTabbedPane tabbedPane;
	public JScrollPane scrollPane_1;
	
	public JPanel posTablePanel;
	public Point clickedLocation;
	public ArrayList<Anchor> posTables = new ArrayList<Anchor>();
	
	public DefaultTableModel[] model = new DefaultTableModel[3];
	
	public Vector<String> itemTableCol=new Vector<String>();
	public Vector<String> memberTableCol=new Vector<String>();
	public Vector<String> salesTableCol=new Vector<String>();
	public Graph graph;
	public JTable salesTable;
	
	public MainFrame()
	{
		instance = this;
		
		setTitle("POS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1024, 650);
		getContentPane().setLayout(null);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 1000, 600);
		tabbedPane.addChangeListener(this);
		
		getContentPane().add(tabbedPane);
		
		itemTableCol.add("상품번호");
		itemTableCol.add("상품명");
		itemTableCol.add("가격");
		itemTableCol.add("재고"); 
		
		memberTableCol.add("회원번호");
		memberTableCol.add("이름");
		memberTableCol.add("전화번호");
		memberTableCol.add("현재 포인트");
		memberTableCol.add("등급");
		
		salesTableCol.add("날짜");
		salesTableCol.add("매출액");
		
		/* 주문관리 */
		JPanel orderPane = new JPanel();
		orderPane.setLayout(new BorderLayout(0, 0));
		tabbedPane.addTab("주문관리", orderPane);
		
		posTablePanel = new JPanel();
		posTablePanel.setLayout(null);
		posTablePanel.setBorder(new LineBorder(Color.BLACK));
		orderPane.add(posTablePanel, BorderLayout.CENTER);
		
		JPanel posOptionPanel = new JPanel();
		orderPane.add(posOptionPanel, BorderLayout.SOUTH);
		
		JButton posTableUpdate = new JButton("테이블저장");
		posTableUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.updatePosTable();
				(new MessageDialog(MainFrame.this, "테이블관리", true, "저장되었습니다!")).setVisible(true);
			}
		});
		posOptionPanel.add(posTableUpdate);
		
		JMenuItem menuNewTable = new JMenuItem("좌석추가");
		menuNewTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PosTable posTable = new PosTable(MainFrame.this.clickedLocation.x, MainFrame.this.clickedLocation.y, 100, 200);
				Anchor anchor = new Anchor(MainFrame.this.posTablePanel, posTable);
				MainFrame.this.posTables.add(anchor);
				MainFrame.this.posTablePanel.add(anchor);
			}
		});
		
		final JPopupMenu posPopup = new JPopupMenu();
		posPopup.add(menuNewTable);
		
		posTablePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 3)
				{
					clickedLocation = e.getPoint();
					posPopup.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
		
		/* 상품관리 */
		JPanel itemPane = new JPanel();
		itemPane.setLayout(new BorderLayout(0, 0));
		tabbedPane.addTab("상품관리", null, itemPane, null);
		
		Vector<Object> tempRow1=new Vector<Object>();
		model[MainFrame.ITEM_TABLE] = new DefaultTableModel(tempRow1, itemTableCol);
		
		itemTable = new JTable(model[MainFrame.ITEM_TABLE]);
		itemTable.setEnabled(false);
		
		JScrollPane scrollPane = new JScrollPane(itemTable);
		itemPane.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		itemPane.add(panel_1, BorderLayout.SOUTH);
		
		ItemButtonEvent itemEvent = new ItemButtonEvent();
		
		JButton ItemAddButton = new JButton("상품등록");
		ItemAddButton.addActionListener(itemEvent);
		panel_1.add(ItemAddButton);
		
		JButton itemModifyButton = new JButton("상품수정");
		itemModifyButton.addActionListener(itemEvent);
		panel_1.add(itemModifyButton);
		
		JButton itemDeleteButton = new JButton("상품삭제");
		itemDeleteButton.addActionListener(itemEvent);
		panel_1.add(itemDeleteButton);
		
		/* 회원관리 */
		JPanel memberPane = new JPanel();
		memberPane.setLayout(new BorderLayout(0, 0));
		tabbedPane.addTab("회원관리", null, memberPane, null);
		
		Vector<Object> tempRow2=new Vector<Object>();
		model[MainFrame.MEMBER_TABLE] = new DefaultTableModel(tempRow2, memberTableCol);
		
		memberTable = new JTable(model[MainFrame.MEMBER_TABLE]);
		memberTable.setEnabled(false);
		
		scrollPane_1 = new JScrollPane(memberTable);
		memberPane.add(scrollPane_1, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		memberPane.add(panel, BorderLayout.SOUTH);
		
		MemberButtonEvent memberBtnEvent = new MemberButtonEvent();
		
		JButton memberAddButton = new JButton("회원등록");
		memberAddButton.addActionListener(memberBtnEvent);
		panel.add(memberAddButton);
		
		JButton memberModifyButton = new JButton("회원수정");
		memberModifyButton.addActionListener(memberBtnEvent);
		panel.add(memberModifyButton);
		
		JButton memberDeleteButton = new JButton("회원삭제");
		memberDeleteButton.addActionListener(memberBtnEvent);
		panel.add(memberDeleteButton);
		
		/* 매출관리 */
		JPanel salesPanel = new JPanel();
		salesPanel.setLayout(new BorderLayout(0, 0));
		tabbedPane.addTab("매출관리", null, salesPanel, null);
		
		Vector<Object> tempRow3=new Vector<Object>();
		model[MainFrame.SALES_TABLE]=new DefaultTableModel(tempRow3,salesTableCol);
		
		salesTable = new JTable(model[MainFrame.SALES_TABLE]);
		salesTable.setEnabled(false);
		
		JScrollPane scrollPane_2 = new JScrollPane(salesTable);
		salesPanel.add(scrollPane_2, BorderLayout.WEST);
		
		JPanel salesGraphPanel = new JPanel();
		salesGraphPanel.setLayout(new BorderLayout(0, 0));
		salesPanel.add(salesGraphPanel, BorderLayout.CENTER);
		
		JPanel salesGraphWrap = new JPanel();
		salesGraphWrap.setLayout(null);
		salesGraphPanel.add(salesGraphWrap, BorderLayout.CENTER);
		
		setVisible(true);
		graph = new Graph(30, 30, salesGraphWrap.getWidth() - 60, salesGraphWrap.getHeight() - 60);
		Anchor anchor = new Anchor(salesGraphWrap, graph);
		salesGraphWrap.add(anchor);
		
		JPanel salesGraphOption = new JPanel();
		salesGraphPanel.add(salesGraphOption, BorderLayout.SOUTH);
		
		salesGraphOption.add(new JButton("test"));
		
		/* Etc */
		tableSet();
		setPosTable();
	}
	
	public void tableSet()
	{
		tableRead(MainFrame.ITEM_TABLE);
		tableRead(MainFrame.MEMBER_TABLE);
		tableRead(MainFrame.SALES_TABLE);
	}
	
	public void tableRead(int key)
	{
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
			Connection con = DriverManager.getConnection(dbServer, dbID, dbPW);
			System.out.println("DB 연결 완료."); 
			
			Statement dbSt = con.createStatement();
			System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");
			
			if (key == MainFrame.MEMBER_TABLE)
			{
				ResultSet result = dbSt.executeQuery("select * from member;");
				while(result.next())
				{
					String num = result.getString("memberNum");
					String name = result.getString("name");
					String tel = result.getString("tel");
					String point = result.getString("nowPoint");
					String grade = result.getString("grade");
					Object[] data = {num, name, tel, point, grade};
					model[MainFrame.MEMBER_TABLE].addRow(data);
      			}
			}
			else if (key == MainFrame.ITEM_TABLE) 
			{
				ResultSet result = dbSt.executeQuery("select * from item;");
				while (result.next())
				{
					String num = result.getString("itemNum");
					String name = result.getString("itemName");
					String price = result.getString("price");
					String stock = result.getString("stock");
					Object[] data = {num, name, price, stock};
					model[MainFrame.ITEM_TABLE].addRow(data);
				}
			}
			else if(key==MainFrame.SALES_TABLE)
			{
				ResultSet result = dbSt.executeQuery("select * from sales;");
				while (result.next())
				{
					String date= result.getString("date");
					String sales = result.getString("sales");
					
					Object[] data = {date, sales};
					model[MainFrame.SALES_TABLE].addRow(data);
				}
			}
			
			con.close();
			dbSt.close();
		}
		catch (SQLException e)
		{
			MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
			md.setVisible(true);
		}
	}
	
	public Vector selectMemberAll()
	{
		Vector data=new Vector();
		
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
			Connection con = DriverManager.getConnection(dbServer, dbID, dbPW);
			System.out.println("DB 연결 완료."); 
			
			Statement dbSt = con.createStatement();
			System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");

			ResultSet result = dbSt.executeQuery("select * from member;");
			while(result.next())
			{
				String num = result.getString("memberNum");
				String name = result.getString("name");
				String tel = result.getString("tel");
				String point = result.getString("nowPoint");
				String grade = result.getString("grade");
				Vector<String>temp =new Vector<String>();
				temp.add(num);
				temp.add(name);
				temp.add(tel);
				temp.add(point);
				temp.add(grade);
				data.add(temp);
  			}
			
			con.close();
			dbSt.close();
		}
		catch (SQLException e)
		{
			MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
			md.setVisible(true);
		}
		
		return data;
	}
	
	public Vector selectItemAll()
	{
		Vector data=new Vector();
		
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
			Connection con = DriverManager.getConnection(dbServer, dbID, dbPW);
			System.out.println("DB 연결 완료."); 
			
			Statement dbSt = con.createStatement();
			System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");

			ResultSet result = dbSt.executeQuery("select * from item;");
			while(result.next())
			{
				String num = result.getString("itemNum");
				String name = result.getString("itemName");
				String price = result.getString("price");
				String stock = result.getString("stock");
				Vector<String>temp =new Vector<String>();
				temp.add(num);
				temp.add(name);
				temp.add(price);
				temp.add(stock);
				data.add(temp);
  			}
			
			con.close();
			dbSt.close();
		}
		catch (SQLException e)
		{
			MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
			md.setVisible(true);
		}
		
		return data;
	}

	public Vector<Vector<String>> selectSalesAll()
	{
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		
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
			Connection con = DriverManager.getConnection(dbServer, dbID, dbPW);
			System.out.println("DB 연결 완료."); 
			
			Statement dbSt = con.createStatement();
			System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");

			ResultSet result = dbSt.executeQuery("select * from sales;");
			while(result.next())
			{
				Vector<String>temp =new Vector<String>();
				temp.add(result.getString("date"));
				temp.add(result.getString("sales"));
				
				data.add(temp);
  			}
			con.close();
			dbSt.close();
		}
		catch (SQLException e)
		{
			MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
			md.setVisible(true);
		}
		
		return data;
	}
	
	public void updatePosTable()
	{
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
			Connection con = DriverManager.getConnection(dbServer, dbID, dbPW);
			System.out.println("DB 연결 완료.");
			Statement dbSt = con.createStatement();
			System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");
			dbSt.executeUpdate("delete from table_info;");
			PreparedStatement pstmt=con.prepareStatement("insert into table_info(width,height,posX,posY) values(?,?,?,?)");
			for (Anchor anchor : posTables)
			{
				if (anchor.isVisible())
				{
					PosTable posTable = (PosTable) anchor.view;
					pstmt.setString(1,Integer.toString(posTable.getWidth()));
					pstmt.setString(2,Integer.toString(posTable.getHeight()));
					pstmt.setString(3,Integer.toString(anchor.getX() + anchor.anchorSize));
					pstmt.setString(4,Integer.toString(anchor.getY() + anchor.anchorSize));
					pstmt.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
			md.setVisible(true);
		}
	}
	
	public void setPosTable()
	{
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
			Connection con = DriverManager.getConnection(dbServer, dbID, dbPW);
			System.out.println("DB 연결 완료.");
			Statement dbSt = con.createStatement();
			System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");
			
			ResultSet rs = dbSt.executeQuery("select * from table_info;");
			
			while(rs.next())
			{
				int width = rs.getInt("width");
				int height = rs.getInt("height");
				int posX = rs.getInt("posX");
				int posY = rs.getInt("posY");
				
				PosTable posTable = new PosTable(posX, posY, width, height);
				Anchor anchor = new Anchor(MainFrame.this.posTablePanel, posTable);
				MainFrame.this.posTables.add(anchor);
				MainFrame.this.posTablePanel.add(anchor);
			}
		}
		catch (SQLException e)
		{
			MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
			md.setVisible(true);
		}
		
	}

	public void stateChanged(ChangeEvent e)
	{
		int i = tabbedPane.getSelectedIndex();
		if (i == 3) 
		{
			Vector<Vector<String>> salesVector = selectSalesAll();
			model[MainFrame.SALES_TABLE].setDataVector(salesVector, salesTableCol);
			
			graph.setHString("날짜", "월");
			graph.setVString("매출", "원");
			
			GraphData[] graphDatas = new GraphData[salesVector.size()];
			for (int j = 0; j < salesVector.size(); j++)
			{
				Vector<String> pairVector = salesVector.get(j);
				graphDatas[j] = new GraphData(Integer.parseInt(pairVector.get(0).replaceAll("-", "")), Integer.parseInt(pairVector.get(1)));
			}
			graph.setGraphDatas(graphDatas);
		}
	}
}

class MemberButtonEvent implements ActionListener
{
	public void actionPerformed(ActionEvent ae)
	{
		String btnName = ae.getActionCommand();
		if (btnName.equals("회원등록"))
		{
			MemberAdd mAdd = new MemberAdd();
			mAdd.setVisible(true);
		} 
		else if (btnName.equals("회원수정")) 
		{
			
			MemberModify mMod=new MemberModify();
			mMod.setVisible(true);
		}
		else
		{
			MemberDel mDel=new MemberDel();
			mDel.setVisible(true);
		}
	}
}

class ItemButtonEvent implements ActionListener
{
	public void actionPerformed(ActionEvent ae)
	{
		String btnName = ae.getActionCommand();
		if (btnName.equals("상품등록"))
		{
			ItemAdd iAdd = new ItemAdd();
			iAdd.setVisible(true);
		}
		else if (btnName.equals("상품수정")) 
		{
			ItemModify iMod=new ItemModify();
			iMod.setVisible(true);
		}
		else 
		{
			ItemDel idel=new ItemDel();
			idel.setVisible(true);
		}
	}
}