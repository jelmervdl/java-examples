import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;

class UndoExample extends JFrame
{
	/**
	 * A simple model which only stores a name, but is observable. (This is also
	 * the practical reason this is an inner class.. UndoExample already extends
	 * JFrame and could therefore not also extend Observable.)
	 */
	private class Model extends Observable
	{
		private String name;
		
		public String getName()
		{
			return name;
		}

		private void setName(String name)
		{
			this.name = name;
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * This innner class represents the change of a name by keeping both the old
	 * and the new value.
	 */
	private class UndoSetName extends AbstractUndoableEdit
	{
		public String newName;

		public String prevName;

		public UndoSetName(String prevName, String newName)
		{
			// Store both the previous name and the new name which we can
			// use to undo or redo this edit.
			this.prevName = prevName;
			this.newName = newName;
		}

		@Override
		public String getPresentationName()
		{
			// This, plus 'Undo' or 'Redo ' will be the name of this edit
			return "change to " + newName;
		}

		@Override
		public void undo() throws CannotUndoException
		{
			// Don't forget to call super.undo() or the 'redo' edit won't be
			// added to the undo manager
			super.undo();

			// Call the internal method so no extra undo or redo edit is
			// added to the undo manager.
			model.setName(prevName);
		}

		@Override
		public void redo() throws CannotUndoException
		{
			// Don't forget to call super.redo() or the 'undo' edit won't be
			// added to the undo manager
			super.redo();
			model.setName(newName);
		}
	}

	private Model model = new Model();
	
	private UndoManager undoManager = new UndoManager();
	
	public UndoExample()
	{
		// Create a text field
		final JTextField field = new JTextField();

		// .. that when [enter] is pressed, stores the text in the model
		field.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Register the edit with the undo manager
				undoManager.addEdit(new UndoSetName(model.getName(), field.getText()));

				// .. then also apply it to the model itself.
				model.setName(field.getText());
			}
		});
		
		// .. but also listens to the model to stay up to date.
		model.addObserver(new Observer() {
			@Override
			public void update(Observable source, Object arg) {
				field.setText(model.getName());
			}
		});


		// Create the undo button
		final JButton undo = new JButton("Undo");

		// Disabled by default.
		undo.setEnabled(false);

		// When clicked, will undo the last edit.
		undo.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				undoManager.undo();
			}
		});

		// Listens to the model to update its label and state.
		model.addObserver(new Observer() {
			@Override
			public void update(Observable source, Object arg) {
				undo.setText(undoManager.getUndoPresentationName());
				undo.setEnabled(undoManager.canUndo());
			}
		});

		// Same for the redo button.
		final JButton redo = new JButton("Redo");
		redo.setEnabled(false);
		redo.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				undoManager.redo();
			}
		});
		model.addObserver(new Observer() {
			@Override
			public void update(Observable source, Object arg) {
				redo.setText(undoManager.getRedoPresentationName());
				redo.setEnabled(undoManager.canRedo());
			}
		});

		// Set a layout, some size, and add the field and the buttons.
		setLayout(new GridLayout(3, 1));
		setSize(300, 120);

		add(field);
		add(undo);
		add(redo);
	}

	static public void main(String[] args)
	{
		JFrame win = new UndoExample();
		win.setDefaultCloseOperation(EXIT_ON_CLOSE);
		win.setVisible(true);
	}
}