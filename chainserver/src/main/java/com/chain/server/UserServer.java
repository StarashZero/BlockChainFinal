package com.chain.server;


import com.chain.ChainApplication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserServer {

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(@RequestBody Map<String, String> user){
        return ChainApplication.asset.login(user.get("username"),user.get("password"));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Map<String, String> register(@RequestBody Map<String, String> user){
        return ChainApplication.asset.register(user.get("username"), user.get("password"), new BigInteger(user.get("amount")), user.get("bank").equals("true"));
    }

    @RequestMapping(value = "/{username}/content", method = RequestMethod.GET)
    public Map<String, String> userContent(@PathVariable("username") String username){
        return ChainApplication.asset.queryUserContent(username);
    }

    @RequestMapping(value = "/{username}/address", method = RequestMethod.GET)
    public Map<String, String> userAddress(@PathVariable("username") String username){
        return ChainApplication.asset.selectName(username);
    }

    @RequestMapping(value = "/{username}/bills", method = RequestMethod.GET)
    public Map<String, String> userBills(@PathVariable("username") String username){
        return ChainApplication.asset.selectBill(username);
    }

    @RequestMapping(value = "/{username}/receipts", method = RequestMethod.GET)
    public Map<String, String> userReceipts(@PathVariable("username") String username){
        return ChainApplication.asset.selectReceipt(username);
    }

}
