package com.esp.tawemud.plugins;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiPluginInterface extends Remote
{
	public boolean authenticate(String name, String password) throws RemoteException;

	public String getName(String name) throws RemoteException;

	public short getLevel(String name) throws RemoteException;

	public boolean isWiz(String name) throws RemoteException;

	public boolean isDPower(String name) throws RemoteException;

	public boolean isPower(String name) throws RemoteException;

	public void sendMessage(String message) throws RemoteException;
	
	public void rebootCode() throws RemoteException;
	
	public void rebootWorld() throws RemoteException;
	
	public void rebootInfo() throws RemoteException;
	
	public void rebootEmotes() throws RemoteException;
	
	public void haltServer() throws RemoteException;
}
