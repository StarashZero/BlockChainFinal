pragma solidity ^0.4.21;

import "./Table.sol";

contract ChainServer_v2 {
    uint tId;
    mapping (address => Company) public companys;
    mapping (uint => Receipt) public receipts;
    
    event Sign(uint rId, address obligor, address creditor, uint amount, address certifier);
    event Transfer(uint rId, address sender, address receiver, uint amount);
    event Finance(uint rId, address creditor, uint amount);
    event Select(uint ret);
    
    struct Company{
        string name;
        string password;
        uint balance;
        bool isBank;
        bool isUsed;
    }
    
    struct Receipt{
        address obligor;
        uint total;
        address certifier;
        address firstBill;
        address lastBill;
        mapping (address => Bill) bills;
        bool isUsed;
    }
    
    struct Bill{
        uint amount;
        uint canUse;
        address previousBill;
        address nextBill;
        bool isUsed;
    }
    
    constructor(){
        tId = 0;
        createCompanysTable();
        createBillsTable();
        createReceiptTable();
    }
    
    function createCompanysTable() private{
        TableFactory tf = TableFactory(0x1001);
        tf.createTable("company", "name", "address");
    }
    
    function createBillsTable() private{
        TableFactory tf = TableFactory(0x1001);
        tf.createTable("bill", "name", "receiptId");
    }
    
    function createReceiptTable() private{
        TableFactory tf = TableFactory(0x1001);
        tf.createTable("receipt", "name", "receiptId");
    }
    
    function openTable(string tablename) private returns(Table){
        TableFactory tf = TableFactory(0x1001);
        return tf.openTable(tablename);
    }
    
    function insertName(string name, address addr) public{
        Table table = openTable("company");
        Entry entry = table.newEntry();
        entry.set("name", name);
        entry.set("address", addr);
        table.insert(name, entry);
    }
    
    function insertBill(string name, uint rId) public{
        Table table = openTable("bill");
        Entry entry = table.newEntry();
        entry.set("name", name);
        entry.set("receiptId", int(rId));
        table.insert(name, entry);
    }
    
    function insertReceipt(string name, uint rId) public{
        Table table = openTable("receipt");
        Entry entry = table.newEntry();
        entry.set("name", name);
        entry.set("receiptId", int(rId));
        table.insert(name, entry);
    }
    
    function selectName(string name) public constant returns(uint, address[]){
        Table table = openTable("company");
        Entries entries = table.select(name, table.newCondition());
        address[] memory address_list = new address[](uint256(entries.size()));
        for(int i=0; i<entries.size(); ++i) {
            Entry entry = entries.get(i);
            
            address_list[uint256(i)] = entry.getAddress("address");
        }
        emit Select(uint256(entries.size()));
        return (uint256(entries.size()), address_list);
    }
    
    function selectBill(string name) public constant returns(uint, uint[]){
        Table table = openTable("bill");
        Entries entries = table.select(name, table.newCondition());
        uint[] memory rId_list = new uint[](uint256(entries.size()));
        for(int i=0; i<entries.size(); ++i) {
            Entry entry = entries.get(i);
            
            rId_list[uint256(i)] = uint256(entry.getInt("receiptId"));
        }
        emit Select(uint256(entries.size()));
        return (uint256(entries.size()), rId_list);
    }
    
    function selectReceipt(string name)public constant returns(uint, uint[]){
        Table table = openTable("receipt");
        Entries entries = table.select(name, table.newCondition());
        uint[] memory rId_list = new uint[](uint256(entries.size()));
        for(int i=0; i<entries.size(); ++i) {
            Entry entry = entries.get(i);
            
            rId_list[uint256(i)] = uint256(entry.getInt("receiptId"));
        }
        emit Select(uint256(entries.size()));
        return (uint256(entries.size()), rId_list);
    }
    
    function getBillAmount(uint rId, address creditor) public returns(uint, uint){
        return (receipts[rId].bills[creditor].amount, receipts[rId].bills[creditor].canUse);
    }
    
    function removeBill(string name, uint rId) public{
        Table table = openTable("bill");
        Condition condition = table.newCondition();
        condition.EQ("name", name);
        condition.EQ("receiptId", int(rId));
        table.remove(name, condition);
    }
    
    function removeReceipt(string name, uint rId) public{
        Table table = openTable("receipt");
        Condition condition = table.newCondition();
        condition.EQ("name", name);
        condition.EQ("receiptId", int(rId));
        table.remove(name, condition);
    }
    
    function login(address company, string password) public returns(bool){
        if (!companys[company].isUsed)
            return false;
        return (keccak256(companys[company].password)==keccak256(password));
    }
    
    function register(address company, string name, string password, uint balance, bool isBank) public returns(bool){
        if (companys[company].isUsed)
            return false;
        uint cnt;
        address[] memory entries;
        (cnt, entries) = selectName(name);
        if (cnt != 0)
            return false;
        insertName(name, company);
        companys[company].name = name;
        companys[company].balance = balance;
        companys[company].isBank = isBank;
        companys[company].password = password;
        companys[company].isUsed = true;
        return true;
    }
    
    function signRecipts(address obligor, address creditor, uint amount, address certifier) public returns(bool, uint){
        if (!companys[obligor].isUsed || !companys[creditor].isUsed)
            return (false, 0);
        insertBill(companys[creditor].name, tId);
        insertReceipt(companys[obligor].name, tId);
        receipts[tId].obligor = obligor;
        receipts[tId].total = amount;
        receipts[tId].certifier = certifier;
        receipts[tId].firstBill = receipts[tId].lastBill = creditor;
        receipts[tId].isUsed = true;
        receipts[tId].bills[creditor].canUse = receipts[tId].bills[creditor].amount = amount;
        receipts[tId].bills[creditor].isUsed = true;
        emit Sign(tId, obligor, creditor, amount, certifier);
        tId++;
        return (true, tId-1);
    }
    
    function transferPayment(uint rId, address sender, address receiver, uint amount) public returns(bool){
        if (!companys[sender].isUsed || !companys[receiver].isUsed)
            return false;
        if (!receipts[rId].bills[sender].isUsed || receipts[rId].bills[sender].canUse < amount)
            return false;
        receipts[rId].bills[sender].amount -= amount;
        receipts[rId].bills[sender].canUse -= amount;
        receipts[rId].bills[receiver].amount += amount;
        receipts[rId].bills[receiver].canUse += amount;
        if (!receipts[rId].bills[receiver].isUsed){
            insertBill(companys[receiver].name, rId);
            
            receipts[rId].bills[receiver].isUsed = true;
            receipts[rId].bills[receiver].previousBill = receipts[rId].lastBill;
            receipts[rId].bills[receipts[rId].lastBill].nextBill = receiver;
            receipts[rId].lastBill = receiver;
        }
        emit Transfer(rId, sender, receiver, amount);
        return true;
    }
    
    function financing(uint rId, address bank, address creditor, uint amount) public returns(bool){
        if ((!receipts[rId].bills[creditor].isUsed || receipts[rId].bills[creditor].canUse < amount)) return false;
        if (!companys[bank].isBank || companys[bank].balance < amount)
            return false;
        companys[bank].balance -= amount;
        companys[creditor].balance += amount;
        receipts[rId].bills[creditor].canUse -= amount;
        signRecipts(creditor, bank, amount, bank);
        emit Finance(rId, creditor, amount);
        return true;
    }
    
    function settle(uint rId, address obligor) public returns(bool){
        if (!receipts[rId].isUsed || (receipts[rId].obligor == obligor) || (receipts[rId].total > companys[obligor].balance))
            return false;
            
        companys[obligor].balance -= receipts[rId].total;
        address creditor = receipts[rId].firstBill;
        
        while(creditor!=receipts[rId].lastBill){
            removeBill(companys[creditor].name, rId);    
            companys[creditor].balance += receipts[rId].bills[creditor].amount;
            receipts[rId].bills[creditor].isUsed = false;
            creditor = receipts[rId].bills[creditor].nextBill;
        }
        removeBill(companys[creditor].name, rId);  
        companys[creditor].balance += receipts[rId].bills[creditor].amount;
        receipts[rId].isUsed = false;
        removeReceipt(companys[obligor].name, rId);
        return true;
    }
    
}
