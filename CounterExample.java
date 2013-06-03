import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

class CounterExample
{
	/**
	 * This is a limited counter, able to count from {@code min} to {@code max}.
	 * You can also query its state, whether it is able to continue incrementing
	 * or decrementing using {@link #canIncrement()} and {@link #canDecrement()}.
	 */
	class Counter extends Observable
	{
		private int min;
		private int max;
		private int value;

		public Counter(int min, int max)
		{
			this.min = min;
			this.max = max;
			this.value = 0;
		}

		public void increment()
		{
			if (canIncrement())
			{
				value++;
				setChanged();
			}

			notifyObservers();
		}

		public void decrement()
		{
			if (canDecrement())
			{
				value--;
				setChanged();
			}

			notifyObservers();
		}

		public int getValue()
		{
			return value;
		}

		public String getValueAsString()
		{
			return Integer.toString(getValue());
		}

		public boolean canIncrement()
		{
			return value < max;
		}

		public boolean canDecrement()
		{
			return value > min;
		}
	}

	/* Actions (Controller-ish code) */

	/**
	 * The actions subscribe themselves to the notifications of the counter.
	 * They can use these to update their state when the counter changes.
	 */
	abstract class AbstractCounterAction extends AbstractAction implements Observer
	{
		protected Counter counter;

		public AbstractCounterAction(String label, Counter counter)
		{
			super(label);

			// All our subclasses need the counter to apply
			// their actions.
			this.counter = counter;

			// Listen for update notifications which we can
			// use to update our is-enabled state.
			counter.addObserver(this);
		}
	}

	class IncrementAction extends AbstractCounterAction
	{
		public IncrementAction(String label, Counter counter)
		{
			super(label, counter);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			counter.increment();
		}

		@Override
		public void update(Observable source, Object arg)
		{
			setEnabled(counter.canIncrement());
		}
	}

	class DecrementAction extends AbstractCounterAction
	{
		public DecrementAction(String label, Counter counter)
		{
			super(label, counter);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			counter.decrement();
		}

		@Override
		public void update(Observable source, Object arg)
		{
			setEnabled(counter.canDecrement());
		}
	}

	class CounterDisplay extends JLabel implements Observer
	{
		// Just to display the two different methods you can use
		// to update your view based on your model changes, this
		// display does not know about the Counter itself. (Note
		// the lack of a member variable to remember Counter.) It
		// only receives updates and asks the source of those
		// updates for its value.

		public CounterDisplay(Counter counter)
		{
			setHorizontalAlignment(CENTER);
			counter.addObserver(this);

			// Also set the initial state of this display.
			setText(counter.getValueAsString());
		}

		@Override
		public void update(Observable source, Object arg)
		{
			// I know the source of the update-call is a Counter instance
			// since I subscribed myself to the counter passed to the
			// constructor.
			Counter counter = (Counter) source;
			setText(counter.getValueAsString());
		}
	}

	class CounterWindow extends JFrame
	{
		public CounterWindow()
		{
			setTitle("Counter");
			setSize(200, 200);
			setLayout(new BorderLayout());

			// Here I have my model: a counter
			Counter counter = new Counter(-5, 5);

			// Here I use a toolbar because toolbar actions listen to
			// the setEnabled-change of AbstractAction. Now, when the
			// model updates, the actions will change the enabled/disabled
			// state of the toolbar buttons because the actions add
			// themselves as observers to the counter. Every time the
			// counter changes, the actions update their isEnabled-state
			// causing the toolbar buttons to grey out when appropriate. 
			// Note that we also pass the counter object to the actions
			// for them to store them. When clicked, the actions can then
			// call the appropriate method of the counter model. 
			JToolBar toolBar = new JToolBar();
			toolBar.add(new IncrementAction("Increment", counter));
			toolBar.add(new DecrementAction("Decrement", counter));
			add(toolBar, BorderLayout.PAGE_START);

			// Finally, add a view for the counter. Again, since it adds
			// itself as an observer to the counter, it will be notified
			// of changes, and will take that opportunity to update itself.
			CounterDisplay display = new CounterDisplay(counter);
			add(display, BorderLayout.CENTER);
		}
	}

	/**
	 * CounterExample is more of an encapsulating class than a functional one.
	 * The constructor contains the test code which sets up a new window and
	 * shows it.
	 */
	public CounterExample()
	{
		CounterWindow win = new CounterWindow();
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.setVisible(true);
	}

	static public void main(String[] args)
	{
		new CounterExample();
	}
}