package edu.ntnu.iir.bidata.laddergame.file;

import edu.ntnu.iir.bidata.laddergame.model.Board;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for reading board files.
 * Implementations should provide a method to read a board from an InputStream.
 */
public interface BoardFileReader {
  Board readBoard(InputStream inputStream) throws IOException;
}
