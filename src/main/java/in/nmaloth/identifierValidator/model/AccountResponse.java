package in.nmaloth.identifierValidator.model;

import java.util.List;

public class AccountResponse {

    private List<String> responseList;
    private Long otb;
    private Long creditLimit;
    private Long balance;
    private String accountType;


    public List<String> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<String> responseList) {
        this.responseList = responseList;
    }

    public Long getOtb() {
        return otb;
    }

    public void setOtb(Long otb) {
        this.otb = otb;
    }

    public Long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Long creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
