package com.wgcloud.controller;

import cn.hutool.json.JSONObject;
import com.wgcloud.entity.MailSet;
import com.wgcloud.service.LogInfoService;
import com.wgcloud.service.MailSetService;
import com.wgcloud.util.msg.WarnMailUtil;
import com.wgcloud.util.staticvar.StaticKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v2.3
 * @ClassName:MailSetController.java
 * @author: http://www.wgstart.com
 * @date: 2019年11月16日
 * @Description: MailSetController.java
 * @Copyright: 2017-2024 wgcloud. All rights reserved.
 */
@Controller
@RequestMapping("/mailset")
public class MailSetController {


    private static final Logger logger = LoggerFactory.getLogger(MailSetController.class);

    @Resource
    private MailSetService mailSetService;
    @Resource
    private LogInfoService logInfoService;


    /**
     * 根据条件查询列表
     *
     * @return
     */
    @RequestMapping(value = "list")
    @ResponseBody
    public JSONObject MailSetList() {
        JSONObject resultJson = new JSONObject();
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            List<MailSet> list = mailSetService.selectAllByParams(params);
            if (list.size() > 0) {
                resultJson.put("mailSet", list.get(0));
            }else{
                resultJson.put("mailSet", new MailSet());
            }
        } catch (Exception e) {
            logger.error("查询邮件设置错误", e);
            logInfoService.save("查询邮件设置错误：", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("error", e.getMessage());
        }
        return resultJson;
    }


    /**
     * 保存邮件设置信息
     *
     * @return
     */
    @RequestMapping(value = "save")
    @ResponseBody
    public JSONObject saveMailSet(@RequestBody MailSet mailSet) {
        JSONObject resultJson = new JSONObject();
        try {
            if (StringUtils.isEmpty(mailSet.getId())) {
                mailSetService.save(mailSet);
            } else {
                mailSetService.updateById(mailSet);
            }
            StaticKeys.mailSet = mailSet;
            resultJson.put("result","success");
        } catch (Exception e) {
            logger.error("保存邮件设置信息错误：", e);
            logInfoService.save("邮件设置信息错误", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("result","error");
            resultJson.put("msg",e.getMessage());
        }
        return resultJson;
    }

    @RequestMapping(value = "test")
    @ResponseBody
    public JSONObject test(@RequestBody MailSet mailSet) {
        JSONObject resultJson = new JSONObject();
        String result = "success";
        try {
            if (StringUtils.isEmpty(mailSet.getId())) {
                mailSetService.save(mailSet);
            } else {
                mailSetService.updateById(mailSet);
            }
            StaticKeys.mailSet = mailSet;
            result = WarnMailUtil.sendMail(mailSet.getToMail(), "WGCLOUD测试邮件发送", "WGCLOUD测试邮件发送");
            if("success".equals(result)){
                resultJson.put("result","success");
                resultJson.put("msg","测试发送成功");
            }else{
                resultJson.put("result","error");
                resultJson.put("msg","测试发送失败，请查看日志");
            }
        } catch (Exception e) {
            logger.error("测试邮件设置信息错误：", e);
            logInfoService.save("测试邮件设置信息错误", e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("result","error");
            resultJson.put("msg","测试发送失败，请查看日志");
        }
        return resultJson;
    }

    /**
     * 删除告警邮件信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "del")
    @ResponseBody
    public JSONObject delete(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String errorMsg = "删除告警邮件设置错误：";
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                mailSetService.deleteById(id.split(","));
                StaticKeys.mailSet = null;
            }
            resultJson.put("result","success");
        } catch (Exception e) {
            logger.error(errorMsg, e);
            logInfoService.save(errorMsg, e.toString(), StaticKeys.LOG_ERROR);
            resultJson.put("result","error");
            resultJson.put("msg",e.getMessage());
        }
        return resultJson;
    }


}