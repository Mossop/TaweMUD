package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import java.text.SimpleDateFormat;

/**
 * Returns a list of the pronouns for a mobile.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class GetPronouns extends SingleAction
{
	/**
	 * The mobile.
	 *
	 * @required
	 */
	public String mobile;
	/**
	 * A variable to hold the result.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject thisitem = variables.getObject(mobile,server);
		if ((thisitem!=null)&&(thisitem.asMobile()!=null))
		{
			String result="";
			String pn=thisitem.asMobile().getPronoun("me");
			if (pn!=null)
			{
				result+="@+YMe@*   : "+pn+"@/\n";
			}
			pn=thisitem.asMobile().getPronoun("it");
			if (pn!=null)
			{
				result+="@+YIt@*   : "+pn+"@/\n";
			}
			pn=thisitem.asMobile().getPronoun("him");
			if (pn!=null)
			{
				result+="@+YHim@*  : "+pn+"@/\n";
			}
			pn=thisitem.asMobile().getPronoun("her");
			if (pn!=null)
			{
				result+="@+YHer@*  : "+pn+"@/\n";
			}
			pn=thisitem.asMobile().getPronoun("them");
			if (pn!=null)
			{
				result+="@+YThem@* : "+pn+"@/\n";
			}
			variables.setVariable(dest,result);
		}
		return false;
	}
}