package com.forest.admin.builder;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.util.OwnException;
import com.forest.common.util.ResourceUtil;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class BaseBuilder
{
//  private Logger logger = Logger.getLogger(BaseBuilder.class);
  public static final String ADMIN = "admin";
  public final String ENTRY_FORM = "entryForm";
  public final String SEARCH_FORM = "searchForm";
  public final String LISTING_FORM = "listingForm";

  public final String VALIDATION_CLASS = "validateForm";

  protected final String BUTTON_CONTAINER = "progFooter";

  public final String RETURN_CREATE = "0";
  public final String RETURN_UPDATE = "1";
  public final String RETURN_DELETE = "2";
  public final String RETURN_EDIT = "3";
  public final String RETURN_SEARCH = "4";
  public final String RETURN_VALIDATION = "9";

  public final String CREATE = "action.create";
  public final String DELETE = "action.delete";
  public final String UPDATE = "action.update";
  public final String UPDATE_IMAGE = "action.update.image";
  public final String SHOP_UPDATE = "action.shop.update";
  public static final String SEARCH = "action.search";
  public static final String EDIT = "action.retrieve.edit";
  public final String VALIDATE = "action.validate";
  public final String FORGOT_PASSWORD = "action.reset.password";
  public final String GET_SHIPMENT = "action.retrieve.shipment";
  public final String CONFIRM_PAY = "action.confirm.pay";
  public final String RETRIEVE = "action.retrieve";
  public final String CLEAR_CART = "action.clear.cart";
  public final String VIEW_ALL = "action.view.all";

  public final String UPLOAD = "action.upload";

  public final String VIEW_TRANS_DETAIL = "action.view.transaction.detail";

  public final String RECORD_DELETED = "record.deleted";
  public final String RESULT_HTML = "resultHTML";
  public final String HAS_NEXT = "hasNext";
  public final String HAS_PREV = "hasPrev";
  public final String SORT_NAME = "sortName";
  public static final String ACTION_NAME = "action1";
  public final String TOTAL_RECORD_PER_PAGE = "totalRecordPerPage";
  public final String SORT_DIRECTION = "sortDirection";

  public final String CHECKALL = "action.check.all";
  public final String CHECKNONE = "action.check.none";
  public final String ADD_RECORD = "action.addrecord";
  public final String SORTABLE_FIELD = "sortableField";
  public final String TOTAL_RECORD = "totalRecord";
  public final String TOTAL_SEARCH_RECORD = "totalSearchRecord";
  public final static String FIRST_VIEW = "firstView";
  public final static String LAST_VIEW = "lastView";
  public final static String PAGE_NUM = "p";

  public final String BATCH_UPDATE = "action.batch.update";

  protected final String FIELD_NAME_CLASS = "paramName";
  protected final String NEW_RECORD_CLASS = "newrecord";
  protected final String EDITED_RECORD_CLASS = "editedrecord";

  protected final String SEPERATOR = ",";
  protected final String JOINER = "-";
  protected final String NO_IMAGE = "/components/images/NoPhoto.gif";

  protected ShopInfoBean _shopInfoBean = null;
  protected ClientBean _clientBean = null;
  protected Connection _dbConnection = null;
  public static final String DEFAULT_TEMPLATE = "standard01";

  protected BaseBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, String resources)
  {
    this._shopInfoBean = mShopInfoBean;
    this._clientBean = mClientBean;
    this._dbConnection = conn;
  }

  protected StringBuffer getActionField(String value) {
    StringBuffer mBuffer = new StringBuffer();
    mBuffer.append("<input type='hidden'");
    mBuffer.append(" id='").append("action1").append("'");
    mBuffer.append(" name='").append("action1").append("' value='").append(value).append("' />");

    return mBuffer;
  }

  protected StringBuffer getActionField() {
    return getActionField("");
  }

  protected StringBuffer buildSortableColumn(String module, String name, String mDirection) {
    StringBuffer mBuffer = new StringBuffer();
    mDirection = (mDirection == null) ? "" : mDirection;

    if (mDirection.equalsIgnoreCase("ASC"))
      mBuffer.append("<th class='sortableColumnAsc'>");
    else if (mDirection.equalsIgnoreCase("DESC"))
      mBuffer.append("<th class='sortableColumnDesc'>");
    else {
      mBuffer.append("<th>");
    }
    mBuffer.append("<a href='#'");
    mBuffer.append(" id='").append("sortableField").append("'");
    mBuffer.append(" rel='").append(name).append("'>");
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),module, name, this._clientBean.getLocale())).append("</a></th>");

    return mBuffer;
  }

  protected StringBuffer buildNormalColumn(String module, String name) {
    return buildNormalColumn(module, name, null);
  }

  protected StringBuffer buildNormalColumn(String module, String name, String className) {
    StringBuffer mBuffer = new StringBuffer();
    if (className != null)
      mBuffer.append("<th class='").append(className).append("'>");
    else {
      mBuffer.append("<th>");
    }
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),module, name, this._clientBean.getLocale())).append("</th>");

    return mBuffer;
  }

  protected StringBuffer getPagination(int totalRecord) {
    StringBuffer mBuffer = new StringBuffer();
    mBuffer.append("<table id='paginationTable'>");
    mBuffer.append("<tbody>");
    mBuffer.append("<tr>");
    mBuffer.append("<td align='left'>");
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "total.record", this._clientBean.getLocale())).append(": ");
    mBuffer.append("<span id='").append("totalRecord").append("'>");
    mBuffer.append(totalRecord).append("</span>").append("&nbsp;|&nbsp;");

    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "total.search.result", this._clientBean.getLocale())).append(": ");
    mBuffer.append("<span id='").append("totalSearchRecord").append("'>");
    mBuffer.append("</span>").append("&nbsp;|&nbsp;");
    mBuffer.append("</td>");

    mBuffer.append("<td align='right'>");
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "show", this._clientBean.getLocale()));
    mBuffer.append(": <select id='").append("totalRecordPerPage").append("'");
    mBuffer.append(" name='").append("totalRecordPerPage").append("'>");

    mBuffer.append("<option value='10'>10</option>");
    mBuffer.append("<option value='50' selected='selected'>50</option>");
    mBuffer.append("<option value='100'>100</option>");
    mBuffer.append("<option value='200'>200</option>");
    mBuffer.append("</select>");
    mBuffer.append("&nbsp;&nbsp;");

    mBuffer.append("<a href='#' id='").append("action.view.prev").append("' style='display: none'>");
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "navigator.prev", this._clientBean.getLocale()));
    mBuffer.append("</a>");
    mBuffer.append("&nbsp;|&nbsp;");
    mBuffer.append("<span id='viewRecordNumber'></span>");
    mBuffer.append("&nbsp;|&nbsp;");
    mBuffer.append("<a href='#' id='").append("action.view.next").append("' style='display: none'>");
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "navigator.next", this._clientBean.getLocale()));
    mBuffer.append("</a>");
    mBuffer.append("</td>");
    mBuffer.append("</tr>");
    mBuffer.append("</tbody>");
    mBuffer.append("</table>");

    return mBuffer;
  }

  protected StringBuffer getClientPagination(ArrayList mList, int recordPerPage, StringBuffer mQuery, String pageNum, String idName)
    throws OwnException
  {
    StringBuffer mBuffer = new StringBuffer();
    HashMap mMap = null;
    int pageNumber = 0;
    int linkPerPage = 10;
    int currentDivNumber = 0;
    int divNumber = 0;

    String className = "";
    String prevClassName = "";
    String nextClassName = "";

    StringBuffer mQueryString = null;
    StringBuffer idBuffer = new StringBuffer();

    String prevQuery = null;
    String nextQuery = null;

    StringBuffer contentBuffer = new StringBuffer();
    StringBuffer prevBuffer = new StringBuffer();
    StringBuffer nextBuffer = new StringBuffer();
    try {
      pageNum = (pageNum == null) ? "1" : pageNum;
      currentDivNumber = Math.round(Integer.parseInt(pageNum) / linkPerPage);

      if (Integer.parseInt(pageNum) % linkPerPage != 0) ++currentDivNumber;

      mQuery = (mQuery == null) ? new StringBuffer("?") : mQuery;

      if (mQuery.length() > 1) {
        mQuery.append("&");
      }
      mQuery.append("action1").append("=").append("action.retrieve").append("&");

      if (currentDivNumber == 1) {
        prevClassName = "style='display: none'";
        contentBuffer.append("<div id='pagee' style='position:relative;'>");
      } else {
        prevClassName = "";
        contentBuffer.append("<div id='pagee' style='position:relative; display:none'>");
      }

      for (int i = 0; i < mList.size(); ++i) {
        mQueryString = new StringBuffer(mQuery.toString());
        pageNumber = i / recordPerPage + 1;
        divNumber = Math.round(pageNumber / linkPerPage);
        if (pageNumber % linkPerPage != 0) ++divNumber;

        if ((pageNumber % linkPerPage == 1) && (pageNumber != 1)) {
          contentBuffer.append("</div>");

          if (currentDivNumber == divNumber)
            contentBuffer.append("<div id='pagee' style='position:relative;'>");
          else {
            contentBuffer.append("<div id='pagee' style='position:relative; display:none'>");
          }
        }

        mMap = (HashMap)mList.get(i);

        int x = 0;
        idBuffer = new StringBuffer();
        while (x < recordPerPage) {
          idBuffer.append((String)mMap.get(idName));
          ++x;
          if (i + x == mList.size())
            break;
          if (x < recordPerPage) idBuffer.append(",");
          mMap = (HashMap)mList.get(i + x);
        }
        i += recordPerPage - 1;
        mQueryString.append("id=").append(idBuffer).append("&").append("p").append("=").append(pageNumber);

        if ((pageNumber != 1) && 
          (prevQuery == null) && (pageNumber % linkPerPage == 0)) {
          prevQuery = mQueryString.toString();
        }

        if ((currentDivNumber + 1 == divNumber) && 
          (nextQuery == null) && (pageNumber % linkPerPage == 1)) {
          nextQuery = mQueryString.toString();
        }

        className = (pageNumber == Integer.parseInt(pageNum)) ? "selectedPage" : "deSelectedPage";

        contentBuffer.append("<a href='").append(mQueryString).append("'");
        contentBuffer.append(" id='viewPageNumber-").append(pageNumber).append("'");
        contentBuffer.append(" class='").append(className).append("'");
        contentBuffer.append(" rel='address:/").append(mQueryString).append("'");
        contentBuffer.append(">");
        contentBuffer.append(pageNumber);
        contentBuffer.append("</a>");

        if ((i + 1 < mList.size()) && (pageNumber % linkPerPage != 0)) {
          contentBuffer.append("&nbsp;|&nbsp;");
        }
      }
      contentBuffer.append("</div>");

      if (currentDivNumber < divNumber)
        nextClassName = "";
      else {
        nextClassName = "style='display: none'";
      }

      prevBuffer.append("<span ").append(prevClassName).append("><a href='").append(prevQuery).append("' id='pagePrev'>");
      prevBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "navigator.prev", this._clientBean.getLocale()));
      prevBuffer.append("</a></span>&nbsp;&nbsp;");
      nextBuffer.append("&nbsp;&nbsp;<span ").append(nextClassName).append("><a href='").append(nextQuery).append("' id='pageNext'>");
      nextBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "navigator.next", this._clientBean.getLocale()));
      nextBuffer.append("</a></span>");

      mBuffer.append("<div class='paginationTable'>");
      mBuffer.append("<table>");
      mBuffer.append("<tr>");
      mBuffer.append("<td class='navigatorPrev'>").append(prevBuffer).append("</td>");
      mBuffer.append("<td class='navigatorContent'>").append(contentBuffer).append("</td>");
      mBuffer.append("<td class='navigatorNext'>").append(nextBuffer).append("</td>");
      mBuffer.append("</tr>");
      mBuffer.append("</table>");
      mBuffer.append("</div>");
    } catch (Exception e) {
      throw new OwnException(e);
    }
    return mBuffer;
  }

  protected StringBuffer getSearchOptionLink() {
    StringBuffer mBuffer = new StringBuffer();

    mBuffer.append("&nbsp;&nbsp;<input type='button' id='action.search' value='").append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "action.normal.search", this._clientBean.getLocale())).append("' />");
    mBuffer.append("&nbsp;");
    mBuffer.append("<a href='#'");
    mBuffer.append(" id='").append("action.show.more.search").append("' rel='");
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "action.hide.more.search", this._clientBean.getLocale())).append("'>");
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "action.show.more.search", this._clientBean.getLocale()));
    mBuffer.append("</a>");
    return mBuffer;
  }

  protected StringBuffer getSearchMoreOptionLink() {
    StringBuffer mBuffer = new StringBuffer();
    mBuffer.append("<input type='button' id='action.search' value='").append("Search").append("' />");
    mBuffer.append("&nbsp;");
    mBuffer.append("<a href='#'");
    mBuffer.append(" id='").append("action.hide.more.search").append("'>");
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "action.hide.more.search", this._clientBean.getLocale()));
    mBuffer.append("</a>");
    return mBuffer;
  }

  protected StringBuffer getControlOption(boolean editable) {
    StringBuffer mBuffer = new StringBuffer();
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "action.select", this._clientBean.getLocale())).append(": &nbsp;");
    mBuffer.append("<a href='#' id='").append("action.check.all").append("' >");
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "action.check.all", this._clientBean.getLocale()));
    mBuffer.append("</a>");

    mBuffer.append(", &nbsp;");

    mBuffer.append("<a href='#' id='").append("action.check.none").append("' >");
    mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "action.check.none", this._clientBean.getLocale()));
    mBuffer.append("</a>");

    if (editable) {
      mBuffer.append(", &nbsp;");

      mBuffer.append("<a href='#' id='").append("action.delete").append("' >");
      mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "action.delete", this._clientBean.getLocale()));
      mBuffer.append("</a>");

      mBuffer.append(", &nbsp;");

      mBuffer.append("<a href='#' id='").append("action.addrecord").append("' >");
      mBuffer.append(ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),"", "action.addrecord", this._clientBean.getLocale()));
      mBuffer.append("</a>");
    }

    return mBuffer;
  }

  protected boolean isEmpty(String pStr) {
    return (pStr == null) || (pStr.trim().length() == 0);
  }

  protected boolean isNotEmpty(String pStr) {
    return (pStr != null) && (pStr.trim().length() > 0);
  }

  protected String nullToEmpty(String pStr) {
    if (pStr == null) pStr = "";
    return pStr;
  }
  protected boolean isGreaterThenZero(String pStr) {
    boolean result = true;

    if ((pStr == null) || (pStr.trim().length() == 0)) {
      result = false;
    }
    else if (Double.parseDouble(pStr) <= 0.0D) {
      result = false;
    }

    return result;
  }

  protected HashMap setPaginationMap(ArrayList mArrayListSearch, String sortFieldName, String totalRecordPerPage, String firstView, String lastView)
  {
    HashMap mMap = new HashMap();
    mMap.put("sortName", sortFieldName);

    String totalRecord = (String)mArrayListSearch.get(0);
    if ((isEmpty(firstView)) && (isEmpty(lastView))) {
      mMap.put("totalSearchRecord", totalRecord);
    }
    mArrayListSearch.remove(0);

    totalRecordPerPage = (isEmpty(totalRecordPerPage)) ? "0" : totalRecordPerPage;
    boolean hasNext = mArrayListSearch.size() > Integer.parseInt(totalRecordPerPage);

    if (hasNext) {
      if ((isNotEmpty(lastView)) || ((isEmpty(lastView)) && (isEmpty(firstView))))
        mArrayListSearch.remove(mArrayListSearch.size() - 1);
      else {
        mArrayListSearch.remove(0);
      }
      mMap.put("hasNext", String.valueOf(hasNext));
    }
    else if (isNotEmpty(lastView)) {
      mMap.put("hasNext", String.valueOf(false));
    } else if (isNotEmpty(firstView)) {
      mMap.put("hasNext", String.valueOf(true));
    } else if ((isEmpty(lastView)) && (isEmpty(firstView))) {
      mMap.put("hasNext", String.valueOf(false));
    }

    HashMap mFieldMap = null;
    if (mArrayListSearch.size() > 0) {
      mFieldMap = (HashMap)mArrayListSearch.get(0);
      mMap.put("firstView", (String)mFieldMap.get(sortFieldName));
    } else {
      mMap.put("firstView", "");
    }
    if (mArrayListSearch.size() > 1) {
      mFieldMap = (HashMap)mArrayListSearch.get(mArrayListSearch.size() - 1);
      mMap.put("lastView", (String)mFieldMap.get(sortFieldName));
    } else {
      mMap.put("lastView", "");
    }

    return mMap;
  }

  protected String getCurrentDate() {
    Calendar cal = GregorianCalendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    return dateFormat.format(cal.getTime());
  }

  protected String displayDateTime(String timestampDiff) {
    String display = "";
    int diff = 0;

    if (isNotEmpty(timestampDiff)) {
      diff = Integer.parseInt(timestampDiff);

      if (diff < 60)
        display = diff + " " + ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),null, "i18n.system.second", this._clientBean.getLocale());
      else if (diff < 3600)
        display = diff / 60 + " " + ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),null, "i18n.system.minute", this._clientBean.getLocale());
      else if (diff < 86400)
        display = diff / 3600 + " " + ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),null, "i18n.system.hour", this._clientBean.getLocale());
      else if ((diff > 86400) && (diff < 5184000))
        display = diff / 86400 + " " + ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),null, "i18n.system.day", this._clientBean.getLocale());
      else if ((diff > 5184000) && (diff < 62208000))
        display = diff / 5184000 + " " + ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),null, "i18n.system.month", this._clientBean.getLocale());
      else if (diff > 62208000) {
        display = diff / 62208000 + " " + ResourceUtil.getAdminResourceValue(_shopInfoBean.getBusiness(),null, "i18n.system.year", this._clientBean.getLocale());
      }
    }
    return display;
  }
}