package com.netducks.botervene;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Visitor
{
	public String ip;
	public float bot_level;
	public String ban_id;
	public int ban_start;

	private Visitor(String ip)
	{
		this.ip = ip;
		synchronized(Main.visitor_journal)
		{
			Main.visitor_journal.add(this);
			Main.visitor_journal_changed = true;
		}
	}

	public static Visitor fromIp(String ip)
	{
		synchronized(Main.visitor_journal)
		{
			for(Visitor v : Main.visitor_journal)
			{
				if(v.ip.equals(ip))
				{
					return v;
				}
			}
		}
		return new Visitor(ip);
	}

	public boolean isBanned()
	{
		return this.ban_id != null;
	}

	public boolean isBanOver()
	{
		return this.ban_start + Main.config.ban_duration < System.currentTimeMillis() / 1000L;
	}

	public void ban() throws IOException
	{
		URL url = new URL(Configuration.cf_endpoint + "user/firewall/access_rules/rules");
		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection) con;
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		JsonObject postData = new JsonObject();
		postData.addProperty("mode", Main.config.firewall_action);
		JsonObject configuration = new JsonObject();
		configuration.addProperty("target", "ip");
		configuration.addProperty("value", this.ip);
		postData.add("configuration", configuration);
		postData.addProperty("notes", "Botervene Auto Ban - Bot Level " + this.bot_level);
		byte[] out = postData.toString().getBytes(StandardCharsets.UTF_8);
		http.setRequestProperty("Accept", "application/json");
		http.setRequestProperty("Content-Type", "application/json");
		http.setRequestProperty("User-Agent", "Botervene (Java)");
		http.setRequestProperty("X-Auth-Email", Main.config.cf_email);
		http.setRequestProperty("X-Auth-Key", Main.config.cf_key);
		http.connect();
		try(OutputStream os = http.getOutputStream())
		{
			os.write(out);
		}
		String response = new Scanner(http.getInputStream()).useDelimiter("\\A").next();
		System.out.println(ip + " has been banned.");
		this.ban_id = new JsonParser().parse(response).getAsJsonObject().get("result").getAsJsonObject().get("id").getAsString();
		this.ban_start = (int) (System.currentTimeMillis() / 1000L);
	}

	public void unban() throws IOException
	{
		URL url = new URL(Configuration.cf_endpoint + "user/firewall/access_rules/rules/" + this.ban_id);
		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection) con;
		http.setRequestMethod("DELETE");
		http.setRequestProperty("User-Agent", "Botervene (Java)");
		http.setRequestProperty("X-Auth-Email", Main.config.cf_email);
		http.setRequestProperty("X-Auth-Key", Main.config.cf_key);
		http.connect();
		http.getInputStream();
		this.bot_level = 0;
		this.ban_id = null;
		System.out.println(ip + " is no longer banned.");
	}
}
