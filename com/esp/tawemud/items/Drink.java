package com.esp.tawemud.items;

import com.esp.tawemud.TaweServer;
import java.io.PrintWriter;

public class Drink extends Food
{
	public Drink(TaweServer server)
	{
		super(server);
	}

	public Drink asDrink()
	{
		return this;
	}
}
