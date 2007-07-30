/*
 * $Id: GroupRegistration.java,v 1.6 2007/07/30 13:49:50 sigtryggur Exp $
 * Created on May 30, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.marathon.presentation;

import is.idega.idegaweb.marathon.business.ConverterUtility;
import is.idega.idegaweb.marathon.data.Distance;
import is.idega.idegaweb.marathon.data.Participant;
import is.idega.idegaweb.marathon.data.Run;
import is.idega.idegaweb.marathon.util.IWMarathonConstants;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ejb.FinderException;
import com.idega.business.IBORuntimeException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.remotescripting.RemoteScriptHandler;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.Group;


/**
 * Last modified: $Date: 2007/07/30 13:49:50 $ by $Author: sigtryggur $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.6 $
 */
public class GroupRegistration extends RunBlock {

	private static final String PARAMETER_RUN = "prm_run";
	private static final String PARAMETER_DISTANCE = "prm_distance";
	private static final String PARAMETER_GROUP_NAME = "prm_group_name";
	private static final String PARAMETER_PARTICIPANT_NUMBER = "prm_participant_number";
	private static final String PARAMETER_PARTICIPANT = "prm_participant";
	private static final String PARAMETER_LIMIT_RUN_IDS="run_ids";

	private static final int ACTION_STEP_ONE = 1;
	private static final int ACTION_STEP_TWO = 2;
	private static final int ACTION_STEP_THREE = 3;
	private static final int ACTION_SAVE = 4;
	
	private Run run;
	private Distance distance;
	private String groupName;
	private String[] participants;
	private Map participantMap;
	private String runIds;

	public void main(IWContext iwc) throws Exception {
		if (!iwc.isInEditMode()) {
			switch (parseAction(iwc)) {
				case ACTION_STEP_ONE:
					stepOne(iwc);
					break;
				case ACTION_STEP_TWO:
					stepTwo(iwc);
					break;
				case ACTION_STEP_THREE:
					stepThree(iwc);
					break;
				case ACTION_SAVE:
					save(iwc);
					break;
			}
		}
	}
	
