package edu.ntnu.iir.bidata.laddergame.file;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import java.io.IOException;

/**
 * Interface for writing board files.
 * Implementations should provide a method to write a board to a file.
 */
public interface BoardFileWriter {
  void writeBoard(Board board, String filePath) throws IOException;
}
