package com.chain.server;

import com.chain.ChainApplication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/receipt")
public class ReceiptServer {

    @RequestMapping(value = "/{rId}/content", method = RequestMethod.GET)
    public Map<String, String> receiptContent(@PathVariable("rId") String rId){
        return ChainApplication.asset.queryReceipt(new BigInteger(rId));
    }

    @RequestMapping(value = "/sign", method = RequestMethod.POST)
    public Map<String, String> signReceipt(@RequestBody Map<String, String> input){
        return ChainApplication.asset.signReceipt(input.get("obligor"), input.get("password"),input.get("creditor"), new BigInteger(input.get("amount")),input.get("certifier"));
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public Map<String, String> transferPayment(@RequestBody Map<String, String> input){
        return ChainApplication.asset.transferPayment(new BigInteger(input.get("rId")),input.get("sender"),input.get("password"),input.get("receiver"),new BigInteger(input.get("amount")));
    }

    @RequestMapping(value = "/settle", method = RequestMethod.POST)
    public Map<String, String> settle(@RequestBody Map<String, String> input){
        return ChainApplication.asset.settle(new BigInteger(input.get("rId")),input.get("obligor"),input.get("password"));
    }

    @RequestMapping(value = "/{rId}/bills/{username}", method = RequestMethod.GET)
    public Map<String, String> billContent(@PathVariable("rId") String rId, @PathVariable("username") String username){
        return ChainApplication.asset.queryBill(new BigInteger(rId), username);
    }

    @RequestMapping(value = "/finance", method = RequestMethod.POST)
    public Map<String, String> financing(@RequestBody Map<String, String> input){
        return ChainApplication.asset.financing(new BigInteger(input.get("rId")), input.get("bank"),input.get("creditor"),input.get("password"),new BigInteger(input.get("amount")));
    }

}
