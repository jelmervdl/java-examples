import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.*;

class UndoExample extends JFrame
{
	/**
	 * This model has an undo manager which keeps track of all the performed
	 * edits, which is only 'setName'. You can get this undo manager by calling
	 * getUndoManager(). Observers will be notified of the changes.
	 */
	private class Model extends Observable
	{
		private String name;

		private UndoManager undoManager;

		// Let's use an inner class to hide this class from the public world
		// and to have easy access to the internals of the model, mainly
		// setNameInterval(String).
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
				Model.this.setNameInternal(prevName);
			}

			@Override
			public void redo() throws CannotUndoException
			{
				// Don't forget to call super.redo() or the 'undo' edit won't be
				// added to the undo manager
				super.redo();
				Model.this.setNameInternal(newName);
			}
		}

		public Model()
		{
			undoManager = new UndoManager();
		}

		public UndoManager getUndoManager()
		{
			return undoManager;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			// Add the modification to the undo manager
			undoManager.addEdit(new UndoSetName(getName(), name));

			// .. and really set the name (and notify all observers)
			setNameInternal(name);
		}

		// This helper method is only called from within this class (and the
		// Edit inner class) and does the real name changing
		private void setNameInternal(String name)
		{
			this.name = name;
			setChanged();
			notifyObservers();
		}
	}

	public UndoExample()
	{
		// Here lives our name:
		final Model model = new Model();

		// Create a text field
		final JTextField field = new JTextField();

		// .. that when [enter] is pressed, stores the text in the model
		field.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
				model.getUndoManager().undo();
			}
		});

		// Listens to the model to update its label and state.
		model.addObserver(new Observer() {
			@Override
			public void update(Observable source, Object arg) {
				undo.setText(model.getUndoManager().getUndoPresentationName());
				undo.setEnabled(model.getUndoManager().canUndo());
			}
		});

		// Same for the redo button.
		final JButton redo = new JButton("Redo");
		redo.setEnabled(false);
		redo.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.getUndoManager().redo();
			}
		});
		model.addObserver(new Observer() {
			@Override
			public void update(Observable source, Object arg) {
				redo.setText(model.getUndoManager().getRedoPresentationName());
				redo.setEnabled(model.getUndoManager().canRedo());
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