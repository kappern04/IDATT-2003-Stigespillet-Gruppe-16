package edu.ntnu.iir.bidata.object.file;

public class BoardGameFactory {

  private final BoardFileReader reader;
  private final BoardFileWriter writer;

  public BoardGameFactory() {
    this.reader = new BoardFileReaderGson();
    this.writer = new BoardFileWriterGson();
  }
}
