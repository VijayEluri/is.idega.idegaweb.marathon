/*
 * $Id: Distance.java,v 1.8 2007/06/04 13:48:19 tryggvil Exp $
 * Created on Aug 16, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.marathon.data;

import java.util.Locale;
import com.idega.user.data.Group;


/**
 * Last modified: $Date: 2007/06/04 13:48:19 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.8 $
 */
public interface Distance extends Group {

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#isUseChip
	 */
	public boolean isUseChip();

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setUseChip
	 */
	public void setUseChip(boolean useChip);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#isTransportOffered
	 */
	public boolean isTransportOffered();

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setTransportOffered
	 */
	public void setTransportOffered(boolean transportOffered);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#getPrice
	 */
	public float getPrice(Locale locale);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setPriceInISK
	 */
	public void setPriceInISK(float price);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setPriceInEUR
	 */
	public void setPriceInEUR(float price);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#getChildrenPrice
	 */
	public float getChildrenPrice(Locale locale);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setChildrenPriceInISK
	 */
	public void setChildrenPriceInISK(float price);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setChildrenPriceInEUR
	 */
	public void setChildrenPriceInEUR(float price);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#getPriceForTransport
	 */
	public float getPriceForTransport(Locale locale);
	
	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setPriceForTransportInISK
	 */
	public void setPriceForTransportInISK(float price);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setPriceForTransportInEUR
	 */
	public void setPriceForTransportInEUR(float price);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#isFamilyDiscount
	 */
	public boolean isFamilyDiscount();

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setFamilyDiscount
	 */
	public void setFamilyDiscount(boolean discount);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#isAllowsGroups
	 */
	public boolean isAllowsGroups();

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setAllowsGroups
	 */
	public void setAllowsGroups(boolean allowsGroups);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#getNextAvailableParticipantNumber
	 */
	public int getNextAvailableParticipantNumber();

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setNextAvailableParticipantNumber
	 */
	public void setNextAvailableParticipantNumber(int number);

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#getNumberOfSplits
	 */
	public int getNumberOfSplits();

	/**
	 * @see is.idega.idegaweb.marathon.data.DistanceBMPBean#setNumberOfSplits
	 */
	public void setNumberOfSplits(int number);
	/**
	 * <p>
	 * Returns the distance set on the group in KiloMeters.
	 * Returns -1 when an error occurs or distance not decipherable.
	 * </p>
	 * @return
	 */
	public int getDistanceInKms();
	
	public Year getYear();
}
