/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                        *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
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
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.adempiere.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

/**
 * Class Model for Smart View
 * @author victor.perez@e-evoluton.com, e-Evolution
 *
 */
public class MView extends X_AD_View
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4624429043533053271L;
	/**	Logger							*/
	private static CLogger	s_log = CLogger.getCLogger (MView.class);
	
	/**************************************************************************
	 * 	Asset Constructor
	 *	@param ctx context
	 *	@param AD_SmartView_ID  
	 *	@param trxName transaction name 
	 */
	public MView (Properties ctx, int AD_SmartView_ID, String trxName)
	{
		super (ctx, AD_SmartView_ID, trxName);
		if (AD_SmartView_ID == 0)
		{
		}
	}

	/**
	 * 	Discontinued Asset Constructor - DO NOT USE (but don't delete either)
	 *	@param ctx context
	 *	@param AD_SmartView_ID Cahs Flow ID
	 */
	public MView (Properties ctx, int AD_SmartView_ID)
	{
		this (ctx, AD_SmartView_ID, null);
	}

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *	@param trxName transaction
	 */
	public MView (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MAsset

	/**
	 * 	String representation
	 *	@return info
	 */
	@Override
	public String toString ()
	{
		StringBuffer sb = new StringBuffer ("MInOutBound[")
			.append (get_ID ())
			.append ("-")
			.append (getName())
			.append ("]");
		return sb.toString ();
	}	//	toString
	
	/**
	 * return the Smart View Joins
	 * @return
	 */
	public Collection<MViewDefinition> getViewDefinitions()
	{
		final String whereClause = COLUMNNAME_AD_View_ID + "= ?";
		return new Query(getCtx(), MViewDefinition.Table_Name, whereClause, get_TrxName()).
					setParameters(new Object[]{get_ID()})
					.setOrderBy(MViewDefinition.COLUMNNAME_SeqNo)
					.setOnlyActiveRecords(true)
					.list();
	}
	
	/**
	 * Get the Smart View Joins
	 * @return String View Joins
	 */
	public String getJoinsTables()
	{
		final String whereClause = COLUMNNAME_AD_View_ID + "= ?";
		Iterator<MViewDefinition> joins = new Query(getCtx(), MViewDefinition.Table_Name, whereClause, get_TrxName()).
					setParameters(new Object[]{get_ID()})
					.setOrderBy(MViewDefinition.COLUMNNAME_SeqNo)
					.setOnlyActiveRecords(true)
					.iterate();
		StringBuffer tables = new StringBuffer(""); 
		while(joins.hasNext())
		{
			MViewDefinition join = joins.next();
			tables.append(join.getAD_Table().getTableName());
			if(joins.hasNext())
			{	
				tables.append(",");
			}	
		}
		return tables.toString();
	}
	
	/**
	 * get from Clause
	 * @return String from Clause
	 */
	public String getFromClause()
	{
		String fromClause = " ";
		String joinClause = " ";
		for (MViewDefinition join : getViewDefinitions())
		{
			if(join.getJoinClause() == null)
			{
				fromClause = fromClause + join.getAD_Table().getTableName() + " " + join.getTableAlias();
			}
			else if (join.getJoinClause().length() > 0)
			{
				joinClause = joinClause + join.getJoinClause() + " ";
			}
		}
		
		return fromClause + joinClause;
	}
	/**
	 * getViewColumn
	 * @param AD_View_ID
	 * @return MViewColumn 
	 */
	public Collection<MViewColumn> getViewColumn(int AD_View_ID)
	{
		MView view = new MView(Env.getCtx(), AD_View_ID, get_TrxName());
		Collection<MViewColumn> cols = new ArrayList<MViewColumn>();
	    for(MViewDefinition def : view.getViewDefinitions())
	    {
	    	final String whereClause = MViewDefinition.COLUMNNAME_AD_View_Definition_ID + "=?";
	    	List<MViewColumn> columns = new Query(Env.getCtx(), MViewColumn.Table_Name, whereClause , get_TrxName()).
			setParameters(def.get_ID())
			.setOnlyActiveRecords(true)
			.list();

	    	for(MViewColumn col : columns)
	    	{
	    		cols.add(col);
	    	}	
	    }
	    return cols;
	}
	
	/**
	 * get Parent View Join
	 * @return MViewDefinition
	 */
	public MViewDefinition getParentViewDefinition()
	{
		
		String whereClause =  MViewDefinition.COLUMNNAME_AD_View_ID + "=? AND "
							+ MViewDefinition.COLUMNNAME_JoinClause +" IS NULL";
		
		MViewDefinition definition = new Query(getCtx(),MViewDefinition.Table_Name, whereClause, get_TrxName())
		.setParameters(new Object[]{getAD_View_ID()})
		.setOnlyActiveRecords(true)
		.setOrderBy(MViewDefinition.COLUMNNAME_SeqNo)
		.firstOnly();
		
		return definition;
	}
	
	/**
	 * Parent Entity Name
	 * @return String Table Name
	 */
	public String getParentEntityName()
	{
		return MTable.getTableName(getCtx(), getParentViewDefinition().getAD_Table_ID());
	}
	
	/**
	 * get Parent Entity Alias
	 * @return
	 */
	public String getParentEntityAliasName()
	{
		 return getParentViewDefinition().getTableAlias();
	}
	
	// BEGIN AJC E-EVOLUTION 10 SEP
	
	public static String getSQLFromView(int AD_View_ID)
	{
		StringBuffer sql = new StringBuffer();
		StringBuffer joins = new StringBuffer();
		StringBuffer cols = new StringBuffer();
		String from = "";
	    MView view = new MView(Env.getCtx(), AD_View_ID, null);
	    
	    sql.append("SELECT ");
	    boolean co = false;
	    for(MViewDefinition def : view.getViewDefinitions())
	    {
	    	Collection<MViewColumn> columns = new Query(Env.getCtx(), MViewColumn.Table_Name, MViewDefinition.COLUMNNAME_AD_View_Definition_ID + "=?", null).
			setParameters(new Object[]{def.get_ID()})
			.setOnlyActiveRecords(true)
			.list();
	    	
	    	
	    	for(MViewColumn col : columns)
	    	{
	    		if(co)
    				cols.append(",");
	    		if(col.getColumnSQL()!=null && col.getColumnSQL().length()>0)
	    		{
	    			
	    			cols.append(col.getColumnSQL() + " as " + col.getName());
	    			co = true;
	    		}
	    		else if(col.getColumnName()!=null && col.getColumnName().length()>0)
	    		{

	    			cols.append(def.getTableAlias() + "." + col.getColumnName() + " as " + col.getName());
	    			co = true;
	    		}
	    	}
	    	
	    	MTable table = new MTable(Env.getCtx(), def.getAD_Table_ID(), null);
	    	
	    	if(def.getJoinClause()!=null && def.getJoinClause().length()>0)
	    	{
	    		String jc = def.getJoinClause();
	    		
	    		joins.append(" ").append(jc).append(" ");
	    	}
	    	else
	    		from = table.getTableName() + " " + def.getTableAlias();
	    }
	    
	    sql.append(cols).append(" from ").append(from).append(" ").append(joins);
	    
	    return sql.toString();
	}
	
	public static boolean isValidValue(int AD_View_ID, String ColumnName, Object Value)
	{
		boolean valid = false;
		String whereClause="name = ? and ad_view_definition_id in " +
				"(select ad_view_definition_id from ad_view_definition where ad_view_id = ?)";
		MViewColumn column = new Query(Env.getCtx(), MViewColumn.Table_Name, whereClause, null)
		.setParameters(new Object[]{ColumnName, AD_View_ID})
		.first();
		
		if(column!=null)
		{
			if(column.get_ValueAsBoolean("IsMandatory")){
				if(Value!=null)
				{
					valid = true;
				}
			}
			else
			{
				valid = true;
			}
		}
		
		return valid;
	}
}	
