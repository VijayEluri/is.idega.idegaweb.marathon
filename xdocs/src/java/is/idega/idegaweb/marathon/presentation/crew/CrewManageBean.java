package is.idega.idegaweb.marathon.presentation.crew;

import is.idega.idegaweb.marathon.IWBundleStarter;
import is.idega.idegaweb.marathon.business.ConverterUtility;
import is.idega.idegaweb.marathon.business.RunBusiness;
import is.idega.idegaweb.marathon.data.Participant;
import is.idega.idegaweb.marathon.data.Year;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.8 $
 *
 * Last modified: $Date: 2008/07/23 22:27:25 $ by $Author: palli $
 *
 */
public class CrewManageBean {

	private boolean wizardMode = false;
	private boolean crewViewMode = false;
	private String crewLabelForOwner;
	private String runIdForOwner;
	private List runsChoices;
	
	private CrewEditWizardBean crewEditWizardBean;

	public boolean isWizardMode() {
		return wizardMode;
	}

	public void setWizardMode(boolean wizardMode) {
		
		if(wizardMode)
			crewViewMode = false;
		this.wizardMode = wizardMode;
	}
	
	public void startNewCrewRegistration() {
		
		setWizardMode(true);
		getCrewEditWizardBean().setMode(CrewEditWizardBean.newCrewMode);
	}

	public CrewEditWizardBean getCrewEditWizardBean() {
		return crewEditWizardBean;
	}

	public void setCrewEditWizardBean(CrewEditWizardBean crewEditWizardBean) {
		this.crewEditWizardBean = crewEditWizardBean;
	}

	public String getCrewManageHeaderValue() {
		
		IWContext iwc = IWContext.getIWContext(FacesContext.getCurrentInstance());
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		
		String header;
		
		if(getCrewEditWizardBean().isNewCrewMode())
			header = iwrb.getLocalizedString("crew.header.newCrew", "Create new crew");
		else if (getCrewEditWizardBean().isEditCrewMode())
			header = iwrb.getLocalizedString("crew.header.editCrew", "Edit crew");
		else
			header = iwrb.getLocalizedString("crew.header.manageCrew", "Manage crew");
		
		return header;
	}

	public String getCrewLabelForOwner() {
		
		if(crewLabelForOwner == null && getCrewEditWizardBean().getCrewParticipant() != null && getCrewEditWizardBean().getCrewParticipant().isCrewOwner())
			crewLabelForOwner = getCrewEditWizardBean().getCrewParticipant().getCrewLabel();
		
		return crewLabelForOwner;
	}

	public void setCrewLabelForOwner(String crewLabel) {
		
		this.crewLabelForOwner = crewLabel;
	}

	public String getRunIdForOwner() {
		
		if(runIdForOwner == null && getCrewEditWizardBean().getCrewParticipant() != null && getCrewEditWizardBean().getCrewParticipant().isCrewOwner())
			runIdForOwner = String.valueOf(getCrewEditWizardBean().getCrewParticipant().getParticipant().getRunTypeGroupID());
		
		return runIdForOwner;
	}

	public void setRunIdForOwner(String runIdForOwner) {
		this.runIdForOwner = runIdForOwner;
	}
	
