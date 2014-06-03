/* [LGPL] Copyright 2010, 2011 Gima

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package coderats.ar.gl;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;


/**
 * This class presents the user with a dialog from which the user can choose a {@link DisplayMode} and whether to use fullscreen or not.
 * <p>
 * The dialog will be shown with Java Swing framework.
 */
public class DisplayModeChooserDialog extends JFrame implements ActionListener {
	
	public static void main(String[] args) {
		System.out.println(
				DisplayModeChooserDialog.dialogChooseDisplayMode()
				);
	}
	
	//TODO: SEE IF SHIT

	private static final long serialVersionUID = 1L;
	private final AtomicReference<DisplayModePack> transferData;
	private JComboBox<String> comboBox;
	private DisplayMode[] displayModeList;
	private JCheckBox fsCheckbox;
	private int forcedDisplayModesStartIdx;
	
	
	/**
	 * Shows the dialog and returns only when the user has chose something in the dialog.
	 * <p>
	 * Must not be called from inside Swing Event Dispatching thread, will deadlock.
	 *  
	 * @return The DisplayModePack user chose.
	 * @throws NullPointerException If the user did not choose a display mode or an error occurred.
	 */
	public static DisplayModePack dialogChooseDisplayMode() {
		
		final AtomicReference<DisplayModePack> transferData = new AtomicReference<DisplayModePack>();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new DisplayModeChooserDialog(transferData);
			}
		});
		
		
		int numWaitFailed = 0;
		while (true) {
			if (numWaitFailed > 1) {
				return null;
			}
			try {
				synchronized (transferData) {
					transferData.wait();
				}
				break;
			}
			catch (InterruptedException e) {
				numWaitFailed++;
				try { Thread.sleep(100); } catch (InterruptedException e1) {}
			} // catch
		}
		
		DisplayModePack dmp = transferData.get();
		if (dmp == null) throw new NullPointerException("User did not choose a display mode or an error occurred.");
		return dmp;
	}
	
	
	private DisplayModeChooserDialog(final AtomicReference<DisplayModePack> transferData) {
		
		super("Display mode");
		this.transferData = transferData;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		setResizable(false);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				
				transferData.set(null);
				
				synchronized(transferData) {
					transferData.notify();
				}
				
			};
		});

		
		/*
		 * add informative text
		 */
		
		JLabel label = new JLabel("Select graphical application display mode");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel labelPanel = new JPanel();
		
		labelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelPanel.add(label);
		add(labelPanel);
		
		
		/*
		 * add combo box which contains available display modes in sorted order
		 */

		comboBox = new JComboBox<String>();
		comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String>) comboBox.getModel();
		
		try {
			displayModeList = Display.getAvailableDisplayModes();
		}
		catch (LWJGLException e) {
			e.printStackTrace();
			dispose();
			
			transferData.set(null);
			
			synchronized(transferData) {
				transferData.notify();
			}
			
			return;
		}

		/*
		 * sort them
		 */
		
		DisplayModeComparator comparator = new DisplayModeComparator();
		Arrays.sort(displayModeList, comparator);
		
		if (displayModeList.length <= 1) {
			LinkedList<DisplayMode> modes = new LinkedList<>(Arrays.asList(displayModeList));
			forcedDisplayModesStartIdx = displayModeList.length-1;

			modes.add(new DisplayMode(800, 600));
			modes.add(new DisplayMode(1024, 768));
			modes.add(new DisplayMode(1280, 1024));
			modes.add(new DisplayMode(1440, 900));
			modes.add(new DisplayMode(1400, 1050));
			modes.add(new DisplayMode(1680, 1050));
			modes.add(new DisplayMode(1920, 1200));
			
			displayModeList = modes.toArray(new DisplayMode[0]);
		}
		else {
			forcedDisplayModesStartIdx = Integer.MAX_VALUE;
		}
		
		for (DisplayMode dm : displayModeList) {
			comboBoxModel.addElement(DisplayModeComparator.getStringRepresentation(dm));
		}
		
		add( comboBox );
		comboBox.setActionCommand("ComboBox");
		comboBox.addActionListener(this);
		
		
		/*
		 * add panel for fullscreen checkbox
		 */
		
		fsCheckbox = new JCheckBox("Fullscreen");
		if (forcedDisplayModesStartIdx != Integer.MAX_VALUE) {
			fsCheckbox.setEnabled(true);
		}
		else {
			fsCheckbox.setEnabled(displayModeList[0].isFullscreenCapable());
		}
		
		JPanel fsPanel = new JPanel();
		fsPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		fsPanel.add(fsCheckbox);
		
		add( fsPanel );
		
		
		/*
		 * add panel for ok and cancel buttons
		 */
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		add(buttonPanel);
		
		
		/*
		 * make window visible
		 */
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	} // constructor


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			
			boolean fullscreen;
			
			if (fsCheckbox.isEnabled()) fullscreen = fsCheckbox.isSelected();
			else fullscreen = false;

			transferData.set(new DisplayModePack(
					displayModeList[ comboBox.getSelectedIndex() ],
					null, //pixelformat?!
					fullscreen));
			
			
			synchronized( transferData ) {
				transferData.notify();
			}
			setVisible( false );
			dispose();
		}
		else if (  e.getActionCommand().equals( "Cancel" )  ) {
			
			transferData.set(null);
			
			synchronized( transferData ) {
				transferData.notify();
			}
			
			setVisible( false );
			dispose();
		}
		else if (  e.getActionCommand().equals( "ComboBox" )  ) {
			if (comboBox.getSelectedIndex() >= forcedDisplayModesStartIdx) fsCheckbox.setEnabled(true);
			else fsCheckbox.setEnabled(  displayModeList[ comboBox.getSelectedIndex() ].isFullscreenCapable()  );
		}
		
	} // method
	
	
}
