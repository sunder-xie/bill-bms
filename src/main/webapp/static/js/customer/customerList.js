/**
 * Created by 鱼在我这里。 on 2017/3/16.
 */
$(document).ready(function () {

    //添加客户
    $(".addMember").click(function () {

        $(".modal-backdrop").show();
        $(".addMemberDiv").slideDown();
        $(".modal-title").html("添加客户");
        $("#addMemberId").val("1");


    })
    //添加渠道商
    $(".addAGENT").click(function () {
        $(".modal-backdrop").show();
        $(".addMemberDiv").slideDown();
        $(".modal-title").html("添加渠道商");
        $("#addMemberId").val("2");

    })
    $(".close").click(function () {
        $(".addMemberDiv").slideUp();
        $(".modal-backdrop").hide();

    })
    $(".cancel").click(function () {
        $(".addMemberDiv").slideUp();
        $(".modal-backdrop").hide();
    })

    var userName=false;
    var password=false;

//用户名
    $("input[name='userName']").blur(function () {

        if ($("input[name='userName']").val() != "")
        {
            if (/^[A-Za-z]\w{5,12}$/.test($("input[name='userName']").val())) {


                $.ajax({
                    type: "get",
                    data:{userName:$("input[name='userName']").val()},
                    url:CTX+"/user/register/validUserName",
                    dataType:"json",
                    success:function (result) {
                        if (result.code==200){
                            //返回结果code==200代表正确
                            //验证可以使用,
                            userName=true;
                        }else{
                            //验证为已注册,不能使用,
                            $(".pdlogid").css({ "color": "#ff0000" }).text("用户名已存在！");
                            userName = false;
                        }

                    }

                })

            }
            else {
                //格式不对
                userName = false;
                $(".pdlogid").css({ "color": "#ff0000" }).text("用户名格式不正确！");
            }

        }
    });
//密码
    $("input[name='password']").blur(function () {

        if ($("input[name='password']").val()!= "")
        {
            if (/^\w{6,12}$/.test($(this).val())) {
                password = true;

            }
            else {
                //格式不对
                password = false;
                $(".pdpwd").css({ "color": "#ff0000" }).text("密码格式不正确！");
            }
        }
    });
    $("input[name='userName']").focus(function () {
        $(".pdlogid").css({ "color": "#ff0000" }).text("");
        userName = false;

    });
    $(".addOperatorcmt").click(function () {
        if(userName&&password)
        {
            $.ajax({
                type: "post",
                data:{userName: $("input[name='userName']").val(),password:$("input[name='password']").val(),addMemberId:  $("#addMemberId").val(),balance:$("#balance").val()},
                url:CTX+"/customer/customerList",
                dataType:"json",
                success:function (result) {
                    if(result.code==200)
                    {
                        $(".modal-backdrop").hide();
                        $(".addMemberDiv").slideUp();
                        alert(result.message);
                    }
                    else
                    {
                        alert(result.message);
                    }

                }

            })
        }

    })


})
$(function () {

    //1.初始化Table
    var oTable = new TableInit();
    oTable.Init();
    var oTable1 = new TableInit();
    oTable1.Init();

    //2.初始化Button的点击事件
    var oButtonInit = new ButtonInit();
    oButtonInit.Init();


});
var TableInit = function () {
    var oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#myTable').bootstrapTable({
            url: CTX+'/customer/getCustomerList',         //请求后台的URL（*）
            method: 'get',                      //请求方式（*）
            toolbar: '#toolbar',                //工具按钮用哪个容器
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，
            pagination: true,                   //是否显示分页（*）
            pageNumber: 1,                       //初始化加载第一页，默认第一页
            pageSize: 20,                       //每页的记录行数（*）
            pageList: [20, 50, 100],        //可供选择的每页的行数（*）
            sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
            queryParams: oTableInit.queryParams,//传递参数（*）
            queryParamsType: "",
            showColumns: true,                  //是否显示所有的列
            showRefresh: true,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            height: 700,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "Id",                     //每一行的唯一标识，一般为主键列
            showToggle: true,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            showExport: true,                     //是否显示导出
            exportDataType: "basic",
            rowStyle: function (row, index) {
                //这里有5个取值代表5中颜色['active', 'success', 'info', 'warning', 'danger'];
                var strclass = "";
                if ((row.id)%2==0){
                    strclass = '';
                }
                else {
                    strclass = 'active';
                }
                return { classes: strclass }
            },
            columns: [
                {
                    checkbox: true
                },{
                    field: 'id',
                    sortable: true,
                    align: 'center',
                    valign: 'middle',
                    title: '序号',

                },
                {
                    field: 'customerId',
                    sortable: true,
                    align: 'center',
                    valign: 'middle',
                    title: '数据库编号',
                    visible:false

                },
                {
                    field: 'userName',
                    sortable: true,
                    align: 'center',
                    valign: 'middle',
                    title: '客户',

                },
                {
                    field: 'realName',
                    align: 'center',
                    valign: 'middle',
                    sortable: true,
                    title: '真实姓名'
                }, {
                    field: 'contact',
                    align: 'center',
                    valign: 'middle',
                    title: '联系人',

                }, {
                    field: 'qq',
                    align: 'center',
                    sortable: true,
                    valign: 'middle',
                    title: 'qq'
                },
                {
                    field: 'phone',
                    align: 'center',
                    valign: 'middle',
                    sortable: true,
                    title: '电话'
                },

                {
                    field: 'createTime',
                    align: 'center',
                    valign: 'middle',
                    title: '注册时间',

                },

                {
                    field: 'lastLoginTime',
                    align: 'center',
                    valign: 'middle',
                    sortable: true,
                    title: '最新登录',

                },

                {
                    field: "loginCount",
                    align: 'center',
                    valign: 'middle',
                    title: '登录次数',



                },
                {
                    field: "missionCount",
                    align: 'center',
                    valign: 'middle',
                    title: '任务数',



                },
                {
                    field: "balance",
                    align: 'center',
                    valign: 'middle',
                    title: '余额',



                },
                {
                    field: 'operate',
                    title: '操作',
                    align: 'center',
                    valign: 'middle',


                },


            ],

        });
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {
            limit: params.pageSize,   //页面大小
            offset: params.pageNumber,  //页码
            sortOrder: params.sortOrder,
            sortName: params.sortName,

        };
        return temp;
    }



    return oTableInit;
};

$(function () {
    $("#queren").click(function () {

        $('#myTable').bootstrapTable('refresh');
    });

});