	public List getRuns() {

		if(runsChoices == null) {
			
			IWContext iwc = IWContext.getIWContext(FacesContext.getCurrentInstance());
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
			
			runsChoices = new ArrayList();
			
			SelectItem selectItem = new SelectItem();
			
			selectItem.setValue(CoreConstants.EMPTY);
			selectItem.setLabel(iwrb.getLocalizedString("run_year_ddd.select_run", "Select run..."));
			runsChoices.add(selectItem);
			
			String[] constrainedRunIds = null;
			
//			copied from ActiveRunDropDownMenu
			IWTimestamp thisYearStamp = IWTimestamp.RightNow();
			String yearString = String.valueOf(thisYearStamp.getYear());
			IWTimestamp nextYearStamp = IWTimestamp.RightNow();
			nextYearStamp.addYears(1);
			String nextYearString = String.valueOf(nextYearStamp.getYear());
			
			try {
				Collection runs = getCrewEditWizardBean().getRunBusiness().getRuns();
				
				if (runs != null) {
					
					User user = iwc.getCurrentUser();
					RunBusiness runBusiness = getCrewEditWizardBean().getRunBusiness();
					
					for (Iterator iterator = runs.iterator(); iterator.hasNext();) {
						
//						copied from ActiveRunDropDownMenu
						Group run = (Group) iterator.next();
						String runnerYearString = yearString;
						String runId = run.getPrimaryKey().toString();
						
						boolean show = false;
						boolean finished = true;
						Map yearMap = getCrewEditWizardBean().getRunBusiness().getYearsMap(run);
						Year year = (Year) yearMap.get(yearString);
						if (year != null && year.getLastRegistrationDate() != null) {
							IWTimestamp lastRegistrationDate = new IWTimestamp(year.getLastRegistrationDate());
							if (thisYearStamp.isEarlierThan(lastRegistrationDate)) {
								finished = false;
								show = true;
							}
						}
						Year nextYear = (Year) yearMap.get(nextYearString);
						if (finished && nextYear != null && nextYear.getLastRegistrationDate() != null && thisYearStamp.isEarlierThan(new IWTimestamp(nextYear.getLastRegistrationDate()))) {
							runnerYearString = nextYearString;
							show = true;
						}
						
						if(constrainedRunIds!=null){
							boolean match = false;
							for (int i = 0; i < constrainedRunIds.length; i++) {
								String constrainedId = constrainedRunIds[i];
								if(constrainedId.equals(runId)){
									match=true;
								}
							}
							if(!match){
								show=false;
							}
						}
						
						if (show) {
							
							if(runBusiness.isRegisteredInRun(runnerYearString, run, user)) {
							
								selectItem = new SelectItem();
								
								selectItem.setValue(runId);
								selectItem.setLabel(iwrb.getLocalizedString(run.getName(), run.getName()));
								runsChoices.add(selectItem);
							}
						}
					}
				}
				
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Exception while retrieving runs", e);
			}
		}
		
		return runsChoices;
	}
	
	protected void validateCrewLabel(FacesContext context, String crewLabel, Integer runId) {
	
		IWTimestamp ts = IWTimestamp.RightNow();
	    Integer currentYear = new Integer(ts.getYear());
	    
	    RunBusiness runBusiness = getCrewEditWizardBean().getRunBusiness();
	    boolean crewLabelExists = false;
		try {
			crewLabelExists = runBusiness.isCrewLabelAlreadyExistsForRun(runId.intValue(), currentYear.intValue(), crewLabel);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	    
	    if(crewLabelExists) {
	    	
	    	IWContext iwc = IWContext.getIWContext(context);
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
	    	
	    	FacesMessage message = new FacesMessage(iwrb.getLocalizedString("crew.manage.labelExists", "Crew already exists with such label"));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(null, message);
	    }
	}
	
	public void validateRunSelection(FacesContext context, UIComponent toValidate, Object value) {
		
//		will be caught by required
		if(value == null)
			return;
		
		IWContext iwc = IWContext.getIWContext(context);
		
		try {
			Integer runId = new Integer((String)value);
			
			User ownerUser = iwc.getCurrentUser();
			RunBusiness runBusiness = getCrewEditWizardBean().getRunBusiness();
			Group runGroup = runBusiness.getRunGroupByGroupId(runId);
			Group yearGroup = ConverterUtility.getInstance().convertGroupToRun(runGroup).getCurrentRegistrationYear();
			
			Participant participant = null;
			
			try {
				participant = runBusiness.getParticipantByRunAndYear(ownerUser, runGroup, yearGroup);
				
			} catch (FinderException e) {
				// TODO: this happens, when nothing found
			}
			
			if(participant == null)
//				will be caught by createCrew()
				return;
			
			CrewParticipant crewParticipant = new CrewParticipant(participant);

			if(crewParticipant.isParticipatingInCrew()) {
//				already registered or invited to a crew				

				IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
				
				String messageText;
				
				if(crewParticipant.getCrewParticipantRole() == CrewParticipant.CREW_PARTICIPANT_ROLE_OWNER)
					messageText = iwrb.getLocalizedString("crew.manage.validateRun.isOwner", "You have already created and registered to the crew labeled \"{0}\" for this run. You may edit crew from crews list, and/or delete it.");
				else if(crewParticipant.getCrewParticipantRole() == CrewParticipant.CREW_PARTICIPANT_ROLE_MEMBER)
					messageText = iwrb.getLocalizedString("crew.manage.validateRun.isMember", "You are registered to the crew labeled \"{0}\" for this run already. Unregister from the crew before creating new crew.");
				else if(crewParticipant.getCrewParticipantRole() == CrewParticipant.CREW_PARTICIPANT_ROLE_INVITED)
					messageText = iwrb.getLocalizedString("crew.manage.validateRun.isInvited", "You are invited to the crew labeled \"{0}\" for this run already. Reject invitation before creating new crew.");
				else
//					shouldn't possibly happen
					messageText = CoreConstants.EMPTY;
				
				messageText = MessageFormat.format(messageText, new Object[] {crewParticipant.getCrewLabel()});
				
				((UIInput)toValidate).setValid(false);
				FacesMessage message = new FacesMessage(messageText);
				context.addMessage(toValidate.getClientId(context), message);
			}
			
		} catch (Exception e) {
			
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);

//			TODO: add err messages
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Exception while validating run choice", e);
			((UIInput)toValidate).setValid(false);
			FacesMessage message = new FacesMessage(iwrb.getLocalizedString("crew.manage.validateRun.genException", "Error occurred, while validating your run selection"));
			context.addMessage(toValidate.getClientId(context), message);
		}
	}
	
