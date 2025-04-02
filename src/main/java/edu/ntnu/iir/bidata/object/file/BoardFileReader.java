package edu.ntnu.iir.bidata.object.file;

import edu.ntnu.iir.bidata.object.Board;
import java.io.IOException;

public interface BoardFileReader {
  Board readBoard(String filePath) throws IOException;
}
