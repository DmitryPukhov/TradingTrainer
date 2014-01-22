package trading.trainer.view.swing;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Calendar;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYDataset;

import trading.trainer.app.MarketEmulator;
import trading.trainer.model.Bar;
import trading.trainer.model.Settings;

import com.google.common.eventbus.Subscribe;

/**
 * Price chart control
 * 
 * @author dima
 * 
 */
public class PriceChart extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Application settings bean
	 */
	private Settings settings;

	/**
	 * Market emulator service
	 */
	private MarketEmulator marketEmulator;

	/**
	 * Candlestick data
	 */
	private OHLCSeries priceSeries;

	/**
	 * Volume data
	 */
	private TimeSeries volumeSeries;

	/**
	 * Price chart control
	 */
	private JFreeChart priceChart;

	/**
	 * Volume chart control
	 */
	private JFreeChart volumeChart;

	/**
	 * Ctor, jfreechart init
	 * 
	 * @param settings
	 *            application settings bean
	 */
	public PriceChart(Settings settings, MarketEmulator marketEmulator) {
		this.settings = settings;
		this.marketEmulator = marketEmulator;
		// Attach to new bar events
		this.marketEmulator.getBarEventBus().register(this);

		// update view when is shown
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				// Load settings from file when shown
				updateView();
				// Fill chart with test data
				// createTestData(priceSeries);
			}
		});
		// this.title = title;
		setBackground(SystemColor.control);

		// Price chart
		OHLCSeriesCollection priceSeriesCollection = createPriceSeriesCollection();
		priceSeries = priceSeriesCollection.getSeries(0);
		priceChart = createPriceChart(priceSeriesCollection);
		setLayout(new GridLayout(0, 1, 0, 0));

		// Volume chart
		final XYDataset volumeDataSet = createVolumeDataSet();
		// fillTestData(priceSeries);
		volumeChart = createVolumeChart(volumeDataSet);
		setLayout(new GridLayout(0, 1, 0, 0));

		// Main split pane
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.7);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);

		// Price chart pane
		final ChartPanel priceChartPanel = new ChartPanel(priceChart);
		splitPane.setLeftComponent(priceChartPanel);
		FlowLayout flowLayout_1 = (FlowLayout) priceChartPanel.getLayout();
		flowLayout_1.setAlignOnBaseline(true);
		priceChartPanel.setPreferredSize(new java.awt.Dimension(500, 370));

		// Volume chart pane
		final ChartPanel volumeChartPanel = new ChartPanel(volumeChart);
		splitPane.setRightComponent(volumeChartPanel);
		FlowLayout flowLayout_2 = (FlowLayout) volumeChartPanel.getLayout();
		flowLayout_2.setAlignOnBaseline(true);
		volumeChartPanel.setPreferredSize(new java.awt.Dimension(500, 100));
	}

	/**
	 * Add list of bars to the chart
	 * 
	 * @param bars
	 */
	public void addBars(final List<Bar> bars) {
		for (Bar bar : bars) {
			addBar(bar);
		}
	}

	/**
	 * Add new OHLCV bar to price and volume charts
	 * 
	 * @param Bar
	 *            new bar to add
	 */
	public void addBar(final Bar bar) {
		// Get time
		FixedMillisecond fm = new FixedMillisecond(bar.getTime());
		// Add bar
		priceSeries.add(fm, bar.getOpen(), bar.getHigh(), bar.getLow(),
				bar.getClose());

		// Add volume
		volumeSeries.addOrUpdate(fm, bar.getVolume());
	}

	/**
	 * Clear price and volume charts
	 */
	public void clear() {
		priceSeries.clear();
		volumeSeries.clear();
	}

	/**
	 * Price chart creation
	 * 
	 * @param dataset
	 * @return
	 */
	private JFreeChart createPriceChart(
			final OHLCSeriesCollection seriesCollection) {
		final JFreeChart chart = ChartFactory.createCandlestickChart("test",
				"time", "price", seriesCollection, true);

		NumberAxis axisY = (NumberAxis) chart.getXYPlot().getRangeAxis();
		axisY.setAutoRange(true);
		axisY.setAutoRangeIncludesZero(false);

		return chart;

	}

	/**
	 * Create OHLC data for chart
	 */
	OHLCSeriesCollection createPriceSeriesCollection() {
		OHLCSeries series = new OHLCSeries("Price");
		series.setMaximumItemCount(settings.getDataWindowSize());
		OHLCSeriesCollection seriesCollection = new OHLCSeriesCollection();
		seriesCollection.addSeries(series);
		return seriesCollection;
	}

	/**
	 * Fill given series with test data
	 * 
	 * @param seriesCollection
	 *            collection with one series element to fill with data
	 */
	void createTestData(OHLCSeries series) {

		for (int i = 0; i < 10; i++) {
			// Generate bar time
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, i);
			FixedMillisecond fm = new FixedMillisecond(cal.getTime());

			// Add bar to the data
			series.add(fm, 100, 110, 90, 105);
		}
	}

	/**
	 * Create chart for volume data
	 * 
	 * @param dataset
	 * @return
	 */
	private JFreeChart createVolumeChart(final XYDataset dataset) {
		// Create volume chart
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null,
				"volume", dataset, false, false, false);

		// Set up volume chart look and feel
		final XYPlot plot = chart.getXYPlot();
		final ClusteredXYBarRenderer renderer = new ClusteredXYBarRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setShadowVisible(false);
		plot.setRenderer(renderer);

		return chart;
	}

	/**
	 * Data set for volume chart
	 * 
	 * @return
	 */
	private XYDataset createVolumeDataSet() {
		// Add empty time series to fill later
		// Create price series
		volumeSeries = new TimeSeries("Volume");
		volumeSeries.setMaximumItemCount(settings.getDataWindowSize());
		// Create and return DataSet with all time series
		TimeSeriesCollection dataSet = new TimeSeriesCollection();
		dataSet.addSeries(volumeSeries);
		return dataSet;
	}

	/**
	 * @return maximum bars on chart.
	 */
	public int getMaxItemCount() {
		return priceSeries.getMaximumItemCount();
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return priceChart.getTitle().getText();
	}

	/**
	 * New bar Guava event receiver
	 */
	@Subscribe
	public void onNewBar(Bar newBar) {
		// Add new bar to the chart
		addBar(newBar);
		// createTestData(priceSeries);
	}

	/**
	 * @param maxItemCount
	 *            the maxItemCount to set
	 */
	public void setMaxItemCount(int maxItemCount) {
		priceSeries.setMaximumItemCount(maxItemCount);
		volumeSeries.setMaximumItemCount(maxItemCount);
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		priceChart.setTitle(title);
	}

	/**
	 * Additional initialization after bean has been created
	 */
	public void updateView() {
		priceSeries.setMaximumItemCount(settings.getDataWindowSize());
		volumeSeries.setMaximumItemAge(settings.getDataWindowSize());
	}
}
