package com.netducks.botervene;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
	public static final Gson gson = new Gson();
	public final static ArrayList<Visitor> visitor_journal = new ArrayList<>();
	public static boolean visitor_journal_changed = false;
	public static Configuration config;
	public static JsonObject request_blacklist;

	public static void main(String[] args) throws IOException
	{
		System.out.println("Botervene by Netducks");
		System.out.println("Copyright (c) 2018, Netducks");
		System.out.println("https://github.com/netducks/botervene");
		System.out.println("");
		if(Configuration.file.exists())
		{
			config = gson.fromJson(new BufferedReader(new FileReader(Configuration.file)), Configuration.class);
			config.firewall_action = config.firewall_action.toLowerCase();
			if(config.firewall_action.equals("whitelist"))
			{
				System.out.println("You want me to whitelist bots?! Are you crazy?!");
				return;
			}
			config.save();
			if(config.cf_email.equals("") || config.cf_key.equals(""))
			{
				System.out.println("Please configure Botervene and then run it again.");
				System.out.println("Read the CONFIG.md for more information.");
				return;
			}
		}
		else
		{
			config = new Configuration();
			config.save();
			System.out.println("The default configuration file has been generated into config.json.");
			System.out.println("Please configure Botervene and then run it again.");
			System.out.println("Read the CONFIG.md for more information.");
			return;
		}
		System.out.print("Downloading Request Blacklist...");
		URL url = new URL(Configuration.endpoint + "request_blacklist.json");
		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection) con;
		http.setRequestProperty("Accept", "application/json");
		http.setRequestProperty("User-Agent", "Botervene (Java)");
		http.connect();
		String response = new Scanner(http.getInputStream()).useDelimiter("\\A").next();
		request_blacklist = new JsonParser().parse(response).getAsJsonObject();
		System.out.println(" Done.");
		if(new File(config.visitor_journal).exists())
		{
			System.out.print("Loading Visitor Journal...");
			synchronized(visitor_journal)
			{
				for(JsonElement e : new JsonParser().parse(new Scanner(new FileInputStream(config.visitor_journal)).useDelimiter("\\A").next()).getAsJsonArray())
				{
					visitor_journal.add(gson.fromJson(e, Visitor.class));
				}
			}
			System.out.println(" Done.");
		}
		else
		{
			visitor_journal_changed = true;
		}
		System.out.print("Starting Threads...");
		new VisitorManager();
		new LogMonitor();
		System.out.println(" Done.");
		System.out.println("");
	}
}
