package com.netducks.botervene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VisitorManager extends Thread
{
	VisitorManager()
	{
		new Thread(this, "Visitor Manager").start();
	}

	@Override
	public void run()
	{
		do
		{
			try
			{
				synchronized(Main.visitor_journal)
				{
					boolean done;
					do
					{
						done = true;
						for(Visitor v : Main.visitor_journal)
						{
							if(v.isBanned())
							{
								if(v.isBanOver())
								{
									v.unban();
									Main.visitor_journal.remove(v);
									done = false;
									Main.visitor_journal_changed = true;
									break;
								}
							}
							else if(v.bot_level >= 1)
							{
								v.ban();
								Main.visitor_journal_changed = true;
							}
						}
					}
					while(!done);
				}
				if(Main.visitor_journal_changed)
				{
					FileWriter fw = new FileWriter(new File(Main.config.visitor_journal), false);
					fw.write(Main.gson.toJson(Main.visitor_journal));
					fw.close();
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				Thread.sleep(10000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
				break;
			}
		}
		while(true);
	}
}
