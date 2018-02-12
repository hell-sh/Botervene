package com.netducks.botervene;

import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration
{
	static final File file = new File("config.json");
	static final String cf_endpoint = "https://api.cloudflare.com/client/v4/";
	static final String endpoint = "https://botervene.nex.li/";
	public String log_file = "logs/botervene.log";
	public String visitor_journal = "visitors.json";
	public String cf_email = "";
	public String cf_key = "";
	public String firewall_action = "challenge";
	public int ban_duration = 3600;

	void save() throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(this));
		writer.flush();
		writer.close();
	}
}
