package nz.bradcampbell.benchmarkdemo.parceltasks;

public class ParcelResult {
  public long runDuration;
  public int objectsParcelled;

  public ParcelResult(long runDuration, int objectsParsed) {
    this.runDuration = runDuration;
    this.objectsParcelled = objectsParsed;
  }
}
