package cek.ruins.world.history;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cek.ruins.bookofnames.BookOfNames;
import cek.ruins.events.Event;
import cek.ruins.events.EventName;
import cek.ruins.events.EventsDispatcher;
import cek.ruins.map.Map;
import cek.ruins.world.civilizations.Civilizations;
import cek.ruins.world.environment.Biomes;
import cek.ruins.world.environment.Resources;
import cek.ruins.world.locations.Locations;
import cek.ruins.world.locations.settlements.Architect;

public class HistoriansDirector {
	public static String NS = HistoriansDirector.class.toString().substring(6);

	public static int DAYS_IN_A_YEAR = 360;
	public static int DAYS_IN_A_MONTH = 30;
	public static int MONTHS_IN_A_YEAR = 12;
	public static int MONTHS_IN_A_SEASON = 3;
	public static int SEASONS_IN_A_YEAR = 4;

	private List<Historian> registeredHistorians;
	private List<EventsDispatcher> dispatchers;

	private int year;
	private boolean notifyYear;
	private int month;
	private boolean notifyMonth;
	private int day;
	private boolean notifyDay;
	private int season;
	private boolean notifySeason;

	protected Map map;
	protected Civilizations existingCivilizations;
	protected Locations locations;
	protected Biomes biomes;
	protected Resources resources;
	protected Random generator;
	protected BookOfNames bookOfNames;
	protected Architect architect;

	private PrintWriter logger;

	public HistoriansDirector(Map map, Locations locations, Civilizations existingCivilizations, Architect architect, Biomes biomes, Resources resources, Random generator, BookOfNames bookOfNames) {
		this.registeredHistorians = new ArrayList<Historian>();
		this.dispatchers = new ArrayList<EventsDispatcher>();

		this.map = map;
		this.existingCivilizations = existingCivilizations;
		this.locations = locations;
		this.biomes = biomes;
		this.resources = resources;
		this.generator = generator;
		this.bookOfNames = bookOfNames;
		this.architect = architect;

		this.day = 1;
		this.month = 1;
		this.year = 1;
		this.season = 0;

		this.notifyDay = true;
		this.notifyMonth = false;
		this.notifyYear = false;
		this.notifySeason = false;

		//don't init history logging
		this.logger = null;
	}

	public void dispatch(Event event) {
		Iterator<EventsDispatcher> dispatchersIt = this.dispatchers.iterator();
		while (dispatchersIt.hasNext()) {
			dispatchersIt.next().publish(event);
		}
	}

	public void registerHistorian(Historian historian) {
		historian.setDirector(this);
		this.registeredHistorians.add(historian);
	}

	public void registerDispatcher(EventsDispatcher dispatcher) {
		this.dispatchers.add(dispatcher);
	}
	
	public void clearDispatchers() {
		this.dispatchers.clear();
	}

	public int year() {
		return this.year;
	}
	public int season() {
		return this.season;
	}
	public int month() {
		return this.month;
	}
	public int day() {
		return this.day;
	}

	public void setDateNotification(boolean notifyDay, boolean notifyMonth, boolean notifyYear, boolean notifySeason) {
		this.notifyDay = notifyDay;
		this.notifyMonth = notifyMonth;
		this.notifyYear = notifyYear;
		this.notifySeason = notifySeason;
	}

	public void advanceDay() throws Exception {
		//do daily things...
		Iterator<Historian> historiansIt = this.registeredHistorians.iterator();
		while (historiansIt.hasNext()) {
			((Historian) historiansIt.next()).advanceDay();
		}

		//check if we have to change month
		if (this.day == HistoriansDirector.DAYS_IN_A_MONTH) {
			//we need to advance month
			advanceMonth();

			this.day = 1;
		}
		else
			this.day++;

		if (this.notifyDay) {
			//notify date change to subscribers
			Event event = new Event(HistoriansDirector.NS, EventName.DATE_CHANGE_EVENT);
			event.addData("day", this.day);
			event.addData("month", this.month);
			event.addData("season", this.season);
			event.addData("year", this.year);
			dispatch(event);
		}
	}

	public void advanceMonth() throws Exception {
		//do monthly things...
		Iterator<Historian> historiansIt = this.registeredHistorians.iterator();
		while (historiansIt.hasNext()) {
			((Historian) historiansIt.next()).advanceMonth();
		}

		//check if we have to change season
		if (this.month%HistoriansDirector.MONTHS_IN_A_SEASON == 0) {
			//we need to advance year
			advanceSeason();
		}
		else
			this.month++;

		if (this.notifyMonth) {
			//notify date change to subscribers
			Event event = new Event(HistoriansDirector.NS, EventName.DATE_CHANGE_EVENT);
			event.addData("day", this.day);
			event.addData("month", this.month);
			event.addData("season", this.season);
			event.addData("year", this.year);
			dispatch(event);
		}
	}

	public void advanceSeason() throws Exception {
		//do seasonally things...
		Iterator<Historian> historiansIt = this.registeredHistorians.iterator();
		while (historiansIt.hasNext()) {
			((Historian) historiansIt.next()).advanceSeason();
		}

		//check if we have to change year
		if (this.month == HistoriansDirector.MONTHS_IN_A_YEAR) {
			//we need to advance year
			advanceYear();

			this.month = 1;
			this.season = 0;
		}
		else {
			this.season++;
			this.month++;
		}

		if (this.notifySeason) {
			//notify date change to subscribers
			Event event = new Event(HistoriansDirector.NS, EventName.DATE_CHANGE_EVENT);
			event.addData("day", this.day);
			event.addData("month", this.month);
			event.addData("season", this.season);
			event.addData("year", this.year);
			dispatch(event);
		}
	}

	public void advanceYear() throws Exception {
		//do yearly things...
		Iterator<Historian> historiansIt = this.registeredHistorians.iterator();
		while (historiansIt.hasNext()) {
			((Historian) historiansIt.next()).advanceYear();
		}

		this.year++;

		if (this.notifyYear) {
			//notify date change to subscribers
			Event event = new Event(HistoriansDirector.NS, EventName.DATE_CHANGE_EVENT);
			event.addData("day", this.day);
			event.addData("month", this.month);
			event.addData("season", this.season);
			event.addData("year", this.year);
			dispatch(event);
		}
	}

	public void setHistoryLoggingFile(String path) throws IOException {
		File logFile = new File(path);
		this.logger = new PrintWriter(new FileWriter(logFile, true));
	}

	public void disableHistoryLogging() {
		if (this.logger != null)
			this.logger.close();

		this.logger = null;
	}

	public void writeHistory(String entry) {
		if (this.logger != null) {
			String seasonStr = "winter";

			switch (this.season) {
			case 1:
				seasonStr = "spring";
				break;
			case 2:
				seasonStr = "summer";
				break;
			case 3:
				seasonStr = "autumn";
				break;
			}

			this.logger.print("[" + String.format("%02d", this.day) + "/" + String.format("%02d", this.month) + "/" + String.format("%04d", this.year) + "-" + seasonStr + "] ");
			this.logger.println(entry);

			this.logger.flush();
		}
	}
}
