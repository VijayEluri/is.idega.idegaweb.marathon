package is.idega.idegaweb.marathon.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;

public class CharityBMPBean extends GenericEntity implements Charity {

	protected static final String ENTITY_NAME = "run_charity_organization";
	protected static final String COLUMN_NAME_NAME = "name";
	protected static final String COLUMN_NAME_ORGANIZATIONAL_ID = "organizational_id";
	
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_NAME_NAME, "Name", true, true, String.class);
		addAttribute(COLUMN_NAME_ORGANIZATIONAL_ID, "Organizational ID", true, true, String.class);
	}

	public String getName() {
		return (String) getColumnValue(COLUMN_NAME_NAME);
	}

	public String getOrganizationalID() {
		return (String) getColumnValue(COLUMN_NAME_ORGANIZATIONAL_ID);
	}

	public void setName(String name) {
		setColumn(COLUMN_NAME_NAME, name);
	}

	public void setOrganizationalID(String organizationalId) {
		setColumn(COLUMN_NAME_ORGANIZATIONAL_ID, organizationalId);
	}
	
	public Collection ejbFindAllCharities() throws FinderException {
		SelectQuery query = idoSelectQuery();
		return idoFindPKsByQuery(query);
	}
	
	public Object ejbFindCharityByOrganizationalId(String organizationalId) throws FinderException {
		Table table = new Table(this);
		Column orgId = new Column(table, COLUMN_NAME_ORGANIZATIONAL_ID);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn(table));
		query.addCriteria(new MatchCriteria(orgId, MatchCriteria.EQUALS, organizationalId));
		
		return idoFindOnePKByQuery(query);
	}
}