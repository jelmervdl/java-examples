import java.util.*;

class ObserverExample
{
	class MyModel extends Observable
	{
		private String value;

		public void setValue(String value)
		{
			this.value = value;

			setChanged();
			notifyObservers();
		}

		public String getValue()
		{
			return value;
		}
	}

	class MyFirstView implements Observer
	{
		public void update(Observable source, Object arg)
		{
			MyModel model = (MyModel) source;
			System.out.println("MyFirstView: " + model.getValue());
		}
	}

	class MySecondView implements Observer
	{
		private MyModel model;

		public MySecondView(MyModel model)
		{
			this.model = model;
			model.addObserver(this);
		}

		public void update(Observable source, Object arg)
		{
			System.out.println("MySecondView: " + model.getValue());
		}
	}

	public ObserverExample()
	{
		MyModel model = new MyModel();

		// Method 1: a view that updates based on the source of the notification
		MyFirstView first = new MyFirstView();
		model.addObserver(first);

		// Method 2: a view that knows the model, and subscribes itself to
		// redraw based on that model when any update is received.
		MySecondView second = new MySecondView(model);

		// cause a change -> notification
		model.setValue("Changed");
	}

	static public void main(String[] args)
	{
		new ObserverExample();
	}
}