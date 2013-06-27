import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class DragBoundsExample
{
	static public Point getLocationInBounds(Point p, Rectangle object, Rectangle bounds)
	{
		return new Point(
			Math.min(Math.max(p.x, bounds.x), bounds.x + bounds.width - object.width),
			Math.min(Math.max(p.y, bounds.y), bounds.y + bounds.height - object.height));
	}

	static public void main(String[] args)
	{
		final Rectangle object = new Rectangle(90, 90, 50, 50);

		final Rectangle bounds = new Rectangle(50, 50, 260, 260);

		final JFrame win = new JFrame();

		final JPanel panel = new JPanel()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;
				
				g2.setColor(Color.RED);
				g2.draw(bounds);

				g2.setColor(Color.GREEN);
				g2.fill(object);
			}
		};

		panel.addMouseMotionListener(new MouseAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				object.setLocation(getLocationInBounds(e.getPoint(), object, bounds));
				
				panel.repaint();
			}
		});

		win.setSize(400, 420);
		win.add(panel);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.setVisible(true);
	}
}