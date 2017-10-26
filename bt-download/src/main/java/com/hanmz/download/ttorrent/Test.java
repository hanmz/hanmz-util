package com.hanmz.download.ttorrent;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

import java.io.File;
import java.net.InetAddress;

/**
 * *
 * Created by hanmz on 2017/10/10.
 */
public class Test {
  public static void main(String[] args) throws Exception {
    // First, instantiate the Client object.
    Client client = new Client(
      // This is the interface the client will listen on (you might need something
      // else than localhost here).
      InetAddress.getLocalHost(),

      // Load the torrent from the torrent file and use the given
      // output directory. Partials downloads are automatically recovered.
      SharedTorrent.fromFile(
        new File("/path/to/your.torrent"),
        new File("/path/to/output/directory")));

    // You can optionally set download/upload rate limits
    // in kB/second. Setting a limit to 0.0 disables rate
    // limits.
    client.setMaxDownloadRate(50.0);
    client.setMaxUploadRate(50.0);

    // At this point, can you either call download() to download the torrent and
    // stop immediately after...
    client.download();

    // Or call client.share(...) with a seed time in seconds:
    // client.share(3600);
    // Which would seed the torrent for an hour after the download is complete.

    // Downloading and seeding is done in background threads.
    // To wait for this process to finish, call:
    client.waitForCompletion();

    // At any time you can call client.stop() to interrupt the download.
  }
}
