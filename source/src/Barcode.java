import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

public class Barcode extends Canvas
{
	private int code;
	public Color backColor = Color.white;
	
	public Barcode(int x, int y, int w, int h)
	{
		super();
		
		setLocation(x, y);
		setSize(w, h);
		setBackground(backColor);
	}
	
	public void setCode(int code)
	{
		this.code = code;
	}
	
	@Override
	public void paint(Graphics g)
	{
		String binaryString = getBinaryNumber(code);
		binaryString += "01";
		
		float barWidth = (float)getWidth() / binaryString.length();
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(barWidth));
		
		for (int i = binaryString.length() - 1, j = 0; i >= 0; i--, j++)
		{
			if (binaryString.charAt(i) == '1')
			{
				g2d.draw(new Line2D.Float(barWidth * j, 0, barWidth * j, getHeight()));
			}
		}
	}
	
	private String getBinaryNumber(int number)
	{
		int mask = 1;
		String binaryString = "";
		
		for (int i = 0; i < Integer.SIZE; i++)
		{
			binaryString = (((number & mask) > 0) ? "1" : "0") + binaryString;
			mask <<= 1;
		}
		
		return binaryString;
	}
}