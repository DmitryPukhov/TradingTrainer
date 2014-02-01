package trading.trainer.view.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import trading.trainer.model.Settings;

/**
 * Application settings window
 * 
 * @author dima
 * 
 */

public class SettingsDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Main content panel
	 */
	private final JPanel contentPanel = new JPanel();

	/**
	 * Settings xml file path text field
	 */
	private JTextField txtFilePath;

	/**
	 * Application settings bean
	 */
	private Settings settings;

	/**
	 * Text box with parameter - size of bars data
	 */
	private JFormattedTextField txtDataWindowSize;

	/**
	 * Check box with parameter - start trading from random position or from
	 * beginning
	 */
	private JCheckBox chckbxRandomStartPosition;

	/**
	 * Create the dialog.
	 * 
	 * @param settings
	 *            Settings bean
	 */
	public SettingsDialog(Settings settings) {
		this.settings = settings;

		// Load settings from file when run
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				// Load settings from file when shown
				loadSettings();
			}
		});
		setModal(true);
		setTitle("Settings");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);

		// File chooser
		// File chooser
		Box filePanel = Box.createHorizontalBox();

		{
			sl_contentPanel.putConstraint(SpringLayout.EAST, filePanel, -10,
					SpringLayout.EAST, contentPanel);
			contentPanel.add(filePanel);
			{
				txtFilePath = new JTextField();
				filePanel.add(txtFilePath);
				txtFilePath.setColumns(10);
			}
			{
				// File chooser config
				final JFileChooser fileChooser = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("csv", "csv");
				fileChooser.setFileFilter(filter);
				// Browser button
				JButton btnBrowse = new JButton("Browse");
				btnBrowse.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// choose data file
						int result = fileChooser
								.showOpenDialog(SettingsDialog.this);
						if (result == JFileChooser.APPROVE_OPTION) {
							txtFilePath.setText(fileChooser.getSelectedFile()
									.getPath());
						}
					}
				});

				filePanel.add(btnBrowse);
			}
		}
		// Data file label
		JLabel lblDataFile = new JLabel("Data file");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblDataFile, 20,
				SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, filePanel, 25,
				SpringLayout.EAST, lblDataFile);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, filePanel, 0,
				SpringLayout.SOUTH, lblDataFile);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblDataFile, 20,
				SpringLayout.WEST, contentPanel);
		contentPanel.add(lblDataFile);

		// Data window size
		NumberFormatter numberFormatter = new NumberFormatter();
		txtDataWindowSize = new JFormattedTextField(numberFormatter);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, txtDataWindowSize,
				51, SpringLayout.SOUTH, filePanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, txtDataWindowSize,
				229, SpringLayout.WEST, contentPanel);
		contentPanel.add(txtDataWindowSize);

		// Label for window size
		JLabel lblDataWindowSize = new JLabel("Data window size");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, txtDataWindowSize,
				-5, SpringLayout.NORTH, lblDataWindowSize);
		sl_contentPanel.putConstraint(SpringLayout.WEST, txtDataWindowSize, 23,
				SpringLayout.EAST, lblDataWindowSize);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblDataWindowSize,
				31, SpringLayout.SOUTH, filePanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblDataWindowSize, 0,
				SpringLayout.WEST, lblDataFile);
		contentPanel.add(lblDataWindowSize);

		// Random start position check box
		chckbxRandomStartPosition = new JCheckBox("Random start position");
		sl_contentPanel.putConstraint(SpringLayout.NORTH,
				chckbxRandomStartPosition, 22, SpringLayout.SOUTH,
				lblDataWindowSize);
		sl_contentPanel.putConstraint(SpringLayout.WEST,
				chckbxRandomStartPosition, 0, SpringLayout.WEST, lblDataFile);
		contentPanel.add(chckbxRandomStartPosition);

		// Ok button
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				// Ok button
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Save settings
						saveSettings();
						SettingsDialog.this.setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			// Cancel button
			{
				// Cancel button
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Cancel
						SettingsDialog.this.setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * Get application settings
	 * 
	 * @see #settings
	 * 
	 * @return the settings
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * Load settings from file
	 */
	private void loadSettings() {
		settings.load();

		// Update view from settings
		txtFilePath.setText(settings.getDataFilePath());
		txtDataWindowSize.setText(String.valueOf(settings.getDataWindowSize()));
		chckbxRandomStartPosition.setSelected(settings.getRandomStart());
	}

	/**
	 * Save settings to file
	 */
	private void saveSettings() {
		// Update settings object from view
		settings.setDataFilePath(txtFilePath.getText());
		int dataWindowSize = Integer.parseInt(txtDataWindowSize.getText());
		settings.setDataWindowSize(dataWindowSize);
		boolean isRandomStart = chckbxRandomStartPosition.isSelected();
		settings.setRandomStart(isRandomStart);
		// Store to xml file
		settings.save();
	}
}
