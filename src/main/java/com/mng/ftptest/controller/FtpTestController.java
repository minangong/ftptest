package com.mng.ftptest.controller;

import com.mng.ftptest.service.FtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class FtpTestController {
    @Autowired
    FtpService ftpService;

    @RequestMapping(value = "/send/{num}",method = RequestMethod.POST)
    public boolean upload(@PathVariable int num){
        if(num == 1){
            boolean result = ftpService.uploadFile("/root/airflow/dags/mng1.py", "hahahahahaha");
            return result;
        }
        return true;
    }
}
