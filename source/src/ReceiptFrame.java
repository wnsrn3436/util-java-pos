import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class ReceiptFrame extends JFrame
{
	public ReceiptFrame(OrderFrame orderFrame)
	{
		super();
		
		setTitle("영수증");
		setBounds(100, 100, 300, 480);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout(0, 0));
		
		Calendar calendar = Calendar.getInstance();
		
		/* 타이틀 */
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(Color.WHITE);
		pane.add(titlePanel, BorderLayout.NORTH);
		
		JLabel lblTitle = new JLabel("판매 영수증");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		titlePanel.add(lblTitle);
		
		/* 정보 패널 */
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(0, 1, 0, 0));
		pane.add(infoPanel, BorderLayout.CENTER);
		
		JTextPane txtInfo = new JTextPane();
		txtInfo.setEditable(false);
		txtInfo.setFont(new Font("바탕체", Font.PLAIN, 12));
		infoPanel.add(txtInfo);
		
		String orders = "";
		for (int i = 1; i < orderFrame.checkoutData.size(); i++) {
			orders += orderFrame.checkoutData.get(i).toString() + "\n";
		}
		
		txtInfo.setText("출력시간 : " + calendar.get(Calendar.YEAR) + "년 " + calendar.get(Calendar.MONTH)+ "월 " + calendar.get(Calendar.DAY_OF_MONTH)+ "일, " + String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + "시 " + calendar.get(Calendar.MINUTE) + "분 " + calendar.get(Calendar.SECOND) + "초"
			+ "\n" + "------------------------------------------------------------"
			+ "\n" + "POS ID : 1000"
			+ "\n" + "판매처 : 명지전문대학"
			+ "\n" + "대표자 : 아이유"
			+ "\n" + "TEL : 010-1234-5678"
			+ "\n" + "------------------------------------------------------------"
			+ "\n" + orderFrame.checkoutData.get(0).toString()
			+ "\n" + "------------------------------------------------------------"
			+ "\n" + orders
			+ "\n" + "------------------------------------------------------------"
			+ "\n" + "총액 : " + orderFrame.checkoutAmount + "원");
		
		/* 바코드 패널 */
		JPanel barcodePanel = new JPanel();
		barcodePanel.setLayout(new GridLayout(0, 1, 0, 0));
		pane.add(barcodePanel, BorderLayout.SOUTH);
		
		String code = String.format("%d", calendar.get(Calendar.MONTH) + 1)
				+ String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
				+ String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
				+ String.format("%02d", calendar.get(Calendar.MINUTE))
				+ String.format("%02d", calendar.get(Calendar.SECOND));
		
		try {
			Barcode barcode = new Barcode(0, 0, 100, 50);
			barcode.setCode(Integer.parseInt(code));
			barcodePanel.add(barcode);
		}
		catch(NumberFormatException e) { }
	}
}