import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GridExample extends JPanel
{
	protected class Site
	{
		private boolean accessible;

		private Point location;

		private Color color;

		public Site(Point location)
		{
			this.location = location;
			this.accessible = true;
			this.color = Color.WHITE;
		}

		/* Accessible */

		public boolean isAccessible()
		{
			return accessible;
		}

		public void setAccessible(boolean accessible)
		{
			this.accessible = accessible;
		}

		/* Color */

		public Color getColor()
		{
			return color;
		}

		public void setColor(Color color)
		{
			this.color = color;
		}

		/* Location */

		public Point getLocation()
		{
			return location;
		}
	}

	protected class Player
	{	
		private Color color;

		private Point location;

		public Player(Color color)
		{
			this.color = color;
			this.location = new Point(0, 0);
		}

		public Point getLocation()
		{
			return location;
		}

		public void setLocation(Point p)
		{
			location = p;
		}
		
		public Color getColor()
		{
			return color;
		}

		public void setColor(Color color)
		{
			this.color = color;
		}
	}

	protected class MoveController extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			Point current = player.getLocation();
			Point next = null;

			switch (e.getKeyCode())
			{
				case KeyEvent.VK_UP:
					next = new Point((int) current.getX(), (int) current.getY() - 1);
					break;

				case KeyEvent.VK_DOWN:
					next = new Point((int) current.getX(), (int) current.getY() + 1);
					break;

				case KeyEvent.VK_LEFT:
					next = new Point((int) current.getX() - 1, (int) current.getY());
					break;

				case KeyEvent.VK_RIGHT:
					next = new Point((int) current.getX() + 1, (int) current.getY());
					break;

				default:
					return;
			}

			if (isAccessible(next))
				player.setLocation(next);
			else if (getSite(next) != null)
				eatSite(getSite(next));

			repaint();
		}
	}

	private Site[][] grid;

	private Dimension size;

	private Player player;

	/* init code */

	public GridExample(Dimension size)
	{
		this.size = size;

		this.player = new Player(Color.BLACK);
		
		initGrid();

		setFocusable(true);
		addKeyListener(new MoveController());
	}

	private void initGrid()
	{
		grid = new Site[(int) size.getWidth()][(int) size.getHeight()];

		Random dice = new Random();

		for (int i = 0; i < size.getWidth(); ++i)
		{
			for (int j = 0; j < size.getHeight(); ++j)
			{
				grid[i][j] = new Site(new Point(i, j));

				if (dice.nextInt(5) == 0)
				{
					grid[i][j].setAccessible(false);
					grid[i][j].setColor(getRandomColor());
				}
			}
		}
	}

	private Color getRandomColor()
	{
		Random dice = new Random();

		return new Color(
			dice.nextFloat(),
			dice.nextFloat(),
			dice.nextFloat());
	}

	/* Game logic */

	public Site getSite(Point p)
	{
		// Don't walk out of the bounds
		if (p.getX() < 0 || p.getY() < 0)
			return null;

		if (p.getX() >= size.getWidth() || p.getY() >= size.getHeight())
			return null;

		// Test whether the grid allows to walk here.
		return grid[(int) p.getX()][(int) p.getY()];
	}

	public boolean isAccessible(Point p)
	{
		return getSite(p) != null
			&& getSite(p).isAccessible();// || isSameColor(getSite(p).getColor(), player.getColor()));
	}

	public void step()
	{
		for (int i = 0; i < size.getWidth(); ++i)
			for (int j = 0; j < size.getHeight(); ++j)
				if (!grid[i][j].isAccessible())
					grid[i][j].setColor(getRandomColor());

		repaint();
	}

	private void eatSite(Site site)
	{
		if (isSameColor(site.getColor(), player.getColor()))
		{
			player.setColor(site.getColor());
			site.setColor(Color.WHITE);
			site.setAccessible(true);
		}
	}

	private boolean isSameColor(Color x, Color y)
	{
		int r = x.getRed() - y.getRed();
		int g = x.getGreen() - y.getGreen();
		int b = x.getBlue() - y.getBlue();
		double d = Math.sqrt(r * r + g * g + b * b);

		return d < 200;
	}

	/* Drawing code */

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		for (int x = 0; x < size.getWidth(); ++x)
			for (int y = 0; y < size.getHeight(); ++y)
				paintSite(g, grid[x][y]);

		paintPlayer(g);
	}

	private void paintSite(Graphics g, Site site)
	{
		g.setColor(site.getColor());

		g.fillRect(
			(int) site.getLocation().getX() * 10,
			(int) site.getLocation().getY() * 10,
			10, 10);
	}

	private void paintPlayer(Graphics g)
	{
		g.setColor(player.getColor());
		g.fillOval(
			(int) player.getLocation().getX() * 10,
			(int) player.getLocation().getY() * 10,
			10, 10);
	}

	/* Main for testing */

	static public void main(String[] args)
	{
		JFrame win = new JFrame();

		GridExample game = new GridExample(new Dimension(100, 40));

		win.setTitle("Awesome");
		win.add(game);

		win.setSize(1000, 400);
		win.setVisible(true);

		//psychoLoop(game);
	}

	static private void psychoLoop(final GridExample game)
	{
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				game.step();
			}
		}, new Date(), 200);
	}
}
