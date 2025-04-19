package edu.ntnu.iir.bidata.file;

import edu.ntnu.iir.bidata.model.Board;
import java.io.IOException;
import java.io.InputStream;

public interface BoardFileReader {
  Board readBoard(InputStream inputStream) throws IOException;
}
