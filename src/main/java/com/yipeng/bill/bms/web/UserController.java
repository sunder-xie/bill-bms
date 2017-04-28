package com.yipeng.bill.bms.web;

import com.yipeng.bill.bms.core.crypto.CryptoUtils;
import com.yipeng.bill.bms.core.model.ResultMessage;
import com.yipeng.bill.bms.domain.User;
import com.yipeng.bill.bms.service.UserRoleService;
import com.yipeng.bill.bms.service.UserService;
import com.yipeng.bill.bms.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/3/7.
 */
@Controller
@RequestMapping(value = "/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;


    /**
     * 用户注册
     * @param request
     * @return
     */
    @RequestMapping(value="/register",method = RequestMethod.GET)
    public  String register(HttpServletRequest request)
    {
        return "/user/register";
    }

    /**
     * 注册用户
     * @param request
     * @param user
     * @return
     */
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public  String createUser(HttpServletRequest request,User user)
    {
        if(user.getUserName()!=""&&user.getPassword()!="")
        {
            userService.saveUser(user);
            Long id=user.getId();
        }
        return  "/user/login";
    }

    /**
     *  根据注册用户名查询数据库是否已经存在
     * @param userName
     * @return
     */
    @RequestMapping(value = "/register/validUserName",method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage validUserName(@RequestParam String userName) {

        User user=  userService.getUserByName(userName);
        if(user!=null)
        {
            return this.ajaxDoneError("用户名已注册!");
        }
        else
        {
            return this.ajaxDoneSuccess("用户名可以使用!");
        }

    }
    /**
     *  修改客户信息
     * @param
     * @return
     */
    @RequestMapping(value = "/updateUser",method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage updateUser(HttpServletRequest request,User user) {

        LoginUser loginUser=this.getCurrentAccount();
        if(loginUser!=null)
        {
            user.setId(loginUser.getId());
            user.setUserName(loginUser.getUserName());
            int a=userService.updateByPrimaryKeySelective(user);
            if(a==1)
            {

                return this.ajaxDoneSuccess("修改成功!");
            }
            else
            {
                return this.ajaxDoneError("修改失败，请稍后再试!");
            }
        }
        else
        {
            return this.ajaxDoneError("修改失败，请稍后再试!");
        }

    }


    /**
     *
     * @param userName
     * @return
     */
    @RequestMapping(value = "/checkPwd",method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage checkPwd(@RequestParam String password) {
        LoginUser loginUser=this.getCurrentAccount();
        if(loginUser!=null)
        {
            User user=new User();
            user.setId(loginUser.getId());
            user.setPassword(CryptoUtils.md5(password));
            boolean b=userService.checkPwd(user);
            if (b)
            {
                return this.ajaxDoneSuccess("密码正确!");
            }
            else
            {
                return this.ajaxDoneError("原始密码不正确");
            }

        }
        else
        {
            return this.ajaxDoneError("原始密码不正确");
        }
    }
    /**
     *  修改客户信息
     * @param
     * @return
     */
    @RequestMapping(value = "/updateUserPwd",method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage updateUserPwd(HttpServletRequest request,String password) {

        LoginUser loginUser=this.getCurrentAccount();
        if(loginUser!=null)
        {
            User user=new User();
            user.setId(loginUser.getId());
            user.setUserName(loginUser.getUserName());
            user.setPassword(CryptoUtils.md5(password));
            int a=userService.updateByPrimaryKeySelective(user);
            if(a==1)
            {

                return this.ajaxDoneSuccess("修改成功!");
            }
            else
            {
                return this.ajaxDoneError("修改失败，请稍后再试!");
            }
        }
        else
        {
            return this.ajaxDoneError("修改失败，请稍后再试!");
        }

    }
}
