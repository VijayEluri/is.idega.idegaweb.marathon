/*
 * $Id: RunBlock.java,v 1.10 2007/06/04 21:35:40 sigtryggur Exp $
 * Created on May 17, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.marathon.presentation;

import is.idega.idegaweb.marathon.business.CharityBusiness;
import is.idega.idegaweb.marathon.business.PledgeBusiness;
import is.idega.idegaweb.marathon.business.RunBusiness;
import is.idega.idegaweb.marathon.util.IWMarathonConstants;
import java.util.HashMap;
import java.util.Map;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.RadioButton;
import com.idega.user.business.GenderBusiness;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;


/**
 * Last modified: $Date: 2007/06/04 21:35:40 $ by $Author: sigtryggur $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.10 $
 */
public class RunBlock extends Block {

	public final static String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.marathon";

	public final static String STYLENAME_FORM_ELEMENT = "FormElement";
	public final static String STYLENAME_HEADER = "Header";
	public final static String STYLENAME_TEXT = "Text";
	public final static String STYLENAME_SMALL_TEXT = "SmallText";
	public final static String STYLENAME_LINK = "Link";
	public final static String STYLENAME_INTERFACE = "Interface";
	public final static String STYLENAME_INTERFACE_BUTTON = "InterfaceButton";
	public final static String STYLENAME_CHECKBOX = "CheckBox";

	private IWResourceBundle iwrb = null;
	private IWBundle iwb = null;

	public void _main(IWContext iwc)throws Exception{
		setResourceBundle(getResourceBundle(iwc));
		setBundle(getBundle(iwc));
		super._main(iwc);
	}
	
	public Map getStyleNames() {
		HashMap map = new HashMap();
		String[] styleNames = { STYLENAME_HEADER, STYLENAME_TEXT, STYLENAME_SMALL_TEXT, STYLENAME_LINK, STYLENAME_LINK + ":hover", STYLENAME_INTERFACE, STYLENAME_CHECKBOX, STYLENAME_INTERFACE_BUTTON,  };
		String[] styleValues = { "", "", "", "", "", "", "", "" };

		for (int a = 0; a < styleNames.length; a++) {
			map.put(styleNames[a], styleValues[a]);
		}

		return map;
	}

	public String localize(String textKey, String defaultText) {
		if (this.iwrb == null) {
			return defaultText;
		}
		return this.iwrb.getLocalizedString(textKey, defaultText);
	}
	
	public Text getHeader(String s) {
		return getStyleText(s, STYLENAME_HEADER);
	}

	public Text getText(String text) {
		return getStyleText(text, STYLENAME_TEXT);
	}

	public Text getSmallText(String text) {
		return getStyleText(text, STYLENAME_SMALL_TEXT);
	}

	public Link getLink(String text) {
		return getStyleLink(new Link(text), STYLENAME_LINK);
	}
	
	public InterfaceObject getStyledInterface(InterfaceObject obj) {
		return (InterfaceObject) setStyle(obj, STYLENAME_INTERFACE);
	}
	
	protected CheckBox getCheckBox(String name, String value) {
		return (CheckBox) setStyle(new CheckBox(name,value),STYLENAME_CHECKBOX);
	}
	
	protected RadioButton getRadioButton(String name, String value) {
		return (RadioButton) setStyle(new RadioButton(name,value),STYLENAME_CHECKBOX);
	}
	
	protected GenericButton getButton(GenericButton button) {
		button.setHeight("20");
		return (GenericButton) setStyle(button,STYLENAME_INTERFACE_BUTTON);
	}

	protected IWBundle getBundle() {
		return this.iwb;
	}

	protected void setBundle(IWBundle bundle) {
		this.iwb = bundle;
	}

	protected IWResourceBundle getResourceBundle() {
		return this.iwrb;
	}

	protected void setResourceBundle(IWResourceBundle resourceBundle) {
		this.iwrb = resourceBundle;
	}

	protected RunBusiness getRunBusiness(IWApplicationContext iwac) {
		try {
			return (RunBusiness) IBOLookup.getServiceInstance(iwac, RunBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
	
	protected CharityBusiness getCharityBusiness(IWApplicationContext iwac) {
		try {
			return (CharityBusiness) IBOLookup.getServiceInstance(iwac, CharityBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	protected PledgeBusiness getPledgeBusiness(IWApplicationContext iwac) {
		try {
			return (PledgeBusiness) IBOLookup.getServiceInstance(iwac, PledgeBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	protected UserBusiness getUserBusiness(IWApplicationContext iwac) {
		try {
			return (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	protected GroupBusiness getGroupBusiness(IWApplicationContext iwac) {
		try {
			return (GroupBusiness) IBOLookup.getServiceInstance(iwac, GroupBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	protected GenderBusiness getGenderBusiness(IWApplicationContext iwac) {
		try {
			return (GenderBusiness) IBOLookup.getServiceInstance(iwac, GenderBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	protected Table getPhasesTable(int phase, int totalPhases, String key, String defaultText) {
		Table table = new Table(2, 1);
		table.setCellpadding(3);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setAlignment(2, 1, Table.HORIZONTAL_ALIGN_RIGHT);
		table.setBottomCellBorder(1, 1, 1, "#D7D7D7", "solid");
		table.setBottomCellBorder(2, 1, 1, "#D7D7D7", "solid");
		
		table.add(getHeader(localize(key, defaultText)), 1, 1);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(localize("step", "Step")).append(" ").append(phase).append(" ").append(localize("of", "of")).append(" ").append(totalPhases);
		table.add(getHeader(buffer.toString()), 2, 1);
		
		return table;
	}
	
	protected Table getInformationTable(String information) {
		Table table = new Table(1, 1);
		table.setCellpadding(3);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setBottomCellBorder(1, 1, 1, "#D7D7D7", "solid");
		table.setCellpaddingBottom(1, 1, 6);
		
		table.add(getText(information), 1, 1);
		
		return table;
	}
	
	/**
	 * Returns the default edit icon with the tooltip specified.
	 * @param toolTip	The tooltip to display on mouse over.
	 * @return Image	The edit icon.
	 */
	protected Image getEditIcon(String toolTip) {
		Image editImage = this.iwb.getImage("shared/edit.gif", 12, 12);
		editImage.setToolTip(toolTip);
		return editImage;
	}

	/**
	 * Returns the default delete icon with the tooltip specified.
	 * @param toolTip	The tooltip to display on mouse over.
	 * @return Image	The delete icon.
	 */
	protected Image getDeleteIcon(String toolTip) {
		Image deleteImage = this.iwb.getImage("shared/delete.gif", 12, 12);
		deleteImage.setToolTip(toolTip);
		return deleteImage;
	}

	/**
	 * Returns the default forward icon with the tooltip specified.
	 * @param toolTip	The tooltip to display on mouse over.
	 * @return Image	The forward icon.
	 */
	protected Image getForwardIcon(String toolTip) {
		Image forwardImage = this.iwb.getImage("shared/forward.gif", 12, 12);
		forwardImage.setToolTip(toolTip);
		return forwardImage;
	}
	
	public String getBundleIdentifier() {
		return IWMarathonConstants.IW_BUNDLE_IDENTIFIER;
	}
}