package edu.ntnu.iir.bidata.object.file;

import edu.ntnu.iir.bidata.object.Board;
import java.io.IOException;

public interface BoardFileWriter {
  void writeBoard(Board board, String filePath) throws IOException;
}
