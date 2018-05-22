package com.taobao.zhenge.biz.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.taobao.zhenge.biz.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserInfoController {

    private Map<String, String> data = Maps.newHashMap();

    // 是否可以提供服务
    private volatile boolean inService = false;

    private final String SEPARATOR = ":";

    private final String DATA_PATH = "data.txt";

    /**
     * 启动项目时初始化数据
     */
    @PostConstruct
    public void loadData() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(DATA_PATH)));
            String temp = br.readLine();
            while (StringUtils.isNotBlank(temp)) {
                int pos = temp.indexOf(SEPARATOR);
                if(pos > 0 && pos < temp.length() -1) {
                    String key = temp.substring(0, pos-1);
                    String value = temp.substring(pos+1);
                    data.put(key, value);
                }
                temp = br.readLine();
            }
        } catch (Exception e) {
            // TODO 完善日志
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException ie) {
                    // TODO 完善日志
                }
            }
        }
        inService = true;
    }

    /**
     * 关闭系统时，保存数据
     */
    @PreDestroy
    public void saveData() {
        synchronized (this) {
            inService = false;
        }
        PrintStream ps = null;
        try {
            File file = new File(DATA_PATH);
            if(!file.exists()) {
                file.createNewFile();
            }
            ps = new PrintStream(file);
            for(Map.Entry<String, String> entry : data.entrySet()) {
                ps.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (Exception e) {
            // TODO 完善日志
        } finally {
            if(ps != null) {
                ps.close();
            }
        }
    }

    @RequestMapping("/get")
    public String getDataByKey(@RequestParam("name") String key, Model model) {
        if (inService) {
            if (data.get(key) != null) {
                model.addAttribute("data", data.get(key));
                return "userinfo";
            } else {
                model.addAttribute("msg", "用户不存在！");
            }
        } else {
            model.addAttribute("msg", "暂时无法提供服务！");
        }
        return "error";
    }

    @RequestMapping("/register")
    public synchronized String register(User user, Model model) {
        if(inService) {
            if(user != null && StringUtils.isNotBlank(user.getUsername()) && !data.containsKey(user.getUsername())) {
                data.put(user.getUsername(), JSON.toJSONString(user));
                saveData();
                model.addAttribute("msg", "恭喜" + user.getUsername() + "，你已注册成功！");
                return "success";
            }
            if(user == null || StringUtils.isBlank(user.getUsername())) {
                model.addAttribute("msg", "关键属性为空，注册失败！");
            } else if(data.containsKey(user.getUsername())) {
                model.addAttribute("msg", "用户已经存在，无法注册！");
            }
        } else {
            model.addAttribute("msg", "暂时无法提供服务！");
        }
        return "error";
    }
}
