package com.netducks.botervene;

import java.io.*;

public class LogMonitor extends Thread
{
	final private BufferedInputStream reader;

	LogMonitor() throws IOException
	{
		final File file = new File(Main.config.log_file);
		final FileWriter fw = new FileWriter(file, false);
		fw.close();
		reader = new BufferedInputStream(new FileInputStream(file));
		new Thread(this, "Log Monitor").start();
	}

	@Override
	public void run()
	{
		StringBuilder lineBuilder = new StringBuilder();
		do
		{
			try
			{
				if(reader.available() > 0)
				{
					int c = reader.read();
					if(c == '\n')
					{
						String line = lineBuilder.toString();
						int seperationAt = line.indexOf(' ');
						String path = line.substring(seperationAt + 1);
						if(Main.request_blacklist.has(path))
						{
							String ip = line.substring(0, seperationAt);
							System.out.println(ip + " sent a suspicious request.");
							Visitor v = Visitor.fromIp(ip);
							v.bot_level += Main.request_blacklist.get(path).getAsFloat();
						}
						lineBuilder = new StringBuilder();
					}
					else if(c == -1)
					{
						System.out.println("You can't empty the log while Botervene is monitoring it!");
						System.exit(0);
					}
					else if(c != '\r')
					{
						lineBuilder.append((char) c);
					}
				}
				else
				{
					Thread.sleep(500);
				}
			}
			catch(IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		while(!this.isInterrupted());
	}
}
