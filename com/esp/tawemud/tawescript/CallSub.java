package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import java.util.StringTokenizer;

/**
 * Calls a subroutine.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class CallSub extends TestAction
{
	/**
	 * The name of the subroutine.
	 *
	 * @required
	 */
	public String name;
	/**
	 * Inputs to the subroutine.
	 */
	public String inputs;
	/**
	 * Variables to hold outputs from the subroutine.
	 */
	public String outputs;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject owner = getOwner();
		Subroutine thissub = owner.findSubroutine(variables.parseString(name));
		if (thissub!=null)
		{
			Variables vars = new Variables();
			vars.setVariable("$owner",thissub.getOwner());
			StringTokenizer subargs = new StringTokenizer(thissub.getInputs(),",");
			StringTokenizer args = new StringTokenizer(inputs,",");
			while (subargs.hasMoreTokens())
			{
				if (args.hasMoreTokens())
				{
					String token = args.nextToken();
					CodeableObject obj = variables.getObject(token,server);
					if (obj!=null)
					{
						vars.setVariable(subargs.nextToken(),obj);
					}
					else
					{
						vars.setVariable(subargs.nextToken(),variables.parseString(token));
					}
				}
				else
				{
					vars.setVariable(subargs.nextToken(),"");
				}
			}
			boolean result = thissub.run(server,vars);
			subargs = new StringTokenizer(thissub.getOutputs(),",");
			args = new StringTokenizer(outputs,",");
			while (args.hasMoreTokens())
			{
				String thisarg=args.nextToken();
				if (subargs.hasMoreTokens())
				{
					String token = subargs.nextToken();
					CodeableObject obj = vars.getObject(token,server);
					if (obj!=null)
					{
						variables.setVariable(thisarg,obj);
					}
					else
					{
						variables.setVariable(thisarg,vars.parseString(token));
					}
				}
				else
				{
					variables.setVariable(thisarg,"");
				}
			}
			return result;
		}
		else
		{
			return false;
		}
	}
}
