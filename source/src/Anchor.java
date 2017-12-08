import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Anchor extends JPanel
{
	public JButton anchor = new JButton();
	
	private boolean isBodyClicked;
	private boolean isAnchorClicked;
	
	public Container parent;
	public Component view;
	public int anchorSize = 10;
	
	public Anchor(Container parent, Component view)
	{
		super();
		this.parent = parent;
		this.view = view;
		
		setLocation(view.getX() - anchorSize, view.getY() - anchorSize);
		setSize(view.getWidth() + (anchorSize * 2), view.getHeight() + (anchorSize * 2));
		setLayout(null);
		
		anchor.addMouseListener(new MouseAdapter() 
		{
			public void mousePressed(MouseEvent e) 
			{
				Anchor.this.isAnchorClicked = true;
				((JButton)e.getSource()).setBackground(new Color(255, 0, 0, 100));
			}
		});
		anchor.setBounds(0, 0, anchorSize, anchorSize);
		add(anchor);
		
		view.setLocation(anchorSize, anchorSize);
		add(view);
		
		JMenuItem menuDelete = new JMenuItem("삭제");
		menuDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Anchor.this.setVisible(false);
			}
		});
		
		final JPopupMenu popup = new JPopupMenu();
		popup.add(menuDelete);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (!Anchor.this.isBodyClicked) {
					Anchor.this.setBackground(new Color(0, 255, 0, 30));
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
				if (!Anchor.this.isBodyClicked) {
					Anchor.this.setBackground(null);
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					popup.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (!Anchor.this.isBodyClicked)
				{
					Anchor.this.isBodyClicked = true;
					Anchor.this.setBackground(new Color(255, 0, 0, 30));
				}
			}
		});
		
		parent.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (Anchor.this.isBodyClicked)
				{
					Anchor.this.isBodyClicked = false;
					Anchor.this.setBackground(null);
				}
				if (isAnchorClicked)
				{
					if (Anchor.this.getWidth() <= 0 || Anchor.this.getHeight() <= 0) {
						Anchor.this.setSize(Anchor.this.anchorSize * 2, Anchor.this.anchorSize * 2);
					}
					Anchor.this.isAnchorClicked = false;
					Anchor.this.anchor.setBackground(null);
				}
			}
		});
		
		parent.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (Anchor.this.isBodyClicked) {
					Anchor.this.setLocation(e.getX() + 10, e.getY() + 10);
				}
				if (Anchor.this.isAnchorClicked)
				{
					int margin = 10;
					Point deltaLocation = new Point(e.getX() - Anchor.this.getX() + margin, e.getY() - Anchor.this.getY() + margin);
					Anchor.this.setDeltaSize(-deltaLocation.x , -deltaLocation.y);
					Anchor.this.setLocation(e.getX() + margin, e.getY() + margin);
				}
			}
		});
	}
	
	public void setDeltaSize(int deltaWidth, int deltaHeight)
	{
		view.setSize(view.getWidth() + deltaWidth, view.getHeight() + deltaHeight);
		setSize(getWidth() + deltaWidth, getHeight() + deltaHeight);
	}
	
	@Override
	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		view.setSize(width - (anchorSize * 2), height - (anchorSize * 2));
	}
}