package cek.ruins.world.history;

public abstract class Historian {
	protected HistoriansDirector director;

	public Historian() {

	}

	public abstract void advanceDay() throws Exception;
	public abstract void advanceMonth() throws Exception;
	public abstract void advanceSeason() throws Exception;
	public abstract void advanceYear() throws Exception;

	public void setDirector(HistoriansDirector director) {
		this.director = director;
	}
}