	private void stepOne(IWContext iwc) {
		Form form = new Form();
		form.addParameter(PARAMETER_ACTION, ACTION_STEP_TWO);
		form.maintainParameter(PARAMETER_PARTICIPANT_NUMBER);
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(getPhasesTable(1, 4, "run_reg.group_registration", "Group registration"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize("run_reg.group_information_text_step_1", "Information text 1...")), 1, row++);
		table.setHeight(row++, 6);
		
		DropdownMenu runDropdown = getRunDropdown(iwc);
		DropdownMenu distanceDropdown = (DistanceDropDownMenu) getStyledInterface(new DistanceDropDownMenu(PARAMETER_DISTANCE));
		distanceDropdown.setAsNotEmpty(localize("run_reg.must_select_distance", "You have to select a distance"));
		distanceDropdown.setWidth("130");
		
		Table choiceTable = new Table();
		choiceTable.setColumns(2);
		choiceTable.setCellpadding(2);
		choiceTable.setCellspacing(0);
		table.add(choiceTable, 1, row++);
		int iRow = 1;
				
		choiceTable.add(getHeader(localize(IWMarathonConstants.RR_PRIMARY_DD, "Run") + "/" + localize(IWMarathonConstants.RR_SECONDARY_DD, "Distance")), 1, iRow);
		choiceTable.add(runDropdown, 2, iRow);
		choiceTable.add(distanceDropdown, 2, iRow++);
		
		RemoteScriptHandler rsh = new RemoteScriptHandler(runDropdown, distanceDropdown);
		try {
			rsh.setRemoteScriptCollectionClass(GroupRunInputCollectionHandler.class);
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		add(rsh);
		
		TextInput groupNameField = (TextInput) getStyledInterface(new TextInput(PARAMETER_GROUP_NAME));
		groupNameField.setAsNotEmpty(localize("run_reg.group_name_not_empty", "Group name can not be empty"));

		choiceTable.setHeight(iRow++, 12);
		choiceTable.add(getHeader(localize("run_reg.group_name", "Group name")), 1, iRow);
		choiceTable.add(groupNameField, 2, iRow++);

		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("next", "Next")));
		table.setHeight(row++, 18);
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}
	
	private void stepTwo(IWContext iwc) {
		Form form = new Form();
		form.addParameter(PARAMETER_ACTION, ACTION_STEP_THREE);
		form.maintainParameter(PARAMETER_GROUP_NAME);
		form.maintainParameter(PARAMETER_RUN);
		form.maintainParameter(PARAMETER_DISTANCE);
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(getPhasesTable(2, 4, "run_reg.group_registration", "Group registration"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize("run_reg.group_information_text_step_2", "Information text 2...")), 1, row++);
		table.setHeight(row++, 6);
		
		Table choiceTable = new Table();
		choiceTable.setColumns(5);
		choiceTable.setCellpadding(2);
		choiceTable.setCellspacing(0);
		choiceTable.setWidth(1, "33%");
		choiceTable.setWidth(2, 6);
		choiceTable.setWidth(3, "33%");
		choiceTable.setWidth(4, 6);
		choiceTable.setWidth(5, "33%");
		choiceTable.setWidth(Table.HUNDRED_PERCENT);
		table.add(choiceTable, 1, row++);
		int iRow = 1;
		
		for (int a = 1; a <= 5; a++) {
			TextInput participant = (TextInput) getStyledInterface(new TextInput(PARAMETER_PARTICIPANT_NUMBER));
			participant.setWidth(Table.HUNDRED_PERCENT);
			participant.setAsIntegers(localize("run_reg.invalid_participant_number", "Invalid participant number"));
			if (this.participants != null && this.participants.length >= a) {
				participant.setContent(this.participants[a - 1]);
			}

			choiceTable.add(getHeader(localize("run_reg.partipant_nr", "Participant nr.") + " " + String.valueOf(a)), 1, iRow);
			choiceTable.add(participant, 1, iRow);
			
			if (a != 5) {
				choiceTable.setHeight(iRow++, 3);
			}
		}
		
		SubmitButton previous = (SubmitButton) getButton(new SubmitButton(localize("previous", "Previous")));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_ONE));
		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("next", "Next")));

		table.setHeight(row++, 18);
		table.add(previous, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}
	
	private void stepThree(IWContext iwc) {
		Form form = new Form();
		form.addParameter(PARAMETER_ACTION, ACTION_SAVE);
		form.maintainParameter(PARAMETER_GROUP_NAME);
		form.maintainParameter(PARAMETER_RUN);
		form.maintainParameter(PARAMETER_DISTANCE);
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(getPhasesTable(3, 4, "run_reg.group_registration_overview", "Group registration overview"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize("run_reg.group_information_text_step_3", "Information text 3...")), 1, row++);
		table.setHeight(row++, 12);
		
		table.setCellpaddingLeft(1, row, 3);
		table.add(getHeader(localize("run_reg.group_name", "Group name") + ":"), 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(getText(this.groupName), 1, row++);
		table.setHeight(row++, 12);
		
		Table runnerTable = new Table();
		runnerTable.setColumns(3);
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.add(getHeader(localize("run_reg.runner_name", "Runner name")), 1, 1);
		runnerTable.add(getHeader(localize("run_reg.run", "Run")), 2, 1);
		runnerTable.add(getHeader(localize("run_reg.distance", "Distance")), 3, 1);
		table.add(runnerTable, 1, row++);
		int runRow = 2;
		
		for (int a = 0; a < this.participants.length; a++) {
			String runner = this.participants[a];
			if (runner.length() > 0) {
				Participant participant = (Participant) this.participantMap.get(runner);
				
				if (participant != null) {
					runnerTable.add(new HiddenInput(PARAMETER_PARTICIPANT_NUMBER, runner), 1, runRow);
					runnerTable.add(new HiddenInput(PARAMETER_PARTICIPANT, participant.getPrimaryKey().toString()), 1, runRow);
					runnerTable.add(getText(participant.getUser().getName()), 1, runRow);
					runnerTable.add(getText(localize(this.run.getName(), this.run.getName())), 2, runRow);
					runnerTable.add(getText(localize(this.distance.getName(), this.distance.getName())), 3, runRow++);
				}
			}
		}

		SubmitButton previous = (SubmitButton) getButton(new SubmitButton(localize("previous", "Previous")));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_TWO));
		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("save", "Save")));

		table.setHeight(row++, 18);
		table.add(previous, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}
	
	private void save(IWContext iwc) {
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		int row = 1;
		
		try {
			String[] runners = iwc.getParameterValues(PARAMETER_PARTICIPANT);
			getRunBusiness(iwc).addParticipantsToGroup(runners, this.groupName);
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}

		table.add(getPhasesTable(4, 4, "run_reg.group_registration_done", "Group registration completed"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize("run_reg.group_information_text_step_4", "Information text 4...")), 1, row++);
		table.setHeight(row++, 18);
		
		table.add(getText(localize("run_reg.group_registered_for", "You have registered for the team competition in the ")), 1, row);
		table.add(getHeader(localize(this.run.getName(), this.run.getName())), 1, row++);
		table.setHeight(row++, 16);

		table.add(getText(localize("run_reg.group_registered_name", "Your team is registered under the name ")), 1, row);
		table.add(getHeader(this.groupName), 1, row++);
		table.setHeight(row++, 16);

		table.add(getText(localize("run_reg.group_registered_distance", "Each team member has chosen to run ")), 1, row);
		table.add(getHeader(localize(this.distance.getName(), this.distance.getName())), 1, row++);
		table.setHeight(row++, 16);

		table.add(getText(localize("run_reg.group_registered_runners", "The runners in your team are") + ":"), 1, row++);
		for (int a = 0; a < this.participants.length; a++) {
			String runner = this.participants[a];
			if (runner.length() > 0) {
				Participant participant = (Participant) this.participantMap.get(runner);
				
				if (participant != null) {
					table.add(getHeader(participant.getParticipantNumber() + " - " + participant.getUser().getName()), 1, row++);
				}
			}
		}

		add(table);
	}

	protected int parseAction(IWContext iwc) {
		int action = ACTION_STEP_ONE;
		
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		
		if (iwc.isParameterSet(PARAMETER_RUN)) {
			try {
				this.run = ConverterUtility.getInstance().convertGroupToRun(new Integer(iwc.getParameter(PARAMETER_RUN)));
			}
			catch (FinderException fe) {
				fe.printStackTrace();
			}
		}
		if (iwc.isParameterSet(PARAMETER_DISTANCE)) {
			try {
				this.distance = ConverterUtility.getInstance().convertGroupToDistance(new Integer(iwc.getParameter(PARAMETER_DISTANCE)));
			}
			catch (FinderException fe) {
				fe.printStackTrace();
			}
		}
		if (iwc.isParameterSet(PARAMETER_GROUP_NAME)) {
			this.groupName = iwc.getParameter(PARAMETER_GROUP_NAME);
		}
		this.participants = iwc.getParameterValues(PARAMETER_PARTICIPANT_NUMBER);
		
		int numberOfParticipants = 0;
		if (action == ACTION_STEP_TWO || action == ACTION_SAVE) {
			try {
				Group year = getRunBusiness(iwc).getRunGroupOfTypeForGroup(distance, IWMarathonConstants.GROUP_TYPE_RUN_YEAR);
				Collection participantsByYearAndTeamName = getRunBusiness(iwc).getParticipantsByYearAndTeamName(year.getPrimaryKey(), groupName);
				if (participantsByYearAndTeamName != null && !participantsByYearAndTeamName.isEmpty()) {
					action = ACTION_STEP_ONE;
					getParentPage().setAlertOnLoad(localize("run_reg.teamname_already_exists", "This teamname already exists in this run: ") + groupName);
					return action;
				}
			}
			catch (FinderException e) {
				//No participants found with this teamname in this year of the run, so it is OK to create new team with this name
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		if (action == ACTION_STEP_THREE || action == ACTION_SAVE) {
			this.participantMap = new HashMap();
			for (int i = 0; i < this.participants.length; i++) {
				String participant = this.participants[i];
				if (participant.length() > 0) {
					try {
						Participant runner = getRunBusiness(iwc).getParticipantByDistanceAndParticipantNumber(this.distance.getPrimaryKey(), Integer.parseInt(participant));
						if (runner.getRunGroupName() != null) {
							action = ACTION_STEP_TWO;
							getParentPage().setAlertOnLoad(localize("run_reg.participant_already_in_a_group", "Participant is already in another group: ") + participant);
							return action;
						}
						this.participantMap.put(participant, runner);
						numberOfParticipants++;
					}
					catch (RemoteException re) {
						throw new IBORuntimeException(re);
					}
					catch (FinderException fe) {
						action = ACTION_STEP_TWO;
						getParentPage().setAlertOnLoad(localize("run_reg.participant_not_found_in_distance", "Participant not found in distance: ") + participant);
						return action;
					}
				}
			}
		}
		
		if (action == ACTION_STEP_THREE && numberOfParticipants < 3) {
			action = ACTION_STEP_TWO;
			getParentPage().setAlertOnLoad(localize("run_reg.must_select_three", "You have to select a minimum of three to form a team."));
		}
		if(iwc.isParameterSet(PARAMETER_LIMIT_RUN_IDS)){
			String runIds = iwc.getParameter(PARAMETER_LIMIT_RUN_IDS);
			setRunIds(runIds);	
		}
		return action;
	}

	protected ActiveRunDropDownMenu getRunDropdown(IWContext iwc) {
		ActiveRunDropDownMenu runDropdown = null;
		String[] constrainedRunIds = getRunIdsArray();
		if(constrainedRunIds==null){
			runDropdown = (ActiveRunDropDownMenu) getStyledInterface(new ActiveRunDropDownMenu(PARAMETER_RUN));
		}
		else{
			runDropdown = (ActiveRunDropDownMenu) getStyledInterface(new ActiveRunDropDownMenu(PARAMETER_RUN, null, constrainedRunIds));
		}
		
		runDropdown.setAsNotEmpty(localize("run_reg.must_select_run", "You have to select a run"));
		return runDropdown;
	}
	
	public String getRunIds() {
		return runIds;
	}

	public void setRunIds(String runIds) {
		this.runIds = runIds;
	}
	
	public String[] getRunIdsArray(){
		String runIds = getRunIds();
		if(runIds!=null){
			if(runIds.indexOf(",")!=-1){
				StringTokenizer tokenizer = new StringTokenizer(runIds,",");
				List list = new ArrayList();
				while(tokenizer.hasMoreElements()){
					list.add(tokenizer.nextElement());
				}
				return (String[]) list.toArray(new String[0]);
			}
			else{
				String[] array = {runIds};
				return array;
			}
		}
		
		return null;
	}
}