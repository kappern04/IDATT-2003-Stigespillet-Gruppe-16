package edu.ntnu.iir.bidata.object.file;

import edu.ntnu.iir.bidata.object.Board;
import java.io.IOException;
import java.io.InputStream;

public interface BoardFileReader {
  Board readBoard(InputStream inputStream) throws IOException;
}
