/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2007 e-Evolution,SC. All Rights Reserved.               *
 * Contributor(s): Victor Perez www.e-evolution.com                           *
 *                 Teo Sarca, www.arhipac.ro                                  *
 *****************************************************************************/
package org.adempiere.model;

import java.util.Properties;

import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_AD_Column;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.model.M_Element;
import org.compiere.util.DisplayType;

/**
 * Cost Collector Callout
 *
 * @author Victor Perez www.e-evolution.com     
 */
public class CalloutColumn extends CalloutEngine
{
	public String element (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		Integer AD_Element_ID = (Integer)value;
		if (AD_Element_ID == null || AD_Element_ID <= 0)
			return "";
		I_AD_Column column = GridTabWrapper.create(mTab, I_AD_Column.class);
		MColumn.setAD_Column(ctx, column, null);
		return "";
	}	
}


