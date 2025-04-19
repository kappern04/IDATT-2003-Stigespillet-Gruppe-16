package edu.ntnu.iir.bidata.file;

import edu.ntnu.iir.bidata.model.Board;
import java.io.IOException;

public interface BoardFileWriter {
  void writeBoard(Board board, String filePath) throws IOException;
}
