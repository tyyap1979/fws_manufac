package com.forest.mfg.webbuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.constants.GeneralConst;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.CookiesUtil;
import com.forest.mfg.adminbuilder.MFG_SelectBuilder;
import com.forest.mfg.beandef.MFG_CartDef;
import com.forest.mfg.beandef.MFG_CartDetailDef;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_ProductOptionDetailDef;
import com.forest.share.webbuilder.BaseWebBuilder;

public class MFG_CartWebBuilder extends BaseWebBuilder {
	private Logger logger = Logger.getLogger (MFG_CartWebBuilder.class);
	public MFG_CartWebBuilder(ShopInfoBean shopInfoBean,
			ClientBean clientBean, Connection conn, HttpServletRequest req,
			HttpServletResponse resp, Element moduleEle) throws Exception {
		super(shopInfoBean, clientBean, conn, req, resp, moduleEle);
		
	}
	
	public Object requestJsonHandler()throws Exception{	
		JSONObject json = new JSONObject();
		String reqAction = _req.getParameter(GeneralConst.ACTION_NAME);
		
		if(!CommonUtil.isEmpty(reqAction)){
			json = addToCart(reqAction);
		}
		
		return json;
	}
	
	public JSONObject addToCart(String reqAction){	
		JSONObject json = new JSONObject();
		StringBuffer query = null;
		HashMap[] detailMap = null;
		LinkedHashMap paramMap = null;	
		try{
			String cartId = getCartID();
			detailMap = BuilderUtil.requestValuesToDataObject(MFG_CartDetailDef.class, _req, 
					BeanDefUtil.getArrayList(MFG_CartDetailDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean), 
					(String) BeanDefUtil.getField(MFG_CartDef.class, BeanDefUtil.KEY), cartId, _shopInfoBean.getShopName(),"");
			
			if("update".equals(reqAction)){
				if(detailMap[0]!=null){
													
					query = new StringBuffer();
					query.append("Update ").append(MFG_CartDetailDef.TABLE);
					query.append(" Set ").append(MFG_CartDetailDef.qty).append("=?");
					query.append(" ,").append(MFG_CartDetailDef.sellsubtotal).append("=").append(MFG_CartDetailDef.qty).append("*").append(MFG_CartDetailDef.unit).append("*").append(MFG_CartDetailDef.price);
					query.append(" Where ").append(MFG_CartDetailDef.cartdetailid).append("=?");
					
					paramMap = new LinkedHashMap();
					paramMap.put(MFG_CartDetailDef.qty, (String) detailMap[0].get(MFG_CartDetailDef.qty.name));
					paramMap.put(MFG_CartDetailDef.cartdetailid, (String) detailMap[0].get(MFG_CartDetailDef.cartdetailid.name));
					_gs.executeQuery(query, paramMap);
				}
			}else if("add".equals(reqAction) || "delete".equals(reqAction)){
				if(detailMap!=null){							
					_gs.updateDetail(MFG_CartDetailDef.class, detailMap, BeanDefUtil.getKeyObject(MFG_CartDetailDef.class), BeanDefUtil.getArrayList(MFG_CartDetailDef.class, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
				}
			}
			
			json.put("rc", "0000");	
			if("update".equals(reqAction) || "delete".equals(reqAction)){
				// Get Subtotal 
				query = new StringBuffer();
				query.append("Select ");
				query.append(MFG_CartDetailDef.sellsubtotal);
				query.append(" From ");
				query.append(MFG_CartDetailDef.TABLE);
				query.append(" Where ").append(MFG_CartDetailDef.cartdetailid).append("=?");
				
				paramMap = new LinkedHashMap();
				paramMap.put(MFG_CartDetailDef.cartdetailid, (String) detailMap[0].get(MFG_CartDetailDef.cartdetailid.name));
				
				json.put("data",_gs.searchDataMap(query, paramMap));
			}
		}catch (Exception e) {
			logger.error(e, e);
		}
		return json;
	}
		
	private String getCartID()throws Exception{
		CookiesUtil cu = new CookiesUtil(_req, _resp);
		String cartId = cu.getCartID();
		StringBuffer query = new StringBuffer();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int key = 0;
		if(CommonUtil.isEmpty(cartId)){
			query.append("Insert Into ").append(MFG_CartDef.TABLE).append("(");
			query.append(MFG_CartDef.companyid);
			query.append(",").append(MFG_CartDef.email);
			query.append(",").append(MFG_CartDef.updatedate);
			query.append(") Values(?, ?, now())");
			pstmt = _dbConnection.prepareStatement(query.toString(),PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, _shopInfoBean.getShopName());
			pstmt.setString(2, "");
			pstmt.execute ();
			
			rs = pstmt.getGeneratedKeys ();
			if (rs.next()) {
				key = rs.getInt(1);		
				cartId = String.valueOf(key);
				cu.setCartID(cartId);
		    }
		}
		return cartId;
	}
	
	public void displayHandler(String moduleId)throws Exception{					
		_mc.initModuleContent(_moduleEle, "[custom=loop]");	
		putMainData();
	}
	
	private void putMainData(){
		ArrayList dataArray = null;
		HashMap loopContent = null;		
		LinkedHashMap paramMap = new LinkedHashMap();
		StringBuffer query = new StringBuffer();
		
		query.append("Select ");
		query.append("a.*");
		query.append(", a.").append(MFG_CartDetailDef.price).append("*").append(MFG_CartDetailDef.unit).append(" As unitprice");
		query.append(", b.").append(MFG_CustProductDef.name);
		query.append(", b.").append(MFG_CustProductDef.sellunittype);
		// Options
		query.append(",c.").append(MFG_ProductOptionDetailDef.name).append(" opt1_name");
		query.append(",c.").append(MFG_ProductOptionDetailDef.code).append(" opt1_code");
		query.append(",d.").append(MFG_ProductOptionDetailDef.name).append(" opt2_name");
		query.append(",d.").append(MFG_ProductOptionDetailDef.code).append(" opt2_code");
		query.append(",e.").append(MFG_ProductOptionDetailDef.name).append(" opt3_name");
		query.append(",e.").append(MFG_ProductOptionDetailDef.code).append(" opt3_code");
		query.append(",f.").append(MFG_ProductOptionDetailDef.name).append(" opt4_name");
		query.append(",f.").append(MFG_ProductOptionDetailDef.code).append(" opt4_code");
		
		query.append(",c1.").append(MFG_ProductOptionDef.groupname).append(" opt1_groupname");
		query.append(",d1.").append(MFG_ProductOptionDef.groupname).append(" opt2_groupname");
		query.append(",e1.").append(MFG_ProductOptionDef.groupname).append(" opt3_groupname");	
		query.append(",f1.").append(MFG_ProductOptionDef.groupname).append(" opt4_groupname");	
		
		query.append(" From ").append(MFG_CartDetailDef.TABLE).append(" a");
		query.append(" Inner Join ").append(MFG_CustProductDef.TABLE).append(" b On");
		query.append(" b.").append(MFG_CustProductDef.prodid).append("=a.").append(MFG_CartDetailDef.prodid);
		// Options 
		query.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" c on c.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(" = a.").append(MFG_CartDetailDef.opt1);			
		query.append(" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" c1 on c1.").append(MFG_ProductOptionDef.prodoptid).append(" = c.").append(MFG_ProductOptionDetailDef.prodoptid);
		
		query.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" d on d.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(" = a.").append(MFG_CartDetailDef.opt2);			
		query.append(" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" d1 on d1.").append(MFG_ProductOptionDef.prodoptid).append(" = d.").append(MFG_ProductOptionDetailDef.prodoptid);
		
		query.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" e on e.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(" = a.").append(MFG_CartDetailDef.opt3);			
		query.append(" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" e1 on e1.").append(MFG_ProductOptionDef.prodoptid).append(" = e.").append(MFG_ProductOptionDetailDef.prodoptid);
		
		query.append(" Left Join ").append(MFG_ProductOptionDetailDef.TABLE).append(" f on f.").append(MFG_ProductOptionDetailDef.prodoptdetailid).append(" = a.").append(MFG_CartDetailDef.opt4);			
		query.append(" Left Join ").append(MFG_ProductOptionDef.TABLE).append(" f1 on f1.").append(MFG_ProductOptionDef.prodoptid).append(" = f.").append(MFG_ProductOptionDetailDef.prodoptid);
		
		query.append(" Where");
		query.append(" a.").append(MFG_CartDetailDef.cartid).append("=?");
		query.append(" And b.").append(MFG_CustProductDef.status).append("='").append(GeneralConst.ACTIVE).append("'");
		
		query.append(" Order By");
		query.append(" a.").append(MFG_CartDetailDef.cartdetailid);
		
		String cartId = new CookiesUtil(_req, _resp).getCartID();
		paramMap.put(MFG_CartDetailDef.cartid, cartId);
		
		StringBuffer optionsBuffer = new StringBuffer();
		String opt1, opt2, opt3, opt4, measurement = null;
		try{
			dataArray = _gs.searchDataArray(query, paramMap);
			int count = dataArray.size();
			for(int i=0; i<count; i++){
				loopContent = (HashMap) dataArray.get(i);					
				optionsBuffer = new StringBuffer();
				
				opt1 = (String) loopContent.get(MFG_CartDetailDef.opt1.name);
				opt2 = (String) loopContent.get(MFG_CartDetailDef.opt2.name);
				opt3 = (String) loopContent.get(MFG_CartDetailDef.opt3.name);
				opt4 = (String) loopContent.get(MFG_CartDetailDef.opt4.name);
				measurement = (String) loopContent.get(MFG_CartDetailDef.measurement.name);				
				if(!CommonUtil.isEmpty(measurement)){
					optionsBuffer.append("<br>").append("Measurement: ").append(measurement);
				}
				if(!CommonUtil.isEmpty(opt1)){
					optionsBuffer.append("<br>").append((String) loopContent.get("opt1_groupname")).append(": ").append((String) loopContent.get("opt1_name"));
				}
				if(!CommonUtil.isEmpty(opt2)){
					optionsBuffer.append("<br>").append((String) loopContent.get("opt2_groupname")).append(": ").append((String) loopContent.get("opt2_name"));
				}
				if(!CommonUtil.isEmpty(opt3)){
					optionsBuffer.append("<br>").append((String) loopContent.get("opt3_groupname")).append(": ").append((String) loopContent.get("opt3_name"));
				}
				if(!CommonUtil.isEmpty(opt4)){
					optionsBuffer.append("<br>").append((String) loopContent.get("opt4_groupname")).append(": ").append((String) loopContent.get("opt4_name"));
				}
				loopContent.put("running.no", String.valueOf(i+1));
				loopContent.put(MFG_CustProductDef.sellunittype.name + "_value", (String)MFG_SelectBuilder.getHASH_SELL_UNIT(_dbConnection, _shopInfoBean).get(loopContent.get(MFG_CustProductDef.sellunittype.name)));
				loopContent.put("options", optionsBuffer.toString());
				_mc.setLoopElementContent(loopContent);
			}						
			if(count==0){
				
			}
			//_mc.removeLoopElement();
		}catch(Exception e){
			logger.error(e, e);
		}			
	}	
}
