package is.idega.idegaweb.marathon.presentation;

import is.idega.idegaweb.marathon.business.RunBusiness;
import is.idega.idegaweb.marathon.util.IWMarathonConstants;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;


import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.TimestampInput;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;

public class RunYearEditor extends RunBlock {
	
	private static final String PARAMETER_ACTION = "marathon_prm_action";
	private static final String PARAMETER_MARATHON_PK = "prm_run_pk";
	private static final String PARAMETER_MARATHON_YEAR_PK = "prm_run_year_pk";
	private static final String PARAMETER_YEAR = "prm_year";
	private static final String PARAMETER_RUN_DATE = "prm_run_date";
	private static final String PARAMETER_LAST_REGISTRATION_DATE = "prm_last_registration_date";
	
	private static final String METADATA_RUN_DATE = "run_date";
	private static final String METADATA_LAST_REGISTRATION_DATE = "last_registration_date";
	
	private static final int ACTION_VIEW = 1;
	private static final int ACTION_EDIT = 2;
	private static final int ACTION_NEW = 3;
	private static final int ACTION_SAVE_NEW = 4;
	private static final int ACTION_SAVE_EDIT = 5;
	private static final int ACTION_DELETE = 6;
	

	public void main(IWContext iwc) throws Exception {
		init(iwc);
	}
	
	protected void init(IWContext iwc) throws Exception {
		switch (parseAction(iwc)) {
			case ACTION_VIEW:
				showList(iwc);
				break;

			case ACTION_NEW:
				showNewEditor(iwc);
				break;

			case ACTION_EDIT:
				showEditor(iwc);
				break;

			case ACTION_SAVE_NEW:
				saveNew(iwc);
				showList(iwc);
				break;

			case ACTION_SAVE_EDIT:
				saveEdit(iwc);
				showList(iwc);
				break;

			case ACTION_DELETE:
				//COULD-DO: implement delete functions if needed
				showList(iwc);
				break;
		}
	}

	private int parseAction(IWContext iwc) {
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			return Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		return ACTION_VIEW;
	}
	
	public void showList(IWContext iwc) throws RemoteException {
		Form form = new Form();
				
		Table2 table = new Table2();
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);
				
		Collection runs = getRunBusiness(iwc).getRuns();
		Iterator runIt = runs.iterator();
		DropdownMenu runDropDown = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_MARATHON_PK)); 
		runDropDown.addMenuElement("", localize("select_run","Select run"));
		while (runIt.hasNext()) {
			Group run = (Group)runIt.next();
			runDropDown.addMenuElement(run.getPrimaryKey().toString(), localize(run.getName(),run.getName()));
		}
		runDropDown.setToSubmit();
		
		Collection years = null;
		if (iwc.isParameterSet(PARAMETER_MARATHON_PK)) {
			String runID = iwc.getParameter(PARAMETER_MARATHON_PK);
			runDropDown.setSelectedElement(runID);
			Group selectedRun = getRunBusiness(iwc).getRunGroupByGroupId(Integer.valueOf(runID));
		    String[] types = {IWMarathonConstants.GROUP_TYPE_RUN_YEAR};
			years = selectedRun.getChildGroups(types,true); 
		}
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
		cell.add(runDropDown);
		group.createRow().createCell().setHeight("20");
		
		if (iwc.isParameterSet(PARAMETER_MARATHON_PK)) {
			row = group.createRow();
			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("name", "Name")));
		}
				
		group = table.createBodyRowGroup();
		int iRow = 1;
		
		if (years != null) {
			Iterator iter = years.iterator();
			Group run;
			
			while (iter.hasNext()) {
				row = group.createRow();
				run = (Group) iter.next();
				try {
					Link edit = new Link(getEditIcon(localize("edit", "Edit")));
					edit.addParameter(PARAMETER_MARATHON_YEAR_PK, run.getPrimaryKey().toString());
					edit.addParameter(PARAMETER_ACTION, ACTION_EDIT);
								
					cell = row.createCell();
					cell.add(new Text(run.getName()));
					row.createCell().add(edit);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
				iRow++;
			}
		}
		form.add(table);
		form.add(new Break());
		if (iwc.isParameterSet(PARAMETER_MARATHON_PK)) {
			SubmitButton newLink = (SubmitButton) getButton(new SubmitButton(localize("new_year", "New year"), PARAMETER_ACTION, String.valueOf(ACTION_NEW)));
			form.add(newLink);
		}
		add(form);
	}

	public void showNewEditor(IWContext iwc) throws java.rmi.RemoteException {
		String runID = iwc.getParameter(PARAMETER_MARATHON_PK);
		CreateYearForm form = new CreateYearForm(runID);
		add(form);
	}

	public void showEditor(IWContext iwc) throws java.rmi.RemoteException {
		String yearID = iwc.getParameter(PARAMETER_MARATHON_YEAR_PK);
		Form form = new Form();
		TextInput year = new TextInput(PARAMETER_YEAR);
		DateInput runDate = new DateInput(PARAMETER_RUN_DATE);
		TimestampInput lastRegistrationDate = new TimestampInput(PARAMETER_LAST_REGISTRATION_DATE);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label label = new Label(localize("run_tab.year", "Year"), year);
		layer.add(label);
		layer.add(year);
		form.add(layer);
		form.add(new Break());
		
		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label();
		label.setLabel(localize("run_tab.run_date", "Run date"));
		layer.add(label);
		layer.add(runDate);
		form.add(layer);
		form.add(new Break());
		
		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("run_tab.last_registration_date", "Last registration date"), lastRegistrationDate);
		layer.add(label);
		layer.add(lastRegistrationDate);
		form.add(layer);
		form.add(new Break());

		SubmitButton save = (SubmitButton) getButton(new SubmitButton(localize("save", "Save"), PARAMETER_ACTION, String.valueOf(ACTION_SAVE_EDIT)));
		SubmitButton cancel = (SubmitButton) getButton(new SubmitButton(localize("cancel", "Cancel"), PARAMETER_ACTION, String.valueOf(ACTION_VIEW)));

		form.add(save);
		form.add(cancel);
		
		if (yearID != null) {
			Group selectedYear = getRunBusiness(iwc).getRunGroupByGroupId(Integer.valueOf(yearID.toString()));
			year.setValue(selectedYear.getName());
			year.setDisabled(true);
			String runDateString = selectedYear.getMetaData(METADATA_RUN_DATE);
			if (runDateString != null) { 
				runDate.setDate(new IWTimestamp(runDateString).getDate());
			}
			String lastRegistrationString = selectedYear.getMetaData(METADATA_LAST_REGISTRATION_DATE);
			if (lastRegistrationString != null) { 
				lastRegistrationDate.setTimestamp(new IWTimestamp(lastRegistrationString).getTimestamp());
			}
		}
		add(form);
	}
	
	public void saveNew(IWContext iwc) throws java.rmi.RemoteException {
		String runID = iwc.getParameter(PARAMETER_MARATHON_PK);
		getRunBiz(iwc).createNewGroupYear(iwc, runID);
	}
	
	private void saveEdit(IWContext iwc) throws java.rmi.RemoteException {
		System.out.println("saveYear");
	}

	private RunBusiness getRunBiz(IWContext iwc) {
		RunBusiness business = null;
		try {
			business = (RunBusiness) IBOLookup.getServiceInstance(iwc, RunBusiness.class);
		} catch (IBOLookupException e) {
			business = null;
		}
		return business;
	}
}
