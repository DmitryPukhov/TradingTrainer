package trading.trainer.view.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.GenericXmlApplicationContext;

import trading.trainer.app.MarketEmulator;
import trading.trainer.app.Settings;

@Named
public class MainWindow {
	private static GenericXmlApplicationContext springContext;

	/**
	 * Swing main frame
	 */
	private JFrame frmTradingTrainer;

	/**
	 * Settings swing dialog
	 */
	private SettingsDialog settingsDialog;

	/**
	 * Price candlestick chart control
	 */
	private PriceChart priceChart;

	/**
	 * Application settings bean
	 */
	public Settings settings;

	/**
	 * Market emulation service
	 */
	public MarketEmulator marketEmulator;

	/**
	 * Panel with price/volume chart
	 */
	private JPanel chartPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Spring initialization
		springContext = new GenericXmlApplicationContext();
		springContext.load("classpath:META-INF/spring.xml");
		springContext.registerShutdownHook();
		springContext.refresh();

		// Main window is a bean, get it. Other swing subcontrols like chart,
		// settings dialog are not beans
		final MainWindow window = springContext.getBean(MainWindow.class);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// MainWindow window = new MainWindow();
					window.frmTradingTrainer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@Inject
	public MainWindow(@Value("#{settings}") Settings settings,
			@Value("#{marketEmulator}") MarketEmulator marketEmulator) {
		this.settings = settings;
		this.marketEmulator = marketEmulator;

		frmTradingTrainer = new JFrame();
		frmTradingTrainer.setTitle("Trading trainer");
		frmTradingTrainer.setBounds(100, 100, 676, 449);
		frmTradingTrainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frmTradingTrainer.getContentPane().setLayout(springLayout);

		chartPanel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, chartPanel, 0,
				SpringLayout.NORTH, frmTradingTrainer.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, chartPanel, 0,
				SpringLayout.WEST, frmTradingTrainer.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, chartPanel, 0,
				SpringLayout.EAST, frmTradingTrainer.getContentPane());
		frmTradingTrainer.getContentPane().add(chartPanel);
		SpringLayout sl_chartPanel = new SpringLayout();
		chartPanel.setLayout(sl_chartPanel);

		// Run settings dialog
		JButton btnSettings = new JButton("Settings");
		sl_chartPanel.putConstraint(SpringLayout.EAST, btnSettings, -10,
				SpringLayout.EAST, chartPanel);
		springLayout.putConstraint(SpringLayout.NORTH, btnSettings, 5,
				SpringLayout.NORTH, chartPanel);
		springLayout.putConstraint(SpringLayout.WEST, btnSettings, 287,
				SpringLayout.WEST, chartPanel);
		chartPanel.add(btnSettings);
		btnSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Show settings dialog
				settingsDialog.setVisible(true);

			}
		});
		springLayout.putConstraint(SpringLayout.EAST, btnSettings, -10,
				SpringLayout.EAST, frmTradingTrainer.getContentPane());

		// Start button
		final JToggleButton tglbtnStart = new JToggleButton("Start");
		sl_chartPanel.putConstraint(SpringLayout.NORTH, btnSettings, 0,
				SpringLayout.NORTH, tglbtnStart);
		sl_chartPanel.putConstraint(SpringLayout.NORTH, tglbtnStart, 10,
				SpringLayout.NORTH, chartPanel);
		sl_chartPanel.putConstraint(SpringLayout.WEST, tglbtnStart, 10,
				SpringLayout.WEST, chartPanel);
		tglbtnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Run or stop market emulation
				if (tglbtnStart.isSelected()) {
					// Run
					startMarketEmulator();

				} else {
					// Stop
					MainWindow.this.marketEmulator.stop();
				}
			}
		});
		chartPanel.add(tglbtnStart);

		// Price chart
		priceChart = new PriceChart(settings, marketEmulator);
		springLayout.putConstraint(SpringLayout.SOUTH, chartPanel, -6,
				SpringLayout.NORTH, priceChart);
		springLayout.putConstraint(SpringLayout.NORTH, priceChart, 71,
				SpringLayout.NORTH, frmTradingTrainer.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, priceChart, 0,
				SpringLayout.WEST, frmTradingTrainer.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, priceChart, 0,
				SpringLayout.SOUTH, frmTradingTrainer.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, priceChart, 0,
				SpringLayout.EAST, frmTradingTrainer.getContentPane());
		springLayout = (SpringLayout) frmTradingTrainer.getContentPane()
				.getLayout();

		frmTradingTrainer.getContentPane().add(priceChart);
		// Settings dialog
		settingsDialog = new SettingsDialog(settings);

	}

	/**
	 * Run market emulator
	 */
	private void startMarketEmulator() {
		marketEmulator.execute();
	}
}
