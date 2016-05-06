package socketInterface;

import java.io.*;
import java.net.Socket;

/**
 * A wrapper for writing and reading to a socket's streams.
 * Everything passed into one call of writeLine will come out of one call to readLine, regardless of newline characters
 * in the message. This class should be used at both ends of the communication process.
 */
public class InOut {
  public InOut(Socket s) throws IOException {
    in_ = new BufferedReader(new InputStreamReader(s.getInputStream()));
    out_ = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
  }

  /**
   * Reads a message from the socket, converting \\n to \n and \\ to \, until it comes across a raw \n.
   * @return a String that may have newlines
   * @throws IOException
   */
  public String readLine() throws IOException{
    int r = in_.read();
    boolean special = false;
    StringBuilder builder = new StringBuilder();
    while(r!=-1){
      // While the stream has not ended
      char ch = (char) r;
      if(ch == '\n'){
        // We've reached the end of a line.
        if(special){
          // Add delayed '\\'
          builder.append('\\');
        }
        return builder.toString();
      }
      else if(special){
        // We have a delayed '\\'
        if(ch == 'n'){
          // Found a converted newline, change back to a newline but keep going with the message.
          builder.append('\n');
        }
        else if(ch == '\\'){
          // Found a converted \, change back to \
          builder.append('\\');
        }
        else{
          // Delayed '\\' was not for a newline, add it back on and continue.
          builder.append('\\');
          builder.append(ch);
        }
        special = false;
      }
      else if(ch == '\\'){
        // Could mean a converted character is next, so delay adding this character and look ahead.
        special = true;
      }
      else{
        // No special cases, just add the character.
        builder.append(ch);
      }
      // Read onwards.
      r = in_.read();
    }
    throw new IOException("InputStream has finished.");
  }

  /**
   * Sends a message along a socket, converting \n to \\n and the ending with a \n
   * @param message a message to send along a socket. May contain newlines.
   * @throws IOException
   */
  public void writeLine(String message) throws IOException{
    for(int i = 0; i<message.length(); i++){
      // Check each character of the message.
      char c = message.charAt(i);
      if(c == '\n'){
        // Found a newline mid-message, convert it.
        out_.write("\\n");
      }
      else if(c == '\\'){
        // Found a \, convert it
        out_.write("\\\\");
      }
      else{
        out_.write(c);
      }
    }
    // Always interfaceFailed message with a newline.
    out_.write("\n");
    out_.flush();
  }

  /**
   * Close the communication chanel, rendering it now unuseable.
   * @throws IOException
   */
  public void close() throws IOException{
    in_.close();
    out_.close();
  }

  private BufferedReader in_;
  private BufferedWriter out_;
}
