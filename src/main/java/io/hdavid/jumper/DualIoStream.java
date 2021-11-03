package io.hdavid.jumper;

import lombok.AllArgsConstructor;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

@AllArgsConstructor
public class DualIoStream implements Runnable {
    private final Socket src;
    private final Socket dst;

    @Override
    public void run() {
        try {
            InputStream is = src.getInputStream();
            OutputStream os = dst.getOutputStream();
            int readedByte = 0;
            while ((readedByte = is.read()) >= 0)
                os.write(readedByte);
        } catch (SocketException se) {
          if (!se.getMessage().contains("Socket closed"))
              se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try { dst.getOutputStream().flush(); } catch (Exception IGNORED) { }
        try { dst.close(); } catch (Exception IGNORED) { }
    }
}
