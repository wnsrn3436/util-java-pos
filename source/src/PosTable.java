import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class PosTable extends JPanel
{
	public JButton btnOrderSetting = new JButton("주문관리");
	public JLabel lblAmount = new JLabel("총액 : 0원");
	public int amount = 0;
	
	public Vector<StringFactory> ordersData = new Vector<StringFactory>();
	public JList<StringFactory> lstOrders = new JList<StringFactory>();
	public JScrollPane sclOrders = new JScrollPane(lstOrders);
	
	public int topHeight = 20;
	public int bottomHeight = 20;
	
	public PosTable(int x, int y, int w, int h)
	{
		super();
		
		setLocation(x, y);
		setSize(w, h);
		setLayout(null);
		
		lblAmount.setBorder(new LineBorder(Color.BLACK));
		lblAmount.setHorizontalAlignment(SwingConstants.CENTER);
		
		btnOrderSetting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OrderFrame orderFrame = new OrderFrame(PosTable.this);
				orderFrame.setVisible(true);
			}
		});
		
		ordersData.add(new StringFactory(new String[] { "번호", "품명", "가격", "개수", "합계" }, 8));
		ordersData.get(0).dataVisible[0] = false;
		ordersData.get(0).dataVisible[2] = false;
		ordersData.get(0).dataVisible[4] = false;
		lstOrders.setListData(ordersData);
		lstOrders.setSelectedIndex(0);
		
		lstOrders.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lstOrders.setSelectedIndex(0);
			}
		});
		
		add(btnOrderSetting);
		add(sclOrders);
		add(lblAmount);
	}
	
	@Override
	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		
		btnOrderSetting.setSize(width, topHeight);
		sclOrders.setBounds(0, btnOrderSetting.getY() + btnOrderSetting.getHeight(), width, height - (topHeight + bottomHeight));
		lstOrders.setSize(sclOrders.getWidth(), sclOrders.getHeight());
		lblAmount.setBounds(0, sclOrders.getY() + sclOrders.getHeight(), width, bottomHeight);
	}
}