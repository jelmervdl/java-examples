class MethodChainingExample
{
	// First, I define a few classes that all use each other

	class Battery
	{
		private int voltage = 40;

		public int getVoltage()
		{
			return voltage;
		}
	}

	class Engine
	{
		private Battery battery = new Battery();

		public Battery getBattery()
		{
			return battery;
		}
	}

	class Car
	{
		private Engine engine = new Engine();

		public Engine getEngine()
		{
			return engine;
		}
	}

	class RichMan
	{
		private Car car = new Car();

		public Car getCar()
		{
			return car;
		}
	}

	public MethodChainingExample()
	{
		RichMan bill = new RichMan();

		// I want to know the voltage of the car bill owns:

		// Written in steps:
		Car car = bill.getCar();
		Engine engine = car.getEngine();
		Battery battery = engine.getBattery();
		int voltage = battery.getVoltage();
		System.out.println(voltage);

		// Chained
		System.out.println(bill.getCar().getEngine().getBattery().getVoltage());
	}

	static public void main(String[] args)
	{
		new MethodChainingExample();
	}

}