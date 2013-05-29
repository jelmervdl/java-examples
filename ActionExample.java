import java.awt.event.*;
import javax.swing.*;

class ActionExample extends JFrame
{
	public ActionExample()
	{
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		fileMenu.add(new AbstractAction("Load...")
		{
			public void actionPerformed(ActionEvent e)
			{
				doSomething();
			}
		});
	}

	private void doSomething()
	{
		System.out.println("Hoi!");
	}  

	static public void main(String[] args)
	{
		ActionExample win = new ActionExample();
		win.setSize(400, 400);
		win.setVisible(true);
	}
}