	public void createCrew() {

		FacesContext context = FacesContext.getCurrentInstance();
		
		String crewLabel = getCrewLabelForOwner();
		Integer runId = new Integer(getRunIdForOwner());
		
		validateCrewLabel(context, crewLabel, runId);
		
		if(context.getMessages() != null && context.getMessages().hasNext())
			return;
		
		IWContext iwc = IWContext.getIWContext(FacesContext.getCurrentInstance());
		try {
			
			User ownerUser = iwc.getCurrentUser();
			RunBusiness runBusiness = getCrewEditWizardBean().getRunBusiness();
			Group runGroup = runBusiness.getRunGroupByGroupId(runId);
			Group yearGroup = ConverterUtility.getInstance().convertGroupToRun(runGroup).getCurrentRegistrationYear();
			
			Participant participant = null;
			
			try {
				participant = runBusiness.getParticipantByRunAndYear(ownerUser, runGroup, yearGroup);
				
			} catch (FinderException e) {
				//this happens, when nothing found
			}
			
			if(participant == null) {
				
				IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
				FacesMessage message = new FacesMessage(iwrb.getLocalizedString("crew.manage.createCrew.noParticipant", "Participant not found for current user and selected run and year group"));
				context.addMessage(null, message);
				return;
			}
			
//			relying on validateRunSelection, so everything should be fine (no crew for user)
			
			CrewParticipant crewParticipant = new CrewParticipant(participant, getCrewEditWizardBean().getRunBusiness());
			crewParticipant.setCrewOwner(true);
			crewParticipant.setCrewLabel(crewLabel);
			crewParticipant.store();
			
			getCrewEditWizardBean().setCrewParticipant(crewParticipant);
			getCrewEditWizardBean().setMode(CrewEditWizardBean.editCrewMode);
			
		} catch (Exception e) {

			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Exception while creating new crew", e);
			FacesMessage message = new FacesMessage(iwrb.getLocalizedString("crew.manage.createCrew.genException", "Error occurred while creating crew. Please, try again."));
			context.addMessage(null, message);
		}
	}
	
	public void editCrew() {
		
		CrewParticipant crewParticipant = getCrewEditWizardBean().getCrewParticipant();
		
//		TODO: check if this participant is amongst current user participants
		
		if(!crewParticipant.isCrewOwner()) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Tried to edit crew, when the participant is not the crew owner. Participant id: "+crewParticipant.getParticipantId());
			IWContext iwc = IWContext.getIWContext(FacesContext.getCurrentInstance());
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
			FacesMessage message = new FacesMessage(iwrb.getLocalizedString("crew.manage.editCrew.notCrewOwner", "Error occurred while trying to edit crew. Please, try again."));
			FacesContext.getCurrentInstance().addMessage(null, message);
			setWizardMode(false);
		}
		
		setCrewLabelForOwner(crewParticipant.getCrewLabel());
		
