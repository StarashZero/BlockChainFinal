package com.chain.client;

import com.chain.contract.ChainServer_v2;
import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple2;
import org.fisco.bcos.web3j.tuples.generated.Tuple5;
import org.fisco.bcos.web3j.tuples.generated.Tuple6;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.tx.txdecode.ResultEntity;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoder;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class ChainClient {

    static Logger logger = LoggerFactory.getLogger(ChainClient.class);

    private Web3j web3j;

    private Credentials credentials;

    public Web3j getWeb3j() {
        return web3j;
    }

    public void setWeb3j(Web3j web3j) {
        this.web3j = web3j;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public void recordAssetAddr(String address) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        prop.setProperty("address", address);
        final Resource contractResource = new ClassPathResource("contract.properties");
        FileOutputStream fileOutputStream = new FileOutputStream(contractResource.getFile());
        prop.store(fileOutputStream, "contract address");
    }

    public String loadAssetAddr() throws Exception {
        // load ChainServer_v1 contact address from contract.properties
        Properties prop = new Properties();
        final Resource contractResource = new ClassPathResource("contract.properties");
        prop.load(contractResource.getInputStream());

        String contractAddress = prop.getProperty("address");
        if (contractAddress == null || contractAddress.trim().equals("")) {
            contractAddress = deployAssetAndRecordAddr();
        }
        logger.info(" load ChainServer_v1 address from contract.properties, address is {}", contractAddress);
        return contractAddress;
    }

    public void initialize() throws Exception {

        // init the Service
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Service service = context.getBean(Service.class);
        service.run();

        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        Web3j web3j = Web3j.build(channelEthereumService, 1);

        // init Credentials
        Credentials credentials = Credentials.create(Keys.createEcKeyPair());
        setCredentials(credentials);
        setWeb3j(web3j);

        logger.debug(" web3j is " + web3j + " ,credentials is " + credentials);
    }

    private static BigInteger gasPrice = new BigInteger("30000000");
    private static BigInteger gasLimit = new BigInteger("30000000");

    public String deployAssetAndRecordAddr() {

        try {
            ChainServer_v2 asset = ChainServer_v2.deploy(web3j, credentials, new StaticGasProvider(gasPrice, gasLimit)).send();
            System.out.println(" deploy ChainServer_v1 success, contract address is " + asset.getContractAddress());

            recordAssetAddr(asset.getContractAddress());
            return asset.getContractAddress();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println(" deploy ChainServer_v1 contract failed, error message is  " + e.getMessage());
        }
        return "";
    }

    //讲用户名转换为地址
    public Map<String, String> selectName(String name) {
        HashMap<String, String> res = new HashMap<>();
        try {
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));

            //调用合约函数
            Tuple2<BigInteger, List<String>> result = asset.selectName(name).send();
            System.out.println(result);
            //返回结果
            if (!result.getValue1().equals(new BigInteger("0"))) {
                res.put("status", "ok");
                res.put("address", result.getValue2().get(0));
                return res;
            } else {
                System.out.printf(" %s asset account is not exist \n", name);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());

            System.out.printf(" query asset account failed, error message is %s\n", e.getMessage());
        }
        res.put("status", "fail");
        return res;
    }

    //通过用户名获得所持账单ID
    public Map<String, String> selectBill(String name){
        HashMap<String, String> res = new HashMap<>();
        try {
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));

            //调用合约函数
            Tuple2<BigInteger, List<BigInteger>> result = asset.selectBill(name).send();
            System.out.println(result);
            //返回结果
            res.put("status","ok");
            res.put("bills", ""+result.getValue2());
            return res;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" selectBill exception, error message is {}", e.getMessage());

            System.out.printf(" selectBill failed, error message is %s\n", e.getMessage());
        }
        res.put("status", "fail");
        return res;
    }

    //通过用户名获得待还账单ID
    public Map<String, String> selectReceipt(String name){
        HashMap<String, String> res = new HashMap<>();
        try {
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            //调用合约函数
            Tuple2<BigInteger, List<BigInteger>> result = asset.selectReceipt(name).send();
            System.out.println(result);
            //返回结果
            res.put("status","ok");
            res.put("receipts", ""+result.getValue2());
            return res;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" selectReceipt exception, error message is {}", e.getMessage());

            System.out.printf(" selectReceipt failed, error message is %s\n", e.getMessage());
        }
        res.put("status", "fail");
        return res;
    }

    //查询账单的基本信息
    public Map<String, String> queryReceipt(BigInteger rId) {
        HashMap<String, String> res = new HashMap<>();
        try {
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            //调用合约函数
            Tuple6<String, BigInteger, String, String, String, Boolean> result = asset.receipts(rId).send();
            Tuple5<String, String, BigInteger, Boolean, Boolean> result2 = asset.companys(result.getValue1()).send();
            //返回结果
            res.put("obligor", result2.getValue1());
            res.put("total", ""+result.getValue2());
            res.put("used", ""+result.getValue6());
            res.put("status","ok");
            return res;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" query receipt exception, error message is {}", e.getMessage());

            System.out.printf(" query receipt failed, error message is %s\n", e.getMessage());
        }
        res.put("status","fail");
        return res;
    }

    //查询账单的详细信息
    public Map<String, String> queryBill(BigInteger rId, String name){
        HashMap<String, String> res = new HashMap<>();
        try {
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            //调用合约查询基本信息
            Tuple6<String, BigInteger, String, String, String, Boolean> result = asset.receipts(rId).send();
            Tuple5<String, String, BigInteger, Boolean, Boolean> result2 = asset.companys(result.getValue1()).send();
            //调用合约查询详细信息
            String addr = selectName(name).get("address");
            TransactionReceipt receipt = asset.getBillAmount(rId, addr).send();
            TransactionDecoder decoder = TransactionDecoderFactory.buildTransactionDecoder(ChainServer_v2.ABI, "");
            InputAndOutputResult outputResult = decoder.decodeOutputReturnObject(receipt.getInput(), receipt.getOutput());
            List<ResultEntity> response = outputResult.getResult();
            //返回结果
            res.put("creditor",name);
            res.put("amount",""+response.get(0).getData());
            res.put("canUse",""+response.get(1).getData());
            res.put("obligor", result2.getValue1());
            res.put("total", ""+result.getValue2());
            res.put("used", ""+result.getValue6());
            res.put("status","ok");
            return res;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" query receipt exception, error message is {}", e.getMessage());

            System.out.printf(" query receipt failed, error message is %s\n", e.getMessage());
        }
        res.put("status","fail");
        return res;
    }

    //查询用户信息
    public Map<String, String> queryUserContent(String name) {
        HashMap<String, String> res = new HashMap<>();
        try {
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            //调用合约函数
            String addr = selectName(name).get("address");
            Tuple5<String, String, BigInteger, Boolean, Boolean> result = asset.companys(addr).send();
            //返回结果
            res.put("username", ""+result.getValue1());
            res.put("account", ""+result.getValue3());
            res.put("bank", ""+result.getValue4());
            res.put("used", ""+result.getValue5());
            res.put("status", "ok");
            return res;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());

            System.out.printf(" query asset account failed, error message is %s\n", e.getMessage());
        }
        res.put("status", "fail");
        return res;
    }

    //登录
    public Map<String, String> login(String name, String password){
        HashMap<String, String> res = new HashMap<>();
        try {
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            //调用合约函数
            String addr = selectName(name).get("address");
            TransactionReceipt receipt = asset.login(addr, password).send();
            TransactionDecoder decoder = TransactionDecoderFactory.buildTransactionDecoder(ChainServer_v2.ABI, "");
            InputAndOutputResult outputResult = decoder.decodeOutputReturnObject(receipt.getInput(), receipt.getOutput());
            List<ResultEntity> response = outputResult.getResult();
            System.out.println(response);
            //返回结果
            if (!response.isEmpty()) {
                res.put("status", response.get(0).getData().equals(true)?"ok":"fail");
                return res;
            } else {
                System.out.println(" back value not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            logger.error(" login exception, error message is {}", e.getMessage());
            System.out.printf(" login failed, error message is %s\n", e.getMessage());
        }
        res.put("status", "fail");
        return res;
    }

    //注册
    public Map<String, String> register(String name, String password, BigInteger amount, boolean isBank) {
        HashMap<String, String> res = new HashMap<>();
        try {
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            //为用户创建一个私钥账户(暂未使用)
            Credentials credentials = GenCredential.create();
            String address = credentials.getAddress();
            String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
            String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
            //调用合约函数
            TransactionReceipt receipt = asset.register(address, name, password, amount, isBank).send();
            TransactionDecoder decoder = TransactionDecoderFactory.buildTransactionDecoder(ChainServer_v2.ABI, "");
            InputAndOutputResult outputResult = decoder.decodeOutputReturnObject(receipt.getInput(), receipt.getOutput());
            List<ResultEntity> response = outputResult.getResult();
            System.out.println(response);
            //返回结果
            if (!response.isEmpty()) {
                if (response.get(0).getData().equals(true)) {
                    res.put("address", address);
                    res.put("privateKey", privateKey);
                    res.put("publicKey", publicKey);
                    res.put("username", name);
                    res.put("amount", ""+amount);
                    res.put("bank", ""+isBank);
                    res.put("status","ok");
                    return res;
                } else {
                    System.out.print(" register asset account failed, ret code is false \n");
                }
            } else {
                System.out.println(" back value not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            logger.error(" registe exception, error message is {}", e.getMessage());
            System.out.printf(" register asset account failed, error message is %s\n", e.getMessage());
        }
        res.put("status","fail");
        return res;
    }

    //签署账单
    public Map<String, String> signReceipt(String obligor, String password, String creditor, BigInteger amount, String certifier) {
        HashMap<String, String> res = new HashMap<>();
        try {
            //确认请求身份
            Map<String, String> check = login(obligor, password);
            if (check.get("status").equals("fail")){
                res.put("status","fail");
                return res;
            }
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            //调用合约函数
            String addr1, addr2, addr3;
            addr1 = selectName(obligor).get("address");
            addr2 = selectName(creditor).get("address");
            addr3 = selectName(certifier).get("address");
            TransactionReceipt receipt = asset.signRecipts(addr1, addr2, amount, addr3).send();
            List<ChainServer_v2.SignEventResponse> response = asset.getSignEvents(receipt);
            //返回结果
            if (!response.isEmpty()) {
                System.out.printf(" sign receipt success => from_asset: %s, to_asset: %s, amount: %s \n",
                        obligor, creditor, amount);
                res.put("status","ok");
                res.put("obligor",obligor);
                res.put("creditor",creditor);
                res.put("amount",amount+"");
                res.put("certifier", certifier);
                res.put("rId", response.get(0).rId + "");
                return res;
            } else {
                System.out.println(" event log not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            logger.error(" signReceipt exception, error message is {}", e.getMessage());
            System.out.printf(" sign receipt success failed, error message is %s\n", e.getMessage());
        }
        res.put("status","fail");
        return res;
    }

    //转让账单
    public Map<String, String> transferPayment(BigInteger rId, String sender, String password, String receiver, BigInteger amount){
        HashMap<String, String> res= new HashMap<>();
        try {
            //确认请求身份
            Map<String, String> check = login(sender, password);
            if (check.get("status").equals("fail")){
                res.put("status","fail");
                return res;
            }
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            //调用合约函数
            String addr1, addr2;
            addr1 = selectName(sender).get("address");
            addr2 = selectName(receiver).get("address");
            TransactionReceipt receipt = asset.transferPayment(rId, addr1, addr2, amount).send();
            List<ChainServer_v2.TransferEventResponse> response = asset.getTransferEvents(receipt);
            //返回结果
            if (!response.isEmpty()) {
                System.out.printf(" transfer success => from_asset: %s, to_asset: %s, amount: %s \n",
                        sender, receiver, amount);
                res.put("from",sender);
                res.put("to",receiver);
                res.put("rId",rId+"");
                res.put("amount",amount+"");
                res.put("status","ok");
                return res;
            } else {
                System.out.println(" event log not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            logger.error(" transfer exception, error message is {}", e.getMessage());
            System.out.printf(" transfer failed, error message is %s\n", e.getMessage());
        }
        res.put("status","fail");
        return res;
    }

    //结算账单
    public Map<String, String> settle(BigInteger rId, String obligor, String password){
        HashMap<String, String> res = new HashMap<>();
        try {
            //确认请求身份
            Map<String, String> check = login(obligor, password);
            if (check.get("status").equals("fail")){
                res.put("status","fail");
                return res;
            }
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            //调用合约函数
            String addr1;
            addr1 = selectName(obligor).get("address");
            TransactionReceipt receipt = asset.settle(rId, addr1).send();
            TransactionDecoder decoder = TransactionDecoderFactory.buildTransactionDecoder(ChainServer_v2.ABI, "");
            InputAndOutputResult outputResult = decoder.decodeOutputReturnObject(receipt.getInput(), receipt.getOutput());
            List<ResultEntity> response = outputResult.getResult();
            System.out.println(response);
            //返回结果
            if (!response.isEmpty()) {
                if (response.get(0).getData().equals(true)) {
                    System.out.printf(" settle success => rId: %s, obligor: %s \n", rId,
                            obligor);
                    res.put("status","ok");
                    res.put("obligor",obligor);
                    res.put("rId",rId+"");
                    return res;
                } else {
                    System.out.print(" settle failed, ret code is false \n");
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            logger.error(" settle exception, error message is {}", e.getMessage());
            System.out.printf(" settle failed, error message is %s\n", e.getMessage());
        }
        res.put("status","fail");
        return res;
    }

    //融资
    public Map<String, String> financing(BigInteger rId, String bank, String creditor, String password, BigInteger amount){
        HashMap<String, String> res = new HashMap<>();
        try {
            //确认请求身份
            Map<String, String> check = login(creditor, password);
            if (check.get("status").equals("fail")){
                res.put("status","fail");
                return res;
            }
            //加载合约
            String contractAddress = loadAssetAddr();
            ChainServer_v2 asset = ChainServer_v2.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
            //调用合约函数
            String addr1, addr2;
            addr1 = selectName(bank).get("address");
            addr2 = selectName(creditor).get("address");
            TransactionReceipt receipt = asset.financing(rId, addr1, addr2, amount).send();
            TransactionDecoder decoder = TransactionDecoderFactory.buildTransactionDecoder(ChainServer_v2.ABI, "");
            InputAndOutputResult outputResult = decoder.decodeOutputReturnObject(receipt.getInput(), receipt.getOutput());
            List<ResultEntity> response = outputResult.getResult();
            System.out.println(response);
            //返回结果
            if (!response.isEmpty()) {
                if (response.get(0).getData().equals(true)) {
                    System.out.printf(" financing success => rId: %s, bank: %s, creditor: %s\n", rId,
                            bank, creditor);
                    res.put("status","ok");
                    res.put("bank",bank);
                    res.put("creditor", creditor);
                    res.put("rId",rId+"");
                    res.put("amount", amount+"");
                    return res;
                } else {
                    System.out.print(" financing failed, ret code is false \n");
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            logger.error(" financing exception, error message is {}", e.getMessage());
            System.out.printf(" financing failed, error message is %s\n", e.getMessage());
        }
        res.put("status","fail");
        return res;
    }

}
