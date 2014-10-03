package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsName extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsName()
	{
		// Aliases
		this.addAliases("name");

		// Args
		this.addRequiredArg("new name");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.NAME.node));
		this.addRequirements(ReqHasFaction.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Arg
		String newName = this.arg(0);
		
		// MPerm
		if ( ! MPerm.getPermName().has(msender, msenderFaction, true)) return;
		
		// TODO does not first test cover selfcase?
		if (FactionColl.get().isNameTaken(newName) && ! MiscUtil.getComparisonString(newName).equals(msenderFaction.getComparisonName()))
		{
			msg("<b>That name is already taken");
			return;
		}

		ArrayList<String> errors = new ArrayList<String>();
		errors.addAll(FactionColl.get().validateName(newName));
		if (errors.size() > 0)
		{
			sendMessage(errors);
			return;
		}

		// Event
		EventFactionsNameChange event = new EventFactionsNameChange(sender, msenderFaction, newName);
		event.run();
		if (event.isCancelled()) return;
		newName = event.getNewName();

		// Apply
		msenderFaction.setName(newName);

		// Inform
		msenderFaction.msg("%s<i> changed your faction name to %s", msender.describeTo(msenderFaction, true), msenderFaction.getName(msenderFaction));
	}
	
}