package com.yipeng.bill.bms.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.yipeng.bill.bms.core.model.Page;
import com.yipeng.bill.bms.core.model.ResultMessage;
import com.yipeng.bill.bms.dao.BillCostMapper;
import com.yipeng.bill.bms.dao.BillMapper;
import com.yipeng.bill.bms.dao.BillPriceMapper;
import com.yipeng.bill.bms.domain.Bill;
import com.yipeng.bill.bms.domain.BillCost;
import com.yipeng.bill.bms.domain.BillPrice;
import com.yipeng.bill.bms.domain.User;
import com.yipeng.bill.bms.service.BillService;
import com.yipeng.bill.bms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/10.
 */
@Controller
@RequestMapping(value = "/bill")
public class BillController extends BaseController {
    @Autowired
    private BillService billService;
    @Autowired
    private UserService userService;


    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @RequestMapping(value = "/list")
    public  String listDetails(HttpServletRequest request)
    {

        return "/bill/billList";
    }

    /**
     * table表格获取数据
     * @param limit
     * @param offset
     * @return
     */
    @RequestMapping(value = "/getBillDetails")
    @ResponseBody
    public Map<String,Object> getBillDetails( int limit, int offset)
    {

       offset=(offset-1)*limit;
       // Page<Bill> page = this.getPageRequest();    //分页对象
       Map<String, Object> params = this.getSearchRequest(); //查询参数
        User user=this.getCurrentAccount();
        params.put("limit",limit);
        params.put("offset",offset);
        params.put("user",user);


        //调的是USER表的数据

        Map<String, Object> modelMap=billService.findBillList(params);
        return  modelMap;
    }

    /**
     *  相同价提交(测试)
     * @param search
     * @param keyword
     * @param url
     * @param rankend
     * @param price
     * @param rankend1
     * @param price1
     * @param rankend2
     * @param price2
     * @param rankend3
     * @param price3
     * @return
     */
    @RequestMapping(value = "/list/sameprice",method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage samePrice(@RequestParam(value="search",required = true) String search,@RequestParam(value="keyword",required = true) String keyword,
                                    @RequestParam(value="url",required = true) String url,@RequestParam(value="rankend",required = true) Long rankend,
                                    @RequestParam(value="price",required = true) Long price,@RequestParam(value="rankend1",required = false) Long rankend1,
                                    @RequestParam(value="price1",required = false) Long price1,@RequestParam(value="rankend2",required = false) Long rankend2,
                                    @RequestParam(value="price2",required = false) Long price2,@RequestParam(value="rankend3",required = false) Long rankend3,
                                    @RequestParam(value="price3",required = false) Long price3
    ) throws UnsupportedEncodingException {

         if(keyword!=null)
         {
             try {
                 //get参数乱码问题
                 keyword=new String(keyword.getBytes("ISO-8859-1"), "UTF-8");

             } catch (UnsupportedEncodingException e) {
                 e.printStackTrace();
             }
         }

        User user = this.getCurrentAccount();
        billService.saveBill( user,search,url, keyword, rankend, price, rankend1, price1, rankend2, price2, rankend3, price3);

        return this.ajaxDoneError("xxx!");
    }
    @Autowired
    private BillMapper billMapper;
    @Autowired
    private BillPriceMapper billPriceMapper;
    @Autowired
    private BillCostMapper  billCostMapper;
    //测试查排名 产生消费记录
    @RequestMapping(value = "/testpm")
    @ResponseBody
    public ResultMessage testpm()
    {
        List<Bill> billList=billMapper.selectAll();
        for (Bill bill: billList
             ) {
            int x=1+(int)(Math.random()*30);
            bill.setNewRanking(x);
            billMapper.updateByPrimaryKeySelective(bill);
           List<BillPrice>  billPrice=billPriceMapper.selectByBillId(bill.getId());
            Map<String, Object> modelMap = new HashMap();
            modelMap.put("BillId",bill.getId());
            modelMap.put("date",new Date());
            if(x<=billPrice.get(0).getBillRankingStandard())
            {

                BillCost billCost=billCostMapper.selectByBillIdAndDate(modelMap);
                if(billCost!=null)
                {
                    billCost.setCostAmount(billPrice.get(0).getPrice());
                    billCost.setRanking(x);
                    billCostMapper.updateByPrimaryKey(billCost);
                }
                else
                {
                    BillCost billCost1=new BillCost();
                    billCost1.settBillId(bill.getId());
                    billCost1.settBillPriceId(billPrice.get(0).getId());
                    billCost1.setCostAmount(billPrice.get(0).getPrice());
                    billCost1.setCostDate(new Date());
                    billCost1.setRanking(x);
                    billCostMapper.insert(billCost1);
                }

            }
            else
            {
                long a=0;
                BillCost billCost2=billCostMapper.selectByBillIdAndDate(modelMap);
                if(billCost2!=null)
                {
                    billCost2.setCostAmount(a);
                    billCost2.setRanking(x);
                    billCostMapper.updateByPrimaryKey(billCost2);
                }
                else
                {
                    BillCost billCost3=new BillCost();
                    billCost3.settBillId(bill.getId());
                    billCost3.settBillPriceId(billPrice.get(0).getId());
                    billCost3.setCostAmount(a);
                    billCost3.setCostDate(new Date());
                    billCost3.setRanking(x);
                    billCostMapper.insert(billCost3);
                }
            }
        }

        return  this.ajaxDoneError("");
    }
}