		setWizardMode(true);
		getCrewEditWizardBean().setMode(CrewEditWizardBean.editCrewMode);
		
	}
	
	public void acceptInvitation() {
		
		CrewParticipant crewParticipant = getCrewEditWizardBean().getCrewParticipant();
		
		if(crewParticipant.getCrewParticipantRole() != CrewParticipant.CREW_PARTICIPANT_ROLE_INVITED) {
			
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Tried to accept invitation, but CrewParticipant was not 'Invited', rather '"+crewParticipant.getCrewParticipantRole()+"'. Participant id: "+crewParticipant.getParticipantId());
			IWContext iwc = IWContext.getIWContext(FacesContext.getCurrentInstance());
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
			FacesMessage message = new FacesMessage(iwrb.getLocalizedString("crew.manage.acceptInvitation.roleNotInvited", "Error occurred while trying to accept invitation. Please, try again."));
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		
		crewParticipant.acceptInvitation();
		crewParticipant.store();
	}
	
	public void rejectInvitation() {
		
		CrewParticipant crewParticipant = getCrewEditWizardBean().getCrewParticipant();
		
		if(crewParticipant.getCrewParticipantRole() != CrewParticipant.CREW_PARTICIPANT_ROLE_INVITED) {
			
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Tried to reject invitation, but CrewParticipant was not 'Invited', rather '"+crewParticipant.getCrewParticipantRole()+"'. Participant id: "+crewParticipant.getParticipantId());
			IWContext iwc = IWContext.getIWContext(FacesContext.getCurrentInstance());
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
			FacesMessage message = new FacesMessage(iwrb.getLocalizedString("crew.manage.rejectInvitation.roleNotInvited", "Error occurred while trying to reject invitation. Please, try again."));
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		
		crewParticipant.rejectInvitation();
		crewParticipant.store();
	}
	
	
	public void updateCrew() {

		FacesContext context = FacesContext.getCurrentInstance();
		
		CrewParticipant crewParticipant = getCrewEditWizardBean().getCrewParticipant();
		String crewLabel = getCrewLabelForOwner();
		
		if(!crewParticipant.isCrewOwner()) {
			
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Tried to update crew, but CrewParticipant was not crew owner. Participant id: "+crewParticipant.getParticipantId());
			IWContext iwc = IWContext.getIWContext(context);
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
			FacesMessage message = new FacesMessage(iwrb.getLocalizedString("crew.manage.updateCrew.notCrewOwner", "Error occurred while trying to update crew. Please, try again."));
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		
		if(!crewLabel.equals(crewParticipant.getCrewLabel())) {
		
			Integer runId = new Integer(getRunIdForOwner());
			validateCrewLabel(context, crewLabel, runId);
			
			if(context.getMessages() != null && context.getMessages().hasNext())
				return;
			
			crewParticipant.setCrewLabel(crewLabel);
			crewParticipant.store();
		}
	}
	
	public void deleteCrew() {

		CrewParticipant crewParticipant = getCrewEditWizardBean().getCrewParticipant();
		
		if(!crewParticipant.isCrewOwner()) {
			
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Tried to delete crew, but CrewParticipant was not crew owner. Participant id: "+crewParticipant.getParticipantId());
			
			IWContext iwc = IWContext.getIWContext(FacesContext.getCurrentInstance());
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
			FacesMessage message = new FacesMessage(iwrb.getLocalizedString("crew.manage.deleteCrew.notCrewOwner", "Error occurred while trying to delete crew. Please, try again."));
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}

		try {
			crewParticipant.deleteCrew();
			setWizardMode(false);
			getCrewEditWizardBean().setMode(CrewEditWizardBean.newCrewMode);
			
		} catch (Exception e) {
			
			IWContext iwc = IWContext.getIWContext(FacesContext.getCurrentInstance());
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
			FacesMessage message = new FacesMessage(iwrb.getLocalizedString("crew.manage.deleteCrew.genException", "Error occurred while trying to delete crew. Please, try again."));
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	
	public void viewCrewsList() {
		setWizardMode(false);
		setCrewViewMode(false);
	}
	
	public void viewCrewView() {
		setWizardMode(false);
		setCrewViewMode(true);
	}

	public boolean isCrewViewMode() {
		return crewViewMode;
	}

	public void setCrewViewMode(boolean crewViewMode) {
		this.crewViewMode = crewViewMode;
	}
	
	public String getCrewViewHeader() {
		
		CrewParticipant crewParticipant = getCrewEditWizardBean().getCrewParticipant();
		
		String crewLabel = crewParticipant.getCrewLabel();
		
		if(crewLabel == null)
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Tried to get crew view header, but crew label was null. Participant id: "+crewParticipant.getParticipantId());
		
		IWContext iwc = IWContext.getIWContext(FacesContext.getCurrentInstance());
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		
		return iwrb.getLocalizedAndFormattedString("crew.manage.crewViewHeader", "Crew \"{0}\" view", new Object[] {crewLabel == null ? CoreConstants.EMPTY : crewLabel});
	}
}