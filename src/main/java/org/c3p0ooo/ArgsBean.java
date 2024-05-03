package org.c3p0ooo;

import java.util.HashMap;

public class ArgsBean {

    private HashMap map = new HashMap();

    public ArgsBean(){
        map.put("fastjson", "Fastjson");
        map.put("jackson", "Jackson");
        map.put("jackson2", "Jackson2");
        map.put("groovy", "Groovy");
        map.put("CC4", "CC4");
        map.put("CC6", "CC6");
        map.put("hibernate", "Hibernate_ClassPathXmlApplicationContextExec");
        map.put("CB192","CB192");
        map.put("CB183","CB183");
        map.put("rome","Rome");
        map.put("execAll", "ExecAll");
    }

    public HashMap getMap() {
        return map;
    }
}
