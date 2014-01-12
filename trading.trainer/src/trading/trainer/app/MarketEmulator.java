package trading.trainer.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.SwingWorker;

import trading.trainer.model.Bar;

import com.google.common.eventbus.EventBus;

/**
 * Get data from file, produces every single bar event like real stock exchange
 * 
 * @author dima
 * 
 */
@Named
public class MarketEmulator extends SwingWorker<Void, Bar> {

	private long intervalMillis;
	private EventBus barEventBus = new EventBus();

	/**
	 * @return Guava EventBus for new bar event
	 */
	public EventBus getBarEventBus() {
		return barEventBus;
	}

	/**
	 * Application settings
	 */
	@Inject
	Settings settings;

	/**
	 * Stop signal
	 */
	private boolean stopFlag = false;

	/**
	 * @return the tick intervalMillis
	 */
	public long getIntervalMillis() {
		return intervalMillis;
	}

	/**
	 * Get bar from csv string
	 * 
	 * @return
	 */
	private Bar parseBar(String csvString) {
		// Example of csv file with header:
		// <TICKER>;<PER>;<DATE>;<TIME>;<OPEN>;<HIGH>;<LOW>;<CLOSE>;<VOL>
		// SPFB.RTS;5;20131108;100000;144950.0000000;145000.0000000;144540.0000000;144670.0000000;27993
		// SPFB.RTS;5;20131108;100500;144650.0000000;144690.0000000;144330.0000000;144390.0000000;22271
		StringTokenizer st = new StringTokenizer(csvString, ";");
		// Ticker - skip
		st.nextToken();
		// Period - skip
		st.nextToken();
		// Datetime
		String dateString = st.nextToken();
		String timeString = st.nextToken();
		Date time = parseDate(dateString, timeString);
		// OHLC
		double open = Double.parseDouble(st.nextToken());
		double high = Double.parseDouble(st.nextToken());
		double low = Double.parseDouble(st.nextToken());
		double close = Double.parseDouble(st.nextToken());
		long volume = Long.parseLong(st.nextToken());

		Bar bar = new Bar(time, open, high, low, close, volume);
		return bar;
	}

	/**
	 * Parse date from csv
	 * 
	 * @param dateString
	 *            date value from csv in format yyyymmdd
	 * @param timeString
	 *            time value from csv in format hhmmss
	 * @return
	 */
	private Date parseDate(String dateString, String timeString) {
		// Get year, month and day
		int dateValue = Integer.valueOf(dateString);
		int year = dateValue / 10000;
		int month = (dateValue % 10000) / 100;
		int day = dateValue % 100;

		// Get hour, min and second
		int timeValue = Integer.valueOf(timeString);
		int hour = timeValue / 10000;
		int minute = (timeValue % 10000) / 100;
		int sec = timeValue % 100;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day - 1, hour, minute, sec);

		return cal.getTime();

	}

	/**
	 * @param intervalMillis
	 *            the tick intervalMillis to set
	 */
	public void setIntervalMillis(long intervalMillis) {
		this.intervalMillis = intervalMillis;
	}

	/**
	 * Loads all data from csv file and generate events
	 * 
	 */
	public void start() {
		stopFlag = false;
		String filePath = settings.getDataFilePath();
		File file = new File(filePath);
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			// Skip header first
			String headerLine = reader.readLine();

			if (headerLine != null) {
				// Read bar line from csv file, convert it to bar and
				// fire event
				for (String line = reader.readLine(); line != null && !stopFlag; line = reader
						.readLine()) {
					// Parse string in data file
					Bar bar = parseBar(line);
					// publish bar as a swing worker result
					publish(bar);

					// Delay before next bar
					int intervalMillis = settings.getTickIntervalSeconds() * 1000;

					if (intervalMillis > 0) {
						Thread.sleep(intervalMillis);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Interrupts market emulation
	 */
	public void stop() {
		stopFlag = true;
	}

	/**
	 * Swing worker entry method
	 */
	@Override
	protected Void doInBackground() throws Exception {
		start();
		return null;
	}

	/**
	 * Process published swing worker event
	 */
	@Override
	protected void process(List<Bar> bars) {
		// Fire last bar added event. Bars is always not empty
		Bar bar = bars.get(bars.size() - 1);
		barEventBus.post(bar);
	}
}
