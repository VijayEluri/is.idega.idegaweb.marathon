package is.idega.idegaweb.marathon.presentation.crew;

import is.idega.idegaweb.marathon.IWBundleStarter;

import java.io.IOException;

import javax.faces.application.Application;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;

import org.apache.myfaces.component.html.ext.HtmlDataTable;
import org.apache.myfaces.component.html.ext.HtmlInputHidden;
import org.apache.myfaces.custom.column.HtmlSimpleColumn;
import org.apache.myfaces.custom.htmlTag.HtmlTag;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/01/10 18:56:26 $ by $Author: civilis $
 *
 */
public class UICrewsOverviewList extends IWBaseComponent {

	public static final String COMPONENT_TYPE = "idega_CrewsOverviewList";
	public static final String EDIT_CREW_ID = "ecid";
	
	public static final String crewsOverviewListBean_crewsOverviewListExp = 				"#{crewsOverviewListBean.crewsOverviewList}";
	public static final String crewsOverviewListBean_forceIdHackExp = 						"#{crewsOverviewListBean.forceIdHack}";
	
	public static final String crewsOverviewListBean_participantIdParam = 					"colb_pid";
	
	
	public static final String crew_var = 					"crew";
	public static final String crew_label = 				"label";
	public static final String crew_labelExp = 				"#{crew.label}";
	public static final String crew_runLabel = 				"runLabel";
	public static final String crew_runLabelExp = 			"#{crew.runLabel}";
	public static final String crew_distance = 				"distance";
	public static final String crew_distanceExp = 			"#{crew.distance}";
	public static final String crew_pidOnclick = 			"pidOnclick";
	public static final String crew_pidOnclickExp = 		"#{crew.pidOnclick}";
	
	
	private static final String onclickAtt = "onclick";
	private static final String forceIdAtt = "forceId";
	private static final String containerFacet = "container";
	
	/**
	 * @Override
	 */
	protected void initializeComponent(FacesContext context) {
	
		Application application = context.getApplication();
		IWContext iwc = IWContext.getIWContext(context);
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		
		HtmlForm form = (HtmlForm)context.getApplication().createComponent(HtmlForm.COMPONENT_TYPE);
		form.setId(context.getViewRoot().createUniqueId());
		getFacets().put(containerFacet, form);
		
		
		HtmlInputHidden hidden = (HtmlInputHidden)application.createComponent(HtmlInputHidden.COMPONENT_TYPE);
		hidden.setId(crewsOverviewListBean_participantIdParam);
		hidden.setValueBinding(valueAtt, application.createValueBinding(UICrewRegistrationWizard.crewEditWizardBean_participantIdExp));
		hidden.setValueBinding(forceIdAtt, application.createValueBinding(crewsOverviewListBean_forceIdHackExp));
		form.getChildren().add(hidden);
		
		HtmlTag containerDiv = (HtmlTag)application.createComponent(HtmlTag.COMPONENT_TYPE);
		containerDiv.setId(context.getViewRoot().createUniqueId());
		containerDiv.setValue(divTag);
		form.getChildren().add(containerDiv);
		
		HtmlCommandLink startNewCrewRegLink = (HtmlCommandLink)application.createComponent(HtmlCommandLink.COMPONENT_TYPE);
		startNewCrewRegLink.setId(context.getViewRoot().createUniqueId());
		startNewCrewRegLink.setValue("Create new crew");
		startNewCrewRegLink.setAction(application.createMethodBinding(UICrewRegistrationWizard.crewManageBean_startNewCrewRegistrationExp, null));
		
		containerDiv.getChildren().add(startNewCrewRegLink);
		
		HtmlDataTable crewsTable = (HtmlDataTable)application.createComponent(HtmlDataTable.COMPONENT_TYPE);
		crewsTable.setSortable(true);
		crewsTable.setId(context.getViewRoot().createUniqueId());
		crewsTable.setVar(crew_var);
		crewsTable.setValueBinding(valueAtt, context.getApplication().createValueBinding(crewsOverviewListBean_crewsOverviewListExp));
		
		crewsTable.getChildren().add(createColumn(context, crew_labelExp, "Crew label"));
		crewsTable.getChildren().add(createColumn(context, crew_runLabelExp, "Run label"));
		crewsTable.getChildren().add(createColumn(context, crew_distanceExp, "Distance"));
		
		HtmlCommandLink editCrewLink = (HtmlCommandLink)application.createComponent(HtmlCommandLink.COMPONENT_TYPE);
		editCrewLink.setId(context.getViewRoot().createUniqueId());
		editCrewLink.setValue("Edit");
		editCrewLink.setValueBinding(onclickAtt, application.createValueBinding(crew_pidOnclickExp));
		editCrewLink.setAction(application.createMethodBinding(UICrewRegistrationWizard.crewManageBean_editCrewExp, null));
		
		crewsTable.getChildren().add(createColumn(context, editCrewLink, " "));
		
		containerDiv.getChildren().add(crewsTable);
	}
	
	protected UIColumn createColumn(FacesContext context, UIComponent child, String headerText) {
		
		HtmlSimpleColumn column = (HtmlSimpleColumn)context.getApplication().createComponent(HtmlSimpleColumn.COMPONENT_TYPE);
		column.getChildren().add(child);
		
		if(headerText != null) {
		
			HtmlOutputText text = (HtmlOutputText)context.getApplication().createComponent(HtmlOutputText.COMPONENT_TYPE);
			text.setValue(headerText);
			column.setHeader(text);
		}
		
		return column;
	}
	
	protected UIColumn createColumn(FacesContext context, String textExpression, String headerText) {

		HtmlOutputText text = (HtmlOutputText)context.getApplication().createComponent(HtmlOutputText.COMPONENT_TYPE);
		text.setValueBinding(valueAtt, context.getApplication().createValueBinding(textExpression));
		
		return createColumn(context, text, headerText);
	}
	
	/**
	 * @Override
	 */
	public void encodeChildren(FacesContext context) throws IOException {
		
		renderChild(context, getFacet(containerFacet));
	}
	
	/**
	 * @Override
	 */
	public boolean getRendersChildren() {
		return true;
	}
}