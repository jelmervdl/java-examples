Java Examples
=============

Here are a bunch of examples that may help you with the Object Oriented Programming course, or with programming Java in general. Some examples are simpler than others, but I thought it would be useful to also show some more complete programs which combine the techniques shown in the simpler examples. If you miss an example, feel free to contact me or [create an Issue](https://github.com/jelmervdl/java-examples/issues).

Examples
----------

### Action example ###
This example demonstrates how to build a simple menu bar using actions based on `AbstractAction` in anonymous classes.

### Drawing example ###
This example shows how to use and override `protected void paintComponent(Graphics g)` to paint your own component (or more specifically, JPanel).

### Observer example ###
This demo shows how you can use the `Observer` interface and `Observable` class.

### Counter example ###
This larger demo combines the Action and Observer examples and is the tiniest complete working program example of the MVC pattern I could remember.

### Grid example ###
This is more of a fun project to be honest, it is a demonstration of how to use the code from the Drawing Example combined with the `KeyListener` interface to create a simple game which stores its data in a grid.

### Method chaining example ###
A simple example to show what really happens when you write `x.getA().getB().getC()` using a rich man.

### Undo Manager example ###
This program contains a model that has an `UndoManager` which it uses to store all the edits made. The example also demonstrates how to keep all the places where the data is used up to date, and is therefore also quite a nice example of the Observable pattern.
