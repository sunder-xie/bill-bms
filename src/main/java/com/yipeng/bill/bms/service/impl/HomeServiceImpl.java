package com.yipeng.bill.bms.service.impl;

import com.yipeng.bill.bms.dao.*;
import com.yipeng.bill.bms.domain.Bill;
import com.yipeng.bill.bms.domain.BillPrice;
import com.yipeng.bill.bms.domain.FundAccount;
import com.yipeng.bill.bms.domain.Role;
import com.yipeng.bill.bms.service.HomeService;
import com.yipeng.bill.bms.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import java.util.*;

/**
 * Created by Administrator on 2017/4/10.
 */
@Service
public class HomeServiceImpl implements HomeService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private BillCostMapper billCostMapper;
    @Autowired
    private BillMapper billMapper;
    @Autowired
    private FundAccountMapper fundAccountMapper;
    @Autowired
    private  BillPriceMapper billPriceMapper;
    /**
     * 首页详情
     * @param loginUser
     * @return
     */
    @Override
    public Map<String, Object> homeDetails(LoginUser loginUser) {
        Map<String, Object> map=new HashMap<>();
        Map<String, Object> params=new HashMap<>();
        params.put("userId",loginUser.getId());
        if(loginUser.hasRole("SUPER_ADMIN"))
        {

            //客户数
            Role role=roleMapper.selectByRoleCode("DISTRIBUTOR");
            params.put("roleId",role.getId());

            Long count=UserCount(params);
            map.put("UserCount",count);
            //本月消费
            Double MonthConsumption=MonthConsumption(params);
            map.put("MonthConsumption",MonthConsumption);
            params.put("state",2);

            //本日消费
            Double DayConsumption=DayConsumption(params);
            map.put("DayConsumption",DayConsumption);

            //当前任务数
            Long billCount=billMapper.getBillListCount(params);
            map.put("billCount",billCount);
            //累计任务数
            params.put("state2",3);
            Long AllbillCount=billMapper.getBillListCount(params);
            map.put("AllbillCount",AllbillCount);
            //今日达标数
            //1,先获取对应所有的订单
            Map<String,Object> billparam=new HashMap<>();
            billparam.put("userId",loginUser.getId());
            billparam.put("state",2);
            List<Bill> billList=billMapper.selectByInMemberId(billparam);
             //判断哪些订单今日达标
            int standardSum=0;//今天达标数
            if(billList.size()>0)
            {
                for (Bill bill:billList
                     ) {
                    //对应订单排名标准
                    BillPrice billPrice=new BillPrice();
                    billPrice.setBillId(bill.getId());
                    billPrice.setInMemberId(loginUser.getId());
                    List<BillPrice> billPriceList=new ArrayList<>();
                    billPriceList=billPriceMapper.selectByBillPrice(billPrice);
                    //判断今日订单达到哪个标准
                    for (BillPrice item:billPriceList
                         ) {
                         if(bill.getNewRanking()<=item.getBillRankingStandard())
                         {
                             standardSum+=1;
                             break;
                         }
                    }
                }
            }
            map.put("standardSum",standardSum);
            //总完成率(今日达标数/总订单数)
            double AllCompleteness=((double)standardSum/billList.size())*100;
            map.put("AllCompleteness",AllCompleteness);
            //百度完成率
            String baidu="百度";
            Double baiduCompleteness=searchCompleteness(baidu,loginUser);
            map.put("baiduCompleteness",baiduCompleteness);
            //百度wap完成率
            String baiduWap="手机百度";
            Double baiduWapCompleteness=searchCompleteness(baiduWap,loginUser);
            map.put("baiduWapCompleteness",baiduWapCompleteness);
            //360完成率
            String sanliuling="360";
            Double sanliulingCompleteness=searchCompleteness(sanliuling,loginUser);
            map.put("sanliulingCompleteness",sanliulingCompleteness);
            //搜狗完成率
            String sougou="搜狗";
            Double sougouCompleteness=searchCompleteness(sougou,loginUser);
            map.put("sougouCompleteness",sougouCompleteness);
            return map;
        }
        //渠道商和代理商
        else if(loginUser.hasRole("DISTRIBUTOR")||loginUser.hasRole("AGENT"))
        {
            //客户数
            //Role role=roleMapper.selectByRoleCode("AGENT");
            //params.put("roleId",role.getId());
            //Long count=UserCount(params);
           // Role role1=roleMapper.selectByRoleCode("CUSTOMER");
           // params.put("roleId",role1.getId());
            //Long count1=UserCount(params);
            //Long AllCount=count+count1;
            //.put("UserCount",AllCount);

            //账户余额
            FundAccount fundAccount=fundAccountMapper.selectByUserId(loginUser.getId());
            if (fundAccount==null)
            {

                map.put("balance",0);
            }
            else
            {
                map.put("balance",fundAccount.getBalance());

            }
             //月总消费
            Double MonthConsumption=MonthConsumption(params);
            map.put("MonthConsumption",MonthConsumption);
            //本日消费
            Double DayConsumption=DayConsumption(params);
            map.put("DayConsumption",DayConsumption);
            //当前任务数
            params.put("state",2);
            Long billCount=billMapper.getBillListCount(params);
            map.put("billCount",billCount);
            //累计任务数
            params.put("state2",3);
            Long AllbillCount=billMapper.getBillListCount(params);
            map.put("AllbillCount",AllbillCount);

            //今日达标数
            //1,先获取对应所有的订单
            Map<String,Object> billparam=new HashMap<>();
            billparam.put("userId",loginUser.getId());
            billparam.put("state",2);
            List<Bill> billList=billMapper.selectByInMemberId(billparam);
            //判断哪些订单今日达标
            int standardSum=0;//今天达标数
            if(billList.size()>0)
            {
                for (Bill bill:billList
                        ) {
                    //对应订单排名标准
                    BillPrice billPrice=new BillPrice();
                    billPrice.setBillId(bill.getId());
                    billPrice.setInMemberId(loginUser.getId());
                    List<BillPrice> billPriceList=new ArrayList<>();
                    billPriceList=billPriceMapper.selectByBillPrice(billPrice);
                    //判断今日订单达到哪个标准
                    for (BillPrice item:billPriceList
                            ) {
                        if(bill.getNewRanking()<=item.getBillRankingStandard())
                        {
                            standardSum+=1;
                            break;
                        }
                    }
                }
            }
            map.put("standardSum",standardSum);




            return map;
        }
        //操作员
        else  if (loginUser.hasRole("COMMISSIONER"))
        {
            //客户数
            params.put("ascription",loginUser.getId());
            params.put("inMemberId",loginUser.getCreateUserId());
            Long UserCount=userMapper.getUserBillAscriptionCount(params);
            map.put("UserCount",UserCount);
            //当前任务数
            params.put("state",2);
            params.put("userId",loginUser.getCreateUserId());
            params.put("billAscription",loginUser.getId());
            Long billCount=billMapper.getBillListCount(params);
            map.put("billCount",billCount);
            //累计任务数
            params.put("state2",3);
            Long AllbillCount=billMapper.getBillListCount(params);
            map.put("AllbillCount",AllbillCount);

            //月总消费
            Calendar now =Calendar.getInstance();
            params.put("year",now.get(Calendar.YEAR));
            params.put("month",now.get(Calendar.MONTH)+1);
            params.put("createId",loginUser.getCreateUserId());
            Double sum=billCostMapper.MonthConsumptionCommissioner(params);
            map.put("MonthConsumption",sum);
            //今日消费
            params.put("day",now.get(Calendar.DATE));
            Double sum1=billCostMapper.MonthConsumptionCommissioner(params);
            map.put("DayConsumption",sum1);

            //今日达标数
            //1,先获取对应所有的订单
            Map<String,Object> billparam=new HashMap<>();
            billparam.put("userId",loginUser.getCreateUserId());
            billparam.put("billAscription",loginUser.getId());
            billparam.put("state",2);
            List<Bill> billList=billMapper.selectByInMemberId(params);
            //判断哪些订单今日达标
            int standardSum=0;//今天达标数
            if(billList.size()>0)
            {
                for (Bill bill:billList
                        ) {
                    //对应订单排名标准
                    BillPrice billPrice=new BillPrice();
                    billPrice.setBillId(bill.getId());
                    billPrice.setInMemberId(loginUser.getId());
                    List<BillPrice> billPriceList=new ArrayList<>();
                    billPriceList=billPriceMapper.selectByBillPrice(billPrice);
                    //判断今日订单达到哪个标准
                    for (BillPrice item:billPriceList
                            ) {
                        if(bill.getNewRanking()<=item.getBillRankingStandard())
                        {
                            standardSum+=1;
                            break;
                        }
                    }
                }
            }
            map.put("standardSum",standardSum);
            return map;
        }
        else if (loginUser.hasRole("CUSTOMER"))
        {
            //账户余额
            FundAccount fundAccount=fundAccountMapper.selectByUserId(loginUser.getId());
            if (fundAccount==null)
            {

                map.put("balance",0);
            }
            else
            {
                map.put("balance",fundAccount.getBalance());

            }
            //月总消费
            Calendar now =Calendar.getInstance();
            params.put("year",now.get(Calendar.YEAR));
            params.put("month",now.get(Calendar.MONTH)+1);
            Double sum=billCostMapper.MonthConsumptionCustomer(params);
            map.put("MonthConsumption",sum);
            //今日消费
            params.put("day",now.get(Calendar.DATE));
            Double sum1=billCostMapper.MonthConsumptionCustomer(params);
            map.put("DayConsumption",sum1);
            return map;
        }

         return null;
    }




    //客户数
    public Long UserCount(Map<String, Object> params)
    {
        Long Count=userMapper.getUserRoleByCreateIdCount(params);
        return  Count;
    }
    //本月总消费
    public  Double MonthConsumption(Map<String, Object> params)
    {
        Calendar now =Calendar.getInstance();
        params.put("year",now.get(Calendar.YEAR));
        params.put("month",now.get(Calendar.MONTH)+1);
        Double sum=billCostMapper.MonthConsumption(params);
         return sum;
    }
    //今日消费
    public  Double DayConsumption(Map<String, Object> params)
    {
        Calendar now =Calendar.getInstance();
        params.put("year",now.get(Calendar.YEAR));
        params.put("month",now.get(Calendar.MONTH)+1);
        params.put("day",now.get(Calendar.DATE));
        Double sum=billCostMapper.MonthConsumption(params);
        return sum;
    }
    //搜索引擎完成率
    public  Double searchCompleteness(String search, LoginUser loginUser)
    {
        //今日达标数
        //1,先获取对应所有的订单
        Map<String,Object> billparam=new HashMap<>();
        billparam.put("userId",loginUser.getId());
        billparam.put("state",2);
        billparam.put("searchName",search);
        List<Bill> billList=billMapper.selectByInMemberId(billparam);
        //判断哪些订单今日达标
        int standardSum=0;//今天达标数
        if(billList.size()>0)
        {
            for (Bill bill:billList
                    ) {
                //对应订单排名标准
                BillPrice billPrice=new BillPrice();
                billPrice.setBillId(bill.getId());
                billPrice.setInMemberId(loginUser.getId());
                List<BillPrice> billPriceList=new ArrayList<>();
                billPriceList=billPriceMapper.selectByBillPrice(billPrice);
                //判断今日订单达到哪个标准
                for (BillPrice item:billPriceList
                        ) {
                    if(bill.getNewRanking()<=item.getBillRankingStandard())
                    {
                        standardSum+=1;
                        break;
                    }
                }
            }
        }
        double Completeness=0.0;
        if(billList.size()>0)
        {
            Completeness=((double)standardSum/billList.size())*100;
        }

        return Completeness;
    }
}
