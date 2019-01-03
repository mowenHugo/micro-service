package com.hugo.base.framework.constant;

public class Constants {

    /**
     *
     */
    public static final String TRACE_ID = "X-B3-TraceId";
    /**
     *
     */
    public static final String X_SESSION_ID = "X-session-id";
    /**
     * 所有请头
     */
    public static final String REQUEST_HEADER = "request_header";
    /**
     * 所有敏感控制列
     */
    public final static String ALL_PERMISSION_COLUMN = "all_permission_column";
    /**
     * 拥有权限敏感的列
     */
    public final static String MY_PERMISSION_COLUMN = "my_permission_column";

    /**
     * 所有权限列字段
     */
    public final static String ALL_PERMISSION_AUTH_COLUMN = "all_permission_auth_column";
    /**
     * 拥有权限列字段
     */
    public final static String MY_PERMISSION_AUTH_COLUMN = "my_permission_auth_column";
    /**
     * 所有虚拟号字段
     */
    public final static String ALL_VIRTUAL_COLUMN = "all_virtual_column";
    /**
     * 拥有虚拟号字段
     */
    public final static String MY_VIRTUAL_COLUMN = "my_virtual_column";

    /**
     * 用户信息
     */
    public final static String THREAD_USERINFO_CONTEXT = "THREAD_USERINFO_CONTEXT";

    // JSON 映
    public final static String INCLUDED_JSON_MAP = "INCLUDED_JSON_MAP";
    public final static String EXCLUDED_JSON_MAP = "EXCLUDED_JSON_MAP";
    public final static String AUTH_INCLUDED_EXCLUDED_JSON_MAP = "AUTH_INCLUDED_EXCLUDED_JSON_MAP";

    public static final String X_B3_TRACE_ID = "X-B3-TraceId";
    public static final String X_REAL_IP = "X-Real-IP";
    public static final String X_SPAN_NAME = "X-Span-Name";
    public static final String X_SPAN_ID = "X-Span-Id";
    public static final String X_UID = "X-uid";

    public static final String X_SPANPID = "spanPid";
    public static final String X_SPANID = "spanId";

    /**
     * 用户工号
     */
    public static String X_USERNUMBER = "X-usernumber";
    /**
     * 用户名
     */
    public static String X_USERNAME = "X-Username";
    /**
     * 用户英文名
     */
    public static String X_USERNAMEEN = "X-UserNameEn";
    /**
     * 部门id
     */
    public static String X_DEPTID = "X-DeptId";
    /**
     * 部门名称
     */
    public static String X_DEPTNAME = "X-DeptName";
    /**
     * 职位
     */
    public static String X_POSITION = "X-Position";
    /**
     * 部门负责人
     */
    public static String X_DEPTDUTYUSERID = "X-DeptDutyUserId";
    /**
     * 部门负责人姓名
     */
    public static String X_DEPTDUTYUSERNAME = "X-DeptDutyUserName";
    /**
     * 点部id
     */
    public static String X_NETWORKID = "X-NetworkId";
    /**
     * 点部名称
     */
    public static String X_NETWORKNAME = "X-NetwrokName";
    /**
     * 计算机名称
     */
    public static String X_COMPUTERNAME = "X-ComputerName";
    /**
     * 网卡地址
     */
    public static String X_NETWORKCARD = "X-NetworkCard";
    /**
     * 主板编码
     */
    public static String X_MAINBOARDNO = "X-MainBoardNo";
    /**
     * 导出
     */
    public static String X_EXPORT = "X-Export";

    /**
     * 调用客户端获取用户信息
     */
    public static final String FRAMEWORK_USERINFOSERVICE = "framework_UserInfoService";

    public static final String MENU_ID = "X-menu-id";

    public static final String BUTTON_ID = "X-button-id";

    /**
     * 不清空PERMISSION_ID的URL
     */
    public static final String[] UNEMPTY_PERMISSION_ID_URL = new String[]{"/cache/dataScope/list",
            "/permission/column/listAttributeByPermissionId", "/permission/column/listByPermissionId",
            "/cache/dataScope/getDataScopeByPermissionId", "/cache/dataColumn/listColumnByPermissionId",
            "/file/genericExportExcel", "/file/exportExcel"};
}
