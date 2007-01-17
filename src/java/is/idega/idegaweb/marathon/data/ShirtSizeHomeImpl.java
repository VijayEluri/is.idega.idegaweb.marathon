package is.idega.idegaweb.marathon.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;

public class ShirtSizeHomeImpl extends IDOFactory implements ShirtSizeHome {
	public Class getEntityInterfaceClass() {
		return ShirtSize.class;
	}

	public ShirtSize create() throws CreateException {
		return (ShirtSize) super.createIDO();
	}

	public ShirtSize findByPrimaryKey(Object pk) throws FinderException {
		return (ShirtSize) super.findByPrimaryKeyIDO(pk);
	}
}