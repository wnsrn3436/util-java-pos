import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Graph extends JPanel
{
	public Canvas cvsHorizontal = new Canvas() {
		@Override
		public void paint(Graphics g) {
			Font font = g.getFont();
			g.setFont(new Font(font.getFontName(), font.getStyle(), 10));
			
			Point point = new Point(getWidth() - (font.getSize() * (Graph.this.hString.length() + 1)), getHeight() / 2);
			g.drawLine(0, point.y, point.x, point.y);
			g.drawString(Graph.this.hString, point.x + font.getSize(), point.y + (font.getSize() / 3));
			g.drawString(">", point.x, point.y + (font.getSize() / 3));
		}
	};
	
	public Canvas cvsVertical = new Canvas() {
		@Override
		public void paint(Graphics g) {
			Font font = g.getFont();
			g.setFont(new Font(font.getFontName(), font.getStyle(), 10));
			
			Point point = new Point(getWidth() / 2, getHeight());
			g.drawLine(point.x, font.getSize() * 2, point.x, point.y);
			g.drawString(Graph.this.vString, point.x - (font.getSize() / 3) - (font.getSize() * Graph.this.vString.length() / 3), font.getSize());
			g.drawString("∧", point.x - (font.getSize() / 3), font.getSize() * 2);
		}
	};
	
	public Canvas cvsGraphBody = new Canvas() {
		@Override
		public void paint(Graphics g) {
			if (graphDatas != null)
			{
				Font font = g.getFont();
				g.setFont(new Font(font.getFontName(), font.getStyle(), 10));
				
				GraphData[] data = Graph.this.getGraphDatas();
				
				int padding = 60;
				int hGap = (getWidth() - padding) / data.length;
				int rectSize = 3;
				
				Point preLocation = new Point(0, getHeight() - padding);
				for (int i = 0; i < data.length; i++)
				{
					double vGapNormal = 1.0d - (double)(data[i].getVData() - Graph.this.getGraphDatasVMin()) / (Graph.this.getGraphDatasVMax() - Graph.this.getGraphDatasVMin());
					Point crtLocation = new Point((i * hGap) + padding, (int)(vGapNormal * (getHeight() - (padding * 2)) + padding));
					
					g.setColor(Color.RED);
					g.drawRect(crtLocation.x, crtLocation.y, rectSize, rectSize);
					g.drawLine(preLocation.x + (rectSize / 2), preLocation.y + (rectSize / 2), crtLocation.x + (rectSize / 2), crtLocation.y + (rectSize / 2));
					
					g.setColor(Color.BLACK);
					g.drawString(data[i].getVData() + Graph.this.vHint, crtLocation.x, crtLocation.y - font.getSize());
					g.drawString(data[i].getHData() + Graph.this.hHint, crtLocation.x, getHeight() - font.getSize());
					
					preLocation = crtLocation;
				}
			}
		}
	};
	
	private String hString = "가로";
	private String vString = "세로";
	private String hHint = "H";
	private String vHint = "V";
	public int hHeight = 40;
	public int vWidth = 40;
	public int hvMargin = 30;
	
	public Color backColor = Color.white;
	private GraphData[] graphDatas;
	
	public Graph(int x, int y, int w, int h)
	{
		super();
		
		setLocation(x, y);
		setSize(w, h);
		setLayout(null);
		
		setBackground(backColor);
		cvsHorizontal.setBackground(backColor);
		cvsVertical.setBackground(backColor);
		cvsGraphBody.setBackground(backColor);
		
		add(cvsHorizontal);
		add(cvsVertical);
		add(cvsGraphBody);
	}
	
	@Override
	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		
		cvsHorizontal.setBounds(hvMargin, getHeight() - hHeight, getWidth() - hvMargin, hHeight);
		cvsVertical.setBounds(0, 0, vWidth, getHeight() - hvMargin);
		cvsGraphBody.setBounds(vWidth, 0, getWidth(), getHeight() - hHeight);
	}
	
	public void setHString(String str, String hint)
	{
		hString = str;
		hHint = hint;
	}
	
	public void setVString(String str, String hint)
	{
		vString = str;
		vHint = hint;
	}
	
	public void setGraphDatas(GraphData[] graphDatas)
	{
		this.graphDatas = graphDatas;
	}
	
	public GraphData[] getGraphDatas()
	{
		return graphDatas;
	}
	
	public int getGraphDatasVMax()
	{
		int result = graphDatas[0].getVData();
		for (GraphData data : graphDatas)
		{
			if (data.getVData() > result) {
				result = data.getVData();
			}
		}
		return result;
	}
	
	public int getGraphDatasVMin()
	{
		int result = graphDatas[0].getVData();
		for (GraphData data : graphDatas)
		{
			if (data.getVData() < result) {
				result = data.getVData();
			}
		}
		return result;
	}
}

class GraphData
{
	private int hData;
	private int vData;
	
	public GraphData(int hData, int vData)
	{
		this.hData = hData;
		this.vData = vData;
	}
	
	public int getHData()
	{
		return hData;
	}
	
	public int getVData()
	{
		return vData;
	}
}