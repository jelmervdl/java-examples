import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DrawingExample extends JPanel
{
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		for (int x = 0; x < getWidth() / 10; ++x)
			for (int y = 0; y < getHeight() / 10; ++y)
				drawSquare(x, y, g);
	}

	private void drawSquare(int x, int y, Graphics g)
	{
		g.setColor(Color.RED);
		g.fillOval(x * 20 + 2, y * 20 + 2, 6, 6);

		g.setColor(Color.BLACK);
		g.drawRect(x * 20, y * 20, 10, 10);
	}

	static public void main(String[] args)
	{
		JFrame win = new JFrame();
		win.add(new DrawingExample());

		win.setSize(400, 400);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.setVisible(true);
	}
}
