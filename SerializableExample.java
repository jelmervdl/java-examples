import java.util.*;
import java.io.*;

/**
 * This is an example of implementing an Observable model in which a change
 * to one of the child objects in the model still causes the model to notify
 * its observers.
 *
 * One possible way to implement this 'propagating of events' is by letting
 * each child be Observable, and letting the parent observe each child.
 * Unfortunately Observable is not serializable, and all the registered 
 * observers are lost when you read the hierarchy using
 * ObjectInputStream.readObject. Therefore, I implemented my own simple parent
 * -child notifications by basing everything inside the model on Entity. Each
 * Entity can have a parent and can notify this parent of changes. The parent
 * does not have to be an Entity itself (e.g. Model is a parent but not an
 * Entity) and the default behavior of an Entity when notified is to notify
 * its own parents.
 */
class SerializableExample implements Observer
{
	/**
	 * A child listener can be used as a parent for an Entity and
	 * is notified of changes in the children of which it is a parent.
	 */
	private interface ChildListener
	{
		public void childChanged(Entity child);
	}

	/**
	 * Base class for my complete house building class hierarchy.
	 */
	static private class Entity implements Serializable, ChildListener
	{
		private double price;

		private ChildListener parent;

		public Entity()
		{
			price = 0.0;
		}

		/* Observable hierarchy */

		public void setParent(ChildListener entity)
		{
			parent = entity;
		}

		protected void notifyParent()
		{
			if (parent != null)
				parent.childChanged(this);
		}

		public void childChanged(Entity enity)
		{
			// Default behavior is to notify our own parent
			notifyParent();
		}

		/* Price */

		public void setPrice(double price)
		{
			this.price = price;

			notifyParent();
		}

		public double getPrice()
		{
			return price;
		}
	}

	/**
	 * It's a house model! And it is not an entity, but it is observable using the Observer
	 * interface. It is also serializable, although it won't serialize all the classes
	 * that observe it.
	 */
	static private class Model extends Observable implements ChildListener
	{
		private House house;

		public Model()
		{
			house = new House();
			house.setParent(this);
		}

		public House getHouse()
		{
			return house;
		}

		@Override
		public String toString()
		{
			return "Model(" + getHouse() + " of total value " + house.getPrice() + ")";
		}

		/* ChildListener interface */

		@Override
		public void childChanged(Entity entity)
		{
			setChanged();
			notifyObservers();
		}

		/* Saving and loading of the house */

		public void write(OutputStream out) throws IOException
		{
			// Remove me as a parent so I won't be serialized
			house.setParent(null);

			ObjectOutputStream objout = new ObjectOutputStream(out);
			objout.writeObject(house);
			objout.flush();

			// Here I had to flush because I don't want to close the out
			// stream, but if the out stream is closed, objout won't be
			// notified and might not have written all its data.

			// Reinstate me as the parent
			house.setParent(this);
		}

		public void read(InputStream in) throws IOException, ClassNotFoundException
		{
			ObjectInputStream objin = new ObjectInputStream(in);
			
			// Release the current house completely
			house.setParent(null);

			// Read the new house
			house = (House) objin.readObject();

			// .. and make me the parent
			house.setParent(this);

			// (also notify our audience)
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * These are really all children of the Model
	 */
	static private class House extends Entity
	{
		private List<Floor> floors;

		public House()
		{
			floors = new ArrayList<Floor>();
		}

		public void addFloor(Floor floor)
		{
			floors.add(floor);
			floor.setParent(this);

			notifyParent();
		}

		public Floor getFloor(int level)
		{
			return floors.get(level);
		}

		@Override
		public double getPrice()
		{
			double sum = super.getPrice();

			for (Floor floor : floors)
				sum += floor.getPrice();

			return sum;
		}

		@Override
		public String toString()
		{
			return "House" + floors;
		}
	}

	static private class Floor extends Entity
	{
		private List<Room> rooms;

		public Floor()
		{
			rooms = new ArrayList<Room>();
		}

		public void addRoom(Room room)
		{
			rooms.add(room);
			room.setParent(this);

			notifyParent();
		}

		public Room getRoom(int index)
		{
			return rooms.get(index);
		}

		@Override
		public double getPrice()
		{
			double sum = super.getPrice();

			for (Room room : rooms)
				sum += room.getPrice();

			return sum;
		}

		@Override
		public String toString()
		{
			return "Floor" + rooms;
		}
	}

	static private class Room extends Entity
	{
		private String purpose;

		public Room(String purpose)
		{
			this.purpose = purpose;
		}

		public String getPurpose()
		{
			return purpose;
		}

		@Override
		public String toString()
		{
			return purpose;
		}
	}

	/* Demo code */

	Model model;

	public SerializableExample() throws IOException, ClassNotFoundException
	{
		model = new Model();
		model.addObserver(this);

		// Since we are observing model, all these actions of adding floors
		// and rooms should result in SerializableExample.update being called
		// with `house` as the source argument.

		System.out.println("Building a house:");
		
		House house = model.getHouse();
		
		Room kitchen = new Room("Kitchen");
		Room bedroom = new Room("Bedroom");

		Floor groundFloor = new Floor();
		house.addFloor(groundFloor);
		groundFloor.addRoom(kitchen);

		Floor firstFloor = new Floor();
		house.addFloor(firstFloor);
		firstFloor.addRoom(bedroom);

		System.out.println("Setting price of kitchen:");

		// This should eventually call SerializableExample.update since we are an
		// observer of House, and changing the kitchen changes the house.
		kitchen.setPrice(15.00);

		System.out.println("Writing house to house.dat:");

		OutputStream out = new FileOutputStream("house.dat");
		model.write(out);
		out.close();

		System.out.println("Adding a second room to the ground floor");
		groundFloor.addRoom(new Room("Living room"));

		System.out.println("Reading the old house from house.dat:");

		InputStream in = new FileInputStream("house.dat");
		model.read(in);
		in.close();

		System.out.println("Setting price of first room on first floor:");
		
		// Again, this should result in SerializableExample.update getting called, but this
		// time with the old house with only one room on the ground floor.
		model.getHouse().getFloor(1).getRoom(0).setPrice(100.00);

		// note that all the other variables, `groundFloor`, `kitchen` etc. still refer to
		// the house with three rooms and not to the one currently in the model. This can
		// get a bit confusing, but just use the model as staring point for every operation
		// and you should be safe.
	}

	public void update(Observable source, Object arg)
	{
		System.out.println("-> " + model);
	}

	static public void main(String[] args)
	{
		try {
			new SerializableExample();
		}
		catch (IOException e) {
			e.printStackTrace(System.err);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace(System.err);
		}
	}
}