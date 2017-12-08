import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class OrderFrame extends JFrame
{
	public boolean checkoutState = false;
	public Vector<StringFactory> checkoutData = new Vector<StringFactory>();
	public int checkoutAmount = 0;
	public JLabel lblState = new JLabel("상태 : 계산 전");
	
	public Vector<StringFactory> itemsData = new Vector<StringFactory>();
	public JList<StringFactory> lstItems = new JList<StringFactory>();
	public JScrollPane sclItems = new JScrollPane(lstItems);
	
	public Vector<StringFactory> ordersData = new Vector<StringFactory>();
	public JList<StringFactory> lstOrders = new JList<StringFactory>();
	public JScrollPane sclOrders = new JScrollPane(lstOrders);
	
	public JLabel lblAmount = new JLabel("총액 : 0원");
	
	private PosTable posTable;
	
	public OrderFrame(PosTable posTable)
	{
		super();
		this.posTable = posTable;
		posTable.ordersData.forEach(dataFactory -> {
			String[] str = new String[dataFactory.data.length];
			for (int i = 0; i < str.length; i++) {
				str[i] = dataFactory.data[i];
			}
			ordersData.add(new StringFactory(str, 8));
		});
		
		setTitle("주문관리");
		setBounds(100, 100, 480, 480);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout(0, 0));
		
		/* 타이틀 패널 */
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new GridLayout(0, 3, 0, 0));
		pane.add(titlePanel, BorderLayout.NORTH);
		
		JLabel lblOrder = new JLabel("주문목록");
		lblOrder.setHorizontalAlignment(SwingConstants.CENTER);
		titlePanel.add(lblOrder);
		
		JLabel lblDivider = new JLabel("|");
		lblDivider.setHorizontalAlignment(SwingConstants.CENTER);
		titlePanel.add(lblDivider);
		
		JLabel lblItems = new JLabel("상품목록");
		lblItems.setHorizontalAlignment(SwingConstants.CENTER);
		titlePanel.add(lblItems);
		
		/* 주문목록 패널 */
		JPanel orderListPanel = new JPanel();
		orderListPanel.setLayout(new GridLayout(0, 1, 0, 0));
		pane.add(orderListPanel, BorderLayout.WEST);
		
		orderListPanel.add(sclOrders);
		
		lstOrders.setFont(new Font("바탕체", Font.PLAIN, 12));
		lstOrders.setListData(ordersData);
		lstOrders.setSelectedIndex(0);
		lstOrders.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				subOrders();
			}
		});
		
		/* 물품목록 패널 */
		JPanel itemsListPanel = new JPanel();
		itemsListPanel.setLayout(new GridLayout(0, 1, 0, 0));
		pane.add(itemsListPanel, BorderLayout.EAST);
		
		itemsListPanel.add(sclItems);
		
		itemsData.add(new StringFactory(new String[] { "번호", "품명", "가격", "재고" }, 8));
		lstItems.setFont(new Font("바탕체", Font.PLAIN, 12));
		lstItems.setListData(itemsData);
		lstItems.setSelectedIndex(0);
		lstItems.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				addOrders();
			}
		});
		
		/* 옵션 패널 */
		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new BorderLayout(0, 0));
		pane.add(optionPanel, BorderLayout.SOUTH);
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(0, 1, 0, 0));
		optionPanel.add(labelPanel, BorderLayout.NORTH);
		
		lblAmount.setText("총액 : " + posTable.amount + "원");
		labelPanel.add(lblAmount);
		
		lblState.setHorizontalAlignment(SwingConstants.CENTER);
		labelPanel.add(lblState);
		
		JPanel buttonPanel = new JPanel();
		optionPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnCheckout = new JButton("계산");
		btnCheckout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCheckout();
				
				OrderFrame.this.checkoutState = true;
				OrderFrame.this.checkoutAmount = posTable.amount;
				OrderFrame.this.lblState.setText("상태 : 계산 완료");
				
				posTable.amount = 0;
				posTable.lblAmount.setText("총액 : " + posTable.amount + "원");
				OrderFrame.this.lblAmount.setText("총액 : " + posTable.amount + "원");
				
				OrderFrame.this.checkoutData.clear();
				OrderFrame.this.checkoutData.add(ordersData.get(0));
				while (OrderFrame.this.ordersData.size() > 1)
				{
					OrderFrame.this.checkoutData.add(OrderFrame.this.ordersData.get(1));
					OrderFrame.this.ordersData.remove(1);
					posTable.ordersData.remove(1);
				}
				
				OrderFrame.this.lstOrders.setListData(OrderFrame.this.ordersData);
				posTable.lstOrders.setListData(posTable.ordersData);
			}
		});
		buttonPanel.add(btnCheckout);
		
		JButton btnReceipt = new JButton("영수증");
		btnReceipt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (OrderFrame.this.checkoutState)
				{
					ReceiptFrame receiptFrame = new ReceiptFrame(OrderFrame.this);
					receiptFrame.setVisible(true);
				}
				else {
					(new MessageDialog(OrderFrame.this, "주문관리", true, "먼저 계산을 해주세요.")).setVisible(true);
				}
			}
		});
		buttonPanel.add(btnReceipt);
		
		JButton btnClose = new JButton("닫기");
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OrderFrame.this.dispose();
			}
		});
		buttonPanel.add(btnClose);
		
		/* Etc */
		updateItems();
	}
	
	public void onCheckout()
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
			Connection con = DriverManager.getConnection(MainFrame.instance.dbServer, MainFrame.instance.dbID, MainFrame.instance.dbPW);
			System.out.println("DB 연결 완료.");
			Statement dbSt = con.createStatement();
			System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");
			
			PreparedStatement pstmt=con.prepareStatement("insert into pay (payDate,payMoney) values(?,?)");
			
			java.util.Date today= new java.util.Date();
			pstmt.setDate(1,new Date(today.getTime()));
			pstmt.setInt(2, posTable.amount);
			pstmt.executeUpdate();
			dbSt.close();
			con.close();
		}
		catch (SQLException e) {
			MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
			md.setVisible(true);
		}
	}
	
	public void updateItems()
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
			Connection con = DriverManager.getConnection(MainFrame.instance.dbServer, MainFrame.instance.dbID, MainFrame.instance.dbPW);
			System.out.println("DB 연결 완료.");
			Statement dbSt = con.createStatement();
			System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");
			ResultSet result=dbSt.executeQuery("select * from item");
			while(result.next())
			{
				itemsData.add(new StringFactory(new String[] { result.getString("itemNum"), result.getString("itemName"), result.getString("price"), result.getString("stock") }, 8));
			}
			dbSt.close();
			con.close();
			lstItems.setListData(itemsData);
		}
		catch (SQLException e) 
		{
			MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
			md.setVisible(true);
		}
	}
	
	public void addOrders()
	{
		if (lstItems.getSelectedIndex() > 0)
		{
			StringFactory itemFactory = lstItems.getSelectedValue();
			StringFactory orderFactory = null;
			
			try {
				int itemNum = Integer.parseInt(itemFactory.data[0]);
				String itemName = itemFactory.data[1];
				int itemPrice = Integer.parseInt(itemFactory.data[2]);
				int itemStock = Integer.parseInt(itemFactory.data[3]);
				
				if (itemStock > 0)
				{
					int orderItemNum = itemNum;
					String orderItemName = itemName;
					int orderItemPrice = itemPrice;
					int orderItemStock = 1;
					int orderItemSum = itemPrice;
					
					int ordersIndex = getFactorysDataAt(ordersData, 0, String.valueOf(itemNum));
					if (ordersIndex >= 0)
					{
						orderFactory = ordersData.get(ordersIndex);
						orderItemStock = Integer.parseInt(orderFactory.data[3]) + 1;
						orderFactory.data[3] = String.valueOf(orderItemStock);
						orderFactory.data[4] = String.valueOf(orderItemStock * orderItemPrice);
					}
					else {
						ordersData.add(new StringFactory(new String[] { String.valueOf(orderItemNum), orderItemName, String.valueOf(orderItemPrice), String.valueOf(orderItemStock), String.valueOf(orderItemSum) }, 8));
					}
					
					lstOrders.setListData(ordersData);
					lstOrders.setSelectedIndex(0);
					itemFactory.data[3] = String.valueOf(--itemStock);
					
					onItemDataChanged(itemFactory);
					onOrderDataChanged(orderFactory);
				}
			}
			catch (NumberFormatException e) { }
			lstItems.setSelectedIndex(0);
		}
	}
	
	public void subOrders()
	{
		if (lstOrders.getSelectedIndex() > 0)
		{
			StringFactory orderFactory = lstOrders.getSelectedValue();
			StringFactory itemFactory = null;
			
			try {
				int orderItemNum = Integer.parseInt(orderFactory.data[0]);
				int orderItemPrice = Integer.parseInt(orderFactory.data[2]);
				int orderItemStock = Integer.parseInt(orderFactory.data[3]);
				
				int itemsIndex = getFactorysDataAt(itemsData, 0, String.valueOf(orderItemNum));
				if (itemsIndex >= 0)
				{
					itemFactory = itemsData.get(itemsIndex);
					int itemStock = Integer.parseInt(itemFactory.data[3]);
					itemFactory.data[3] = String.valueOf(++itemStock);
				}
				
				orderFactory.data[3] = String.valueOf(--orderItemStock);
				if (orderItemStock <= 0) {
					ordersData.remove(lstOrders.getSelectedValue());
				}
				else {
					orderFactory.data[4] = String.valueOf(orderItemStock * orderItemPrice);
				}
				
				lstItems.setListData(itemsData);
				lstItems.setSelectedIndex(0);
				
				lstOrders.setListData(ordersData);
				
				onItemDataChanged(itemFactory);
				onOrderDataChanged(orderFactory);
			}
			catch (NumberFormatException e) { }
			lstOrders.setSelectedIndex(0);
		}
	}
	
	public int getFactorysDataAt(Vector<StringFactory> factorys, int index, String value)
	{
		for (int i = 0; i < factorys.size(); i++)
		{
			if (factorys.get(i).data[index].equals(value)) {
				return i;
			}
		}
		return -1;
	}
	
	public void onItemDataChanged(StringFactory itemFactory)
	{
		if (itemFactory != null)
		{
			int itemNum = Integer.parseInt(itemFactory.data[0]);
			String itemName = itemFactory.data[1];
			int itemPrice = Integer.parseInt(itemFactory.data[2]);
			int itemStock = Integer.parseInt(itemFactory.data[3]);
			
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
				dbSt.executeUpdate("update item set stock="+itemStock+" where itemNum ="+ itemNum +";");
				
				Vector temp = MainFrame.instance.selectItemAll();
				MainFrame.instance.model[MainFrame.ITEM_TABLE].setDataVector(temp, MainFrame.instance.itemTableCol);
			}
			catch (SQLException e)
			{
				MessageDialog md = new MessageDialog(this, "DB 오류", true, "SQLException : " + e.getMessage());
				md.setVisible(true);
			}
		}
	}
	
	public void onOrderDataChanged(StringFactory orderFactory)
	{
		if (orderFactory != null)
		{
			int orderItemNum, orderItemPrice, orderItemStock, orderItemSum;
			String orderName;
			
			try {
				orderItemNum = Integer.parseInt(orderFactory.data[0]);
				orderName = orderFactory.data[1];
				orderItemPrice = Integer.parseInt(orderFactory.data[2]);
				orderItemStock = Integer.parseInt(orderFactory.data[3]);
				orderItemSum = Integer.parseInt(orderFactory.data[4]);
			}
			catch (NumberFormatException e) { }
		}
		
		posTable.amount = 0;
		ordersData.forEach(dataFactory -> {
			try { posTable.amount += Integer.parseInt(dataFactory.data[4]); }
			catch (NumberFormatException e) { }
		});
		posTable.lblAmount.setText("총액 : " + posTable.amount + "원");
		lblAmount.setText("총액 : " + posTable.amount + "원");
		
		posTable.ordersData.clear();
		
		ordersData.forEach(dataFactory -> {
			String[] str = new String[dataFactory.data.length];
			for (int i = 0; i < str.length; i++) {
				str[i] = dataFactory.data[i];
			}
			StringFactory newFactory = new StringFactory(str, 8);
			newFactory.dataVisible[0] = false;
			newFactory.dataVisible[2] = false;
			newFactory.dataVisible[4] = false;
			posTable.ordersData.add(newFactory);
		});
		
		posTable.lstOrders.setListData(posTable.ordersData);
		
		checkoutState = false;
		lblState.setText("상태 : 계산 전");
	}
}