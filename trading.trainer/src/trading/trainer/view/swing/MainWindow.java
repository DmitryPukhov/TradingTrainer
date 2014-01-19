package trading.trainer.view.swing;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.GenericXmlApplicationContext;

import trading.trainer.app.MarketEmulator;
import trading.trainer.app.TradingService;
import trading.trainer.events.OrderClosedEvent;
import trading.trainer.events.OrderOpenedEvent;
import trading.trainer.model.Bar;
import trading.trainer.model.Settings;
import trading.trainer.model.TradingContext;

import com.google.common.eventbus.Subscribe;

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
	 * Application settings bean. CDI from constructor
	 */
	private Settings settings;

	/**
	 * Market emulation service. CDI from constructor
	 */
	private MarketEmulator marketEmulator;

	/**
	 * Trading service bean. CDI from constructor
	 */
	private TradingService tradingService;

	/**
	 * Panel with price/volume chart
	 */
	private JPanel chartPanel;

	/**
	 * Trading data of the application. Account, orders etc.
	 */
	private TradingContext tradingContext;

	/**
	 * Label displays money on account
	 */
	private JLabel lblAmount;

	/**
	 * Opened orders profit
	 */
	private JLabel lblOrderProfit;

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
			@Value("#{tradingContext}") TradingContext tradingContext,
			@Value("#{marketEmulator}") MarketEmulator marketEmulator,
			@Value("#{tradingService}") TradingService tradingService) {
		this.settings = settings;
		// Attach bar events
		this.marketEmulator = marketEmulator;
		marketEmulator.getBarEventBus().register(this);

		this.tradingContext = tradingContext;
		this.tradingService = tradingService;
		// Attach order events
		tradingService.getOrderEventBus().register(this);

		// Main frame
		frmTradingTrainer = new JFrame();
		frmTradingTrainer.setTitle("Trading trainer");
		frmTradingTrainer.setBounds(100, 100, 676, 449);
		frmTradingTrainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frmTradingTrainer.getContentPane().setLayout(springLayout);

		// Chart wrapper
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
		sl_chartPanel.putConstraint(SpringLayout.NORTH, btnSettings, 10,
				SpringLayout.NORTH, chartPanel);
		sl_chartPanel.putConstraint(SpringLayout.WEST, btnSettings, 10,
				SpringLayout.WEST, chartPanel);
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
		sl_chartPanel.putConstraint(SpringLayout.NORTH, tglbtnStart, 0,
				SpringLayout.NORTH, btnSettings);
		sl_chartPanel.putConstraint(SpringLayout.WEST, tglbtnStart, 79,
				SpringLayout.EAST, btnSettings);
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

		// Buy button
		JButton btnBuy = new JButton("Buy");
		btnBuy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainWindow.this.tradingService.buy();
			}
		});
		sl_chartPanel.putConstraint(SpringLayout.NORTH, btnBuy, 0,
				SpringLayout.NORTH, chartPanel);
		sl_chartPanel.putConstraint(SpringLayout.EAST, btnBuy, -10,
				SpringLayout.EAST, chartPanel);
		chartPanel.add(btnBuy);

		// Sell button
		JButton btnSell = new JButton("Sell");
		btnSell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainWindow.this.tradingService.sell();
			}
		});
		sl_chartPanel.putConstraint(SpringLayout.NORTH, btnSell, 5,
				SpringLayout.SOUTH, btnBuy);
		sl_chartPanel.putConstraint(SpringLayout.EAST, btnSell, 0,
				SpringLayout.EAST, btnBuy);
		chartPanel.add(btnSell);

		// Price chart
		priceChart = new PriceChart(settings, marketEmulator);
		springLayout.putConstraint(SpringLayout.SOUTH, chartPanel, -6,
				SpringLayout.NORTH, priceChart);

		// Amount label
		JLabel lblAmountTitle = new JLabel("Amount");
		sl_chartPanel.putConstraint(SpringLayout.NORTH, lblAmountTitle, 5,
				SpringLayout.NORTH, btnBuy);
		sl_chartPanel.putConstraint(SpringLayout.EAST, lblAmountTitle, -300,
				SpringLayout.EAST, chartPanel);
		chartPanel.add(lblAmountTitle);

		lblAmount = new JLabel("0");
		sl_chartPanel.putConstraint(SpringLayout.NORTH, lblAmount, -9,
				SpringLayout.NORTH, btnSell);
		sl_chartPanel.putConstraint(SpringLayout.WEST, lblAmount, 0,
				SpringLayout.WEST, lblAmountTitle);
		lblAmount.setForeground(Color.BLUE);
		lblAmount.setFont(new Font("Dialog", Font.BOLD, 24));
		chartPanel.add(lblAmount);

		// Profit label
		JLabel lblProfitTitle = new JLabel("Order");
		sl_chartPanel.putConstraint(SpringLayout.NORTH, lblProfitTitle, 5,
				SpringLayout.NORTH, btnBuy);
		sl_chartPanel.putConstraint(SpringLayout.WEST, lblProfitTitle, 80,
				SpringLayout.EAST, lblAmountTitle);
		chartPanel.add(lblProfitTitle);

		lblOrderProfit = new JLabel("0");
		sl_chartPanel.putConstraint(SpringLayout.WEST, lblOrderProfit, 0,
				SpringLayout.WEST, lblProfitTitle);
		sl_chartPanel.putConstraint(SpringLayout.SOUTH, lblOrderProfit, 0,
				SpringLayout.SOUTH, lblAmount);
		lblOrderProfit.setForeground(Color.BLUE);
		lblOrderProfit.setFont(new Font("Dialog", Font.BOLD, 24));
		chartPanel.add(lblOrderProfit);

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

	/**
	 * Guava order opened event handler
	 * 
	 * @param e
	 *            OrderOpenedEvent
	 */
	@Subscribe
	public void onOrderOpened(OrderOpenedEvent e) {

	}

	/**
	 * Guava order closed event
	 * 
	 * @param e
	 *            OrderClosedEvent with order and profit info
	 */
	@Subscribe
	public void onOrderClosed(OrderClosedEvent e) {
		updateView();

	}

	/**
	 * New bar Guava event
	 * 
	 * @param bar
	 *            newly received bar
	 */
	@Subscribe
	public void onNewBar(Bar bar) {
		updateView();
	}

	/**
	 * Update account view from trading context
	 */
	private void updateView() {
		// Total amount
		updateAmountView();

		// Order profit
		updateProfitView();
	}

	/**
	 * Update profit info displayed
	 */
	private void updateProfitView() {
		Bar bar = tradingContext.getLastBar();
		if (bar == null) {
			return;
		}
		Double profit = tradingService.getOpenedOrdersProfit();
		lblOrderProfit.setText(profit.toString());
	}

	/**
	 * Update amount label
	 */
	private void updateAmountView() {
		// Display current account amount
		Double amount = tradingContext.getAmount();
		// Set label color
		Color color = Color.BLUE;
		if (amount > 0) {
			color = Color.GREEN;
		} else if (amount < 0) {
			color = Color.RED;
		}
		// Set label
		lblAmount.setForeground(color);
		lblAmount.setText(amount.toString());
	}
}
