package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Emote;
import java.text.SimpleDateFormat;

/**
 * Returns some information about a fixed emote.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class GetEmoteInfo extends TestAction
{
	/**
	 * The emote to find out about.
	 *
	 * @required
	 */
	public String emote;
	/**
	 * The variable to hold the result.
	 *
	 * @required
	 */
	public String dest;

	public boolean doTest(TaweServer server, Variables variables)
	{
		Emote thisone=server.getWorld().findEmote(variables.parseString(emote));
		if (thisone!=null)
		{
			String result="Information about the "+thisone.getName()+" Action:@/@/\n";
			Variables vars = new Variables();
			vars.setVariable("$n","<caller>");
			vars.setVariable("$himselfcaller","<himself/herself>");
			vars.setVariable("$hecaller","<he/she>");
			vars.setVariable("$hiscaller","<his/her>");
			vars.setVariable("$himcaller","<him/her>");
			vars.setVariable("$herscaller","<his/hers>");
			vars.setVariable("$t","<target>");
			vars.setVariable("$himtarget","<him/her>");
			vars.setVariable("$histarget","<his/her>");
			vars.setVariable("$himselftarget","<himself/herself>");
			vars.setVariable("$hetarget","<he/she>");
			vars.setVariable("$herstarget","<his/hers>");
			vars.setVariable("$s","<text>");
			result+="@+YFlags@*  : ";
			if (thisone.getAllFlag())
			{
				result+="all,";
			}
			if (thisone.getSingleFlag())
			{
				result+="single,";
			}
			if (thisone.getFarFlag())
			{
				result+="far,";
			}
			if (thisone.getWorldFlag())
			{
				result+="world,";
			}
			if (thisone.getViolentFlag())
			{
				result+="violent,";
			}
			if (result.endsWith(","))
			{
				result=result.substring(0,result.length()-1);
			}
			result+="@/\n";
			if (thisone.getAllFlag()||thisone.getWorldFlag())
			{
				result+="@+YAll@*    : "+vars.parseString(thisone.getAll().toString())+"@/\n";
				result+="@+YMe@*     : "+vars.parseString(thisone.getMe().toString())+"@/\n";
			}
			if (thisone.getSingleFlag())
			{
				result+="@+YSender@* : "+vars.parseString(thisone.getSender().toString())+"@/\n";
				result+="@+YTarget@* : "+vars.parseString(thisone.getTarget().toString())+"@/\n";
				result+="@+YOthers@* : "+vars.parseString(thisone.getOthers().toString())+"@/\n";
			}
			variables.setVariable(dest,result);
			return true;
		}
		else
		{
			return false;
		}
	}
}